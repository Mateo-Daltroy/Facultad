# Plan S8: Gestión de cupos (Módulo de Autorización)

> **BORRADOR v1 (20/11/2026).** Decisiones cerradas. Los estilos de la matriz y del popover se ajustan visualmente durante la implementación.
>
> Plan de implementación de la octava slice del "Plan de desarrollo — Módulo de Autorización" (artefacto). **En el backend el alcance es menor que en el artefacto** (ver "Por qué el backend de S8 es chico").
>
> Referencias:
> - Slice anterior: `documentacion-personal/plan-s7-filtros-y-totales.md` y `reporte-s7-filtros-y-totales.md`.
> - Datos de demo: `documentacion-personal/seeds-per-slice.md` (tabla "Sectores y cupos", tabla de pedidos y sección S8).
> - Enunciado, `Etapa_1y2_Trabajo Integrador_2026_PUBLIC.md`:
>   - Sección **7. Cupos**: CRUD; campos sector, comida, cantidad, fecha de creación y de última modificación; no se crea un cupo para un sector inactivo; no coexisten dos cupos para el mismo sector y comida; búsqueda por sector y comida; orden por sector; validar en cliente y servidor que la cantidad sea mayor que cero; "la modificación de un cupo no deberá alterar retroactivamente pedidos que ya se encuentren confirmados, autorizados o entregados".
>   - Módulo de Autorización: "Gestionar los cupos de comidas por sector: listar, crear y modificar el cupo de cada sector y tipo de comida". Endpoints: `GET, POST y PUT /api/quotas`, `GET /api/sectors`.
>   - Ampliaciones de la API, `PUT /api/quotas/{quota_id}`: "se elimina el error 400 cuando la nueva cantidad es menor a lo ya entregado […]. Errores: 400 si la cantidad no es mayor que cero o el sector está inactivo; 404 si el cupo no existe; 409 si ya existe otro cupo para el mismo sector y tipo de comida".
> - Artefacto, S8: "Backend: ajustar `PUT /api/quotas/{id}` a la regla de Etapa 2 […]. Frontend: `CuposView` con tabla sector × comida y `CupoForm` […]; entrada 'Cupos' en el menú. Tests: las tres reglas del `PUT`. **Listo cuando:** un cupo modificado no cambia la condición de pedidos ya cargados."
>
> **Precondición:** S7 está terminada y mergeada en `feature/autorizacion-pedidos` (merge `e8c51c3`). Este plan usa estos nombres del código actual:
>
> - Backend (`code/admin/src`):
>   - `core/cupo.py`: `Cupo` (`id`, `sector_id`, `tipo_comida`, `cantidad`, `inserted_at`, `updated_at`, relación `sector`), `create_cupo`, `update_cupo`, `list_cupos` (paginado, máx. 25, orden por nombre de sector), `validate_sector_cupo` (400 si el sector no existe o está inactivo), `validate_cupo_duplicado` (409) y `validate_cantidad`.
>   - `web/controlers/api_cupos.py`: `GET /api/quotas`, `GET /api/quotas/<id>`, `POST /api/quotas` y `PUT /api/quotas/<id>` (permisos `cupo_index`, `cupo_show`, `cupo_new` y `cupo_update`).
>   - `web/schemas/cupos.py`: `CupoInputSchema` (los tres campos obligatorios, `strict`, `cantidad >= 1`, `unknown = RAISE`), `CupoOutputSchema` (`id`, `sector_id`, `sector_name`, `tipo_comida`, `cantidad`, `inserted_at` y `updated_at`) y `MEAL_TYPES`.
>   - `core/pedido.py`: `build_solicitudes` calcula Regular/Excepción con `_cupos_del_sector` **al crear** el pedido; `update_pedido` (Carga, solo pedidos `confirmado`) recalcula con el cupo actual.
>   - `core/entrega.py`: `create_entrega(usuario_id, tipo_comida, personal_ids, responsable_id, fecha)`.
>   - `core/seeds.py`: `CUPOS` y `ROLES["alimentacion"]` (ya tiene `cupo_index`, `cupo_show`, `cupo_new` y `cupo_update`).
>   - Tests: `tests/unit/test_api_cupos.py` (fixture `data` con `quirofano`, `pediatria`, `alimentacion`, `jefe` y `comedor`; `login_as`; `count_cupos`) y `tests/unit/test_api_autorizaciones.py` (fixtures y helpers de pedidos: `pedido`, `detalle`, `put_todas`, `fijar_hoy`, `DIA`).
>   - `admin/docs/openapi.yaml`: rutas `/quotas` y `/quotas/{quota_id}`.
> - Portal (`code/portal/src`):
>   - `modules/autorizacion/routes.js` (ruta `autorizacion-pendientes` con `menu: { label: 'Autorizaciones', order: 20 }`); Reportes usa `order: 30`.
>   - `modules/autorizacion/filtros.js`: `COMIDAS`, `POR_PAGINA`, `unoDe`, `enteroDesde` y `primero`.
>   - `modules/autorizacion/services/autorizacionService.js`: `sectores()` (`GET /api/sectors?estado=todos`, todas las páginas → `[{ id, nombre, activo }]`) y los helpers internos `respuestaInvalida`, `esEntero`, `esObjeto` y `esTexto` (no exportados).
>   - `modules/autorizacion/components/PeriodoNav.vue`: patrón de popover propio (`useId`, `pointerdown` afuera, devolver el foco).
>   - `composables/useAsyncData.js`, `composables/useConfirm.js`, `stores/notifications.js` (toasts), `components/LoadingState.vue`, `ErrorState.vue` y `EmptyState.vue`, `api/errors.js` (`ApiError` con `type`, `status` y `fields`; `ERROR_TYPES`).
>   - `composables/useMenu.js`: arma la sidebar con `meta.menu` de las rutas.
>   - Vitest (`npm test`, `vitest run`, toma `**/*.test.js`).
>
> Si algo de esto no coincide con el código, **manda el código**: se adapta el plan sin renombrar lo existente.

## 0. Por qué el backend de S8 es chico

| Parte del artefacto | Estado | Qué hace S8 |
|---|---|---|
| Quitar el 400 por "cantidad menor a lo entregado" en `PUT /api/quotas/{id}` | **Ya está así.** `update_cupo` nunca tuvo esa validación | Un test que lo documenta |
| 409 por duplicado y 400 por sector inactivo o cantidad ≤ 0 en el `PUT` | **Ya está hecho** (`validate_cupo_duplicado`, `validate_sector_cupo`, `CupoInputSchema`). El test del 409 existe; faltan los del 400 en el `PUT` | Tests que faltan |
| "Un cupo modificado no cambia la condición de pedidos ya cargados" | **Ya se cumple**: la condición se guarda en cada `Solicitud` al crear el pedido | Test del criterio "Listo cuando" |
| Portal: pantalla de cupos para Alimentación | **Falta.** Solo existe `carga-pedidos/services/cuposService.js` para el Jefe (solo lectura) | Sí: es casi todo el trabajo de S8 |

**No se modifican** `core/cupo.py`, `api_cupos.py` ni `schemas/cupos.py`: son compartidos con Carga y con la app privada (`web/controlers/cupo.py`), y ya cumplen el enunciado.

## 1. Objetivo

Al terminar S8, el usuario de Alimentación:

- Ve **"Cupos"** en la sidebar (entre Autorizaciones y Reportes) y entra a **`/cupos`**. Los otros roles no la ven y la ruta los manda a "Acceso denegado".
- Ve una **matriz sector × comida**:
  - **Filas:** todos los sectores **activos** (tengan o no cupos, así se pueden crear) y los **inactivos que tengan algún cupo**, marcados "Inactivo".
  - **Columnas:** Sector, Desayuno, Almuerzo, Merienda, Cena y **Editar**.
  - **Celda:** la cantidad, o "—" con texto para lector de pantalla "Sin cupo: todas las solicitudes de esta comida son excepción".
  - **Búsqueda** por nombre de sector (cuadro de texto, sin acentos ni mayúsculas) y **paginación** de 25 filas. **Orden** por sector A→Z o Z→A desde el encabezado "Sector". Los tres (`q`, `page`, `orden`) quedan en la URL.
  - **No hay** filtros de sector ni de comida.
- Con **Editar** en una fila abre un **popover** con cuatro campos numéricos, uno por comida:
  - Comida sin cupo + valor → se **crea** (`POST /api/quotas`).
  - Comida con cupo + valor distinto → se **modifica** (`PUT /api/quotas/{id}`, reenviando `sector_id` y `tipo_comida` sin cambios).
  - Comida con cupo + campo vacío → **error de formulario** ("No se puede quitar un cupo: dejá la cantidad o cancelá"). La API no permite borrar.
  - Valor no entero o ≤ 0 → error de formulario. La API también lo valida.
  - Debajo de cada campo con cupo: "Modificado el …" (`updated_at`).
  - Texto fijo: "El cambio aplica solo a los pedidos que se carguen después. Los pedidos ya cargados conservan su condición Regular o Excepción."
  - Guardar ejecuta los cambios **en secuencia**, se corta en el primer error y después **siempre** recarga la matriz.
- En las filas de sectores inactivos, **Editar** está deshabilitado y la fila explica "Sector inactivo: no se pueden crear ni modificar sus cupos".

**Criterio "Listo cuando" del artefacto:** "un cupo modificado no cambia la condición de pedidos ya cargados". Se verifica con un test de backend y en el navegador (sección 7.2, paso 6).

## 2. Decisiones

| Tema | Decisión | Motivo |
|---|---|---|
| Página | Ruta propia `/cupos` (`name: 'cupos'`), `meta: { roles: [ROLES.ALIMENTACION], title: 'Cupos por sector', menu: { label: 'Cupos', order: 25 } }`. **Sin** rutas de formulario (`/cupos/nuevo`, `/cupos/:id/editar`) | Pedido del usuario: una página en la sidebar como Autorizaciones y Reportes; crear y editar desde el popover |
| Ubicación en el código | Carpeta nueva **`modules/autorizacion/cupos/`**. La ruta se agrega en `modules/autorizacion/routes.js` (único archivo existente que se modifica en el portal). No se toca `router/index.js` | Pedido del usuario. Reusa `sectores()`, `COMIDAS` y `POR_PAGINA` del módulo importándolos, sin editarlos |
| Nada de `carga-pedidos/` | No se importa ni se modifica su `cuposService.js` | Es del módulo de Carga (otro integrante), solo lectura y con otro parseo |
| Matriz con todos los datos en el cliente | `GET /api/quotas` y `GET /api/sectors?estado=todos`, **todas las páginas** (25 por página), en paralelo. Búsqueda, orden y paginación de filas en el cliente | La matriz necesita todos los cupos de cada sector en la misma fila; con ~30 sectores y ≤ 120 cupos son pocas peticiones. La API ya cumple "búsqueda, orden y paginación de servidor" del enunciado; la pantalla agrupa por sector |
| Búsqueda | `<input type="search">` "Buscar sector". Compara sin acentos ni mayúsculas (`normalize('NFD')` sin diacríticos + `toLocaleLowerCase('es')`), por subcadena. Cada tecla hace `router.replace` con `q` y `page: 1` (sin entradas nuevas en el historial). Sin debounce: el filtrado es local | Pedido del usuario (búsqueda y/o paginación). `replace` evita llenar el Atrás con cada letra |
| Paginación | 25 filas por página (`POR_PAGINA`), con `page` en la URL y `router.push`. Si `page` es mayor que la última, se usa la última (no se reescribe la URL). Paginador simple: "Anterior", "Página X de Y" y "Siguiente". Oculto si hay una sola página | Igual que la bandeja |
| Orden | Botón en el encabezado "Sector" con `aria-sort` (`ascending`/`descending`). `orden=desc` en la URL; el default `asc` no se escribe. `localeCompare(..., 'es')` | "Resultados ordenables por sector" del enunciado |
| URL inválida | `q` se recorta (texto vacío = sin búsqueda), `page` usa `enteroDesde(..., 1)` y `orden` usa `unoDe` con default `asc`. Valores inválidos → defaults, sin error | Mismo criterio que `parseFiltros` |
| Popover | Mismo patrón que `PeriodoNav` (contenedor relativo, `useId`, `aria-expanded`/`aria-controls` en el botón Editar), con una diferencia: **no** se cierra con un clic afuera, para no perder lo cargado. Se cierra con **Cancelar**, **Escape** o al guardar sin errores. Al abrir, el foco va al primer campo; al cerrar, vuelve al botón Editar de esa fila. Uno abierto a la vez: abrir otro cierra el anterior **solo si no tiene cambios**; si los tiene, el botón Editar de otra fila no hace nada y el popover abierto muestra "Guardá o cancelá los cambios de <sector>" | Pedido del usuario (popover desde una columna Editar). Clic afuera = pérdida silenciosa de datos |
| Contenido del popover | `<form>` con `<fieldset>` y `<legend>` "Cupos de <sector>", 4 `<input type="number" min="1" step="1" inputmode="numeric">` con `<label>`. Errores por campo con `aria-describedby` y `aria-invalid` | Accesibilidad y validación en el cliente que pide el enunciado |
| Parseo de los campos (cliente) | Función pura `leerCantidad(texto)` → `{ tipo: 'vacio' }` \| `{ tipo: 'valor', cantidad }` \| `{ tipo: 'invalido', mensaje }`. Solo `^\d+$` con valor ≥ 1 es válido | "Parse, don't validate": el formulario guarda texto y se convierte una vez antes de armar los cambios |
| Cambios a enviar | Función pura `cambiosDeFila(fila, textos)` → `{ tipo: 'ok', cambios: Cambio[] }` \| `{ tipo: 'errores', errores: { [comida]: mensaje } }`, con `Cambio = { tipo: 'crear', comida, cantidad } \| { tipo: 'modificar', id, comida, cantidad }`. Una comida sin cambios no genera nada. Sin cambios → "Guardar" deshabilitado | Lógica testeable sin Vue |
| Guardado | Los cambios en el orden de `COMIDAS`, **en secuencia** (`for … await`). Se corta en el **primer error**. Siempre se recarga la matriz al final. Mensajes: todo bien → toast "Cupos de <sector> guardados (N cambios)" y se cierra el popover. Error en el k-ésimo → el popover queda abierto con los valores recargados de la API para las comidas ya guardadas y el error en el campo que falló; toast "Se guardaron k−1 de N cambios; falló <Comida>: <mensaje>" | Decisión del usuario. No hay endpoint de lote y "todo o nada" necesitaría uno |
| Errores de la API en el guardado | `ApiError` con `type === VALIDATION` y `fields` → mensaje en el campo de esa comida (`fields.cantidad`, `fields.sector_id` o el `message`). `CONFLICT` (409: otro usuario creó ese cupo) o `NOT_FOUND` (404) → mensaje en el campo y recarga (al recargar, el campo pasa a "modificar"). 401, 403 y 503 los maneja el cliente HTTP. Cualquier otro error se propaga al estado de error del popover ("No se pudo guardar", con el mensaje) | Usa los errores tipados que ya existen; no se traga nada |
| Concurrencia | Sin token de versión: el último `PUT` gana. El 409 cubre la creación duplicada | El enunciado no lo pide para cupos. Se documenta en Pendientes |
| Parseo de las respuestas | Estricto, con un `respuestaInvalida` propio del submódulo: `items` es un array; `id` y `sector_id` enteros ≥ 1; `tipo_comida` en `COMIDAS`; `cantidad` entero ≥ 1; `inserted_at` y `updated_at` fechas ISO válidas; `next` entero o `null`. La respuesta de `POST` y `PUT` se parsea con el mismo `aCupo` | Igual que `autorizacionService.js` |
| Cupo duplicado o huérfano | `matrizDeCupos(sectores, cupos)` tira `Error` si llegan dos cupos para el mismo sector y comida (posible: no hay UNIQUE en la base) o si un cupo apunta a un sector que no vino en `sectores()`. La vista lo muestra con `ErrorState` | No se pisa un dato en silencio |
| Sectores inactivos | Fila visible solo si tiene cupos; Editar deshabilitado con el motivo; también fuera del popover | La API respondería 400. Se muestran porque esos cupos existen |
| UNIQUE en la base | **No** se agrega | Sin cambio de esquema ni `reset-db`. Va a Pendientes |
| Esquema, seeds y permisos | **Sin cambios** | Ya está todo asignado a `alimentacion` |
| Rama | `feat/autorizacion-s8` desde `feature/autorizacion-pedidos`, merge local `--no-ff` al terminar | Igual que S1 a S7 |

## 3. Fuera de alcance

- Cambios en `core/cupo.py`, `api_cupos.py`, `schemas/cupos.py`, `core/pedido.py` y en `modules/carga-pedidos/`.
- Borrar cupos (la API no tiene `DELETE` y el enunciado de Autorización pide listar, crear y modificar).
- Mover un cupo a otro sector o comida: en el popover solo se cambia la cantidad.
- UNIQUE `(sector_id, tipo_comida)` en la base y token de versión para cupos.
- Mostrar en la matriz cuántas solicitudes regulares se usaron de cada cupo.
- Vista responsive específica para móvil (S11; alcanza con `.table-wrap`).
- Tests de componentes Vue. Solo funciones puras y parsers.

## 4. Git

```sh
cd code
git switch feature/autorizacion-pedidos
git log --oneline -3          # confirmar el merge de feat/autorizacion-s7 (e8c51c3)
git status                    # árbol limpio
git switch -c feat/autorizacion-s8
```

Commits sugeridos:

- `test: reglas de PUT /api/quotas y cupos no retroactivos`
- `docs(api): errores de PUT /api/quotas según Etapa 2`
- `feat(portal): matriz de cupos por sector`
- `feat(portal): edición de cupos por sector en un popover`

Al terminar: `git switch feature/autorizacion-pedidos && git merge --no-ff feat/autorizacion-s8`. **No pushear** `feat/autorizacion-s8`. `feature/autorizacion-pedidos` se pushea solo si lo pide el usuario.

## 5. Backend (`code/admin`)

### 5.1 Código

Sin cambios en `src/`. Si algún test de 5.3 falla, **se corrige el código de dominio** (`core/cupo.py`) para cumplir el enunciado y se documenta en el reporte. No se ajusta el test al comportamiento.

### 5.2 `admin/docs/openapi.yaml`

- `PUT /quotas/{quota_id}`: en la descripción, "La nueva cantidad se aplica solo a los pedidos que se carguen después; no cambia la condición de las solicitudes ya registradas ni depende de lo entregado. 400 si la cantidad no es mayor que cero o el sector está inactivo; 404 si el cupo no existe; 409 si ya existe otro cupo para el mismo sector y tipo de comida."
- Nada más cambia.

### 5.3 Tests

En `tests/unit/test_api_cupos.py`, **agregando** casos al final y reusando `data`, `login_as` y `count_cupos` (los existentes no se modifican):

| Test | Preparación | Esperado |
|---|---|---|
| `test_update_for_inactive_sector_returns_400` | Cupo de Pediatría; después `update_sector(pediatria.id, active=False)` | `PUT` con la misma comida y otra cantidad → 400 `business_rule` con `sector_id` en `fields`; la cantidad no cambia en la base |
| `test_update_with_invalid_cantidad_returns_400` | Parametrizado: `cantidad` 0, −1 y `"5"` | 400 `validation_error`; la cantidad no cambia |
| `test_update_below_delivered_is_allowed` | Un pedido autorizado del sector con almuerzo para 3 personas y una entrega (`create_entrega`) de almuerzo para las 3. Cupo de almuerzo en 3 | `PUT` con `cantidad: 1` → 200 y `cantidad == 1` (regla de Etapa 2: se eliminó el 400 por lo entregado) |
| `test_update_changes_updated_at` | Cupo creado; se fija la hora o se compara con el valor anterior | `updated_at` de la respuesta es posterior al de antes e `inserted_at` no cambia (campos "fecha de creación" y "última modificación" del enunciado) |

Archivo nuevo **`tests/unit/test_cupos_no_retroactivos.py`** (test del criterio "Listo cuando"). Para armar pedidos se reusan las fixtures de `test_api_autorizaciones.py` (moviéndolas a `conftest.py` **solo** si hace falta compartirlas; si se mueven, sin cambiar su comportamiento) o se crean con las funciones de `core/pedido.py`:

| Test | Preparación | Esperado |
|---|---|---|
| `test_bajar_el_cupo_no_cambia_pedidos_cargados` | Sector con cupo de desayuno 3 y un pedido `confirmado` con 5 desayunos | Antes: 3 `regular` y 2 `excepcion`. `PUT` del cupo a 1 → 200. Después: el mismo pedido sigue con 3 y 2, tanto en la base como en `GET /api/authorizations/{id}` |
| `test_subir_el_cupo_no_cambia_pedidos_cargados` | Igual, subiendo el cupo a 10 | El pedido sigue con 3 y 2 |
| `test_cambio_de_cupo_no_toca_pedidos_autorizados` | Pedido con 5 desayunos, autorizado completo | Bajar el cupo no cambia ni la condición ni `autorizada` de ninguna solicitud, ni `modificado_en` del pedido |
| `test_pedido_nuevo_usa_el_cupo_actual` | Después de bajar el cupo a 1, un pedido nuevo del mismo sector para otra fecha con 5 desayunos | 1 `regular` y 4 `excepcion` |
| `test_crear_cupo_no_cambia_pedidos_cargados` | Sector **sin** cupo de cena y un pedido con 2 cenas (las dos `excepcion`) | `POST` de un cupo de cena de 5 → 201; el pedido sigue con 2 `excepcion` |

Los tests de `test_api_autorizaciones.py`, `test_api_pedidos.py` y el resto **no se modifican** (salvo mover fixtures, si hace falta, sin cambiar su comportamiento).

## 6. Portal (`code/portal`)

### 6.1 Archivos

```
src/modules/autorizacion/
├── routes.js                              (modificado) ruta /cupos con meta.menu
└── cupos/                                 (nuevo)
    ├── cupos.js                           funciones puras: matriz, búsqueda, orden, páginas, URL, formulario
    ├── cupos.test.js
    ├── services/cuposService.js           GET/POST/PUT /api/quotas con parseo estricto
    ├── components/MatrizCupos.vue         tabla sector × comida + columna Editar
    ├── components/EditorCuposSector.vue   popover con el formulario de una fila
    └── views/CuposView.vue
```

No se tocan `router/index.js`, `constants/`, `api/`, `components/` globales, `filtros.js`, `autorizacionService.js` ni otros módulos. No se agregan dependencias.

### 6.2 `cupos/cupos.js` (puro)

```js
import { COMIDAS, POR_PAGINA } from '../filtros'

/**
 * @typedef {{ id: number, sectorId: number, comida: string, cantidad: number,
 *             creado: string, modificado: string }} Cupo
 * @typedef {{ sector: { id: number, nombre: string, activo: boolean },
 *             cupos: Map<string, Cupo> }} FilaCupos   // clave: comida
 */

/**
 * Filas de la matriz: sectores activos (con o sin cupos) e inactivos con algún cupo.
 * Tira Error si hay dos cupos para el mismo sector y comida, o un cupo de un sector desconocido.
 * @returns {FilaCupos[]} sin ordenar
 */
export function matrizDeCupos(sectores, cupos) {}

/** texto sin acentos y en minúsculas ('es'), para comparar */
export function normalizarTexto(texto) {}

/** filas cuyo nombre de sector contiene q (normalizado); q vacío = todas */
export function buscarFilas(filas, q) {}

/** array nuevo ordenado por nombre de sector con localeCompare 'es'; orden 'asc' | 'desc' */
export function ordenarFilas(filas, orden) {}

/**
 * { filas, pagina, totalPaginas } con POR_PAGINA filas por página.
 * pagina se acota a [1, totalPaginas]; con 0 filas: { filas: [], pagina: 1, totalPaginas: 1 }.
 */
export function paginarFilas(filas, pagina) {}

/** query de la URL -> { q: string, page: number, orden: 'asc' | 'desc' } (valores inválidos -> defaults) */
export function parseQueryCupos(query) {}

/** inverso: omite q vacío, page 1 y orden 'asc' */
export function aQueryCupos({ q, page, orden }) {}

/** texto del input -> { tipo: 'vacio' } | { tipo: 'valor', cantidad } | { tipo: 'invalido', mensaje } */
export function leerCantidad(texto) {}

/** textos iniciales del formulario: la cantidad de cada comida con cupo, '' en las demás */
export function textosDeFila(fila) {}

/**
 * Compara la fila con los textos del formulario ({ [comida]: string }).
 * -> { tipo: 'ok', cambios } en el orden de COMIDAS
 *  | { tipo: 'errores', errores: { [comida]: mensaje } }
 * Cambio = { tipo: 'crear', comida, cantidad } | { tipo: 'modificar', id, comida, cantidad }.
 * Vaciar una comida con cupo es error ("No se puede quitar un cupo…").
 */
export function cambiosDeFila(fila, textos) {}

/** true si algún texto difiere de textosDeFila(fila) (para habilitar Guardar y bloquear otro popover) */
export function hayCambios(fila, textos) {}
```

`cupos.test.js` cubre:
- `matrizDeCupos`, con los datos de los seeds: 7 sectores (Rayos inactivo sin cupos, Residentes activo sin cupos) y 13 cupos → 6 filas (sin Rayos); Residentes con 0 cupos; un inactivo con un cupo aparece; el error por duplicado y por sector desconocido.
- `buscarFilas`: "quiro" encuentra "Quirófano", "MÉDICOS" encuentra "Médicos Internos", y vacío o solo espacios devuelve todas.
- `ordenarFilas` en los dos sentidos, sin mutar la entrada.
- `paginarFilas`: 30 filas → 2 páginas (25 + 5); página 5 → acotada a 2; 0 filas.
- `parseQueryCupos` y `aQueryCupos`: válidos, inválidos (`page=0`, `page=abc`, `orden=foo`), ausentes e ida y vuelta.
- `leerCantidad`: `''`, `'  '`, `'3'`, `'0'`, `'-1'`, `'2.5'`, `'1e3'` y `'abc'`.
- `cambiosDeFila`, con el ejemplo obligatorio: Quirófano (desayuno 3, almuerzo 3, cena 40, sin merienda) con textos `{ desayuno: '1', almuerzo: '3', merienda: '5', cena: '40' }` → `[{ modificar, desayuno, 1 }, { crear, merienda, 5 }]`. También sin cambios → `[]`, cena `''` → error en cena, y textos inválidos → errores por comida.
- `hayCambios`.

### 6.3 `cupos/services/cuposService.js`

```js
/** GET /api/quotas, todas las páginas (per_page 25) -> Cupo[] */
export async function listarCupos() {}

/** POST /api/quotas { sector_id, tipo_comida, cantidad } -> Cupo */
export async function crearCupo({ sectorId, comida, cantidad }) {}

/** PUT /api/quotas/{id} { sector_id, tipo_comida, cantidad } -> Cupo */
export async function modificarCupo({ id, sectorId, comida, cantidad }) {}

/**
 * Ejecuta los cambios en orden y se corta en el primer error.
 * -> { tipo: 'ok', guardados: n }
 *  | { tipo: 'fallo', guardados: n, cambio, error: ApiError }   (solo VALIDATION, CONFLICT o NOT_FOUND)
 * Cualquier otro error se propaga.
 */
export async function guardarCambios(sectorId, cambios) {}
```

- `aCupo(json)`: parseo estricto (ver Decisiones) → `{ id, sectorId, comida, cantidad, creado, modificado }`. `sector_name` se ignora: el nombre sale de `sectores()`.
- `listarCupos` recorre `next` como `sectores()` y valida `items` y `next`.
- Los sectores se piden con `sectores()` de `../../services/autorizacionService` (sin modificarlo).

### 6.4 `cupos/components/MatrizCupos.vue`

- Props: `filas` (las de la página visible), `orden`, `editando` (id del sector con el popover abierto o `null`) y `bloqueado` (si hay cambios sin guardar). Emite `ordenar`, `editar(sectorId)` y `guardado`.
- `<table class="table">` dentro de `.table-wrap`, con `<caption>` visualmente oculto "Cupos por sector y comida".
- Encabezados con `scope="col"`. "Sector" es un `<button>` con `aria-sort` en el `<th>`.
- Primera celda de cada fila: `<th scope="row">` con el nombre y, si es inactivo, el badge "Inactivo".
- Celdas numéricas con la clase `num`. "—" con `<span class="sr-only">` "Sin cupo: todas las solicitudes de esta comida son excepción" (si no existe `.sr-only`, usar el patrón de "visualmente oculto" que ya use el proyecto).
- Columna Editar: botón "Editar" con `aria-label="Editar cupos de <sector>"`, `aria-expanded` y `aria-controls`. Deshabilitado en los inactivos, con el motivo en `title` y en texto visible pequeño.
- El popover (`EditorCuposSector`) se renderiza dentro de la celda de Editar de la fila activa, como en `PeriodoNav`.

### 6.5 `cupos/components/EditorCuposSector.vue`

- Props: `fila`. Emite `cerrar` y `guardado` (después de recargar). Expone `hayCambios`.
- Estado local: `textos` (de `textosDeFila`), `errores`, `guardando` y `avisoBloqueo`.
- Cuando cambia `fila` (después de una recarga), se recalculan los `textos` **solo** para las comidas sin error y sin edición pendiente: las que se guardaron bien muestran el valor de la API.
- Submit:
  1. `cambiosDeFila` → si hay errores, se marcan y se pone el foco en el primer campo con error.
  2. `guardarCambios` (Guardar y Cancelar deshabilitados, "Guardando…").
  3. Siempre se emite `guardado` para que la vista recargue.
  4. Si salió todo bien, toast y `cerrar`. Si hubo `fallo`, error en el campo de `cambio.comida` y toast con el resumen (ver Decisiones).
- Escape (en el contenedor) y Cancelar → `cerrar` (si está guardando, no hace nada).
- Marcado: ver Decisiones (`form`, `fieldset`, `legend`, `label`, `aria-invalid`, `aria-describedby`, "Modificado el …" con `formatearFechaHora` si ya existe en el proyecto; si no, `Intl.DateTimeFormat('es-AR', { dateStyle: 'short', timeStyle: 'short' })`).
- Texto fijo sobre la no retroactividad (ver Objetivo).

### 6.6 `cupos/views/CuposView.vue`

- `<h1>` "Cupos por sector" y un párrafo corto: "Cantidad máxima de solicitudes regulares por sector y comida. Las que la superan quedan como excepción."
- Carga `Promise.all([sectores(), listarCupos()])` con `useAsyncData` → `matrizDeCupos`. Estados `LoadingState`, `ErrorState` (con reintentar) y `EmptyState` ("No hay sectores activos").
- `parseQueryCupos(route.query)` → `buscarFilas` → `ordenarFilas` → `paginarFilas`.
- Búsqueda: `<label for="cupos-buscar">Buscar sector</label>` + `<input type="search" id="cupos-buscar">`. Cada `input` hace `router.replace({ query: aQueryCupos({ ...actual, q, page: 1 }) })`. Si no hay resultados: "Ningún sector coincide con «q»" y un botón "Limpiar búsqueda".
- Contador con `aria-live="polite"`: "N sectores" (o "N de M sectores" con búsqueda).
- Paginador (ver Decisiones), con `router.push`.
- `ordenar` → `router.push` con el orden invertido y `page: 1`.
- Popover: `editando` (sector id o `null`). Si el editor abierto tiene cambios, otro Editar no lo cambia: muestra el aviso. Cambiar de página, de orden o de búsqueda con el popover abierto lo cierra si no tiene cambios; si los tiene, el popover queda abierto pero se cierra si su fila deja de estar visible, después de un `useConfirm` "Hay cambios sin guardar en <sector>. ¿Descartarlos?". Si se cancela, la navegación no se hace.
- `guardado` → recarga los datos (sin cambiar la URL).

### 6.7 `routes.js`

```js
{
  path: '/cupos',
  name: 'cupos',
  component: () => import('./cupos/views/CuposView.vue'),
  meta: {
    roles: [ROLES.ALIMENTACION],
    title: 'Cupos por sector',
    menu: { label: 'Cupos', order: 25 },
  },
},
```

## 7. Verificación

### 7.1 Automática

```sh
cd code/admin && poetry run pytest          # nuevos en verde; las fallas previas de SQLite siguen igual (ver reportes S1 a S7)
cd code/portal && npm run lint && npm run build && npm test
```

### 7.2 Manual desde el portal

No hay cambio de esquema ni de permisos: la base de S7 sirve. Si hay un backend o un portal del usuario corriendo, levantar instancias propias en otros puertos y con una base temporal, como en S3, S6 y S7, y apagarlas al terminar. Usuarios: `alimentacion@test.com` / `alim1234` y `jefe.quirofano@test.com` (ver `seeds-per-slice.md`). Datos: tablas "Sectores y cupos" y de pedidos, y sección S8 de `seeds-per-slice.md`.

**Antes de la prueba**, anotar en el reporte con `curl` el detalle (`GET /api/authorizations/{id}`) de los pedidos 5 y 9 (desayuno de Quirófano: 3 regulares y 2 excepciones).

| # | Acción | Esperado |
|---|---|---|
| 1 | Entrar como Alimentación | "Cupos" aparece en la sidebar entre Autorizaciones y Reportes. `/cupos` muestra 6 filas (Alimentación, Hemoterapia, Intendencia, Médicos Internos, Quirófano y Residentes) con los 13 cupos de la tabla de seeds. Rayos no aparece. Residentes tiene las 4 celdas en "—" |
| 2 | Buscar "quiro", después "MÉDICOS", después "xyz" | Solo Quirófano; solo Médicos Internos; mensaje sin resultados y "Limpiar búsqueda". La URL tiene `q`. Atrás no recorre letra por letra |
| 3 | Clic en el encabezado "Sector" | Orden Z→A, `orden=desc` en la URL, `aria-sort="descending"`. Recargar conserva búsqueda y orden |
| 4 | Paginación: crear sectores activos desde la app privada hasta superar 25 (o anotar que se omite si no se puede) | Dos páginas; `page=2` en la URL; `page=99` muestra la última |
| 5 | Editar Hemoterapia: desayuno 4, guardar | Toast "Cupos de Hemoterapia guardados (1 cambio)"; la celda muestra 4; en la red, un `POST` |
| 6 | **Criterio "Listo cuando"**: editar Quirófano, desayuno de 3 a 1, guardar. Revisar los pedidos 5 y 9 en la bandeja / detalle (y con `curl`). Después, como `jefe.quirofano@test.com`, cargar un pedido para D+2 con 5 desayunos, y verlo como Alimentación | Los pedidos 5 y 9 siguen con 3 regulares y 2 excepciones en el desayuno. El pedido nuevo tiene 1 regular y 4 excepciones |
| 7 | Editar Quirófano: merienda 5 y cena 35 en la misma edición | Dos peticiones (`POST` merienda, `PUT` cena) en ese orden; toast con 2 cambios |
| 8 | Guardado parcial: en DevTools / route de Playwright bloquear (500) el `PUT` y editar Intendencia: desayuno 3 (PUT) y cena 2 (POST) | El primer cambio falla (no hay respuesta 400/409/404) → mensaje "No se pudo guardar" y no se hace el segundo. Repetir haciendo que el `POST` responda 409: el desayuno queda guardado, la cena muestra el error y el toast dice "Se guardaron 1 de 2 cambios; falló Cena: …" |
| 9 | 409 real: dos pestañas sin cupo de cena en Intendencia; crear cena en una y después en la otra | La segunda muestra el 409 en el campo Cena y, al recargar, el campo pasa a mostrar el cupo creado |
| 10 | Validación en el cliente: `0`, `-1`, `2.5`, `abc` y vaciar un cupo existente | Errores por campo, sin peticiones. "Guardar" deshabilitado si no hay cambios |
| 11 | Validación en el servidor: con `curl`, `PUT` con `cantidad: 0` y `POST` para Rayos | 400 `validation_error` / 400 `business_rule` con `sector_id` |
| 12 | Desactivar un sector con cupos desde la app privada (por ejemplo Hemoterapia) y recargar `/cupos` | La fila aparece con "Inactivo" y Editar deshabilitado con el motivo. Volver a activarlo al terminar |
| 13 | Pedido 7 (con entrega registrada): bajar el cupo de esa comida y ese sector por debajo de lo entregado | 200: el cupo se guarda (ya no existe el 400 por lo entregado) |
| 14 | Popover y teclado: abrir con Enter, Tab por los campos, Escape | Foco en el primer campo al abrir; Escape cierra y el foco vuelve a Editar. Clic afuera no cierra. Con cambios, Editar en otra fila muestra el aviso. Cambiar de página con cambios pide confirmación |
| 15 | Entrar como `jefe.quirofano@test.com` | "Cupos" no aparece en la sidebar; `/cupos` lleva a "Acceso denegado"; la pantalla de carga sigue mostrando su `ResumenCupos` como antes |
| 16 | Lector de pantalla o inspección | `caption`, `th` con `scope`, `aria-sort`, nombre accesible de cada Editar, `aria-expanded`, `legend`, `label`, `aria-invalid` y contador con `aria-live` |
| 17 | Regresión: bandeja de Autorizaciones y Reportes | Igual que antes |

### 7.3 Criterio de terminado

- 7.1 y 7.2 pasan completos, incluidos los pasos de navegador (los omitidos, con su motivo en el reporte).
- La rama está mergeada en local a `feature/autorizacion-pedidos` y **no** está pusheada.
- `seeds-per-slice.md`, sección S8, actualizada con:
  - los resultados reales del paso 6 (pedidos 5 y 9 y el pedido nuevo);
  - que el backend de cupos ya cumplía la regla de Etapa 2;
  - la nota de que los pasos 5 a 13 cambian cupos de la base de demo y cómo volver a los valores de la tabla (`reset-db` + seeds, o editarlos a mano).
- Está escrito el reporte en `documentacion-personal/reporte-s8-gestion-de-cupos.md` con la plantilla del artefacto:
  - Encabezado.
  - Alcance, incluida la reducción del backend respecto del artefacto (sección 0).
  - Cambios, con "base de datos: sin cambios".
  - Prueba manual con el resultado de cada paso.
  - Pendientes y decisiones.

## 8. Pendientes para otras slices

- **UNIQUE en la base:** `UniqueConstraint('sector_id', 'tipo_comida')` en `Cupo`, con `IntegrityError` → 409. Requiere `reset-db` o `ALTER` en el servidor de la cátedra. Coordinar con quien mantiene la app privada.
- **Concurrencia en cupos:** hoy el último `PUT` gana. Si hiciera falta, usar `updated_at` como versión, como `modificado_en` en los pedidos.
- **Coordinación con Carga:** `update_pedido` recalcula la condición de un pedido **confirmado** con el cupo actual cuando el Jefe lo modifica (regla del enunciado para "modificar pedido"). Es distinto de "cambiar el cupo no altera pedidos registrados" y se documenta en el reporte.
- **S9:** el sector Residentes aparece en la matriz sin cupos. Sus almuerzos generados no consumen cupo. Evaluar si conviene ocultar Residentes o deshabilitar Editar en esa fila (hoy se puede crear un cupo para Residentes, y la API lo permite).
- **S11:**
  - la matriz en móvil (6 columnas, `.table-wrap`) y el popover en pantallas chicas;
  - "Ampliaciones de la API": `PUT /api/quotas` según Etapa 2 (sin cambios de código; solo documentación).
