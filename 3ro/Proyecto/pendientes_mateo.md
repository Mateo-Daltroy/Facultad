# Pendientes de Mateo — Sectores y Personal (Etapa 1)

Basado en la revisión del 15/10/2026 contra `Etapa1_Trabajo Integrador_2026.pdf`
(ver `informe_revision_etapa1.md`). Rama: `feat/crud-personal`.
Rutas relativas a `code/admin/`.

Prioridad: 🔴 lo pide el enunciado y hoy no se cumple · 🟡 cumple a medias / mejora visible en la defensa · ⚪ prolijidad, opcional

---

## Resumen

| # | Módulo | Tarea | Prioridad |
| 1 | Personal | Baja: si tiene pedidos → inactiva, no eliminada | 🔴 |
| 2 | Personal | No asignar un sector inactivo (servidor) | 🔴 |
| 3 | Sectores | No eliminar un sector con personal, usuarios o pedidos | 🔴 |
| 4 | Sectores | No desactivar un sector con usuarios activos | 🔴 |
| 5 | Sectores | Búsqueda por Jefe de Servicio asignado | 🔴 |
| 6 | Sectores | Asociar Jefes de Servicio desde el formulario del sector, con sus validaciones | 🔴 |
| 7 | Sectores | Nombre único sin distinguir mayúsculas | 🟡 |
| 8 | Sectores | Mostrar Jefes y fecha de creación en el listado | 🟡 |
| 9 | Sectores | Seeds con sectores reales del hospital | 🟡 |
| 10 | Layout | Marcar "Personal" como activo en el menú | 🟡 |
| 11 | Sectores | Limpieza del controlador y los templates | ⚪ |
| 12 | Personal | Pendientes sueltos de etapas anteriores | ⚪ |

Orden sugerido: 1 → 2 → 3 → 4 → 5 → 7 → 10 → 6 → 8 → 9 → resto. Del 1 al 5 son cambios chicos en el core con tests. El 6 es el más grande.

---

## 🔴 1. Personal: baja con historial → inactiva

**Enunciado (secc. 6 y API DELETE /api/personnel):** "No se podrá eliminar una persona que tenga pedidos o entregas asociados. En esos casos deberá marcarse como inactiva." "Si la persona no tiene pedidos [...] podrá eliminarse."

**Hoy:** `src/core/personal.py`, `delete_personal`, siempre marca `deleted=True` y conserva el `TODO (pedidos)`. Ahora que existe `Solicitud`, ya se puede implementar.

**Qué hacer:**
- En `delete_personal`, contar las solicitudes de la persona:
  ```python
  tiene_pedidos = db.session.scalar(
      select(Solicitud.id).filter(Solicitud.personal_id == personal.id).limit(1)
  ) is not None
  ```
  Ojo con el import circular: `pedido.py` importa `personal.py`. `solicitud.py` no importa a `personal`, así que importar `Solicitud` desde `personal.py` debería andar. Si no, usar un import local.
- Si tiene solicitudes: `personal.activo = False` (no tocar `deleted`). Si no tiene: `deleted = True`, como ahora.
- Cambiar el valor de retorno para que el llamador sepa qué pasó. Por ejemplo, un `Enum`/`Literal` `"eliminada" | "desactivada"`, o `None` si no existe. Hoy devuelve `bool` y lo usan `test_personal.py:217,224`.
- **Web** (`src/web/controlers/personal.py`, `delete`): flash distinto según el caso, por ejemplo *"La persona tiene pedidos registrados: se marcó como inactiva."*
- **API** (`api_personal.py`, `delete`, que es de Augus pero llama a tu core): el enunciado pide "confirmación de la operación realizada". Avisarle que puede devolver 200 con `{"resultado": "desactivada"}` en lugar de 204. Coordinar con él.
- Cuando existan entregas (etapa 2) hay que sumarlas al chequeo. Dejar un comentario.

**Tests:**
- Una persona con una solicitud (crear un pedido con `create_pedido`) → `activo=False`, `deleted=False`, y sigue apareciendo en `list_personal`.
- Una persona sin pedidos → `deleted=True`, como ahora.
- Controlador: el flash correcto en cada caso.

---

## 🔴 2. Personal: no asignar un sector inactivo

**Enunciado (secc. 5):** "Un sector inactivo no podrá utilizarse para nuevas asignaciones de personal ni para nuevos pedidos." Secc. 6: "Validar en el cliente y en el servidor: [...] Existencia de los sectores asignados."

**Hoy:** `validate_sector_for_tipo` usa `get_sector_by_id`, que solo descarta los **eliminados**. Por la API, o con un POST armado a mano, se puede crear personal en un sector inactivo. El form web y el CSV ya filtran los activos; el core no.

**Qué hacer:**
- En `validate_sector_for_tipo`, agregar un parámetro `sector_actual_id: int | None = None` y rechazar `sector.active is False` **salvo** que `sector_id == sector_actual_id`. Así, editar a alguien cuyo sector se desactivó después no obliga a moverlo. Es la decisión que ya tomaste en la etapa 2, cuando se muestra "(inactivo)" en el `<select>`.
- `create_personal` → llamar sin `sector_actual_id`.
- `update_personal` → pasar `personal.sector_id`.
- Mensaje: *"El sector está inactivo"*.

**Tests:**
- `create_personal` con sector inactivo → `ValueError`.
- `update_personal` que mantiene un sector que pasó a inactivo → OK.
- `update_personal` que mueve a alguien a otro sector inactivo → `ValueError`.
- API: POST con sector inactivo → 400 (ya lo traduce `services/personal.py`).

---

## 🔴 3. Sectores: no eliminar un sector con personal, usuarios o pedidos

**Enunciado (secc. 5):** "No se podrá eliminar un sector que tenga personal, usuarios o pedidos asociados. En esos casos deberá desactivarse." "La desactivación de un sector no deberá eliminar sus registros históricos."

**Hoy:** `src/core/sector.py`, `delete_sector`, hace la baja lógica sin chequear nada.

**Qué hacer:**
- En `delete_sector`, antes de marcar `deleted`, contar:
  - personal no eliminado con `sector_id` igual al del sector (`Personal.deleted.is_(False)`),
  - usuarios con ese `sector_id`,
  - pedidos con ese `sector_id` (`core.pedido.Pedido`; `pedido.py` importa `sector.py`, así que va con import local).
- Si hay alguno, lanzar `BusinessRuleError` (de `core/errors.py`, hereda de `ValueError`) con un mensaje que diga qué tiene, por ejemplo *"No se puede eliminar: el sector tiene 3 personas, 1 usuario y 2 pedidos asociados. Desactivelo en su lugar."*
- **Decisión a tomar:** ¿rechazar con el mensaje o **desactivar automáticamente**? El enunciado dice "deberá desactivarse". Yo propongo rechazar y explicar, porque una acción "Eliminar" que en realidad desactiva sorprende. Pero desactivar sola también es defendible. Si eligen desactivar, igual tiene que respetar la regla 4 (usuarios activos).
- Controlador `sector.delete`: hoy no captura `ValueError`. Agregar `try/except ValueError` con flash `"error"` y redirect al listado.
- La función `Sector.status_label` y el estado "Eliminado" no cambian.

**Tests** (`tests/unit/test_sector.py`, ya hay `test_delete_sector_applies_soft_delete`):
- Un sector con personal, uno con usuario y uno con pedido → error y `deleted` sigue en `False`.
- Un sector vacío → baja OK.
- Controlador: flash de error y redirect.

---

## 🔴 4. Sectores: no desactivar un sector con usuarios activos

**Enunciado (secc. 2, reglas de asignación):** "No se podrá desactivar un sector con usuarios activos."

**Hoy:** `update_sector(active=False)` no verifica nada.

**Qué hacer:**
- En `update_sector`, si `active is False and sector.active` (es decir, si pasa de activo a inactivo), buscar `User` con `sector_id == sector.id` y `active.is_(True)`. Si hay, lanzar `BusinessRuleError`: *"No se puede desactivar: hay N usuarios activos asociados (Jefes de Servicio). Reasígnelos o bloquéelos primero."*
- Validar **antes** de modificar el objeto, igual que hiciste en `update_personal`, para no dejarlo a medio actualizar en la sesión. Hoy `update_sector` asigna `sector.name` antes de validar `active`.
- El personal activo **no** impide desactivar. Solo los usuarios.

**Tests:**
- Un sector con un jefe activo → error al desactivar.
- Un sector con un jefe bloqueado → OK.
- Un sector sin usuarios → OK.

---

## 🔴 5. Sectores: búsqueda por Jefe de Servicio asignado

**Enunciado (secc. 5):** "Funcionalidad de búsqueda por: Nombre. Estado. Jefe de Servicio asignado."

**Hoy:** `list_sectors(manager_user_id=...)` ya existe en el core, pero `controlers/sector.py: index` no lo lee y `sectors/index.html` no tiene el filtro.

**Qué hacer:**
- Core: una función `list_jefes_de_servicio()` que devuelva los usuarios con rol `jefe_servicio` (el rol se busca por nombre con `validators.SECTOR_ROLE`), ordenados por apellido y nombre. Ubicarla en `core/auth/__init__.py`, que es de Bauti, o en `core/sector.py`.
- Controlador `index`: leer `manager_user_id` como entero (si no es válido se descarta, igual que tu `_leer_filtros` de personal) y pasarlo a `list_sectors`. Pasar también los jefes al template.
- Template: un `<select name="manager_user_id">` con "Todos los jefes" + la lista. Agregar `manager_user_id` a los `url_for` de la paginación.

**Tests:** filtrar por jefe devuelve solo su sector. Con un id inválido, el filtro se ignora.

---

## 🔴 6. Sectores: asociar Jefes de Servicio desde el sector

**Enunciado (secc. 5):** "Permitir asociar uno o más usuarios con rol Jefe de Servicio a un sector." Validar en el servidor:
- "Existencia de los usuarios seleccionados."
- "Que los usuarios asignados tengan rol Jefe de Servicio."
- "Que un Jefe de Servicio no se encuentre asociado a otro sector."

Más el alcance: "Asignar responsables a los sectores."

**Hoy:** solo se asigna desde el módulo de usuarios (el sector en el form del usuario). Desde Sectores no hay nada.

**Qué hacer:**
- **Core** (`core/sector.py`): `asignar_jefes(sector_id, user_ids: list[int])`, que:
  1. verifica que el sector exista y esté **activo** ("No se podrá asociar un usuario a un sector inactivo");
  2. busca todos los usuarios; si falta alguno → error *"Uno o más usuarios no existen"*;
  3. si alguno no tiene rol `jefe_servicio` → error;
  4. si alguno tiene `sector_id` distinto de `None` y del sector → error *"X ya es jefe del sector Y"*;
  5. asigna `user.sector_id = sector_id` y hace `commit`.
- **Decisión a tomar — desasignar:** un Jefe de Servicio **tiene que** tener sector (secc. 2), así que quitarlo de un sector lo dejaría inválido. Opciones:
  - a) desde Sectores solo se **agregan** jefes, y para sacar uno se lo mueve a otro sector o se le cambia el rol desde Usuarios. Es lo más simple y coherente con las reglas.
  - b) permitir quitar, solo si el jefe está bloqueado.

  Recomiendo (a).
- **Form** (`sectors/new.html` y `sectors/edit.html`): un `<select multiple name="jefes">` con los jefes **sin sector** + los que ya son de este sector (preseleccionados). Solo HTML, sin JS. Al crear un sector, se asignan después de `create_sector`.
- **Controlador:** `request.form.getlist("jefes", type=int)`. Va en la misma transacción que crear/editar o justo después. Si falla, flash de error y se vuelve a renderizar el form con lo elegido.
- Coordinar con **Santi**: su form de usuario también asigna sector. Las dos vías tienen que usar las mismas reglas. Lo ideal es que las dos llamen a la misma función del core.

**Tests:** cada una de las 4 validaciones, más el caso feliz con 2 jefes en el mismo sector.

---

## 🟡 7. Sectores: nombre único sin distinguir mayúsculas

**Enunciado:** "Validar que no existan dos sectores con el mismo nombre."

**Hoy:** en `validate_sector_name` se compara `Sector.name == working_name`, así que "Urgencias" y "urgencias" pueden coexistir.

**Qué hacer:** comparar con `func.lower(Sector.name) == working_name.lower()`. Considerar si los sectores **eliminados** deben bloquear el nombre (hoy no lo hacen; con personal decidiste que el DNI queda ocupado). Para sectores tiene sentido dejarlo libre, como está.

**Test:** crear "Urgencias" y después "urgencias" → "Nombre duplicado".

---

## 🟡 8. Sectores: listado más completo

**Enunciado:** ordenar por fecha de creación (ya funciona), y los campos mínimos incluyen las fechas de creación y de modificación.

**Qué hacer en `sectors/index.html`:**
- Columna **Fecha de creación**. Hoy se puede ordenar por fecha, pero no se ve.
- Columna **Jefes de Servicio** (nombres separados por coma; ya está la relación `sector.users`). Usar `selectinload(Sector.users)` en `list_sectors` para no hacer N+1, igual que en `list_personal`.
- Opcional: en `edit.html`, mostrar la fecha de última modificación.

---

## 🟡 9. Seeds con sectores del hospital

**Enunciado:** "Los sectores/servicios existentes son los siguientes. Instanciar a modo de ejemplo alguno o la totalidad de los mismos": Medicos Internos, Quirofano, Intendencia, Alimentacion, Hemoterapia, etc.

**Hoy:** `src/core/seeds.py`, `SECTORS`, tiene `buffet`, `administracion`, `urgencias` e `internacion`.

**Qué hacer:** reemplazarlos o sumar sectores de la lista (por ejemplo Médicos Internos, Quirófano, Intendencia, Alimentación, Hemoterapia, Rayos; dejar uno inactivo para probar). **Cuidado:** `CUPOS`, `PERSONAL` y `PEDIDOS` de los seeds referencian los sectores por nombre. Hay que actualizarlos también, o mantener los viejos además de los nuevos. Avisar a Sofi (cupos y pedidos), y correr `flask reset-db && flask seeds` para probar.

---

## 🟡 10. Layout: "Personal" activo en el menú

**Enunciado (secc. 1):** "El menú deberá indicar visualmente cuál es el módulo o sección activa."

**Hoy:** en `templates/layout.html`, el `<a>` de Personal no tiene la clase condicional.

**Qué hacer:** copiar el patrón de los otros links:
```jinja
class="enlace-navegacion {% if request.endpoint and request.endpoint.startswith('personal') %}activo{% endif %}"
```
Es del layout de Santi: avisarle o hacerlo vos (es una línea).

---

## ⚪ 11. Limpieza de Sectores

- `controlers/sector.py`: borrar el bloque de ~40 líneas comentadas ("ESQUELETO DE AUTENTICACIÓN") y los imports que no se usan (`wraps`, `session`).
- Dos reglas responden a `POST /sectores/<id>` (update y delete); gana `update`. Funciona porque el borrado usa `/eliminar`, pero confunde en la defensa. Sacar `"POST"` de la regla `/<int:sector_id>` de `delete`.
- Ramas JSON/PATCH/DELETE en el controlador: por tu criterio de "controladores solo HTML" sobran, porque la API de sectores ya está en `api_sectores.py`. **Pero** `test_sector_controller.py` las usa (9 llamadas con `json=`). Si se borran, hay que reescribir esos tests como form POST. Hacerlo solo si sobra tiempo.
- `sectors/index.html`: `{% block flashes %}{% endblock %}` no hace nada (el layout no define ese bloque). El borrado usa `onsubmit="return confirm(...)"`, que es JS inline; en personal usaste `popover`, así que conviene unificarlos. También hay varios `style=""` inline.
- `sectors/edit.html`: vuelve a renderizar los flashes, que ya muestra el layout. Sacarlo.
- `Sector` tiene `created_at` / `updated_at` (de `BaseModel`) **y** además `inserted_at` / `updated_at` propios. Al redefinir `updated_at` se pisa el de `BaseModel`, y quedan dos columnas de creación. Unificar en `created_at` toca la API de Augus (`schemas/sectores.py`) y el orden por `inserted_at`; evaluarlo.
- Docstrings: `validate_sector_*` y `get_sector_by_id` están bien. Revisar que todas las funciones del controlador tengan.

---

## ⚪ 12. Personal: pendientes sueltos

- Borrar o mover `src/core/personal-specs_delete.md`, que quedó en el core.
- Restaurar personal eliminado: hoy el DNI queda ocupado para siempre. Con el punto 1, solo se eliminan los que no tienen pedidos, así que hay menos casos, pero sigue pasando.
- Vínculo User↔Personal para Jefes de Servicio. El enunciado dice que "los Jefes de Servicio [...] pueden ser personal autorizado para las comidas". Hoy se cargan por separado con el mismo DNI. Alcanza con explicarlo en la defensa; es para la etapa 2.
- `docs/personal.md`: actualizar la sección de baja cuando esté el punto 1. Sigue pendiente acordar con el grupo lo del permiso `personal_index` de comedor.

---

## Cosas del grupo en las que podés ayudar (no son tuyas)

- **Logging** (nadie lo hizo y el enunciado lo pide): `logging.basicConfig` en `create_app`, más `app.logger.exception(...)` en el handler de 500 y logs de login/logout. Son unas 10 líneas.
- **Tag `v1.0.0`** cuando se mergee todo a `main` para la entrega.
- **Tests de usuarios** (8 fallan): no tocar, es de Santi y Bauti. Pero verificá que sigan pasando los tuyos después de mergear sus arreglos (`pytest tests/unit/test_personal*.py tests/unit/test_sector*.py tests/unit/test_api_personal.py tests/unit/test_api_sectores.py`).

---

## Checklist final antes de entregar

- [ ] 1–5 implementados con tests
- [ ] `pytest` completo: tus módulos en verde
- [ ] `flask reset-db && flask seeds` + prueba manual: alta/edición/baja de personal y de sector, importar CSV, filtros
- [ ] Probar como Jefe de Servicio: ve su sector + residentes y no gestiona residentes
- [ ] Commit(s) en `feat/crud-personal` → merge a `development` (sin push hasta confirmarlo con el grupo)
