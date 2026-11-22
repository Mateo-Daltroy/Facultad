# Plan S10: Exportaciones CSV (Módulo de Autorización)

> **BORRADOR v1 (20/11/2026).** Decisiones cerradas. Los estilos de los botones y del diálogo se ajustan visualmente durante la implementación.
>
> Plan de implementación de la décima slice del "Plan de desarrollo — Módulo de Autorización" (artefacto). S10 agrega los **dos listados CSV** que el enunciado pide para este módulo. Los dos salen de `GET /api/authorized-meals?formato=csv`, no de `/summaries` (ver sección 0 y la decisión "Endpoint del listado de totales").
>
> Referencias:
> - Slice anterior: `documentacion-personal/plan-s9-almuerzos-de-residentes.md` y `reporte-s9-almuerzos-de-residentes.md`.
> - Datos de demo: `documentacion-personal/seeds-per-slice.md` (tablas "Personal" y "Pedidos", y sección S10).
> - Enunciado, `Etapa_1y2_Trabajo Integrador_2026_PUBLIC.md`:
>   - Módulo de Autorización, requerimientos: "Exportar listados a formato CSV, respetando los filtros aplicados:
>     - Listado detalle de personas autorizadas: apellido y nombre, DNI, sector, desayuno, almuerzo, merienda, cena y tipo de menú (general, vegetariano o celíaco).
>     - Listado con totales por sector, detallando la cantidad de viandas por comida y tipo de menú."
>   - Módulo de Autorización, endpoints sugeridos: "GET /api/authorized-meals y GET /api/summaries/authorizations con el parámetro formato=csv, para las exportaciones".
>   - `GET /api/authorized-meals` (Etapa 1):
>     - "Reglas de negocio: **solo deben aparecer pedidos en estado autorizado**";
>     - "Errores: 400 si no se envía ningún filtro".
>   - Ampliaciones de la API, "Exportaciones" (`GET /api/authorized-meals y GET /api/summaries/*`):
>     - "estos endpoints aceptan el parámetro opcional formato=csv. Con ese parámetro devuelven el resultado completo para los filtros recibidos, sin paginación, como archivo CSV";
>     - Rol: Servicio de Alimentación;
>     - "se aplican los mismos filtros, permisos y validaciones que en la consulta paginada, y **el rango de fechas es obligatorio**. En GET /api/authorized-meals, el CSV tiene **una fila por persona** con las columnas apellido y nombre, DNI, sector, desayuno, almuerzo, merienda, cena y tipo de menú";
>     - "Errores adicionales: 400 si falta el rango de fechas o el valor de formato es inválido; 403 si lo invoca un usuario sin rol Servicio de Alimentación".
>   - Módulo de Reportes, notas: "Los almuerzos de residentes generados […] se muestran agrupados como Residentes en los reportes por sector".
> - Artefacto, S10:
>   - **Backend:** `formato=csv` en `GET /api/authorized-meals` (una fila por persona: apellido y nombre, DNI, sector, desayuno, almuerzo, merienda, cena y menú) y en `GET /api/summaries/authorizations`. Rango obligatorio, módulo `csv` de la biblioteca estándar, `Content-Disposition` y BOM UTF-8 para Excel. Permiso `autorizacion_export`.
>   - **Frontend:** `exportService` con `responseType: 'blob'` y descarga por URL de objeto, con botones "Exportar detalle" y "Exportar totales" en la bandeja.
>   - **Tests:** columnas y filas del CSV, 400 sin rango y 400 con formato inválido.
>   - **Listo cuando:** el CSV abre en una planilla con los acentos correctos y respeta los filtros.
>
> **Precondición:** S9 está terminada y mergeada en `feature/autorizacion-pedidos` (el último merge de `git log` es el de `feat/autorizacion-s9`). S10 usa la condición `generada` y el sector Residentes de S9. **Si S9 no está mergeada, no se empieza S10**: se avisa al usuario y se detiene.
>
> Este plan usa estos nombres del código actual:
>
> - Backend (`code/admin/src`):
>   - `core/entrega.py` (de **Entrega**, de otro integrante):
>     - `_autorizadas()` (select de `Solicitud` join `Pedido` con `Pedido.estado == "autorizado"` y `Solicitud.autorizada IS TRUE`);
>     - `list_comidas_autorizadas(fecha, dni, texto, sector_id, tipo_comida, page, per_page)`: filtra por **una** `fecha`, DNI (dígitos, `contains`), texto (nombre o apellido, en minúsculas), sector y comida; ordena por fecha, apellido, nombre e id; pagina con `paginate`;
>     - `comidas_autorizadas_de_persona`, `ids_entregados`, `resumen_entregas`.
>   - `web/schemas/entregas.py` (de Entrega): `ComidaQuerySchema(PaginationQuerySchema)` con `unknown = RAISE`, `fecha`, `dni`, `nombre`, `sector_id`, `tipo_comida` y `validate_algun_filtro` ("Indicá al menos un filtro…"). También `ComidaAutorizadaSchema`.
>   - `web/controlers/api_comidas.py` (de Entrega): `api_comidas_bp` (`/authorized-meals`), `index()` con `@permission_required("comida_index")` y `show(person_id)`.
>   - `web/controlers/api_resumenes.py` (de **Reportes**): referencia de cómo ya se exporta un CSV (`_csv`: `csv.writer`, BOM `"﻿"`, `mimetype="text/csv"` y `Content-Disposition`) y de cómo se chequea un permiso extra solo para el CSV (`has_permission(g.current_user.id, "resumen_exportar")` + `werkzeug.exceptions.Forbidden`). **No se modifica.**
>   - `web/schemas/cupos.py`: `MEAL_TYPES` (orden de las comidas). `core/enum_tables.py`: `TipoMenu` (`id`, `nombre`). `core/solicitud.py`: `condiciones_validas` (con `generada`, desde S9). `core/sector.py`: `Sector`. `core/personal.py`: `Personal` (`apellido`, `nombre`, `dni`).
>   - `core/errors.py`: `BusinessRuleError(message, field=None)` (400 `business_rule`). Los `ValidationError` de marshmallow dan 400 `validation_error` con `fields`.
>   - `core/seeds.py`: `AUTORIZACION_PERMISSIONS` (se asigna a `administrador` y `alimentacion`) y `ENTREGA_PERMISSIONS` (`comida_index` lo tienen `comedor`, `alimentacion` y `administrador`).
>   - Tests:
>     - `tests/unit/test_api_entregas.py`: fixture `autorizado` (pedido de Quirófano de hoy con Gomez y Fernandez autorizados), `HOY = date.today()`, y helpers importados de `test_api_pedidos` (`client`, `data`, `login_as`, `crear` y `autorizar`);
>     - `tests/unit/test_api_autorizaciones.py` (`fijar_hoy` y `fijar_ahora`) y `tests/unit/test_api_almuerzos_residentes.py` (de S9: fixture `residentes` y helper `generar`).
>   - `admin/docs/openapi.yaml` (`/authorized-meals`, cerca de la línea 744).
> - Portal (`code/portal/src`):
>   - `api/httpClient.js`: `http.get(path, { params, responseType: 'blob' })` ya devuelve un `Blob`. Si la respuesta no es ok, el error se lee como JSON y se lanza un `ApiError`. Los `null`/`''` de `params` se omiten.
>   - `api/errors.js`: `ApiError` (`type`, `status`, `message`, `fields`) y `ERROR_TYPES`.
>   - `modules/autorizacion/filtros.js`: `parseFiltros(route.query, hoy)` → `{ vista, fecha, sectorId, estado, condicion, comida, menuId, totales, page }`, más `CONDICIONES`, `COMIDAS`, `ESTADOS`, `parseFechaISO`, `sumarDiasISO`, `hoyISO` y `formatearDiaCorto`.
>   - `modules/autorizacion/calendario.js`: `periodoDe(fecha, vista)` → `{ desde, hasta }` (el día o la semana de lunes a domingo).
>   - `modules/autorizacion/views/BandejaAutorizacionView.vue`:
>     - `filtros`, `periodo`, `listaSectores` y `listaMenus`;
>     - barra `.bandeja__acciones` (botón "Revisar pendientes" / "Ver todos" y la ayuda de cantidades).
>   - `modules/autorizacion/components/DialogoRechazo.vue`: patrón de diálogo (`<dialog>` con `showModal()`, prop `abierto`, `enviando`, `error`, eventos `confirmar`/`cancelar`, y foco devuelto al cerrar).
>   - `modules/autorizacion/residentes/residentes.js` (de S9): `parseRango` como referencia del formato `{ tipo: 'ok' | 'errores' }`.
>   - `stores/notifications.js` (`success`, `warning`, `error`).
>   - Vitest (`npm test`).
>
> Si algo de esto no coincide con el código, **manda el código**: se adapta el plan sin renombrar lo existente.
>
> Antes de escribir código Python o JS, leer `~/.claude/CLAUDE.md` (tipos y parseo en el borde, manejo de errores) y, si existe, el archivo de `~/.claude/lang-specs/` del lenguaje.

## 0. Qué ya existe y qué hace S10

| Parte | Estado | Qué hace S10 |
|---|---|---|
| `GET /api/authorized-meals` (JSON) | **Ya está** (Entrega): paginado, una fila **por solicitud**, filtra por una `fecha` única, sin menú ni condición | **Aditivo:** filtros `fecha_desde`/`fecha_hasta`, `tipo_menu_id` y `condicion`, válidos también en JSON. La forma de la respuesta JSON y los filtros existentes no cambian |
| `GET /api/authorized-meals?formato=csv` | **Falta** | Dos listados: `agrupar=persona` (detalle, default) y `agrupar=sector` (totales por sector, comida y menú) |
| `GET /api/summaries/authorizations?formato=csv` | **Ya está** (Reportes), con BOM, rango obligatorio y permiso `resumen_exportar`. Agrupa por **una** dimensión | **No se toca** (ver sección 2, "Endpoint del listado de totales") |
| Descarga de blobs en el portal | `httpClient` ya soporta `responseType: 'blob'`. Reportes descarga su CSV con código propio en `ReportesView` | Servicio y helper propios del módulo de Autorización. Reportes no se toca |
| Almuerzos generados (S9) | Están en pedidos `autorizado` del sector Residentes, con `condicion='generada'` | Aparecen solos en los dos CSV como sector **Residentes**, sin casos especiales |

## 1. Objetivo

Al terminar S10:

- En la bandeja (`/autorizaciones`), la barra de acciones tiene dos botones: **"Exportar detalle"** y **"Exportar totales por sector"**.
- Cada botón abre un **diálogo** con Desde y Hasta, precargados con el período visible (el día, o la semana de lunes a domingo) y editables, hasta 62 días. El diálogo muestra qué filtros de la bandeja se van a aplicar (sector, comida, menú y condición). "Descargar" baja el archivo.
- **Detalle** (`comidas-autorizadas_<desde>_<hasta>.csv`): una fila por **fecha + persona + sector**, con `Sí` o vacío en cada comida y el tipo de menú. Se abre en Excel o LibreOffice con columnas separadas y acentos correctos.
- **Totales por sector** (`totales-por-sector_<desde>_<hasta>.csv`): una fila por sector, una columna por cada **comida × menú** con la cantidad de viandas autorizadas, la columna Total y la fila Total.
- Con el filtro de estado en **Confirmado** o **Rechazado**, los dos botones quedan deshabilitados con el motivo "Solo se exportan comidas autorizadas".
- Comedor y los Jefes reciben 403 si piden `formato=csv`. Comedor sigue usando el JSON de `authorized-meals` como antes.

**Criterio "Listo cuando" del artefacto:** "el CSV abre en una planilla con acentos correctos y respeta los filtros". Se verifica con tests de backend (sección 5.3) y en la prueba manual (sección 7.2, pasos 3 a 9).

## 2. Decisiones

| Tema | Decisión | Motivo |
|---|---|---|
| Qué se exporta desde Autorización | **Solo los dos listados del enunciado:** detalle por persona y totales por sector × comida × menú. **No** se agrega un botón de exportar en el panel "Totales del período" (S7) | El panel no es uno de los listados del enunciado. La exportación de resúmenes es del módulo de Reportes, que ya la tiene. La nota "S10: CSV del panel" del reporte de S7 queda descartada (anotarlo en el reporte) |
| **Endpoint del listado de totales** | `GET /api/authorized-meals?formato=csv&agrupar=sector`, **no** `/api/summaries/authorizations` | Decisión del usuario. `summaries` agrupa por una sola dimensión y no puede dar sector × comida × menú. Para darlo habría que agregar un agrupamiento compuesto en `core/resumen.py` (Reportes), que por regla del enunciado ("el CSV contiene los mismos agrupamientos que la respuesta en JSON") cambiaría también su JSON. El endpoint es **sugerido**, no obligatorio. Además, el listado sale de los mismos datos y filtros que el detalle (comidas autorizadas), así que los dos CSV son coherentes entre sí. **Anotar esta justificación en "Pendientes y decisiones" del reporte** |
| **Solo pedidos autorizados (sin filtro `estado`)** | `authorized-meals` (JSON y CSV) sigue listando **solo** solicitudes `autorizada=True` de pedidos `autorizado`. **No** recibe `estado` (un `estado` en la query da 400 por `unknown = RAISE`, como hoy) | Decisión del usuario, basada en el enunciado de `GET /api/authorized-meals`: "solo deben aparecer pedidos en estado autorizado". Mostrar pendientes o rechazados en un listado de "personas autorizadas" lo contradiría. **Copiar esta decisión, con la cita, en "Pendientes y decisiones" del reporte de S10**, para poder justificarla |
| Bandeja con filtro de estado | `estado` `confirmado` o `rechazado` → botones deshabilitados con el motivo visible "Solo se exportan comidas autorizadas: quitá el filtro de estado o elegí Autorizado." `estado` vacío o `autorizado` → habilitados, y no se manda `estado` a la API | Consecuencia de la fila anterior: con esos estados el resultado sería siempre vacío |
| Fila del detalle | Una fila por **(fecha, persona, sector)**. Si una persona tiene comidas autorizadas en dos sectores el mismo día, sale en dos filas | Decisión del usuario. Con un rango, "una fila por persona" necesita la fecha. Así sirve para armar las viandas de cada día |
| Columnas del detalle | `Fecha;Apellido y nombre;DNI;Sector;Desayuno;Almuerzo;Merienda;Cena;Tipo de menú` | Las del enunciado, más `Fecha` adelante (rango) |
| Celdas del detalle | Comidas: `Sí` si está autorizada, vacío si no. `Apellido y nombre` = `"<Apellido>, <Nombre>"`. `Fecha` en ISO (`2026-10-20`). `Tipo de menú`: los nombres de los menús de las comidas de la fila, sin repetir, en orden de `tipos_menu.id` y unidos con `" / "` (normalmente uno solo) | Una persona puede tener menús distintos en distintas comidas. ISO es inequívoco y Excel/LibreOffice lo reconocen como fecha |
| Orden del detalle | Fecha, sector (`name`, `casefold`), apellido, nombre, DNI y `personal_id` | Agrupa visualmente por día y sector |
| Matriz (totales por sector) | Una fila por sector con al menos una vianda autorizada para los filtros, ordenada por `name` (`casefold`). Columnas: `Sector`, una por cada **comida × menú** (`"<Comida> <Menú>"`, por ejemplo `Almuerzo Vegetariano`), y `Total`. Al final, la fila `Total` (sumas por columna). Cuenta **solo autorizadas**. Comidas en el orden de `MEAL_TYPES` y menús leídos de la tabla `tipos_menu` por id (no fijos en el código). Etiquetas de comida con mayúscula inicial | Decisión del usuario ("viandas" = lo que se prepara). Coherente con el detalle |
| Sin resultados | Detalle: solo la cabecera. Matriz: la cabecera y la fila `Total` en 0. **200** en los dos casos | Un período sin autorizadas es un resultado válido, no un error |
| Filtros nuevos en `authorized-meals` | `fecha_desde` y `fecha_hasta` (`Date`), `tipo_menu_id` (`Integer ≥ 1`) y `condicion` (`OneOf(sorted(condiciones_validas))`: `regular`, `excepcion` o `generada`; ausente = todas). Valen **también en JSON** | "Respetar los filtros aplicados" de la bandeja (sector, comida, menú y condición). Son aditivos: no cambian lo que devolvía una consulta existente |
| `fecha` vs. rango | `fecha` y (`fecha_desde`/`fecha_hasta`) son **excluyentes**: si vienen los dos → 400 `validation_error` en `fecha`. El rango va completo: si falta uno de los dos extremos → 400 en el que falta. El rango cuenta como filtro para `validate_algun_filtro` | Sin ambigüedad sobre qué fecha manda |
| Reglas del rango | `fecha_hasta ≥ fecha_desde` y como máximo **62 días** (`MAX_DIAS_EXPORTACION`), en JSON y en CSV. Las **fechas pasadas se permiten** (exportar no modifica nada) | 62 como `GET /api/authorization-dates` (S6). Un solo parser del rango para los dos formatos |
| `formato` | `OneOf(["json", "csv"])`, default `json`. Inválido → 400 `validation_error` en `formato` | Enunciado |
| CSV sin rango | `formato=csv` con `fecha` (o sin rango) → 400 `validation_error` con `fecha_desde` y `fecha_hasta` ("Obligatorio para exportar.") | Enunciado: "el rango de fechas es obligatorio". Mismo texto que `ResumenQuerySchema` |
| `agrupar` | `OneOf(["persona", "sector"])`, default `persona`. Solo con `formato=csv`: `agrupar` en JSON → 400 `validation_error` en `agrupar` ("Solo se usa con formato=csv.") | El JSON paginado no cambia de forma |
| Paginación con CSV | `page` y `per_page` se aceptan (son del schema base) pero se **ignoran** con `formato=csv` | Enunciado: "sin paginación" |
| Permisos | El JSON sigue con `comida_index`. El CSV exige además **`autorizacion_export`** (permiso nuevo del artefacto), que va en `AUTORIZACION_PERMISSIONS` (`administrador` y `alimentacion`). Sin él → **403** ("Solo el Servicio de Alimentación puede exportar las comidas autorizadas."). Mismo patrón que `resumen_exportar` en `api_resumenes.py` | Enunciado (403 si no es Alimentación). Comedor tiene `comida_index` y no debe poder exportar |
| Formato del archivo | `csv.writer(delimiter=";", lineterminator="\r\n")`, BOM UTF-8 al principio, `mimetype="text/csv"` con `charset=utf-8` y `Content-Disposition: attachment; filename="..."` | `;` porque Excel con configuración regional es-AR abre un CSV separado por comas en una sola columna. El BOM hace que reconozca UTF-8 (acentos) |
| Nombres de archivo | `comidas-autorizadas_<desde>_<hasta>.csv` y `totales-por-sector_<desde>_<hasta>.csv` (fechas ISO). El portal arma **el mismo nombre** (no puede leer `Content-Disposition` a través de `httpClient`) | Que el archivo diga qué período contiene |
| Inyección de fórmulas | A toda celda de **texto** que empiece con `=`, `+`, `-`, `@`, tabulador o retorno de carro se le antepone `'`. Los números de la matriz no se tocan | Los nombres y DNI los carga gente. Es la mitigación estándar (OWASP CSV injection) |
| Capas | Dominio nuevo en `core/exportacion.py` (devuelve dataclasses, no texto). El filtrado se comparte con `list_comidas_autorizadas` mediante un builder extraído en `core/entrega.py`, que no cambia el comportamiento de lo existente. La serialización a CSV va en un helper web (`web/csv_export.py`) | Separar dominio de formato. Una sola definición de "comida autorizada que cumple los filtros" para el JSON y los dos CSV |
| Parseo en el borde | `ComidaQuerySchema` hace `post_load` → `ConsultaComidas`: una unión discriminada `ConsultaPaginada | ExportacionCsv` (ver 5.1). El controlador hace `match` sobre el tipo | `~/.claude/CLAUDE.md`: parse, don't validate |
| Rango en el portal | Diálogo con Desde y Hasta precargados con `periodoDe(filtros.fecha, filtros.vista)`, validados en el cliente con las mismas reglas (obligatorias, `hasta ≥ desde`, ≤ 62 días). El servidor es quien decide | Decisión del usuario: exportar más que el período visible (por ejemplo, un mes) sin salir de la bandeja |
| Qué filtros de la bandeja se mandan | `sectorId`, `comida`, `menuId` y `condicion`. **No** se mandan `vista`, `fecha`, `page`, `totales` ni `estado` | `vista`, `fecha` y `page` los reemplaza el rango del diálogo. `totales` es del panel. `estado`, ver arriba |
| Archivos de otros integrantes | `core/entrega.py`, `web/schemas/entregas.py`, `web/controlers/api_comidas.py` y `admin/docs/openapi.yaml` (Entrega): **solo cambios aditivos**. `api_resumenes.py`, `core/resumen.py` y `modules/reportes/` (Reportes) y `modules/entrega/` **no se tocan** | Los cambios no alteran respuestas ni validaciones existentes. Se listan en el reporte |
| Esquema | **Sin cambios de esquema.** El permiso nuevo requiere volver a correr los seeds (o `reset-db` + seeds) | Igual que los permisos de S6 y S9 |
| Rama | `feat/autorizacion-s10` desde `feature/autorizacion-pedidos`, merge local `--no-ff` al terminar | Igual que S1 a S9 |

## 3. Fuera de alcance

- `formato=csv` en `/summaries/*` (ya existe en Reportes) y cualquier cambio en `api_resumenes.py`, `core/resumen.py` o `modules/reportes/`. En particular, el separador `,` del CSV de Reportes (ver sección 8).
- Botón de exportar en el panel "Totales del período".
- Filtro `estado` en `authorized-meals`, y exportar pedidos pendientes o rechazados.
- Columnas extra en el detalle (condición, entregada, número de pedido).
- Pedidas en la matriz (solo autorizadas).
- Exportar a `.xlsx` y cualquier dependencia nueva.
- `GET /api/authorized-meals/{person_id}`: no cambia.
- Vista responsive específica del diálogo (S11; alcanza con el layout de `DialogoRechazo`).
- Tests de componentes Vue: solo funciones puras y parsers.

## 4. Git

```sh
cd code
git switch feature/autorizacion-pedidos
git log --oneline -3          # confirmar el merge de feat/autorizacion-s9; si no está, DETENERSE
git status                    # árbol limpio
git switch -c feat/autorizacion-s10
```

Commits sugeridos, en este orden:

1. `test: exportaciones CSV de comidas autorizadas` (los tests nuevos de la sección 5.3, en rojo)
2. `feat(api): filtros de rango, menú y condición en authorized-meals`
3. `feat(api): formato=csv en authorized-meals (detalle y totales por sector)`
4. `docs(api): exportaciones CSV de authorized-meals`
5. `feat(portal): exportar detalle y totales por sector desde la bandeja`

Si el primer commit en rojo complica la historia, se puede unir con el 2 o el 3. Al terminar: `git switch feature/autorizacion-pedidos && git merge --no-ff feat/autorizacion-s10`. **No pushear** `feat/autorizacion-s10`. `feature/autorizacion-pedidos` se pushea solo si lo pide el usuario.

## 5. Backend (`code/admin`)

### 5.1 Código

#### `core/entrega.py` (de Entrega, cambio aditivo)

Extraer el filtrado de `list_comidas_autorizadas` a una función pública que **no** ordena ni pagina, y agregarle los filtros nuevos. `list_comidas_autorizadas` sigue con su firma actual, más kwargs nuevos opcionales, y su comportamiento con los filtros de antes no cambia.

```python
@dataclass(frozen=True)
class FiltrosComidas:
    """filtros de comidas autorizadas. fecha y rango son excluyentes (lo garantiza el schema)"""
    fecha: date | None = None
    fecha_desde: date | None = None
    fecha_hasta: date | None = None
    dni: str | None = None
    texto: str | None = None
    sector_id: int | None = None
    tipo_comida: str | None = None
    tipo_menu_id: int | None = None
    condicion: str | None = None


def consulta_comidas_autorizadas(filtros: FiltrosComidas) -> Select:
    """select de Solicitud autorizadas (pedido autorizado + solicitud autorizada) con join a
    Personal y los filtros aplicados. sin orden ni paginación"""


def list_comidas_autorizadas(fecha=None, dni=None, texto=None, sector_id=None, tipo_comida=None,
                             page=1, per_page=25, *, fecha_desde=None, fecha_hasta=None,
                             tipo_menu_id=None, condicion=None) -> Pagination:
    """igual que antes; arma FiltrosComidas, usa consulta_comidas_autorizadas y conserva el
    orden (fecha, apellido, nombre, id) y la paginación"""
```

- Rango: `Solicitud.fecha.between(fecha_desde, fecha_hasta)`. Menú: `Solicitud.tipo_menu_id == tipo_menu_id`. Condición: `Solicitud.condicion == condicion`.
- DNI, texto, sector y comida: exactamente como hoy (mover el código, no reescribirlo).

#### `core/exportacion.py` (nuevo)

```python
MAX_DIAS_EXPORTACION = 62


@dataclass(frozen=True)
class RangoExportacion:
    desde: date
    hasta: date

    @staticmethod
    def parse(desde: date, hasta: date) -> "RangoExportacion":
        """único parser del rango (JSON y CSV): BusinessRuleError field fecha_hasta si
        hasta < desde ("No puede ser anterior a fecha_desde.") o si el rango supera
        MAX_DIAS_EXPORTACION ("El rango no puede superar 62 días.")"""


@dataclass(frozen=True)
class FilaDetalle:
    fecha: date
    apellido: str
    nombre: str
    dni: str
    sector: str
    comidas: frozenset[str]        # subconjunto de MEAL_TYPES
    menus: tuple[str, ...]         # nombres sin repetir, en orden de tipos_menu.id


def detalle_por_persona(filtros: FiltrosComidas) -> list[FilaDetalle]:
    """una fila por (fecha, personal_id, sector_id) con las comidas autorizadas que cumplen
    los filtros. orden: fecha, sector (casefold), apellido, nombre, dni, personal_id"""


@dataclass(frozen=True)
class FilaSector:
    sector: str
    cantidades: Mapping[tuple[str, int], int]   # (tipo_comida, tipo_menu_id) -> viandas; faltante = 0

    def total(self) -> int: ...


@dataclass(frozen=True)
class MatrizSector:
    comidas: tuple[str, ...]                # MEAL_TYPES, en orden
    menus: tuple[tuple[int, str], ...]      # (id, nombre) de tipos_menu, por id
    filas: tuple[FilaSector, ...]           # sectores con al menos una vianda, por nombre (casefold)

    def total_columna(self, comida: str, menu_id: int) -> int: ...
    def total(self) -> int: ...


def matriz_por_sector(filtros: FiltrosComidas) -> MatrizSector:
    """viandas autorizadas por sector, comida y menú (un GROUP BY sector, tipo_comida,
    tipo_menu_id sobre consulta_comidas_autorizadas)"""
```

Implementación sugerida:
- **Detalle:** a partir de `consulta_comidas_autorizadas(filtros)`, seleccionar solo las columnas necesarias (`Solicitud.fecha`, `Solicitud.tipo_comida`, `Solicitud.tipo_menu_id`, `Personal.id`, `Personal.apellido`, `Personal.nombre`, `Personal.dni`, `Sector.id`, `Sector.name`) con un join a `Sector`. Agrupar en Python por `(fecha, personal_id, sector_id)` y traer los nombres de menú con una consulta a `TipoMenu`. Evitar el N+1 de `ComidaAutorizadaSchema` (que hace `get_tipo_menu_by_id` por fila).
- **Matriz:** `select(Sector.name, Solicitud.tipo_comida, Solicitud.tipo_menu_id, func.count())` sobre la misma consulta, `group_by` los tres. Los menús salen de `select(TipoMenu).order_by(TipoMenu.id)`.
- `MEAL_TYPES` está en `web/schemas/cupos.py`. Si importarlo desde `core` invierte las capas, usar la constante equivalente de `core` (`validate_tipo_comida` en `core/cupo.py` tiene la lista) o moverla a `core` con un re-export en `web/schemas/cupos.py`, sin cambiar su valor.

#### `web/schemas/entregas.py` (de Entrega, cambio aditivo)

`ComidaQuerySchema` suma:
- `fecha_desde`, `fecha_hasta` (`Date`);
- `tipo_menu_id` (`Integer`, `Range(min=1)`);
- `condicion` (`String`, `OneOf(sorted(condiciones_validas))`);
- `formato` (`String`, `load_default="json"`, `OneOf(["json", "csv"])`);
- `agrupar` (`String`, `load_default=None`, `OneOf(["persona", "sector"])`).

`@validates_schema` (además de `validate_algun_filtro`, que ahora también acepta el rango, `tipo_menu_id` y `condicion` como filtro):
- `fecha` + algún extremo del rango → error en `fecha` ("No se puede combinar con fecha_desde/fecha_hasta.");
- un extremo del rango sin el otro → error en el que falta ("Obligatorio si se envía fecha_desde/fecha_hasta.");
- `formato == "csv"` sin rango completo → errores en `fecha_desde` y `fecha_hasta` ("Obligatorio para exportar.");
- `agrupar` con `formato == "json"` → error en `agrupar` ("Solo se usa con formato=csv.").

`@post_load` → `ConsultaComidas`:

```python
@dataclass(frozen=True)
class ConsultaPaginada:
    filtros: FiltrosComidas
    page: int
    per_page: int


@dataclass(frozen=True)
class ExportacionCsv:
    filtros: FiltrosComidas          # con fecha_desde y fecha_hasta, y fecha=None
    rango: RangoExportacion
    agrupar: Literal["persona", "sector"]


ConsultaComidas = ConsultaPaginada | ExportacionCsv
```

- El rango se convierte con `RangoExportacion.parse` también en JSON, cuando viene. Su `BusinessRuleError` sale como 400 `business_rule` (el post_load no lo atrapa).
- `nombre` se mapea a `FiltrosComidas.texto`, como hoy hace el controlador.

#### `web/csv_export.py` (nuevo)

```python
def respuesta_csv(nombre_archivo: str, cabecera: Sequence[str],
                  filas: Iterable[Sequence[str | int]]) -> Response:
    """CSV con ';', '\r\n', BOM UTF-8, text/csv; charset=utf-8 y Content-Disposition attachment.
    las celdas str pasan por _celda_segura (prefijo ' ante = + - @ \t \r); los int se escriben tal cual"""
```

#### `web/controlers/api_comidas.py` (de Entrega, cambio aditivo)

```python
@api_comidas_bp.get("")
@permission_required("comida_index")
def index():
    consulta = comida_query_schema.load(request.args.to_dict())
    match consulta:
        case ConsultaPaginada():
            ...  # exactamente lo de hoy, con list_comidas_autorizadas(**campos de consulta.filtros, page, per_page)
        case ExportacionCsv(agrupar="persona"):
            _exigir_permiso_exportar()
            return respuesta_csv(f"comidas-autorizadas_{desde}_{hasta}.csv", CABECERA_DETALLE,
                                 _filas_detalle(detalle_por_persona(consulta.filtros)))
        case ExportacionCsv(agrupar="sector"):
            _exigir_permiso_exportar()
            matriz = matriz_por_sector(consulta.filtros)
            return respuesta_csv(f"totales-por-sector_{desde}_{hasta}.csv", _cabecera_matriz(matriz),
                                 _filas_matriz(matriz))
```

- `_exigir_permiso_exportar()`: `if not has_permission(g.current_user.id, "autorizacion_export"): raise Forbidden("Solo el Servicio de Alimentación puede exportar las comidas autorizadas.")`.
- Orden de chequeo: primero se parsea la query (400) y después el permiso del CSV (403), igual que `api_resumenes`. Un comedor con una query inválida recibe 400, como en el JSON.
- `CABECERA_DETALLE = ("Fecha", "Apellido y nombre", "DNI", "Sector", "Desayuno", "Almuerzo", "Merienda", "Cena", "Tipo de menú")`. Las columnas de comida siguen el orden de `MEAL_TYPES`; si `MEAL_TYPES` tuviera otro orden, manda el enunciado (desayuno, almuerzo, merienda, cena).
- `_filas_matriz` agrega al final la fila `Total`.
- Si el `match` no cubre un caso → `TypeError` (no hay `case _` silencioso).

#### `core/seeds.py`

- `AUTORIZACION_PERMISSIONS += ["autorizacion_export"]` (se asigna solo a `administrador` y `alimentacion`, como el resto de la lista). Los pedidos de demo no cambian.

### 5.2 `admin/docs/openapi.yaml`

- `/authorized-meals` `GET`:
  - parámetros nuevos `fecha_desde`, `fecha_hasta`, `tipo_menu_id`, `condicion` (enum `regular`, `excepcion` y `generada`), `formato` (enum `json` y `csv`) y `agrupar` (enum `persona` y `sector`, solo con csv);
  - en la descripción: solo pedidos autorizados, `fecha` excluyente con el rango, rango ≤ 62 días, y que con csv el rango es obligatorio y no hay paginación;
  - respuesta 200 con dos `content`: `application/json` (sin cambios) y `text/csv` (descripción de las columnas de cada `agrupar`, `;`, BOM);
  - errores 400 (`validation_error` y `business_rule` en `fecha_hasta`), 401 y 403 (`x-permission: comida_index`; con csv además `autorizacion_export`).
- No se toca `/summaries/*`.

### 5.3 Tests

Archivo nuevo **`tests/unit/test_api_exportaciones.py`**:
- Importa `client`, `data`, `login_as`, `crear` y `autorizar` de `test_api_pedidos`, y si hace falta, `generar` y la fixture `residentes` de `test_api_almuerzos_residentes` (S9).
- Fixture `autorizados`:
  - como el Jefe, crea dos pedidos de Quirófano (hoy y mañana) con Gomez, Fernandez y Perez, y uno de Pediatría para hoy;
  - autoriza en parte: por ejemplo, a Perez no se le autoriza el desayuno de hoy;
  - deja un pedido **confirmado** y otro **rechazado** con solicitudes, y crea un apellido con acento (por ejemplo, "Ibáñez") y uno que empiece con `=` (para la inyección de fórmulas);
  - hace login como `alimentacion`.
- Helpers:
  - `csv_de(response)`: verifica `response.data.startswith(b"\xef\xbb\xbf")`, decodifica con `utf-8-sig` y devuelve `list(csv.reader(..., delimiter=";"))`;
  - `exportar(client, **query)`.

| Test | Esperado |
|---|---|
| `test_detalle_columnas_y_filas` | 200, `text/csv`, `Content-Disposition` con `comidas-autorizadas_<desde>_<hasta>.csv`. La cabecera es exacta. Filas exactas: una por fecha + persona + sector, `Sí` y vacíos correctos (Perez sin desayuno hoy), `"Apellido, Nombre"`, menú (Fernandez `Vegetariano`). Orden: fecha, sector y apellido |
| `test_detalle_acentos_y_bom` | Empieza con BOM. "Ibáñez" se lee bien con `utf-8-sig`. Las líneas terminan en `\r\n` |
| `test_detalle_dos_sectores_mismo_dia` | Una persona con comidas autorizadas en Quirófano y Pediatría el mismo día → dos filas |
| `test_detalle_menus_distintos_en_una_fila` | Una persona con almuerzo `General` y cena `Celiaco` el mismo día y sector → `Tipo de menú == "General / Celiaco"` (orden por id) |
| `test_solo_autorizadas` | No aparecen las solicitudes `autorizada=False` ni `None`, ni las de los pedidos confirmado o rechazado (aunque tengan `autorizada=True` en la base) |
| `test_filtros_csv` | Parametrizado: `sector_id`, `tipo_comida` (solo se llena esa columna; quien no la tiene no aparece), `tipo_menu_id`, `condicion=excepcion`, `dni` y `nombre` → filas esperadas |
| `test_matriz_por_sector` | `agrupar=sector`: cabecera `Sector`, `Desayuno General` … `Cena <último menú>`, `Total` (comidas × menús de la tabla). Filas por sector con cantidades exactas, `Total` por fila y fila `Total` con las sumas. Un sector sin autorizadas no aparece |
| `test_matriz_respeta_filtros` | Con `sector_id` → una fila de sector + Total. Con `tipo_comida=cena` → solo las columnas de cena con valores (el resto en 0) |
| `test_residentes_en_ambos_csv` | Después de `generar` (S9): en el detalle, filas con `Sector == "Residentes"` y `Almuerzo == "Sí"`; en la matriz, la fila `Residentes` con los almuerzos por menú. `condicion=generada` en el detalle → solo esas filas |
| `test_sin_resultados` | Rango sin autorizadas → detalle con la cabecera sola; matriz con la cabecera y `Total` en 0; ambos 200 |
| `test_inyeccion_de_formulas` | La celda del apellido que empieza con `=` sale con `'` adelante |
| `test_csv_sin_rango_400` | Parametrizado: `formato=csv` solo con `fecha`; con `fecha_desde` sin `fecha_hasta` → 400 `validation_error` con los campos que faltan |
| `test_rango_invalido_400` | Parametrizado: `hasta < desde`; 63 días → 400 con `fecha_hasta`. **62 días → 200** (caso límite) |
| `test_formato_invalido_400` | `formato=xml` → 400 `validation_error` en `formato` |
| `test_agrupar_invalido_400` | `agrupar=menu` con csv → 400; `agrupar=sector` sin csv → 400 en `agrupar` |
| `test_fecha_y_rango_excluyentes_400` | `fecha` + `fecha_desde` + `fecha_hasta` → 400 en `fecha` |
| `test_estado_no_es_filtro_400` | `estado=autorizado` → 400 (campo desconocido): documenta la decisión de la sección 2 |
| `test_permisos_csv` | Parametrizado con `comedor` y `jefe` → 403 en csv. Sin token → 401. `comedor` con la misma query sin `formato` → 200 (JSON intacto) |
| `test_paginacion_ignorada_en_csv` | Más de 25 comidas autorizadas y `per_page=5&page=2` con csv → están todas |

Agregados al final de **`tests/unit/test_api_entregas.py`** (sin modificar los casos existentes):
- `test_authorized_meals_rango_json`: `fecha_desde`/`fecha_hasta` devuelve las de los dos días, con la misma forma de ítem que hoy;
- `test_authorized_meals_menu_y_condicion_json`: `tipo_menu_id` y `condicion` filtran en JSON;
- `test_authorized_meals_rango_cuenta_como_filtro`: solo el rango → 200 (no el 400 de "ningún filtro").

Si algún test existente falla, **se corrige el código**, no el test: los cambios son aditivos y no deberían romper ninguno.

## 6. Portal (`code/portal`)

### 6.1 Archivos

```
src/modules/autorizacion/
├── views/BandejaAutorizacionView.vue          (modificado) botones y diálogo de exportación
└── exportacion/                               (nuevo)
    ├── exportacion.js                         funciones puras
    ├── exportacion.test.js
    ├── services/exportacionService.js         GET /api/authorized-meals?formato=csv y descarga
    └── components/DialogoExportar.vue
```

No se tocan `router/`, `api/`, `components/` globales, `filtros.js`, `TotalesPeriodo.vue`, ni los módulos `carga-pedidos/`, `reportes/` y `entrega/`. No se agregan dependencias.

### 6.2 `exportacion/exportacion.js` (puro)

```js
export const MAX_DIAS_EXPORTACION = 62

export const EXPORTACIONES = [
  { valor: 'detalle', etiqueta: 'Detalle por persona', boton: 'Exportar detalle', agrupar: 'persona', archivo: 'comidas-autorizadas' },
  { valor: 'sector', etiqueta: 'Totales por sector', boton: 'Exportar totales por sector', agrupar: 'sector', archivo: 'totales-por-sector' },
]

/**
 * null si se puede exportar con estos filtros de la bandeja; si no, el motivo (texto).
 * estado 'confirmado' o 'rechazado' -> "Solo se exportan comidas autorizadas: quitá el filtro de estado o elegí Autorizado."
 */
export function motivoSinExportar(filtros) {}

/**
 * { desde: string, hasta: string } (texto de los inputs) ->
 *   { tipo: 'ok', rango: { desde, hasta } }
 * | { tipo: 'errores', errores: { desde?: string, hasta?: string } }
 * Reglas (las del servidor, que es quien decide): las dos obligatorias y fechas válidas
 * (parseFechaISO); hasta >= desde; como máximo MAX_DIAS_EXPORTACION días. Se permiten fechas pasadas.
 */
export function parseRangoExportacion(textos) {}

/**
 * parámetros de GET /api/authorized-meals para exportar. Los null los omite el cliente HTTP.
 * -> { formato: 'csv', agrupar, fecha_desde, fecha_hasta, sector_id, tipo_comida, tipo_menu_id, condicion }
 * No incluye estado, vista, fecha, page ni totales.
 */
export function paramsDeExportacion(filtros, rango, tipo) {}

/** 'comidas-autorizadas_2026-10-13_2026-10-28.csv': el mismo nombre que arma el servidor */
export function nombreArchivo(tipo, rango) {}

/**
 * textos de los filtros que se aplican, para mostrarlos en el diálogo:
 * ["Sector: Intendencia", "Menú: Celiaco"] o [] (sin filtros: "Todos los sectores, comidas, menús y condiciones")
 */
export function filtrosAplicados(filtros, { sectores, menus }) {}
```

- `tipo` es el `valor` de `EXPORTACIONES`. Con uno desconocido, `Error`.
- `exportacion.test.js` cubre:
  - `motivoSinExportar` con cada estado;
  - `parseRangoExportacion`: válido, vacíos, `hasta < desde`, 62 días (ok), 63 (error), fecha pasada (ok) y texto inválido;
  - `paramsDeExportacion`: mapea sector, comida, menú y condición, omite `estado`, y `agrupar` según el tipo;
  - `nombreArchivo` para los dos tipos;
  - `filtrosAplicados` con y sin filtros, y con un sector que no está en la lista (muestra el id).

### 6.3 `exportacion/services/exportacionService.js`

```js
/**
 * GET /api/authorized-meals con paramsDeExportacion(...) y responseType 'blob'.
 * Si la respuesta no es un Blob -> Error ("Respuesta inválida del servidor al exportar").
 * @returns {Promise<Blob>}
 */
export async function exportarCsv({ tipo, filtros, rango }) {}

/** descarga el blob con un <a download> temporal y URL.createObjectURL; revoca la URL al terminar */
export function guardarArchivo(blob, nombre) {}
```

### 6.4 `exportacion/components/DialogoExportar.vue`

Mismo patrón que `DialogoRechazo.vue`: `<dialog>` con `showModal()`, foco atrapado, Escape cierra (salvo mientras descarga) y el foco vuelve al botón que lo abrió.

- **Props:**
  - `abierto`;
  - `tipo` (`EXPORTACIONES[i]`);
  - `periodo` (`{ desde, hasta }`, la precarga);
  - `filtros` (`string[]`, de `filtrosAplicados`);
  - `enviando`;
  - `erroresServidor` (`{ desde?, hasta?, general? }`).
- **Emite:** `confirmar(rango)` y `cancelar`.
- **Contenido:**
  - `<h2>` "Exportar <etiqueta>";
  - un párrafo con lo que incluye: "Comidas autorizadas de los pedidos autorizados." y la lista de `filtros`, o "Todos los sectores, comidas, menús y condiciones";
  - `<fieldset>` con `<legend>` "Período" y dos `<input type="date">` Desde y Hasta, con `<label>`, `aria-invalid` y `aria-describedby` hacia el error y hacia una ayuda ("Hasta 62 días.");
  - al abrir, los inputs se cargan con `periodo` y el foco va a Desde.
- **"Descargar"** (submit): `parseRangoExportacion`. Si hay errores, se marcan y el foco va al primer campo con error. Si no, `emit('confirmar', rango)`. Mientras `enviando`, los botones están deshabilitados y el botón dice "Descargando…".
- Los `erroresServidor` se muestran en sus campos y `general` va arriba del formulario, con `role="alert"`.

### 6.5 `views/BandejaAutorizacionView.vue`

- En `.bandeja__acciones`, un grupo `role="group"` con `aria-label="Exportar CSV"` y un botón secundario por cada `EXPORTACIONES`: "Exportar detalle" y "Exportar totales por sector".
- `const motivo = computed(() => motivoSinExportar(filtros.value))`. Si hay motivo, los botones quedan `disabled` con `aria-describedby` hacia un `<p class="field__hint">` con el motivo.
- Estado local `exportacion = reactive({ tipo: null, enviando: false, errores: {} })`. El diálogo se abre con `tipo` y `periodo` (el `periodo` computado que ya existe).
- `confirmar(rango)`:
  1. `enviando = true` y `errores = {}`;
  2. `const blob = await exportarCsv({ tipo, filtros: filtros.value, rango })`;
  3. `guardarArchivo(blob, nombreArchivo(tipo, rango))`;
  4. toast `success` "Se descargó <nombre>" y se cierra el diálogo;
  5. errores:
     - `ApiError` `VALIDATION` con `fields.fecha_desde` o `fields.fecha_hasta` → errores en esos campos;
     - otros `fields` (por ejemplo, `sector_id` o `tipo_menu_id`, si la URL tenía un valor raro) → `general` con su mensaje;
     - sin `fields` → `general` con `error.message`;
     - el diálogo queda abierto;
     - 401, 403 y 503 los maneja el cliente HTTP;
     - cualquier otro error se propaga: toast `error` "No se pudo exportar: <mensaje>" y el diálogo queda abierto;
  6. `finally`: `enviando = false`.
- Los botones de exportar no dependen de que la bandeja tenga datos: el rango del diálogo puede ser otro.

## 7. Verificación

### 7.1 Automática

```sh
cd code/admin && poetry run pytest          # nuevos en verde; las fallas previas de SQLite siguen igual (ver reportes S1 a S9)
cd code/portal && npm run lint && npm run build && npm test
```

### 7.2 Manual desde el portal

**Hay un permiso nuevo:** hacen falta los seeds de nuevo. **No** se resetea la base del usuario: se levantan instancias propias en otros puertos con una base temporal, como en S3 y S6 a S9, y se apagan al terminar. En el reporte se avisa que la base del usuario necesita volver a correr los seeds (o `reset-db` + seeds).

- **Usuarios:** `alimentacion@test.com` / `alim1234`, `comedor@test.com` / `comedor1234` y `jefe.medicos@test.com` / `jefe1234`.
- **Datos:** sección S10 de `seeds-per-slice.md`:
  - rango D-7 a D+8, que cubre los 13 pedidos;
  - autorizadas: pedidos 1, 2, 6, 7 y 12;
  - 25400003 sin desayuno en D;
  - Intendencia en D → solo el pedido 6;
  - Celiaco en D → Lopez (25555666) y Cabrera (26600003);
  - apellidos con acento: Álvarez, Domínguez, Giménez, Ibáñez, Juárez, Ríos y Suárez.
- **Antes del paso 8,** generar almuerzos de residentes para D a D+1 desde `/residentes` (S9).
- **Para revisar cada archivo:**
  - con Python: `python3 -c "import csv,sys; print(*csv.reader(open(sys.argv[1], encoding='utf-8-sig'), delimiter=';'), sep='\n')" <archivo>`;
  - `head -c 3 <archivo> | xxd` (BOM `efbbbf`);
  - si está `soffice`: `soffice --headless --convert-to xlsx <archivo>`, y revisar que haya columnas y acentos.

| # | Acción | Esperado |
|---|---|---|
| 1 | Entrar como Alimentación a `/autorizaciones` | Barra de acciones con "Exportar detalle" y "Exportar totales por sector", habilitados |
| 2 | Filtro Estado → Confirmado, después Rechazado; volver a Autorizado y a Todos | Con Confirmado o Rechazado: los dos botones deshabilitados, con el motivo visible. Con Autorizado o Todos: habilitados |
| 3 | En D (vista día), "Exportar detalle" | Diálogo con Desde = Hasta = D, "Todos los sectores…". Foco en Desde |
| 4 | Validación en el cliente: Hasta < Desde; 63 días; Desde vacío | Errores por campo, **sin** peticiones (pestaña Red) |
| 5 | Rango D-7 a D+8, "Descargar" | Una petición `GET /api/authorized-meals?formato=csv&agrupar=persona&fecha_desde=…&fecha_hasta=…` → 200 `text/csv`. Toast "Se descargó comidas-autorizadas_<D-7>_<D+8>.csv". El archivo tiene BOM, `;`, la cabecera exacta, las personas de los pedidos 1, 2, 6, 7 y 12, y 25400003 en D sin desayuno. **Criterio "Listo cuando":** los acentos se ven bien al leerlo con `utf-8-sig` (y en LibreOffice, si está) |
| 6 | Filtro Sector = Intendencia, vista día en D, "Exportar detalle" con D a D | El diálogo muestra "Sector: Intendencia". Solo las personas del pedido 6 |
| 7 | Quitar el sector, filtro Menú = Celiaco, exportar detalle de D | Solo Lopez (25555666) y Cabrera (26600003) |
| 8 | Sin filtros, vista semana, "Exportar totales por sector" | Diálogo precargado con lunes a domingo. El archivo `totales-por-sector_…csv` tiene la cabecera `Sector;Desayuno General;…;Total`, una fila por sector con autorizadas (incluida **Residentes**, con sus almuerzos), la fila `Total`, y las sumas cierran. Anotar las cantidades en el reporte |
| 9 | Filtro Condición = Generada, exportar detalle de D a D+1 | Solo filas del sector Residentes, con `Almuerzo = Sí` |
| 10 | Cruce: totales por sector de D (sin filtros) contra la bandeja de D | La suma de la fila de cada sector coincide con la suma de "autorizadas" de sus pedidos en la tabla de la bandeja |
| 11 | Errores del servidor con `curl` y el token de Alimentación: `formato=csv` sin rango; `formato=xml`; 63 días; `fecha` + rango; `estado=autorizado`; `agrupar=sector` sin csv | 400 en todos, con el campo correspondiente |
| 12 | `curl` con el token de `comedor@test.com`: la misma consulta en JSON y en CSV | JSON 200 (igual que antes); CSV 403 |
| 13 | Como `jefe.medicos@test.com` | Sin acceso a `/autorizaciones` (como antes); `curl` CSV → 403 |
| 14 | Regresión: Entrega (`/entrega`, búsqueda por DNI y por fecha como Comedor), Reportes (pantalla y su CSV, sin cambios), bandeja, detalle, Cupos y Residentes | Igual que antes |
| 15 | Accesibilidad (inspección o lector) | `role="group"` con `aria-label` en los botones, motivo asociado con `aria-describedby`, `<dialog>` con `aria-labelledby`, `legend`/`label`, `aria-invalid`, foco al primer error, `role="alert"` en el error general y foco devuelto al cerrar |

### 7.3 Criterio de terminado

- 7.1 y 7.2 pasan completos, incluidos los pasos de navegador. Los pasos omitidos se anotan en el reporte con su motivo. Abrir el CSV en **Excel** queda como verificación del usuario si no hay Excel en la máquina: se anota en el reporte.
- La rama está mergeada en local a `feature/autorizacion-pedidos` y **no** está pusheada.
- `seeds-per-slice.md`, sección S10, actualizada con:
  - los números reales de los pasos 5 a 9 (cantidad de filas del detalle y la matriz de D-7 a D+8);
  - la nota de que S10 agrega el permiso `autorizacion_export` y que hay que volver a correr los seeds;
  - en la tabla de usuarios o permisos, si figura, `autorizacion_export`.
- Está escrito el reporte `documentacion-personal/reporte-s10-exportaciones-csv.md` con la plantilla del artefacto:
  - Encabezado.
  - Alcance: los dos listados desde `authorized-meals` y lo que queda afuera (sección 3).
  - Cambios:
    - "base de datos: sin cambios; permiso nuevo `autorizacion_export` (requiere volver a correr los seeds)";
    - los archivos de otros integrantes que se tocaron (`core/entrega.py`, `web/schemas/entregas.py`, `web/controlers/api_comidas.py` y `openapi.yaml`), por qué, y que los cambios son aditivos;
    - los tests agregados.
  - Prueba manual con el resultado de cada paso.
  - **Pendientes y decisiones**, que tiene que incluir sí o sí:
    - **"Solo pedidos autorizados en `authorized-meals` (sin filtro de estado)"**, con la cita del enunciado ("Reglas de negocio: solo deben aparecer pedidos en estado autorizado") y la consecuencia en la bandeja (botones deshabilitados con Confirmado o Rechazado);
    - **"El listado de totales sale de `authorized-meals?agrupar=sector` y no de `/summaries/authorizations`"**, con el motivo: `summaries` agrupa por una dimensión; cambiarlo tocaría Reportes y su JSON; el endpoint es sugerido; y los dos listados quedan coherentes;
    - **"Sin CSV del panel Totales del período"**: no es un listado del enunciado de Autorización, y la nota del reporte de S7 queda descartada;
    - el resto de las decisiones que surjan durante la sesión.

## 8. Pendientes para otras slices

- **Reportes:** el CSV de `/summaries/*` usa `,` como separador. Excel con configuración regional es-AR lo abre en una sola columna. Sugerirle a quien mantiene Reportes pasarlo a `;` (es una línea en `api_resumenes._csv`), o reusar `web/csv_export.respuesta_csv`.
- **Entrega:** los filtros nuevos de `authorized-meals` (rango, menú y condición) están disponibles para la vista de Comedor si los quiere usar.
- **S11:**
  - el diálogo de exportación y la barra de acciones en móvil;
  - "Ampliaciones de la API": `formato=csv` y `agrupar` en `GET /api/authorized-meals`, los filtros nuevos y el permiso `autorizacion_export`;
  - el recorrido guionado termina con "exportar" (usar el paso 8 de 7.2).
