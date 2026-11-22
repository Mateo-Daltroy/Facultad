# Plan S2: Detalle del pedido en solo lectura (Módulo de Autorización)

> **BORRADOR v2 (19/11/2026).** Decisiones cerradas. La matriz y la tabla de totales se ajustan visualmente durante la implementación.
>
> Plan de implementación de la segunda slice del "Plan de desarrollo — Módulo de Autorización" (artefacto).
>
> Referencias: `documentacion-personal/plan-s1-bandeja-diaria.md` y `reporte-s1-bandeja-diaria.md` (slice anterior), `documentacion-personal/seeds-per-slice.md` (sección S2), `documentacion-personal/resumen_portal_y_core.md` y `Etapa_1y2_Trabajo Integrador_2026_PUBLIC.md` (Módulo de Autorización y Ampliaciones de la API).

## 1. Objetivo

Hoy la bandeja (S1) lista los pedidos del día, pero no se puede abrir ninguno. `GET /api/authorizations/{id}` existe, pero devuelve el mismo detalle que ve el Jefe (`PedidoDetailSchema`): sin la fecha de última modificación, sin acciones, sin revisor y sin saber qué comidas se entregaron.

Al terminar S2, el usuario de Alimentación:

- Hace clic en un pedido de la bandeja y abre **`/autorizaciones/pedidos/:id`**.
- Ve una **matriz persona × comida** (una por fecha del pedido) con el **menú** y la **condición** (Regular o Excepción) de cada solicitud.
- Si el pedido ya fue revisado, ve **lo autorizado aparte de lo pedido**: en cada celda y en una tabla de totales por comida y menú.
- Ve **quién revisó y cuándo**, el **motivo** si fue rechazado, qué solicitudes ya se **entregaron** y la **fecha y hora de última modificación**.
- Ve los botones **Autorizar**, **Rechazar** o **Corregir autorización** que corresponden al pedido, o el **aviso** de por qué no se puede hacer nada. En S2 los botones son clickeables pero **no hacen nada**: el comportamiento llega en S3, S4 y S5.
- Vuelve a la bandeja **con la misma fecha y filtros**.

En la API queda lista la base de S3 a S5: `Pedido.modificado_en` (la versión para el 409) y `acciones_permitidas()` con las reglas finales de Etapa 2, probadas pero todavía no usadas como guarda.

**Inconsistencias transitorias aceptadas.** Mientras no estén todas las slices, la API y la vista pueden mostrar cosas que todavía no funcionan (por ejemplo, `corregir: true` en el pedido 6 aunque el PUT responda 409 hasta S5, o un botón que no hace nada). Se acepta siempre que el estado final, con todas las slices terminadas, sea consistente. Cada slice escribe directamente la versión final de lo que toca.

Requisitos del enunciado que cubre: "mostrar el detalle del pedido por persona y comida, con el tipo de menú y la condición Regular o Excepción de cada solicitud", "visualizar las cantidades pedidas y autorizadas por separado", el `GET /api/authorizations/{id}` de Etapa 1 ("detalle completo discriminado por comida y tipo de menú, con cantidades pedidas y autorizadas por separado, y las personas incluidas") y, de las Ampliaciones, "el detalle de un pedido incluye la fecha y hora de su última modificación".

## 2. Decisiones

| Tema | Decisión | Motivo |
|---|---|---|
| Pedidos de varios días: "día anterior" | Un pedido es de un período anterior si **`fecha_desde < hoy`**. Aislado en `acciones_permitidas` | Criterio conservador acordado: si ya empezó, queda en solo lectura. Cuando Carga pase a "un pedido por fecha", `fecha_desde` es la fecha única y la regla no cambia |
| Pedidos de varios días: matriz | **Una matriz por fecha**, en orden. Con un solo día (todos los seeds) se ve una sola, sin título de fecha repetido | No se asume `fecha_desde == fecha_hasta`, que Carga todavía no garantiza |
| `core/reloj.py` | Entra en **S2** (venía de S1) | `acciones_permitidas` necesita "hoy" y `modificado_en` necesita "ahora" |
| `Pedido.modificado_en` | Columna nueva `DateTime(timezone=True)`, `NOT NULL`. La asignan **explícitamente** `create_pedido`, `update_pedido`, `autorizar_pedido` y `rechazar_pedido` con `reloj.ahora()` | `updated_at` no se mueve cuando el Jefe cambia solo personas (ver 5.2) |
| Migración | **Solo `reset-db` + `seeds`**. No se crea `admin/migrations/` | Acordado. El servidor se resetea al desplegar |
| Volver a la bandeja | Link "← Volver a la bandeja". Si la entrada anterior del historial es la bandeja, hace **`router.back()`**; si no (link directo, recarga), va a la bandeja **del día del pedido** | Acordado. La URL del detalle queda limpia |
| `acciones_permitidas` | Implementa las **reglas finales de Etapa 2**, incluida "corregir sin entregas". Ver 2.1 | Se escribe y se prueba una sola vez; S3 a S5 solo la usan como guarda |
| Botones en el detalle | **Autorizar**, **Rechazar** y **Corregir autorización** se ven según `acciones`, son clickeables y **no hacen nada** hasta su slice. Si el pedido está bloqueado, se ve el aviso de `motivo_bloqueo` en lugar de los botones | La vista queda con su forma final; cada slice solo conecta su botón |
| Estado desconocido en `acciones_permitidas` | `ValueError` | No hay `else` silencioso que lo trate como autorizado |
| `motivo_rechazo` en el portal | El parser **exige** un motivo no vacío en todo pedido rechazado | Es la regla final (S4). Los seeds lo cumplen; un rechazado de Etapa 1 sin motivo se ve como respuesta inválida, y se acepta |
| Forma de `acciones` en el dominio | Unión de dos tipos: `Disponibles` (qué se puede hacer) o `Bloqueado` (por qué no se puede hacer nada). En el JSON se aplana a `{ autorizar, corregir, rechazar, motivo_bloqueo }` | Un pedido bloqueado con alguna acción en `true` no se puede escribir |
| `acciones_permitidas` es pura | Recibe `hay_entregas: bool`; no consulta la base | Se prueba sin base de datos. El controlador ya necesita las entregas para `entregada` por solicitud |
| Schema del detalle | `GET /authorizations/{id}` pasa a un detalle propio (`dump_detalle_autorizacion`). `modificado_en` se agrega a `PedidoDetailSchema`, así también lo ve el Jefe. `acciones`, `revision` y `entregada` van **solo** en el de Autorización | El enunciado pide `modificado_en` "en el detalle de un pedido"; lo demás es del módulo |
| PUT y reject | **Sin cambios** de comportamiento. Siguen devolviendo `PedidoDetailSchema` (que ahora trae `modificado_en`) | S3 y S4 los reescriben |
| Parsers del portal | En `services/autorizacionService.js`, como en S1 (no se crea `domain/mappers.js`) | Consistencia con S1 |
| Orden de las personas en la matriz | Orden de incorporación al pedido (id de su primera solicitud) | Explica por qué los últimos de cada comida son excepción. Se revisa visualmente |
| Menú en la matriz | En cada celda, como pide el enunciado ("por persona y comida, con el tipo de menú") | En la práctica es el mismo en toda la fila (se copia de la ficha al cargar), pero el modelo lo guarda por solicitud. Se revisa visualmente |
| Rama | `feat/autorizacion-s2` sale de `feature/autorizacion-pedidos` (`49406da`, con S1 mergeada) y vuelve con merge local `--no-ff` | Igual que S1 |

### 2.1 Qué es `acciones_permitidas`

`acciones_permitidas(pedido, hoy, hay_entregas)` es la **única** función que decide qué se puede hacer con un pedido: autorizarlo, corregirlo, rechazarlo, o nada (y por qué). Tiene dos usos:

1. **Como salida.** El GET de detalle la devuelve en `acciones`, y el portal muestra los botones según eso, sin repetir las reglas en Vue.
2. **Como guarda.** El PUT y el POST reject la llaman antes de escribir y responden 409 con el motivo si la acción no está habilitada.

En S2 se implementa con las reglas finales y se usa solo para la salida. La guarda se conecta en S3 (PUT de autorización), S4 (reject) y S5 (corrección).

En el portal, S2 deja la vista con su forma final: los botones que habilita `acciones` se ven y se pueden clickear, pero el handler está vacío. Cuando `motivo_bloqueo` no es `null`, en lugar de botones se ve el aviso: "Período anterior: solo lectura" (pedidos 1, 2 y 3), "Pedido rechazado" (8) o "Ya tiene comidas entregadas: la autorización no se puede corregir" (7).

## 3. Fuera de alcance

No hacer en S2 (cada ítem tiene su slice):

- Checkboxes, selección, lo que hace el botón "Autorizar", totales "a autorizar" en vivo y el 409 por versión (S3).
- Lo que hacen los botones "Rechazar" (S4) y "Corregir autorización" (S5).
- Quitar la doble llamada a `get_pedido_para_revisar` y usar `acciones_permitidas` como guarda en el PUT (S3) y en el reject (S4).
- Rechazo con motivo, `CHECK` del motivo y atajos masivos (S4).
- Corrección, historial (`autorizacion_cambios`) y `GET /history` (S5).
- Cambiar `autorizado_en` y `revisado_en` a `reloj.ahora()`: siguen con `datetime.now()` hasta que S3 reescriba `autorizar_pedido` (ver 5.2).
- Pasar Carga a "un pedido por fecha" (es del Módulo de Carga).
- Responsive fino de la matriz (lista por persona en móvil) y navegación por teclado dentro de la matriz (S11). En S2 la matriz va en `.table-wrap` con scroll horizontal.

## 4. Git

```sh
cd code
git switch feature/autorizacion-pedidos
git switch -c feat/autorizacion-s2
```

- Commits chicos por capa, por ejemplo: `feat(core): reloj y Pedido.modificado_en`, `feat(core): acciones_permitidas`, `feat(api): detalle de autorización con acciones, revisión y entregas`, `test: ...`, `feat(portal): detalle del pedido en solo lectura`.
- Al terminar: `git switch feature/autorizacion-pedidos && git merge --no-ff feat/autorizacion-s2`.
- **No pushear `feat/autorizacion-s2` al origin.** `feature/autorizacion-pedidos` se pushea solo si lo pide el usuario.

## 5. Backend (`code/admin`)

### 5.1 `src/core/reloj.py` (nuevo)

```python
"""reloj del servidor. todas las reglas de fecha lo usan; los tests lo reemplazan con monkeypatch"""
from datetime import date, datetime
from zoneinfo import ZoneInfo

ZONA = ZoneInfo("America/Argentina/Buenos_Aires")


def ahora() -> datetime:
    """fecha y hora actual, con zona"""
    return datetime.now(ZONA)


def hoy() -> date:
    """fecha actual en Buenos Aires, aunque el servidor esté en UTC"""
    return ahora().date()
```

- Los módulos lo importan como **módulo** (`from . import reloj` y después `reloj.ahora()`), no con `from .reloj import ahora`. Así un `monkeypatch.setattr(reloj, "hoy", ...)` en un test alcanza a todos.
- No se reemplazan los `date.today()` / `datetime.now()` que ya existen en otros módulos (entregas, comidas, seeds). Fuera de alcance.

### 5.2 `src/core/pedido.py`: `Pedido.modificado_en`

```python
modificado_en: Mapped[datetime] = mapped_column(
    DateTime(timezone=True), nullable=False, default=lambda: reloj.ahora()
)
```

- **Por qué no `updated_at`:** `onupdate` solo corre si cambia la fila de `pedidos`. Cuando el Jefe cambia solo las personas, `update_pedido` reasigna `periodicidad`, `fecha_desde` y `fecha_hasta` con los mismos valores (SQLAlchemy no emite `UPDATE` si el valor no cambió) y regenera las solicitudes, así que `updated_at` queda igual. Es justo el caso del 409 del enunciado ("el Jefe de Servicio agregó o quitó personas").
- **Asignación explícita** con `reloj.ahora()` en:
  - `create_pedido` (después de construir el `Pedido`).
  - `update_pedido` (junto con las fechas).
  - `autorizacion._cerrar_revision`, que usan `autorizar_pedido` y `rechazar_pedido`. Se asigna `pedido.modificado_en = reloj.ahora()` **aparte** de `revisado_en`, que sigue con el `datetime.now()` actual (ver Fuera de alcance: mezclar un valor con zona en una columna sin zona es un cambio que conviene hacer junto con S3).
- El `default` de la columna es solo una red para los `INSERT` que no pasan por `create_pedido` (por ejemplo, `tests/unit/test_sector.py` construye `Pedido(...)` directamente). Los `UPDATE` no tienen red: cada función de dominio lo asigna.
- `delete_pedido` no lo toca.
- **SQLite y la zona:** en los tests (SQLite) la columna vuelve sin zona; en PostgreSQL vuelve con zona. En S2 solo se muestra. Que la comparación del 409 funcione en las dos bases es tarea de S3 (ver Pendientes).

### 5.3 `src/core/autorizacion.py`: `acciones_permitidas`

```python
from dataclasses import dataclass
from datetime import date
from typing import Literal

MotivoBloqueo = Literal["rechazado", "dia_anterior", "con_entregas"]


@dataclass(frozen=True)
class Disponibles:
    """lo que se puede hacer con el pedido. al menos una acción está habilitada"""
    autorizar: bool
    corregir: bool
    rechazar: bool


@dataclass(frozen=True)
class Bloqueado:
    """no se puede hacer nada con el pedido, y por qué"""
    motivo: MotivoBloqueo


AccionesPedido = Disponibles | Bloqueado

_REVISAR = Disponibles(autorizar=True, corregir=False, rechazar=True)
_CORREGIR = Disponibles(autorizar=False, corregir=True, rechazar=False)


def acciones_permitidas(pedido: Pedido, hoy: date, hay_entregas: bool) -> AccionesPedido:
    """única fuente de las reglas de estado y fecha. la usan el detalle (S2) y,
    desde S3, el PUT y el reject como guarda"""
    match pedido.estado:
        case "rechazado":
            return Bloqueado("rechazado")
        case _ if pedido.fecha_desde < hoy:
            return Bloqueado("dia_anterior")
        case "confirmado":
            return _REVISAR
        case "autorizado":
            return Bloqueado("con_entregas") if hay_entregas else _CORREGIR
        case otro:
            raise ValueError(f"Estado de pedido desconocido: {otro!r}")
```

- Las únicas dos instancias de `Disponibles` son `_REVISAR` y `_CORREGIR`. No se exporta un constructor genérico.
- Prioridad de los motivos: `rechazado` > `dia_anterior` > `con_entregas`. Un rechazado de ayer dice "rechazado", porque es el estado final.
- Un `estado` fuera de los tres conocidos lanza `ValueError`. El modelo no debería permitirlo, pero si pasa, el GET responde 500 en lugar de mostrar acciones inventadas.
- Ojo con el orden del `match`: el caso `dia_anterior` va después de `rechazado` y antes de los demás. Con un estado desconocido y fecha pasada devuelve `dia_anterior` en lugar de lanzar el error; se acepta, porque no habilita nada.

Tabla de la regla, con los pedidos de los seeds que la prueban en el portal:

| Estado | `fecha_desde` | Entregas | Resultado | Seeds |
|---|---|---|---|---|
| confirmado | < hoy | — | `Bloqueado("dia_anterior")` | 3 |
| confirmado | ≥ hoy | — | autorizar + rechazar | 4, 5, 9, 10, 11, 13 |
| autorizado | < hoy | — | `Bloqueado("dia_anterior")` | 1, 2 |
| autorizado | ≥ hoy | no | corregir | 6, 12 |
| autorizado | ≥ hoy | sí | `Bloqueado("con_entregas")` | 7 |
| rechazado | cualquiera | — | `Bloqueado("rechazado")` | 8 |

### 5.4 `src/core/pedido.py`: relación con el revisor

Se agrega `revisor = relationship("User", foreign_keys=[revisado_por])` en `Pedido`. No cambia el esquema (la columna `revisado_por` ya existe). Sirve para devolver el nombre del revisor sin una consulta aparte.

### 5.5 `src/web/schemas/`

**`pedidos.py`:** `PedidoDetailSchema` gana `modificado_en = fields.DateTime()`. Lo ven el Jefe (`/api/requests/{id}`) y Alimentación. El listado (`PedidoOutputSchema`) no cambia.

**`autorizaciones.py`:** marshmallow 4 ya no tiene `context`, así que `acciones` y `entregadas` no pueden llegar al schema por ahí. Se arma con una función que recibe todo:

```python
class SolicitudAutorizacionSchema(SolicitudOutputSchema):
    dni = fields.String(attribute="personal.dni")
    es_residente = fields.Boolean(attribute="personal.es_residente")


def dump_detalle_autorizacion(pedido, acciones: AccionesPedido, entregadas: set[int]) -> dict:
    detalle = pedido_detail_schema.dump(pedido)
    detalle["solicitudes"] = [
        {**SolicitudAutorizacionSchema().dump(s), "entregada": s.id in entregadas}
        for s in _ordenadas(pedido.solicitudes)
    ]
    detalle["acciones"] = _dump_acciones(acciones)
    detalle["revision"] = _dump_revision(pedido)
    return detalle
```

- `_ordenadas` es el mismo orden que hoy usa `PedidoDetailSchema.dump_solicitudes` (fecha, comida, id). Se reusa la función, no se copia.
- `_dump_acciones`:
  - `Disponibles(a, c, r)` → `{"autorizar": a, "corregir": c, "rechazar": r, "motivo_bloqueo": None}`.
  - `Bloqueado(m)` → los tres en `false` y `"motivo_bloqueo": m`.
  - Con `match` sobre los dos tipos. Si llega otra cosa, `TypeError` (no hay `else` silencioso).
- `_dump_revision`: `None` si `revisado_por` es `None`; si no, `{"usuario": "Nombre Apellido", "en": revisado_en}`.

Forma de la respuesta (lo nuevo en negrita):

```jsonc
{
  "id": 6, "sector_id": 3, "sector_name": "Intendencia",
  "periodicidad": "diaria", "fecha_desde": "2026-11-19", "fecha_hasta": "2026-11-19",
  "estado": "autorizado", "motivo_rechazo": null, "revisado_en": "...",
  "modificado_en": "2026-11-19T09:12:03.123456-03:00",        // nuevo
  "totales":     { "desayuno": { "total": 4, "regular": 2, "excepcion": 2, "por_menu": {...} }, ... },
  "autorizados": { "desayuno": { "total": 3, ... }, ... },    // null si está pendiente
  "acciones": { "autorizar": false, "corregir": true, "rechazar": false, "motivo_bloqueo": null }, // nuevo
  "revision": { "usuario": "Ana Pérez", "en": "2026-11-19T09:12:03" },                         // nuevo, o null
  "solicitudes": [
    { "id": 31, "fecha": "2026-11-19", "tipo_comida": "desayuno",
      "personal_id": 21, "apellido": "Quiroga", "nombre": "...",
      "dni": "25400001", "es_residente": false,               // nuevos
      "tipo_menu_id": 1, "tipo_menu": "General",
      "condicion": "regular", "autorizada": true,
      "entregada": false }                                     // nuevo
  ],
  "detalle": { ... }   // se mantiene (lo usa Carga)
}
```

### 5.6 `src/web/controlers/api_autorizaciones.py`: `show`

```python
@api_autorizaciones_bp.get("/<int:request_id>")
@permission_required("autorizacion_show")
def show(request_id: int):
    pedido = _get_or_404(request_id)
    entregadas = ids_entregados(s.id for s in pedido.solicitudes)
    acciones = acciones_permitidas(pedido, reloj.hoy(), hay_entregas=bool(entregadas))
    return jsonify(dump_detalle_autorizacion(pedido, acciones, entregadas)), 200
```

- `ids_entregados` ya existe en `core/entrega.py` (una sola consulta con `IN`).
- `authorize` y `reject` no cambian.

### 5.7 `admin/docs/openapi.yaml`

- Schema nuevo `AuthorizationDetail` (extiende `RequestDetail`) con `acciones`, `revision` y, en cada solicitud, `dni`, `es_residente` y `entregada`. `GET /authorizations/{request_id}` pasa a devolverlo.
- `RequestDetail` gana `modificado_en` (date-time): "Fecha y hora de la última modificación del pedido o de sus solicitudes. Se usará como versión en el PUT (S3)."
- Descripción de `acciones.motivo_bloqueo`: enum `rechazado`, `dia_anterior`, `con_entregas`, o `null` si alguna acción está habilitada.

### 5.8 Tests

Se usan los fixtures y helpers existentes (`client`, `data`, `login_as`, `crear`, `set_estado`). "Hoy" se fija con `monkeypatch.setattr(reloj, "hoy", lambda: D)` y "ahora" con `monkeypatch.setattr(reloj, "ahora", ...)`.

**`tests/unit/test_acciones_permitidas.py` (nuevo, sin base de datos).** La función es pura: alcanza con un objeto con `estado` y `fecha_desde` (`SimpleNamespace`).

| Test | Esperado |
|---|---|
| `test_acciones` parametrizado con las 6 filas de la tabla de 5.3 (más `confirmado` con `hay_entregas=True`, que no debería pasar pero no cambia nada) | El resultado de la tabla |
| `test_fecha_desde_es_la_que_manda` | Pedido con `fecha_desde = hoy - 1` y `fecha_hasta = hoy + 5` → `Bloqueado("dia_anterior")` |
| `test_hoy_no_es_dia_anterior` | `fecha_desde == hoy` → no bloqueado |
| `test_estado_desconocido` | `estado = "foo"`, fecha futura → `ValueError` |

**`tests/unit/test_api_autorizaciones.py` (agregados).**

| Test | Preparación | Esperado |
|---|---|---|
| `test_show_incluye_modificado_en_y_acciones` | Pedido confirmado de hoy | `modificado_en` presente; `acciones == {autorizar: true, corregir: false, rechazar: true, motivo_bloqueo: null}`; `revision` es `null` |
| `test_show_dia_anterior_bloqueado` | Pedido de D, consultado con `hoy = D+1` | `motivo_bloqueo == "dia_anterior"` |
| `test_show_revision_y_motivo` | Pedido rechazado con motivo por `data.alimentacion` | `revision.usuario` con el nombre del usuario; `motivo_rechazo` presente; `motivo_bloqueo == "rechazado"` |
| `test_show_entregada_por_solicitud` | Pedido autorizado + `create_entrega` de una persona en una comida | Esa solicitud `entregada: true`, el resto `false`; `motivo_bloqueo == "con_entregas"` |
| `test_show_dni_y_residente` | Pedido con un residente | `es_residente` true solo para él; `dni` presente |
| `test_modificado_en_cambia_al_modificar_solo_personas` | Crear con `ahora = T1`, modificar como jefe **solo las personas** (mismas fechas) con `ahora = T2` | `modificado_en` pasa de T1 a T2 |
| `test_modificado_en_cambia_al_autorizar_y_rechazar` | Dos pedidos, `ahora = T2` en la revisión | Los dos con `modificado_en == T2` |
| `test_requests_show_incluye_modificado_en` | Detalle como jefe | `modificado_en` presente; **no** trae `acciones` ni `revision` |

El 401, el 403 para jefe y comedor y el 404 ya están cubiertos (`test_without_authentication_returns_401`, `test_only_alimentacion_can_authorize`, `test_show_unknown_returns_404`). **No se duplican.**

## 6. Portal (`code/portal/src/modules/autorizacion/`)

### 6.1 Archivos

```
modules/autorizacion/
├── routes.js                              (modificado) ruta del detalle
├── filtros.js                             (modificado) MOTIVOS_BLOQUEO, formatearFechaHora
├── services/autorizacionService.js        (modificado) obtenerPedido + aPedidoDetalle
├── components/TablaPedidos.vue            (modificado) el sector es un link al detalle
├── components/MatrizSolicitudes.vue       (nuevo)
├── components/TotalesComparados.vue      (nuevo)
└── views/DetallePedidoView.vue            (nuevo)
```

Igual que en S1: no se tocan `router/`, `constants/`, `api/` ni otros módulos, y no se importa nada de `carga-pedidos` (aunque tenga una `TablaTotales` parecida).

### 6.2 `services/autorizacionService.js`: `obtenerPedido(id)`

`GET /authorizations/{id}` y `aPedidoDetalle(json)`. Como en S1: si falta un campo o un valor no es válido, tira `Error` con el campo y el valor; no completa con defaults.

Forma que usa la vista:

```js
/**
 * @typedef {{ solicitudId: number, menu: string, condicion: 'regular'|'excepcion',
 *             autorizada: boolean|null, entregada: boolean }} Celda
 * @typedef {{ personaId: number, apellido: string, nombre: string, dni: string,
 *             esResidente: boolean, celdas: { [comida]: Celda } }} FilaPersona
 * @typedef {{ fecha: string, personas: FilaPersona[] }} DiaPedido
 */
{
  id, sector, estado,                      // estado ∈ ESTADOS
  fechaDesde, fechaHasta,
  modificadoEn,                            // string ISO tal como viene (S3 la devuelve en el PUT)
  motivoRechazo,                           // string | null
  revision,                                // null | { usuario, en }
  acciones,                                // { autorizar, corregir, rechazar, motivoBloqueo }
  pedidas,                                 // { [comida]: { total, regular, excepcion, porMenu: { [menu]: n } } }
  autorizadas,                             // igual que pedidas, o null si está pendiente
  dias,                                    // DiaPedido[], ordenados por fecha
}
```

Reglas del parser:

- `pedidas` y `autorizadas` reusan `aTotales` de S1, que pasa a conservar `por_menu` como `porMenu` (validando que sean enteros ≥ 0).
- `autorizadas` es `null` si y solo si `estado === 'confirmado'` (igual que en S1).
- `revision` es `null` si y solo si `estado === 'confirmado'`.
- `motivoRechazo` es string no vacío si y solo si `estado === 'rechazado'`. Es la regla final (S4 hace el motivo obligatorio). Hasta S4, un rechazado sin motivo (posible con la API de Etapa 1) se ve como respuesta inválida; los seeds no tienen ninguno.
- `acciones.motivoBloqueo` es uno de `MOTIVOS_BLOQUEO` o `null`; si no es `null`, los tres flags tienen que ser `false`.
- `dias` se arma agrupando `solicitudes` por `fecha` y, dentro de cada fecha, por `personal_id`. Las personas van en el orden de incorporación (el menor `id` de solicitud de cada una). **Una persona con dos solicitudes de la misma comida en la misma fecha** es una respuesta inválida (no puede pasar por las validaciones del core).
- `autorizada` es `null` en todas las celdas si el pedido está pendiente, y `boolean` en todas si no.

### 6.3 `views/DetallePedidoView.vue`

- Props: `id` (Number, de la ruta).
- `const { data: pedido, loading, error, load } = useAsyncData(() => obtenerPedido(props.id))` y `watch(() => props.id, load, { immediate: true })`.

Plantilla (`.page` / `.stack`):

1. Link **"← Volver a la bandeja"** (ver 6.7).
2. **Encabezado** (`.page__header`): `h1` "Pedido de {sector}", subtítulo con `formatearFechaLarga(fechaDesde)` (o "del … al …" si son varios días) y el badge de estado (el mismo `datosDeEstado` de S1). A la derecha, la **barra de acciones** (`.toolbar`):
   - "Autorizar" (`.btn--primary`) si `acciones.autorizar`.
   - "Rechazar" (`.btn--danger`) si `acciones.rechazar`.
   - "Corregir autorización" (`.btn--secondary`) si `acciones.corregir`.
   - Cada botón llama a un handler vacío con un comentario que dice en qué slice se implementa (`// S3: confirmar y PUT`, `// S4: diálogo de rechazo`, `// S5: pasar la matriz a edición`). No muestra toast ni mensaje.
3. **Datos de revisión** (lista de definiciones, `dl`):
   - "Última modificación": `formatearFechaHora(modificadoEn)`.
   - "Revisado por": `revision.usuario`, el `formatearFechaHora(revision.en)`. Solo si `revision` no es `null`.
4. **Avisos:**
   - Rechazado: `.alert.alert--error` con "Pedido rechazado. Motivo: …".
   - `motivoBloqueo` distinto de `rechazado`: `.alert.alert--info` con el texto de `MOTIVOS_BLOQUEO`. Con `rechazado` solo va el aviso de error, para no repetirlo.
5. **`TotalesComparados`** con `pedidas` y `autorizadas`.
6. **`MatrizSolicitudes`** por cada día de `dias` (con `h2` de la fecha solo si hay más de un día).
7. Estados: `LoadingState` "Cargando pedido…"; `ErrorState` con `error.message` y `retryable`. El 404 llega como `ApiError` no reintentable con el mensaje de la API ("El pedido no existe.").

### 6.4 `components/MatrizSolicitudes.vue`

- Props: `personas` (`FilaPersona[]`), `revisado` (bool), `fecha` (para el `caption`).
- `.table-wrap` > `table.table`, `caption` (`visually-hidden`) "Solicitudes del {fecha}".
- Filas: personas. `th scope="row"` con "Apellido, Nombre", debajo en texto chico el DNI y, si es residente, un badge "Residente".
- Columnas: las cuatro comidas de `COMIDAS`, siempre las cuatro (mismo criterio que la bandeja).
- Celda:
  - Sin solicitud: `—` (con texto `visually-hidden` "Sin solicitud").
  - Con solicitud: el menú y el badge de condición (`Regular` / `Excepción`).
  - Si `revisado`: además "✓ Autorizada" o "✗ No autorizada" (texto, no solo ícono), y "Entregada" si corresponde.
  - Una celda no autorizada de un pedido revisado lleva un estilo atenuado. No depende solo del color, porque dice "No autorizada".
- Pie (`tfoot`): por comida, la cantidad pedida y, si `revisado`, la autorizada (`14 / 12`, el mismo formato que la bandeja).

### 6.5 `components/TotalesComparados.vue`

- Props: `pedidas`, `autorizadas` (o `null`).
- `.table-wrap` > `table.table`, `caption` "Totales por comida y menú".
- Columnas: **Comida / menú**, **Pedidas**, **Regular**, **Excepción** y, si hay `autorizadas`, **Autorizadas**. Los números van con `.num`.
- Filas, para cada comida que tenga solicitudes, en el orden de `COMIDAS`:
  - Una **fila de comida** (`th scope="rowgroup"`, en negrita) con el total pedido, regulares, excepciones y el total autorizado.
  - Debajo, una **fila por menú** (sangrada, `th scope="row"`) con lo pedido de ese menú y lo autorizado de ese menú (`autorizadas[comida].porMenu[menu] ?? 0`). En estas filas las columnas Regular y Excepción quedan vacías, porque la API no da la condición por menú.
- Última fila (`tfoot`): **Total**, con la suma de las comidas en cada columna.
- Una comida con solicitudes pedidas y ninguna autorizada muestra `0` en Autorizadas, no `—`.
- S3 le agrega la columna "A autorizar" en vivo.
- Se ajusta visualmente con el pedido 4 (pendiente, cuatro comidas, tres menús) y el 6 (parcial).

### 6.6 `components/TablaPedidos.vue`

- El nombre del sector pasa a ser un `RouterLink` a `{ name: 'autorizacion-detalle', params: { id } }`, con `aria-label` "Ver pedido de {sector}".
- No se agrega columna "Ver detalle".

### 6.7 Volver a la bandeja

```js
const destino = computed(() => {
  const anterior = window.history.state?.back   // Vue Router guarda acá la ruta anterior
  return esBandeja(anterior) ? anterior : { name: 'autorizacion-pendientes', query: { fecha: pedido.value?.fechaDesde } }
})

function volver(evento) {
  if (esBandeja(window.history.state?.back)) {
    evento.preventDefault()
    router.back()
  }
}
```

- Es un `RouterLink :to="destino" @click="volver"`: funciona como link (abrir en otra pestaña, por ejemplo), y con un clic normal hace Atrás si venimos de la bandeja, así el historial no crece.
- `esBandeja(ruta)`: `router.resolve(ruta).name === 'autorizacion-pendientes'`. Si `ruta` no es un string, es `false`.
- Si el pedido todavía no cargó (error o carga), el fallback va a la bandeja sin `fecha` (hoy).

### 6.8 `routes.js`

```js
{
  path: '/autorizaciones/pedidos/:id(\\d+)',
  name: 'autorizacion-detalle',
  component: () => import('./views/DetallePedidoView.vue'),
  props: (ruta) => ({ id: Number(ruta.params.id) }),
  meta: {
    roles: [ROLES.ALIMENTACION],
    title: 'Detalle del pedido',
    section: 'autorizacion-pendientes',   // deja marcada "Autorizaciones" en el menú
  },
}
```

### 6.9 `filtros.js`

- `MOTIVOS_BLOQUEO`: `[{ valor: 'dia_anterior', texto: 'Período anterior: solo lectura.' }, { valor: 'rechazado', texto: 'Pedido rechazado.' }, { valor: 'con_entregas', texto: 'Ya tiene comidas entregadas: la autorización no se puede corregir.' }]`.
- `formatearFechaHora(iso)`: "19/11/2026 09:12" en hora local.

## 7. Verificación

### 7.1 Automática

```sh
cd code/admin && poetry run pytest          # los tests nuevos en verde; las 4 fallas previas de SQLite siguen igual (ver reporte S1)
cd code/portal && npm run lint && npm run build   # el módulo sin errores; los 3 de carga-pedidos son previos
```

### 7.2 Manual desde el portal

Preparación (en `code/admin`). **Hay cambio de esquema**, así que el reset es obligatorio:

```sh
poetry run flask --app web reset-db
USER_NAME=admin@test.com USER_PASS=admin1234 poetry run flask --app web seeds
poetry run flask --app web run --debug
# en otra terminal: cd code/portal && npm run dev
```

Usuario: `alimentacion@test.com` / `alim1234`. Datos: sección S2 de `seeds-per-slice.md`.

| # | Acción | Esperado |
|---|---|---|
| 1 | En la bandeja de hoy, clic en Médicos Internos (pedido 4) | Abre `/autorizaciones/pedidos/4`. "Autorizaciones" sigue marcado en el menú. Se ven "Autorizar" y "Rechazar"; al clickearlos no pasa nada y no hay errores en la consola |
| 2 | Mirar la matriz del 4 | 10 personas × 4 comidas. Perez y Benítez con el badge "Residente" y una sola comida cada uno. 10 excepciones (ver la tabla de excepciones del pedido 4 en los seeds). Sin columna de autorización (pendiente) |
| 3 | Mirar los totales del 4 | Desayuno 8 (6 reg. / 2 exc.), almuerzo 9 (6 / 3), merienda 6 (4 / 2), cena 6 (3 / 3); total 29. Desglose por menú no trivial. Sin columna "Autorizadas" |
| 4 | "← Volver a la bandeja" | Vuelve a la bandeja de hoy con los mismos filtros. El Adelante del navegador vuelve al detalle |
| 5 | Filtrar la bandeja por estado autorizado, abrir el 6, volver | Vuelve con `?estado=autorizado` |
| 6 | Pedido 6 | Lo pedido 8, lo autorizado 7. 25400003 en el desayuno: "✗ No autorizada". "Revisado por" con el nombre del usuario de Alimentación. Solo se ve el botón "Corregir autorización" (no hace nada) |
| 7 | Pedido 7 | El desayuno de 26600001 y 26600002 dice "Entregada". Aviso "Ya tiene comidas entregadas…" y ningún botón |
| 8 | Pedido 8 | Badge Rechazado y "Motivo: No se informó la guardia del día…". 5 solicitudes "No autorizada", Acosta como residente. Ningún botón ni aviso de bloqueo aparte |
| 9 | "← Día anterior" en la bandeja, abrir el 3 | Aviso "Período anterior: solo lectura" y ningún botón, aunque esté pendiente |
| 10 | Pedido 12 (navegar a D+3) | Pedido 8, autorizado 6 |
| 11 | Abrir `/autorizaciones/pedidos/4` en otra pestaña y usar "Volver" | Va a la bandeja del día del pedido (sin historial) |
| 12 | Abrir `/autorizaciones/pedidos/999` | `ErrorState` "El pedido no existe.", sin "Reintentar" |
| 13 | Con `jefe.medicos@test.com`, abrir `/autorizaciones/pedidos/4` | "Acceso denegado". `GET /api/authorizations/4` con su token responde 403 |
| 14 | Como jefe, `GET /api/requests/4` | Trae `modificado_en`; no trae `acciones` ni `revision` |

### 7.3 Criterio de terminado

- Los pasos 7.1 y 7.2 pasan completos, **incluidos los pasos de navegador** (en S1 quedaron pendientes).
- La rama está mergeada en local a `feature/autorizacion-pedidos` y **no** está pusheada.
- `seeds-per-slice.md` no cambia (los seeds usan `create_pedido`, que ya asigna `modificado_en`). Si algo cambia, se actualiza.
- Está escrito el reporte en `documentacion-personal/reporte-s2-detalle-pedido.md` con la plantilla del artefacto (encabezado, alcance, cambios con "base de datos: columna `pedidos.modificado_en`, aplicada con reset-db", prueba manual con el resultado de cada paso, pendientes y decisiones).

## 8. Pendientes para otras slices

- **S3:** comparar `modificado_en` en el PUT. Definir cómo se compara para que dé igual en SQLite (sin zona) y en PostgreSQL (con zona); por ejemplo, normalizar a UTC antes de guardar y de comparar. Pasar `autorizado_en` / `revisado_en` a `reloj.ahora()` al reescribir `autorizar_pedido`.
- **S3/S4/S5:** usar `acciones_permitidas` como guarda en PUT y reject; quitar `get_pedido_para_revisar` del controlador. Completar los handlers vacíos de "Autorizar" (S3), "Rechazar" (S4) y "Corregir autorización" (S5) en `DetallePedidoView.vue`.
- **S4:** el motivo obligatorio en la API hace consistente lo que el parser del portal ya exige desde S2.
- **Coordinación con Carga:** S2 toca `create_pedido` y `update_pedido` (una línea cada una). Avisar al que trabaje en "un pedido por fecha" para evitar conflictos de merge.
