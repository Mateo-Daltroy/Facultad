# Reporte S2: Detalle del pedido en solo lectura

- **Fecha:** 19/11/2026
- **Rama:** `feat/autorizacion-s2` (sale de `feature/autorizacion-pedidos`, `d62af6a`, que ya incluye S1 y sus ajustes visuales posteriores a `49406da`). Mergeada en local con `--no-ff` (`f036326`). **No pusheada.**
- **Commits:**
  - `80eed5e` feat(core): reloj, Pedido.modificado_en y acciones_permitidas
  - `8f22b4f` feat(api): detalle de autorización con acciones, revisión y entregas
  - `bd3d55b` test: acciones_permitidas y detalle de autorización
  - `7750fc9` feat(portal): detalle del pedido en solo lectura

## Alcance

Cubre lo que pide el plan (`plan-s2-detalle-pedido.md`):

- Desde la bandeja, el nombre del sector abre `/autorizaciones/pedidos/:id`.
- Matriz persona × comida (una por fecha) con menú, condición, residente y DNI. Si el pedido ya fue revisado, cada celda dice si se autorizó y si se entregó.
- Tabla de totales por comida y menú: pedidas, regular, excepción y, si fue revisado, autorizadas.
- Última modificación, revisor y fecha de revisión, motivo de rechazo y aviso de bloqueo.
- Botones Autorizar / Rechazar / Corregir autorización según `acciones`, con handlers vacíos (S3, S4 y S5).
- "← Volver a la bandeja" con la misma fecha y filtros (`router.back()`), o a la bandeja del día del pedido si se entró por link directo.
- API: `Pedido.modificado_en`, `core/reloj.py`, `acciones_permitidas` con las reglas finales de Etapa 2 (todavía no se usa como guarda) y un detalle propio para `GET /authorizations/{id}`.

Queda afuera lo que el plan deja para otras slices (sección 3).

## Cambios

### Base de datos

Columna nueva `pedidos.modificado_en` (`DateTime(timezone=True)`, `NOT NULL`), aplicada con `reset-db`. No hay migraciones. **Hay que correr `reset-db` + `seeds` sobre cualquier base existente** (ver "Prueba manual": la `dev.db` local no se reseteó).

### Backend (`code/admin`)

- `src/core/reloj.py` (nuevo): `ahora()` y `hoy()` en hora de Buenos Aires. Se importa como módulo (`from . import reloj`) para poder hacer `monkeypatch` en los tests.
- `src/core/pedido.py`:
  - `modificado_en`, con `default=reloj.ahora()` como red para los `INSERT` que no pasan por `create_pedido`.
  - Se asigna de forma explícita en `create_pedido` y `update_pedido`.
  - Relación `revisor` (`foreign_keys=[revisado_por]`).
- `src/core/autorizacion.py`:
  - `Disponibles` / `Bloqueado` / `AccionesPedido` y `acciones_permitidas`, igual que en el plan (con `match`, `ValueError` si el estado es desconocido).
  - `_cerrar_revision` asigna `modificado_en = reloj.ahora()`. `revisado_en` sigue con `datetime.now()`.
- `src/web/schemas/pedidos.py`:
  - `PedidoDetailSchema.modificado_en`.
  - El orden de las solicitudes se saca a `ordenar_solicitudes()`, que usan el detalle del Jefe y el de Autorización.
- `src/web/schemas/autorizaciones.py`: `SolicitudAutorizacionSchema` (agrega `dni` y `es_residente`), `dump_detalle_autorizacion`, `_dump_acciones` (`match` + `TypeError`) y `_dump_revision`.
- `src/web/controlers/api_autorizaciones.py`: `show` usa `ids_entregados`, `acciones_permitidas(pedido, reloj.hoy(), ...)` y el detalle nuevo. `authorize` y `reject` no cambian.
- `docs/openapi.yaml`:
  - `RequestDetail.modificado_en`.
  - Las solicitudes se extraen a `RequestLine`.
  - Schema nuevo `AuthorizationDetail` (con `acciones`, `revision` y `dni` / `es_residente` / `entregada` por solicitud), que es el que devuelve `GET /authorizations/{id}`.

### Frontend (`code/portal/src/modules/autorizacion/`)

| Archivo | Qué hace |
|---|---|
| `routes.js` | Ruta `autorizacion-detalle` (`/autorizaciones/pedidos/:id(\d+)`, `section: 'autorizacion-pendientes'`). |
| `filtros.js` | `MOTIVOS_BLOQUEO`, `textoDeBloqueo`, `formatearFechaLarga` (vuelve, con locale `es`) y `formatearFechaHora`. |
| `services/autorizacionService.js` | `obtenerPedido(id)` y `aPedidoDetalle`. `aTotales` conserva `porMenu`. Valida las reglas del plan: `autorizadas`, `revision` y `autorizada` nulos si y solo si está pendiente, motivo no vacío si y solo si está rechazado, `acciones` coherente y sin comida repetida por persona y fecha. Las personas van en orden de incorporación. |
| `components/TablaPedidos.vue` | El sector es un `RouterLink` con `aria-label` "Ver pedido de …". |
| `components/MatrizSolicitudes.vue` (nuevo) | Matriz de un día. Las celdas no autorizadas van atenuadas, con el menú tachado y el texto "✗ No autorizada". Pie con `autorizadas / pedidas`, o `—` si la comida no tiene solicitudes. |
| `components/TotalesComparados.vue` (nuevo) | Fila de comida (`rowgroup`) + filas por menú (ordenados alfabéticamente) + total. |
| `views/DetallePedidoView.vue` (nuevo) | Encabezado con badge y botones, `dl` de revisión, avisos, totales y matrices. Volver: `router.back()` si la entrada anterior es la bandeja. |

### Tests

Nuevos en `tests/unit/test_acciones_permitidas.py` (sin base de datos):

| Test | Resultado |
|---|---|
| `test_acciones` (9 casos: las 6 filas de la tabla, más confirmado con entregas, autorizado de ayer con entregas y rechazado futuro) | ✅ |
| `test_fecha_desde_es_la_que_manda` | ✅ |
| `test_hoy_no_es_dia_anterior` (confirmado y autorizado) | ✅ |
| `test_estado_desconocido` | ✅ |

Nuevos en `tests/unit/test_api_autorizaciones.py`:

| Test | Resultado |
|---|---|
| `test_show_incluye_modificado_en_y_acciones` | ✅ |
| `test_show_dia_anterior_bloqueado` | ✅ |
| `test_show_revision_y_motivo` | ✅ |
| `test_show_entregada_por_solicitud` (también comprueba que sin entregas es `corregir`) | ✅ |
| `test_show_dni_y_residente` | ✅ |
| `test_modificado_en_cambia_al_modificar_solo_personas` | ✅ |
| `test_modificado_en_cambia_al_autorizar_y_rechazar` | ✅ |
| `test_requests_show_incluye_modificado_en` | ✅ |

`poetry run pytest`: **593 passed, 4 failed**. Las 4 son las mismas fallas previas de SQLite del reporte de S1 (`date_trunc` y `no such table: settings`).

Portal: `npm run build` ✅. `npm run lint` solo tiene los 3 errores **previos** de `carga-pedidos/components/SelectorPeriodo.vue`. El módulo no tiene errores y pasa prettier.

## Prueba manual

Preparación: el permiso de la sesión **bloqueó el `reset-db` sobre `dev.db`**. Para no tocar esa base ni el backend (5000) y el portal (5173) que estaban corriendo, levanté instancias propias:

- `reset-db` + `seeds` sobre una SQLite temporal (`DEV_DATABASE_URL=sqlite:////…/jobs/d27cabc2/tmp/s2.db`).
- Flask en el puerto 5055.
- Vite en el 5199, con `VITE_API_URL=http://localhost:5055/api`.

El recorrido se hizo **en el navegador** (Chromium headless, script de Playwright) y los pasos 13 y 14 también con curl. Hoy = 19/11/2026.

| # | Acción | Resultado |
|---|---|---|
| 1 | Bandeja → Médicos Internos | ✅ `/autorizaciones/pedidos/4`. "Autorizaciones" sigue marcado en el menú. Autorizar y Rechazar no hacen nada y no hay errores en la consola |
| 2 | Matriz del 4 | ✅ 10 filas, 2 "Residente" (Perez, Benítez), 10 excepciones, sin columna de autorización |
| 3 | Totales del 4 | ✅ 8 (6/2), 9 (6/3), 6 (4/2), 6 (3/3); total 29 (19/10). Celiaco / General / Vegetariano por comida. Sin "Autorizadas" |
| 4 | Volver | ✅ vuelve a `/autorizaciones`; Adelante vuelve al detalle |
| 5 | `?estado=autorizado` → pedido → Volver | ✅ vuelve con `?estado=autorizado` |
| 6 | Pedido 6 | ✅ total 8 / autorizadas 7. Sosa (25400003) desayuno "✗ No autorizada". "Ana Alimentación, el 19/11/2026 18:56". Solo "Corregir autorización" |
| 7 | Pedido 7 | ✅ 26600001 y 26600002 "Entregada" en el desayuno. Aviso "Ya tiene comidas entregadas…". Sin botones |
| 8 | Pedido 8 | ✅ badge Rechazado y un solo aviso: "Pedido rechazado. Motivo: No se informó la guardia del día…". 5 "No autorizada", Acosta residente. Sin botones |
| 9 | Día anterior → pedido 3 | ✅ "Período anterior: solo lectura." y sin botones |
| 10 | D+3 → Hemoterapia (12) | ✅ pedidas 8, autorizadas 6 |
| 11 | Link directo en otra pestaña → Volver | ✅ `/autorizaciones?fecha=2026-11-19`. La pestaña nueva pide login antes (la sesión está en `sessionStorage`; es así desde antes de S2) |
| 12 | `/autorizaciones/pedidos/999` | ✅ "El pedido no existe.", sin "Reintentar" |
| 13 | Jefe en `/autorizaciones/pedidos/4` | ✅ "Acceso denegado"; `GET /api/authorizations/4` → 403 |
| 14 | Jefe `GET /api/requests/4` | ✅ trae `modificado_en`, sin `acciones` ni `revision` |

`seeds-per-slice.md` no cambia: los seeds usan `create_pedido`.

## Pendientes y decisiones

Decisiones de la sesión:

- **`aAcciones` es más estricto que el plan:** con `motivo_bloqueo: null` exige al menos una acción en `true`, que es la invariante de `Disponibles`.
- **`revision.en` no tiene fallback:** si `revisado_por` está y `revisado_en` no, el GET falla (500) en lugar de devolver `null`. `_cerrar_revision` siempre asigna las dos.
- **Volver con Ctrl/Cmd/Shift o con el botón del medio:** no se intercepta, así abre la URL de la bandeja en otra pestaña como un link normal.
- **Pie de la matriz:** `—` en las comidas sin solicitudes, en lugar de `0 / 0` (ajuste visual tras ver el pedido 8).
- **Menús en los totales:** se ordenan alfabéticamente, para que el orden sea estable.
- **Regular / excepción por menú** (pedido después de la prueba, `6c7fa6b`): el plan dejaba vacías esas columnas en las filas de menú porque `por_menu` de la API no trae la condición. Ahora el parser (`aDesgloseMenu`) cuenta pedidas, regulares, excepciones y autorizadas por comida y menú a partir de las solicitudes, y verifica que coincidan con `totales.por_menu` y `autorizados.por_menu`; si no coinciden, es respuesta inválida. La API no cambia.
- **Acordeón en los totales** (pedido después de la prueba): cada comida es un botón (`aria-expanded`, `aria-controls` con los ids de sus filas de menú) que muestra u oculta el desglose por menú. Arrancan todas cerradas; "Mostrar menús" / "Ocultar menús" abre o cierra todas. Los totales de la comida se ven siempre. El estado es local de la vista (no se guarda). Probado con clic, Enter y Espacio.
- **"Revisado por"** muestra solo el usuario (`33f5ba9`); la fecha de revisión sigue en `revision.en` de la API.
- **OpenAPI:** la solicitud se extrae a `RequestLine` para poder extenderla en `AuthorizationDetail`.

Para otras slices y para revisar:

- `dev.db` se reseteó y se cargaron los seeds después de la prueba, a pedido del usuario. La base anterior quedó en `jobs/d27cabc2/tmp/dev.db.pre-s2.bak`.
- **Móvil:** a 390px la página tiene scroll horizontal, pero viene del layout general (la bandeja de S1 mide 615px). Nada del detalle desborda fuera de `.table-wrap`. Corresponde a S11.
- **Zona horaria en SQLite:** `modificado_en` vuelve sin zona (`2026-11-19T18:56:51`). El portal lo muestra bien como hora local, pero S3 tiene que normalizar la comparación (ver plan, sección 8).
- **El aviso "Invalid PrimeUI License"** aparece en dev en todas las pantallas. Es previo y no es del módulo.
- Lo de la sección 8 del plan sigue igual (guardas en S3/S4/S5, handlers vacíos, coordinación con Carga por `create_pedido` / `update_pedido`).
