# Resumen del proyecto: Portal (Vue) y Core (Flask)

> Referencia personal, escrita el 19/11/2026 sobre el código en `code/`
> (último commit leído: `5ece7bb` — Modo mantenimiento).

---

## 1. Visión general

```
code/
├── admin/        Flask: app privada (Jinja, admin) + API REST /api (JWT)  ← Etapa 1
│   └── src/
│       ├── core/   modelos SQLAlchemy + lógica de negocio (sin Flask)
│       └── web/    controladores, schemas, templates, auth, errores
├── portal/       Vue 3 + Vite + Pinia + vue-router: app pública       ← Etapa 2
└── calculator/   resto de un ejercicio, no se usa
```

Hay **un solo backend** con dos "caras":

| | App privada | API `/api` |
|---|---|---|
| Quién la usa | Administrador | Portal Vue (jefe_servicio, alimentacion, comedor) |
| Autenticación | Sesión Flask en cookie (`flask_session/`) | JWT `Authorization: Bearer` |
| Permisos | `web/handlers/permissions.py` | `web/api_auth.py` |
| Respuesta | HTML con Jinja | JSON `{error: {code, message, fields}}` |

El portal **solo** habla con `/api`. Nunca toca la base ni usa la sesión de Flask.

---

## 2. Levantar el entorno

**Backend** (en `admin/`):

```sh
poetry install
poetry run flask --app web run --debug         # http://localhost:5000
USER_NAME=admin@x.com USER_PASS=xxx poetry run flask --app web seeds
poetry run flask --app web reset-db
poetry run pytest
```

- En desarrollo, `admin/.env` apunta a SQLite (`DEV_DATABASE_URL=sqlite:///dev.db`) y los tests
  usan SQLite en memoria. En producción se usa Postgres (`DATABASE_URL`).
- Las tablas se crean solas al arrancar (`create_app` → `database.create_tables()`).
- CORS: `CORS_ORIGINS` vale `*` por defecto, así que el portal en `:5173` puede llamar a `:5000`
  sin configurar nada.

**Portal** (en `portal/`):

```sh
cp .env.example .env      # VITE_API_URL=http://localhost:5000/api ya viene bien
npm install
npm run dev               # http://localhost:5173
npm run lint
```

Las variables `VITE_*` se escriben en el bundle **al compilar**: si cambiás `.env`, reiniciá
`npm run dev`.

**Usuarios de los seeds** (`core/seeds.py`):

| Email | Rol | Contraseña |
|---|---|---|
| `jefe.quirofano@test.com` | jefe_servicio (con sector) | jefe1234 |
| `jefe.libre@test.com` | jefe_servicio (sin sector) | jefe1234 |
| `js@js.com` | comedor | 1234 |
| `bloqueado@test.com` | comedor bloqueado | bloqueado1234 |

⚠️ **No hay ningún usuario `alimentacion` en los seeds.** Para probar Autorización o Reportes
hay que crear uno desde el panel de admin.

---

## 3. El Portal en profundidad

### 3.1 Arranque (`src/main.js`)

1. Crea la app y Pinia.
2. **Conecta el cliente HTTP con la sesión** mediante `configureHttpClient({...})`:
   - `getToken` toma el token del store de sesión.
   - `onUnauthorized` corre ante un 401 de un request que llevaba token: hace
     `session.expireSession()` y redirige al login con `?redirect=<ruta actual>`.
   - `onMaintenance` corre ante cualquier 503: llama a `configuration.enterMaintenance(msg)`.
3. Cuando la pestaña vuelve a estar visible, revisa de nuevo el modo mantenimiento.
4. Registra el router y monta la app.

Esta inyección existe para que `api/` no importe stores y así no haya dependencias circulares.

### 3.2 Capa de API (`src/api/`)

**Regla:** las vistas nunca llaman a `fetch`. La cadena es
`vista → modules/<x>/services/xxxService.js → http (de '@/api')`.
(De dónde sale esta regla: ver la sección 6.)

`httpClient.js` → `request(method, path, options)` se encarga de:

- Armar la URL con `env.apiUrl + path`, descartando los params vacíos o null y repitiendo la
  clave si el valor es un array.
- Agregar `Authorization: Bearer` salvo que se pase `auth: false` (lo usan login y configuración).
- Un timeout propio de 15 s (`VITE_API_TIMEOUT_MS`) y aceptar un `signal` para cancelar desde el
  componente.
- Recibir `responseType: 'blob'` cuando hay que descargar CSV.
- Si la respuesta es ok, devolver el body ya parseado. Si no, lanzar un **`ApiError`**.

`errors.js` → **`ApiError`** tiene estos campos:

| Campo | Contenido |
|---|---|
| `status` | Código HTTP (0 si fue un error de red) |
| `type` | `validation` (400 y otros 4xx), `unauthorized` (401), `forbidden` (403), `not_found` (404), `conflict` (409), `maintenance` (503), `server` (5xx), `network` |
| `code` | El `code` del backend, por ejemplo `invalid_credentials` o `business_rule` |
| `message` | El mensaje del backend, salvo en los 5xx, donde siempre se muestra uno genérico |
| `fields` | Los errores por campo (`{campo: [msgs]}`), que vienen de marshmallow o de `DomainError.field` |
| `retryable` | `true` solo para `server` y `network` |

Atajos disponibles: `http.get(path, {params})`, `http.post(path, body)`, `http.put`,
`http.delete`.

### 3.3 Router y control de acceso (`src/router/`)

- **`index.js`** junta las rutas: `/` redirige a `/login`, después vienen las rutas de cada
  módulo (`...cargaPedidosRoutes`, etc.), luego `/acceso-denegado` y por último el catch-all 404.
- **`access.js`**:
  - `canAccess(route, role)` devuelve true si la ruta tiene `meta.public` o si `meta.roles`
    incluye el rol.
  - En modo dev, `assertRoutesDeclareAccess` **rompe la app** si una ruta con componente no
    declara `meta.roles` ni `meta.public`.
- **Orden de los guards** (`beforeEach`):
  1. `maintenance.js`: en la primera navegación siempre hace `GET /configuration`; después,
     solo si pasaron más de 30 s.
  2. `guards.js`:
     - Si la ruta es `guestOnly` (el login) y ya hay sesión, te manda al home de tu rol.
     - Si es pública, deja pasar.
     - Si no hay sesión, va a `/login?redirect=...`.
     - Si el rol no tiene acceso, va a `/acceso-denegado`.
- `destinationAfterLogin` valida `?redirect` (tiene que ser una ruta interna, existir y ser
  accesible para el rol). Si no pasa, usa `ROLE_HOME_ROUTE`.
- `afterEach` pone el `document.title` a partir de `meta.title`.

**Metadatos de ruta que existen:**

| `meta` | Para qué |
|---|---|
| `roles: [ROLES.X]` / `public: true` | Acceso (obligatorio) |
| `title` | Título de la pestaña |
| `menu: {label, order}` | Aparece en el menú lateral (DefaultLayout) |
| `kioskMenu: {label, order}` | Aparece en el panel del modo kiosco |
| `section: 'nombre-ruta'` | Pantalla interna que marca como activa una opción del menú (ej. el detalle de un pedido) |
| `layout: 'kiosco' \| 'auth'` | Elige el layout |
| `guestOnly` | Solo se ve sin sesión (login) |

### 3.4 Stores (Pinia, estilo setup)

- **`session.js`**:
  - Estado: `token`, `expiresAt`, `user` (lo que devuelve `/auth/me`: nombre, rol, `sector_id`,
    `sector_name`). Se persiste en **`sessionStorage`** (`portal.session`); el README del portal
    explica por qué se eligió.
  - `login` y `loginWithGoogle` usan `startSession`: piden el token, después `/auth/me`, y solo
    entonces lo persisten. Si falla `/me`, no queda ninguna sesión a medias.
  - `hasValidSession()` también chequea el vencimiento del lado del cliente.
  - `logout()` revoca el token en la API y limpia todo aunque la API falle.
  - `notice` / `consumeNotice` guardan un mensaje de un solo uso para el login (ej. "tu sesión
    expiró").
- **`configuration.js`**: guarda `settings` (respuesta de `/configuration`), `maintenance`,
  `maintenanceMessage` y `maxPerPage` (25). Además deduplica requests concurrentes con `pending`.
- **`notifications.js`**: los toasts. Ver la sección 3.6.

### 3.5 Layouts (`App.vue`)

`App.vue` decide qué mostrar:

- Si `configuration.maintenance` es true, muestra **solo** `MaintenanceView`. No cambia la URL ni
  borra la sesión, y vuelve a consultar cada 30 s.
- Si `meta.layout === 'auth'`, renderiza la vista sola (el login).
- Si no hay sesión, usa `BlankLayout`.
- Si `meta.layout === 'kiosco'`, usa `KioskLayout`: pantalla completa y un botón redondo en la
  esquina con `UserSummary`, los ítems de `kioskMenu` y logout.
- En cualquier otro caso usa `DefaultLayout`: topbar con marca, usuario/rol/sector y logout, más
  el menú. El menú es lateral desde 1025 px y desplegable por debajo de ese ancho.

Además, `ToastRegion` y `ConfirmDialog` se montan **una sola vez**, acá.

`useMenu(key)` arma el menú **a partir de las rutas** (`router.getRoutes()` filtradas por
`meta[key]` y `canAccess`). Agregar una opción al menú es agregar `meta.menu` en el `routes.js`
del módulo, nada más.

`useFocusOnNavigation` mueve el foco al `<main>` en cada navegación (accesibilidad).

### 3.6 Kit de UI y composables para los módulos

| Necesidad | Herramienta |
|---|---|
| Cargar datos con los tres estados (cargando, error, datos) | `const { data, loading, error, load } = useAsyncData(() => servicio(...))`. Si se llama dos veces seguidas, solo cuenta la última respuesta, así que sirve para filtros. |
| Mostrar esos estados | `<LoadingState>`, `<ErrorState :message @retry :retryable>`, `<EmptyState title>` (con slot `action`) |
| Toast | `useNotificationsStore().success/info/warning/error(msg)`. Los de info y success se cierran solos; warning y error quedan hasta que el usuario los cierra. |
| Mensaje fijo en la pantalla | `<AppAlert type="warning" title>` |
| Confirmación | `if (!(await useConfirm().confirm({ title, message, confirmLabel, danger }))) return` (usa `<dialog>` nativo) |
| Validar en el cliente | `utils/validators.js` (`validateRequired`, `validateEmail`). Devuelven `''` o el mensaje de error. |
| Estilos | `assets/main.css`: CSS propio, mobile first, con variables `--color-*` y `--touch-target` y clases `.btn`, `.btn--primary/secondary/danger`, `.field`, `.field__input`, `.field__error`, `.alert--*`, `.state`, `.visually-hidden` |

Patrón típico:

```vue
<script setup>
const { data: pedidos, loading, error, load } = useAsyncData(() => listarPedidos())
onMounted(load)
</script>
<template>
  <LoadingState v-if="loading" text="Cargando pedidos…" />
  <ErrorState v-else-if="error" :message="error.message" @retry="load" />
  <EmptyState v-else-if="!pedidos?.items?.length" title="No hay pedidos" />
  <TablaPedidos v-else :pedidos="pedidos.items" />
</template>
```

`views/LoginView.vue` es la **mejor referencia** de cómo armar un formulario en este proyecto:
validación en blur e input, `aria-invalid` y `aria-describedby`, mapeo de `error.fields` a cada
campo, foco en el primer error y botón deshabilitado mientras se envía.

### 3.7 Módulos (`src/modules/<modulo>/`)

Cada módulo tiene `routes.js`, `views/`, `components/` y `services/`. Las vistas se cargan en
forma lazy.

| Módulo | Rol | Ruta inicial | Estado (al 19/11) |
|---|---|---|---|
| `carga-pedidos` | jefe_servicio | `/pedidos` | Vista placeholder; el service tiene `listarPedidos` |
| `autorizacion` | alimentacion | `/autorizaciones` | Placeholder |
| `entrega` | comedor | `/entrega` (kiosco) | Placeholder |
| `reportes` | alimentacion | `/reportes` | Placeholder |

Los comentarios de cada `*Service.js` listan los endpoints que sugiere el enunciado. Toda la
infraestructura (auth, layouts, mantenimiento, errores) ya está hecha; lo que falta es el
contenido de los módulos.

Para sumar una pantalla:

1. Agregá una ruta en `modules/x/routes.js` con `meta.roles`.
2. Creá la vista en `views/`.
3. Agregá las funciones de API en `services/`.
4. Si el módulo es nuevo, importá su `routes.js` en `router/index.js`.

### 3.8 Flujo completo de un request

```
Vista → useAsyncData(load) → pedidosService.listarPedidos(params)
  → http.get('/requests', {params}) → fetch con Bearer
     ├ 200 → data
     ├ 401 → session.expireSession + router a /login?redirect   (y lanza ApiError)
     ├ 503 → configuration.enterMaintenance → App.vue muestra MaintenanceView
     └ resto → ApiError → error.value → <ErrorState> o error por campo en el form
```

---

## 4. Repaso del core (backend)

### 4.1 Capas

```
web/controlers/api_*.py   Blueprint por recurso: decorador de permiso → schema.load → core → schema.dump
web/schemas/*.py          marshmallow: Input (unknown=RAISE), Query (filtros + paginación), Output/Detail
web/services/*.py         traducen excepciones del core a DomainError (+ rollback)
core/*.py                 modelo SQLAlchemy + funciones validate_* / create_* / list_* / update_*
core/errors.py            DomainError(message, field) → NotFound / Conflict / BusinessRule / Forbidden
```

Todos los blueprints `api_*` se registran bajo `/api` con `create_api_blueprint(...)` en
`web/__init__.py`.

**Cómo se traducen los errores** (`web/api_errors.py`):

| Excepción | Respuesta |
|---|---|
| marshmallow `ValidationError` | 400 `validation_error` con `fields` |
| `BusinessRuleError` | 400 `business_rule` |
| `NotFoundError` / werkzeug `NotFound` | 404 |
| `ConflictError` | 409 |
| `ForbiddenError` | 403 |
| `HTTPException` dentro de `/api` | JSON `http_<code>` |

**Auth de la API** (`web/api_auth.py` y `web/tokens.py`):

- El token es un JWT HS256 con `sub`, `jti`, `iat` y `exp`, y dura 8 h.
- `authenticate()` verifica la firma y el vencimiento, que el `jti` no esté en `revoked_tokens`
  (el logout lo agrega) y que el usuario siga activo y con un rol de la API. Esto se chequea
  **en cada request**.
- Los decoradores son `@token_required` y `@permission_required("x")`. Dejan disponibles
  `g.current_user` y `g.token`.
- `sector_scope(user, sector_id)`: si el usuario es jefe_servicio, se fuerza su propio sector
  (y da 403 si pide otro). Para los demás roles, el valor pasa sin cambios. **El sector del jefe
  sale siempre del token, nunca del cliente.**

**Permisos por rol** (`core/seeds.py`: `ROLES[...]`):

| Rol | Permisos |
|---|---|
| jefe_servicio | `pedido_*`, `personal_*`, `cupo_index/show`, `resumen_pedidos` |
| alimentacion | `autorizacion_*`, `cupo_*`, `sector_index/show`, `personal_*`, `comida_index`, `entrega_index/show/summary`, `resumen_*` |
| comedor | `entrega_*`, `comida_index`, `cupo_index/show`, `sector_*`, `personal_index/show` |

Si agregás un endpoint con un permiso nuevo, también hay que agregarlo en los seeds.

**Modo mantenimiento:**

- `before_request` → `check_maintenance`: con el mantenimiento activo, todo `/api` responde 503
  salvo `GET /api/configuration`.
- Los OPTIONS de preflight pasan igual, para que el navegador no corte el request por CORS.

### 4.2 Modelo de dominio

```
User ─(role)─ Role ─< Permission         User.sector_id (jefe_servicio)
Sector ─< Personal (tipo_personal, tipo_menu, activo, deleted)
Sector ─< Cupo (tipo_comida, cantidad)    uno por (sector, comida)
Pedido (sector, periodicidad, fecha_desde/hasta, estado: confirmado|autorizado|rechazado)
  └─< Solicitud (personal, fecha, tipo_comida, tipo_menu_id copiado, condicion regular|excepcion,
                 autorizada None|True|False, autorizado_por/en)
Entrega (fecha, tipo_comida, usuario, responsable) ─< EntregaItem (solicitud, personal, tipo_menu)
Setting (key/value: maintenance_mode, maintenance_message)
TipoMenu / TipoPersonal (catálogos)
```

Las reglas importantes:

- **`create_pedido`** (`core/pedido.py`):
  - Valida periodicidad, fechas, detalle, que no haya otro pedido del sector que se superponga,
    que las personas sean del sector y que no haya solicitudes duplicadas.
  - Después genera **una Solicitud por persona × día × comida**.
  - **Condición**: en cada fecha y comida, las primeras personas hasta llenar el cupo del sector
    quedan `regular` y el resto `excepcion`. Por eso el orden del array en `detalle` importa.
  - El menú se copia de la ficha de la persona al momento de crear el pedido.
- **Autorización** (`core/autorizacion.py`):
  - Solo se puede revisar un pedido en estado `confirmado`; si no, da 409.
  - `autorizar_pedido` recibe `{solicitud_id: bool}`. Las solicitudes que no vienen en el body
    quedan como no autorizadas.
  - `rechazar_pedido` marca todas las solicitudes en false y guarda el motivo (máx. 255
    caracteres).
- **Entregas** (`core/entrega.py`):
  - Se arman a partir de solicitudes autorizadas.
  - `ids_entregados` sirve para marcar `entregada` en `/authorized-meals`.
- **Listados**:
  - Formato paginado: `{items, page, per_page, total, next}`.
  - `per_page` máximo 25, que es el `max_per_page` que expone `/configuration`.

### 4.3 Endpoints existentes (`admin/docs/openapi.yaml`)

| Grupo | Endpoints |
|---|---|
| Auth | `POST /auth/login`, `/auth/google`, `/auth/logout`, `GET /auth/me` |
| Catálogos | `GET /menu-types`, `/personnel-types`, `/configuration` (este último es público) |
| Sectores y personal | `/sectors`, `/sectors/{id}`, `/sectors/{id}/personnel`, `/personnel` (CRUD) |
| Cupos | `/quotas` (GET/POST/PUT) |
| Pedidos | `/requests` (CRUD), `/authorizations` (GET, PUT `{id}`, POST `{id}/reject`) |
| Comidas y entregas | `/authorized-meals`, `/authorized-meals/{person_id}`, `/deliveries` (GET/POST), `/deliveries/{id}`, `/deliveries/summary` |
| Resúmenes | `/summaries/requests`, `/summaries/authorizations`, `/summaries/deliveries` |

Las "Ampliaciones de la API" de Etapa 2 (sección del enunciado a partir de la línea ~1306)
todavía no están implementadas, salvo `/auth/google` y `/configuration`. Por ejemplo, falta la
exportación a CSV y la corrección de autorizaciones. Si una feature depende de alguna, el
endpoint se suma siguiendo el patrón de la sección 4.1 (controller, schema, core y permiso en
los seeds).

---

## 5. Detalles que te pueden morder

- La entrega de la Etapa 2 es el **20/11/2026**, según el enunciado.
- No hay usuario `alimentacion` en los seeds.
- `components/SessionBar.vue` y `views/HomeView.vue` son restos que no se usan.
- En `MaintenanceView.vue`, el `<template v-if="stillActive">` está vacío, así que el aviso
  "sigue en mantenimiento" nunca muestra texto.
- `.env.production` del portal está commiteado a propósito (no tiene secretos). El `.env` del
  admin no está commiteado.

---

## 6. ¿De dónde sale la regla "las vistas no usan fetch, usan services"?

**Del enunciado, no de las clases teóricas.** En
`Etapa_1y2_Trabajo Integrador_2026_PUBLIC.md`, sección **Etapa 2 → Arquitectura de la
aplicación frontend → Requerimientos funcionales** (≈ línea 947):

> * Centralizar las llamadas a la API en **un único cliente HTTP** que:
>   * Agregue el token en el header Authorization (Bearer) de cada solicitud.
>   * Tome la URL base de la API desde una variable de entorno, sin valores fijos en el código.
>   * Interprete los códigos de error de la API de forma uniforme, según lo definido en Manejo
>     de errores y validaciones.
> * Organizar el código por módulo (carga de pedidos, autorización, entrega y reportes),
>   **separando vistas, componentes reutilizables y servicios de acceso a la API**.

Eso pide dos cosas: un único cliente HTTP (`src/api/httpClient.js`) y una capa de servicios por
módulo separada de las vistas (`modules/<x>/services/`). La frase "las vistas no usan `fetch`
directamente" es cómo lo escribió el grupo en `portal/README.md` (sección "Reglas") para cumplir
ese requisito. No es una cita textual de la cátedra.

**Las clases teóricas no lo exigen.** De hecho, los ejemplos hacen lo contrario:

- `clase17_vue_directivas.pdf` (diapos 11–13): `fetch` y `axios` directo en el hook `created()`
  del componente.
- `clase18_vue_componentes.pdf` (diapo ~30, ejemplo `api_client`): un componente `ApiClient.vue`
  que hace el request él mismo.
- `clase19_vue_jwt_flask.pdf` (diapos 6–7): `fetch` con `Authorization: Bearer
  ${localStorage.getItem('jwt')}` armado a mano en cada función.
- `clase16_vue_frameworksjs_intro.pdf` solo habla de "centralizar la comunicación" a nivel
  arquitectura (API Gateway entre microservicios), que es otro tema.

Son ejemplos didácticos mínimos. El requisito del proyecto es el del enunciado.
