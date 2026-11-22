# Plan S5: Corrección y días anteriores (Módulo de Autorización)

> **BORRADOR v1 (19/11/2026).** Decisiones cerradas. Los textos del aviso de corrección y del panel de historial se ajustan visualmente durante la implementación.
>
> Plan de implementación de la quinta slice del "Plan de desarrollo — Módulo de Autorización" (artefacto). Con S5 se cierra el circuito mínimo S1–S5.
>
> Referencias: `documentacion-personal/plan-s4-atajos-y-rechazo.md` y `reporte-s4-atajos-y-rechazo.md` (slice anterior), `plan-s3-autorizar-solicitudes.md` y `reporte-s3-autorizar-solicitudes.md`, `documentacion-personal/seeds-per-slice.md` (sección S5), `documentacion-personal/resumen_portal_y_core.md` y `Etapa_1y2_Trabajo Integrador_2026_PUBLIC.md` (Módulo de Autorización, Notas, y Ampliaciones de la API: "PUT /api/authorizations/{request_id} — corrección de autorizaciones y días anteriores").
>
> **Precondición:** S4 está terminada y mergeada en `feature/autorizacion-pedidos` (con su reporte escrito). Este plan usa los nombres del código de S3 y S4 tal como se planificaron:
>
> - En `core/autorizacion.py`: `revisar_pedido`, `rechazar_pedido`, `_bloquear`, `_exigir(acciones, accion)`, `_habilitada`, `_exigir_version`, `_exigir_cobertura`, `MENSAJES_BLOQUEO`, `version_de`, `VersionPedido`, `RevisionAutorizacion`, `RechazoPedido`, `parse_motivo`, `_cerrar_revision(pedido, estado, usuario_id, ahora)`, `_commit`, `Disponibles`, `Bloqueado`, `_REVISAR` y `_CORREGIR`.
> - `_detalle` en `api_autorizaciones.py`, y `dump_detalle_autorizacion` y `_dump_revision` en `web/schemas/autorizaciones.py`.
> - En el portal:
>   - `seleccion.js` con `seleccionInicial`, `soloRegulares`, `todas`, `ninguna`, `idsDeComida`, `idsDePersona`, `estadoGrupo`, `alternarGrupo`, `contarSeleccion`, `totalesAAutorizar` y el helper interno `marcada`.
>   - `seleccion.test.js` y Vitest (`npm test`).
>   - `useSeleccionSolicitudes`, `AtajosSeleccion.vue`, `DialogoRechazo.vue`, `MatrizSolicitudes.vue` (con la columna "Todas" y la fila "Todos") y `TotalesComparados.vue` (prop `aAutorizar`).
>   - En `DetallePedidoView.vue`: `volverABandeja`, `enviando`, `conteo`, `textoConfirmacion` y el `corregir()` vacío (`// S5`).
> - En los tests: `put_todas`, `detalle`, `post_rechazo`, `rechazo_de`, `revision_de`, `cambios_de`, `fijar_ahora`, `fijar_hoy`, `EN_DIA` y el fixture `pedido` en `tests/unit/test_api_autorizaciones.py`, y `autorizar`, `crear` y `set_estado` en `tests/unit/test_api_pedidos.py`.
>
> Si algo de esto no coincide con el código, **manda el código**: se adapta el plan sin renombrar lo existente. Si S4 no está mergeada, **no se empieza**: se avisa al usuario.

## 1. Objetivo

Hoy (S4) un pedido autorizado muestra el botón "Corregir autorización", pero el botón no hace nada, y el PUT sobre un autorizado responde 409 "El pedido ya fue autorizado." Además, la tabla `autorizacion_cambios` se escribe pero no se puede consultar.

Al terminar S5, el usuario de Alimentación:

- En un pedido autorizado de hoy o del futuro, y sin comidas entregadas, pulsa **"Corregir autorización"**:
  - La matriz pasa a edición con checkbox. La selección arranca con **lo autorizado hoy**: cada solicitud marcada si `autorizada == true`.
  - Arriba de los totales aparece un aviso: "Estás corrigiendo la autorización. Lo pedido no cambia." y un contador vivo de cambios ("3 cambios").
  - Los atajos de S4 (barra "Todo / Solo regulares / Ninguno", columna "Todas" y fila "Todos") funcionan igual.
  - `TotalesComparados` muestra Pedidas, Autorizadas (lo actual) y **"Quedan autorizadas"** (la selección).
  - La barra de acciones cambia a **[Guardar corrección] [Cancelar corrección]**. "Guardar corrección" está deshabilitado si no hay cambios o si `enviando`.
- "Cancelar corrección" vuelve a la vista de solo lectura y descarta la selección, **sin confirmar**.
- "Guardar corrección" abre una confirmación (`useConfirm`): "Se van a cambiar 3 solicitudes: 2 pasan a autorizadas y 1 a no autorizada. Quedan autorizadas 7 de 8."
  - Si sale bien: toast de éxito y **vuelve a la bandeja** con `volverABandeja()`, igual que después de autorizar y de rechazar.
  - Si da 409 (otro usuario corrigió, el Comedor entregó una comida, o pasó la medianoche): toast `warning` con el mensaje de la API, sale del modo corrección y recarga el detalle.
- En **todo pedido revisado** (autorizado o rechazado, también los de días anteriores) ve, debajo de las matrices, una sección plegable **"Historial de cambios"**. La primera vez que la abre, el portal pide `GET /api/authorizations/{id}/history`. Muestra cada operación (fecha y hora, usuario y tipo: Autorización, Corrección o Rechazo) con la lista de solicitudes que cambió: "Zapata, Ezequiel · almuerzo: No autorizada → Autorizada".
- El dato "Revisado por" pasa a **"Última revisión: {usuario}, {fecha y hora}"**, porque con correcciones la revisión vigente puede ser de otra persona.
- Los días anteriores siguen en solo lectura con el aviso "Período anterior: solo lectura." (ya funciona desde S2/S3: en S5 se **verifica**, no se reescribe).

En la API:

- `PUT /api/authorizations/{id}` con el **mismo body** de S3 (`{ modificado_en, solicitudes }`) sirve para las dos operaciones. La API **deduce** si es autorización inicial o corrección a partir de `acciones_permitidas`:
  - confirmado → autorización (`tipo=autorizacion`);
  - autorizado corregible → corrección (`tipo=correccion`);
  - bloqueado → 409 con el motivo (día anterior, con entregas o rechazado).
- Una corrección que **no cambia ninguna solicitud** da **400**.
- En la corrección, solo las solicitudes que cambian actualizan `autorizado_por` y `autorizado_en`. El pedido actualiza `revisado_por`, `revisado_en` y `modificado_en` (la última revisión).
- `POST /reject` sobre un autorizado sigue dando 409, pero ahora el mensaje sugiere corregir.
- `GET /api/authorizations/{id}/history` (nuevo, permiso `autorizacion_show`) devuelve el historial agrupado por operación.
- Los seeds dejan el pedido 12 con una **corrección hecha por `alimentacion2`**, así el historial tiene una fila `tipo=correccion` desde el arranque.

**Criterio "Listo cuando" del artefacto:** "se corrige un pedido y el historial muestra usuario, hora y valor anterior".

**Trazabilidad que queda** (acordado con el usuario): cada fila de `autorizacion_cambios` es una solicitud concreta (persona, comida y día) con `autorizada_antes → autorizada_despues`, `usuario_id`, `momento` y `tipo`. Se registra **qué** cambió, **quién** y **cuándo**, solicitud por solicitud. No quedan registradas: las solicitudes que una operación no cambió, los cambios del Jefe sobre el pedido (eso es de Carga, y el Jefe solo modifica pedidos confirmados, que no tienen historial) ni el texto del motivo de rechazo (está en `pedido.motivo_rechazo`, y como el rechazo es final hay uno solo).

Requisitos del enunciado que cubre:

- "Corregir la autorización de un pedido ya autorizado, en las condiciones indicadas en las Notas."
- Notas: "Una autorización puede corregirse mientras el pedido sea de la fecha actual o de una fecha futura y ninguna de sus comidas se haya entregado. Cada corrección conserva las cantidades pedidas y queda registrada con el usuario, la fecha y la hora."
- "Consultar períodos anteriores en modo de solo lectura."
- Ampliaciones del PUT:
  - "cada corrección conserva las cantidades pedidas, recalcula las autorizadas y registra el usuario, la fecha, la hora y el estado anterior de cada solicitud modificada";
  - "Los pedidos de días anteriores no pueden autorizarse ni corregirse, y un pedido rechazado no puede autorizarse";
  - "el 409 de la Etapa 1 deja de aplicarse a los pedidos ya autorizados. Ahora corresponde 409 si el pedido es de un día anterior, si alguna de sus comidas ya fue entregada o si el pedido fue rechazado".
- Ampliaciones del reject: "Para dejar sin efecto un pedido ya autorizado se corrige su autorización marcando las solicitudes como no autorizadas."
- "Pedir confirmación antes de autorizar, corregir […]".
- "registrar el historial de las operaciones realizadas" (introducción del enunciado).

## 2. Decisiones

| Tema | Decisión | Motivo |
|---|---|---|
| Cómo sabe el PUT si es corrección | Lo **deduce** de `acciones_permitidas`. El body no cambia (sin flag `correccion`) | Si otro usuario autorizó entre el GET y el PUT, la versión ya no coincide y da 409. No hace falta que el cliente declare la intención |
| Guarda del PUT | Nueva `_tipo_de_revision(acciones) -> TipoRevision`, con `TipoRevision = Literal["autorizacion", "correccion"]`. `match`: `Disponibles(autorizar=True)` → `"autorizacion"`; `Disponibles(corregir=True)` → `"correccion"`; `Bloqueado(motivo=m)` → `ConflictError(MENSAJES_BLOQUEO[m])`; otro → `TypeError`. El PUT deja de usar `_exigir` | "Parse, don't validate": la guarda devuelve el tipo de operación que el resto de la función usa, en vez de un chequeo booleano |
| `_exigir` | Queda solo para el reject (`accion: Literal["rechazar"]`, o se mantiene el `Literal` de dos valores si simplifica). El caso `Disponibles` sin la acción lanza `ConflictError("El pedido ya fue autorizado: para dejarlo sin efecto, corregí su autorización.")` | Lo prometido en S4: el botón de corrección ya funciona, así que el mensaje puede sugerirlo |
| Orden de los chequeos del PUT | 404 → bloqueo de fila → guarda (409) → versión (409) → cobertura (400) → **sin cambios (400, solo corrección)** | Igual que S3 más el chequeo nuevo al final. "Sin cambios" necesita la cobertura completa para comparar |
| Corrección sin cambios | `BusinessRuleError("La corrección no cambia ninguna solicitud.", field="solicitudes")` → 400. No se escribe nada ni se mueve `modificado_en`. En el portal, "Guardar corrección" queda deshabilitado mientras no haya cambios | Decisión acordada. Una revisión vacía no aporta nada al historial |
| Autorización inicial con "sin cambios" | No aplica: en un confirmado todas las solicitudes pasan de `NULL` a un booleano, así que siempre cambian todas | — |
| `autorizado_por` / `autorizado_en` | En la **corrección**, solo las solicitudes cuyo valor cambia. En la **autorización inicial**, todas (como en S3). El pedido actualiza `revisado_por`, `revisado_en` y `modificado_en` siempre (`_cerrar_revision`) | Decisión acordada. Cada solicitud conserva quién decidió su valor actual; el pedido refleja la última revisión |
| Historial de la corrección | `tipo="correccion"`, una fila por solicitud cuyo valor cambia, con `autorizada_antes` (booleano, nunca `NULL` en una corrección) y `autorizada_despues` | Mismo criterio que S3 y S4. El CHECK de `tipo` ya admite `correccion` |
| Estado después de corregir | Sigue `autorizado`, aunque queden 0 autorizadas | El enunciado: un autorizado se deja sin efecto corrigiendo, no rechazando |
| Endpoint de historial | `GET /api/authorizations/{id}/history`, permiso **`autorizacion_show`** (reusado) | Decisión acordada: quien ve el detalle ve el historial. No se tocan los permisos de los seeds |
| Forma del historial | **Agrupado por operación** en la API: `{ items: [{ momento, usuario, tipo, cambios: [...] }] }`. Clave de grupo `(momento, usuario_id, tipo)`. Orden: la operación más nueva primero. Dentro de cada operación, los cambios en el orden de `ordenar_solicitudes` (el del detalle) | Decisión acordada. El portal no reagrupa, y "una corrección" es una unidad |
| Paginación del historial | No. Un pedido sin historial devuelve `items: []` (200), no 404 | El historial de un pedido es acotado: a lo sumo una autorización, unas pocas correcciones o un rechazo |
| Dominio del historial | `historial_de(pedido_id) -> list[OperacionHistorial]` en `core/autorizacion_cambio.py`. `OperacionHistorial` y `CambioSolicitud` son `@dataclass(frozen=True)`. 404 si el pedido no existe | `core` arma los grupos. El schema solo serializa |
| Después de corregir | Toast de éxito y `volverABandeja()` | Decisión acordada, igual que autorizar y rechazar |
| Cancelar corrección | Botón "Cancelar corrección": `corrigiendo = false`. Sin confirmación aunque haya cambios | Decisión acordada. El aviso de cambios sin guardar sigue fuera de alcance |
| Diferencias en la edición | **Solo contador y totales**: aviso con el número de cambios y columna "Quedan autorizadas". **No** se marca cada celda que cambia | Decisión acordada |
| Selección al corregir | `seleccionActual(dias)`: cada solicitud con su `autorizada` actual. Si alguna es `null`, `Error` (un autorizado no tiene pendientes) | La corrección parte de lo vigente, no de la sugerencia "regulares sí, excepciones no" |
| Modo de la selección | `useSeleccionSolicitudes(pedido, modo)` con `modo` un `Ref<'autorizar' \| 'corregir' \| null>`. Un `watch` con `flush: 'sync'` sobre `[pedido, modo]` rearma la selección | Al entrar o salir de la corrección, y al recargar, la selección arranca de cero con la fuente correcta |
| "Revisado por" | Pasa a "Última revisión" con `{usuario}, {fecha y hora}` (`revision.en` ya viene en el detalle desde S3) | Decisión acordada. Con correcciones, el revisor puede cambiar |
| Panel de historial | `HistorialAutorizacion.vue`: `<details>` plegable, carga diferida al abrirse por primera vez. Se muestra si `pedido.revision !== null` | Decisión acordada |
| Seeds | `Autorizado` suma `correccion: frozenset[tuple[str, str]]` (las `(dni, comida)` que se invierten en una corrección posterior). El pedido 12 se corrige con `("29500004", "almuerzo")` por **`alimentacion2`** | Decisión acordada. El historial arranca con una corrección de otro usuario |
| Esquema de base | **Sin cambios.** El `reset-db` + `seeds` se corre igual para regenerar los datos de demo (el 12 cambia) | — |
| Carrera corrección / entrega | **No se resuelve en S5.** `create_entrega` no bloquea la fila del pedido, así que una corrección y una entrega simultáneas pueden pasar las dos | Se anota para coordinar con Entregas (sección 8) |
| Rama | `feat/autorizacion-s5` sale de `feature/autorizacion-pedidos` (con S4 mergeada) y vuelve con merge local `--no-ff` | Igual que S1 a S4 |

## 3. Fuera de alcance

No hacer en S5 (cada ítem tiene su slice o queda como pendiente):

- Marcar visualmente cada celda que cambia en la corrección (decisión acordada: solo contador y totales).
- Aviso de cambios sin guardar al salir del detalle o al cancelar la corrección.
- Rechazar un autorizado: sigue sin permitirse.
- Que la entrega del Comedor bloquee la fila del pedido o mueva `modificado_en` (coordinación con Entregas).
- Historial de los cambios del Jefe sobre el pedido (Carga).
- Historial en la bandeja o entre pedidos, filtros sobre el historial, exportarlo.
- Calendario, vista semanal y navegación que salta días vacíos (S6).
- Navegación por teclado dentro de la matriz y vista móvil (S11).
- Tests de componentes Vue (`@vue/test-utils`). Solo funciones puras.

## 4. Git

```sh
cd code
git switch feature/autorizacion-pedidos
git log --oneline -5          # confirmar que el merge de feat/autorizacion-s4 está
git status                    # árbol limpio antes de empezar
git switch -c feat/autorizacion-s5
```

- Commits chicos por capa, por ejemplo:
  - `feat(core): corrección de autorizaciones en revisar_pedido`
  - `feat(core): historial de autorización agrupado por operación`
  - `feat(core): seeds con una corrección del pedido 12`
  - `feat(api): historial de autorización y mensaje del reject sobre autorizados`
  - `test: corrección, días anteriores e historial`
  - `feat(portal): corregir autorización`
  - `feat(portal): historial de cambios en el detalle`
- Al terminar: `git switch feature/autorizacion-pedidos && git merge --no-ff feat/autorizacion-s5`.
- **No pushear `feat/autorizacion-s5` al origin.** `feature/autorizacion-pedidos` se pushea solo si lo pide el usuario.

## 5. Backend (`code/admin`)

### 5.1 `src/core/autorizacion.py`

```python
TipoRevision = Literal["autorizacion", "correccion"]


def _tipo_de_revision(acciones: AccionesPedido) -> TipoRevision:
    """guarda del PUT: qué operación es, o 409 si el pedido no se puede revisar"""
    match acciones:
        case Disponibles(autorizar=True):
            return "autorizacion"
        case Disponibles(corregir=True):
            return "correccion"
        case Bloqueado(motivo=motivo):
            raise ConflictError(MENSAJES_BLOQUEO[motivo])
        case otro:
            raise TypeError(f"Acciones de pedido desconocidas: {otro!r}")


def revisar_pedido(pedido_id: int, usuario_id: int, revision: RevisionAutorizacion,
                   ahora: datetime) -> Pedido:
    """autorización inicial de un confirmado o corrección de un autorizado. todo o nada.

    orden: 404 -> bloqueo de fila -> guarda (409) -> versión (409) -> cobertura (400) ->
    sin cambios (400, solo corrección)"""
    pedido = _bloquear(pedido_id)                                       # 404
    entregadas = ids_entregados(s.id for s in pedido.solicitudes)
    acciones = acciones_permitidas(pedido, ahora.date(), hay_entregas=bool(entregadas))
    tipo = _tipo_de_revision(acciones)                                  # 409
    _exigir_version(pedido, revision.version)                           # 409
    _exigir_cobertura(pedido, revision.decisiones)                      # 400

    cambiadas = [s for s in pedido.solicitudes
                 if s.autorizada != revision.decisiones[s.id]]
    if tipo == "correccion" and not cambiadas:
        raise BusinessRuleError("La corrección no cambia ninguna solicitud.",
                                field="solicitudes")                    # 400

    for solicitud in cambiadas:
        nueva = revision.decisiones[solicitud.id]
        db.session.add(AutorizacionCambio(
            solicitud_id=solicitud.id, pedido_id=pedido.id, usuario_id=usuario_id,
            momento=ahora, autorizada_antes=solicitud.autorizada,
            autorizada_despues=nueva, tipo=tipo,
        ))
        solicitud.autorizada = nueva
        solicitud.autorizado_por = usuario_id
        solicitud.autorizado_en = ahora
    # en la autorización inicial todas cambian (NULL -> bool), así que `cambiadas`
    # son todas y el comportamiento de S3 se mantiene
    _cerrar_revision(pedido, "autorizado", usuario_id, ahora)
    _commit()
    return pedido
```

Detalles:

- **Verificar** la afirmación del comentario: en un confirmado todas las solicitudes tienen `autorizada is None`. Si no fuera así (por ejemplo, datos viejos), agregar un `assert` o tratar la autorización inicial aparte para que todas reciban `autorizado_por`/`autorizado_en`, como en S3. Un test lo cubre (5.6).
- `_cerrar_revision` no cambia: asigna `estado`, `revisado_por`, `revisado_en` y `modificado_en`.
- **`_exigir`**: queda solo para `rechazar_pedido`. El caso `Disponibles()` sin la acción pasa a:
  ```python
  raise ConflictError(
      "El pedido ya fue autorizado: para dejarlo sin efecto, corregí su autorización.")
  ```
  Si con un solo uso `_habilitada` y el `Literal` de dos valores sobran, se pueden simplificar, pero no es obligatorio. Lo importante es que el comportamiento del reject no cambie salvo el mensaje.
- Actualizar el docstring del módulo y los comentarios que dicen "S5 cambia este caso".
- `acciones_permitidas` **no cambia**: ya devuelve `_CORREGIR` para autorizado + fecha ≥ hoy + sin entregas, y `Bloqueado("con_entregas")` o `Bloqueado("dia_anterior")` en los otros casos.

### 5.2 `src/core/autorizacion_cambio.py`: historial

```python
@dataclass(frozen=True)
class CambioSolicitud:
    solicitud_id: int
    fecha: date
    tipo_comida: str
    apellido: str
    nombre: str
    dni: str
    autorizada_antes: bool | None
    autorizada_despues: bool


@dataclass(frozen=True)
class OperacionHistorial:
    """una autorización, corrección o rechazo: las filas con el mismo momento,
    usuario y tipo"""
    momento: datetime
    usuario: str                       # "Nombre Apellido"
    tipo: TipoCambio
    cambios: tuple[CambioSolicitud, ...]


def historial_de(pedido_id: int) -> list[OperacionHistorial]:
    """historial de autorización del pedido, la operación más nueva primero.
    404 si el pedido no existe; [] si nunca se revisó"""
```

- 404 con `NotFoundError("El pedido no existe.")` (mismo criterio que `_bloquear`: un id `<= 0` también es 404). **Sin** bloqueo de fila: es una lectura.
- Una sola consulta con `join` a `Solicitud`, `Personal` y `User`, filtrada por `AutorizacionCambio.pedido_id`.
- Agrupar por `(momento, usuario_id, tipo)` conservando el orden: operaciones por `momento` descendente (desempate por el menor `id` de la operación, descendente). Dentro de cada operación, los cambios en el orden de `ordenar_solicitudes` (fecha, comida en el orden del día, persona) o, si ese helper vive en `web/schemas/pedidos.py` y no conviene importarlo desde `core`, un orden equivalente en la consulta (`fecha`, orden de comida, `apellido`, `nombre`). Documentar cuál se usó.
- **Ojo con SQLite:** `momento` vuelve sin zona en SQLite y con zona en PostgreSQL (ver `como_instante` en los tests). Agrupar por el valor tal como lo devuelve la base: todas las filas de una operación se escribieron con el mismo `ahora`, así que comparan iguales en los dos motores.
- `usuario` se arma igual que `_dump_revision` (`f"{first_name} {last_name}"`). Si conviene, extraer un helper compartido.

### 5.3 `src/core/seeds.py`

```python
@dataclass(frozen=True)
class Autorizado:
    """... `correccion` son las (dni, tipo_comida) que una corrección posterior,
    hecha por el segundo usuario de Alimentación, invierte"""
    no_autorizadas: frozenset[tuple[str, str]] = frozenset()
    entregas: Mapping[str, tuple[str, ...]] = field(default_factory=dict)
    correccion: frozenset[tuple[str, str]] = frozenset()
```

- Pedido 12: `correccion=frozenset({("29500004", "almuerzo")})`. Queda **1** no autorizada (29500003, almuerzo) y **7** autorizadas.
- En `seed_pedidos_demo`, después de `revisar_pedido` (y antes de las entregas), si `correccion` no está vacío:
  ```python
  revisar_pedido(pedido.id, alimentacion2.id, RevisionAutorizacion(
      version=version_de(pedido),
      decisiones=MappingProxyType({
          s.id: s.autorizada != ((s.personal.dni, s.tipo_comida) in correccion)
          for s in pedido.solicitudes
      }),
  ), ahora=reloj.ahora())
  ```
  - El instante de la corrección es `reloj.ahora()`, que es posterior al de la autorización (el 12 es futuro, así que su `cuando` también fue `reloj.ahora()`, pero leído antes). **Verificar** que la corrección quede estrictamente después; si en algún motor quedaran iguales, el orden del historial lo desempata por id.
  - Una corrección sobre un pedido pasado no pasaría la guarda. Si alguien pone `correccion` en un pedido con `dias_desde_hoy < 0`, lanzar `ValueError` con un mensaje claro en vez de dejar que falle con un 409.
  - Una corrección sobre un pedido con `entregas` tampoco pasaría la guarda si las entregas van antes. Por eso la corrección va **antes** de las entregas, pero igual conviene un `ValueError` si un `PedidoDemo` tiene las dos cosas: no hay caso de uso y confunde.
- Buscar `alimentacion2` con `auth.find_user_by_email("alimentacion2@test.com")` (constante nueva `USUARIO_DEMO_ALIMENTACION_2`), con el mismo `LookupError` si falta.
- Actualizar el comentario del pedido 12 en `PEDIDOS_DEMO` ("Autorizado a futuro, corregido por el segundo usuario: corregible").

### 5.4 `src/web/schemas/autorizaciones.py`

```python
class CambioSolicitudSchema(Schema):
    solicitud_id = fields.Integer()
    fecha = fields.Date()
    tipo_comida = fields.String()
    apellido = fields.String()
    nombre = fields.String()
    dni = fields.String()
    autorizada_antes = fields.Boolean(allow_none=True)
    autorizada_despues = fields.Boolean()


class OperacionHistorialSchema(Schema):
    momento = fields.Method("dump_momento")   # isoformat, como revision.en
    usuario = fields.String()
    tipo = fields.String()
    cambios = fields.List(fields.Nested(CambioSolicitudSchema))
```

- `momento` se serializa con el mismo criterio que `revision.en` en `_dump_revision` (`isoformat()`), para que el portal lo parsee con el mismo `aFechaHora`.
- `historial_output_schema = OperacionHistorialSchema(many=True)`.

### 5.5 `src/web/controlers/api_autorizaciones.py`

```python
@api_autorizaciones_bp.get("/<int:request_id>/history")
@permission_required("autorizacion_show")
def history(request_id: int):
    return jsonify({"items": historial_output_schema.dump(historial_de(request_id))}), 200
```

- El PUT (`authorize`) no cambia: la corrección la decide `revisar_pedido`.

### 5.6 `admin/docs/openapi.yaml`

- `PUT /authorizations/{request_id}`:
  - Descripción: autoriza un pedido confirmado o **corrige** uno autorizado (fecha ≥ hoy y sin comidas entregadas). Mismo body. En la corrección, el historial registra `tipo=correccion` y solo las solicitudes que cambian actualizan `autorizado_por`/`autorizado_en`.
  - 400: suma "la corrección no cambia ninguna solicitud".
  - 409: período anterior, con comidas entregadas, rechazado o versión desactualizada. **Se quita** "ya autorizado".
- `POST /authorizations/{request_id}/reject`: el 409 sobre un autorizado con el mensaje nuevo.
- `GET /authorizations/{request_id}/history` (nuevo): 200 con `AuthorizationHistory` (`items: OperacionHistorial[]`), 401, 403, 404. Describir el agrupamiento y el orden.
- Schemas nuevos `AuthorizationHistory`, `AuthorizationOperation` y `AuthorizationChange`. `tipo` es un enum `autorizacion | correccion | rechazo | generacion`.

### 5.7 Tests

Se usan los fixtures y helpers existentes. **Helpers nuevos** en `tests/unit/test_api_autorizaciones.py`:

```python
def get_historial(client, pedido_id):
    response = client.get(f"/api/authorizations/{pedido_id}/history")
    assert response.status_code == 200, response.get_json()
    return response.get_json()["items"]
```

Para la corrección por API alcanza con `put_todas(client, id, decidir=..., version=...)` después de una primera autorización, con `fijar_ahora` en un instante posterior para distinguir los momentos.

**Tests existentes que se reescriben:**

- `test_put_autorizado_corregible_409` pasa a ser `test_put_corrige_autorizado`: autoriza todo, después corrige con otra decisión (con la versión nueva) y espera **200** con `estado == "autorizado"`. Si el segundo PUT repetía la misma decisión, ahora daría 400 "no cambia ninguna solicitud": cubrirlo en su propio test.
- `test_authorize_reviewed_request_returns_409` (parametrizado por estado): el caso `autorizado` ya no es 409 si es corregible. Dejar `rechazado` y, para `autorizado`, usar uno con entregas o de un día anterior, o moverlo a los tests nuevos.
- `test_reject_autorizado_409` y `test_reject_authorized_request_returns_409`: el mensaje nuevo.
- Cualquier otro que espere "El pedido ya fue autorizado." del PUT (`grep -rn "ya fue autorizado" tests`).
- Los que comprueban los datos de los seeds, si los hay (`grep -rn "seed_pedidos_demo" tests`).

**Tests nuevos:**

| Test | Preparación | Esperado |
|---|---|---|
| `test_put_corrige_autorizado` | Autorizar todo en T, corregir 2 solicitudes a `false` en T2 | 200, `estado == "autorizado"`, `autorizados` recalculados, `totales` (lo pedido) sin cambios |
| `test_correccion_historial_solo_de_las_que_cambian` | Igual | 2 filas `tipo=correccion` con `autorizada_antes is True`, `autorizada_despues is False`, `momento == T2`; las filas `autorizacion` de T siguen intactas |
| `test_correccion_autorizado_por_solo_de_las_que_cambian` | Autorizar con `alimentacion` en T, corregir con un segundo usuario de Alimentación en T2 | Las 2 cambiadas: `autorizado_por` = segundo usuario, `autorizado_en == T2`. Las otras: el primero y T. El pedido: `revisado_por` = segundo, `revisado_en == T2`, `modificado_en == T2` |
| `test_correccion_sin_cambios_400` | Autorizar y después PUT con la misma decisión y la versión nueva | 400 con `fields.solicitudes`; sin filas `correccion`; `modificado_en` igual |
| `test_correccion_version_vieja_409` | Autorizar, corregir con la versión de **antes** de autorizar | 409 "cambió mientras se revisaba"; sin filas `correccion` |
| `test_correccion_cobertura_400` | Autorizar, corregir con una solicitud de menos | 400 "Faltan las solicitudes" |
| `test_correccion_con_entregas_409` | Autorizar, registrar una entrega de una solicitud autorizada (`create_entrega`), corregir | 409 "El pedido ya tiene comidas entregadas."; sin cambios |
| `test_correccion_dia_anterior_409` | Autorizar en D, `ahora` en D+1, corregir | 409 "período anterior"; sin cambios |
| `test_correccion_puede_dejar_todo_sin_autorizar` | Autorizar todo, corregir todo a `false` | 200, sigue `autorizado`, `autorizados` en 0 por comida |
| `test_autorizacion_inicial_sigue_firmando_todas` | Confirmado, autorizar con mezcla de true y false | Todas las solicitudes con `autorizado_por` y `autorizado_en`, y una fila `autorizacion` por cada una (regresión de S3) |
| `test_core_tipo_de_revision` | `_REVISAR`, `_CORREGIR`, cada `Bloqueado` y un valor raro | `"autorizacion"`, `"correccion"`, `ConflictError` con `MENSAJES_BLOQUEO[m]`, `TypeError` |
| `test_detalle_dia_anterior_solo_lectura` | Autorizado de D, `hoy` en D+1, GET del detalle | `acciones == {autorizar: false, corregir: false, rechazar: false, motivo_bloqueo: "dia_anterior"}` |
| `test_historial_pedido_inexistente_404` | id 9999 y 0 | 404 |
| `test_historial_sin_revisar_vacio` | Confirmado | 200, `items == []` |
| `test_historial_agrupa_y_ordena` | Autorizar en T (N solicitudes), corregir 2 en T2 con otro usuario | 2 operaciones: primero la corrección (`tipo`, `usuario` del segundo, `momento` T2, 2 cambios con antes y después), después la autorización (N cambios con `autorizada_antes is None`). Cambios en el orden del detalle |
| `test_historial_rechazo` | Rechazar | 1 operación `tipo == "rechazo"` con todas las solicitudes en `false` |
| `test_historial_jefe_403` | `GET /history` como `jefe_servicio` | 403 (el 401 se suma a la parametrización existente de `test_without_authentication_returns_401` si es fácil) |
| `test_seeds_pedido_12_corregido` (si hay infraestructura para correr los seeds en un test; si no, se verifica a mano en 7.2) | `seed_auth` + `seed_pedidos_demo` | El 12 tiene 1 no autorizada y su historial tiene una operación `correccion` de `alimentacion2` y una `autorizacion` de `alimentacion` |

Para crear el segundo usuario de Alimentación en los tests, usar el patrón que ya exista en `data` o en `test_api_pedidos.py`. Si no hay, agregar uno al fixture con el rol `alimentacion`.

## 6. Portal (`code/portal`)

### 6.1 Archivos

```
src/modules/autorizacion/
├── seleccion.js                                 (modificado) seleccionActual, contarCambios
├── seleccion.test.js                            (modificado) tests nuevos
├── composables/useSeleccionSolicitudes.js       (modificado) parámetro modo, cambios
├── services/autorizacionService.js              (modificado) obtenerHistorial + aHistorial
├── components/TotalesComparados.vue             (modificado) título de la columna por prop
├── components/MatrizSolicitudes.vue             (modificado) editable + revisado a la vez
├── components/HistorialAutorizacion.vue         (nuevo) sección plegable con carga diferida
└── views/DetallePedidoView.vue                  (modificado) modo corrección, historial, "Última revisión"
```

Igual que en S1 a S4:

- No se tocan `router/`, `constants/`, `api/`, `components/` globales ni otros módulos.
- No se crea `domain/`.
- No se agregan dependencias.

### 6.2 `seleccion.js`

```js
/**
 * Selección con lo autorizado hoy: cada solicitud con su `autorizada`.
 * Para corregir un pedido autorizado; una solicitud pendiente (null) es un bug.
 * @returns {Seleccion}
 */
export function seleccionActual(dias) { ... }

/**
 * Cuántas solicitudes cambian respecto de lo autorizado hoy.
 * @returns {{ total: number, aAutorizada: number, aNoAutorizada: number }}
 */
export function contarCambios(dias, seleccion) { ... }
```

- `seleccionActual` tira `Error` si alguna `celda.autorizada` no es booleana.
- `contarCambios` reusa `marcada` (así un id faltante también tira `Error`) y tira `Error` si una celda tiene `autorizada === null`: solo tiene sentido sobre un autorizado.
- `seleccionInicial` no cambia (sigue siendo la sugerencia para la autorización inicial).

### 6.3 `seleccion.test.js`

Suma, con un `dias` de prueba de un pedido autorizado (mezcla de `true` y `false`):

- `seleccionActual` copia `autorizada`, y tira `Error` con una celda `null`.
- `contarCambios`: sin cambios da `{ total: 0, ... }`; después de `alternar` dos celdas en sentidos distintos da 2 / 1 / 1; después de `todas` cuenta solo las que estaban en `false`; tira `Error` con un id faltante.
- `soloRegulares` sobre un autorizado: los atajos de S4 siguen funcionando sobre una selección de corrección.

### 6.4 `composables/useSeleccionSolicitudes.js`

```js
/**
 * @param {Ref<PedidoDetalle|null>} pedido
 * @param {Ref<'autorizar'|'corregir'|null>} modo  null: no se edita
 */
export function useSeleccionSolicitudes(pedido, modo) {
  const seleccion = shallowRef(new Map())
  watch(
    [pedido, modo],
    ([p, m]) => { seleccion.value = desde(p, m) },
    { immediate: true, flush: 'sync' },
  )
  ...
  const cambios = computed(() =>
    pedido.value && modo.value === 'corregir'
      ? contarCambios(pedido.value.dias, seleccion.value) : null)
  return { ..., cambios }
}
```

- `desde(p, m)`: `m === 'autorizar'` → `seleccionInicial(p.dias)`; `m === 'corregir'` → `seleccionActual(p.dias)`; `null` o sin pedido → `new Map()`. Un `modo` desconocido tira `Error`.
- `aAutorizar` y `conteo` se calculan solo si hay `modo` (fuera de la edición devuelven `null`). **Verificar** que el resto de la vista (por ejemplo `resumenRechazo` de S4, que usa `conteo.pedidas`) siga funcionando: con un confirmado el modo es `'autorizar'`, así que `conteo` existe.
- `flush: 'sync'` sigue siendo necesario (ver reporte de S3).

### 6.5 `components/TotalesComparados.vue`

- Prop nueva `etiquetaSeleccion` (string, default `'A autorizar'`). La columna de `aAutorizar` usa ese título.
- En la corrección se pasa `'Quedan autorizadas'`. Las columnas quedan: Pedidas (con regular/excepción como hoy), Quedan autorizadas (la selección, en color primario) y Autorizadas (lo actual). **Verificar** el orden actual de las columnas y mantenerlo; si conviene que "Autorizadas" quede antes que "Quedan autorizadas", ajustarlo solo en modo corrección y documentarlo.

### 6.6 `components/MatrizSolicitudes.vue`

- Hoy el comentario dice "En S3 `editable` y `revisado` no son true a la vez (S5 los combina)". En la corrección se pasan los dos en `true`.
- Con `editable`, la celda muestra el checkbox como en S3/S4 y **no** muestra "✓ Autorizada" / "✗ No autorizada" (lo actual ya está en la columna "Autorizadas" de los totales y el checkbox arranca con ese valor). Revisar las ramas `v-if="revisado"` del template y la función `atenuada`, que ya prioriza `editable`.
- La leyenda del pie en edición: con `revisado` y `editable`, "(quedan autorizadas / pedidas)". Con solo `editable`, "(a autorizar / pedidas)" como hoy.
- Actualizar el comentario del componente.

### 6.7 `services/autorizacionService.js`: `obtenerHistorial(id)`

- `GET /authorizations/{id}/history` y parseo con `aHistorial`:

```js
/**
 * @typedef {{ solicitudId: number, fecha: string, comida: string, apellido: string,
 *             nombre: string, dni: string, antes: boolean|null, despues: boolean }} CambioHistorial
 * @typedef {{ momento: string, usuario: string,
 *             tipo: 'autorizacion'|'correccion'|'rechazo'|'generacion',
 *             cambios: CambioHistorial[] }} OperacionHistorial
 * @returns {OperacionHistorial[]}
 */
```

- Validaciones: `items` es un array; `momento` con `aFechaHora`; `usuario` con `esTexto`; `tipo` uno de los cuatro (constante `TIPOS_CAMBIO` en el servicio o en `filtros.js` con su etiqueta: "Autorización", "Corrección", "Rechazo", "Generación"); `cambios` es un array **no vacío**; cada cambio con `fecha` (`aFecha`), `tipo_comida` en `COMIDAS`, textos no vacíos, `autorizada_antes` booleano o `null` y `autorizada_despues` booleano. Si algo viene mal, `respuestaInvalida`.

### 6.8 `components/HistorialAutorizacion.vue`

- Props: `pedidoId` (number).
- Un `<details>` con `<summary>` "Historial de cambios". Al primer `toggle` con `open`, llama a `obtenerHistorial` con `useAsyncData` (o el patrón de carga que ya usa el módulo) y muestra `LoadingState`, `ErrorState` (con reintentar) o el contenido. No vuelve a pedir al cerrar y abrir.
- Contenido: una lista de operaciones. Cada una tiene un encabezado "{fecha y hora} · {usuario} · {tipo}" (`formatearFechaHora`) y una lista de cambios:
  - "{Apellido}, {Nombre} · {comida}{ · fecha corta si el pedido tiene varios días}: {antes} → {después}"
  - `antes`: "Pendiente" si es `null`, si no "Autorizada" o "No autorizada". `después`: "Autorizada" o "No autorizada".
- Si `items` está vacío: "Todavía no hay cambios registrados." (no debería pasar si se muestra solo con `revision`, pero se cubre).
- La vista lo usa con `:key="pedido.modificadoEn"`, así después de un `load()` el componente se rearma y no muestra un historial viejo.
- Estilos con las clases y variables del design system existentes, sin librerías.

### 6.9 `views/DetallePedidoView.vue`

- Estado nuevo: `const corrigiendo = ref(false)`. Se pone en `false` cada vez que cambia el pedido cargado (watch sobre `props.id`, y después de un 409).
- `modo`:
  ```js
  const modo = computed(() => {
    const acciones = pedido.value?.acciones
    if (acciones?.autorizar) return 'autorizar'
    if (corrigiendo.value && acciones?.corregir) return 'corregir'
    return null
  })
  const editable = computed(() => modo.value !== null)
  ```
- `useSeleccionSolicitudes(pedido, modo)`, que ahora devuelve también `cambios`.
- **Barra de acciones:**
  - Con `corrigiendo`: "Guardar corrección" (`btn--primary`, `:disabled="enviando || cambios.total === 0"`) y "Cancelar corrección" (`btn--secondary`, `:disabled="enviando"`). No se muestran "Corregir autorización", "Autorizar" ni "Rechazar".
  - Sin `corrigiendo`: como en S4.
  - `corregir()` (reemplaza el vacío `// S5`): `corrigiendo.value = true`. Mover el foco al aviso de corrección o a la primera fila de la matriz para que el lector de pantalla note el cambio (con `nextTick`).
  - `cancelarCorreccion()`: `corrigiendo.value = false` y devolver el foco al botón "Corregir autorización".
- **Aviso** (solo con `corrigiendo`), entre el bloque de revisión y los totales: un `alert alert--info` con "Estás corrigiendo la autorización. Lo pedido no cambia." y un `<span aria-live="polite">` con "{n} cambio(s)" (singular y plural; "Sin cambios" con 0).
- `AtajosSeleccion` y la matriz ya dependen de `editable`, así que aparecen en la corrección sin cambios extra. Pasar `:revisado="revisado"` como hoy.
- `TotalesComparados`: `:a-autorizar="editable ? aAutorizar : null"` y `:etiqueta-seleccion="modo === 'corregir' ? 'Quedan autorizadas' : 'A autorizar'"`.
- **Confirmación y envío:**

```js
/** "Se van a cambiar 3 solicitudes: 2 pasan a autorizadas y 1 a no autorizada. Quedan autorizadas 7 de 8." */
function textoCorreccion(cambios, conteo) { ... }   // singular/plural en cada parte; omitir la parte en 0

async function guardarCorreccion() {
  const p = pedido.value
  const ok = await confirm({
    title: 'Corregir autorización',
    message: textoCorreccion(cambios.value, conteo.value),
    confirmLabel: 'Guardar corrección',
  })
  if (!ok) return
  enviando.value = true
  try {
    await autorizarPedido(p.id, p.modificadoEn, seleccion.value)   // mismo PUT
    notifications.success(`Autorización de ${p.sector} corregida: ${cambios.value.total} cambio(s).`)
    volverABandeja()
  } catch (error) {
    if (!(error instanceof ApiError)) throw error
    if (error.type === ERROR_TYPES.CONFLICT) {
      // otro usuario lo corrigió, el Comedor entregó una comida o ya es un día anterior
      notifications.warning(error.message)
      corrigiendo.value = false
      await load()
    } else if (error.type === ERROR_TYPES.VALIDATION) {
      // solo por un bug del cliente (sin cambios o cobertura): no se pierde la selección
      notifications.error(error.message)
    } else {
      throw error
    }
  } finally {
    enviando.value = false
  }
}
```

  - Guardar el número de cambios **antes** del `await`, para el toast (igual que `marcadas` en `autorizar`).
- **"Última revisión"**: el `<dt>` "Revisado por" pasa a "Última revisión" y el `<dd>` muestra `{usuario}, {formatearFechaHora(revision.en)}`.
- **Historial**: `<HistorialAutorizacion v-if="pedido.revision" :key="pedido.modificadoEn" :pedido-id="pedido.id" />` después de las matrices.
- Actualizar el comentario de la vista: autorizar, rechazar y corregir ya están conectados.

## 7. Verificación

### 7.1 Automática

```sh
cd code/admin && poetry run pytest          # nuevos y reescritos en verde; las fallas previas de SQLite siguen igual (ver reportes S1 a S4)
cd code/portal && npm run lint && npm run build && npm test   # el módulo sin errores de lint; los 3 de carga-pedidos son previos
```

### 7.2 Manual desde el portal

Preparación (en `code/admin`). No hay cambio de esquema, pero los datos de demo cambian (pedido 12), así que se regeneran:

```sh
poetry run flask --app web reset-db
USER_NAME=admin@test.com USER_PASS=admin1234 poetry run flask --app web seeds
poetry run flask --app web run --debug
# en otra terminal: cd code/portal && npm run dev
```

Si hay un backend o un portal del usuario corriendo, levantar instancias propias en otros puertos y con una base temporal, como en S3, y apagarlas al terminar.

Usuarios: `alimentacion@test.com` / `alim1234` (Ana) y `alimentacion2@test.com` / `alim1234` (Bruno); `comedor@test.com` / `comedor1234`; `jefe.hemoterapia@test.com` / `jefe1234`.

Datos: sección S5 de `seeds-per-slice.md` (actualizada en esta slice).

| # | Acción | Esperado |
|---|---|---|
| 1 | Seeds | Terminan sin error. El 12 queda autorizado con **1** no autorizada (29500003, almuerzo). `autorizacion_cambios` tiene filas `correccion` del 12 (una, de Bruno) además de las `autorizacion` y `rechazo` de S3 y S4 |
| 2 | Abrir el 12 y desplegar "Historial de cambios" | Dos operaciones: primero "Corrección" de Bruno Alimentación (Zapata, Ezequiel · almuerzo: No autorizada → Autorizada) y después "Autorización" de Ana (8 cambios desde "Pendiente"). "Última revisión: Bruno Alimentación, {fecha y hora}" |
| 3 | Abrir el 6 y pulsar "Corregir autorización" | Aviso de corrección con "Sin cambios". La matriz con checkbox: marcado todo salvo 25400003 en el desayuno. Atajos visibles. Totales con "Quedan autorizadas" igual a "Autorizadas". "Guardar corrección" deshabilitado |
| 4 | Marcar 25400003 (desayuno) | "1 cambio". "Quedan autorizadas" del desayuno sube en 1. "Guardar corrección" habilitado |
| 5 | Desmarcarla otra vez | "Sin cambios" y "Guardar corrección" deshabilitado |
| 6 | "Cancelar corrección" | Vuelve a solo lectura, sin checkbox, igual que antes de entrar. El foco vuelve a "Corregir autorización" |
| 7 | Entrar de nuevo, marcar 25400003, desmarcar a 25555666 en el almuerzo y "Guardar corrección" | Confirmación: "Se van a cambiar 2 solicitudes: 1 pasa a autorizada y 1 a no autorizada. Quedan autorizadas 7 de 8." Cancelar no hace nada |
| 8 | Confirmar | Toast "Autorización de Intendencia corregida: 2 cambios." Vuelve a la bandeja con los mismos filtros. El 6 sigue Autorizado con `7 / 8` |
| 9 | Abrir el 6 y desplegar el historial | Arriba, "Corrección" de Ana con los 2 cambios y sus valores anteriores. Debajo, la autorización de los seeds. **Criterio "Listo cuando"** |
| 10 | En el 6, "Corregir autorización" → "Ninguno" → guardar y confirmar | Éxito. El 6 sigue Autorizado con `0 / 8` |
| 11 | El 12 en dos pestañas (Ana y Bruno). Corregir en la primera. En la segunda, sin recargar, corregir otra cosa y guardar | Toast warning "El pedido cambió mientras se revisaba…", sale del modo corrección y recarga con la corrección de la primera pestaña. El historial (al desplegarlo) muestra la corrección nueva |
| 12 | Abrir el 7 | Aviso "Ya tiene comidas entregadas: la autorización no se puede corregir." Sin botón "Corregir autorización". El historial muestra su autorización |
| 13 | Pedido 12 abierto en modo corrección con un cambio. En otra ventana, como `comedor`, registrar una entrega de una persona autorizada del 12 **para su fecha** (si el Comedor solo permite entregar hoy, hacerlo con `curl`/`flask shell` y `create_entrega(fecha=...)`). Volver y guardar | 409 "El pedido ya tiene comidas entregadas.", sale del modo corrección y recarga sin el botón. Si no se puede registrar la entrega del 12, hacer el paso con un pedido de hoy (por ejemplo el 5, después de autorizarlo) y anotarlo en el reporte |
| 14 | Abrir el 1 y el 2 (pasados, autorizados) | "Período anterior: solo lectura." Sin botones ni checkbox. El historial sí se puede ver |
| 15 | Abrir el 3 (pasado, pendiente) | "Período anterior: solo lectura." Sin botones. **Sin** sección de historial (no fue revisado) |
| 16 | Abrir el 8 (rechazado) y desplegar el historial | Una operación "Rechazo" con las 5 solicitudes de "Pendiente" a "No autorizada" |
| 17 | `curl` PUT al 12 con todas sus solicitudes, los mismos valores que tiene y su versión | 400 "La corrección no cambia ninguna solicitud." |
| 18 | `curl` PUT al 2 (pasado) con una corrección y su versión | 409 "El pedido es de un período anterior: es solo lectura." |
| 19 | `curl` PUT al 7 con una corrección y su versión | 409 "El pedido ya tiene comidas entregadas." |
| 20 | `curl` reject al 12 con motivo y su versión | 409 "El pedido ya fue autorizado: para dejarlo sin efecto, corregí su autorización." |
| 21 | `curl` GET `/history` del 12 como alimentación, del 9999, y como `jefe.hemoterapia` | 200 agrupado; 404; 403 |
| 22 | Como `jefe.hemoterapia`, abrir el 12 en Carga | Ve lo autorizado vigente (7 de 8 o lo que haya quedado después del paso 11) |
| 23 | Consultar `autorizacion_cambios` (`psql` o `flask shell`) | Filas `correccion` del 6 y del 12, cada una con `autorizada_antes` booleano (nunca nulo), el usuario y el momento correctos |
| 24 | Con DevTools en "Slow 3G" (o una ruta demorada de Playwright), guardar una corrección | Mientras la petición está en curso, "Guardar corrección", "Cancelar corrección", los atajos y los checkbox quedan deshabilitados, o al menos no permiten mandar dos veces |

Para los `curl`, el token se obtiene con el login de la API (ver `reporte-s1-bandeja-diaria.md` o `openapi.yaml`). La versión sale del `modificado_en` del `GET /api/authorizations/{id}`.

### 7.3 Criterio de terminado

- 7.1 y 7.2 pasan completos, **incluidos los pasos de navegador**.
- La rama está mergeada en local a `feature/autorizacion-pedidos` y **no** está pusheada.
- `seeds-per-slice.md` actualizado con el pedido 12 corregido. Hay que revisar **todas** las menciones al 12, como mínimo:
  - el usuario `alimentacion2` en la tabla de usuarios ("corrigió el pedido 12");
  - la tabla general ("No aut." = 1, solo 29500003; particularidad "corregido por alimentacion2");
  - S2 ("lo autorizado 7");
  - la nota de historial de S5 (filas `correccion` del 12 desde los seeds);
  - S7 y S10 si dependen de cantidades autorizadas.

  Con `grep -n "12" seeds-per-slice.md` se encuentran todas.
- Está escrito el reporte en `documentacion-personal/reporte-s5-correccion-y-dias-anteriores.md` con la plantilla del artefacto:
  - Encabezado.
  - Alcance.
  - Cambios, con "base de datos: sin cambios de esquema; reset-db + seeds por los datos de demo".
  - Prueba manual con el resultado de cada paso.
  - Pendientes y decisiones.

## 8. Pendientes para otras slices

- **Coordinación con Entregas:**
  - `create_entrega` no bloquea la fila del pedido ni mueve `modificado_en`. Una corrección y una entrega simultáneas del mismo pedido pueden pasar las dos. Propuesta: que la entrega tome `with_for_update` sobre el pedido de la solicitud antes de insertar.
  - Una entrega sobre una solicitud que una corrección acaba de dejar como no autorizada: revisar que Entregas valide `autorizada is True` en la misma transacción.
- **Coordinación con Carga:** sigue pendiente el bloqueo de fila en `update_pedido` (ver S3 y S4).
- **S6:** el calendario puede marcar los días con pedidos corregibles. No hace falta nada de S5.
- **S9:** los almuerzos generados escriben `tipo=generacion`. El panel de historial ya sabe mostrarlo (etiqueta "Generación").
- **S11:**
  - la corrección en la vista móvil (lista por persona);
  - navegación por teclado dentro de la matriz;
  - la sección "Ampliaciones de la API" con `GET /history` y el cambio del PUT;
  - el recorrido guionado "autorizar parcial, corregir".
- **Opcional:** marcar por celda lo que cambia en la corrección, si el uso real lo pide.
