# Plan S7: Filtros completos y totales del período (Módulo de Autorización)

> **BORRADOR v1 (19/11/2026).** Decisiones cerradas. Los estilos del panel se ajustan visualmente durante la implementación.
>
> Plan de implementación de la séptima slice del "Plan de desarrollo — Módulo de Autorización" (artefacto). **El alcance es menor que el del artefacto** (ver "Por qué S7 es más chica que en el artefacto").
>
> Referencias:
> - Slice anterior: `documentacion-personal/plan-s6-calendario-y-vista-semanal.md` y `reporte-s6-calendario-y-vista-semanal.md`.
> - Datos de demo: `documentacion-personal/seeds-per-slice.md` (sección S7 y tabla de pedidos).
> - Enunciado, `Etapa_1y2_Trabajo Integrador_2026_PUBLIC.md`, Módulo de Autorización:
>   - "Filtrar por sector, tipo de comida, tipo de menú, condición (Regular o Excepción) y estado."
>   - "Visualizar las cantidades pedidas y autorizadas por separado."
>   - "Visualizar totales por sector, fecha, comida y tipo de menú."
>   - Endpoints sugeridos: `GET /api/menu-types` y `GET /api/summaries/authorizations`.
>
> **Precondición:** S6 está terminada y mergeada en `feature/autorizacion-pedidos` (merge `8c8648b`, con su reporte escrito). Este plan usa estos nombres del código actual:
>
> - Backend:
>   - `core/pedido.py`: `consulta_pedidos(sector_id, fecha_desde, fecha_hasta, tipo_comida, tipo_menu_id, condicion, estado) -> Select` y `list_pedidos`.
>   - `core/autorizacion_fechas.py`: `semana_de(fecha, sector_id, estado, condicion) -> Semana` (usa `consulta_pedidos`).
>   - `web/schemas/autorizacion_fechas.py`: `SemanaQuerySchema` (con `post_load` `con_defaults`) y `semana_query_schema`.
>   - `web/controlers/api_autorizaciones.py`: `week()` (`semana_de(**consulta)`).
>   - `core/resumen.py`: `resumen_autorizaciones(agrupar, **filtros)`, `sumar_totales`, `CAMPOS_AUTORIZACIONES`. `web/controlers/api_resumenes.py`: `GET /api/summaries/authorizations` (permiso `resumen_autorizaciones`). `web/schemas/resumenes.py`: `ResumenQuerySchema`.
>   - `web/controlers/api_catalogos.py`: `GET /api/menu-types` (permiso `user_auth`) → `[{ id, nombre }]`.
>   - `web/schemas/cupos.py`: `MEAL_TYPES`.
>   - Tests: `tests/unit/test_api_autorizacion_fechas.py` (helpers `get_semana`, etc.), `test_api_autorizaciones.py` (`crear`, `autorizar`, `set_estado`, `login_as`, `fijar_hoy`, `DIA`) y `test_api_resumenes.py`.
> - Portal, `src/modules/autorizacion/`:
>   - `filtros.js`: `parseFiltros`, `aQuery`, `unoDe`, `enteroDesde`, `primero`, `COMIDAS`, `CONDICIONES`, `ESTADOS`, `VISTAS` y `formatearDiaCorto`.
>   - `services/autorizacionService.js`: `listarAutorizaciones`, `semanaDeAutorizaciones`, `sectores`, `respuestaInvalida`, `esEntero`, `esObjeto`, `esTexto`, `esUnoDe` y `aFecha`.
>   - `components/FiltrosAutorizacion.vue`, `TablaPedidos.vue` y `TablaSemana.vue`; `views/BandejaAutorizacionView.vue` (`cargar()` devuelve `{ vista, pagina | semana }`).
>   - Vitest (`npm test`): `filtros.test.js`, `calendario.test.js` y `seleccion.test.js`.
>
> Si algo de esto no coincide con el código, **manda el código**: se adapta el plan sin renombrar lo existente.

## 0. Por qué S7 es más chica que en el artefacto

El artefacto le asignaba a S7 tres cosas. Releyendo el enunciado y el código:

| Parte del artefacto | Estado | Qué hace S7 |
|---|---|---|
| Backend: filtro y agrupamiento por condición en `GET /api/summaries/authorizations`, totales regular y excepción, 400 ante valores inválidos | **Ya está hecho.** Lo hizo el módulo de Reportes (commit `97c934c`): `condicion`, `agrupar=condicion`, `autorizadas_regulares`, `autorizadas_excepcion`, `totales` y los 400 | Nada. **No se toca** `core/resumen.py`, `api_resumenes.py` ni `schemas/resumenes.py`, porque los comparte Reportes |
| Totales `generada` | Depende de los almuerzos de residentes | Pasa a **S9** |
| Filtros de comida y menú en la bandeja | **Falta.** La API de la bandeja ya los acepta, pero el portal no | Sí |
| "Totales por sector, fecha, comida y tipo de menú" | Reportes los muestra, pero en otro módulo y sin los filtros de la bandeja | Sí: un panel chico en la bandeja que **reusa** el endpoint existente |

## 1. Objetivo

Al terminar S7, el usuario de Alimentación:

- En los filtros de la bandeja, **vista diaria y semanal**, tiene además **Comida** (desayuno, almuerzo, merienda, cena) y **Menú** (de `GET /api/menu-types`). Quedan en la URL (`tipo_comida`, `tipo_menu_id`), los limpia "Limpiar filtros" y cuentan para `hayFiltros`.
- Debajo de la tabla (o de la matriz semanal) ve el panel **"Totales del período"**:
  - El período es el día visible o la semana lunes–domingo visible.
  - Usa **todos** los filtros de la bandeja (sector, estado, condición, comida y menú) y cubre todas las páginas, no solo la visible.
  - Un selector **"Agrupar por"**: Comida (default), Sector, Menú, Condición y, solo en la vista semanal, Día. El agrupamiento va en la URL (`totales`; el default no se escribe).
  - Una tabla con cuatro columnas: **Pedidas**, **Pendientes** (solicitudes de pedidos confirmados), **Autorizadas** y **No autorizadas** (solicitudes que se dejaron sin autorizar en un pedido autorizado, más las de pedidos rechazados). Siempre Pedidas = Pendientes + Autorizadas + No autorizadas. Abajo, una fila **Total**. El desglose regular / excepción se ve agrupando por Condición.
  - Si hay un filtro de comida, menú o condición, una pista: "Los totales cuentan solo las solicitudes que cumplen los filtros de comida, menú y condición. La tabla muestra cada pedido completo."

**Criterio "Listo cuando" del artefacto:** "los totales del panel coinciden con la suma de los pedidos de la bandeja". Se verifica sin filtros de comida, menú o condición (ver la decisión "Semántica de los filtros"), con un test de backend y en el navegador.

## 2. Decisiones

| Tema | Decisión | Motivo |
|---|---|---|
| Endpoint de totales | `GET /api/summaries/authorizations` **sin cambios** de código, contrato ni CSV | Lo comparte el módulo de Reportes (otro integrante). Ya tiene todo lo que el panel necesita |
| Columnas del panel | **Pedidas, Pendientes, Autorizadas y No autorizadas.** No se usa `diferencia` de la API | `diferencia` = pedidas − autorizadas mezcla a los pendientes (nadie los revisó) con lo que se denegó. Separarlos es más explícito. Se llama "No autorizadas" y no "Rechazadas", porque "rechazado" es el estado del pedido entero y esta columna incluye también lo no autorizado dentro de pedidos autorizados |
| Cálculo de Pendientes y No autorizadas | Sin tocar el endpoint de resúmenes. **Pendientes:** si el filtro `estado` es `null`, segunda petición al mismo endpoint con los mismos parámetros y `estado=confirmado` (su `pedidas` por grupo y su total); si `estado === 'confirmado'`, pendientes = pedidas (sin segunda petición); si es `autorizado` o `rechazado`, pendientes = 0 (sin segunda petición). **No autorizadas** = pedidas − autorizadas − pendientes, por grupo y en el total. Un resultado negativo o un grupo de pendientes que no está en la respuesta general es un error (`respuestaInvalida`) | Las dos peticiones vienen del backend: el cliente solo resta totales ya calculados, no descarga el detalle. Agregar un campo al endpoint cambiaría el JSON y el CSV de Reportes (otro módulo) |
| Semántica de los filtros | La bandeja muestra los pedidos que tienen **alguna** solicitud que cumple comida + menú + condición (comportamiento actual, no cambia) y cada fila muestra el pedido completo. El panel cuenta **solo las solicitudes** que cumplen (semántica del resumen, igual que Reportes). Con esos filtros activos se muestra la pista del objetivo | Es lo que pide cada endpoint del enunciado. Cambiar las filas implicaría cambiar `GET /authorizations` y `/week` |
| Agrupamientos | Vista diaria: `comida`, `sector`, `menu` y `condicion`. Vista semanal: los mismos más `dia`. **No** `semana` ni `mes` | Son los del requisito ("sector, fecha, comida y tipo de menú") más condición. `dia` en la vista diaria daría una sola fila. `semana` y `mes` usan `date_trunc` (solo PostgreSQL) y no aplican a un período de a lo sumo 7 días |
| `totales` en la URL | `parseFiltros` suma `totales: unoDe(AGRUPAMIENTOS_TOTALES, ...) ?? 'comida'`. `aQuery` lo omite si es `'comida'`. Si la vista es diaria y `totales === 'dia'`, el panel usa `'comida'` (no se reescribe la URL) | Como `vista` en S6: la URL refleja lo que se ve y el link se puede compartir |
| Cambiar el agrupamiento | `actualizar({ totales })` **sin** volver a la página 1 | El agrupamiento no cambia la lista. `actualizar` hoy pone `page: 1`: el panel usa una función aparte que conserva `page` |
| Orden de las filas del panel | Comida en el orden del día (`COMIDAS`); condición Regular → Excepción; sector y menú por `nombre` (`localeCompare` con `'es'`); día ascendente | La API ordena por `grupo` (id): para sector y menú no sirve |
| Ubicación y carga del panel | Siempre visible, debajo de la tabla o matriz y del paginador. Tiene su propio `useAsyncData` y `watch` sobre los filtros y la vista, así un error en el panel **no rompe** la bandeja (`ErrorState` chico con reintentar dentro del panel). No se muestra mientras la bandeja está vacía, cargando o con error | Una petición más por cambio de filtro es barata. Un `<details>` plegable agregaría estado sin beneficio |
| Filtro de menú | `<select>` con los tipos de `GET /api/menu-types`, cargados una vez como los sectores. Si fallan, el select se deshabilita con "No se pudieron cargar los menús" (mismo patrón que sectores). Un `tipo_menu_id` de la URL que no está en el catálogo se manda igual; la API devuelve vacío | Igual que el sector, que tampoco se valida contra el catálogo en `parseFiltros` |
| Calendario y navegación "con pedidos" | **No** aplican comida ni menú (igual que `condicion` en S6) | Coherente con S6 y con `GET /authorization-dates` del enunciado (solo sector y estado). Se documenta como en S6 |
| Vista semanal | `GET /api/authorizations/week` acepta `tipo_comida` y `tipo_menu_id` (filtran pedidos, igual que `condicion`). Las celdas siguen contando todas las solicitudes del día del pedido | Mismo criterio que la vista diaria |
| Esquema de base, seeds y permisos | **Sin cambios.** No hace falta `reset-db` | `GET /api/menu-types` (`user_auth`) y `resumen_autorizaciones` ya están asignados a `alimentacion` |
| Rama | `feat/autorizacion-s7` sale de `feature/autorizacion-pedidos` y vuelve con merge local `--no-ff` | Igual que S1 a S6 |

## 3. Fuera de alcance

- Cambios en `core/resumen.py`, `api_resumenes.py`, `schemas/resumenes.py` y el módulo `reportes/` del portal.
- Totales y condición `generada` (S9; ver Pendientes).
- Gráficos en el panel (están en Reportes).
- Exportar el panel a CSV (S10).
- Resaltar en `TablaPedidos` la columna de la comida filtrada.
- Aplicar comida, menú o condición al calendario y a la navegación.
- Tests de componentes Vue. Solo funciones puras y parsers.

## 4. Git

```sh
cd code
git switch feature/autorizacion-pedidos
git log --oneline -3          # confirmar el merge de feat/autorizacion-s6 (8c8648b)
git status                    # árbol limpio
git switch -c feat/autorizacion-s7
```

Commits sugeridos:

- `feat(api): filtros de comida y menú en authorizations/week`
- `test: filtros de la semana y totales del período contra la bandeja`
- `feat(portal): filtros de comida y menú en la bandeja`
- `feat(portal): panel de totales del período`

Al terminar: `git switch feature/autorizacion-pedidos && git merge --no-ff feat/autorizacion-s7`. **No pushear** `feat/autorizacion-s7`. `feature/autorizacion-pedidos` se pushea solo si lo pide el usuario.

## 5. Backend (`code/admin`)

### 5.1 `core/autorizacion_fechas.py`: `semana_de`

```python
def semana_de(fecha: date, sector_id: int | None, estado: str | None,
              condicion: str | None, tipo_comida: str | None = None,
              tipo_menu_id: int | None = None) -> Semana:
```

- Se pasan a `consulta_pedidos(sector_id, lunes, domingo, tipo_comida=tipo_comida, tipo_menu_id=tipo_menu_id, condicion=condicion, estado=estado)`. Nada más cambia: las celdas cuentan todas las solicitudes del día.
- Actualizar el docstring: "los filtros de comida, menú y condición eligen pedidos (alguna solicitud que cumpla las tres); las celdas cuentan el día completo".

### 5.2 `web/schemas/autorizacion_fechas.py`: `SemanaQuerySchema`

- Suma `tipo_comida = fields.String(validate=validate.OneOf(MEAL_TYPES))` y `tipo_menu_id = fields.Integer(validate=validate.Range(min=1))`, igual que `PedidoQuerySchema`.
- `con_defaults` devuelve también `tipo_comida` y `tipo_menu_id` (`None` si faltan).
- `week()` no cambia (`semana_de(**consulta)`).

### 5.3 `admin/docs/openapi.yaml`

- `GET /authorizations/week`: parámetros `tipo_comida` y `tipo_menu_id` y el 400 por valores inválidos. Aclarar que filtran pedidos y que las celdas cuentan el día completo.
- `GET /summaries/authorizations` no cambia.

### 5.4 Tests

En `tests/unit/test_api_autorizacion_fechas.py`, reusando sus helpers y fixtures:

| Test | Preparación | Esperado |
|---|---|---|
| `test_semana_filtro_comida_y_menu` | En la semana de D: un pedido con solo almuerzo General y otro con cena Vegetariana (sectores distintos) | `tipo_comida=cena` → solo el segundo sector; `tipo_menu_id=<General>` → solo el primero; `tipo_comida=almuerzo&tipo_menu_id=<Vegetariano>` → vacío (misma solicitud); las celdas que aparecen tienen `pedidas` del día completo |
| `test_semana_filtros_invalidos_400` | `tipo_comida=brunch`, `tipo_menu_id=0`, `tipo_menu_id=abc` | 400 con el campo |

En un archivo nuevo `tests/unit/test_totales_bandeja.py` (**test del criterio "Listo cuando"**):

| Test | Preparación | Esperado |
|---|---|---|
| `test_totales_del_dia_coinciden_con_la_bandeja` | En D, cuatro pedidos de sectores distintos: uno confirmado, uno autorizado completo, uno autorizado parcial y uno rechazado, con comidas, menús y condiciones mezclados | Para cada combinación de `sector_id` y `estado` (ninguno, uno, ambos): la suma de `pedidos` (todas las comidas) de `GET /api/authorizations?fecha_desde=D&fecha_hasta=D` es igual a `totales.pedidas` de `GET /api/summaries/authorizations?fecha_desde=D&fecha_hasta=D&agrupar=comida`. Lo mismo con `autorizados` (los `null` de los pendientes cuentan 0) contra `totales.autorizadas`. Para cada `agrupar` (`comida`, `sector`, `menu` y `condicion`), los `totales` son los mismos |
| `test_totales_de_la_semana_coinciden_con_la_matriz` | Pedidos en tres días de la semana de D | La suma de `pedidas` de todas las celdas de `GET /api/authorizations/week?fecha=D` es igual a `totales.pedidas` de `summaries/authorizations` con el rango lunes–domingo, y lo mismo con `autorizadas` (con `null` = 0). Con `agrupar=dia`, cada fila coincide con la suma de la columna de ese día |
| `test_pendientes_del_resumen_coinciden_con_la_bandeja` | Igual que el primero | `totales.pedidas` de `summaries/authorizations` con `estado=confirmado` es igual a la suma de `pedidos` de los pedidos `confirmado` de la bandeja. Con los números del ejemplo, Pedidas − Autorizadas − Pendientes es igual a las solicitudes no autorizadas de los pedidos autorizados más todas las de los rechazados. Es la base del cálculo del panel |
| `test_totales_con_filtro_de_comida_cuentan_solicitudes` | El pedido mixto de arriba | Con `tipo_comida=cena`, `totales.pedidas` = cantidad de cenas, **menor** que la suma de la bandeja con el mismo filtro (documenta la semántica decidida) |

Los tests de `test_api_resumenes.py`, `test_list_pedidos.py` y `test_api_autorizaciones.py` **no se modifican**.

## 6. Portal (`code/portal`)

### 6.1 Archivos

```
src/modules/autorizacion/
├── filtros.js                           (modificado) comida, menuId y totales en parseFiltros/aQuery; AGRUPAMIENTOS_TOTALES
├── filtros.test.js                      (modificado)
├── totales.js                           (nuevo) agrupamientosDe(vista), ordenarFilasTotales(...)
├── totales.test.js                      (nuevo)
├── services/autorizacionService.js      (modificado) tiposDeMenu, totalesDelPeriodo; filtros nuevos en listar y semana
├── components/FiltrosAutorizacion.vue   (modificado) selects de comida y menú
├── components/TotalesPeriodo.vue        (nuevo) panel
└── views/BandejaAutorizacionView.vue    (modificado)
```

No se tocan `router/`, `constants/`, `api/`, `components/` globales ni otros módulos (en particular `reportes/`: no se importa nada de ahí). No se agregan dependencias.

### 6.2 `filtros.js`

- `parseFiltros` suma:
  - `comida: unoDe(COMIDAS, primero(query.tipo_comida))`
  - `menuId: enteroDesde(primero(query.tipo_menu_id), 1)`
  - `totales: unoDe(AGRUPAMIENTOS_TOTALES, primero(query.totales)) ?? 'comida'`
- `aQuery` escribe `tipo_comida`, `tipo_menu_id` (como texto) y `totales` (solo si no es `'comida'`), y omite los `null`.
- `export const AGRUPAMIENTOS_TOTALES = [{ valor: 'comida', etiqueta: 'Comida' }, { valor: 'sector', etiqueta: 'Sector' }, { valor: 'menu', etiqueta: 'Menú' }, { valor: 'condicion', etiqueta: 'Condición' }, { valor: 'dia', etiqueta: 'Día' }]`.
- `filtros.test.js`: los tres campos con valor válido, inválido y ausente; `aQuery` omite `totales=comida`; ida y vuelta `parseFiltros(aQuery(f))` igual a `f`.

### 6.3 `totales.js` (puro)

```js
/** agrupamientos que ofrece el panel en cada vista: 'dia' solo en la semanal */
export function agrupamientosDe(vista) {}

/** el agrupamiento efectivo: si no está disponible en la vista (dia en la diaria), 'comida' */
export function agrupamientoEfectivo(totales, vista) {}

/**
 * filas del panel ordenadas según el agrupamiento: comida en el orden de COMIDAS,
 * condición regular -> excepción, sector y menú por nombre ('es'), día ascendente.
 * Devuelve un array nuevo; tira Error si recibe un agrupamiento desconocido.
 */
export function ordenarFilasTotales(filas, agrupar) {}

/** etiqueta visible de una fila: etiqueta de comida o condición, nombre de sector o menú, formatearDiaCorto del día */
export function etiquetaDeFila(fila, agrupar) {}

/**
 * Une el resumen general con los pendientes. `pendientes` es:
 * - { tipo: 'resumen', resumen }: la respuesta parseada con estado=confirmado;
 * - { tipo: 'todo' }: el filtro de estado es confirmado (pendientes = pedidas);
 * - { tipo: 'ninguno' }: el filtro de estado es autorizado o rechazado (pendientes = 0).
 * Devuelve { filas: [{ grupo, nombre, pedidas, pendientes, autorizadas, noAutorizadas }], total: {...} }.
 * Tira Error si un grupo de pendientes no está en el general, si pendientes > pedidas,
 * o si noAutorizadas (= pedidas - autorizadas - pendientes) da negativo.
 */
export function combinarTotales(general, pendientes) {}
```

`totales.test.js`:
- los cuatro órdenes y las etiquetas, `agrupamientoEfectivo('dia', 'dia') === 'comida'` y `agrupamientosDe` en cada vista;
- `combinarTotales` con los tres tipos de `pendientes`: un grupo sin pendientes (pendientes 0), el total, y los tres errores.
- Ejemplo obligatorio: Hemoterapia pendiente con 10 almuerzos más Quirófano autorizado con 8 de 10 → Pedidas 20, Pendientes 10, Autorizadas 8, No autorizadas 2.

### 6.4 `services/autorizacionService.js`

- `listarAutorizaciones` y `semanaDeAutorizaciones` suman `tipo_comida: filtros.comida` y `tipo_menu_id: filtros.menuId`. **Verificar** que el cliente HTTP omite los parámetros `null` o `undefined` (S6 ya manda `sectorId` nulo). Si no los omite, filtrarlos en el servicio: `ResumenQuerySchema` y `SemanaQuerySchema` tienen `unknown = RAISE` y no aceptan vacíos.

```js
/** GET /api/menu-types -> [{ id, nombre }] ordenado por id; ids únicos */
export async function tiposDeMenu() {}

/**
 * GET /api/summaries/authorizations con el período y los filtros de la bandeja
 * (una o dos peticiones, ver Decisiones), unidas con combinarTotales.
 * @returns {Promise<{ filas: { grupo: string|number, nombre: string|null, pedidas: number,
 *   pendientes: number, autorizadas: number, noAutorizadas: number }[],
 *   total: { pedidas, pendientes, autorizadas, noAutorizadas } }>}
 */
export async function totalesDelPeriodo({ desde, hasta, sectorId, estado, condicion, comida, menuId, agrupar }) {}
```

- Una función interna `paramsDeTotales(...)` arma los parámetros: `fecha_desde`, `fecha_hasta`, `sector_id`, `estado`, `condicion` (omitido si es `null`; la API usa `todas`), `tipo_comida`, `tipo_menu_id` y `agrupar`. Se exporta para que S10 la reuse en el CSV.
- Con `estado === null`, las dos peticiones van en paralelo (`Promise.all`); la segunda es igual con `estado: 'confirmado'`.
- Parseo estricto de cada respuesta, con `respuestaInvalida`, antes de combinar:
  - `items` es un array y `totales` un objeto;
  - `pedidas` y `autorizadas` son enteros ≥ 0 y `autorizadas <= pedidas`;
  - `grupo` según el agrupamiento: comida en `COMIDAS`, condición en `CONDICIONES`, día con `aFecha`, sector y menú entero ≥ 1 con `nombre` texto no vacío;
  - sin `grupo` repetido.
- Se ignoran `diferencia`, `autorizadas_regulares` y `autorizadas_excepcion`.
- Un `Error` de `combinarTotales` se relanza como `respuestaInvalida` con el contexto.
- `tiposDeMenu` usa un parseo estricto: array, `id` entero ≥ 1, `nombre` texto y sin ids repetidos.

### 6.5 `components/FiltrosAutorizacion.vue`

- Props nuevas: `menus` (array) y `menusError` (boolean).
- Dos selects más, con el mismo patrón que los existentes:
  - **Comida** (`id="aut-comida"`): "Todas" + `COMIDAS`. Emite `{ comida }`.
  - **Menú** (`id="aut-menu"`): "Todos" + `menus`, o deshabilitado con "No se pudieron cargar los menús". Emite `{ menuId: Number | null }`.
- Orden: Sector, Estado, Comida, Menú, Condición.

### 6.6 `components/TotalesPeriodo.vue`

- Props: `filtros` (el objeto de `parseFiltros`) y `periodo` (`{ desde, hasta }`). Emite `agrupar(valor)`.
- `agrupar = agrupamientoEfectivo(filtros.totales, filtros.vista)`.
- Carga con `useAsyncData(() => totalesDelPeriodo({ ...periodo, ...filtros, agrupar }))` y un `watch` (`immediate: true`) sobre período, filtros (sin `page`) y `agrupar`.
- Marcado:
  - `<section class="card" aria-labelledby>` con `<h2>` "Totales del período" y un subtítulo con el período (`formatearFechaLarga` o "Semana del " + `formatearSemana`).
  - Un `<select>` "Agrupar por" con las opciones de `agrupamientosDe(filtros.vista)`.
  - `<table class="table">` con `caption` visualmente oculto y encabezados (agrupamiento), Pedidas, Pendientes, Autorizadas y No autorizadas. Números alineados con la clase `num`. `<tfoot>` con la fila Total.
  - Colores discretos que coinciden con los badges: Pendientes `--color-info`, Autorizadas `--color-success` y No autorizadas `--color-danger`. Solo en el encabezado o en un punto, nunca como único indicador.
  - Si no hay filas: "Sin solicitudes para estos filtros" (puede pasar con estado o filtros raros aunque la bandeja tenga pedidos).
  - Pista condicional por los filtros de comida, menú y condición, con el texto del objetivo.
  - `LoadingState` chico y `ErrorState` con reintentar, **dentro** del panel.
  - `aria-live="polite"` en el contenedor de la tabla, para anunciar el recálculo.

### 6.7 `views/BandejaAutorizacionView.vue`

- Carga los menús una vez, como los sectores (`useAsyncData(tiposDeMenu, { initialData: [] })` + `onMounted`), y los pasa a `FiltrosAutorizacion`.
- `limpiar()` también pone `comida: null` y `menuId: null`. `hayFiltros` los incluye. `totales` no es un filtro: no se limpia.
- `periodo`: vista diaria `{ desde: fecha, hasta: fecha }`; vista semanal `{ desde, hasta }` de `diasDeSemana(fecha)` (el mismo cálculo que `PeriodoNav`; si ya existe un helper compartido, usarlo).
- `<TotalesPeriodo>` debajo de la tabla o matriz (y del paginador), solo cuando la bandeja tiene datos (`!loading && !error && !vacio`). `@agrupar="(totales) => cambiarAgrupamiento(totales)"`.
- `cambiarAgrupamiento(totales)`: `router.push({ query: aQuery({ ...filtros.value, totales }) })`, **sin** tocar `page`.
- `TotalesPeriodo` no se recarga cuando cambia solo `page`: el `watch` excluye `page`.

## 7. Verificación

### 7.1 Automática

```sh
cd code/admin && poetry run pytest          # nuevos en verde; las fallas previas de SQLite siguen igual (ver reportes S1 a S6)
cd code/portal && npm run lint && npm run build && npm test
```

### 7.2 Manual desde el portal

No hay cambio de esquema ni de permisos: la base de S6 sirve. Si hay un backend o un portal del usuario corriendo, levantar instancias propias en otros puertos y con una base temporal, como en S3 y S6, y apagarlas al terminar. Usuario: `alimentacion@test.com` / `alim1234`. Datos: tabla de pedidos y sección S7 de `seeds-per-slice.md`.

**Antes de la prueba**, obtener con `curl` los números esperados (`GET /api/summaries/authorizations?fecha_desde=D&fecha_hasta=D&agrupar=comida`, ídem por semana) y anotarlos en el reporte. Con eso se comparan los pasos.

| # | Acción | Esperado |
|---|---|---|
| 1 | Bandeja de D (vista Día) sin filtros | Debajo de la tabla, el panel "Totales del período", agrupado por Comida (desayuno → cena). **Total pedidas = suma de la columna Total de la tabla** (contando lo pedido) y total autorizadas = suma de las autorizadas. Pendientes = lo pedido por los pedidos pendientes (Total de sus filas). No autorizadas = lo pedido no autorizado de los autorizados más todo lo de los rechazados. En cada fila, Pedidas = Pendientes + Autorizadas + No autorizadas. **Criterio "Listo cuando"** |
| 1b | Filtro estado Pendiente; después Autorizado | Con Pendiente: Pendientes = Pedidas, y Autorizadas y No autorizadas en 0. Con Autorizado: Pendientes 0. En la pestaña Network, una sola petición al resumen en los dos casos, y dos sin filtro de estado |
| 2 | "Agrupar por" Sector, Menú y Condición | La URL cambia (`totales=…`), la página de la bandeja no cambia, el Total es el mismo en los cuatro agrupamientos. Sector y menú ordenados por nombre. Sin opción "Día" |
| 3 | Filtro sector Hemoterapia, después estado Autorizado | La bandeja y el panel se recalculan, y el Total coincide con la suma de la tabla en los dos casos |
| 4 | Filtro comida **Merienda** en el rango de la sección S7 (pedidos 3, 4 y 12) | La bandeja muestra solo los pedidos con merienda (completos). El panel cuenta solo las meriendas y aparece la pista. Volver a Atrás restaura el filtro anterior |
| 5 | Filtro menú Celíaco | La bandeja casi no cambia (Celíaco está en los 13 pedidos) y el panel cuenta solo las solicitudes celíacas. Con comida Cena + menú Celíaco, se combinan sobre la misma solicitud |
| 6 | Filtro condición Excepción con agrupamiento Condición | El panel muestra solo la fila Excepción |
| 7 | Cargar `/autorizaciones?tipo_comida=brunch&tipo_menu_id=abc&totales=foo` | Filtros ignorados (defaults), panel por Comida, sin errores |
| 8 | "Limpiar filtros" | Quita sector, estado, condición, comida y menú. Conserva la vista, la fecha y el agrupamiento |
| 9 | Vista Semana en D | El panel tiene el período "Semana del …" y la opción Día. Con Día: una fila por día con pedidos, y cada una coincide con la suma de esa columna de la matriz. El Total coincide con la suma de todas las celdas |
| 10 | En la vista semanal, filtro comida Cena | La matriz muestra solo los sectores con cena esa semana (pedidos 4, 5, 11 y 13, si caen en la semana) y el panel cuenta solo las cenas |
| 11 | Volver a Día con `totales=dia` en la URL | El panel usa Comida y el select muestra Comida |
| 12 | Paginación: si un día tiene más de 25 pedidos (no pasa con los seeds; se puede omitir y anotarlo) | El panel cubre todas las páginas |
| 13 | Error en el panel: bloquear `/api/summaries/authorizations` (route de Playwright o DevTools) | `ErrorState` dentro del panel, con reintentar. La bandeja sigue funcionando |
| 14 | Error en menús: bloquear `/api/menu-types` | Select de menú deshabilitado con el aviso; el resto funciona |
| 15 | `curl` `GET /api/authorizations/week?fecha=D&tipo_comida=cena` y con `tipo_comida=brunch` | 200 filtrado / 400 `fields.tipo_comida` |
| 16 | Regresión de Reportes: abrir `/reportes` como Alimentación con el mes en curso | Igual que antes (no se tocó) |
| 17 | Lector de pantalla o inspección: panel | `h2`, `caption`, encabezados con `scope`, `aria-live` al recalcular y select con `label` |

### 7.3 Criterio de terminado

- 7.1 y 7.2 pasan completos, incluidos los pasos de navegador.
- La rama está mergeada en local a `feature/autorizacion-pedidos` y **no** está pusheada.
- `seeds-per-slice.md`, sección S7, actualizada con:
  - los números reales del panel para D y para la semana de D (agrupado por comida);
  - la aclaración de la semántica (la bandeja muestra pedidos completos y el panel cuenta solicitudes);
  - que el backend de resúmenes ya existía.
- Está escrito el reporte en `documentacion-personal/reporte-s7-filtros-y-totales.md` con la plantilla del artefacto:
  - Encabezado.
  - Alcance, incluida la reducción respecto del artefacto (sección 0).
  - Cambios, con "base de datos: sin cambios".
  - Prueba manual con el resultado de cada paso.
  - Pendientes y decisiones.

## 8. Pendientes para otras slices

- **S9:**
  - con `condicion = 'generada'`, hay que sumar `generada` a `CONDICIONES` de `filtros.js` y al parseo de `totalesDelPeriodo` (el agrupamiento por Condición tendrá una fila "Generada"). Los almuerzos generados nacen autorizados: cuentan en Pedidas y Autorizadas, así que las cuatro columnas siguen cerrando;
  - los totales `generada` separados que pide la ampliación de Resúmenes son del endpoint de Reportes: coordinarlo con su responsable;
  - el filtro de condición de la bandeja decide si ofrece "Generada".
- **S10:** la exportación de totales usa `GET /api/summaries/authorizations?formato=csv` con los **mismos** parámetros que arma `totalesDelPeriodo` (reusar esa función de parámetros), para que el CSV coincida con el panel.
- **S11:**
  - el panel en móvil (la tabla tiene 5 columnas: alcanza con `.table-wrap`);
  - "Ampliaciones de la API": `tipo_comida` y `tipo_menu_id` en `/authorizations/week`.
- **Coordinación con Reportes:** avisar que la bandeja usa `GET /api/summaries/authorizations` (`items`, `grupo`, `nombre`, `pedidas`, `autorizadas`, `totales` y el filtro `estado`). Cambiar esos nombres rompe el panel.
- **Opcional:** aplicar comida, menú y condición a las marcas del calendario y a la navegación "con pedidos".
