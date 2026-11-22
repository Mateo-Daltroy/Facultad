# Plan S4: Atajos masivos y rechazo con motivo (Módulo de Autorización)

> **BORRADOR v1 (19/11/2026).** Decisiones cerradas. Los textos del diálogo de rechazo y el estilo del checkbox tri-estado se ajustan visualmente durante la implementación.
>
> Plan de implementación de la cuarta slice del "Plan de desarrollo — Módulo de Autorización" (artefacto).
>
> Referencias: `documentacion-personal/plan-s3-autorizar-solicitudes.md` y `reporte-s3-autorizar-solicitudes.md` (slice anterior), `documentacion-personal/seeds-per-slice.md` (sección S4), `documentacion-personal/resumen_portal_y_core.md` y `Etapa_1y2_Trabajo Integrador_2026_PUBLIC.md` (Módulo de Autorización y Ampliaciones de la API, "POST /api/authorizations/{request_id}/reject — motivo obligatorio y días anteriores").
>
> **Precondición:** S3 está terminada y mergeada en `feature/autorizacion-pedidos`. Este plan usa los nombres del código de S3 tal como quedaron: `revisar_pedido`, `_bloquear`, `_exigir_autorizar`, `MENSAJES_BLOQUEO`, `version_de`, `VersionPedido`, `RevisionAutorizacion`, `_cerrar_revision(pedido, estado, usuario_id, ahora)` y `_commit` en `core/autorizacion.py`; `_detalle` en `api_autorizaciones.py`; y `seleccion.js`, `useSeleccionSolicitudes`, `volverABandeja`, `enviando` y `conteo` (`{ marcadas, pedidas, porComida }`) en el portal. Si algo de esto no coincide con el código, **manda el código**: se adapta el plan sin renombrar lo existente.

## 1. Objetivo

Hoy el detalle (S3) permite autorizar solicitud por solicitud, pero marcar 40 personas una por una es lento. Además, el rechazo es el de Etapa 1:

- el motivo es opcional;
- no hay control de día anterior ni de versión;
- no queda historial;
- el botón "Rechazar" no hace nada.

Al terminar S4, el usuario de Alimentación:

- En un pedido pendiente ve, sobre las matrices, una barra de atajos: **[Todo] [Solo regulares] [Ninguno]**. Cada botón actúa sobre todos los días del pedido.
- En cada matriz, una **columna "Todas"** entre "Persona" y "Desayuno" tiene un checkbox tri-estado por persona: marca o desmarca todas las comidas de esa persona en ese día. Una **fila "Todos"** debajo del encabezado tiene un checkbox tri-estado por comida: marca o desmarca esa comida para todas las personas de ese día. La celda de la esquina (columna "Todas" × fila "Todos") queda vacía.
- Ve en vivo, igual que en S3, los totales "A autorizar" y el pie de la matriz.
- Pulsa "Rechazar" y se abre un **diálogo con un resumen** ("Se va a rechazar el pedido de Quirófano del jueves 19 de noviembre: sus 14 solicitudes quedan como no autorizadas…") y un **motivo obligatorio**, con contador de caracteres (máximo 255). Si el motivo está vacío o tiene solo espacios, el botón "Rechazar" del diálogo está deshabilitado.
- Si el rechazo sale bien: toast de éxito y **vuelve a la bandeja** con `volverABandeja()`, igual que después de autorizar.
- Si el pedido cambió mientras lo revisaba: **409**, se cierra el diálogo, aparece un toast con el motivo y el detalle se recarga.

En la API:

- `POST /api/authorizations/{id}/reject` recibe `{ motivo, modificado_en }`, **los dos obligatorios**.
- Usa `acciones_permitidas` como guarda: da 409 por día anterior, por pedido ya rechazado y por pedido ya autorizado.
- Compara la **versión** (409), bloquea la fila y escribe el **historial** `tipo=rechazo`.
- Responde con el detalle de autorización (`_detalle`, el mismo del GET y del PUT).
- La base tiene un `CHECK` que impide un pedido rechazado sin motivo.
- El Jefe ya ve `motivo_rechazo` en `GET /api/requests/{id}` y en su pantalla de detalle (Carga). S4 solo lo **verifica** con un test y en la prueba manual.

**Criterio "Listo cuando" del artefacto:** "solo regulares" deja las excepciones sin autorizar, y el Jefe ve el motivo del rechazo.

**Inconsistencias transitorias aceptadas** (mismo criterio que S2 y S3):

- Los pedidos autorizados corregibles (6 y 12) siguen mostrando `corregir: true`. El PUT y el reject sobre ellos responden 409 "El pedido ya fue autorizado." El mensaje **no** sugiere corregir, porque el botón "Corregir autorización" sigue sin hacer nada hasta S5.

Requisitos del enunciado que cubre:

- "Realizar autorizaciones masivas […] Se sugieren accesos rápidos como autorizar todas las solicitudes regulares o autorizar el pedido completo".
- "Permitir acciones masivas, como seleccionar una comida para todas las personas listadas o todas las comidas para una persona".
- "Rechazar un pedido completo, indicando el motivo del rechazo, que es obligatorio".
- De las Ampliaciones del reject:
  - el motivo obligatorio (400 si falta);
  - el motivo se devuelve también en `GET /api/requests/{id}`;
  - 409 por día anterior o ya rechazado;
  - un autorizado no se rechaza, se corrige.
- "Pedir confirmación antes de […] rechazar un pedido".

## 2. Decisiones

| Tema | Decisión | Motivo |
|---|---|---|
| Versión en el reject | El body pasa a ser `{ motivo, modificado_en }`, con los dos campos obligatorios. Si la versión no coincide, 409 | Decisión acordada. Si el Jefe agregó o quitó personas mientras se revisaba, el rechazo tiene que verse sobre la versión actual, igual que la autorización. Es un cambio de contrato respecto de Etapa 1 y se documenta en openapi |
| Firma de dominio | `rechazar_pedido(pedido_id, usuario_id, rechazo: RechazoPedido, ahora)`. `RechazoPedido` es un `@dataclass(frozen=True)` con `version: VersionPedido` y `motivo: MotivoRechazo` | Misma forma que `revisar_pedido`. `core` nunca recibe el dict crudo |
| Motivo | `MotivoRechazo = NewType("MotivoRechazo", str)` con un **único parser**, `parse_motivo(texto: str) -> MotivoRechazo`, en `core/autorizacion.py`. Hace `strip`, y si el resultado está vacío o pasa de `MAX_MOTIVO_LENGTH` (255), lanza `BusinessRuleError(field="motivo")`. Lo llama el `post_load` del schema | "Parse, don't validate": un `MotivoRechazo` siempre es no vacío y de hasta 255 caracteres. La regla vive en el dominio y no se repite en el schema |
| Orden de los chequeos | Parseo del body (400) → 404 → bloqueo de fila → guarda `acciones_permitidas` (409) → versión (409) | Igual que S3. El body se parsea en el controlador, así que un body inválido da 400 antes que el 404 (ya se aceptó en S3) |
| Guarda | `_exigir_autorizar(acciones)` pasa a `_exigir(acciones, accion: Literal["autorizar", "rechazar"])`. `Bloqueado` usa `MENSAJES_BLOQUEO`. Si la acción es `Disponibles` pero la pedida no está habilitada (hoy solo `_CORREGIR`), lanza 409 "El pedido ya fue autorizado." | Una sola guarda para las dos operaciones. El mensaje no menciona la corrección porque el botón es de S5 |
| Historial | `tipo="rechazo"`, una fila por solicitud cuyo valor cambia. En un confirmado son todas, porque pasan de `NULL` a `false` | Mismo criterio que S3. El CHECK de `tipo` ya admite `rechazo` |
| CHECK del motivo | `CheckConstraint("estado <> 'rechazado' OR motivo_rechazo IS NOT NULL", name="ck_pedido_motivo_rechazo")` en `Pedido`. Que el motivo no sea vacío lo garantiza el parser, no la base | El artefacto pide el motivo obligatorio también en la base. Requiere reset-db |
| Respuesta del reject | `_detalle(pedido)` (`dump_detalle_autorizacion` con las acciones recalculadas), igual que el PUT | Contrato del artefacto: "detalle en estado rechazado". El portal la parsea con `aPedidoDetalle` |
| Funciones que se borran | `get_pedido_para_revisar` y `validate_motivo` | Ya no las usa nadie |
| Atajos globales | `AtajosSeleccion.vue`, con [Todo], [Solo regulares] y [Ninguno], sobre todos los días del pedido. Solo se muestra si `editable` | Atajos del enunciado |
| Atajos por comida y por persona | Checkbox tri-estado: marcado si están todas, `indeterminate` si están algunas, vacío si no hay ninguna. Un clic marca todas, o las desmarca si ya estaban todas. Cada uno actúa **solo sobre el día de su matriz**. Columna "Todas" entre Persona y Desayuno, fila "Todos" debajo del encabezado y la esquina vacía | Decisión acordada (layout propuesto por el usuario) |
| "Solo regulares" | **Fija** la selección: regulares en `true` y excepciones en `false`. No es "sumar las regulares a lo marcado" | Así lo espera `seeds-per-slice.md`: en el pedido 4 deja 19 autorizadas y 10 sin autorizar |
| `soloRegulares` y `seleccionInicial` | Funciones separadas en `seleccion.js`. Hoy `seleccionInicial(dias)` devuelve `soloRegulares(dias)` | En S5 la selección inicial de una corrección pasa a ser el estado actual, y el atajo no cambia |
| Vitest | Se agrega como devDependency con el script `"test": "vitest run"` y `seleccion.test.js` | Decisión acordada. `seleccion.js` no depende de Vue |
| Diálogo de rechazo | Componente propio del módulo, `DialogoRechazo.vue`, con `<dialog>` + `showModal()` como `ConfirmDialog`. Tiene un resumen, una textarea obligatoria y un contador. No se extiende `useConfirm` | `useConfirm` es global y solo muestra un párrafo. El motivo es un formulario |
| Después del reject | Toast de éxito y `volverABandeja()` | Decisión acordada, igual que después de autorizar |
| 409 en el reject | Se cierra el diálogo, toast `warning` con el mensaje de la API y `load()` | Regla del artefacto |
| 400 en el reject | El diálogo queda abierto y muestra debajo de la textarea `error.fields?.motivo?.[0] ?? error.message`. Se conserva el texto escrito | "400 marca campos" (artefacto) |
| Botón "Corregir autorización" | Sigue sin hacer nada | S5 |
| Rama | `feat/autorizacion-s4` sale de `feature/autorizacion-pedidos` (con S3 mergeada) y vuelve con merge local `--no-ff` | Igual que S1 a S3 |

## 3. Fuera de alcance

No hacer en S4 (cada ítem tiene su slice):

- Corrección de autorizados: `tipo=correccion`, cambiar el caso `Disponibles(autorizar=False)` de `_exigir`, el botón "Corregir autorización", `GET /history` y el panel de historial (S5).
- Que un autorizado se pueda rechazar: **no se permite**. El enunciado dice que se corrige.
- Aviso de cambios sin guardar al salir del detalle, o al cerrar el diálogo de rechazo con texto escrito.
- Navegación por teclado dentro de la matriz y la vista móvil (S11). Los checkbox nuevos sí tienen que ser accesibles con Tab y Espacio, que es el comportamiento nativo.
- Atajos entre pedidos distintos. El enunciado los define dentro de un pedido.
- Bloquear la fila en `update_pedido` del Jefe (sigue pendiente de Carga, ver S3).
- Tests de componentes Vue con Vitest (`@vue/test-utils`). En S4 solo se prueban funciones puras.

## 4. Git

```sh
cd code
git switch feature/autorizacion-pedidos
git log --oneline -3          # confirmar que el merge de feat/autorizacion-s3 está
git status                    # árbol limpio antes de empezar
git switch -c feat/autorizacion-s4
```

- Commits chicos por capa, por ejemplo:
  - `feat(core): rechazar_pedido con motivo obligatorio, versión, guarda e historial`
  - `feat(core): CHECK del motivo de rechazo`
  - `feat(api): reject con versión y detalle de autorización`
  - `test: reglas del rechazo`
  - `build(portal): vitest`
  - `feat(portal): atajos de selección`
  - `feat(portal): rechazar pedido con motivo`
- Al terminar: `git switch feature/autorizacion-pedidos && git merge --no-ff feat/autorizacion-s4`.
- **No pushear `feat/autorizacion-s4` al origin.** `feature/autorizacion-pedidos` se pushea solo si lo pide el usuario.

## 5. Backend (`code/admin`)

### 5.1 `src/core/pedido.py`: CHECK del motivo

Agregar a `Pedido` (si ya tiene `__table_args__`, sumarlo a la tupla):

```python
__table_args__ = (
    CheckConstraint(
        "estado <> 'rechazado' OR motivo_rechazo IS NOT NULL",
        name="ck_pedido_motivo_rechazo",
    ),
)
```

- `motivo_rechazo` sigue `String(255)`, nullable (los confirmados y los autorizados no tienen motivo).
- Revisar que ningún camino de Carga o Entregas deje un pedido rechazado sin motivo. Lo esperado es que el único que rechaza sea `rechazar_pedido`.

### 5.2 `src/core/autorizacion.py`

```python
MotivoRechazo = NewType("MotivoRechazo", str)


def parse_motivo(texto: str) -> MotivoRechazo:
    """único parser del motivo de rechazo: sin espacios en los extremos, no vacío y de
    hasta MAX_MOTIVO_LENGTH caracteres"""
    motivo = texto.strip()
    if not motivo:
        raise BusinessRuleError("El motivo del rechazo es obligatorio.", field="motivo")
    if len(motivo) > MAX_MOTIVO_LENGTH:
        raise BusinessRuleError(
            f"El motivo no puede superar los {MAX_MOTIVO_LENGTH} caracteres.", field="motivo"
        )
    return MotivoRechazo(motivo)


@dataclass(frozen=True)
class RechazoPedido:
    """lo que manda el portal al rechazar: la versión que vio y el motivo"""
    version: VersionPedido
    motivo: MotivoRechazo


def rechazar_pedido(pedido_id: int, usuario_id: int, rechazo: RechazoPedido,
                    ahora: datetime) -> Pedido:
    """rechaza el pedido completo. todo o nada.
    orden: 404 -> bloqueo de fila -> guarda (409) -> versión (409)"""
    pedido = _bloquear(pedido_id)                                       # 404
    entregadas = ids_entregados(s.id for s in pedido.solicitudes)
    acciones = acciones_permitidas(pedido, ahora.date(), hay_entregas=bool(entregadas))
    _exigir(acciones, "rechazar")                                       # 409
    _exigir_version(pedido, rechazo.version)                            # 409

    for solicitud in pedido.solicitudes:
        if solicitud.autorizada is not False:
            db.session.add(AutorizacionCambio(
                solicitud_id=solicitud.id, pedido_id=pedido.id, usuario_id=usuario_id,
                momento=ahora, autorizada_antes=solicitud.autorizada,
                autorizada_despues=False, tipo="rechazo",
            ))
        solicitud.autorizada = False
        solicitud.autorizado_por = usuario_id
        solicitud.autorizado_en = ahora
    pedido.motivo_rechazo = rechazo.motivo
    _cerrar_revision(pedido, "rechazado", usuario_id, ahora)
    _commit()
    return pedido
```

Detalles:

- **`_exigir(acciones, accion)`** reemplaza a `_exigir_autorizar` y la usan `revisar_pedido` (`"autorizar"`) y `rechazar_pedido` (`"rechazar"`). Es un `match` sobre `AccionesPedido`:
  - `Disponibles()` con la acción habilitada (`getattr(acciones, accion)` es `True`, o un `match` explícito por acción) → sigue.
  - `Bloqueado(motivo)` → `ConflictError(MENSAJES_BLOQUEO[motivo])`.
  - `Disponibles` sin la acción → `ConflictError("El pedido ya fue autorizado.")`. Hoy solo `_CORREGIR` llega acá; S5 cambia este caso para autorizar.
  - Otro valor → `TypeError`.
  - `accion` se tipa `Literal["autorizar", "rechazar"]`. Si se usa `getattr`, agregar `assert accion in ("autorizar", "rechazar")` o, mejor, un `match accion` que lance `ValueError` en el caso por defecto.
- **`_exigir_version(pedido, version)`**: se extrae de `revisar_pedido` el `if revision.version != version_de(pedido): raise ConflictError(...)` con el **mismo mensaje** ("El pedido cambió mientras se revisaba. Se recargó la versión actual.", `field="modificado_en"`). `revisar_pedido` pasa a usarla.
- **Se borran** `get_pedido_para_revisar` y `validate_motivo`. Se borran también los imports que queden sin uso (`get_pedido_by_id` y `reloj`, si ya nadie los usa en el módulo).
- `MAX_MOTIVO_LENGTH` queda en `core/autorizacion.py`. El schema no repite el 255.

### 5.3 `src/core/seeds.py`

En `seed_pedidos_demo`, el caso `Rechazado(motivo=motivo)` pasa a:

```python
case Rechazado(motivo=motivo):
    cuando = min(datetime.combine(fecha, time(9, 0), tzinfo=reloj.ZONA), reloj.ahora())
    rechazar_pedido(pedido.id, alimentacion.id, RechazoPedido(
        version=version_de(pedido), motivo=parse_motivo(motivo),
    ), ahora=cuando)
```

- Mismo criterio de `cuando` que S3. Conviene extraer `cuando` antes del `match` si los dos casos lo usan.
- El pedido 8 (Hemoterapia, D) queda rechazado igual que antes, ahora con filas `tipo=rechazo` en `autorizacion_cambios`.
- Verificar que `seeds-per-slice.md` siga siendo correcto. Lo esperado es que no cambie nada visible. **Sí está desactualizada** la nota de la sección S5 que dice "los pedidos de demo no tienen filas en `autorizacion_cambios`": después de S3 y S4 sí las tienen (`autorizacion` y `rechazo`). Se actualiza esa nota.

### 5.4 `src/web/schemas/autorizaciones.py`

```python
class RechazarInputSchema(Schema):
    class Meta:
        unknown = RAISE

    # el largo y el "no vacío" los decide parse_motivo (dominio)
    motivo = fields.String(required=True)
    # versión del pedido tal como la devolvió el GET de detalle. opaca: no se parsea
    modificado_en = fields.String(required=True, validate=validate.Length(min=1))

    @post_load
    def a_rechazo(self, data, **kwargs) -> RechazoPedido:
        return RechazoPedido(
            version=VersionPedido(data["modificado_en"]),
            motivo=parse_motivo(data["motivo"]),
        )
```

- `motivo: null` da 400 de marshmallow (`allow_none` en `False`, que es el valor por defecto).
- `BusinessRuleError` lanzado dentro del `post_load` se traduce a 400 con `fields.motivo`, igual que cualquier error de dominio. **Verificarlo:** si `api_errors.py` no lo traduce cuando sale del `load` (por ejemplo, porque marshmallow lo envuelve), lanzar `ValidationError({"motivo": [...]})` desde el `post_load` con el mensaje del `BusinessRuleError`. Lo importante es que la respuesta sea 400 con `fields.motivo` y que la regla siga viviendo solo en `parse_motivo`.

### 5.5 `src/web/controlers/api_autorizaciones.py`

```python
@api_autorizaciones_bp.post("/<int:request_id>/reject")
@permission_required("autorizacion_reject")
def reject(request_id: int):
    rechazo = rechazar_input_schema.load(request.get_json(silent=True) or {})
    pedido = rechazar_pedido(request_id, g.current_user.id, rechazo, reloj.ahora())
    return jsonify(_detalle(pedido)), 200
```

- Si el import de `pedido_detail_schema` queda sin uso, se borra.
- Con `get_json(silent=True) or {}`, un POST sin body da 400 por los campos requeridos y no 415.

### 5.6 `admin/docs/openapi.yaml`

- `POST /authorizations/{request_id}/reject`:
  - Body requerido: `motivo` (string, obligatorio, de 1 a 255 caracteres después de quitar los espacios de los extremos) y `modificado_en` (string, obligatorio, con la misma descripción de versión opaca que el PUT).
  - Respuesta 200: `AuthorizationDetail`.
  - 400: falta el motivo, el motivo es vacío o solo espacios, supera los 255 caracteres, falta `modificado_en`, o hay campos desconocidos.
  - 404.
  - 409: pedido de un período anterior, ya rechazado, ya autorizado, o versión desactualizada.
- `RequestDetail.motivo_rechazo`: aclarar que está presente (string no vacío) si y solo si `estado == "rechazado"`.

### 5.7 Tests

Se usan los fixtures y helpers existentes (`client`, `data`, `login_as`, `crear`, `set_estado`, `pedido`, `detalle`, `put_todas`, `fijar_ahora`).

**Helper nuevo** en `tests/unit/test_api_autorizaciones.py`:

```python
def post_rechazo(client, pedido_id, motivo="Sin cupo", version=None):
    """POST reject con la versión actual (salvo que se pase otra)"""
    body = {"motivo": motivo,
            "modificado_en": version or detalle(client, pedido_id)["modificado_en"]}
    return client.post(f"/api/authorizations/{pedido_id}/reject", json=body)
```

Para los tests de dominio también conviene un `rechazo_de(pedido, motivo="Sin cupo")` que arme `RechazoPedido(version_de(pedido), parse_motivo(motivo))`.

**Ajustes por el CHECK** (si no, fallan con `IntegrityError`):

- `set_estado` en `tests/unit/test_api_pedidos.py`: si `estado == "rechazado"`, asignar `pedido.motivo_rechazo = "Motivo de prueba"`.
- `test_core_pedido_rechazado_no_se_entrega` en `tests/unit/test_api_entregas.py`: asignar `motivo_rechazo` antes del commit.
- Buscar otros casos con `grep -rn '"rechazado"' tests` y `grep -rn "estado = " tests`.

**Tests existentes que se reescriben** (hoy rechazan sin versión, sin motivo o con la firma vieja):

- `test_core_no_se_revisa_dos_veces`: usa `rechazo_de`. El segundo `rechazar_pedido` da 409 "fue rechazado".
- `test_core_pedido_inexistente`: el rechazo con la firma nueva.
- `test_put_rechazado_409`: rechazar con `post_rechazo`.
- `test_reject_with_reason`: con `post_rechazo`. Comprueba además la forma de detalle de autorización (`acciones`, `revision`).
- `test_reject_without_body`: pasa a esperar **400**, con `fields` que mencionan `motivo` y `modificado_en`.
- `test_reject_authorized_request_returns_409`: mensaje "ya fue autorizado".
- `test_reject_with_too_long_reason_returns_400`: con `post_rechazo` (la versión válida, así el 400 es por el motivo).
- El uso de la línea ~514 y `test_modificado_en_cambia_al_autorizar_y_rechazar`: la firma nueva con `ahora=T2`.
- Cualquier otro que use `rechazar_pedido` o `/reject` (`grep -rn "rechazar_pedido\|/reject" tests`).

**Tests nuevos** (`tests/unit/test_api_autorizaciones.py`):

| Test | Preparación | Esperado |
|---|---|---|
| `test_core_parse_motivo` | `"  sin cupo  "`, `""`, `"   "`, `"x" * 255`, `"x" * 256` | `"sin cupo"`; `BusinessRuleError` con `field == "motivo"`; error; ok; error |
| `test_reject_motivo_vacio_400` (parametrizado: falta la clave, `""`, `"   "`, `None`) | `post_rechazo` con ese motivo, o el body sin `motivo` | 400 con `fields.motivo`; el pedido sigue confirmado y sin historial |
| `test_reject_motivo_255_con_espacios_ok` | `"  " + "x" * 255 + "  "` | 200 y `motivo_rechazo == "x" * 255` |
| `test_reject_sin_version_400` | Body con `motivo` solo, y con `modificado_en: ""` | 400 con `fields.modificado_en` |
| `test_reject_version_vieja_409` | `version="otra"` | 409 con el mensaje de "cambió mientras se revisaba"; el pedido sigue confirmado y sin historial |
| `test_reject_jefe_cambio_personas_409` | GET como alimentación (guardar la versión), el Jefe modifica las personas con `ahora = T2`, rechazo con la versión vieja | 409 |
| `test_reject_dia_anterior_409` | Pedido de D, `ahora` en D+1 | 409 con "período anterior" |
| `test_reject_rechazado_409` | Rechazar dos veces (la segunda con la versión nueva) | 409 "fue rechazado" |
| `test_reject_autorizado_409` | `put_todas` y después rechazo con la versión nueva | 409 "ya fue autorizado"; el pedido sigue autorizado |
| `test_reject_crea_historial` | Pedido con N solicitudes, `ahora = T` | N filas `tipo=rechazo`, `autorizada_antes is None`, `autorizada_despues is False`, `momento == T`, `usuario_id` de alimentación |
| `test_reject_devuelve_detalle_de_autorizacion` | Rechazo de hoy | 200 con `estado == "rechazado"`, `motivo_rechazo`, `acciones == {autorizar: false, corregir: false, rechazar: false, motivo_bloqueo: "rechazado"}`, `revision.usuario`, todas las solicitudes con `autorizada: false`, `modificado_en` distinto del anterior |
| `test_check_motivo_rechazo_en_base` | `pedido.estado = "rechazado"` con `motivo_rechazo = None` y `db.session.commit()` | `IntegrityError` (SQLite aplica los CHECK). Hacer `rollback` al final |
| `test_jefe_ve_motivo_del_rechazo` | Rechazar como alimentación y `GET /api/requests/{id}` como el Jefe del sector | 200 con `motivo_rechazo` igual al enviado (sin espacios en los extremos) |

El 401 y el 403 del reject ya están cubiertos. **No se duplican.**

## 6. Portal (`code/portal`)

### 6.1 Archivos

```
package.json                                     (modificado) vitest + script "test"
src/modules/autorizacion/
├── seleccion.js                                 (modificado) atajos y grupos
├── seleccion.test.js                            (nuevo) tests de Vitest
├── composables/useSeleccionSolicitudes.js       (modificado) acciones de atajos
├── services/autorizacionService.js              (modificado) rechazarPedido
├── components/AtajosSeleccion.vue               (nuevo) Todo / Solo regulares / Ninguno
├── components/MatrizSolicitudes.vue             (modificado) columna "Todas" y fila "Todos"
├── components/DialogoRechazo.vue                (nuevo) resumen + motivo obligatorio
└── views/DetallePedidoView.vue                  (modificado) atajos y handler de "Rechazar"
```

Igual que en S1 a S3:

- No se tocan `router/`, `constants/`, `api/`, `components/` globales ni otros módulos.
- No se crea `domain/`: `seleccion.js` sigue en la raíz del módulo.

### 6.2 Vitest

- `npm install -D vitest`, con la versión **exacta** más reciente que acepte la versión de Vite del proyecto (Vite 8). Verificar las peer dependencies con `npm view vitest@<versión> peerDependencies` antes de instalar. Si ninguna versión estable de Vitest acepta Vite 8 sin `--force`/`--legacy-peer-deps`, **no se fuerza**: se anota en el reporte, se deja `seleccion.test.js` escrito y se avisa al usuario.
- Script en `package.json`: `"test": "vitest run"`.
- Sin config propia si alcanza con la de Vite (`vite.config.js` ya resuelve el alias `@`). El entorno por defecto (`node`) alcanza porque `seleccion.js` no toca el DOM.
- Confirmar que `npm run lint` no se queje de `seleccion.test.js`: `describe`, `it` y `expect` se **importan** de `vitest` (sin globals), así no hace falta tocar la config de ESLint/oxlint.

### 6.3 `seleccion.js`

Funciones nuevas, todas puras. Reciben `dias` (o las `personas` de un día) y una `Seleccion`, y devuelven valores nuevos:

```js
/** todas las solicitudes marcadas */
export function todas(dias) { ... }

/** ninguna solicitud marcada */
export function ninguna(dias) { ... }

/** regulares en true, excepciones en false: fija la selección, no suma a la anterior */
export function soloRegulares(dias) { ... }

/** selección con la que arranca el detalle. hoy = soloRegulares; S5 la cambia para corregir */
export function seleccionInicial(dias) { return soloRegulares(dias) }

/** ids de las solicitudes de una comida en un día (personas = dia.personas) */
export function idsDeComida(personas, comida) { ... }

/** ids de las solicitudes de una persona en un día (fila = FilaPersona) */
export function idsDePersona(fila) { ... }

/** @returns {'todas' | 'algunas' | 'ninguna'} */
export function estadoGrupo(ids, seleccion) { ... }

/** si están todas marcadas, las desmarca; si no, las marca todas. el resto no cambia */
export function alternarGrupo(ids, seleccion) { ... }
```

- `estadoGrupo` y `alternarGrupo` con `ids` vacío lanzan `Error`: la interfaz no dibuja un checkbox de grupo sin solicitudes, así que llegar ahí es un bug.
- Un id de `ids` que no está en `seleccion` lanza `Error`, igual que `marcada` de S3. Reusar ese helper.
- `todas`, `ninguna` y `soloRegulares` reusan el generador `celdasDe` de S3.

### 6.4 `seleccion.test.js`

Con un `dias` de prueba armado a mano (dos días, mezcla de regulares y excepciones, una persona con una sola comida y una comida sin solicitudes en un día):

- `soloRegulares` y `seleccionInicial`: regulares en `true`, excepciones en `false`.
- `todas` y `ninguna`.
- `soloRegulares` después de `todas` deja las excepciones en `false` (fija la selección, no suma).
- `idsDeComida`: solo los de esa comida y ese día. Una comida sin solicitudes da `[]`.
- `idsDePersona`: solo las comidas de esa persona.
- `estadoGrupo`: los tres estados y el `Error` con `[]`.
- `alternarGrupo`: de `algunas` pasa a todas marcadas, de `todas` pasa a ninguna, no toca ids fuera del grupo y no muta el `Map` recibido.
- `contarSeleccion` y `totalesAAutorizar` de S3: un caso cada uno, y el `Error` por un id faltante.

### 6.5 `composables/useSeleccionSolicitudes.js`

Suma acciones que reemplazan la selección por un `Map` nuevo:

```js
function aplicar(siguiente) { seleccion.value = siguiente }

const marcarTodas = () => aplicar(todas(pedido.value.dias))
const marcarSoloRegulares = () => aplicar(soloRegulares(pedido.value.dias))
const desmarcarTodas = () => aplicar(ninguna(pedido.value.dias))
const alternarGrupoDe = (ids) => aplicar(alternarGrupo(ids, seleccion.value))

return { seleccion, alternar, aAutorizar, conteo,
         marcarTodas, marcarSoloRegulares, desmarcarTodas, alternarGrupo: alternarGrupoDe }
```

- `watch` con `flush: 'sync'` y `seleccionInicial` queda igual que en S3.

### 6.6 `components/AtajosSeleccion.vue`

- Tres botones `btn btn--secondary` (o la clase de botón chico que ya exista en el design system del portal) dentro de un `role="group"` con `aria-label="Atajos de selección"`: "Todo", "Solo regulares" y "Ninguno".
- Emite `todo`, `solo-regulares` y `ninguno`.
- Prop `disabled` (bool), que recibe `enviando`.
- Al lado de los botones, un texto con el conteo vivo ("20 de 29 marcadas"), con `aria-live="polite"` para que el lector de pantalla anuncie el efecto del atajo. Recibe `conteo` como prop.

### 6.7 `components/MatrizSolicitudes.vue`

Solo cuando `editable` (en solo lectura, la matriz queda igual que en S2/S3):

- **Columna "Todas"**:
  - `<th scope="col">Todas</th>` entre "Persona" y la primera comida.
  - En cada fila, una celda con un checkbox tri-estado sobre `idsDePersona(fila)`.
  - `aria-label`: "Autorizar todas las comidas de {Apellido}, {Nombre}".
- **Fila "Todos"**:
  - Segunda fila del `<thead>`. En la primera celda dice "Todos" (`<th scope="row">`), la celda de la columna "Todas" queda **vacía**, y por cada comida hay un checkbox tri-estado sobre `idsDeComida(personas, comida)`.
  - Si esa comida no tiene solicitudes en el día, la celda queda vacía (sin checkbox).
  - `aria-label`: "Autorizar {comida} de todas las personas".
- **Tri-estado**: `:checked="estado === 'todas'"` e `:indeterminate="estado === 'algunas'"`. Vue lo asigna como propiedad del DOM. Al hacer clic, emite `alternar-grupo(ids)`.
  - Ojo: el navegador cambia `checked` e `indeterminate` solo al hacer clic. Como la prop viene del `Map` nuevo, el re-render los corrige. Verificar que después de un clic sobre un indeterminado el checkbox quede marcado y no "desmarcado" por el toggle nativo. Si hace falta, usar `@click.prevent` + emit y dejar que el estado venga solo de la prop.
- **Estilo**: se reusa el checkbox propio de S3 (`.check`, `.check__input`, `.check__caja`) y se agrega la variante indeterminada: `.check__input:indeterminate + .check__caja::after` con un "–" del color principal.
- Emite `alternar-grupo(ids: number[])`, además del `alternar(solicitudId)` de S3.
- El pie (`tfoot`) sigue igual. En la fila del pie, la columna "Todas" lleva una celda vacía para que las columnas no se corran.

### 6.8 `components/DialogoRechazo.vue`

Mismo patrón que `components/ConfirmDialog.vue` (`<dialog>` + `showModal()`, foco atrapado, Escape cierra y el foco vuelve al botón que lo abrió). Se copian sus estilos al componente: no se importa ni se modifica el global.

- Props:
  - `abierto` (bool);
  - `resumen` (string, un párrafo);
  - `enviando` (bool);
  - `error` (string o `null`, el mensaje del 400 a mostrar bajo el campo).
- Emite `confirmar(motivo: string)`, con el motivo **ya sin espacios en los extremos**, y `cancelar`.
- Contenido:
  - `<h2>` "Rechazar pedido" y el `resumen` en un `<p>`.
  - `<label for>` "Motivo del rechazo (obligatorio)" y una `<textarea>` con `maxlength="255"`, `required` y `aria-describedby` apuntando al contador y al error.
  - Contador "n/255" (`motivo.length`).
  - Error en un `<p role="alert">` si `error` no es `null`.
  - Botones "Cancelar" (secundario) y "Rechazar" (`btn--danger`). "Rechazar" está `disabled` si `motivo.trim() === ''` o si `enviando`.
- Al abrirse: limpia el texto y pone el foco en la textarea.
- Mientras `enviando`, Escape y "Cancelar" no cierran: evento `cancel` del `<dialog>` con `preventDefault()`.
- El `<form method="dialog">` no se usa para el submit: el botón "Rechazar" emite `confirmar` y es el padre quien cierra (pasa `abierto` a `false`) cuando corresponde.

### 6.9 `services/autorizacionService.js`: `rechazarPedido(id, version, motivo)`

- `POST /authorizations/{id}/reject` con `{ motivo, modificado_en: version }`.
- La respuesta pasa por `aPedidoDetalle` (S2): si viene mal, tira `Error`.
- `version` es `pedido.modificadoEn` sin tocar, igual que en `autorizarPedido`.

### 6.10 `views/DetallePedidoView.vue`

- Del composable: `marcarTodas`, `marcarSoloRegulares`, `desmarcarTodas` y `alternarGrupo`.
- `<AtajosSeleccion v-if="editable" :conteo="conteo" :disabled="enviando" @todo="marcarTodas" @solo-regulares="marcarSoloRegulares" @ninguno="desmarcarTodas" />`, entre los totales y las matrices.
- A cada `MatrizSolicitudes` se le agrega `@alternar-grupo="alternarGrupo"`.
- Estado del diálogo: `const dialogoRechazo = ref(false)` y `const errorMotivo = ref(null)`.
- `<DialogoRechazo :abierto="dialogoRechazo" :resumen="resumenRechazo" :enviando="enviando" :error="errorMotivo" @confirmar="confirmarRechazo" @cancelar="dialogoRechazo = false" />`.
- **`resumenRechazo`** (`computed`), un solo párrafo:
  - "Se va a rechazar el pedido de {sector} del {periodo en minúscula}: sus {n} solicitudes quedan como no autorizadas. El Jefe de Servicio va a ver el motivo."
  - `n` es el total de solicitudes del pedido. Se cuenta sobre `pedido.dias` (por ejemplo, `conteo.value.pedidas`, que existe siempre que el pedido sea editable; en S4 `rechazar` implica `autorizar`). Si `conteo` es `null`, se lanza `Error` en vez de mostrar 0.
  - Con una sola solicitud: "su única solicitud queda como no autorizada".
- Handlers (reemplazan el vacío `// S4` de S3):

```js
function rechazar() {
  errorMotivo.value = null
  dialogoRechazo.value = true
}

async function confirmarRechazo(motivo) {
  const p = pedido.value
  enviando.value = true
  errorMotivo.value = null
  try {
    await rechazarPedido(p.id, p.modificadoEn, motivo)
    dialogoRechazo.value = false
    notifications.success(`Pedido de ${p.sector} rechazado.`)
    volverABandeja()
  } catch (error) {
    if (!(error instanceof ApiError)) throw error
    if (error.type === ERROR_TYPES.CONFLICT) {
      // otro usuario lo revisó, el Jefe lo modificó o ya no se puede rechazar
      dialogoRechazo.value = false
      notifications.warning(error.message)
      await load()
    } else if (error.type === ERROR_TYPES.VALIDATION) {
      // el diálogo queda abierto con lo escrito
      errorMotivo.value = error.fields?.motivo?.[0] ?? error.message
    } else {
      throw error
    }
  } finally {
    enviando.value = false
  }
}
```

- No se usa `useConfirm` para el rechazo: el diálogo ya es la confirmación (el enunciado pide confirmar antes de rechazar, y eso se cumple con el resumen y el botón explícito).
- El botón "Rechazar" de la barra ya tiene `:disabled="enviando"` (S3).
- `corregir()` sigue vacío (`// S5`).
- Actualizar el comentario de la vista: rechazar ya está conectado, corregir queda para S5.

## 7. Verificación

### 7.1 Automática

```sh
cd code/admin && poetry run pytest          # nuevos y reescritos en verde; las fallas previas de SQLite siguen igual (ver reportes S1 a S3)
cd code/portal && npm run lint && npm run build && npm test   # el módulo sin errores de lint; los 3 de carga-pedidos son previos
```

### 7.2 Manual desde el portal

Preparación (en `code/admin`). **Hay cambio de esquema** (CHECK en `pedidos`), así que el reset es obligatorio:

```sh
poetry run flask --app web reset-db
USER_NAME=admin@test.com USER_PASS=admin1234 poetry run flask --app web seeds
poetry run flask --app web run --debug
# en otra terminal: cd code/portal && npm run dev
```

Usuarios:

- `alimentacion@test.com` / `alim1234` y `alimentacion2@test.com` / `alim1234`.
- Los Jefes de la tabla de usuarios de `seeds-per-slice.md` (por ejemplo `jefe.quirofano@test.com`, `jefe.hemoterapia@test.com`, `jefe.medicos@test.com`).

Datos: sección S4 de `seeds-per-slice.md`.

| # | Acción | Esperado |
|---|---|---|
| 1 | Seeds | Terminan sin error. El pedido 8 queda rechazado con su motivo. `autorizacion_cambios` tiene filas `tipo=rechazo` del 8 |
| 2 | Abrir el pedido 4 | La barra de atajos, la columna "Todas", la fila "Todos" y la esquina vacía. Arranca con 19 regulares marcadas: los checkbox de grupo están marcados o indeterminados según corresponda |
| 3 | En el 4, pulsar "Todo" | Las 29 marcadas. "A autorizar" igual a lo pedido. Todos los checkbox de grupo marcados. El conteo dice "29 de 29" |
| 4 | Pulsar "Ninguno" | Ninguna marcada. "A autorizar" en 0 en todas las comidas. Los checkbox de grupo vacíos |
| 5 | Pulsar "Solo regulares" | 19 marcadas y las 10 excepciones desmarcadas (igual a la carga inicial) |
| 6 | "Solo regulares" en el pedido 13 | Toda la cena queda sin autorizar |
| 7 | En el 4, checkbox "Todos" del desayuno (indeterminado) | Pasa a marcado: todo el desayuno marcado y el resto sin cambios. Otro clic lo desmarca todo |
| 8 | En el 4, checkbox "Todas" de Perez o Benítez (una sola comida) | Marca o desmarca solo esa celda. El de la fila "Todos" de esa comida se actualiza |
| 9 | Con los atajos combinados, pulsar "Autorizar" y confirmar | La confirmación de S3 refleja la selección. Éxito y vuelve a la bandeja |
| 10 | Abrir el pedido 9 y pulsar "Rechazar" | Diálogo con el resumen ("…de Quirófano del {fecha}: sus N solicitudes…"), el foco en la textarea y "Rechazar" deshabilitado |
| 11 | Escribir solo espacios | "Rechazar" sigue deshabilitado. El contador cuenta los espacios |
| 12 | Pegar 300 caracteres | La textarea corta en 255 y el contador marca 255/255 |
| 13 | Cancelar (botón y Escape) | Se cierra sin cambios y el foco vuelve al botón "Rechazar" |
| 14 | Escribir "  Falta la guardia  " y confirmar | Toast "Pedido de Quirófano rechazado." Vuelve a la bandeja con los mismos filtros. El 9 figura como Rechazado |
| 15 | Abrir el 9 otra vez | Sin botones ni checkbox. Alerta "Pedido rechazado. Motivo: Falta la guardia" (sin espacios en los extremos). "Revisado por" con el usuario |
| 16 | Entrar como `jefe.quirofano@test.com` y abrir el pedido 9 en Carga | Ve "Pedido rechazado. Motivo: Falta la guardia" |
| 17 | Como `jefe.hemoterapia@test.com`, abrir el pedido 8 | Ve el motivo de los seeds |
| 18 | Pedido 10 en dos pestañas (`alimentacion` y `alimentacion2`). En la primera, autorizar. En la segunda, sin recargar, rechazar con motivo | Toast warning "El pedido ya fue autorizado.", el diálogo se cierra y el detalle se recarga autorizado (sin botón "Rechazar") |
| 19 | Pedido 11 abierto como alimentación. En otra ventana, `jefe.medicos@test.com` le quita una persona. Volver y rechazar | 409 "El pedido cambió mientras se revisaba…", el diálogo se cierra y el detalle se recarga sin esa persona |
| 20 | `curl` reject al pedido 3 con motivo y su versión | 409 con el mensaje de período anterior |
| 21 | `curl` reject al 6 (y al 12) con motivo y su versión | 409 "El pedido ya fue autorizado." |
| 22 | `curl` reject al 8 con motivo y su versión | 409 "El pedido fue rechazado." |
| 23 | `curl` reject al 13 sin `motivo`, con `"motivo": "   "` y sin `modificado_en` | 400 con `fields.motivo` o `fields.modificado_en`. El 13 sigue pendiente |
| 24 | Rechazar el 13 desde el portal | Éxito |
| 25 | Consultar `autorizacion_cambios` (`psql` o `flask shell`) | Filas `tipo=rechazo` del 9 y del 13 (una por solicitud, `autorizada_antes` nulo y `autorizada_despues` falso), más las de los seeds |
| 26 | Con DevTools en "Slow 3G", confirmar un rechazo | Mientras la petición está en curso, "Rechazar" y "Cancelar" del diálogo, Escape, los atajos y los botones de la barra quedan deshabilitados |

Para los `curl`, el token se obtiene con el login de la API (ver `reporte-s1-bandeja-diaria.md` o `openapi.yaml`). La versión sale del `modificado_en` del `GET /api/authorizations/{id}`.

### 7.3 Criterio de terminado

- 7.1 y 7.2 pasan completos, **incluidos los pasos de navegador**.
- La rama está mergeada en local a `feature/autorizacion-pedidos` y **no** está pusheada.
- `seeds-per-slice.md` sigue siendo correcto (paso 1), con la nota de historial de la sección S5 actualizada.
- Está escrito el reporte en `documentacion-personal/reporte-s4-atajos-y-rechazo.md` con la plantilla del artefacto:
  - Encabezado.
  - Alcance.
  - Cambios, con "base de datos: CHECK `ck_pedido_motivo_rechazo` en `pedidos`, aplicado con reset-db" y "dependencias: vitest (dev)".
  - Prueba manual con el resultado de cada paso.
  - Pendientes y decisiones.

## 8. Pendientes para otras slices

- **S5:**
  - `revisar_pedido` acepta autorizados con `corregir` (historial `tipo=correccion`). Cambiar el caso `Disponibles` sin la acción de `_exigir` para `"autorizar"`. Para `"rechazar"` sigue siendo 409.
  - `seleccionInicial` pasa a ser el estado actual cuando se corrige. Los atajos (`todas`, `ninguna`, `soloRegulares`, grupos) se reusan sin cambios.
  - El mensaje del 409 por rechazar un autorizado puede sugerir "corregí su autorización" una vez que el botón funcione.
  - `GET /history` sobre `autorizacion_cambios`: ya tiene filas `autorizacion` y `rechazo`.
- **S11:** navegación por teclado dentro de la matriz (incluidas la columna "Todas" y la fila "Todos") y la vista móvil (lista por persona, con el "Todas" por persona).
- **Coordinación con Carga:**
  - El CHECK del motivo afecta cualquier código que ponga `estado = "rechazado"`. Avisar al que trabaje en Carga y Entregas.
  - Sigue pendiente el bloqueo de fila en `update_pedido` (ver S3).
