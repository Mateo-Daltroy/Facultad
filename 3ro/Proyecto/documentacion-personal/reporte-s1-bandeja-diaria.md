# Reporte S1: Bandeja diaria de pedidos

- **Fecha:** 19/11/2026
- **Rama:** `feat/autorizacion-s1` (sale de `feature/autorizacion-pedidos`, `8e4bd56`). Mergeada en local con `--no-ff` (`49406da`, y `30473a9` con los ajustes visuales). **No pusheada.**
- **Commits:**
  - `7e79b3e` feat(api): filtro por condición y orden por estado en /authorizations
  - `50444f0` test: bandeja diaria de autorizaciones (día, condición y orden por estado)
  - `1eb67a9` feat(portal): bandeja diaria de autorizaciones
  - `9278540` style(portal): ajustes visuales de la bandeja de autorizaciones

## Alcance

Cubre lo que pide el plan (`plan-s1-bandeja-diaria.md`):

- Bandeja de los pedidos de un día, todos los sectores, con los pendientes primero y resaltados.
- Navegación al día anterior, al siguiente, a hoy y a una fecha elegida.
- Filtros por sector, estado y condición (Regular o Excepción).
- Botón "Revisar pendientes" / "Ver todos".
- Estado de la bandeja en la URL (`fecha`, `sector_id`, `estado`, `condicion`, `page`).

Queda afuera, como dice el plan: el detalle y el link "Ver detalle" (S2), `core/reloj.py` y `acciones_permitidas` (S2), la vista semanal (S6), los filtros de comida y menú y el panel de totales (S7), CSV (S10), responsive fino y accesibilidad del cierre (S11).

## Cambios

### Base de datos

Sin cambios. No hay migraciones.

### Backend (`code/admin`)

- `src/core/pedido.py` → `list_pedidos` (único cambio en el core):
  - Parámetro `condicion` (entre `tipo_menu_id` y `estado`), validado contra `condiciones_validas` y sumado a la misma condición `Pedido.solicitudes.any(and_(...))` que comida y menú.
  - Orden `fecha_desde desc → estado (confirmado, autorizado, rechazado) → id desc`, con `case(...)`.
  - El único otro llamador (`web/controlers/pedido.py`) usa kwargs, así que el parámetro nuevo no lo afecta.
- `src/web/schemas/autorizaciones.py`: `AutorizacionQuerySchema.condicion` con `OneOf(regular, excepcion)`. `PedidoQuerySchema` no cambia, así que `/api/requests?condicion=...` sigue dando 400.
- Controlador y servicio: sin cambios.
- `docs/openapi.yaml`: parámetro `condicion` y descripción del orden en `GET /authorizations`.

### Frontend (`code/portal/src/modules/autorizacion/`)

| Archivo | Qué hace |
|---|---|
| `filtros.js` (nuevo) | Constantes (`ESTADOS`, `CONDICIONES`, `COMIDAS`, `POR_PAGINA`), helpers de fecha y `parseFiltros` / `aQuery`. `parseFiltros` nunca falla: un valor inválido toma el default. Agregué `parseFechaISO` (exportado) para reusarlo en `PeriodoNav` y `datosDeEstado`. |
| `services/autorizacionService.js` | `listarAutorizaciones(filtros)` y `sectores()`. Las respuestas se convierten una sola vez (`aPedidoResumen`, `aSector`); si falta un campo o un valor no es válido, tira `Error` con el campo y el valor, que la vista muestra con `ErrorState`. |
| `components/PeriodoNav.vue` | Día anterior / input de fecha / Hoy (deshabilitado en hoy) / Día siguiente. |
| `components/FiltrosAutorizacion.vue` | Tres selects que emiten al cambiar y "Limpiar filtros". Rayos aparece como "(inactivo)". Si fallan los sectores, el select queda deshabilitado con el aviso. |
| `components/TablaPedidos.vue` | Sector, estado (badge), las cuatro comidas como `pedidas / autorizadas` (solo pedidas si es pendiente, `—` si no hay, `(N exc.)` debajo) y total. Los pendientes llevan `fila--pendiente`. |
| `views/BandejaAutorizacionView.vue` | Estado derivado de la URL; cada cambio hace `router.push`. Loading / error / vacío / tabla + `Paginator` si hay más de 25. |
| `routes.js` | Apunta a la vista nueva; se mantiene el nombre `autorizacion-pendientes`. |
| `views/AutorizacionPendientesView.vue` | Borrado. |

### Tests

Nuevos en `tests/unit/test_api_autorizaciones.py`:

| Test | Resultado |
|---|---|
| `test_index_filtra_un_dia` | ✅ |
| `test_index_filtra_por_condicion` | ✅ |
| `test_index_condicion_y_comida_sobre_la_misma_solicitud` | ✅ |
| `test_index_condicion_invalida_400` (`foo` y `todas`) | ✅ |
| `test_index_ordena_por_estado_dentro_del_dia` | ✅ |
| `test_requests_no_acepta_condicion` (extra: confirma que `/api/requests` no ganó el parámetro) | ✅ |

`poetry run pytest`: **572 passed, 4 failed**. Las 4 fallas son previas a S1 (las verifiqué con `git stash` sobre `8e4bd56`) y vienen de correr los tests en SQLite:

- `test_api_resumenes.py::test_core_resumen_por_mes_y_semana`: `no such function: date_trunc`.
- `test_home_controller.py` (3 tests): `no such table: settings`.

Portal: `npm run build` ✅. `npm run lint` tiene 3 errores **previos**, en `carga-pedidos/components/SelectorPeriodo.vue` (variables sin usar). No los toqué porque el plan dice no tocar otros módulos. El módulo de autorización no tiene errores de lint.

## Prueba manual

Preparación: `reset-db` + `seeds` sobre `dev.db` (SQLite local; quedó una copia del anterior en el directorio temporal del job). D = 19/11/2026.

Verifiqué contra la API real (curl con el token de `alimentacion@test.com`) todos los datos que la bandeja pide. Lo que depende del navegador (clics, URL, historial) lo revisé en el código pero **falta recorrerlo en el navegador**.

| # | Acción | Esperado | Resultado |
|---|---|---|---|
| 1 | Login | `/autorizaciones` con hoy; 4 y 5 Pendiente, 6 y 7 Autorizado, 8 Rechazado | ✅ API: orden `5, 4, 7, 6, 8` (dentro de cada estado, id desc). ⏳ navegador |
| 2 | Columnas del pedido 6 | Pedidas ≠ autorizadas | ✅ API: desayuno 4 / 3, total 8 / 7 |
| 3 | "Revisar pendientes" | `?estado=confirmado`, 4 y 5, botón "Ver todos" | ✅ API (4 y 5). ⏳ navegador |
| 4 | "Ver todos", "← Día anterior" | D-1: solo el 3 | ✅ API |
| 5 | "Hoy", "Día siguiente →" | D+1: 9 y 10 | ✅ API |
| 6 | "Día siguiente →" | D+2 vacío | ✅ API (0 ítems). ⏳ navegador |
| 7 | Sector Médicos Internos | Solo el 4; Rayos "(inactivo)" | ✅ API (`sector_id=1` → 4; Rayos `active=false`). ⏳ navegador |
| 8 | Condición Excepción | 4, 5, 6 y 8 | ✅ API |
| 9 | Copiar la URL en otra pestaña | Misma bandeja | ⏳ navegador |
| 10 | Atrás varias veces | Deshace de a uno | ⏳ navegador |
| 11 | `/autorizaciones?fecha=2026-02-30&estado=foo` | Hoy, sin filtro, sin error | ✅ `parseFiltros` probado con node. ⏳ navegador |
| 12 | Jefe en `/autorizaciones` | "Acceso denegado"; API 403 | ✅ API 403 (jefe y comedor). ⏳ pantalla |

## Pendientes y decisiones

Decisiones de la sesión:

- **Test de orden:** el plan pedía tres pedidos de la misma fecha en sectores distintos, pero el fixture `data` tiene solo dos sectores. Usé el caso real del desempate: Quirófano rechazado y su reemplazo autorizado, más Pediatría pendiente. El pendiente se crea primero (id más chico) para que el orden por id no coincida con el esperado. También se agrega un pedido de otro día para comprobar que la fecha manda sobre el estado.
- **Parseo de las respuestas** más estricto que el del plan: también se valida `sector_name`, que `autorizados` sea `null` en los pendientes y que cada comida de `totales` / `autorizados` tenga `total`, `regular` y `excepcion` enteros.
- **`sectores` se importa como `cargarSectores`** en la vista, para no chocar con la variable de los sectores cargados.
- **`retryable`**: un error de parseo (un `Error` común, sin `retryable`) muestra "Reintentar", igual que el default de `ErrorState`.

Ajustes visuales pedidos después de la primera prueba (`9278540`):

- "Revisar pendientes" / "Ver todos" pasa del encabezado a una fila propia arriba de la tabla, con la nota de formato a la derecha. La fila se ve siempre, también con la bandeja vacía.
- Las cantidades se muestran como **autorizadas / pedidas** (ej. `7 / 8`).
- Los pendientes pierden la línea lateral y usan un **tinte azul** (`--color-info-bg`, el color principal) en la fila y `badge--info` en el badge "Pendiente". Se probó un naranja complementario (`55c7fcc`, más saturado en `eec0ff1`) y se volvió al azul (`3dd5bec`).
- "Limpiar filtros" es un botón con ícono: una **cruz** (SVG inline, porque el portal no tiene librería de íconos), con `aria-label` y `title`. Va pegado a la derecha de los filtros (flex con `space-between`) y baja de línea si no entra. Se probó un embudo tachado, pero la línea no quedaba bien centrada y se reemplazó por la cruz (`3009732`).
- Se quita el subtítulo con la fecha: ya está en el selector. `formatearFechaLarga` se borra porque quedó sin uso.
- Se pasó prettier sobre el módulo.

Para otras slices:

- El fixture de tests tiene solo 2 sectores; si S6/S7 necesitan más, conviene sumar un tercero al fixture.
- Lint previo en `carga-pedidos/SelectorPeriodo.vue` y los 4 tests que fallan en SQLite: no son de este módulo, pero ensucian el criterio "todo en verde".
- Hacer el recorrido de 7.2 en el navegador (pasos marcados con ⏳).
