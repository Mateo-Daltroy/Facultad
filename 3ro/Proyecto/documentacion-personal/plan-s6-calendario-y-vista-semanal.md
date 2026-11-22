# Plan S6: Calendario y vista semanal (Módulo de Autorización)

> **BORRADOR v1 (19/11/2026).** Decisiones cerradas. Los estilos del calendario y de la matriz semanal se ajustan visualmente durante la implementación.
>
> Plan de implementación de la sexta slice del "Plan de desarrollo — Módulo de Autorización" (artefacto). Es la primera slice después del circuito mínimo S1–S5.
>
> Referencias:
> - Slices anteriores: `documentacion-personal/plan-s1-bandeja-diaria.md` y `reporte-s1-bandeja-diaria.md` (la bandeja que se extiende), y `plan-s5-correccion-y-dias-anteriores.md` y su reporte (slice anterior).
> - Datos de demo: `documentacion-personal/seeds-per-slice.md` (sección S6).
> - Enunciado, `Etapa_1y2_Trabajo Integrador_2026_PUBLIC.md`:
>   - Módulo de Autorización: "Consultar pedidos mediante vistas diarias y semanales, con navegación entre períodos" y "Consultar únicamente los períodos que tengan pedidos registrados, por ejemplo marcando en el calendario los días con datos".
>   - Ampliaciones de la API: "GET /api/authorization-dates — fechas con pedidos registrados".
>
> **Precondición:** S5 está terminada y mergeada en `feature/autorizacion-pedidos` (con su reporte escrito). Este plan usa estos nombres del código de S1 a S5:
>
> - Backend:
>   - `core/pedido.py`: `list_pedidos(sector_id, fecha_desde, fecha_hasta, tipo_comida, tipo_menu_id, condicion, estado, page, per_page)` (filtra por **superposición** de rango y pagina con máximo 25), `validate_pedido_duplicado`, `condiciones_validas` y `estados_validos`.
>   - `web/schemas/pedidos.py`: `PedidoQuerySchema` (con `validate_rango`) y `totals`.
>   - `web/schemas/autorizaciones.py`: `AutorizacionQuerySchema` y `AutorizacionOutputSchema` (`dump_autorizados`).
>   - `web/controlers/api_autorizaciones.py`: `api_autorizaciones_bp` (prefijo `/authorizations`), `index` y `_get_or_404`.
>   - `core/seeds.py`: `PERMISSIONS`, `ROLES` y `AUTORIZACION_PERMISSIONS`.
>   - Tests: el fixture `data` de `tests/unit/test_api_pedidos.py` arma los roles desde `seeds.ROLES` (un permiso nuevo en los seeds llega solo a los tests). En `tests/unit/test_api_autorizaciones.py`: `crear`, `autorizar`, `set_estado`, `login_as`, `fijar_hoy`, `DIA` y las parametrizaciones `test_without_authentication_returns_401` y `test_only_alimentacion_can_authorize`.
> - Portal, `src/modules/autorizacion/`:
>   - `filtros.js`: `parseFiltros`, `aQuery`, `hoyISO`, `sumarDiasISO`, `parseFechaISO`, `formatearFechaLarga`, `ESTADOS`, `COMIDAS` y `POR_PAGINA`.
>   - `services/autorizacionService.js`: `listarAutorizaciones`, `aPedidoResumen`, `aTotales`, `aFecha`, `respuestaInvalida`, `esEntero`, `esObjeto` y `esUnoDe`.
>   - `components/PeriodoNav.vue`, `FiltrosAutorizacion.vue` y `TablaPedidos.vue`; `views/BandejaAutorizacionView.vue`.
>   - Vitest (`npm test`) con `seleccion.test.js` como ejemplo.
>
> Si algo de esto no coincide con el código, **manda el código**: se adapta el plan sin renombrar lo existente. Si S5 no está mergeada, **no se empieza**: se avisa al usuario.

## 1. Objetivo

Hoy (S1–S5) la bandeja muestra un día. Se navega de a un día con "← Día anterior" / "Día siguiente →", aunque esté vacío, o con un `<input type="date">` libre. No hay vista semanal ni forma de saber qué días tienen pedidos.

Al terminar S6, el usuario de Alimentación:

- Ve en la bandeja un **selector Día / Semana** (`vista` en la URL; `dia` es el default y no se escribe).
- En la **vista diaria**:
  - Los botones pasan a **"← Anterior con pedidos"** y **"Siguiente con pedidos →"**. Saltan al día con pedidos más cercano (respetando los filtros de sector y estado), sin límite de distancia. Se deshabilitan si no hay ninguno.
  - "Hoy" sigue igual.
  - El `<input type="date">` se reemplaza por un **botón con la fecha larga** ("jueves 19 de noviembre de 2026") que abre el calendario en un popover.
- En la **vista semanal**:
  - Ve una **matriz sector × día** de lunes a domingo, la semana que contiene `fecha`.
  - Cada celda tiene el badge de estado y "autorizadas / pedidas", o solo "pedidas" si está pendiente, y enlaza al detalle. Las celdas sin pedido muestran "—".
  - El encabezado de cada día lleva a la vista diaria de ese día.
  - Las flechas pasan a **"← Semana anterior con pedidos"** y **"Semana siguiente con pedidos →"**.
  - Los filtros (sector, estado y condición) y el botón "Revisar pendientes" siguen funcionando. No hay paginación.
- En el **calendario** (`CalendarioDias`):
  - Ve el mes en una grilla de lunes a domingo, con ‹ › para cambiar de mes.
  - Los días **sin pedidos están deshabilitados**.
  - Cada día con pedidos tiene hasta **tres puntos de color** (pendiente, autorizado, rechazado), y su `aria-label` y `title` dicen "19 de noviembre: 2 pendientes, 2 autorizados, 1 rechazado". Un día con pendientes se resalta.
  - El día elegido (`fecha`) y hoy se distinguen visualmente.
  - Al elegir un día se cierra el popover y la bandeja va a ese día. En la vista semanal, va a la semana de ese día.
- **Las marcas del calendario y la navegación "con pedidos" respetan los filtros de sector y estado.** La condición no se aplica, como en el enunciado: con `condicion=excepcion`, "Siguiente con pedidos" puede llevar a un día que la bandeja muestra vacío. Es aceptable y se documenta.

En la API:

- `GET /api/authorization-dates`: el endpoint del enunciado.
- `GET /api/authorization-dates/adjacent` (nuevo, acordado con el usuario): la fecha con pedidos anterior y la siguiente a un período, en una sola petición.
- `GET /api/authorizations/week` (nuevo, acordado con el usuario): todo lo necesario para dibujar la semana de una fecha, sin paginación.
- Permiso nuevo `autorizacion_dates` para el rol `alimentacion` (y `administrador`).

**Criterio "Listo cuando" del artefacto:** "en un mes con tres días cargados, la navegación recorre solo esos tres".

Requisitos del enunciado que cubre:

- "Consultar pedidos mediante vistas diarias y semanales, con navegación entre períodos."
- "Consultar únicamente los períodos que tengan pedidos registrados, por ejemplo marcando en el calendario los días con datos."
- `GET /api/authorization-dates`: "Entrada: rango de fechas y, opcionalmente, sector y estado. Salida: listado de las fechas del rango que tienen pedidos, con la cantidad de pedidos pendientes, autorizados y rechazados de cada una. […] Errores: 400 si falta el rango de fechas o es inválido; 403 si lo invoca un usuario sin rol Servicio de Alimentación."

## 2. Decisiones

| Tema | Decisión | Motivo |
|---|---|---|
| Calendario | **Componente propio** `CalendarioDias.vue` con la lógica de la grilla en funciones puras (`calendario.js`). **No** se usa el `DatePicker` de PrimeVue | Decisión acordada. Control total de las marcas y de la carga por mes, sin adaptarse a la API y al tema de PrimeVue |
| Ubicación del calendario | **Popover** que se abre desde el botón con la fecha en `PeriodoNav`. Escape y clic afuera lo cierran y devuelven el foco al botón | Decisión acordada. La bandeja conserva su ancho |
| Marcas por día | Hasta 3 puntos de color por estado. Los conteos van en `aria-label` y `title`. Un día con pendientes se resalta | Decisión acordada |
| Días sin pedidos | Botón `disabled` en la grilla. "Hoy" (botón de `PeriodoNav`) siempre lleva a hoy aunque esté vacío | El enunciado pide consultar solo los períodos con datos. "Hoy" es el punto de partida natural y mostrar su estado vacío es útil |
| Filtros que afectan marcas y navegación | `sector_id` y `estado`. **No** `condicion` | Decisión acordada, igual que el enunciado |
| `GET /api/authorization-dates` | `fecha_desde` y `fecha_hasta` obligatorias, `hasta ≥ desde`, a lo sumo **62 días** (inclusive), `sector_id?` y `estado?`. Devuelve `{ items: [{ fecha, pendientes, autorizados, rechazados }] }` solo con los días que tienen al menos un pedido, en orden ascendente | Enunciado y artefacto. 62 días alcanzan para la grilla de un mes (42) con margen |
| Navegación "con pedidos" | Endpoint nuevo `GET /api/authorization-dates/adjacent?fecha_desde&fecha_hasta[&sector_id&estado]` → `{ anterior: fecha \| null, siguiente: fecha \| null }`. `anterior` es la última fecha con pedidos **antes** de `fecha_desde` y `siguiente` la primera **después** de `fecha_hasta`. **Sin tope de distancia.** Vista diaria: `desde = hasta = fecha`. Vista semanal: lunes y domingo | Decisión acordada (parámetro extra en la API, en vez de ventanas en el cliente). Una sola petición para los dos botones, y en la semana saltea las semanas vacías |
| Ruta de `adjacent` | Sub-recurso de `/authorization-dates`, en el mismo blueprint | Mantiene el contrato del enunciado intacto y agrupa lo de fechas |
| Pedidos de varios días | Un pedido cuenta en **cada día de su rango** `fecha_desde..fecha_hasta`, recortado al rango consultado. Lo mismo en `adjacent` y en la semana | Decisión acordada. Coincide con la bandeja, que filtra por superposición: si el calendario marca un día, la bandeja muestra el pedido. Cuando Carga pase a "un pedido por fecha", el caso deja de existir sin cambiar el código |
| Cálculo de los conteos | Una consulta trae `(id, estado, fecha_desde, fecha_hasta)` de los pedidos que se superponen con el rango y cumplen los filtros. La expansión por día y el conteo se hacen en Python | Portable entre SQLite y PostgreSQL, sin `generate_series`. El rango está acotado a 62 días |
| Cálculo de `adjacent` | Dos consultas portables por lado, sin `least`/`greatest` (ver 5.2) | `least` no existe en SQLite con ese nombre |
| Vista semanal: datos | Endpoint nuevo `GET /api/authorizations/week?fecha[&sector_id&estado&condicion]`, con la forma acordada (ver 5.4). Permiso `autorizacion_index` (reusado: es la misma bandeja en otra vista) | Decisión acordada. `GET /authorizations` pagina con un máximo de 25 y una semana puede tener más. La API devuelve la semana lista para dibujar |
| Inicio de semana | **Siempre lunes.** Para cualquier `fecha`, la semana es lunes–domingo de la semana que la contiene (miércoles 18 → 16 a 22; domingo 22 → 16 a 22) | Decisión acordada |
| Una celda, un pedido | Un sector tiene a lo sumo un pedido por día (`validate_pedido_duplicado` impide superposiciones). Si `semana_de` encontrara dos para la misma celda, lanza `RuntimeError` (invariante rota), **no** pisa uno con otro | No ocultar datos inconsistentes |
| `fecha` en la vista semanal | La URL conserva la `fecha` tal cual (no se normaliza al lunes). La semana se deriva | Al volver a "Día" se vuelve al mismo día |
| Parseo del rango | `RangoFechas` (`@dataclass(frozen=True)` en `core`) con un constructor `RangoFechas.parse(desde, hasta, max_dias)` que lanza `BusinessRuleError(..., field=...)`. El schema lo arma en `post_load`. `core` nunca recibe fechas sueltas sin validar | "Parse, don't validate" |
| Refactor de `list_pedidos` | Los filtros pasan a `consulta_pedidos(...) -> Select` en `core/pedido.py`. `list_pedidos` la usa y la semana la reusa. **Requisito duro: todo el código que usa `list_pedidos` sigue funcionando exactamente igual**: misma firma, mismos defaults, mismos resultados, mismo orden, misma paginación y mismas excepciones con los mismos mensajes. Antes de mover código se escriben tests de caracterización de los tres usos (ver 5.1); si el refactor no se puede hacer sin cambiar el comportamiento, **no se hace** y la semana arma su propia consulta | Un solo lugar para los filtros. `list_pedidos` es compartida con Carga, con la API del Jefe y con la aplicación privada |
| Estado vacío | Vista diaria: "No hay pedidos para esta fecha y filtros" (como hoy). Vista semanal: "No hay pedidos en esta semana con estos filtros" | — |
| "Revisar pendientes" | Se mantiene en las dos vistas | Decisión acordada |
| Esquema de base | **Sin cambios.** Hace falta `reset-db` + `seeds` en la base de demo porque `seed_auth` no agrega permisos nuevos a un rol que ya existe (ver 5.6) | — |
| Rama | `feat/autorizacion-s6` sale de `feature/autorizacion-pedidos` (con S5 mergeada) y vuelve con merge local `--no-ff` | Igual que S1 a S5 |

## 3. Fuera de alcance

No hacer en S6 (cada ítem tiene su slice o queda como pendiente):

- Navegación con flechas del teclado dentro de la grilla del calendario y de la matriz semanal (S11). Alcanza con Tab entre los días habilitados, Enter y Escape.
- Vista móvil específica de la matriz semanal (S11). Alcanza con scroll horizontal dentro de `.table-wrap`.
- Filtros de comida y menú, y el panel de totales (S7).
- Vista mensual de la bandeja (el enunciado pide diaria y semanal; el calendario mensual es solo un selector).
- Marcar en el calendario los pedidos corregibles o con entregas.
- Totales por día en la vista semanal (S7).
- Cambiar `GET /api/authorizations` (la vista diaria sigue igual, con paginación).
- Tests de componentes Vue (`@vue/test-utils`). Solo funciones puras.

## 4. Git

```sh
cd code
git switch feature/autorizacion-pedidos
git log --oneline -5          # confirmar que el merge de feat/autorizacion-s5 está
git status                    # árbol limpio antes de empezar
git switch -c feat/autorizacion-s6
```

- Commits chicos por capa, por ejemplo:
  - `test: caracterización de list_pedidos` (antes de tocar `core/pedido.py`)
  - `refactor(core): consulta_pedidos con los filtros de list_pedidos`
  - `feat(core): fechas con pedidos, adyacentes y semana de autorizaciones`
  - `feat(api): authorization-dates, adjacent y authorizations/week`
  - `test: calendario, navegación con pedidos y vista semanal`
  - `feat(portal): calendario de días con pedidos`
  - `feat(portal): navegación con pedidos y vista semanal`
- Al terminar: `git switch feature/autorizacion-pedidos && git merge --no-ff feat/autorizacion-s6`.
- **No pushear `feat/autorizacion-s6` al origin.** `feature/autorizacion-pedidos` se pushea solo si lo pide el usuario.

## 5. Backend (`code/admin`)

### 5.1 `src/core/pedido.py`: `consulta_pedidos`

```python
def consulta_pedidos(sector_id: int | None = None,
                     fecha_desde: date | None = None,
                     fecha_hasta: date | None = None,
                     tipo_comida: str | None = None,
                     tipo_menu_id: int | None = None,
                     condicion: str | None = None,
                     estado: str | None = None) -> Select:
    """select(Pedido) con los filtros de la bandeja, sin orden ni paginación.
    el rango trae los pedidos que se superponen con él"""
```

**Requisito duro:** todo el código que hoy usa `list_pedidos` tiene que seguir funcionando **exactamente igual**. El refactor es puramente interno. Usos actuales (en `feature/autorizacion-pedidos`; volver a buscarlos con `grep -rn "list_pedidos" admin/src` por si S5 u otra rama agregó alguno):

| Uso | Archivo | Quién lo usa |
|---|---|---|
| `GET /api/requests` (vía `list_pedidos_checked`) | `web/controlers/api_pedidos.py` | Jefe de Servicio desde el portal (Carga), con `sector_scope` |
| `GET /api/authorizations` (vía `list_pedidos_checked`) | `web/controlers/api_autorizaciones.py` | La bandeja de Alimentación |
| Listado de pedidos de la aplicación privada (Jinja) | `web/controlers/pedido.py` (`list_pedidos(..., per_page=25)`) | Aplicación privada |

Lo que **no** puede cambiar:

- La **firma** de `list_pedidos`: nombres, orden y defaults de los parámetros (`page=1`, `per_page=25`) y el tipo de retorno (`Pagination`).
- El **resultado** para cualquier combinación de filtros: mismos pedidos, mismo **orden** (`fecha_desde` desc, estado con pendientes primero, `id` desc), mismo tope `per_page` entre 1 y 25 y mismo `total`.
- Las **excepciones**: los mismos `ValueError` con los mismos mensajes ("Condicion invalida", "Estado invalido" y lo que lance `validate_tipo_comida`), en el mismo orden de evaluación. `list_pedidos_checked` los traduce a 400 y la aplicación privada los muestra con `flash`: un mensaje distinto es un cambio visible.
- La **normalización** de los valores (`strip().lower()` de `condicion` y `estado`, `validate_tipo_comida`): se mueve tal cual a `consulta_pedidos`.
- `web/services/pedidos.py`, `api_pedidos.py` y `pedido.py` (controlador de la aplicación privada) **no se tocan**.

Procedimiento, en este orden:

1. **Tests de caracterización antes del refactor**, en un commit propio (`test: caracterización de list_pedidos`), en verde sobre el código **sin tocar**:
   - **`core`** (`tests/unit/test_list_pedidos.py`, nuevo), directamente sobre `list_pedidos`, con pedidos de varios sectores, fechas (incluido uno de varios días), estados, comidas, menús y condiciones:
     - cada filtro solo y combinaciones, en particular comida + menú + condición sobre **la misma** solicitud (un pedido con almuerzo general y cena vegetariana **no** sale con `tipo_comida=almuerzo&tipo_menu_id=vegetariano`);
     - superposición de rango (pedido de varios días que empieza antes o termina después del rango);
     - el orden exacto de los ids devueltos;
     - paginación: `page` 2, `per_page` 0, 1 y 100 (queda en 25) y `total`;
     - valores con espacios y mayúsculas (`" Confirmado "`) y los `ValueError` con su mensaje exacto para condición, estado y comida inválidos.
   - **Aplicación privada:** un test del listado de pedidos (`GET` de la ruta de `pedido.index` con un usuario jefe logueado en la sesión de Flask, siguiendo el patrón de `test_sector_controller.py` o `test_personal_controller.py`). Comprobar que responde 200 con los pedidos del sector del usuario y que un filtro inválido hace `flash` del mensaje y redirige. Hoy no hay test de este controlador. Si armar la sesión resultara muy costoso, documentarlo en el reporte y cubrir esa ruta en la prueba manual (7.2, paso 0).
   - **APIs:** los tests de `GET /api/requests` (`test_index_*` de `test_api_pedidos.py`) y de `GET /api/authorizations` ya existen. Revisar que cubran orden y filtros inválidos, y sumar lo que falte.
2. **Refactor** en otro commit (`refactor(core): consulta_pedidos con los filtros de list_pedidos`): se mueve el bloque de filtros **sin reescribirlo** (mismas condiciones, mismo orden de validación, mismos mensajes). `list_pedidos` queda como `consulta_pedidos(...)` + el mismo `order_by` + el mismo clamp de `per_page` + `paginate`.
3. Correr `tests/unit/test_list_pedidos.py`, `test_api_pedidos.py`, `test_api_autorizaciones.py` y el test de la aplicación privada **sin modificar ninguno**. Si alguno necesita cambios para pasar, el refactor cambió comportamiento: se corrige el refactor, no el test.
4. Si no se logra un comportamiento idéntico, se revierte el refactor y `semana_de` / `fechas_con_pedidos` / `fechas_adyacentes` arman su propia consulta. Duplicar los filtros es preferible a cambiar `list_pedidos`. Anotarlo en el reporte.

`consulta_pedidos` es nueva, así que su orden de parámetros no tiene compatibilidad que respetar. Igual conviene usar los mismos nombres que `list_pedidos`.

### 5.2 `src/core/autorizacion_fechas.py` (nuevo)

```python
MAX_DIAS_RANGO = 62

@dataclass(frozen=True)
class RangoFechas:
    desde: date
    hasta: date

    @staticmethod
    def parse(desde: date, hasta: date, max_dias: int | None = MAX_DIAS_RANGO) -> "RangoFechas":
        """hasta >= desde y, si max_dias no es None, a lo sumo max_dias días contando los dos extremos.
        BusinessRuleError(field="fecha_hasta") si no"""

    def dias(self) -> Iterator[date]: ...


@dataclass(frozen=True)
class ConteoDia:
    fecha: date
    pendientes: int        # estado confirmado
    autorizados: int
    rechazados: int


@dataclass(frozen=True)
class Adyacentes:
    anterior: date | None
    siguiente: date | None


def fechas_con_pedidos(rango: RangoFechas, sector_id: int | None,
                       estado: str | None) -> list[ConteoDia]:
    """días del rango con al menos un pedido, ascendente. un pedido de varios días
    cuenta en cada día de su rango que cae dentro de `rango`"""


def fechas_adyacentes(periodo: RangoFechas, sector_id: int | None,
                      estado: str | None) -> Adyacentes:
    """última fecha con pedidos antes de periodo.desde y primera después de periodo.hasta,
    sin límite de distancia"""
```

**`fechas_con_pedidos`:**

- Consulta `select(Pedido.id, Pedido.estado, Pedido.fecha_desde, Pedido.fecha_hasta)` sobre `consulta_pedidos(sector_id, rango.desde, rango.hasta, estado=estado)`. Solo se piden las columnas, no se cargan las solicitudes.
- Expandir cada pedido a `max(fecha_desde, rango.desde) .. min(fecha_hasta, rango.hasta)` y contar por `(día, estado)` con un `Counter`.
- El mapeo de estado a campo es explícito: `confirmado → pendientes`, `autorizado → autorizados`, `rechazado → rechazados`. Un estado desconocido lanza `ValueError` (no se descarta en silencio).
- `estado` se valida igual que en `list_pedidos` (lo hace `consulta_pedidos`).

**`fechas_adyacentes`** (F1 = `periodo.desde`, F2 = `periodo.hasta`; todas las consultas aplican los filtros de sector y estado):

- `anterior`:
  1. Si existe un pedido con `fecha_desde < F1 <= fecha_hasta` (empezó antes y sigue en F1), el resultado es `F1 - 1 día`.
  2. Si no, `max(fecha_hasta)` de los pedidos con `fecha_hasta < F1` (o `None`).
- `siguiente`, simétrico:
  1. Si existe un pedido con `fecha_desde <= F2 < fecha_hasta`, el resultado es `F2 + 1 día`.
  2. Si no, `min(fecha_desde)` de los pedidos con `fecha_desde > F2` (o `None`).
- Usar `func.max` / `func.min` y `exists()` sobre `consulta_pedidos(...)`, con `.subquery()` o `with_only_columns` según convenga. Nada de `least`/`greatest`.
- Explicar en el docstring por qué el paso 1 es correcto: un pedido que se superpone con F1−1 y empezó antes de F1 cubre F1−1.
- `periodo` en `adjacent` se parsea **sin** tope de días (`max_dias=None`); solo exige `hasta >= desde`.

### 5.3 `src/core/autorizacion_fechas.py`: semana

```python
@dataclass(frozen=True)
class CeldaSemana:
    pedido_id: int
    estado: str
    pedidas: int               # solicitudes del pedido en ese día
    autorizadas: int | None    # None si está confirmado (pendiente)


@dataclass(frozen=True)
class FilaSemana:
    sector_id: int
    sector: str
    celdas: Mapping[date, CeldaSemana]   # solo los días con pedido; MappingProxyType


@dataclass(frozen=True)
class Semana:
    desde: date                # lunes
    hasta: date                # domingo
    filas: tuple[FilaSemana, ...]       # ordenadas por nombre de sector

    @property
    def dias(self) -> tuple[date, ...]: ...   # los 7 días, lunes a domingo


def inicio_de_semana(fecha: date) -> date:
    """lunes de la semana de fecha (fecha - fecha.weekday())"""


def semana_de(fecha: date, sector_id: int | None, estado: str | None,
              condicion: str | None) -> Semana:
    """pedidos de la semana lunes-domingo que contiene fecha, por sector y día"""
```

- La consulta usa `consulta_pedidos(sector_id, lunes, domingo, condicion=condicion, estado=estado)` con `selectinload(Pedido.solicitudes)` y `selectinload(Pedido.sector)`, para evitar el N+1.
- Un pedido de varios días aparece en cada día de su rango dentro de la semana. `pedidas` y `autorizadas` de esa celda cuentan **solo las solicitudes de ese día** (`s.fecha == dia`). En un pedido diario son todas.
- `autorizadas`: `None` si `estado == "confirmado"`; si no, la cantidad de solicitudes del día con `autorizada is True`. Mismo criterio que `AutorizacionOutputSchema.dump_autorizados`.
- Si dos pedidos caen en la misma `(sector, día)`, lanzar `RuntimeError` (ver Decisiones).
- Los sectores sin pedidos esa semana **no** aparecen.

### 5.4 Schemas: `src/web/schemas/autorizaciones.py` (o un `autorizacion_fechas.py` nuevo si queda más claro)

```python
class FechasQuerySchema(Schema):
    class Meta:
        unknown = RAISE
    fecha_desde = fields.Date(required=True)
    fecha_hasta = fields.Date(required=True)
    sector_id = fields.Integer(validate=validate.Range(min=1))
    estado = fields.String(validate=validate.OneOf(sorted(estados_validos)))
    # post_load: {"rango": RangoFechas.parse(desde, hasta), "sector_id": ..., "estado": ...}

class AdyacentesQuerySchema(FechasQuerySchema):
    # post_load con RangoFechas.parse(desde, hasta, max_dias=None)

class SemanaQuerySchema(Schema):
    class Meta:
        unknown = RAISE
    fecha = fields.Date(required=True)
    sector_id = fields.Integer(validate=validate.Range(min=1))
    estado = fields.String(validate=validate.OneOf(sorted(estados_validos)))
    condicion = fields.String(validate=validate.OneOf(sorted(condiciones_validas)))
```

- El `BusinessRuleError` de `RangoFechas.parse` sale como 400 con `fields.fecha_hasta`. Si el manejador de errores no mapea un `BusinessRuleError` lanzado dentro de `post_load`, convertirlo ahí en `ValidationError` con el mismo mensaje. Lo importante es el 400 con el campo. Mensajes:
  - "No puede ser anterior a fecha_desde." (el mismo de `PedidoQuerySchema`);
  - "El rango no puede superar los 62 días."
- Fechas mal formadas o faltantes: los 400 estándar de marshmallow.

Salidas (funciones `dump_*` o schemas; el formato de fecha es `AAAA-MM-DD`):

```json
// GET /api/authorization-dates
{ "items": [ { "fecha": "2026-11-19", "pendientes": 2, "autorizados": 2, "rechazados": 1 } ] }

// GET /api/authorization-dates/adjacent
{ "anterior": "2026-11-18", "siguiente": "2026-11-20" }

// GET /api/authorizations/week?fecha=2026-11-19
{
  "desde": "2026-11-16",
  "hasta": "2026-11-22",
  "dias": ["2026-11-16", "2026-11-17", "...", "2026-11-22"],
  "sectores": [
    { "id": 5, "nombre": "Hemoterapia",
      "celdas": {
        "2026-11-18": { "pedido_id": 3, "estado": "confirmado", "pedidas": 8, "autorizadas": null },
        "2026-11-19": { "pedido_id": 8, "estado": "rechazado",  "pedidas": 5, "autorizadas": 0 }
      } }
  ]
}
```

### 5.5 Controladores

- **Nuevo** `src/web/controlers/api_autorizacion_fechas.py` con `api_autorizacion_fechas_bp = Blueprint(..., url_prefix="/authorization-dates")`, registrado donde se registran los demás blueprints de la API (buscar el registro de `api_autorizaciones_bp`):
  ```python
  @bp.get("")
  @permission_required("autorizacion_dates")
  def index(): ...            # fechas_con_pedidos -> {"items": [...]}

  @bp.get("/adjacent")
  @permission_required("autorizacion_dates")
  def adjacent(): ...         # fechas_adyacentes -> {"anterior", "siguiente"}
  ```
- En `api_autorizaciones.py`:
  ```python
  @api_autorizaciones_bp.get("/week")
  @permission_required("autorizacion_index")
  def week(): ...             # semana_de -> dump
  ```
  - `/week` no choca con `/<int:request_id>` (no es entero). Verificarlo con un test: `GET /api/authorizations/week` sin `fecha` da 400, no 404.

### 5.6 `src/core/seeds.py`

- `PERMISSIONS += ["autorizacion_dates"]`. Se agrega a `ROLES["alimentacion"]` y a `ROLES["administrador"]`. El administrador además recibe todos por `grant_permissions`; agregarlo explícito igual, como el resto.
- Los pedidos de demo **no cambian**.
- `seed_auth` **no** agrega permisos a un rol que ya existe ("Si el rol ya existe, se respetan los cambios hechos desde la pantalla de Roles y permisos"). En la base de demo hay que correr `reset-db` + `seeds`, o asignar el permiso desde la pantalla de Roles y permisos. Anotarlo en el reporte y en la sección de despliegue de S11.

### 5.7 `admin/docs/openapi.yaml`

- `GET /authorization-dates`: parámetros, 200 con `AuthorizationDates`, 400 (rango faltante, invertido o de más de 62 días, fecha inválida, estado inválido), 401 y 403. Describir que un pedido de varios días cuenta en cada día y que solo vienen los días con pedidos.
- `GET /authorization-dates/adjacent`: parámetros, 200 con `AdjacentDates` (`anterior` y `siguiente` nullable), 400, 401 y 403. Describir "sin límite de distancia" y el uso desde la vista diaria y la semanal.
- `GET /authorizations/week`: parámetros, 200 con `AuthorizationWeek`, 400, 401 y 403. Describir la regla del lunes y el recorte por día de un pedido de varios días.
- Schemas nuevos: `AuthorizationDates`, `AuthorizationDateCount`, `AdjacentDates`, `AuthorizationWeek`, `AuthorizationWeekSector` y `AuthorizationWeekCell`.

### 5.8 Tests

Archivo nuevo `tests/unit/test_api_autorizacion_fechas.py`, que reusa el fixture `data` y los helpers `crear`, `autorizar`, `set_estado` y `login_as` (importarlos o moverlos a un helper compartido si ya se importan así en otros tests; seguir el patrón existente). Los pedidos se crean en fechas fijas (`DIA` y relativas) con `fijar_hoy` donde haga falta para pasar el horario de corte de Carga.

Helpers sugeridos:

```python
def get_fechas(client, **params): ...      # 200 -> items
def get_adyacentes(client, **params): ...  # 200 -> dict
def get_semana(client, **params): ...      # 200 -> dict
```

| Test | Preparación | Esperado |
|---|---|---|
| `test_fechas_sin_rango_400` | Sin `fecha_desde`, sin `fecha_hasta`, sin ninguna | 400 con el campo faltante |
| `test_fechas_rango_invertido_400` | `hasta < desde` | 400 `fields.fecha_hasta` |
| `test_fechas_rango_63_dias_400` / `test_fechas_rango_62_dias_ok` | 63 y 62 días contando los dos extremos | 400 / 200 |
| `test_fechas_fecha_invalida_400` | `fecha_desde=2026-02-30` | 400 |
| `test_fechas_conteos_por_estado` | En un día, un pedido confirmado, uno autorizado y uno rechazado (sectores distintos); otro día con uno solo | `items` ascendente con los conteos exactos; los días sin pedidos no aparecen |
| `test_fechas_filtro_sector_y_estado` | Igual | Con `sector_id`, solo los días de ese sector; con `estado=autorizado`, solo cuenta autorizados |
| `test_fechas_estado_invalido_400` | `estado=foo` | 400 |
| `test_fechas_pedido_de_varios_dias` | Un pedido semanal de 3 días (si Carga todavía lo permite; si no, crearlo por `core` o ajustar `fecha_hasta` en la base) | Cuenta en cada uno de los 3 días, y recortado si el rango corta el pedido |
| `test_adyacentes_saltea_dias_vacios` | Pedidos en D-5, D y D+4 | Para `desde=hasta=D`: `anterior=D-5`, `siguiente=D+4` |
| `test_adyacentes_sin_mas_pedidos` | Un solo pedido en D | `anterior` y `siguiente` en `null` |
| `test_adyacentes_semana` | Pedidos en D-10, en la semana de D y en D+12; período lunes–domingo de la semana de D | Las fechas fuera de la semana, nunca una de adentro |
| `test_adyacentes_pedido_largo_en_el_borde` | Pedido de D-2 a D+2; período `desde=hasta=D` | `anterior=D-1`, `siguiente=D+1` |
| `test_adyacentes_sin_tope_de_distancia` | Pedido en D+200 | `siguiente=D+200` |
| `test_adyacentes_respeta_filtros` | Pedidos de dos sectores y estados | Con `sector_id` o `estado`, solo los que cumplen |
| `test_semana_lunes_a_domingo` | `fecha` un miércoles y `fecha` un domingo de la misma semana | Mismo `desde` (lunes) y `hasta` (domingo); `dias` con 7 fechas |
| `test_semana_celdas` | Pedidos de dos sectores en días distintos de la semana, uno autorizado parcial y uno pendiente; un pedido fuera de la semana | Sectores ordenados por nombre; celdas con `pedido_id`, `estado`, `pedidas` y `autorizadas` correctos (`null` en el pendiente); el de afuera no aparece |
| `test_semana_filtros` | Igual + `estado`, `sector_id` y `condicion` | Filtran como en `GET /authorizations` |
| `test_semana_sin_fecha_400` | `GET /api/authorizations/week` | 400, no 404 |
| `test_semana_vacia` | Semana sin pedidos | 200 con `sectores == []` |
| `test_core_rango_fechas_parse` | Casos límite de `RangoFechas.parse` (igual, invertido, 62, 63 y `max_dias=None`) | — |
| `test_core_inicio_de_semana` | Lunes, miércoles y domingo | El lunes correspondiente |
| 401 / 403 | Sumar los 3 endpoints a las parametrizaciones de `test_without_authentication_returns_401` y `test_only_alimentacion_can_authorize` (o equivalentes nuevas) | 401 sin token; 403 para `jefe` y `comedor` |

Los tests de caracterización de `list_pedidos` y de la aplicación privada se describen en 5.1. Se escriben antes del refactor y no se modifican después.

## 6. Portal (`code/portal`)

### 6.1 Archivos

```
src/modules/autorizacion/
├── filtros.js                                   (modificado) vista en parseFiltros/aQuery, VISTAS, formateos
├── calendario.js                                (nuevo) funciones puras de semana y grilla del mes
├── calendario.test.js                           (nuevo)
├── filtros.test.js                              (nuevo, o sumar a uno existente) parseFiltros/aQuery con vista
├── services/autorizacionService.js              (modificado) fechasConPedidos, fechasAdyacentes, semanaDeAutorizaciones
├── components/CalendarioDias.vue                (nuevo) grilla del mes con marcas
├── components/SelectorVista.vue                 (nuevo) Día / Semana
├── components/TablaSemana.vue                   (nuevo) matriz sector × día
├── components/PeriodoNav.vue                    (modificado) navegación con pedidos + botón de fecha con popover
└── views/BandejaAutorizacionView.vue            (modificado) vista diaria o semanal
```

Igual que en S1 a S5:

- No se tocan `router/`, `constants/`, `api/`, `components/` globales ni otros módulos.
- No se crea `domain/`.
- No se agregan dependencias (`date-fns` ya está).

### 6.2 `filtros.js`

- `export const VISTAS = [{ valor: 'dia', etiqueta: 'Día' }, { valor: 'semana', etiqueta: 'Semana' }]`.
- `parseFiltros` suma `vista: unoDe(VISTAS, primero(query.vista)) ?? 'dia'`.
- `aQuery` escribe `vista` solo si es `'semana'`, para que la URL de la vista diaria quede igual que hoy.
- Formateos nuevos: `formatearMes(iso)` → "noviembre 2026", `formatearDiaCorto(iso)` → "mié 18", `formatearSemana(desde, hasta)` → "16 al 22 de noviembre de 2026" (con mes o año en los dos extremos si cambian: "30 de noviembre al 6 de diciembre de 2026").
- En la bandeja, cambiar `vista` **no** vuelve a la página 1 por sí solo: `actualizar` ya pone `page: 1` en cualquier cambio. La vista semanal ignora `page`.

### 6.3 `calendario.js` (puro, sin Vue)

```js
/** lunes de la semana de iso: "2026-11-19" -> "2026-11-16" (date-fns startOfWeek con weekStartsOn: 1) */
export function inicioDeSemana(iso) {}

/** los 7 días lunes..domingo de la semana de iso */
export function diasDeSemana(iso) {}

/** "2026-11-19" -> "2026-11" */
export function mesDe(iso) {}

/** "2026-11", 1 -> "2026-12" */
export function sumarMeses(mes, n) {}

/**
 * Grilla del mes: semanas de 7 celdas de lunes a domingo, desde la semana del día 1
 * hasta la del último día. Cada celda: { iso, delMes: boolean }.
 * Siempre entre 4 y 6 semanas.
 */
export function grillaDelMes(mes) {}

/** { desde, hasta } de la grilla completa (a lo sumo 42 días: entra en el tope de 62 de la API) */
export function rangoDeGrilla(mes) {}
```

- `mes` es un texto `AAAA-MM`. Tirar `Error` si no tiene ese formato (lo arma el propio código, así que un valor inválido es un bug).

`calendario.test.js`:
- `inicioDeSemana` para lunes, miércoles y domingo, y en un cambio de mes y de año.
- `grillaDelMes` de un mes que empieza en lunes, de uno que empieza en domingo (6 semanas) y de febrero de un año no bisiesto que empieza en lunes (4 semanas), con `delMes` correcto.
- `rangoDeGrilla` ≤ 42 días.
- `sumarMeses` cruzando el año.

`filtros.test.js` (o sumarlos donde convenga): `parseFiltros` con `vista` válida, inválida y ausente, y `aQuery` omite `vista=dia`.

### 6.4 `services/autorizacionService.js`

```js
/**
 * GET /api/authorization-dates.
 * @returns {Promise<Map<string, { pendientes: number, autorizados: number, rechazados: number }>>}
 */
export async function fechasConPedidos({ desde, hasta, sectorId, estado }) {}

/** GET /api/authorization-dates/adjacent -> { anterior: string|null, siguiente: string|null } */
export async function fechasAdyacentes({ desde, hasta, sectorId, estado }) {}

/**
 * GET /api/authorizations/week.
 * @returns {Promise<{ desde: string, hasta: string, dias: string[],
 *   sectores: { id: number, nombre: string,
 *               celdas: Map<string, { pedidoId: number, estado: string,
 *                                     pedidas: number, autorizadas: number|null }> }[] }>}
 */
export async function semanaDeAutorizaciones({ fecha, sectorId, estado, condicion }) {}
```

Parseo estricto, con `respuestaInvalida` si algo viene mal:

- **Fechas:**
  - `items` es un array, cada `fecha` pasa por `aFecha` y no hay fechas repetidas (por eso es un `Map`);
  - los conteos son enteros ≥ 0 y al menos uno > 0.
- **Adyacentes:**
  - `anterior` y `siguiente` son `null` o una fecha;
  - `anterior < desde` y `siguiente > hasta` (si no, es un bug de la API).
- **Semana:**
  - `dias` tiene exactamente 7 fechas consecutivas que empiezan en `desde` y terminan en `hasta`, y `desde` es lunes (`inicioDeSemana(desde) === desde`);
  - cada clave de `celdas` está en `dias`;
  - `estado` está en `ESTADOS`;
  - `autorizadas` es `null` si y solo si `estado === 'confirmado'`;
  - `autorizadas <= pedidas`.

### 6.5 `components/CalendarioDias.vue`

- Props:
  - `fecha` (string ISO, el día elegido);
  - `sectorId` (number | null);
  - `estado` (string | null).
- Emite `elegir(iso)`.
- Estado interno: `mes` (`ref(mesDe(props.fecha))`). Las flechas ‹ › con `aria-label` "Mes anterior" / "Mes siguiente" cambian `mes`.
- Carga: `useAsyncData(() => fechasConPedidos({ ...rangoDeGrilla(mes.value), sectorId, estado }))`, que se recarga con un `watch` sobre `[mes, sectorId, estado]` (`immediate: true`). Mientras carga se muestra la grilla con todos los días deshabilitados y un `LoadingState` chico, o un indicador `aria-busy`. Con error, `ErrorState` con reintentar.
- **Grilla:**
  - `<table role="grid">` (o `<div role="grid">`) con encabezados "lu ma mi ju vi sá do" (con `abbr` o `aria-label` del nombre completo);
  - cada día es un `<button type="button">`:
    - `disabled` si no hay pedidos ese día;
    - `aria-label` "jueves 19 de noviembre: 2 pendientes, 2 autorizados, 1 rechazado", omitiendo los conteos en 0, o "jueves 19 de noviembre: sin pedidos";
    - `aria-current="date"` para el día de hoy;
    - `aria-pressed="true"` (o `aria-selected`) para `fecha`;
  - los días fuera del mes se muestran atenuados y siguen siendo elegibles si tienen pedidos;
  - los puntos son `<span aria-hidden="true">` con las variables de color de los badges de estado (`--color-info`, `--color-success` y `--color-danger`, o las que usen `badge--info`, `badge--success` y `badge--danger`);
  - un día con `pendientes > 0` lleva una clase que lo resalta, con el mismo fondo que `fila--pendiente` de `TablaPedidos`.
- Al montarse, el foco va al botón del día elegido si está habilitado; si no, al primer día habilitado; y si no hay ninguno, a "Mes siguiente". Usar `nextTick`.
- Leyenda debajo: "● Pendiente ● Autorizado ● Rechazado".

### 6.6 `components/PeriodoNav.vue` (reescrito)

- Props: `fecha`, `vista`, `sectorId`, `estado`. Emite `cambiar(fecha)`.
- `periodo`:
  - vista diaria: `{ desde: fecha, hasta: fecha }`;
  - vista semanal: `{ desde: lunes, hasta: domingo }` con `diasDeSemana(fecha)`.
- `adyacentes`: `useAsyncData(() => fechasAdyacentes({ ...periodo, sectorId, estado }))`, que se recarga con un `watch` sobre `[periodo.desde, periodo.hasta, sectorId, estado]`.
  - Mientras carga o con error, los botones de navegación quedan deshabilitados. El error se muestra con un `title`/`aria-describedby` "No se pudo calcular la navegación", sin romper la bandeja, y se reintenta al cambiar el período.
- Botones:
  - "← Anterior con pedidos" / "← Semana anterior con pedidos": `disabled` si `anterior === null`; emite `cambiar(anterior)`;
  - "Siguiente con pedidos →" / "Semana siguiente con pedidos →": lo simétrico con `siguiente`;
  - "Hoy": igual que hoy (deshabilitado si ya es hoy).
  - En la vista semanal, "Hoy" está deshabilitado si hoy cae en la semana visible.
- **Botón de fecha** (reemplaza el `<input type="date">`):
  - texto: vista diaria `formatearFechaLarga(fecha)`; vista semanal "Semana del " + `formatearSemana(desde, hasta)`;
  - `aria-haspopup="dialog"`, `aria-expanded` y `aria-controls`;
  - abre un popover (`<div role="dialog" aria-label="Elegir fecha">` posicionado debajo, con las clases de `card` del design system) con `<CalendarioDias>`;
  - se cierra con Escape, con clic afuera y al elegir. Al cerrar, el foco vuelve al botón de fecha.
- `CalendarioDias` se monta solo con el popover abierto (`v-if`), así no se pide `/authorization-dates` hasta que se abre.
- `aria-label` del `<nav>`: "Navegación entre días" o "Navegación entre semanas".

### 6.7 `components/SelectorVista.vue`

- Props: `vista`. Emite `cambiar(vista)`.
- Un grupo (`role="group"`, `aria-label="Vista"`) con dos botones `btn--secondary` y `aria-pressed`; el activo, con estilo de botón activo.

### 6.8 `components/TablaSemana.vue`

- Props: `semana` (la que devuelve `semanaDeAutorizaciones`). Emite `elegirDia(iso)`.
- `<table class="table">` dentro de `.table-wrap`, con `caption` visualmente oculto "Pedidos de la semana del …".
- Encabezados:
  - "Sector" y un `<th scope="col">` por día con un botón-link (`formatearDiaCorto`, más la fecha completa en `aria-label`) que emite `elegirDia`;
  - la columna de hoy se marca (clase + `aria-current="date"` en el encabezado).
- Filas: `<th scope="row">` con el nombre del sector. Por cada día:
  - **con pedido:** un `RouterLink` a `autorizacion-detalle` con el badge (`datosDeEstado`) y "autorizadas / pedidas" (o solo "pedidas" si está pendiente), y `aria-label` "Pedido de {sector} del {fecha larga}: {estado}, …";
  - **sin pedido:** "—" con `visually-hidden` "Sin pedido".
- Las celdas pendientes llevan el fondo de `fila--pendiente`.

### 6.9 `views/BandejaAutorizacionView.vue`

- `filtros` ya trae `vista`.
- Encima de los filtros: `<SelectorVista :vista="filtros.vista" @cambiar="(vista) => actualizar({ vista })" />` y `<PeriodoNav :fecha :vista :sector-id :estado @cambiar="(fecha) => actualizar({ fecha })" />`.
- Datos:
  - **Vista diaria:** `listarAutorizaciones(filtros)` como hoy, con `TablaPedidos` y `Paginator`.
  - **Vista semanal:** `semanaDeAutorizaciones(filtros)` con `TablaSemana` y sin paginación. `@elegir-dia="(fecha) => actualizar({ fecha, vista: 'dia' })"`.
  - Un solo `useAsyncData` cuya función elige según `filtros.value.vista`, o dos separados (lo que quede más claro). El `watch(filtros, load)` se mantiene.
- Estado vacío:
  - vista diaria: el texto actual;
  - vista semanal: "No hay pedidos en esta semana con estos filtros".
  - Las dos con "Limpiar filtros" si `hayFiltros`.
- Debajo de la tabla de la vista semanal, la misma pista: "Cantidades: autorizadas / pedidas."
- "Revisar pendientes" / "Ver todos" se mantiene en las dos vistas.
- Actualizar el comentario de la vista: "Bandeja diaria o semanal…".

## 7. Verificación

### 7.1 Automática

```sh
cd code/admin && poetry run pytest          # nuevos en verde; las fallas previas de SQLite siguen igual (ver reportes S1 a S5)
cd code/portal && npm run lint && npm run build && npm test   # el módulo sin errores de lint; las fallas previas de otros módulos siguen igual
```

### 7.2 Manual desde el portal

Preparación (en `code/admin`). No hay cambio de esquema, pero el permiso nuevo solo llega a un rol nuevo, así que se regenera la base:

```sh
poetry run flask --app web reset-db
USER_NAME=admin@test.com USER_PASS=admin1234 poetry run flask --app web seeds
poetry run flask --app web run --debug
# en otra terminal: cd code/portal && npm run dev
```

Si hay un backend o un portal del usuario corriendo, levantar instancias propias en otros puertos y con una base temporal, como en S3, y apagarlas al terminar.

Usuarios: `alimentacion@test.com` / `alim1234`; `jefe.hemoterapia@test.com` / `jefe1234`; `comedor@test.com` / `comedor1234`.

Datos: sección S6 de `seeds-per-slice.md`. Días con pedidos: D-7, D-2, D-1, D, D+1, D+3 y D+8.

| # | Acción | Esperado |
|---|---|---|
| 0 | **Regresión del refactor.** Antes y después de `refactor(core): consulta_pedidos…`: (a) en el portal, como `jefe.hemoterapia`, el listado de pedidos de Carga con y sin filtros (fecha, estado); (b) en la aplicación privada, el listado de pedidos con un jefe, con y sin filtros, y con un filtro inválido; (c) en el portal, la bandeja de Alimentación (vista diaria) con sus filtros y la paginación | Mismos pedidos, mismo orden, mismos mensajes de error y misma paginación antes y después. Anotar en el reporte lo que se comparó |
| 1 | Login como Alimentación | La bandeja de hoy (D), vista "Día" marcada, URL sin `vista`. El botón de fecha dice la fecha larga de hoy. "Hoy" deshabilitado |
| 2 | "Siguiente con pedidos →" varias veces | D+1 → D+3 → D+8. En D+8 el botón queda deshabilitado. D+2 y D+4 a D+7 no aparecen nunca |
| 3 | "← Anterior con pedidos" desde D+8 hasta el final | D+3 → D+1 → D → D-1 → D-2 → D-7. En D-7 el botón queda deshabilitado |
| 4 | Filtro sector Hemoterapia y navegar desde D | Recorre solo D-1, D y D+3 (los del 3, 8 y 12). **Criterio "Listo cuando"** |
| 5 | Filtro estado Autorizado y navegar | Solo D-7, D-2, D y D+3 |
| 6 | Abrir el calendario desde el botón de fecha | Popover con el mes de D. Puntos en los días con pedidos, con los colores de la tabla de S6 de `seeds-per-slice.md` (D con los tres colores). D resaltado por tener pendientes. Los días vacíos deshabilitados. El foco en el día elegido |
| 7 | Pasar el mouse por D (o leerlo con lector de pantalla) | "…: 2 pendientes, 2 autorizados, 1 rechazado" |
| 8 | ‹ › para ir al mes anterior y al siguiente | Las marcas se cargan para cada mes. Si D+8 o D-7 caen en otro mes, aparecen ahí |
| 9 | Elegir D+3 en el calendario | Se cierra el popover, la bandeja muestra el 11 y el 12, y el foco vuelve al botón de fecha |
| 10 | Abrir el calendario y cerrarlo con Escape; abrirlo y hacer clic afuera | Se cierra sin cambiar de fecha, con el foco en el botón |
| 11 | Con el filtro de sector Hemoterapia, abrir el calendario | Solo D-1, D y D+3 habilitados |
| 12 | Cambiar a "Semana" en D | URL con `vista=semana`. Matriz lunes–domingo de la semana de D con los sectores que tienen pedidos, celdas con badge y cantidades, la columna de hoy marcada y las pendientes resaltadas |
| 13 | Clic en una celda | Abre el detalle de ese pedido. "Volver" regresa a la vista semanal con la misma fecha |
| 14 | Clic en el encabezado de un día | Vista diaria de ese día |
| 15 | En la vista semanal, "Semana siguiente con pedidos →" | Salta a la semana que contiene la próxima fecha con pedidos fuera de la semana actual, salteando las semanas vacías. Llega a la semana de D+8, donde aparece un solo pedido (el 13) |
| 16 | Volver a "Día" | Muestra el día de `fecha` (el que se eligió), no el lunes |
| 17 | Vista semanal con el filtro de condición Excepción y con "Revisar pendientes" | Filtran las celdas. Si la semana queda vacía, se muestra el estado vacío con "Limpiar filtros" |
| 18 | Cargar a mano `/autorizaciones?vista=semana&fecha=<un miércoles>` y `?fecha=<un domingo>` de la misma semana | La misma semana lunes–domingo |
| 19 | Cargar `/autorizaciones?vista=foo` | Vista diaria (el valor inválido toma el default) |
| 20 | `curl` `GET /api/authorization-dates` sin rango, con `hasta < desde` y con 63 días | 400 con el campo |
| 21 | `curl` `GET /api/authorization-dates?fecha_desde=D-7&fecha_hasta=D+8` | Los 7 días con los conteos de la tabla de S6 de `seeds-per-slice.md` |
| 22 | `curl` `GET /api/authorization-dates/adjacent?fecha_desde=D&fecha_hasta=D` | `{anterior: D-1, siguiente: D+1}`; con `fecha_desde=fecha_hasta=D+8`, `siguiente: null` |
| 23 | `curl` `GET /api/authorizations/week?fecha=D` y sin `fecha` | 200 con la forma de 5.4; 400 |
| 24 | `curl` los tres endpoints como `jefe.hemoterapia` y como `comedor`, y sin token | 403 y 401 |
| 25 | DevTools en "Slow 3G": abrir el calendario y navegar | Mientras carga, los días y los botones de navegación quedan deshabilitados y no hay saltos raros. Un error de red muestra el `ErrorState` del calendario sin romper la bandeja |

Para los `curl`, el token se obtiene con el login de la API (ver `reporte-s1-bandeja-diaria.md` o `openapi.yaml`).

### 7.3 Criterio de terminado

- 7.1 y 7.2 pasan completos, **incluidos los pasos de navegador**.
- La rama está mergeada en local a `feature/autorizacion-pedidos` y **no** está pusheada.
- `seeds-per-slice.md`, sección S6, está actualizada con:
  - los endpoints reales (`/authorization-dates`, `/adjacent` y `/authorizations/week`);
  - la secuencia con filtro (Hemoterapia: D-1, D, D+3);
  - el aviso de `reset-db` por el permiso nuevo.

  Verificar que la tabla de conteos por día siga siendo correcta después de S5 (S5 corrige el 12 pero no cambia su estado).
- Está escrito el reporte en `documentacion-personal/reporte-s6-calendario-y-vista-semanal.md` con la plantilla del artefacto:
  - Encabezado.
  - Alcance.
  - Cambios, con "base de datos: sin cambios de esquema; reset-db + seeds por el permiso nuevo".
  - Prueba manual con el resultado de cada paso.
  - Pendientes y decisiones.

## 8. Pendientes para otras slices

- **Coordinación con Carga:**
  - cuando `Pedido` pase a una sola fecha, `fechas_con_pedidos`, `fechas_adyacentes` y `semana_de` se simplifican (sin expansión por rango), pero no hace falta cambiarlas: siguen siendo correctas con `fecha_desde == fecha_hasta`;
  - el refactor `consulta_pedidos` toca `core/pedido.py`: avisar al responsable de Carga y señalarle los tests de caracterización de `list_pedidos`, que fijan su comportamiento.
- **S7:** totales por día o por semana en la vista semanal, y los filtros de comida y menú (que se suman a `semana_de` vía `consulta_pedidos`).
- **S10:** la exportación CSV con la vista semanal (rango lunes–domingo).
- **S11:**
  - navegación con flechas dentro del calendario y de la matriz semanal;
  - matriz semanal en móvil (lista por día);
  - la sección "Ampliaciones de la API" con `/authorization-dates/adjacent` y `/authorizations/week`;
  - el permiso `autorizacion_dates` en el servidor de la cátedra (rol existente: asignarlo desde Roles y permisos o con un script).
- **Opcional:** aplicar `condicion` a las marcas y a la navegación si el uso real muestra que confunde caer en días vacíos.
