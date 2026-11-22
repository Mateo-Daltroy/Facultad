# Reporte S5: Corrección y días anteriores

- **Fecha:** 19/11/2026
- **Rama:** `feat/autorizacion-s5` (sale de `feature/autorizacion-pedidos`, `a7813a6`, con S4 mergeada). Mergeada en local con `--no-ff` (`f9292bc`). **No pusheada.**
- **Commits:**
  - `5bfd174` feat(core): corrección de autorizaciones en revisar_pedido
  - `022c3d4` feat(core): historial de autorización agrupado por operación
  - `70babdd` feat(core): seeds con una corrección del pedido 12
  - `53ad11b` feat(api): historial de autorización y mensaje del reject sobre autorizados
  - `c2f6da5` test: corrección, días anteriores e historial
  - `424c0a3` feat(portal): historial de cambios en el detalle
  - `c7ec945` feat(portal): corregir autorización

Con S5 se cierra el circuito mínimo S1–S5.

## Alcance

Cubre lo que pide el plan (`plan-s5-correccion-y-dias-anteriores.md`):

- En un autorizado de hoy o del futuro y sin entregas, **"Corregir autorización"** pasa la matriz a edición con lo autorizado hoy. Aparece el aviso "Estás corrigiendo la autorización. Lo pedido no cambia." con un contador vivo de cambios (`aria-live="polite"`). Los atajos de S4 funcionan igual, los totales muestran "Quedan autorizadas" junto a "Autorizadas" y la barra cambia a **[Guardar corrección] [Cancelar corrección]**. "Guardar" está deshabilitado sin cambios o mientras se envía.
- "Cancelar corrección" vuelve a solo lectura sin confirmar y devuelve el foco al botón "Corregir autorización". Al entrar en corrección, el foco va al aviso.
- "Guardar corrección" confirma con el resumen de cambios. Si sale bien: toast y vuelta a la bandeja. Si da 409: toast warning, sale de la corrección y recarga.
- En todo pedido revisado, una sección plegable **"Historial de cambios"** con carga diferida (`GET /history` la primera vez que se abre).
- "Revisado por" pasa a **"Última revisión: {usuario}, {fecha y hora}"**.
- API: el mismo `PUT /api/authorizations/{id}` autoriza o corrige. La operación se deduce de `acciones_permitidas`. Una corrección sin cambios da 400. El reject sobre un autorizado da 409 con un mensaje que sugiere corregir. Nuevo `GET /api/authorizations/{id}/history`, agrupado por operación.
- Seeds: el pedido 12 queda con una corrección de `alimentacion2`.

Los días anteriores ya eran de solo lectura desde S2/S3. Se **verificó** (pasos 14, 15 y 18, y `test_detalle_dia_anterior_solo_lectura` / `test_correccion_dia_anterior_409`), sin reescribirlos.

**Criterio "Listo cuando":** ✅ "se corrige un pedido y el historial muestra usuario, hora y valor anterior" (pasos 8 y 9, y `test_historial_agrupa_y_ordena`).

## Cambios

### Base de datos

**Sin cambios de esquema.** Hay que correr `reset-db` + `seeds` para regenerar los datos de demo, porque cambia el pedido 12. La `dev.db` local y lo que corre en 5000/5173 **no** se tocaron.

### Backend (`code/admin`)

- `src/core/autorizacion.py`:
  - `TipoRevision = Literal["autorizacion", "correccion"]` y la guarda `_tipo_de_revision(acciones)` con `match`, como en el plan.
  - `revisar_pedido`: 404 → bloqueo → guarda (409) → versión (409) → cobertura (400) → sin cambios (400, solo corrección). Solo las solicitudes que cambian escriben historial (`tipo` = el de la operación) y actualizan `autorizado_por`/`autorizado_en`. `_cerrar_revision` actualiza el pedido siempre.
  - **Afirmación verificada con un chequeo explícito:** en la autorización inicial, si alguna solicitud de un confirmado ya tiene valor (dato inválido), se lanza `RuntimeError` antes de escribir nada. Así "todas cambian, todas se firman" (comportamiento de S3) queda garantizado (`test_core_autorizacion_inicial_exige_todo_pendiente`).
  - `_exigir` + `_habilitada` se reemplazaron por **`_exigir_rechazar(acciones)`**, que es el único uso que quedaba. Hace `match` sobre `Disponibles(rechazar=True)`, `Bloqueado` y `Disponibles()`; este último da el mensaje nuevo: "El pedido ya fue autorizado: para dejarlo sin efecto, corregí su autorización." El plan permitía esta simplificación.
  - Docstrings actualizados. `acciones_permitidas` no cambia.
- `src/core/autorizacion_cambio.py`:
  - `CambioSolicitud` y `OperacionHistorial` (`frozen`), y `historial_de(pedido_id)`. Un id `<= 0` o inexistente da 404 `NotFoundError`. No bloquea la fila.
  - Una sola consulta con `join` a `Solicitud`, `Personal` y `User`, ordenada por `momento` desc e `id` desc. Se agrupa con `groupby` sobre `(momento, usuario_id, tipo)`, con el valor tal como lo devuelve la base.
  - Dentro de cada operación, los cambios van en el orden del detalle: fecha, comida (constante `ORDEN_COMIDAS` en core, igual a `MEAL_ORDER`) e id de solicitud. Es el mismo criterio que `ordenar_solicitudes`, sin importar `web` desde `core`.
  - Helper `nombre_de_usuario(user)` ("Nombre Apellido"), que también usa ahora `_dump_revision`.
- `src/core/seeds.py`:
  - `Autorizado.correccion` y la constante `USUARIO_DEMO_ALIMENTACION_2`. El `LookupError` ahora también avisa si falta `alimentacion2`.
  - La corrección va después de la autorización y antes de las entregas, con `reloj.ahora()` leído después.
  - `_validar_correccion` lanza `ValueError` si un `PedidoDemo` con `correccion` es de un día anterior o tiene `entregas`. Se valida **antes** de cargar nada.
  - Pedido 12: `correccion=frozenset({("29500004", "almuerzo")})`. Comentario actualizado.
- `src/web/schemas/autorizaciones.py`: `CambioSolicitudSchema`, `OperacionHistorialSchema` (`momento` con `isoformat()`, como `revision.en`) y `historial_output_schema`.
- `src/web/controlers/api_autorizaciones.py`: `GET /<id>/history` con `autorizacion_show`. El PUT no cambia (solo el docstring).
- `docs/openapi.yaml`:
  - PUT: descripción de autorización/corrección, el 400 "no cambia ninguna solicitud" y el 409 sin "ya autorizado".
  - Reject: mensaje nuevo del 409.
  - `GET /history` nuevo.
  - Schemas `AuthorizationHistory`, `AuthorizationOperation` y `AuthorizationChange`.
  - `revision` descrita como "la última revisión".

**Verificado:** en los seeds, la corrección del 12 queda estrictamente después de la autorización (`test_seeds_pedido_12_corregido` lo comprueba con `correccion.momento > autorizacion.momento`). Si alguna vez quedaran iguales, el orden por id las desempata.

### Frontend (`code/portal/src/modules/autorizacion/`)

| Archivo | Qué hace |
|---|---|
| `seleccion.js` | `seleccionActual(dias)` (tira `Error` con una celda no booleana) y `contarCambios(dias, seleccion)` → `{ total, aAutorizada, aNoAutorizada }` (reusa `marcada`, tira `Error` con un id faltante o una celda pendiente). `seleccionInicial` no cambia. |
| `seleccion.test.js` | 8 tests nuevos (ver abajo). |
| `composables/useSeleccionSolicitudes.js` | Parámetro `modo` (`'autorizar' \| 'corregir' \| null`). El `watch` sobre `[pedido, modo]` con `flush: 'sync'` rearma la selección. `aAutorizar` y `conteo` son `null` fuera de la edición. Nuevo `cambios`. Un modo desconocido tira `Error`. |
| `services/autorizacionService.js` | `obtenerHistorial(id)` con parseo estricto (`aOperacionHistorial` y `aCambioHistorial`): `cambios` no vacío, `tipo` en `TIPOS_CAMBIO`, comida en `COMIDAS`, `autorizada_antes` booleano o `null`. |
| `filtros.js` | `TIPOS_CAMBIO` con sus etiquetas ("Autorización", "Corrección", "Rechazo", "Generación"). |
| `components/TotalesComparados.vue` | Prop `etiquetaSeleccion` (default "A autorizar"). El orden de las columnas no cambia: Pedidas · Regular · Excepción · **Quedan autorizadas** · Autorizadas. |
| `components/MatrizSolicitudes.vue` | Comentario actualizado. Leyenda del pie en la corrección: "(quedan autorizadas / pedidas)". Las ramas del template ya priorizaban `editable`, así que en la corrección la celda muestra el checkbox sin "✓ Autorizada" / "✗ No autorizada". |
| `components/HistorialAutorizacion.vue` (nuevo) | `<details class="card">` con carga diferida por `toggle` (`useAsyncData`, una sola vez). Usa `LoadingState`, `ErrorState` con reintentar y "Todavía no hay cambios registrados." si viene vacío. Cada operación: "{fecha y hora} · {usuario} · **{tipo}**" y una lista "Apellido, Nombre · comida[ · dd/mm si el pedido tiene varios días]: antes → después". Después en verde/rojo; la flecha tiene un texto oculto "pasa a" para lectores de pantalla. |
| `views/DetallePedidoView.vue` | `corrigiendo`, `modo`, `editable` y `enCorreccion`. Barra de corrección, aviso con contador, `textoCorreccion`, `textoCambios`, `guardarCorreccion`, `corregir` y `cancelarCorreccion` (con el foco). "Última revisión" e `<HistorialAutorizacion :key="pedido.modificadoEn">`. Comentario de la vista actualizado. |

### Tests

Backend (`tests/unit/test_api_autorizaciones.py`): helpers nuevos `get_historial`, `salvo(*apellidos)` y `correcciones_de`, y los fixtures `alimentacion2` (otro nombre, "Bruno Segundo", para distinguirlo en el historial) y `autorizado` (todo autorizado en `EN_DIA`, con el reloj en `T_COR`).

| Test nuevo | Resultado |
|---|---|
| `test_core_tipo_de_revision` | ✅ |
| `test_put_corrige_autorizado` | ✅ |
| `test_correccion_historial_solo_de_las_que_cambian` | ✅ |
| `test_correccion_autorizado_por_solo_de_las_que_cambian` | ✅ |
| `test_correccion_sin_cambios_400` | ✅ |
| `test_correccion_version_vieja_409` | ✅ |
| `test_correccion_cobertura_400` | ✅ |
| `test_correccion_con_entregas_409` | ✅ |
| `test_correccion_dia_anterior_409` | ✅ |
| `test_correccion_puede_dejar_todo_sin_autorizar` | ✅ |
| `test_autorizacion_inicial_sigue_firmando_todas` | ✅ |
| `test_core_autorizacion_inicial_exige_todo_pendiente` (extra) | ✅ |
| `test_detalle_dia_anterior_solo_lectura` | ✅ |
| `test_historial_pedido_inexistente_404` (9999 y 0) | ✅ |
| `test_historial_sin_revisar_vacio` | ✅ |
| `test_historial_agrupa_y_ordena` | ✅ |
| `test_historial_cambios_en_el_orden_del_dia` (extra: cena y desayuno con ids invertidos) | ✅ |
| `test_historial_rechazo` | ✅ |
| `test_seeds_pedido_12_corregido` (con el fixture `seeded_db`, que corre los seeds completos) | ✅ |
| `test_seeds_correccion_invalida` (extra) | ✅ |
| 401 y 403 (jefe y comedor) de `GET /history`, sumados a la parametrización existente | ✅ |

Reescritos:

- `test_put_autorizado_corregible_409` se borró: su caso lo cubren `test_put_corrige_autorizado` y `test_correccion_sin_cambios_400`.
- `test_authorize_reviewed_request_returns_409` queda solo con `rechazado` (y comprueba el mensaje).
- `test_reject_authorized_request_returns_409` y `test_reject_autorizado_409` usan el mensaje nuevo.

No había otros tests con "ya fue autorizado" ni que comprobaran los datos de los seeds.

Portal (`seleccion.test.js`): `seleccionActual` copia lo autorizado y tira `Error` con una pendiente. `contarCambios`: sin cambios, 2 cambios en sentidos distintos (1 / 1), `todas` cuenta solo las no autorizadas, `Error` con un id faltante y `Error` sobre un pendiente. Además, los atajos de S4 (`soloRegulares` y `alternarGrupo`) sobre una selección de corrección.

Resultados:

- `poetry run pytest`: **648 passed, 4 failed**. Las 4 son las fallas previas de SQLite: `test_core_resumen_por_mes_y_semana` y las 3 de `test_home_controller`.
- `npm test`: **22 passed**.
- `npm run build`: ✅
- `npm run lint`: solo los 3 errores **previos** de `carga-pedidos/components/SelectorPeriodo.vue`. El módulo no tiene errores y pasa prettier.

## Prueba manual

**Preparación:** igual que en S3 y S4, para no tocar `dev.db` ni lo que corre en 5000/5173:

- `reset-db` + `seeds` sobre una SQLite temporal (`jobs/d27cabc2/tmp/s5.db`).
- Flask en el puerto 5055 y Vite en el 5199 (`VITE_API_URL=http://localhost:5055/api`).

Después se apagaron. El recorrido se hizo **en el navegador** (Chromium headless con Playwright; un contexto por usuario). Los pasos 17 a 21 se hicieron con requests HTTP y el 23 con una consulta a la base. Para que el 17 no choque con la entrega del 13, el orden fue: navegador (todo menos el 13) → HTTP (17–21) → navegador (13) → base (23). Hoy = 19/11/2026.

| # | Acción | Resultado |
|---|---|---|
| 1 | Seeds | ✅ sin error. El 12 queda autorizado con 1 no autorizada (29500003, almuerzo). En `autorizacion_cambios`, 1 fila `correccion` del 12 de `alimentacion2`, además de las `autorizacion` y `rechazo` |
| 2 | 12 → "Historial de cambios" | ✅ primero "Corrección" de Bruno Alimentación (Zapata, Ezequiel · almuerzo: No autorizada → Autorizada) y después "Autorización" de Ana con 8 cambios desde "Pendiente". "Última revisión: Bruno Alimentación, 19/11/2026 21:51" |
| 3 | 6 → "Corregir autorización" | ✅ aviso con "Sin cambios" (el foco va al aviso). 8 checkbox: marcado todo salvo Sosa, Oscar (25400003) en el desayuno. Atajos visibles. Columnas `… · Quedan autorizadas · Autorizadas`, iguales (3 / 4). "Guardar corrección" deshabilitado. Leyenda "(quedan autorizadas / pedidas)" |
| 4 | Marcar 25400003 (desayuno) | ✅ "1 cambio", "Quedan autorizadas" del desayuno 3 → 4 (Autorizadas sigue en 3), "Guardar" habilitado |
| 5 | Desmarcarla | ✅ "Sin cambios", "Guardar" deshabilitado |
| 6 | "Cancelar corrección" | ✅ vuelve a solo lectura (sin checkbox ni aviso). El foco vuelve a "Corregir autorización" |
| 7 | Entrar, marcar 25400003, desmarcar 25555666 en el almuerzo, "Guardar corrección" | ✅ "Se van a cambiar 2 solicitudes: 1 pasa a autorizada y 1 a no autorizada. Quedan autorizadas 7 de 8." Cancelar: sigue en la corrección con los 2 cambios |
| 8 | Confirmar (entrando desde la bandeja con `estado=autorizado`) | ✅ toast "Autorización de Intendencia corregida: 2 cambios." y vuelta a `/autorizaciones?fecha=2026-11-19&estado=autorizado`. El 6 sigue Autorizado con 4 / 4 y 3 / 4 (7 / 8) |
| 9 | 6 → historial | ✅ arriba "Corrección" de Ana: "Sosa, Oscar · desayuno: No autorizada → Autorizada" y "Lopez, Carlos · almuerzo: Autorizada → No autorizada". Debajo, la autorización de los seeds (09:00). **Criterio "Listo cuando"** |
| 10 | 6 → Corregir → "Ninguno" → guardar | ✅ "Se van a cambiar 7 solicitudes: las 7 pasan a no autorizadas. Quedan autorizadas 0 de 8." Éxito, sigue Autorizado con 0 / 8 |
| 11 | 12 en Ana y Bruno. Ana corrige (merienda de Zapata); Bruno, sin recargar, corrige otra cosa | ✅ Ana: "…corregida: 1 cambio." Bruno: toast warning "El pedido cambió mientras se revisaba. Se recargó la versión actual.", sale de la corrección (sin aviso ni checkbox) y recarga con "Última revisión: Ana Alimentación". Su historial muestra 3 operaciones: la corrección de Ana, la de Bruno de los seeds y la autorización |
| 12 | Abrir el 7 | ✅ "Ya tiene comidas entregadas: la autorización no se puede corregir." Sin botones. El historial muestra su autorización (6 cambios) |
| 13 | 12 en corrección con un cambio; entrega del almuerzo de Torres para el 22/11 (`create_entrega(fecha=…)` desde un script, porque el Comedor solo entrega hoy); guardar | ✅ toast warning "El pedido ya tiene comidas entregadas.", sale de la corrección y recarga: sin botones, con el aviso de entregas |
| 14 | Abrir el 1 y el 2 | ✅ "Período anterior: solo lectura." Sin botones ni checkbox. El historial se ve (el 2: 10 cambios del 17/11 09:00) |
| 15 | Abrir el 3 | ✅ "Período anterior: solo lectura." Sin botones y **sin** historial |
| 16 | 8 → historial | ✅ una operación "Rechazo" con las 5 solicitudes de "Pendiente" a "No autorizada" |
| 17 | PUT al 12 con sus mismos valores y su versión | ✅ 400 "La corrección no cambia ninguna solicitud." (`fields.solicitudes`) |
| 18 | PUT al 2 con una corrección | ✅ 409 "El pedido es de un período anterior: es solo lectura." |
| 19 | PUT al 7 con una corrección | ✅ 409 "El pedido ya tiene comidas entregadas." |
| 20 | Reject al 12 | ✅ 409 "El pedido ya fue autorizado: para dejarlo sin efecto, corregí su autorización." |
| 21 | `GET /history` del 12, del 9999 y como `jefe.hemoterapia` | ✅ 200 con `[correccion Ana (1), correccion Bruno (1), autorizacion Ana (8)]`; 404 "El pedido no existe."; 403 |
| 22 | `jefe.hemoterapia` abre `/pedidos/12` en Carga | ✅ "Autorizado (solo lectura)": almuerzo 3 y merienda 3 (lo que quedó después del paso 11); Zapata, almuerzo: Autorizada |
| 23 | `autorizacion_cambios` (`tipo=correccion`) | ✅ 6: 2 filas (paso 8), 7 (paso 10) y 8 (paso 24), de `alimentacion`. 12: 1 de `alimentacion2` (seeds) y 1 de `alimentacion` (paso 11). **Ninguna** con `autorizada_antes` nulo |
| 24 | Corrección del 6 ("Todo") con el PUT demorado 1,5 s (route de Playwright en vez de "Slow 3G") | ✅ durante la petición: "Guardar corrección", "Cancelar corrección" y los 3 atajos deshabilitados. Después, "…corregida: 8 cambios." Los checkbox de la matriz **no** se deshabilitan (no tienen prop `disabled`), pero no permiten mandar dos veces |

En la consola del navegador solo aparecen los `Failed to load resource: 409` esperados de los pasos 11 y 13. No hubo errores de JS.

Nota del recorrido: en la primera pasada, el script del paso 7 desmarcó por error el "Todos" del almuerzo en vez de López (el selector también matcheaba ese checkbox). La app hizo lo correcto: 5 cambios, "4 a no autorizadas", 4 de 8. Se corrigió el selector, se resetearon la base y los seeds, y se repitió todo. La tabla es de esa segunda pasada.

`seeds-per-slice.md` actualizado:

- usuario `alimentacion2` ("corrigió el pedido 12");
- tabla general (12: "No aut." = 1, particularidad "corregido por alimentacion2");
- S2 ("lo autorizado 7");
- S4 (mensaje del 409);
- S5 (corrección del 12, 400 sin cambios, concurrencia, reject y nota del historial).

S7 y S10 no dependen de las cantidades autorizadas del 12.

## Pendientes y decisiones

Decisiones de la sesión (desvíos menores del plan):

- **`_exigir_rechazar` en vez de `_exigir(acciones, "rechazar")`:** con un solo uso, `_habilitada` y el `Literal` de dos valores sobraban.
- **Chequeo de la autorización inicial:** `RuntimeError` si un confirmado ya tiene solicitudes decididas, en vez de un `assert`, que se puede desactivar con `-O`.
- **Orden de los cambios del historial:** fecha, comida e **id de solicitud**. Es el mismo criterio que `ordenar_solicitudes`, no por apellido.
- **`historial_de` vive en `core/autorizacion_cambio.py`,** que ahora importa `Pedido`, `Solicitud`, `Personal` y `User`. No hay ciclo: solo lo importan `autorizacion.py` y `database.py` (este último dentro de una función).
- **Paso 13:** la entrega del 12 se registró con un script que llama a `create_entrega(fecha=…)`. La API del Comedor solo entrega en la fecha de hoy.
- **Paso 24:** route de Playwright en vez de "Slow 3G". Los checkbox de la matriz no se deshabilitan mientras se envía (en S3 y S4 tampoco). Los botones y atajos sí.
- **Tests extra:** `test_core_autorizacion_inicial_exige_todo_pendiente`, `test_historial_cambios_en_el_orden_del_dia` y `test_seeds_correccion_invalida`. `test_seeds_pedido_12_corregido` usa el fixture `seeded_db` que ya existía.
- **Historial en un pedido de varios días:** cada cambio muestra la fecha corta (dd/mm). La vista le pasa `varios`.
- **Toast de la corrección:** "Autorización de {sector} corregida: N cambio(s)." en singular o plural.

Para otras slices y para revisar:

- **La `dev.db` local no se reseteó.** Para ver el 12 corregido en el entorno de desarrollo hay que correr `reset-db` + `seeds`. Además, el Vite del 5173 puede necesitar un reinicio para tomar los componentes nuevos (ver lo que pasó en S4).
- La sección 8 del plan sigue vigente:
  - **Entregas:** `create_entrega` no bloquea la fila del pedido. Una corrección y una entrega simultáneas pueden pasar las dos. Hay que revisar que la entrega valide `autorizada is True` en la misma transacción.
  - **Carga:** sigue pendiente el bloqueo de fila en `update_pedido`.
  - **S9:** el panel ya muestra `tipo=generacion` ("Generación").
  - **S11:** la corrección en la vista móvil, el teclado dentro de la matriz, la sección "Ampliaciones de la API" (con `GET /history` y el PUT que corrige) y el recorrido guionado "autorizar parcial, corregir".
