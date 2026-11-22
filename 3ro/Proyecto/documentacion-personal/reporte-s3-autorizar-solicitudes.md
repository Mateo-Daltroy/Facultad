# Reporte S3: Autorizar solicitudes una por una

- **Fecha:** 19/11/2026
- **Rama:** `feat/autorizacion-s3` (sale de `feature/autorizacion-pedidos`, `c2511cf`, con S2 y sus ajustes mergeados). Mergeada en local con `--no-ff` (`8a92192`). **No pusheada.**
- **Commits:**
  - `a28a5a8` feat(core): autorizacion_cambios, fechas con zona y revisar_pedido con versión, cobertura y guarda
  - `7c5874c` feat(api): PUT de autorización con versión y detalle
  - `374749c` test: revisar_pedido, PUT con versión, cobertura e historial
  - `878a321` feat(portal): autorizar solicitudes desde el detalle
  - `b2297d9` style(portal): pie de la matriz en edición con el formato de la bandeja

## Alcance

Cubre lo que pide el plan (`plan-s3-autorizar-solicitudes.md`):

- El detalle de un pedido con `acciones.autorizar` muestra un checkbox por solicitud. Las regulares arrancan marcadas y las excepciones desmarcadas.
- La tabla de totales agrega la columna "A autorizar" (por comida, por menú y total) y el pie de la matriz muestra lo marcado. Las dos cosas se actualizan en vivo.
- "Autorizar" abre una confirmación con los totales. Si sale bien, muestra un toast y vuelve a la bandeja (misma fecha y filtros). Si la API responde 409, muestra un toast warning y recarga el detalle. Si responde 400, muestra un toast error y no recarga.
- API: `PUT /api/authorizations/{id}` con `{ modificado_en, solicitudes }`. Exige todas las solicitudes, usa `acciones_permitidas` como guarda, compara la versión, bloquea la fila, escribe `autorizacion_cambios` y responde con el detalle de autorización.

Queda afuera lo que el plan deja para otras slices (sección 3). Las inconsistencias transitorias aceptadas siguen igual: el 6 y el 12 dicen `corregir: true`, pero el PUT responde 409 "ya fue autorizado" hasta S5. "Corregir" y "Rechazar" siguen como en S2.

**Criterio "Listo cuando":** ✅ con dos sesiones sobre el pedido 9, la segunda en confirmar recibe 409 y ve la versión nueva (paso 8).

## Cambios

### Base de datos

Se aplica con `reset-db`, sin migraciones:

- Tabla nueva `autorizacion_cambios`. El `CHECK` de `tipo` ya admite los cuatro valores.
- `pedidos.revisado_en` y `solicitudes.autorizado_en` pasan a `DateTime(timezone=True)`.

**Hay que correr `reset-db` + `seeds` sobre cualquier base existente.** La `dev.db` local **no** se tocó (ver "Prueba manual").

### Backend (`code/admin`)

- `src/core/autorizacion_cambio.py` (nuevo): `AutorizacionCambio` y `TipoCambio`, igual que en el plan. `tipo` se tipa como `Mapped[TipoCambio]`. Se registra en `database._import_models`.
- `src/core/autorizacion.py`:
  - `VersionPedido` y `version_de`. Es la única función que arma la versión.
  - `RevisionAutorizacion` (frozen y con `MappingProxyType`).
  - `MENSAJES_BLOQUEO`, con una clave por cada `MotivoBloqueo`.
  - `revisar_pedido(pedido_id, usuario_id, revision, ahora)`. Los chequeos van en este orden: 404, bloqueo, guarda (409), versión (409) y cobertura (400).
  - Helpers `_bloquear` (`with_for_update` + `populate_existing`), `_exigir_autorizar` (`match` y `TypeError`) y `_exigir_cobertura`.
  - Se borró `autorizar_pedido`. `_cerrar_revision` recibe `ahora` y lo usa para `revisado_en` y `modificado_en`. `rechazar_pedido` usa `reloj.ahora()`.
- `src/core/seeds.py`: `seed_pedidos_demo` usa `revisar_pedido` (ver Decisiones por el instante que se usa) y `reloj.hoy()`.
- `src/web/schemas/pedidos.py`: `modificado_en = fields.Function(version_de)`.
- `src/web/schemas/autorizaciones.py`: `AutorizarInputSchema` exige `modificado_en` (string no vacío) y en `post_load` devuelve `RevisionAutorizacion`.
- `src/web/controlers/api_autorizaciones.py`:
  - `authorize` llama a `revisar_pedido(..., reloj.ahora())` y responde con `_detalle(pedido)`. `show` también usa `_detalle`.
  - Se quitaron las llamadas previas a `get_pedido_para_revisar` en el PUT y en el reject.
- `docs/openapi.yaml`:
  - PUT: body con `modificado_en`, respuesta 200 `AuthorizationDetail` y descripciones del 400 y del 409.
  - `RequestDetail.modificado_en` se describe como versión opaca (sin `format: date-time` y sin "(S3)").

### Frontend (`code/portal/src/modules/autorizacion/`)

| Archivo | Qué hace |
|---|---|
| `seleccion.js` (nuevo) | Funciones puras: `seleccionInicial`, `contarSeleccion` (`{ marcadas, pedidas, porComida }`) y `totalesAAutorizar` (misma forma que `pedidas`, y una comida sin marcas queda con `total: 0`). Si a la selección le falta un id, tiran `Error`. |
| `composables/useSeleccionSolicitudes.js` (nuevo) | `shallowRef` con un `Map` nuevo en cada cambio. Se rearma con cada pedido nuevo (`watch` con `flush: 'sync'`, ver Decisiones). Devuelve `alternar`, `aAutorizar` y `conteo`. |
| `services/autorizacionService.js` | `autorizarPedido(id, version, seleccion)`: PUT, y la respuesta pasa por `aPedidoDetalle`. |
| `components/MatrizSolicitudes.vue` | Props `editable` y `seleccion`, emite `alternar`. Cada celda tiene un checkbox con `aria-label` "Autorizar {comida} de {Apellido}, {Nombre}" y el `<label>` cubre toda la celda (`::before` absoluto). Las celdas desmarcadas se ven atenuadas. El pie muestra `a autorizar / pedidas`. |
| `components/TotalesComparados.vue` | Prop `aAutorizar`. Agrega la columna "A autorizar" (en color primario) en la comida, en cada menú (`?? 0`) y en el total. |
| `views/DetallePedidoView.vue` | `editable` sale de `acciones.autorizar`. Usa `enviando` para deshabilitar los botones. Agrega `textoConfirmacion`, `volverABandeja()` (la misma lógica del link) y el handler `autorizar`, con el manejo de 409, 400 y el resto de los errores tal como dice el plan. |

### Tests

Se reescribieron los tests que usaban `autorizar_pedido` o bodies parciales. En `test_api_pedidos.py` hay un helper nuevo, `autorizar(pedido, usuario_id, autorizadas, ahora=None)`, que usan `test_api_entregas.py`, `test_api_resumenes.py` y `test_api_autorizaciones.py`.

**Fixture `pedido`:** ahora fija `reloj.ahora` en DIA (20/10/2026) a las 09:00. Hace falta porque hoy real (19/11) es posterior a DIA y la guarda daría `dia_anterior`.

Nuevos en `tests/unit/test_api_autorizaciones.py`:

| Test | Resultado |
|---|---|
| `test_put_faltan_solicitudes_400` | ✅ |
| `test_put_solicitud_ajena_400` | ✅ |
| `test_put_version_vieja_409` (sin historial y sigue confirmado) | ✅ |
| `test_put_jefe_cambio_personas_409` | ✅ |
| `test_put_dia_anterior_409` | ✅ |
| `test_put_autorizado_corregible_409` | ✅ |
| `test_put_rechazado_409` | ✅ |
| `test_put_crea_historial` | ✅ |
| `test_put_no_cambia_lo_pedido` | ✅ |
| `test_put_devuelve_detalle_de_autorizacion` | ✅ |
| `test_core_revisar_es_todo_o_nada` | ✅ |
| `test_core_revisar_relee_la_version` (extra: el pedido ya está en la sesión y la versión cambia en la base. **Falla si se quita `populate_existing`**, probado) | ✅ |

Reescritos: `test_core_autorizar_marca_cada_solicitud` (también comprueba `revisado_en` y `autorizado_en` = `ahora`), `test_core_autorizar_solicitud_ajena_no_cambia_nada`, `test_core_no_se_revisa_dos_veces`, `test_core_pedido_inexistente` (agrega `revisar_pedido` con id 9999 y 0), `test_authorize_by_meal_and_returns_totals`, `test_authorize_with_foreign_request_returns_400`, `test_authorize_with_invalid_body_returns_400` (9 casos, incluidos sin `modificado_en`, con `""` y con `null`), `test_authorize_reviewed_request_returns_409`, `test_authorize_unknown_returns_404`, `test_sector_head_sees_authorized_amounts`, `test_modificado_en_cambia_al_autorizar_y_rechazar` y `test_show_entregada_por_solicitud`.

Resultados:

- `poetry run pytest`: **609 passed, 4 failed**. Las 4 son las fallas previas de SQLite (S1/S2): `test_core_resumen_por_mes_y_semana` y las 3 de `test_home_controller`.
- `npm run build`: ✅
- `npm run lint`: solo los 3 errores **previos** de `carga-pedidos/components/SelectorPeriodo.vue`. El módulo no tiene errores y pasa prettier.

## Prueba manual

**Preparación:** para no tocar `dev.db` ni el backend (5000) y el portal (5173) que estaban corriendo, levanté instancias propias:

- `reset-db` + `seeds` sobre una SQLite temporal (`jobs/d27cabc2/tmp/s3.db`).
- Flask en el puerto 5055.
- Vite en el 5199 (`VITE_API_URL=http://localhost:5055/api`).

Después de la prueba se apagaron. El recorrido se hizo **en el navegador** (Chromium headless con Playwright, en dos contextos para las dos sesiones). Los pasos 11 a 13 se hicieron con requests HTTP. Hoy = 19/11/2026.

| # | Acción | Resultado |
|---|---|---|
| 1 | Seeds | ✅ sin error. Autorizados 1, 2, 6, 7 y 12, con "No aut." 0 / 3 / 1 / 0 / 2, igual que la tabla de `seeds-per-slice.md` |
| 2 | Pedido 4 | ✅ 29 checkboxes: 19 marcados y 10 desmarcados. "A autorizar": 6 / 6 / 4 / 3, total 19 |
| 3 | Marcar la excepción 27200007 del desayuno (clic en una esquina de la celda, no en el checkbox) | ✅ se marca. "A autorizar" del desayuno pasa a 7 y el total a 20. El menú General pasa de 4 a 5. Pie de la matriz: `7 / 8` (ver Decisiones) |
| 4 | Pedido 5 desde la bandeja (`?estado=confirmado`) → Autorizar | ✅ "Se van a autorizar 10 de 14 solicitudes (desayuno 3 de 5, almuerzo 3 de 5, cena 4 de 4). Las 4 restantes quedan como no autorizadas." Cancelar no hace nada |
| 5 | Confirmar | ✅ toast "Pedido de Quirófano autorizado: 10 de 14 solicitudes." Vuelve a `/autorizaciones?estado=confirmado`. En la bandeja de hoy el 5 figura Autorizado con `10 / 14` (el plan decía `14 / 10`, pero el formato de la bandeja es autorizadas / pedidas) |
| 6 | Abrir el 5 | ✅ sin checkboxes, pedidas 14 y autorizadas 10, 4 "No autorizada", "Revisado por Ana Alimentación". Solo el botón "Corregir autorización" |
| 7 | Pedido 9 con `alimentacion` y `alimentacion2`. Autorizar en la primera | ✅ "Pedido de Quirófano autorizado: 6 de 10 solicitudes." |
| 8 | Autorizar en la segunda sin recargar | ✅ toast warning "El pedido ya fue autorizado." y recarga: badge Autorizado, sin checkboxes, solo "Corregir autorización" |
| 9 | Pedido 11 abierto. `jefe.medicos` quita la última persona de cada comida (PUT `/api/requests/11` → 200). Autorizar | ✅ 409 "El pedido cambió mientras se revisaba. Se recargó la versión actual." Recarga con 7 personas (antes 9) y la selección se rearma: 11 checkboxes, 9 marcados |
| 10 | Pedido 3 (D-1) | ✅ sin checkboxes ni botones, con el aviso "Período anterior: solo lectura." |
| 11 | PUT al 3 con todas sus solicitudes y su versión | ✅ 409 "El pedido es de un período anterior: es solo lectura." |
| 12 | PUT al 10 con 3 de 4 | ✅ 400 `fields.solicitudes`: "Faltan las solicitudes [106]." El 10 sigue confirmado |
| 13 | PUT al 10 con sus 4 + una del 9 | ✅ 400 "No pertenecen al pedido: [93]." |
| 14 | Autorizar el 10 con todo marcado | ✅ "Se van a autorizar las 4 solicitudes." y después "Pedido de Intendencia autorizado: 4 de 4 solicitudes." |
| 15 | `autorizacion_cambios` | ✅ filas `tipo=autorizacion` de los seeds (1, 2, 6, 7 y 12) y de la prueba (5, 9, 10 y 13), todas con `autorizada_antes` nulo y `usuario` alimentacion. Los seeds 1 y 2 tienen `momento` = su día a las 09:00 |
| 16 | Autorizar el 13 con el PUT demorado 1,5 s (route de Playwright en vez de "Slow 3G") | ✅ durante la petición, Autorizar y Rechazar quedan `disabled` (Corregir no se muestra en un pendiente). Después, éxito |

En la consola del navegador solo aparecen los dos `Failed to load resource: 409` que el navegador registra solo en los pasos 8 y 9, que son los esperados. No hubo errores de JS.

`seeds-per-slice.md` no cambia: mismos estados y mismas "No aut.".

## Pendientes y decisiones

Decisiones de la sesión (desvíos menores del plan):

- **Pie de la matriz en edición: `a autorizar / pedidas`** (`7 / 8`). El plan pedía "el formato de la bandeja" y daba de ejemplo `8 / 7`. Pero la bandeja (S1) y la matriz revisada (S2) muestran `autorizadas / pedidas`, así que se siguió la regla y no el ejemplo. La leyenda dice "(a autorizar / pedidas)".
- **Instante de autorización en los seeds:** `min(día del pedido a las 09:00, ahora)`, y no siempre el día del pedido. Así los pasados (1 y 2) pasan la guarda igual, y el 12 (D+3) no queda "revisado" en el futuro. El 6 y el 7 quedan a las 09:00 de hoy, salvo que los seeds corran antes de esa hora. `seed_pedidos_demo` usa `reloj.hoy()` en vez de `date.today()`.
- **`_exigir_autorizar(acciones)` sin el parámetro `pedido`:** no lo usaba.
- **`contarSeleccion`** devuelve `{ marcadas, pedidas, porComida: { [comida]: { marcadas, pedidas } } }`. Con eso se arman el texto de la confirmación y el toast.
- **Checkbox con estilo propio** (pedido después de la prueba). El checkbox va a la izquierda del menú y la condición. Es transparente con borde del color principal, y marcado lleva un ✓ (U+2713) del color principal. Tiene `border-radius: 4px`. El `<input>` real queda invisible encima de un `span` decorativo, así conserva foco, teclado y `aria-label`. En edición, una celda desmarcada solo se atenúa: el tachado del menú queda para la vista de solo lectura, donde no hay checkbox.
- **Confirmación en singular:** si queda una sola sin autorizar, dice "La restante queda como no autorizada."
- **Watch con `flush: 'sync'`** en `useSeleccionSolicitudes`. Si no, un `computed` podría evaluarse con el pedido nuevo y la selección vieja. Después de un 409 con personas cambiadas, eso tiraría "La selección no tiene la solicitud…".
- **Sin `try/except` amplio en `revisar_pedido`.** Si un chequeo falla, la sesión se descarta en el teardown de Flask y eso libera el `FOR UPDATE`. `_commit` sigue con su rollback.
- **Parser de `modificado_en` en el portal:** se mantuvo `aFechaHora` de S2, porque el valor también se muestra como "Última modificación". La API ahora lo documenta como opaco. Si algún día la versión deja de ser una fecha ISO, hay que separar los dos usos.

Para otras slices y para revisar:

- **`dev.db` no se reseteó.** Antes de probar en tu entorno: `poetry run flask --app web reset-db`, después `seeds`, y reiniciar el Flask del puerto 5000 (tiene el código viejo cargado y la base no tiene `autorizacion_cambios`).
- **Paso 9:** el Jefe modificó el pedido por la API y no desde la pantalla de Carga. El efecto en el servidor es el mismo.
- La sección 8 del plan sigue vigente:
  - S4: rechazo con guarda, historial y `ahora`; borrar `get_pedido_para_revisar`; atajos.
  - S5: corrección.
  - Coordinación con Carga: `update_pedido` sin bloqueo de fila, y la firma de `_cerrar_revision` y las columnas con zona.
- `delete_pedido` (Jefe) borra pedidos confirmados. Esos pedidos no tienen historial, así que no hay conflicto con la FK de `autorizacion_cambios`. No apareció ningún otro camino que borre solicitudes con historial.
