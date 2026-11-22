# Reporte S4: Atajos masivos y rechazo con motivo

- **Fecha:** 19/11/2026
- **Rama:** `feat/autorizacion-s4` (sale de `feature/autorizacion-pedidos`, `8b0f539`, con S3 y sus ajustes mergeados). Mergeada en local con `--no-ff` (`62cc31b`). **No pusheada.**
- **Commits:**
  - `4d75348` feat(core): rechazar_pedido con motivo obligatorio, versión, guarda e historial
  - `9b80944` feat(core): CHECK del motivo de rechazo
  - `c385e13` feat(api): reject con versión y detalle de autorización
  - `a58130e` test: reglas del rechazo
  - `f22cb96` build(portal): vitest
  - `20bd8eb` feat(portal): atajos de selección
  - `052a51a` feat(portal): rechazar pedido con motivo

## Alcance

Cubre lo que pide el plan (`plan-s4-atajos-y-rechazo.md`):

- En un pedido pendiente, una barra de atajos **[Todo] [Solo regulares] [Ninguno]** sobre todos los días, con el conteo vivo ("19 de 29 marcadas", `aria-live="polite"`).
- En cada matriz, la columna **"Todas"** (por persona) y la fila **"Todos"** (por comida), con checkbox tri-estado que actúan solo sobre el día de esa matriz. La esquina queda vacía y una comida sin solicitudes en el día no tiene checkbox.
- "Rechazar" abre un diálogo con el resumen y el motivo obligatorio (contador `n/255`). Éxito → toast y vuelta a la bandeja; 409 → se cierra, toast warning y recarga; 400 → el diálogo queda abierto con el error bajo el campo y el texto escrito.
- API: `POST /api/authorizations/{id}/reject` con `{ motivo, modificado_en }` obligatorios. Guarda con `acciones_permitidas`, versión, bloqueo de fila, historial `tipo=rechazo` y respuesta con el detalle de autorización. `CHECK` en la base para que un rechazado siempre tenga motivo.

Queda afuera lo que el plan deja para otras slices (sección 3). Sigue la inconsistencia transitoria aceptada: el 6 y el 12 dicen `corregir: true`, pero el PUT y el reject responden 409 "El pedido ya fue autorizado." hasta S5.

**Criterio "Listo cuando":** ✅ "Solo regulares" deja las 10 excepciones del pedido 4 sin autorizar (pasos 2 y 5) y toda la cena del 13 (paso 6). El Jefe ve el motivo del rechazo (pasos 16 y 17, y `test_jefe_ve_motivo_del_rechazo`).

## Cambios

### Base de datos

- `CHECK ck_pedido_motivo_rechazo` en `pedidos`: `estado <> 'rechazado' OR motivo_rechazo IS NOT NULL`. Aplicado con **reset-db** (no hay migraciones).

**Hay que correr `reset-db` + `seeds` sobre cualquier base existente.** La `dev.db` local **no** se tocó (ver "Prueba manual").

### Dependencias

- `vitest` **5.1.1** (dev, versión exacta). Sus peer dependencies aceptan `vite ^6.4.0 || ^7.0.0 || ^8.0.0`, así que se instaló sin `--force`. Script `"test": "vitest run"`. Sin config propia: alcanza con la de Vite.
  - Ojo: 5.1.1 se publicó el 18/11/2026, un día antes. El plan pedía la más reciente compatible y es esa; si preferís una con más rodaje, `5.0.x` también acepta Vite 8.

### Backend (`code/admin`)

- `src/core/autorizacion.py`:
  - `MotivoRechazo` (`NewType`) con su único parser `parse_motivo`: `strip`, no vacío y hasta `MAX_MOTIVO_LENGTH` (255). Si no, `BusinessRuleError(field="motivo")`.
  - `RechazoPedido` (`frozen`) con `version` y `motivo`.
  - `rechazar_pedido(pedido_id, usuario_id, rechazo, ahora)`: 404 → bloqueo de fila → guarda (409) → versión (409). Una fila `tipo=rechazo` por solicitud cuyo valor cambia.
  - `_exigir(acciones, accion)` reemplaza a `_exigir_autorizar`. Usa un helper `_habilitada` con `match accion` (lanza `ValueError` si la acción no es `"autorizar"` ni `"rechazar"`), en vez de `getattr`.
  - `_exigir_version` extraída de `revisar_pedido`, con el mismo mensaje y `field="modificado_en"`.
  - Se borraron `get_pedido_para_revisar`, `validate_motivo` y los imports `reloj` y `get_pedido_by_id`, que quedaron sin uso.
- `src/core/pedido.py`: `__table_args__` con el `CHECK`.
- `src/core/seeds.py`: el caso `Rechazado` usa `rechazar_pedido` con `RechazoPedido(version_de(pedido), parse_motivo(motivo))`. `cuando` se calcula una vez antes del `match` y lo usan los dos casos.
- `src/web/schemas/autorizaciones.py`: `RechazarInputSchema` con `motivo` y `modificado_en` requeridos; el `post_load` devuelve `RechazoPedido`. El largo y el "no vacío" los decide `parse_motivo`.
- `src/web/controlers/api_autorizaciones.py`: `reject` llama a `rechazar_pedido(..., reloj.ahora())` y responde `_detalle(pedido)`. Se quitó el import de `pedido_detail_schema`.
- `docs/openapi.yaml`: reject con body requerido (`RejectInput` con los dos campos), 200 `AuthorizationDetail`, descripciones del 400 y del 409. `RequestDetail.motivo_rechazo`: presente (no vacío) si y solo si `estado == "rechazado"`. La descripción de `modificado_en` menciona el reject.

**Verificado:** el `BusinessRuleError` lanzado en el `post_load` llega tal cual al handler del blueprint (marshmallow no lo envuelve) y sale como 400 `business_rule` con `fields.motivo`. No hizo falta convertirlo a `ValidationError`.

Revisé el resto de `src`: ningún otro camino pone `estado = "rechazado"`.

### Frontend (`code/portal/src/modules/autorizacion/`)

| Archivo | Qué hace |
|---|---|
| `seleccion.js` | Nuevas: `todas`, `ninguna`, `soloRegulares` (fija la selección), `seleccionInicial` (= `soloRegulares`), `idsDeComida`, `idsDePersona`, `estadoGrupo` y `alternarGrupo`. Un grupo vacío o un id que falta tiran `Error`. |
| `seleccion.test.js` (nuevo) | 14 tests de Vitest (ver abajo). Importa `describe/it/expect` de `vitest`. |
| `composables/useSeleccionSolicitudes.js` | `marcarTodas`, `marcarSoloRegulares`, `desmarcarTodas` y `alternarGrupo`. Todas reemplazan la selección por un `Map` nuevo. |
| `services/autorizacionService.js` | `rechazarPedido(id, version, motivo)`: POST y la respuesta pasa por `aPedidoDetalle`. |
| `components/AtajosSeleccion.vue` (nuevo) | Tres `btn--secondary btn--small` en un `role="group"`, prop `disabled` y el conteo vivo. |
| `components/MatrizSolicitudes.vue` | Con `editable`: columna "Todas", fila "Todos" (segunda fila del `thead`, con fondo suave), celda vacía en la esquina y en el pie. Tri-estado con `:checked` / `:indeterminate`, emite `alternar-grupo(ids)`. Variante indeterminada del checkbox propio: "–" (U+2013) del color principal. Los grupos se calculan en dos `computed` (por comida y por persona). |
| `components/DialogoRechazo.vue` (nuevo) | `<dialog>` + `showModal()` como `ConfirmDialog`, con estilos propios. Resumen, textarea (`maxlength=255`, `required`, `aria-describedby` al contador y al error, `aria-invalid` con error), contador, error `role="alert"`. "Rechazar" deshabilitado con el motivo vacío o mientras se envía. Emite `confirmar(motivo.trim())` y `cancelar`. Al abrirse limpia el texto y enfoca la textarea; al cerrarse, el foco vuelve al botón que lo abrió. |
| `views/DetallePedidoView.vue` | Atajos entre los totales y las matrices, `@alternar-grupo`, `resumenRechazo`, `rechazar()` y `confirmarRechazo()` como en el plan. Comentario de la vista actualizado. |

### Tests

Backend: se agregaron `post_rechazo` y `rechazo_de` en `test_api_autorizaciones.py`. Por el `CHECK`, `set_estado` (`test_api_pedidos.py`) y `test_core_pedido_rechazado_no_se_entrega` (`test_api_entregas.py`) asignan un motivo.

| Test nuevo | Resultado |
|---|---|
| `test_core_parse_motivo` | ✅ |
| `test_reject_motivo_vacio_400` (falta la clave, `""`, `"   "`, `None`) | ✅ |
| `test_reject_motivo_255_con_espacios_ok` | ✅ |
| `test_reject_sin_version_400` (sin la clave y con `""`) | ✅ |
| `test_reject_campo_desconocido_400` (extra) | ✅ |
| `test_reject_version_vieja_409` | ✅ |
| `test_reject_jefe_cambio_personas_409` | ✅ |
| `test_reject_dia_anterior_409` | ✅ |
| `test_reject_rechazado_409` | ✅ |
| `test_reject_autorizado_409` | ✅ |
| `test_reject_crea_historial` | ✅ |
| `test_reject_devuelve_detalle_de_autorizacion` | ✅ |
| `test_check_motivo_rechazo_en_base` | ✅ |
| `test_jefe_ve_motivo_del_rechazo` | ✅ |

Reescritos: `test_core_no_se_revisa_dos_veces`, `test_core_pedido_inexistente` (ids 9999 y 0), `test_put_rechazado_409`, `test_reject_with_reason` (también `acciones` y `revision`), `test_reject_without_body` (ahora 400 con `motivo` y `modificado_en`), `test_reject_authorized_request_returns_409` (mensaje), `test_reject_with_too_long_reason_returns_400`, `test_show_revision_y_motivo` y `test_modificado_en_cambia_al_autorizar_y_rechazar`.

Portal (`seleccion.test.js`): `soloRegulares` y `seleccionInicial`, `todas` y `ninguna`, `soloRegulares` fija la selección, `idsDeComida` (incluida una comida sin solicitudes → `[]`), `idsDePersona`, los tres estados de `estadoGrupo` y el `Error` con `[]` o un id faltante, `alternarGrupo` (algunas → todas → ninguna, ninguna → todas, no toca otros ids y no muta el `Map`), y un caso de `contarSeleccion`, de `totalesAAutorizar` y del `Error` por id faltante.

Resultados:

- `poetry run pytest`: **627 passed, 4 failed**. Las 4 son las fallas previas de SQLite: `test_core_resumen_por_mes_y_semana` y las 3 de `test_home_controller`.
- `npm test`: **14 passed**.
- `npm run build`: ✅
- `npm run lint`: solo los 3 errores **previos** de `carga-pedidos/components/SelectorPeriodo.vue`. El módulo no tiene errores y pasa prettier.

## Prueba manual

**Preparación:** igual que en S3, para no tocar `dev.db` ni lo que corre en 5000/5173:

- `reset-db` + `seeds` sobre una SQLite temporal (`jobs/d27cabc2/tmp/s4.db`).
- Flask en el puerto 5055 y Vite en el 5199 (`VITE_API_URL=http://localhost:5055/api`).

Después se apagaron. El recorrido se hizo **en el navegador** (Chromium headless con Playwright; varios contextos para las sesiones). Los pasos 20 a 23 y 25 se hicieron con requests HTTP y consultas a la base. Hoy = 19/11/2026.

| # | Acción | Resultado |
|---|---|---|
| 1 | Seeds | ✅ sin error. Estados y "No aut." iguales a la tabla de `seeds-per-slice.md`. El 8 queda rechazado con su motivo y 5 filas `tipo=rechazo` (`autorizada_antes` nulo, `autorizada_despues` falso) |
| 2 | Pedido 4 | ✅ encabezado `Persona · Todas · Desayuno…`, fila `Todos · (vacía) · ☐ ☐ ☐ ☐`. 19 de 29 marcadas. "Todos" de las cuatro comidas indeterminados; "Todas" marcado para Álvarez, Castro y Domínguez, indeterminado para Giménez, Herrera e Ibáñez, vacío para Juárez, Ledesma, Perez y Benítez |
| 3 | "Todo" | ✅ 29 marcadas, "29 de 29 marcadas", todos los grupos marcados, "A autorizar" 8 / 9 / 6 / 6 = lo pedido |
| 4 | "Ninguno" | ✅ 0 marcadas, "A autorizar" en 0 en las cuatro comidas, grupos vacíos |
| 5 | "Solo regulares" | ✅ 19 marcadas (igual a la carga inicial) |
| 6 | "Todo" y después "Solo regulares" en el 13 | ✅ las 4 cenas desmarcadas, "Todos" de la cena vacío, "6 de 10 marcadas" |
| 7 | "Todos" del desayuno (indeterminado) en el 4 | ✅ pasa a marcado (no indeterminado), las 8 del desayuno marcadas, el resto igual (19 → 21). Otro clic: las 8 desmarcadas (21 → 13) |
| 8 | "Todas" de Perez y de Benítez (una sola comida) | ✅ marca solo esa celda; el "Todos" del almuerzo y de la cena sigue indeterminado (quedan otras excepciones sin marcar), que es lo correcto. Extra: "Todas" de Álvarez (4 comidas) alterna las 4; Espacio sobre un checkbox de grupo enfocado funciona igual que el clic |
| 9 | "Todo" + desmarcar "Todos" de la merienda → Autorizar | ✅ "Se van a autorizar 23 de 29 solicitudes (desayuno 8 de 8, almuerzo 9 de 9, merienda 0 de 6, cena 6 de 6). Las 6 restantes quedan como no autorizadas." Toast de éxito y vuelve a la bandeja |
| 10 | Pedido 9 (desde la bandeja de D+1 con `estado=confirmado`) → Rechazar | ✅ "Se va a rechazar el pedido de Quirófano del viernes 20 de noviembre de 2026: sus 10 solicitudes quedan como no autorizadas. El Jefe de Servicio va a ver el motivo." Foco en la textarea y "Rechazar" deshabilitado |
| 11 | Solo espacios | ✅ "Rechazar" deshabilitado, contador `3/255` |
| 12 | Pegar 300 caracteres | ✅ queda en 255, contador `255/255` |
| 13 | Cancelar (botón y Escape) | ✅ se cierra, el foco vuelve a "Rechazar" y el 9 sigue confirmado. Al reabrir, la textarea está vacía |
| 14 | "  Falta la guardia  " y confirmar | ✅ toast "Pedido de Quirófano rechazado." Vuelve a `/autorizaciones?fecha=2026-11-20&estado=confirmado` (mismos filtros; el 9 ya no figura porque no está pendiente). Sin filtro, el 9 figura Rechazado |
| 15 | Abrir el 9 | ✅ sin botones, checkbox ni atajos. "Pedido rechazado. Motivo: Falta la guardia" (sin espacios). "Revisado por Ana Alimentación" |
| 16 | `jefe.quirofano` abre `/pedidos/9` en Carga | ✅ "Pedido rechazado. Motivo: Falta la guardia" |
| 17 | `jefe.hemoterapia` abre `/pedidos/8` | ✅ ve el motivo de los seeds |
| 18 | Pedido 10 en `alimentacion` y `alimentacion2`; autorizar en la primera, rechazar en la segunda | ✅ toast warning "El pedido ya fue autorizado.", el diálogo se cierra y recarga: badge Autorizado, solo "Corregir autorización" |
| 19 | Pedido 11 abierto; `jefe.medicos` quita la última persona de cada comida (PUT `/api/requests/11` → 200); rechazar | ✅ 409 "El pedido cambió mientras se revisaba. Se recargó la versión actual.", el diálogo se cierra y recarga con 7 personas (antes 9), sigue Pendiente |
| 20 | Reject al 3 con su versión | ✅ 409 "El pedido es de un período anterior: es solo lectura." |
| 21 | Reject al 6 y al 12 | ✅ 409 "El pedido ya fue autorizado." |
| 22 | Reject al 8 | ✅ 409 "El pedido fue rechazado." |
| 23 | Reject sin `motivo`, con `"   "`, sin `modificado_en` y sin body | ✅ 400 con `fields.motivo` / `fields.modificado_en` (sin body: los dos). Se hizo sobre el **5** porque el 13 ya estaba rechazado por el paso 24 (ver Decisiones). Sigue confirmado |
| 24 | Rechazar el 13 desde el portal | ✅ "Pedido de Alimentación rechazado." |
| 25 | `autorizacion_cambios` | ✅ filas `tipo=rechazo` del 8 (5), 9 (10) y 13 (10), todas con `autorizada_antes` nulo, `autorizada_despues` falso y usuario `alimentacion`. Además las `tipo=autorizacion` de los seeds y de los pasos 9 y 18 |
| 26 | Rechazo del 13 con el POST demorado 1,5 s (route de Playwright en vez de "Slow 3G") | ✅ durante la petición: "Cancelar" y "Rechazar" del diálogo deshabilitados (el botón dice "Rechazando…"), la textarea `readonly`, Escape no cierra, los 3 atajos y los 2 botones de la barra deshabilitados |

Extra: con el reject respondiendo 400 (simulado con Playwright), el diálogo queda abierto, muestra el mensaje bajo el campo (`role="alert"`, `aria-invalid="true"`, `aria-describedby` con el contador y el error) y conserva lo escrito.

En la consola del navegador solo aparecen los dos `Failed to load resource: 409` de los pasos 18 y 19, que son los esperados. No hubo errores de JS.

`seeds-per-slice.md`: los datos no cambian. Se actualizó la nota de historial de la sección S5: los pedidos de demo ya tienen filas `autorizacion` y `rechazo`.

## Pendientes y decisiones

Decisiones de la sesión (desvíos menores del plan):

- **`_exigir` con `match accion`** (helper `_habilitada`) en vez de `getattr`, como sugería el plan. El caso por defecto lanza `ValueError`.
- **Textarea `readonly` mientras se envía** y el botón dice "Rechazando…". No estaba en el plan; evita editar un motivo que ya se mandó.
- **Resumen de varios días:** con un solo día dice "del {fecha}"; con varios usa el período ("del … al …") sin repetir "del".
- **El `resumen` solo se calcula con el diálogo abierto.** Así el `Error` por `conteo === null` no salta en pedidos que no se pueden rechazar.
- **Fila "Todos" con fondo suave** (`--color-bg`) para separarla de las personas.
- **Paso 23 sobre el pedido 5:** el recorrido automatizado rechazó el 13 (paso 24, junto con el 26) antes de los requests HTTP. La regla que se prueba es la misma.
- **Test de "soloRegulares fija la selección":** como los atajos no reciben la selección anterior, el test comprueba que el resultado después de `todas` es el mismo que el de `soloRegulares` sola y que las excepciones quedan en `false`.
- **Test extra** `test_reject_campo_desconocido_400`.
- **Después de la prueba (revisión del usuario):**
  - El "Todas" de Gomez, Maria no desmarcaba nada ni cambiaba el pie. La causa era el Vite del 5173, que seguía sirviendo un `DetallePedidoView.vue` anterior a S4: sin atajos, sin diálogo y sin `@alternar-grupo`, aunque la matriz ya era la nueva. El checkbox cambiaba solo en el DOM y nadie escuchaba el evento. Se reinició ese Vite (y se borró `node_modules/.vite`); el código no tenía el bug. Si vuelve a pasar, reiniciar `npm run dev`.
  - Se aprovechó para que toda la celda de "Todas" y "Todos" alterne el grupo, como las celdas de comida (`fe8a77a`). Antes solo respondía el cuadrito.
  - `dev.db` se reseteó con `reset-db` + `seeds` (backup en `jobs/d27cabc2/tmp/dev.db.pre-s4.bak`).

Para otras slices y para revisar:

- **`dev.db` ya está reseteada** con el `CHECK` y los seeds de S4. El Flask del 5000 corre con `--debug`, así que ya recargó el código.
- **Paso 19:** el Jefe modificó el pedido por la API y no desde la pantalla de Carga. El efecto en el servidor es el mismo.
- La sección 8 del plan sigue vigente:
  - S5: `_exigir` para `"autorizar"` con `Disponibles(corregir=True)`, `seleccionInicial` desde el estado actual, el mensaje del 409 de rechazar un autorizado y `GET /history`.
  - S11: teclado dentro de la matriz (incluidas "Todas" y "Todos") y vista móvil.
  - Coordinación con Carga: avisar del `CHECK` del motivo a quien toque Carga o Entregas, y sigue pendiente el bloqueo de fila en `update_pedido`.
