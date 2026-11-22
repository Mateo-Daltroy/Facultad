# Reporte S7: Filtros completos y totales del período

- **Fecha:** 19-20/11/2026
- **Rama:** `feat/autorizacion-s7` (sale de `feature/autorizacion-pedidos`, `8c8648b`, con S6 mergeada). Mergeada en local con `--no-ff` (`e8c51c3`). **No pusheada.**
- **Commits:**
  - `f3e4115` feat(api): filtros de comida y menú en authorizations/week
  - `6f88b5c` test: filtros de la semana y totales del período contra la bandeja
  - `ce04153` feat(portal): filtros de comida y menú en la bandeja
  - `f3f9d01` feat(portal): panel de totales del período

## Alcance

Cubre lo que pide el plan (`plan-s7-filtros-y-totales.md`), **más chico que el del artefacto** (sección 0 del plan):

- El backend de totales (`GET /api/summaries/authorizations` con `condicion`, `agrupar=condicion`, regulares y excepción, `totales` y los 400) **ya existía**: lo hizo Reportes (`97c934c`). No se tocó `core/resumen.py`, `api_resumenes.py` ni `schemas/resumenes.py`.
- Los totales `generada` pasan a S9.
- **Filtros Comida y Menú** en la bandeja, en las vistas diaria y semanal. Van en la URL (`tipo_comida`, `tipo_menu_id`), los limpia "Limpiar filtros" y cuentan para `hayFiltros`.
- **Panel "Totales del período"** debajo de la tabla o de la matriz:
  - el período es el día visible o la semana lunes–domingo;
  - usa todos los filtros de la bandeja y cubre todas las páginas;
  - "Agrupar por" Comida (default), Sector, Menú, Condición y, solo en la vista semanal, Día. Va en la URL (`totales`; el default no se escribe);
  - columnas Pedidas, Pendientes, Autorizadas y No autorizadas, con fila Total;
  - pista cuando hay filtro de comida, menú o condición.

**Criterio "Listo cuando":** ✅ "los totales del panel coinciden con la suma de los pedidos de la bandeja". En D: panel 62 pedidas / 13 autorizadas = suma de la columna Total de la tabla (14 + 29 + 6/6 + 7/8 + 0/5). Para la semana: 115 / 27 = suma de las celdas de la matriz, y cada fila por Día coincide con su columna. Lo cubren `test_totales_del_dia_coinciden_con_la_bandeja` y `test_totales_de_la_semana_coinciden_con_la_matriz`, y los pasos 1, 3 y 9.

## Cambios

### Base de datos

**Sin cambios** de esquema, seeds ni permisos. No hizo falta `reset-db`: la prueba se hizo directamente sobre la `dev.db` del usuario (5000 / 5173), solo con lecturas. `GET /api/menu-types` (`user_auth`) y `resumen_autorizaciones` ya estaban asignados a `alimentacion`.

### Backend (`code/admin`)

- `core/autorizacion_fechas.py`: `semana_de` acepta `tipo_comida` y `tipo_menu_id` (default `None`) y los pasa a `consulta_pedidos`. Docstring: los filtros de comida, menú y condición eligen pedidos y las celdas cuentan el día completo.
- `web/schemas/autorizacion_fechas.py`: `SemanaQuerySchema` suma `tipo_comida` (`OneOf(MEAL_TYPES)`) y `tipo_menu_id` (`Range(min=1)`), igual que `PedidoQuerySchema`. `con_defaults` los devuelve. `week()` no cambió.
- `docs/openapi.yaml`: los dos parámetros en `GET /authorizations/week`, con la aclaración de la semántica y del 400.

### Frontend (`code/portal/src/modules/autorizacion/`)

| Archivo | Qué hace |
|---|---|
| `filtros.js` | `AGRUPAMIENTOS_TOTALES`. `parseFiltros` suma `comida`, `menuId` y `totales` (default `comida`). `aQuery` escribe `tipo_comida`, `tipo_menu_id` y `totales` (salvo `comida`) |
| `totales.js` (nuevo) | `agrupamientosDe`, `agrupamientoEfectivo`, `ordenarFilasTotales`, `etiquetaDeFila`, `combinarTotales` y la clase de error `TotalesInconsistentes` |
| `calendario.js` | `periodoDe(fecha, vista)`: el helper compartido que pedía el plan. Lo usan `PeriodoNav` (antes lo calculaba adentro) y la vista |
| `services/autorizacionService.js` | `listarAutorizaciones` y `semanaDeAutorizaciones` mandan `tipo_comida` y `tipo_menu_id`. Nuevas: `tiposDeMenu`, `paramsDeTotales` (exportada para S10) y `totalesDelPeriodo`, con parseo estricto de cada respuesta del resumen |
| `components/FiltrosAutorizacion.vue` | Selects Comida y Menú (orden: Sector, Estado, Comida, Menú, Condición). Menú se deshabilita con "No se pudieron cargar los menús" |
| `components/TotalesPeriodo.vue` (nuevo) | El panel, con su propio `useAsyncData`, `LoadingState` y `ErrorState` con reintentar adentro |
| `views/BandejaAutorizacionView.vue` | Carga los menús, `limpiar()` y `hayFiltros` con comida y menú, `periodo`, `cambiarAgrupamiento` (conserva `page`) y el panel |

El cliente HTTP ya omite los parámetros `null`/`undefined`/`''` (`buildUrl`), así que no hizo falta filtrarlos en el servicio.

### Tests

Backend:

- `tests/unit/test_api_autorizacion_fechas.py`: `test_semana_filtro_comida_y_menu` y `test_semana_filtros_invalidos_400` (3 casos).
- `tests/unit/test_totales_bandeja.py` (nuevo, 4 tests):
  - `test_totales_del_dia_coinciden_con_la_bandeja`: 4 pedidos (confirmado, autorizado completo, autorizado parcial y rechazado). Para 3 sectores × 4 estados × 4 agrupamientos, pedidas y autorizadas del resumen = suma de la bandeja;
  - `test_totales_de_la_semana_coinciden_con_la_matriz`: incluye un pedido semanal que cruza el domingo y uno de la semana siguiente. Total y cada día de `agrupar=dia` contra la matriz;
  - `test_pendientes_del_resumen_coinciden_con_la_bandeja`: 10 = 4 pendientes + 3 autorizadas + 3 no autorizadas;
  - `test_totales_con_filtro_de_comida_cuentan_solicitudes`: con cena, la bandeja suma 6 y el panel 2.
- `test_api_resumenes.py`, `test_list_pedidos.py` y `test_api_autorizaciones.py` sin modificaciones.

Portal: `totales.test.js` (nuevo, 16 tests, con el ejemplo obligatorio 20 / 10 / 8 / 2), `filtros.test.js` (+5) y `calendario.test.js` (+1, `periodoDe`).

Resultados:

- `poetry run pytest`: **715 passed, 4 failed**. Las 4 son las fallas previas de SQLite (`test_core_resumen_por_mes_y_semana` y las 3 de `test_home_controller`).
- `npm test`: **65 passed**.
- `npm run build`: ✅
- `npm run lint`: solo los 3 errores **previos** de `carga-pedidos/components/SelectorPeriodo.vue`. El módulo no tiene errores y pasa prettier.

## Prueba manual

**Preparación:** Flask del usuario en el 5000 (`--debug`, recargó solo) y Vite en el 5173 (HMR). Recorrido en Chromium headless con Playwright, `alimentacion@test.com`. D = jueves 19/11/2026. A mitad de la prueba pasó la medianoche: el recorrido completo se repitió con `fecha=2026-11-19` fija, con los mismos resultados.

**Números esperados** (curl a `summaries/authorizations`, con y sin `estado=confirmado`): D 62 / 43 / 13 / 6; semana 115 / 78 / 27 / 10. Detalle por comida en `seeds-per-slice.md`, sección S7.

| # | Acción | Resultado |
|---|---|---|
| 1 | Bandeja de D sin filtros | ✅ Desayuno 20/13/6/1, Almuerzo 26/14/7/5, Merienda 6/6/0/0, Cena 10/10/0/0, **Total 62/43/13/6**. La tabla suma 62 pedidas y 13 autorizadas. Cada fila cierra. 2 peticiones al resumen. **Criterio "Listo cuando"** |
| 1b | Estado Pendiente; Autorizado | ✅ Pendiente: Total 43/43/0/0. Autorizado: 14/0/13/1 (la bandeja suma 14 y 13). **1 petición** en cada caso |
| 2 | Agrupar por Sector, Menú y Condición | ✅ URL `totales=sector|menu|condicion` sin `page`, Total 62/43/13/6 en los cuatro. Sector: Alimentación, Hemoterapia, Intendencia, Médicos Internos, Quirófano. Menú: Celiaco, General, Vegetariano. Condición: Regular 43, Excepción 19. Sin opción Día. Al volver a Comida se borra `totales` de la URL |
| 3 | Hemoterapia; + Autorizado; solo Autorizado | ✅ Hemoterapia: bandeja 0/5, panel 5/0/0/5. Con Autorizado la bandeja queda vacía y el panel no se muestra. Solo Autorizado: bandeja 13/14, panel 14/0/13/1 |
| 4 | Merienda en D-1, D y D+3 (pedidos 3, 4 y 12) | ✅ La bandeja muestra solo Hemoterapia / Médicos Internos / Hemoterapia, completos (8, 29 y 8 pedidas). El panel cuenta solo meriendas: 4/4/0/0, 6/6/0/0 y 4/0/4/0. Aparece la pista. Atrás restaura la URL anterior (sin comida) |
| 5 | Menú Celiaco; + Cena | ✅ La bandeja sigue con los 5 pedidos y el panel cuenta 11 celíacas (4/5/1/1 por comida). Cena + Celiaco: 1 |
| 6 | Excepción con agrupamiento Condición | ✅ Solo la fila Excepción 19/14/1/4, con pista |
| 7 | `?tipo_comida=brunch&tipo_menu_id=abc&totales=foo` | ✅ Selects en Todas/Todos, panel por Comida con los números del paso 1, sin errores |
| 8 | "Limpiar filtros" desde la semana con los 5 filtros y `totales=sector` | ✅ `?vista=semana&fecha=2026-11-19&totales=sector`: conserva vista, fecha y agrupamiento |
| 9 | Vista Semana en D | ✅ "Semana del 16 al 22 de noviembre de 2026" y opción Día. Matriz 115 pedidas / 27 autorizadas = Total del panel 115/78/27/10. Por día: mar 17 10, mié 18 8, jue 19 62, vie 20 14, dom 22 21, igual a cada columna |
| 10 | Semana + Cena | ✅ Matriz con Médicos Internos y Quirófano (pedidos 4, 5 y 11; el 13 cae la semana siguiente). Panel jue 19 10, dom 22 5, Total 15 |
| 11 | Vista Día con `totales=dia` | ✅ El select muestra Comida y la tabla agrupa por Comida |
| 12 | Paginación | Omitido: ningún día de los seeds tiene más de 25 pedidos. El panel no depende de la página (`watch` sin `page`, y `v-show` para que no se desmonte al recargar la bandeja) |
| 13 | Route de Playwright que aborta `/api/summaries/authorizations` | ✅ "No se pudieron calcular los totales" con Reintentar dentro del panel. La bandeja sigue con sus 5 filas. Reintentar con la red restaurada muestra los totales |
| 14 | Abortar `/api/menu-types` | ✅ Menú deshabilitado con "No se pudieron cargar los menús". El resto funciona |
| 15 | `curl` `week?fecha=D&tipo_comida=cena`; `brunch`; `tipo_menu_id=abc` | ✅ 200 con Médicos Internos (19 y 22) y Quirófano (19); 400 `fields.tipo_comida`; 400 `fields.tipo_menu_id` |
| 16 | `/reportes` como Alimentación | ✅ Carga igual que antes (no se tocó) |
| 17 | Accesibilidad del panel | ✅ `section` con `aria-labelledby` del `h2`, `caption` oculto ("Totales del período por comida: …"), encabezados con `scope` col/row, `aria-live="polite"` y `aria-busy` en el contenedor, select con `label` "Agrupar por" |

En la consola solo aparecen los errores de red provocados en los pasos 13 y 14.

**Visual:** a 1280 px los cinco filtros y el botón de limpiar entran en una fila, y el panel entra sin scroll. A 390 px los filtros van de a uno y la tabla del panel scrollea dentro de `.table-wrap`. La página tiene scroll horizontal a 390 px (506 px), pero **ya lo tenía en S6** (615 px, medido con el código de S6): viene del layout, no de la bandeja.

## Pendientes y decisiones

Decisiones de la sesión (desvíos menores del plan):

- **`v-show` en vez de `v-if` para el panel.** Con `v-if` el panel se desmonta cada vez que la bandeja recarga (también al cambiar de página) y vuelve a pedir los totales. Con `v-show` queda montado. El `watch` usa una clave de texto para comparar por valor, porque `filtros` es un objeto nuevo en cada cambio de la URL.
- **`min-width: 0` en el panel.** `.stack` es un grid: sin esto, la tabla del panel ensanchaba la página en el móvil en vez de scrollear dentro de `.table-wrap`.
- **Color de Pendientes:** el plan nombraba `--color-info`, que no existe. Se usó `--color-primary`, el mismo del badge Pendiente (`badge--info`).
- **`TotalesInconsistentes`:** clase de error propia de `combinarTotales`. El servicio captura solo esa clase y la relanza como "Respuesta inválida de la API" con `cause`. El parseo verifica además que `totales` sea la suma de `items`.
- **Filtros en una fila:** los campos usan `flex: 1 1 140px` (máximo 220 px) en vez de `min-width: 170px` fijo; si no, a 1280 px los cinco filtros ocupaban tres renglones.
- **`periodoDe` en `calendario.js`:** no existía un helper compartido. `PeriodoNav` lo usa ahora en vez de calcularlo adentro (mismo resultado).
- **Commits del portal:** el servicio y la vista llevan juntos los cambios de los filtros y del panel, así que entraron en el commit del panel.

Para otras slices y para revisar:

- **Pedidos de varios días:** la fila de la bandeja diaria muestra el pedido completo (todas sus fechas), mientras que el panel cuenta solo las solicitudes del día. Con un pedido semanal, la suma de la bandeja diaria es mayor que el Total del panel. En los seeds no pasa (todos los pedidos son de un día). La vista semanal no tiene el problema: sus celdas cuentan por día.
- **S9:** sumar `generada` a `CONDICIONES` y al parseo de `totalesDelPeriodo` (la fila "Generada" al agrupar por condición). Los totales `generada` separados son del endpoint de Reportes: coordinarlo con su responsable.
- **S10:** el CSV del panel tiene que usar `paramsDeTotales` (ya exportada) con `formato=csv`.
- **S11:** "Ampliaciones de la API": `tipo_comida` y `tipo_menu_id` en `/authorizations/week`. El scroll horizontal a 390 px del layout (previo).
- **Coordinación con Reportes:** la bandeja ahora depende de `GET /api/summaries/authorizations` (`items`, `grupo`, `nombre`, `pedidas`, `autorizadas`, `totales` y el filtro `estado`). Cambiar esos nombres rompe el panel. Hay que avisarle al responsable.
- **Opcional:** aplicar comida, menú y condición a las marcas del calendario y a la navegación "con pedidos" (hoy no los aplican, igual que `condicion` en S6).
