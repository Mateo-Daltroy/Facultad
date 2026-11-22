# Plan S1: Bandeja diaria de pedidos (Módulo de Autorización)

> Plan de implementación de la primera slice del "Plan de desarrollo — Módulo de Autorización" (artefacto). Acordado el 19/11/2026.
>
> Referencias: `documentacion-personal/seeds-per-slice.md` (datos de prueba), `documentacion-personal/resumen_portal_y_core.md` (arquitectura del portal y del core) y `Etapa_1y2_Trabajo Integrador_2026_PUBLIC.md` (sección Módulo de Autorización).

## 1. Objetivo

Hoy el rol `alimentacion` entra al portal y ve un placeholder (`AutorizacionPendientesView.vue`: "Módulo en desarrollo").

Al terminar S1, el usuario de Alimentación:

- Entra y ve **los pedidos de hoy**, todos los sectores, con los **pendientes primero y resaltados**.
- Navega al **día anterior y al siguiente**, vuelve a **hoy** o elige una fecha.
- Filtra por **sector, estado y condición** (Regular o Excepción).
- Con un botón **"Revisar pendientes"** aplica el filtro de estado confirmado.
- Ve el **estado de la bandeja en la URL**: un link compartido o un Atrás del navegador reproducen la misma bandeja.

Requisitos del enunciado que cubre: "consultar pedidos mediante vistas diarias con navegación entre períodos" (la parte diaria), "filtrar por sector, condición y estado" (parcial: comida y menú van en S7) e "identificar claramente los pedidos pendientes de autorización".

## 2. Decisiones tomadas

| Tema | Decisión | Motivo |
|---|---|---|
| Filtro por día | El portal manda `fecha_desde = fecha_hasta = D`. **No** se agrega un parámetro `fecha` | `PedidoQuerySchema` y `list_pedidos` ya filtran por superposición de rango. Sin cambios de contrato, y S6 (semana) usa el mismo par |
| `core/reloj.py` | Pasa a **S2** | En S1 ninguna regla de negocio usa "hoy". La fecha inicial de la bandeja sale del navegador: es navegación, no una regla |
| Filtro por condición | Entra en **S1**, en el backend y en la UI | Cada slice es vertical: todo cambio de backend tiene que poder demostrarse desde el portal |
| Orden | Fecha del pedido → estado (**confirmado → autorizado → rechazado**) → id, resuelto en el backend | Una sola lista, sin secciones separadas: los pendientes quedan primero en cada día |
| Atajo a pendientes | Botón **"Revisar pendientes"**, que aplica `estado=confirmado`. Activo, pasa a "Ver todos" | Revisar pendientes es el caso de uso principal de la pantalla |
| Cambios en el core | **Mínimos**: solo dos agregados en `list_pedidos`, sin funciones nuevas | Criterio del grupo: no modificar el core salvo que sea necesario |
| Kit de UI | Componentes propios (`LoadingState`, `ErrorState`, `EmptyState`) y clases de `assets/ui.css`, igual que `reportes`, el otro módulo del mismo rol. De PrimeVue solo el `Paginator`, que es el paginador que ya usa el sistema | Consistencia visual con el resto del portal |
| Tests del portal | No se agrega Vitest en S1 | La lógica pura del portal en S1 es muy chica. Se reconsidera en S4 (funciones de selección) |
| Rama | `feat/autorizacion-s1` sale de `feature/autorizacion-pedidos` (que ya tiene los seeds de demo, commit `8e4bd56`) y vuelve con merge local | `feature/autorizacion-pedidos` es la rama integradora del módulo |

## 3. Fuera de alcance

No hacer en S1 (cada ítem tiene su slice):

- El detalle del pedido, la ruta `/autorizaciones/pedidos/:id` y el link "Ver detalle" desde la tabla (S2).
- `Pedido.modificado_en`, `acciones_permitidas`, `core/reloj.py` (S2).
- Cualquier cambio de esquema o migración.
- La doble llamada a `get_pedido_para_revisar` en el controlador (S3).
- La vista semanal, el calendario y la navegación "solo días con datos" (S6).
- Los filtros de comida y menú en la UI y el panel de totales (S7).
- La exportación CSV (S10). El responsive fino y la accesibilidad del cierre (S11).

## 4. Git

```sh
cd code
git switch feature/autorizacion-pedidos
git switch -c feat/autorizacion-s1
```

- Commits chicos por capa: `feat(api): filtro por condición y orden por estado en /authorizations`, `test: ...`, `feat(portal): bandeja diaria de autorizaciones`.
- Al terminar: `git switch feature/autorizacion-pedidos && git merge --no-ff feat/autorizacion-s1`.
- **No pushear `feat/autorizacion-s1` al origin.** `feature/autorizacion-pedidos` se pushea solo si lo pide el usuario.

## 5. Backend (`code/admin`)

### 5.1 `src/core/pedido.py` → `list_pedidos` (único cambio en el core)

Dos agregados. No se crean funciones nuevas ni cambia el comportamiento existente.

**a) Parámetro `condicion`.** Se agrega `condicion: str | None = None` a la firma, entre `tipo_menu_id` y `estado`. Se valida igual que `estado` y se suma a la lista `condiciones` que ya existe, así queda dentro del mismo `Pedido.solicitudes.any(and_(...))` que comida y menú:

```python
from .solicitud import Solicitud, condiciones_validas
...
    condiciones = []
    if tipo_comida:
        ...
    if tipo_menu_id is not None:
        ...
    if condicion:
        condicion = condicion.strip().lower()
        if condicion not in condiciones_validas:
            raise ValueError("Condicion invalida")
        condiciones.append(Solicitud.condicion == condicion)
    if condiciones:
        query = query.filter(Pedido.solicitudes.any(and_(*condiciones)))
```

Semántica: un pedido aparece si **alguna de sus solicitudes** cumple a la vez todos los filtros de solicitud. Por ejemplo, `condicion=excepcion&tipo_comida=almuerzo` trae los pedidos con al menos un almuerzo de excepción, no los que tienen "algún almuerzo" y "alguna excepción" en solicitudes distintas.

**b) Orden por estado.** Se agrega una clave entre la fecha y el id:

```python
from sqlalchemy import case
...
orden_estado = case({"confirmado": 0, "autorizado": 1, "rechazado": 2}, value=Pedido.estado)
query = query.order_by(desc(Pedido.fecha_desde), orden_estado, desc(Pedido.id))
```

Impacto en `/api/requests` (Jefe de Servicio): ninguno visible. El jefe ve un solo sector, y un sector tiene un solo pedido vigente por fecha (`validate_pedido_duplicado`). La clave nueva solo desempata un pedido rechazado con el que lo reemplazó, y deja primero el vigente, que es lo razonable.

### 5.2 `src/web/schemas/autorizaciones.py`

```python
from core.solicitud import condiciones_validas

class AutorizacionQuerySchema(PedidoQuerySchema):
    sector_id = fields.Integer(validate=validate.Range(min=1))
    condicion = fields.String(validate=validate.OneOf(sorted(condiciones_validas)))
```

- No acepta `"todas"`. Si el parámetro no viene, no se filtra; el portal no lo manda cuando el select está vacío, porque el httpClient descarta los `null` y los `''`.
- `PedidoQuerySchema` no gana `condicion`, así que `/api/requests` sigue respondiendo 400 si se lo mandan (`unknown = RAISE`).

### 5.3 Controlador y servicio

**Sin cambios.** `index()` ya hace `list_pedidos_checked(**filters)`: el `condicion` que carga el schema llega como kwarg, y un `ValueError` se traduce a 400 en `services/pedidos.py::_run`.

### 5.4 `admin/docs/openapi.yaml`

En `GET /authorizations`:

- Agregar el parámetro de query `condicion` (enum `regular`, `excepcion`): "Solo pedidos con alguna solicitud de esa condición. Se combina con `tipo_comida` y `tipo_menu_id` sobre la misma solicitud."
- En la descripción de la respuesta: "Ordenados por `fecha_desde` descendente, después por estado (confirmado, autorizado, rechazado) y por id."

### 5.5 Tests: `tests/unit/test_api_autorizaciones.py`

Se usan los fixtures y helpers existentes: `client`, `data`, `login_as`, `crear` y `set_estado` (importados de `test_api_pedidos`). Los pedidos se crean como jefe con `crear(client, data, fecha_desde=..., detalle=...)` y se consultan como `data.alimentacion`. Para que haya excepciones, alcanza con superar el cupo del sector o usar una comida sin cupo (todas sus solicitudes son de excepción).

| Test | Preparación | Esperado |
|---|---|---|
| `test_index_filtra_un_dia` | Pedidos en D-1, D y D+1 (sectores o fechas distintas) | `?fecha_desde=D&fecha_hasta=D` devuelve solo el de D |
| `test_index_filtra_por_condicion` | Un pedido todo regular y otro con alguna excepción | `condicion=excepcion` → solo el segundo. `condicion=regular` → los dos si ambos tienen regulares |
| `test_index_condicion_y_comida_sobre_la_misma_solicitud` | Pedido con excepción solo en la cena y almuerzo regular | `condicion=excepcion&tipo_comida=almuerzo` → vacío. `...&tipo_comida=cena` → el pedido |
| `test_index_condicion_invalida_400` | — | `condicion=foo` y `condicion=todas` → 400 con `fields.condicion` |
| `test_index_ordena_por_estado_dentro_del_dia` | Tres pedidos de la misma fecha en sectores distintos, con `set_estado` a autorizado, rechazado y confirmado (creados en un orden distinto al esperado) | Los ids salen en el orden confirmado, autorizado, rechazado |

El 401 sin token y el 403 para jefe y comedor ya los cubren `test_without_authentication_returns_401` y `test_only_alimentacion_can_authorize`. **No se duplican.**

## 6. Portal (`code/portal/src/modules/autorizacion/`)

### 6.1 Archivos

```
modules/autorizacion/
├── routes.js                          (modificado)
├── filtros.js                         (nuevo) parseo y serialización de la query, helpers de fecha
├── services/autorizacionService.js    (modificado) listarAutorizaciones, sectores
├── components/PeriodoNav.vue          (nuevo)
├── components/FiltrosAutorizacion.vue (nuevo)
├── components/TablaPedidos.vue        (nuevo)
└── views/BandejaAutorizacionView.vue  (nuevo; se borra AutorizacionPendientesView.vue)
```

No se tocan `router/`, `constants/roles.js`, `api/` ni otros módulos, y el módulo no importa nada de `carga-pedidos` ni de `reportes`.

### 6.2 `filtros.js`

La query de la URL la puede escribir cualquiera, así que se trata como input no confiable: se convierte una sola vez a un objeto de forma conocida y el resto del módulo usa solo ese objeto.

```js
export const ESTADOS = [
  { valor: 'confirmado', etiqueta: 'Pendiente', clase: 'badge--warning' },
  { valor: 'autorizado', etiqueta: 'Autorizado', clase: 'badge--success' },
  { valor: 'rechazado', etiqueta: 'Rechazado', clase: 'badge--danger' },
]
export const CONDICIONES = [
  { valor: 'regular', etiqueta: 'Regular' },
  { valor: 'excepcion', etiqueta: 'Excepción' },
]
export const COMIDAS = [/* desayuno, almuerzo, merienda, cena con su etiqueta */]

export function hoyISO() { /* format(new Date(), 'yyyy-MM-dd') */ }
export function sumarDiasISO(iso, dias) { /* parseISO + addDays + format */ }
export function formatearFechaLarga(iso) { /* "jueves 19/11/2026", locale es */ }

/** route.query -> { fecha, sectorId, estado, condicion, page }. Nunca falla: un valor inválido toma el default. */
export function parseFiltros(query, hoy) { ... }

/** inverso de parseFiltros. Omite null y page = 1 para que la URL quede limpia. */
export function aQuery(filtros) { ... }
```

Reglas de `parseFiltros`:

| Clave de la URL | Campo | Válido | Default |
|---|---|---|---|
| `fecha` | `fecha` | `AAAA-MM-DD` que sea una fecha real (`isValid(parseISO(x))` y que `format(parseISO(x)) === x`, para rechazar `2026-02-30`) | `hoy` |
| `sector_id` | `sectorId` | entero > 0 | `null` |
| `estado` | `estado` | uno de `ESTADOS` | `null` |
| `condicion` | `condicion` | uno de `CONDICIONES` | `null` |
| `page` | `page` | entero ≥ 1 | `1` |

Si llega un array (por ejemplo, `?estado=a&estado=b`), se toma el primer valor. Un valor inválido no muestra error: la bandeja se muestra con el default, y la URL se normaliza en la próxima navegación.

### 6.3 `services/autorizacionService.js`

```js
export async function listarAutorizaciones(filtros) {
  const respuesta = await http.get('/authorizations', {
    params: {
      fecha_desde: filtros.fecha,
      fecha_hasta: filtros.fecha,
      sector_id: filtros.sectorId,
      estado: filtros.estado,
      condicion: filtros.condicion,
      page: filtros.page,
      per_page: POR_PAGINA, // 25, el máximo de la API
    },
  })
  return { items: respuesta.items.map(aPedidoResumen), total: respuesta.total }
}
```

- `aPedidoResumen(json)` → `{ id, sector: json.sector_name, estado: json.estado, pedidas: json.totales, autorizadas: json.autorizados }`. `autorizadas` es `null` en los pendientes. Si falta `id`, `estado` o `totales`, o `estado` no está en `ESTADOS`, tira `Error` con el campo y el valor. No completa con defaults: el error llega a `useAsyncData` y la vista lo muestra con `ErrorState`.
- `sectores()`: `GET /sectors?estado=todos`, recorriendo `next` igual que `reportesService.sectores()`. Devuelve `[{ id, nombre, activo }]`. Se incluyen los inactivos porque pueden tener pedidos históricos.
- Las formas de `totales` y `autorizados` son las de `web/schemas/pedidos.py::totals`: `{ [comida]: { total, regular, excepcion, por_menu } }`, con solo las comidas que tienen solicitudes.

### 6.4 `views/BandejaAutorizacionView.vue`

Estado y carga:

```js
const route = useRoute()
const router = useRouter()
const filtros = computed(() => parseFiltros(route.query, hoyISO()))

function actualizar(cambios) {
  router.push({ query: aQuery({ ...filtros.value, page: 1, ...cambios }) })
}

const { data, loading, error, load } = useAsyncData(() => listarAutorizaciones(filtros.value))
watch(filtros, () => load(), { immediate: true })

const sectores = useAsyncData(() => sectores())   // una vez, en onMounted
```

- Cada cambio usa `router.push`, así cada estado de la bandeja queda en el historial: Atrás deshace el último cambio. Al volver del detalle (S2) la bandeja se reconstruye desde la URL.
- Cambiar cualquier filtro vuelve a la página 1 (`page: 1` va antes de `...cambios`, así el `Paginator` puede pasar su propia página).
- `useAsyncData` ya descarta las respuestas viejas si se navega rápido.

Estructura de la plantilla (`.page` / `.stack`):

1. **Encabezado** (`.page__header`): `h1` "Autorizaciones", subtítulo con `formatearFechaLarga(filtros.fecha)` y el botón de pendientes:
   - Sin filtro de estado confirmado: "Revisar pendientes" → `actualizar({ estado: 'confirmado' })`.
   - Con el filtro activo: "Ver todos" → `actualizar({ estado: null })`.
   - `aria-pressed` refleja si el filtro está activo.
2. **`PeriodoNav`**: `:fecha="filtros.fecha"`, `@cambiar="fecha => actualizar({ fecha })"`.
3. **`FiltrosAutorizacion`**: recibe los filtros y los sectores, y emite `cambiar` y `limpiar`. "Limpiar" hace `actualizar({ sectorId: null, estado: null, condicion: null })` y conserva la fecha.
4. **Resultado**:
   - `loading` → `<LoadingState text="Cargando pedidos…" />`.
   - `error` → `<ErrorState :message="error.message" :retryable="error.retryable" @retry="load" />`.
   - Sin ítems → `<EmptyState title="No hay pedidos para esta fecha y filtros">`, con el botón "Limpiar filtros" en el slot `action` si hay filtros activos.
   - Con ítems → `<TablaPedidos :pedidos="data.items" />` y, si `data.total > 25`, el `Paginator` de PrimeVue (`:first="(filtros.page - 1) * 25"`, `:rows="25"`, `:totalRecords="data.total"`, `@page="e => actualizar({ page: e.page + 1 })"`), con el mismo template que `carga-pedidos/views/PedidosView.vue`.

### 6.5 `components/PeriodoNav.vue`

- Props: `fecha` (ISO). Emite `cambiar(iso)`.
- Toolbar (`.toolbar`) con "← Día anterior" (`sumarDiasISO(fecha, -1)`), un `input type="date"` con `label` visible o `visually-hidden` (emite al cambiar si el valor no está vacío), "Hoy" (deshabilitado si `fecha === hoyISO()`) y "Día siguiente →".
- Los botones usan `.btn .btn--secondary` y el input `.field__input`.

### 6.6 `components/FiltrosAutorizacion.vue`

- Props: `filtros`, `sectores` (array), `sectoresError` (bool). Emite `cambiar(cambios)` y `limpiar`.
- `form.card` con grid igual al de `reportes/components/FiltrosReporte.vue`. Tiene tres `select` con `label` (`.field`, `.field__label`, `.field__input`) que emiten **al cambiar** (sin botón "Filtrar"):
  - Sector: "Todos" + sectores; los inactivos van con el sufijo " (inactivo)". Si fallaron los sectores, queda deshabilitado con el texto "No se pudieron cargar los sectores".
  - Estado: "Todos" + `ESTADOS` (con la etiqueta "Pendiente" para confirmado).
  - Condición: "Todas" + `CONDICIONES`.
- Botón "Limpiar filtros" (`.btn--secondary`).
- Los `value` de los select se convierten al tipo del filtro antes de emitir (`sectorId` a número, `''` a `null`).

### 6.7 `components/TablaPedidos.vue`

- Props: `pedidos` (`PedidoResumen[]`).
- `.table-wrap` > `table.table` con un `caption` (`visually-hidden`) "Pedidos del día".
- Columnas:

| Columna | Contenido |
|---|---|
| Sector | `th scope="row"` |
| Estado | `<span class="badge" :class="clase">etiqueta</span>`, con "Pendiente", "Autorizado" o "Rechazado" |
| Desayuno, Almuerzo, Merienda, Cena (`.center`) | `pedidas / autorizadas` (ej. `14 / 12`). Solo `pedidas` si es pendiente. `—` si la comida no tiene solicitudes. Si tiene excepciones, se agrega debajo, en texto chico, `(3 exc.)` |
| Total (`.num`) | Suma de `total` de todas las comidas, con el mismo formato `pedidas / autorizadas` |

- En el encabezado, una nota corta explica el formato: "Pedidas / autorizadas".
- La fila de un pendiente lleva la clase `fila--pendiente` (estilo scoped: `box-shadow: inset 4px 0 0 var(--color-warning)` y un fondo suave). No depende solo del color, porque el badge dice "Pendiente".
- **Sin link al detalle** (S2).

### 6.8 `routes.js`

```js
{
  path: '/autorizaciones',
  name: 'autorizacion-pendientes',            // se mantiene: lo usa ROLE_HOME_ROUTE
  component: () => import('./views/BandejaAutorizacionView.vue'),
  meta: {
    roles: [ROLES.ALIMENTACION],
    title: 'Bandeja de autorizaciones',
    menu: { label: 'Autorizaciones', order: 20 },
  },
}
```

## 7. Verificación

### 7.1 Automática

```sh
cd code/admin && poetry run pytest          # todo en verde, incluidos los 5 tests nuevos
cd code/portal && npm run lint && npm run build
```

### 7.2 Manual desde el portal

Preparación (en `code/admin`). No hay cambios de esquema; el reset solo sirve para que D sea hoy:

```sh
poetry run flask --app web reset-db
USER_NAME=admin@test.com USER_PASS=admin1234 poetry run flask --app web seeds
poetry run flask --app web run --debug
# en otra terminal: cd code/portal && npm run dev
```

Usuario: `alimentacion@test.com` / `alim1234`. Datos: sección S1 de `seeds-per-slice.md`.

| # | Acción | Esperado |
|---|---|---|
| 1 | Login | Lleva a `/autorizaciones` con la fecha de hoy. Muestra 5 pedidos: 4 y 5 arriba como "Pendiente" (resaltados), después 6 y 7 (Autorizado) y al final 8 (Rechazado) |
| 2 | Mirar las columnas del pedido 6 | El desayuno muestra pedidas y autorizadas distintas (8 en total contra 7 autorizadas) |
| 3 | "Revisar pendientes" | URL `?estado=confirmado`. Quedan 4 y 5. El botón dice "Ver todos" |
| 4 | "Ver todos", después "← Día anterior" | D-1 muestra solo el pedido 3 (Pendiente) |
| 5 | "Hoy", después "Día siguiente →" | D+1 muestra 9 y 10 |
| 6 | "Día siguiente →" otra vez | D+2 muestra el estado vacío |
| 7 | En D, sector Médicos Internos | Solo el 4. En el select, Rayos aparece como "(inactivo)" |
| 8 | En D, sin sector, condición Excepción | 4, 5, 6 y 8 (sin el 7) |
| 9 | Copiar la URL con filtros y abrirla en otra pestaña | Se ve la misma bandeja |
| 10 | Atrás del navegador varias veces | Deshace los cambios de a uno |
| 11 | Abrir `/autorizaciones?fecha=2026-02-30&estado=foo` | Muestra la bandeja de hoy sin filtro de estado, sin error |
| 12 | Login con `jefe.quirofano@test.com` y abrir `/autorizaciones` | "Acceso denegado". `GET /api/authorizations` con su token responde 403 |

### 7.3 Criterio de terminado

- Los pasos 7.1 y 7.2 pasan completos.
- La rama está mergeada en local a `feature/autorizacion-pedidos` y **no** está pusheada.
- Está escrito el reporte de la slice en `documentacion-personal/reporte-s1-bandeja-diaria.md` con la plantilla del artefacto:
  - Encabezado: fecha, rama y commits.
  - Alcance: lo que cubre y lo que queda afuera.
  - Cambios: base de datos ("sin cambios"), backend, frontend y tests (con el resultado de pytest).
  - Prueba manual: la tabla de 7.2 con el resultado de cada paso.
  - Pendientes y decisiones: lo decidido durante la sesión y lo que pasa a otra slice.
