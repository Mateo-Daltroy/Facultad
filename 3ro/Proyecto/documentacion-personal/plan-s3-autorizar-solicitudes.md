# Plan S3: Autorizar solicitudes una por una (Módulo de Autorización)

> **BORRADOR v1 (19/11/2026).** Decisiones cerradas. La columna "A autorizar" y el texto de la confirmación se ajustan visualmente durante la implementación.
>
> Plan de implementación de la tercera slice del "Plan de desarrollo — Módulo de Autorización" (artefacto).
>
> Referencias: `documentacion-personal/plan-s2-detalle-pedido.md` y `reporte-s2-detalle-pedido.md` (slice anterior), `documentacion-personal/seeds-per-slice.md` (sección S3), `documentacion-personal/resumen_portal_y_core.md` y `Etapa_1y2_Trabajo Integrador_2026_PUBLIC.md` (Módulo de Autorización y Ampliaciones de la API).
>
> **Precondición:** S2 está terminada y mergeada en `feature/autorizacion-pedidos`. Si `reporte-s2-detalle-pedido.md` registra nombres o decisiones distintos de los del plan S2 (por ejemplo, el nombre de un componente o del parser), **manda el código de S2**: se adapta este plan a lo que existe, sin renombrar lo de S2.

## 1. Objetivo

Hoy el detalle (S2) muestra un pedido en solo lectura. El botón "Autorizar" existe pero no hace nada, y el `PUT /api/authorizations/{id}` es el de Etapa 1: una solicitud que falta en el body queda como no autorizada, no hay control de versión ni de día anterior, y no queda historial.

Al terminar S3, el usuario de Alimentación:

- Abre un pedido pendiente y ve la matriz **con un checkbox en cada celda**, sin un paso previo de "editar". Arrancan marcadas las solicitudes **regulares** y desmarcadas las de **excepción**.
- Marca y desmarca solicitudes y ve en vivo, en la tabla de totales, cuánto **quedaría autorizado** por comida y menú ("A autorizar"), al lado de lo pedido.
- Pulsa "Autorizar", ve una **confirmación con los totales** y confirma.
- Si salió bien: toast de éxito y **vuelve a la bandeja** (con la misma fecha y filtros, como el link "Volver" de S2).
- Si el pedido cambió mientras lo revisaba (otro usuario lo autorizó, o el Jefe agregó o quitó personas): **409**, toast con el motivo y el detalle se **recarga** con la versión nueva.

En la API:

- `PUT /api/authorizations/{id}` recibe `{ modificado_en, solicitudes: [{ id, autorizada }] }` y exige **todas** las solicitudes del pedido.
- Usa `acciones_permitidas` (S2) como **guarda**, compara la **versión**, bloquea la fila y escribe el **historial** (`autorizacion_cambios`).
- Responde con el detalle de autorización (el mismo del GET).

**Criterio "Listo cuando" del artefacto:** con dos pestañas sobre el mismo pedido, la segunda en confirmar recibe 409 y ve la versión nueva.

**Inconsistencias transitorias aceptadas** (mismo criterio que S2). Los pedidos autorizados corregibles (6 y 12) siguen mostrando `corregir: true`, pero el PUT sobre ellos responde 409 hasta S5. El botón "Corregir autorización" sigue sin hacer nada y "Rechazar" sigue como en Etapa 1 hasta S4.

Requisitos del enunciado que cubre:

- "autorizar o no cada solicitud".
- "ver los totales pedidos y los que se van a autorizar antes de confirmar".
- El `PUT /api/authorizations/{id}` de Etapa 1/2.
- De las Ampliaciones: el 409 cuando "el pedido fue modificado mientras se revisaba" y que "un día anterior a hoy es solo lectura".

## 2. Decisiones

| Tema | Decisión | Motivo |
|---|---|---|
| Función de dominio | `revisar_pedido(pedido_id, usuario_id, revision, ahora)` **reemplaza** a `autorizar_pedido`, que se borra. En S3 solo acepta pedidos con `acciones.autorizar` (confirmados de hoy o del futuro) | Nombre final del artefacto. S5 le agrega la corrección sin cambiar la firma |
| `ahora` como parámetro | Lo recibe `revisar_pedido`, que deriva `hoy = ahora.date()`. El controlador pasa `reloj.ahora()` | Los seeds autorizan pedidos pasados (1 y 2): con `ahora` = fecha del pedido a las 09:00 pasan la guarda y quedan con marcas de tiempo realistas. Los tests de dominio no necesitan monkeypatch |
| Versión | **String opaco.** `version_de(pedido) -> VersionPedido` (`NewType` de `str`) devuelve `pedido.modificado_en.isoformat()`. Es la **única** función que lo arma: la usan el schema (al serializar `modificado_en`) y `revisar_pedido` (al comparar). La API nunca parsea la versión como fecha | Decisión acordada. Como la comparación siempre ocurre dentro de la misma base, no importa que SQLite devuelva sin zona y PostgreSQL con zona |
| Orden de los chequeos | 404 → bloqueo de fila → guarda `acciones_permitidas` (409) → versión (409) → cobertura (400) | Si el Jefe agregó una persona, la versión cambió: lo correcto es 409 y recargar, no un 400 por una solicitud "faltante" |
| Bloqueo de fila | `select(Pedido).where(Pedido.id == id).with_for_update().execution_options(populate_existing=True)` | Sin `populate_existing`, si el pedido ya estaba en la sesión se devuelve la copia en memoria sin releer `modificado_en`. En SQLite `FOR UPDATE` se ignora (se acepta: los tests prueban la versión, no el bloqueo) |
| Cobertura | El conjunto de ids del body tiene que ser **igual** al de las solicitudes del pedido. Si no, `BusinessRuleError` (400) con `field="solicitudes"` y un mensaje que lista faltantes y ajenas | Un body parcial ya no desautoriza en silencio (problema del artefacto) |
| Entrada parseada | `AutorizarInputSchema` exige `modificado_en` (string no vacío) y en `post_load` devuelve `RevisionAutorizacion(version, decisiones)`, `@dataclass(frozen=True)`, con `decisiones` como `MappingProxyType`. `core` nunca recibe el dict crudo | "Parse, don't validate" |
| Historial | Tabla nueva `autorizacion_cambios` (modelo `AutorizacionCambio`) con el esquema final. El CHECK de `tipo` admite ya los cuatro valores (`autorizacion`, `correccion`, `rechazo`, `generacion`). En S3 solo se escribe `autorizacion`: una fila por solicitud cuyo valor cambia (en la primera autorización, todas, porque pasan de `NULL` a `true`/`false`) | Cada slice escribe la versión final de lo que toca |
| Fechas con zona | `pedidos.revisado_en`, `solicitudes.autorizado_en` y `autorizacion_cambios.momento` pasan a `DateTime(timezone=True)` y se asignan con `ahora`. `rechazar_pedido` también pasa a usar `reloj.ahora()` | Pendiente de S2. Hay reset-db igual por la tabla nueva |
| Respuesta del PUT | `dump_detalle_autorizacion` con las acciones recalculadas después de escribir | Contrato final. El portal la parsea (y así la valida) aunque después navegue a la bandeja |
| Controlador | Se quita la llamada previa a `get_pedido_para_revisar` en el PUT **y** en el reject. El reject sigue llamando a `rechazar_pedido`, que por dentro sigue usando `get_pedido_para_revisar` hasta S4 | Pendiente del artefacto. En el reject, ahora un body inválido da 400 antes que el 404/409; se acepta |
| Selección inicial | Función pura `seleccionInicial(dias)`: regulares `true`, excepciones `false`. Vive en `modules/autorizacion/seleccion.js` | Decisión acordada. Es una sugerencia de la interfaz: la API no la conoce. S4 la reusa como atajo "solo regulares" |
| Entrar a la edición | Si `acciones.autorizar`, la matriz ya muestra los checkboxes. No hay botón "Revisar" | Decisión acordada |
| Después del PUT | Toast de éxito y vuelve a la bandeja con la misma lógica del link "Volver" de S2 (`router.back()` si se vino de la bandeja; si no, la bandeja del día del pedido) | Decisión acordada: sigue con el próximo pendiente |
| 409 en el portal | Toast `warning` con el mensaje de la API y `load()`. La selección se rearma con `seleccionInicial` sobre los datos nuevos | Regla del artefacto |
| 400 en el portal | Toast `error` con el mensaje. No recarga | Solo puede pasar por un bug del cliente |
| Cambios sin guardar | **No** se avisa si se sale del detalle con la selección modificada | Acordado: fuera de alcance |
| Rama | `feat/autorizacion-s3` sale de `feature/autorizacion-pedidos` (con S2 mergeada) y vuelve con merge local `--no-ff` | Igual que S1 y S2 |

## 3. Fuera de alcance

No hacer en S3 (cada ítem tiene su slice):

- Atajos masivos (todo, ninguno, solo regulares, por comida, por persona) y el botón "Rechazar" con diálogo y motivo obligatorio (S4). `seleccionInicial` queda lista para que S4 la reuse.
- `historial tipo=rechazo`, `CHECK` del motivo y reescritura de `rechazar_pedido` (S4).
- Corrección de autorizados (`tipo=correccion`), el 409 por entregas en el PUT, `GET /history` y el botón "Corregir autorización" (S5).
- Aviso de cambios sin guardar al salir del detalle.
- Navegación por teclado dentro de la matriz y la vista móvil (S11).
- Bloquear también la fila en `update_pedido` del Jefe (ver Pendientes).
- Reemplazar los `date.today()` / `datetime.now()` de otros módulos (entregas, comidas, seeds fuera de la autorización).

## 4. Git

```sh
cd code
git switch feature/autorizacion-pedidos
git log --oneline -3          # confirmar que el merge de feat/autorizacion-s2 está
git switch -c feat/autorizacion-s3
```

- Commits chicos por capa, por ejemplo:
  - `feat(core): autorizacion_cambios y fechas con zona`
  - `feat(core): revisar_pedido con versión, cobertura y guarda`
  - `feat(api): PUT de autorización con versión y detalle`
  - `test: ...`
  - `feat(portal): autorizar solicitudes desde el detalle`
- Al terminar: `git switch feature/autorizacion-pedidos && git merge --no-ff feat/autorizacion-s3`.
- **No pushear `feat/autorizacion-s3` al origin.** `feature/autorizacion-pedidos` se pushea solo si lo pide el usuario.

## 5. Backend (`code/admin`)

### 5.1 `src/core/autorizacion_cambio.py` (nuevo): `AutorizacionCambio`

```python
"""historial de decisiones sobre solicitudes. una fila por solicitud cuyo valor cambia"""
from datetime import datetime
from typing import Literal

from sqlalchemy import Boolean, CheckConstraint, DateTime, ForeignKey, Integer, String
from sqlalchemy.orm import Mapped, mapped_column

from .database import BaseModel

TipoCambio = Literal["autorizacion", "correccion", "rechazo", "generacion"]


class AutorizacionCambio(BaseModel):
    __tablename__ = "autorizacion_cambios"
    __table_args__ = (
        CheckConstraint(
            "tipo IN ('autorizacion', 'correccion', 'rechazo', 'generacion')",
            name="ck_autorizacion_cambio_tipo",
        ),
    )
    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    solicitud_id: Mapped[int] = mapped_column(Integer, ForeignKey("solicitudes.id"), nullable=False)
    pedido_id: Mapped[int] = mapped_column(Integer, ForeignKey("pedidos.id"), nullable=False)
    usuario_id: Mapped[int] = mapped_column(Integer, ForeignKey("users.id"), nullable=False)
    momento: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)
    autorizada_antes: Mapped[bool | None] = mapped_column(Boolean, nullable=True)
    autorizada_despues: Mapped[bool] = mapped_column(Boolean, nullable=False)
    tipo: Mapped[str] = mapped_column(String(15), nullable=False)
```

- Se importa donde se registran los modelos para `create_all` (mismo lugar que el resto de los modelos de `core`; ver cómo lo hace `reset-db`).
- **Ojo con las solicitudes que se borran:** `update_pedido` del Jefe regenera las solicitudes de un pedido **confirmado**. Esos pedidos todavía no tienen historial, porque el historial empieza al autorizar, y un autorizado no se edita. No hace falta `ON DELETE`. Si al implementar aparece algún camino que borre solicitudes con historial, se anota en el reporte y no se resuelve en S3.
- `autorizada_despues` es `NOT NULL`: ningún cambio deja una solicitud pendiente.

### 5.2 Fechas con zona

- `pedidos.revisado_en` y `solicitudes.autorizado_en` pasan de `DateTime` a `DateTime(timezone=True)`.
- `rechazar_pedido` reemplaza su `datetime.now()` por `reloj.ahora()` (en S4 pasa a recibir `ahora` como parámetro, como `revisar_pedido`).
- `_dump_revision` (S2) sigue igual: `revisado_en.isoformat()`.

### 5.3 `src/core/autorizacion.py`: versión, revisión y `revisar_pedido`

```python
from dataclasses import dataclass
from datetime import datetime
from types import MappingProxyType
from typing import Mapping, NewType

from sqlalchemy import select

from .autorizacion_cambio import AutorizacionCambio
from .entrega import ids_entregados

VersionPedido = NewType("VersionPedido", str)


def version_de(pedido: Pedido) -> VersionPedido:
    """única forma de obtener la versión de un pedido. la usan el GET (al serializar
    modificado_en) y el PUT (al comparar). es un string opaco: nunca se parsea"""
    return VersionPedido(pedido.modificado_en.isoformat())


@dataclass(frozen=True)
class RevisionAutorizacion:
    """lo que manda el portal: la versión que vio y la decisión sobre cada solicitud"""
    version: VersionPedido
    decisiones: Mapping[int, bool]   # MappingProxyType: inmutable, ids únicos por construcción


def revisar_pedido(pedido_id: int, usuario_id: int, revision: RevisionAutorizacion,
                   ahora: datetime) -> Pedido:
    """autorización inicial de un pedido confirmado (S5 agrega la corrección).
    todo o nada: si algo falla no se escribe nada"""
    pedido = _bloquear(pedido_id)                                   # 404
    entregadas = ids_entregados(s.id for s in pedido.solicitudes)
    acciones = acciones_permitidas(pedido, ahora.date(), hay_entregas=bool(entregadas))
    _exigir_autorizar(acciones, pedido)                             # 409
    if revision.version != version_de(pedido):
        raise ConflictError("El pedido cambió mientras se revisaba. Se recargó la versión actual.",
                            field="modificado_en")                   # 409
    _exigir_cobertura(pedido, revision.decisiones)                  # 400

    for solicitud in pedido.solicitudes:
        nueva = revision.decisiones[solicitud.id]
        if solicitud.autorizada != nueva:
            db.session.add(AutorizacionCambio(
                solicitud_id=solicitud.id, pedido_id=pedido.id, usuario_id=usuario_id,
                momento=ahora, autorizada_antes=solicitud.autorizada,
                autorizada_despues=nueva, tipo="autorizacion",
            ))
        solicitud.autorizada = nueva
        solicitud.autorizado_por = usuario_id
        solicitud.autorizado_en = ahora
    _cerrar_revision(pedido, "autorizado", usuario_id, ahora)
    _commit()
    return pedido
```

Detalles:

- **`_bloquear(pedido_id)`**:
  - `db.session.scalars(select(Pedido).where(Pedido.id == pedido_id).with_for_update().execution_options(populate_existing=True)).one_or_none()`.
  - `NotFoundError("El pedido no existe.")` si no existe o si `pedido_id <= 0`.
- **`_exigir_autorizar(acciones, pedido)`** hace `match` sobre `AccionesPedido`:
  - `Disponibles(autorizar=True)` → sigue.
  - `Bloqueado(motivo)` → `ConflictError` con el texto del motivo:
    - `rechazado` → "El pedido fue rechazado."
    - `dia_anterior` → "El pedido es de un período anterior: es solo lectura."
    - `con_entregas` → "El pedido ya tiene comidas entregadas."
    - Los textos van en un dict `MENSAJES_BLOQUEO` junto a `MotivoBloqueo`, con una clave por cada valor del `Literal`.
  - `Disponibles(autorizar=False)` (hoy solo `_CORREGIR`) → `ConflictError("El pedido ya fue autorizado.")`. S5 cambia este caso.
  - Otro valor → `TypeError`.
- **`_exigir_cobertura(pedido, decisiones)`**:
  - `faltantes = sorted(propias - recibidas)`, `ajenas = sorted(recibidas - propias)`.
  - Si alguna no está vacía, `BusinessRuleError` con `field="solicitudes"` y un mensaje como "Faltan las solicitudes [31, 32]. No pertenecen al pedido: [999]." (solo las partes que correspondan).
- **`_cerrar_revision(pedido, estado, usuario_id, ahora)`**: asigna `revisado_en = ahora` y `modificado_en = ahora`. Se borra la llamada a `reloj.ahora()` de adentro: el instante lo decide quien llama. `rechazar_pedido` le pasa `reloj.ahora()`.
- **Se borra `autorizar_pedido`.** `get_pedido_para_revisar` queda solo para `rechazar_pedido` (S4 la borra).
- `core/entrega.py` no importa `autorizacion`, así que no hay import circular. Verificarlo igual al implementar.

### 5.4 `src/core/seeds.py`

`seed_pedidos_demo` reemplaza `autorizar_pedido(...)` por:

```python
cuando = datetime.combine(fecha, time(9, 0), tzinfo=reloj.ZONA)
revisar_pedido(pedido.id, alimentacion.id, RevisionAutorizacion(
    version=version_de(pedido),
    decisiones=MappingProxyType({
        s.id: (s.personal.dni, s.tipo_comida) not in no_autorizadas
        for s in pedido.solicitudes
    }),
), ahora=cuando)
```

- Con `ahora` = el día del pedido, los pedidos 1 (D-7) y 2 (D-2) pasan la guarda de día anterior.
- Esto pide que el seed de entregas del pedido 7 corra **después** de la autorización (ya es así).
- `rechazar_pedido` del pedido 8 no cambia (es de hoy).
- Verificar que `seeds-per-slice.md` siga siendo correcto. Lo esperado es que no cambie nada visible: mismos estados y mismas solicitudes no autorizadas. Si algo cambia, se actualiza el documento.

### 5.5 `src/web/schemas/`

**`pedidos.py`:** `PedidoDetailSchema.modificado_en` pasa de `fields.DateTime()` a `fields.Function(version_de)`. Así el string que ve el cliente es exactamente el que se compara. Lo ve también el Jefe en `/api/requests/{id}`, sin cambio de forma.

**`autorizaciones.py`:**

```python
class AutorizarInputSchema(Schema):
    class Meta:
        unknown = RAISE

    modificado_en = fields.String(required=True, validate=validate.Length(min=1))
    solicitudes = fields.List(fields.Nested(DecisionSchema), required=True)

    @validates("solicitudes")
    def validate_sin_repetidas(self, value, **kwargs):  # ya existe
        ...

    @post_load
    def a_revision(self, data, **kwargs) -> RevisionAutorizacion:
        return RevisionAutorizacion(
            version=VersionPedido(data["modificado_en"]),
            decisiones=MappingProxyType({d["id"]: d["autorizada"] for d in data["solicitudes"]}),
        )
```

- Que vengan todas las solicitudes del pedido **no** se valida acá: el schema no conoce el pedido. Lo hace `revisar_pedido` (400 con `field="solicitudes"`).
- Una lista vacía pasa el schema y da 400 por faltantes en el dominio. Se acepta.

### 5.6 `src/web/controlers/api_autorizaciones.py`

```python
@api_autorizaciones_bp.put("/<int:request_id>")
@permission_required("autorizacion_update")
def authorize(request_id: int):
    revision = autorizar_input_schema.load(request.get_json())
    pedido = revisar_pedido(request_id, g.current_user.id, revision, reloj.ahora())
    return jsonify(_detalle(pedido)), 200


@api_autorizaciones_bp.post("/<int:request_id>/reject")
@permission_required("autorizacion_reject")
def reject(request_id: int):
    data = rechazar_input_schema.load(request.get_json(silent=True) or {})
    pedido = rechazar_pedido(request_id, g.current_user.id, data["motivo"])
    return jsonify(pedido_detail_schema.dump(pedido)), 200
```

- `_detalle(pedido)` arma la salida igual que `show`: entregas, `acciones_permitidas` con `reloj.hoy()` y `dump_detalle_autorizacion`. `show` pasa a usarla también, para no repetir las tres líneas.
- El 404 del PUT ahora lo da el dominio (`NotFoundError` → 404 con "El pedido no existe."). Se mantiene el mismo mensaje que el GET.
- La respuesta del reject no cambia en S3 (S4).

### 5.7 `admin/docs/openapi.yaml`

- `PUT /authorizations/{request_id}`:
  - Body: `modificado_en` (string, requerido): "Versión del pedido tal como la devolvió el GET de detalle. Es un valor opaco: se devuelve sin modificar".
  - `solicitudes`: requerido, todas las del pedido.
  - Respuesta 200: `AuthorizationDetail`.
  - 400: body inválido, solicitudes faltantes, ajenas o repetidas.
  - 404.
  - 409: pedido rechazado, ya autorizado, de un período anterior, con entregas, o versión desactualizada.
- `RequestDetail.modificado_en`: sigue como string, con la descripción de la versión opaca. Se quita el "(S3)" de la descripción de S2.

### 5.8 Tests

Se usan los fixtures y helpers existentes (`client`, `data`, `login_as`, `crear`, `set_estado`, `pedido`, `detalle`).

Para los tests de dominio, `ahora` se pasa directo. Para los de API, se usa `monkeypatch.setattr(reloj, "ahora", lambda: T)` y, si hace falta, `reloj.hoy`.

Helper nuevo en el archivo de tests, para no repetir el body:

```python
def put_todas(client, pedido_id, decidir=lambda s: True, version=None):
    """PUT con todas las solicitudes del pedido y la versión actual (salvo que se pase otra)"""
    actual = detalle(client, pedido_id)
    body = {"modificado_en": version or actual["modificado_en"],
            "solicitudes": [{"id": s["id"], "autorizada": decidir(s)} for s in actual["solicitudes"]]}
    return client.put(f"/api/authorizations/{pedido_id}", json=body)
```

**Tests existentes que se reescriben** (hoy mandan bodies parciales, sin versión, o llaman a `autorizar_pedido`):

- `test_core_autorizar_marca_cada_solicitud`
- `test_core_autorizar_solicitud_ajena_no_cambia_nada`
- `test_core_no_se_revisa_dos_veces`
- `test_authorize_by_meal_and_returns_totals`
- `test_authorize_with_foreign_request_returns_400`
- `test_authorize_with_invalid_body_returns_400` (agregar los casos sin `modificado_en` y con `modificado_en: ""`)
- `test_authorize_reviewed_request_returns_409`
- `test_authorize_unknown_returns_404`
- `test_sector_head_sees_authorized_amounts`
- `test_modificado_en_cambia_al_autorizar_y_rechazar`
- Cualquier otro de `test_api_autorizaciones.py`, `test_api_entregas.py` o `test_api_resumenes.py` que use `autorizar_pedido` (buscar con `grep -rn autorizar_pedido tests`).

**Tests nuevos** (`tests/unit/test_api_autorizaciones.py`):

| Test | Preparación | Esperado |
|---|---|---|
| `test_put_faltan_solicitudes_400` | Body con todas menos una | 400, `fields.solicitudes` menciona el id faltante; el pedido sigue confirmado |
| `test_put_solicitud_ajena_400` | Todas + un id de otro pedido | 400 con el id ajeno |
| `test_put_version_vieja_409` | `version` = string distinto del actual | 409; el pedido sigue confirmado y sin filas de historial |
| `test_put_jefe_cambio_personas_409` | GET como alimentación (guardar versión), el Jefe modifica solo las personas con `ahora = T2`, PUT con la versión vieja | 409 (aunque los ids viejos ya no existan: la versión va antes que la cobertura) |
| `test_put_dia_anterior_409` | Pedido de D, `ahora` en D+1 | 409 con el mensaje de período anterior |
| `test_put_autorizado_corregible_409` | Autorizar, después otro PUT con la versión nueva | 409 "ya fue autorizado" (cambia en S5) |
| `test_put_rechazado_409` | Rechazar, después PUT | 409 "fue rechazado" |
| `test_put_crea_historial` | PUT con dos `true` y una `false`, `ahora = T` | 3 filas `tipo=autorizacion`, `autorizada_antes = None`, `autorizada_despues` según el body, `momento == T`, `usuario_id` de alimentación |
| `test_put_no_cambia_lo_pedido` | Autorizar parcial | `totales` igual que antes del PUT; `autorizados` con lo marcado |
| `test_put_devuelve_detalle_de_autorizacion` | Autorizar de hoy | 200 con `acciones == {autorizar: false, corregir: true, rechazar: false, motivo_bloqueo: null}`, `revision` con el usuario, `modificado_en` distinto del anterior |
| `test_core_revisar_es_todo_o_nada` | `revisar_pedido` con un ajeno | `BusinessRuleError`; ninguna solicitud cambió y no hay historial |

El 401 y el 403 del PUT ya están cubiertos. **No se duplican.**

## 6. Portal (`code/portal/src/modules/autorizacion/`)

### 6.1 Archivos

```
modules/autorizacion/
├── seleccion.js                           (nuevo) seleccionInicial, contarSeleccion, totalesAAutorizar
├── composables/useSeleccionSolicitudes.js (nuevo)
├── services/autorizacionService.js        (modificado) autorizarPedido
├── components/MatrizSolicitudes.vue       (modificado) modo edición con checkboxes
├── components/TotalesComparados.vue       (modificado) columna "A autorizar"
└── views/DetallePedidoView.vue            (modificado) handler de "Autorizar"
```

Igual que en S1 y S2:

- No se tocan `router/`, `constants/`, `api/` ni otros módulos.
- No se crea `domain/`: `seleccion.js` vive en la raíz del módulo, como `filtros.js`.
- `seleccion.js` no importa nada de Vue: son funciones puras. S4 puede agregarle Vitest.

### 6.2 `seleccion.js`

```js
/** @typedef {Map<number, boolean>} Seleccion  solicitudId → autorizar */

/** regulares en true, excepciones en false. es una sugerencia de la interfaz, no una regla */
export function seleccionInicial(dias) { ... }

/** cuántas solicitudes quedan marcadas, en total y por comida */
export function contarSeleccion(dias, seleccion) { ... }

/** { [comida]: { total, regular, excepcion, porMenu } } con lo marcado, misma forma que `pedidas` */
export function totalesAAutorizar(dias, seleccion) { ... }
```

- Todas reciben `dias` (la forma de S2) y no modifican nada: devuelven valores nuevos.
- `totalesAAutorizar` devuelve **la misma forma** que `pedidas`/`autorizadas`, así `TotalesComparados` la muestra con el mismo código. Una comida con solicitudes pedidas y ninguna marcada tiene `total: 0` (no falta la clave).
- Si `seleccion` no tiene un id de `dias`, lanza `Error` (no asume `false`): es un bug.

### 6.3 `composables/useSeleccionSolicitudes.js`

```js
export function useSeleccionSolicitudes(pedido /* Ref<PedidoDetalle|null> */) {
  const seleccion = shallowRef(new Map())
  watch(pedido, (p) => { seleccion.value = p ? seleccionInicial(p.dias) : new Map() }, { immediate: true })

  function alternar(solicitudId) { /* nuevo Map con el valor invertido */ }
  const aAutorizar = computed(() => pedido.value && totalesAAutorizar(pedido.value.dias, seleccion.value))
  const conteo = computed(() => pedido.value && contarSeleccion(pedido.value.dias, seleccion.value))

  return { seleccion, alternar, aAutorizar, conteo }
}
```

- `shallowRef` + un `Map` nuevo en cada cambio, en vez de mutar el `Map`: la reactividad no depende de que Vue siga las mutaciones de un `Map`.
- Cada vez que llega un pedido nuevo (carga inicial o recarga por 409), la selección se rearma desde cero.

### 6.4 `services/autorizacionService.js`: `autorizarPedido(id, version, seleccion)`

- `PUT /authorizations/{id}` con `{ modificado_en: version, solicitudes: [...seleccion].map(([id, autorizada]) => ({ id, autorizada })) }`.
- La respuesta pasa por `aPedidoDetalle` (S2): si viene mal, tira `Error`.
- `version` es `pedido.modificadoEn` tal como lo dio el parser de S2 (string sin tocar). **No** se formatea ni se convierte a `Date`.

### 6.5 `components/MatrizSolicitudes.vue`

- Props nuevas: `editable` (bool) y `seleccion` (`Map` o `null`). Emite `alternar(solicitudId)`.
- Con `editable`, cada celda con solicitud muestra, además del menú y la condición:
  - Un `<input type="checkbox">` con `:checked="seleccion.get(id)"`.
  - `aria-label` "Autorizar {comida} de {Apellido}, {Nombre}".
  - Un `<label>` que cubra la celda, para que el área de clic sea grande.
- Una celda desmarcada en edición lleva el mismo estilo atenuado que "No autorizada" de S2.
- Pie (`tfoot`): en edición muestra `pedidas / a autorizar` por comida, con el formato de la bandeja.
- `editable` y `revisado` no son `true` a la vez en S3. S5 los combina.

### 6.6 `components/TotalesComparados.vue`

- Prop nueva: `aAutorizar` (misma forma que `pedidas`, o `null`).
- Si no es `null`, agrega la columna **"A autorizar"** después de "Excepción":
  - En la fila de cada comida, el total.
  - En cada menú, `aAutorizar[comida].porMenu[menu] ?? 0`.
  - En el `tfoot`, la suma.
- "Autorizadas" y "A autorizar" no se muestran juntas en S3: un pedido pendiente no tiene autorizadas.

### 6.7 `views/DetallePedidoView.vue`

- `const { seleccion, alternar, aAutorizar, conteo } = useSeleccionSolicitudes(pedido)`.
- `editable = computed(() => pedido.value?.acciones.autorizar === true)`. La vista no decide la regla: la toma de la API.
- Se pasa `editable`, `seleccion` y `@alternar` a cada `MatrizSolicitudes`, y `aAutorizar` a `TotalesComparados` (solo si `editable`).
- `enviando = ref(false)`. Los tres botones (Autorizar, Rechazar y Corregir) tienen `:disabled="enviando"`.
- Handler de "Autorizar" (reemplaza el vacío `// S3` de S2):

```js
async function autorizar() {
  const p = pedido.value
  const ok = await confirm({
    title: 'Autorizar pedido',
    message: textoConfirmacion(p, conteo.value),   // un solo párrafo, ver abajo
    confirmLabel: 'Autorizar',
  })
  if (!ok) return
  enviando.value = true
  try {
    await autorizarPedido(p.id, p.modificadoEn, seleccion.value)
    notifications.success(`Pedido de ${p.sector} autorizado: ${conteo.value.total} de ${totalPedidas} solicitudes.`)
    volverABandeja()          // la misma lógica del link "Volver" de S2, extraída a una función
  } catch (error) {
    if (!(error instanceof ApiError)) throw error
    if (error.type === ERROR_TYPES.CONFLICT) {
      notifications.warning(error.message)
      await load()
    } else if (error.type === ERROR_TYPES.VALIDATION) {
      notifications.error(error.message)
    } else {
      throw error
    }
  } finally {
    enviando.value = false
  }
}
```

- **`textoConfirmacion`**: `ConfirmDialog` muestra el mensaje en un `<p>`, así que va en un solo párrafo, por ejemplo "Se van a autorizar 25 de 29 solicitudes (desayuno 7 de 8, almuerzo 8 de 9, merienda 5 de 6, cena 5 de 6). Las 4 restantes quedan como no autorizadas." Si son todas: "Se van a autorizar las 29 solicitudes." Si son ninguna: "No se va a autorizar ninguna solicitud: las 29 quedan como no autorizadas."
- **Volver a la bandeja:** se extrae la lógica de S2 (`destino` + `router.back()` si la anterior es la bandeja) a una función `volverABandeja()`, que usan el link y el handler. El link sigue funcionando igual que en S2.
- **Errores que no son `ApiError`** (por ejemplo, el parser de la respuesta) o con otro tipo (401, 403 y 503 los maneja el cliente HTTP): se relanzan. No hay catch genérico.
- El mensaje del 409 es el de la API ("El pedido cambió mientras se revisaba…", "El pedido es de un período anterior…", etc.). Después del `load()`, la vista muestra lo que corresponda: otra vez editable, o bloqueado con su aviso de S2.

## 7. Verificación

### 7.1 Automática

```sh
cd code/admin && poetry run pytest          # los tests nuevos y reescritos en verde; las 4 fallas previas de SQLite siguen igual (ver reportes S1 y S2)
cd code/portal && npm run lint && npm run build   # el módulo sin errores; los 3 de carga-pedidos son previos
```

### 7.2 Manual desde el portal

Preparación (en `code/admin`). **Hay cambio de esquema** (tabla nueva y columnas con zona), así que el reset es obligatorio:

```sh
poetry run flask --app web reset-db
USER_NAME=admin@test.com USER_PASS=admin1234 poetry run flask --app web seeds
poetry run flask --app web run --debug
# en otra terminal: cd code/portal && npm run dev
```

Usuarios: `alimentacion@test.com` / `alim1234` y `alimentacion2@test.com` / `alim1234`. Datos: sección S3 de `seeds-per-slice.md`.

| # | Acción | Esperado |
|---|---|---|
| 1 | Seeds | Terminan sin error. Los pedidos 1, 2, 6, 7 y 12 quedan autorizados con las mismas "No aut." que la tabla general de los seeds |
| 2 | Abrir el pedido 4 | Checkboxes en todas las celdas con solicitud: las 19 regulares marcadas, las 10 excepciones desmarcadas. Totales con la columna "A autorizar": desayuno 6, almuerzo 6, merienda 4, cena 3, total 19 |
| 3 | En el 4, marcar la excepción 27200007 del desayuno | "A autorizar" del desayuno pasa a 7 y el total a 20, sin recargar. El pie de la matriz muestra `8 / 7` en el desayuno |
| 4 | Abrir el pedido 5, dejar la selección inicial y pulsar "Autorizar" | La confirmación dice "Se van a autorizar 10 de 14 solicitudes (…). Las 4 restantes quedan como no autorizadas." Cancelar no hace nada |
| 5 | Confirmar | Toast "Pedido de Quirófano autorizado: 10 de 14 solicitudes." Vuelve a la bandeja de hoy con los mismos filtros. El 5 figura como Autorizado con `14 / 10` en total |
| 6 | Abrir otra vez el 5 | Solo lectura, sin checkboxes. Lo pedido sigue siendo 14. Las 4 excepciones dicen "✗ No autorizada". Se ve "Revisado por". Solo el botón "Corregir autorización" |
| 7 | Pedido 9 en dos pestañas (`alimentacion` en una ventana normal y `alimentacion2` en una privada). Autorizar en la primera | Éxito en la primera |
| 8 | En la segunda, sin recargar, pulsar "Autorizar" y confirmar | Toast warning "El pedido ya fue autorizado." y el detalle se recarga: ya sin checkboxes, con lo autorizado por la primera |
| 9 | Pedido 11 abierto como alimentación. En otra ventana, `jefe.medicos@test.com` le quita una persona. Volver a la primera y autorizar | 409 "El pedido cambió mientras se revisaba…". Al recargar, la persona ya no está y la selección se rearma |
| 10 | Bandeja de D-1, abrir el 3 | Sin checkboxes ni botones, con el aviso "Período anterior: solo lectura" (S2) |
| 11 | `curl` PUT al pedido 3 con todas sus solicitudes y su versión | 409 con el mensaje de período anterior |
| 12 | `curl` PUT al 10 con 3 de sus 4 solicitudes y la versión correcta | 400 "Faltan las solicitudes [...]". El 10 sigue pendiente |
| 13 | `curl` PUT al 10 con sus 4 + un id del 9 | 400 "No pertenecen al pedido: [...]" |
| 14 | Autorizar el 10 desde el portal con todo marcado | La confirmación dice "Se van a autorizar las 4 solicitudes." Éxito |
| 15 | Consultar `autorizacion_cambios` (`psql` o `flask shell`) | Filas `tipo=autorizacion` de los pedidos autorizados en esta prueba y de los seeds, con `autorizada_antes` nulo |
| 16 | Con DevTools, pulsar "Autorizar" en un pedido pendiente y mirar el botón durante la petición (red en "Slow 3G") | Los tres botones quedan deshabilitados hasta la respuesta |

Para los `curl`, el token se obtiene con el login de la API (ver `reporte-s1-bandeja-diaria.md` o `openapi.yaml`). La versión sale del `modificado_en` del `GET /api/authorizations/{id}`.

### 7.3 Criterio de terminado

- Los pasos 7.1 y 7.2 pasan completos, **incluidos los pasos de navegador**.
- La rama está mergeada en local a `feature/autorizacion-pedidos` y **no** está pusheada.
- `seeds-per-slice.md` sigue siendo correcto (paso 1). Si algo cambió, se actualiza.
- Está escrito el reporte en `documentacion-personal/reporte-s3-autorizar-solicitudes.md` con la plantilla del artefacto: encabezado; alcance; cambios (con "base de datos: tabla `autorizacion_cambios`, columnas `revisado_en` y `autorizado_en` con zona, aplicado con reset-db"); prueba manual con el resultado de cada paso; pendientes y decisiones.

## 8. Pendientes para otras slices

- **S4:**
  - Reescribir `rechazar_pedido` con `ahora` como parámetro, la guarda de `acciones_permitidas` e historial `tipo=rechazo`.
  - Borrar `get_pedido_para_revisar`.
  - Atajos masivos: "solo regulares" es `seleccionInicial`; agregar "todo", "ninguno", por comida y por persona en `seleccion.js`.
  - Completar el handler de "Rechazar".
- **S5:**
  - `revisar_pedido` acepta autorizados con `corregir` (historial `tipo=correccion`; el 409 por entregas ya sale de la guarda).
  - Cambiar el caso `Disponibles(autorizar=False)` de `_exigir_autorizar`.
  - Selección inicial = estado actual. `editable` y `revisado` a la vez en la matriz.
  - `GET /history` sobre `autorizacion_cambios`.
- **Coordinación con Carga:**
  - `update_pedido` del Jefe no bloquea la fila. Hay una carrera chica: el Jefe guarda entre el `SELECT ... FOR UPDATE` de la autorización y su commit. Su `UPDATE` espera al commit de la autorización, pero después pisa las solicitudes de un pedido ya autorizado. Se resuelve si `update_pedido` también bloquea la fila y verifica el estado después del bloqueo. Avisar al que trabaje en Carga.
  - S3 cambia la firma de `_cerrar_revision` y el tipo de `revisado_en`/`autorizado_en`. Verificar que Carga y Entregas no lean esas columnas como fechas sin zona.
