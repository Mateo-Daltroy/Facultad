# Reporte S6: Calendario y vista semanal

- **Fecha:** 19/11/2026
- **Rama:** `feat/autorizacion-s6` (sale de `feature/autorizacion-pedidos`, `f9292bc`, con S5 mergeada). Mergeada en local con `--no-ff` (`8c8648b`). **No pusheada.**
- **Commits:**
  - `6e61932` test: caracterización de list_pedidos
  - `a0aa313` refactor(core): consulta_pedidos con los filtros de list_pedidos
  - `42dcc93` feat(core): fechas con pedidos, adyacentes y semana de autorizaciones
  - `8991bbe` feat(api): authorization-dates, adjacent y authorizations/week
  - `9266517` test: fechas con pedidos, navegación con pedidos y vista semanal
  - `90d35e8` feat(portal): calendario de días con pedidos
  - `da4c2dc` feat(portal): navegación con pedidos y vista semanal

## Alcance

Cubre lo que pide el plan (`plan-s6-calendario-y-vista-semanal.md`):

- **Selector Día / Semana** en la bandeja. `vista` va en la URL solo si es `semana`: la URL de la vista diaria queda igual que antes.
- **Vista diaria:**
  - "← Anterior con pedidos" / "Siguiente con pedidos →" saltan al día con pedidos más cercano, con los filtros de sector y estado y sin límite de distancia.
  - "Hoy" sigue igual.
  - El `<input type="date">` se reemplazó por un botón con la fecha larga que abre el calendario en un popover.
- **Vista semanal:**
  - Matriz sector × día (lunes a domingo) con badge y "autorizadas / pedidas", o solo "pedidas" si está pendiente. Cada celda enlaza al detalle; las vacías muestran "—".
  - El encabezado de cada día lleva a la vista diaria. Se marca la columna de hoy.
  - "← Semana anterior con pedidos" / "Semana siguiente con pedidos →" saltean las semanas vacías.
  - Los filtros y "Revisar pendientes" funcionan. No hay paginación.
- **Calendario** (`CalendarioDias`):
  - Grilla lunes–domingo con ‹ ›.
  - Días sin pedidos deshabilitados. Hasta 3 puntos por estado, conteos en `aria-label` y `title`, y los días con pendientes resaltados.
  - Hoy (`aria-current="date"`, subrayado) y el día elegido (`aria-pressed`, borde) se distinguen.
  - Al elegir un día se cierra y la bandeja va a ese día (o a su semana).
- **API:** `GET /api/authorization-dates`, `GET /api/authorization-dates/adjacent` y `GET /api/authorizations/week`. Permiso nuevo `autorizacion_dates`.

**Criterio "Listo cuando":** ✅ "en un mes con tres días cargados, la navegación recorre solo esos tres". Con el filtro Hemoterapia la navegación y el calendario recorren solo D-1, D y D+3 (pasos 4 y 11, y `test_adyacentes_respeta_filtros` / `test_fechas_filtro_sector_y_estado`).

## Cambios

### Base de datos

**Sin cambios de esquema.** Hay que correr `reset-db` + `seeds` por el permiso nuevo `autorizacion_dates`: `seed_auth` no agrega permisos a un rol que ya existe. Sin eso, el calendario y la navegación dan 403. Los pedidos de demo no cambian.

**Esta vez sí se reseteó la `dev.db` local** (lo pidió el usuario), con una copia previa en `jobs/d27cabc2/tmp/dev.db.pre-s6.bak`. El Flask del 5000 (con `--debug`) recargó solo y el Vite del 5173 se reinició.

### Backend (`code/admin`)

- **Refactor `consulta_pedidos`** (`src/core/pedido.py`): el bloque de filtros de `list_pedidos` se movió sin reescribirlo a `consulta_pedidos(...) -> Select`. `list_pedidos` queda como `consulta_pedidos` + el mismo `order_by`, el mismo clamp y `paginate`. No se tocaron `web/services/pedidos.py`, `api_pedidos.py` ni el controlador de la aplicación privada. Ver "Regresión" más abajo.
- **`src/core/autorizacion_fechas.py`** (nuevo):
  - `RangoFechas` (`frozen`) con `parse(desde, hasta, max_dias=62)`, que lanza `BusinessRuleError(field="fecha_hasta")` con "No puede ser anterior a fecha_desde." o "El rango no puede superar los 62 días.", y `dias()`.
  - `fechas_con_pedidos`: una consulta de `(id, estado, fecha_desde, fecha_hasta)` sobre `consulta_pedidos(...).subquery()`. La expansión por día (recortada al rango) y el conteo se hacen con un `Counter`. El mapeo de estado a campo es explícito y un estado desconocido lanza `ValueError`.
  - `fechas_adyacentes`: dos consultas portables por lado (`exists` + `max`/`min`), sin `least`/`greatest`. El docstring explica por qué el paso 1 es correcto.
  - `semana_de`: `consulta_pedidos` con `selectinload` de solicitudes y sector. Por celda cuenta solo las solicitudes del día; `autorizadas` es `None` en un confirmado. Dos pedidos en la misma `(sector, día)` lanzan `RuntimeError`. Sectores ordenados por nombre (sin mayúsculas) e id. Las celdas son `MappingProxyType`.
- **`src/web/schemas/autorizacion_fechas.py`** (nuevo, para no inflar `autorizaciones.py`): `FechasQuerySchema`, `AdyacentesQuerySchema` (sin tope) y `SemanaQuerySchema`, con `unknown = RAISE`, más los `dump_*`. El `BusinessRuleError` lanzado en `post_load` lo mapea el manejador existente a 400 con `fields.fecha_hasta`: no hizo falta convertirlo a `ValidationError`.
- **Controladores:** `api_autorizacion_fechas.py` (blueprint `/authorization-dates`, registrado junto a `api_autorizaciones_bp`) y `GET /authorizations/week` en `api_autorizaciones.py`, con `autorizacion_index`.
- **`src/core/seeds.py`:** `autorizacion_dates` dentro de `AUTORIZACION_PERMISSIONS`, que ya va a `alimentacion` y `administrador`.
- **`docs/openapi.yaml`:** los 3 endpoints y los schemas `AuthorizationDates`, `AuthorizationDateCount`, `AdjacentDates`, `AuthorizationWeek`, `AuthorizationWeekSector` y `AuthorizationWeekCell`.

### Frontend (`code/portal/src/modules/autorizacion/`)

| Archivo | Qué hace |
|---|---|
| `filtros.js` | `VISTAS`, `vista` en `parseFiltros` (default `dia`) y en `aQuery` (solo `semana`). Formateos `formatearDiaYMes`, `formatearMes`, `formatearDiaCorto` y `formatearSemana` (con mes o año en los dos extremos si cambian). |
| `calendario.js` (nuevo) | `inicioDeSemana`, `diasDeSemana`, `mesDe`, `sumarMeses`, `grillaDelMes` y `rangoDeGrilla`. Tiran `Error` con un mes o una fecha mal formados. |
| `services/autorizacionService.js` | `fechasConPedidos` (`Map`, sin fechas repetidas ni fuera del rango, al menos un conteo > 0), `fechasAdyacentes` (`anterior < desde`, `siguiente > hasta`) y `semanaDeAutorizaciones` (7 días consecutivos desde el lunes de `fecha`, claves de `celdas` en `dias`, `autorizadas` `null` si y solo si está pendiente, `autorizadas <= pedidas`). |
| `components/CalendarioDias.vue` (nuevo) | Grilla del mes con marcas, carga por mes (`watch` sobre mes, sector y estado), todos los días deshabilitados y "Cargando días con pedidos…" mientras carga, `ErrorState` con reintentar, foco inicial y leyenda. |
| `components/PeriodoNav.vue` (reescrito) | Navegación con pedidos (vista diaria o semanal), "Hoy" y el botón de fecha con el popover (`role="dialog"`). Se cierra con Escape, clic afuera, Tab hacia afuera o al elegir. El calendario se monta solo con el popover abierto. |
| `components/SelectorVista.vue` (nuevo) | Grupo Día / Semana con `aria-pressed`. |
| `components/TablaSemana.vue` (nuevo) | Matriz sector × día con `caption` oculto, encabezados que llevan al día y la columna de hoy marcada. Las celdas pendientes llevan el fondo de `fila--pendiente`. |
| `views/BandejaAutorizacionView.vue` | Un solo `useAsyncData` cuyos datos llevan la vista con la que se pidieron (`{ vista: 'dia', pagina }` o `{ vista: 'semana', semana }`), así nunca se dibuja una vista con los datos de la otra. Estado vacío por vista y la pista "Cantidades" debajo de la matriz. |

### Tests

Backend:

- `tests/unit/test_list_pedidos.py` (nuevo, caracterización, 15 tests): cada filtro, combinaciones, comida + menú + condición sobre la misma solicitud, superposición de rango, orden exacto, paginación (`page` 2, `per_page` 0/1/100, tope de 25 con 27 pedidos), normalización y los errores con su mensaje y orden.
- `tests/unit/test_pedido_controller.py` (nuevo, caracterización, 5 tests): listado de la aplicación privada con un jefe en la sesión: solo su sector y en orden, filtros, y `flash` + redirect con un filtro inválido.
- `tests/unit/test_api_autorizacion_fechas.py` (nuevo, 39 tests): todos los de la tabla 5.8 del plan. Además: parámetros inválidos (`sector_id=0`, parámetro desconocido, formato de fecha), estado desconocido en core, invariante de la celda doble, rango invertido en `adjacent` y extremos de un pedido largo. Los 401/403 de los 3 endpoints van en parametrizaciones nuevas de este archivo, no en las de `test_api_autorizaciones.py`.
- Los tests de caracterización y los de `test_api_pedidos.py` / `test_api_autorizaciones.py` pasaron **sin modificaciones** después del refactor.

Portal: `calendario.test.js` (12) y `filtros.test.js` (9), nuevos.

Resultados:

- `poetry run pytest`: **707 passed, 4 failed**. Las 4 son las fallas previas de SQLite: `test_core_resumen_por_mes_y_semana` y las 3 de `test_home_controller`.
- `npm test`: **43 passed**.
- `npm run build`: ✅
- `npm run lint`: solo los 3 errores **previos** de `carga-pedidos/components/SelectorPeriodo.vue`. El módulo no tiene errores y pasa prettier.

### Regresión del refactor (paso 0)

Además de los tests, se comparó el comportamiento **antes y después** sobre la misma base con los seeds (SQLite temporal). Se usó un `git worktree` del commit `6e61932` (sin el refactor) y el código actual. Un script volcó:

- `list_pedidos` para el producto cartesiano de sector, desde, hasta, comida (incluidas `" cena "`, `"Cena"` y `"brunch"`), menú, condición (`"Excepcion "`, `"foo"`), estado (`" Autorizado"`, `"foo"`) y paginación (`(2, 3)`, `per_page` 0 y 100);
- `GET /api/requests` como jefe de Hemoterapia y `GET /api/authorizations`, con 14 queries cada uno (filtros válidos e inválidos, paginación, rango invertido);
- el listado `/pedidos` de la aplicación privada con un jefe, con ids, `flash` y redirección.

Resultado: **115.235 casos, archivos idénticos byte a byte**. Se verificó que cada corrida cargara su `core/pedido.py` (el viejo sin `consulta_pedidos`). Esto reemplaza la comparación manual en el navegador del paso 0. Las pantallas de Carga y de la aplicación privada usan exactamente esas salidas.

## Prueba manual

**Preparación:**

- `reset-db` + `seeds` sobre una SQLite temporal (`jobs/d27cabc2/tmp/s6.db`). Flask en el 5055 y Vite en el 5199. Recorrido en Chromium headless con Playwright. Hoy = D = jueves 19/11/2026.
- Al final, el recorrido de los pasos 1 a 12 se repitió contra el **5173 / 5000 del usuario** después de resetear `dev.db`, con los mismos resultados.

| # | Acción | Resultado |
|---|---|---|
| 0 | Regresión del refactor | ✅ ver arriba: 115.235 casos idénticos |
| 1 | Login como Alimentación | ✅ `/autorizaciones` sin `vista`, "Día" marcado, botón "Jueves 19 de noviembre de 2026", "Hoy" deshabilitado |
| 2 | "Siguiente con pedidos →" | ✅ D → D+1 → D+3 → D+8. En D+8 queda deshabilitado |
| 3 | "← Anterior con pedidos" desde D+8 | ✅ D+3 → D+1 → D → D-1 → D-2 → D-7. En D-7 queda deshabilitado. El foco sigue en el botón |
| 4 | Sector Hemoterapia y navegar desde D | ✅ adelante D → D+3; atrás D → D-1. **Criterio "Listo cuando"** |
| 5 | Estado Autorizado | ✅ atrás D → D-2 → D-7; adelante D → D+3 |
| 6 | Abrir el calendario | ✅ popover con noviembre 2026, `aria-expanded=true`. Habilitados solo D-7, D-2, D-1, D, D+1, D+3 y D+8. Puntos: D-7 y D-2 autorizado, D-1 pendiente, D los tres, D+1 pendiente, D+3 pendiente + autorizado, D+8 pendiente. Resaltados los que tienen pendientes. Foco en D (`aria-current="date"`, `aria-pressed="true"`) |
| 7 | `aria-label` / `title` de D | ✅ "jueves 19 de noviembre: 2 pendientes, 2 autorizados, 1 rechazado". Un día vacío: "sábado 21 de noviembre: sin pedidos" |
| 8 | ‹ › | ✅ diciembre y octubre se cargan, sin días habilitados (D-7 y D+8 caen en noviembre). Vuelve a noviembre con las marcas |
| 9 | Elegir D+3 | ✅ se cierra, la bandeja muestra Médicos Internos y Hemoterapia (11 y 12) y el foco vuelve al botón de fecha |
| 10 | Escape; clic afuera | ✅ se cierra sin cambiar de fecha. Con Escape, el foco vuelve al botón |
| 11 | Calendario con Hemoterapia | ✅ solo D-1, D y D+3 habilitados |
| 12 | "Semana" en D | ✅ `vista=semana&fecha=2026-11-19`. "Semana del 16 al 22 de noviembre de 2026". Encabezados Lun 16 … Dom 22, con Jue 19 marcado. 5 sectores con badge y cantidades. 6 celdas pendientes resaltadas. "Hoy" deshabilitado |
| 13 | Clic en una celda y "Volver" | ✅ abre `/autorizaciones/pedidos/7` (`aria-label` "Pedido de Alimentación del jueves 19 de noviembre de 2026: autorizado, 6 de 6 autorizadas"). "Volver" regresa a la misma URL semanal |
| 14 | Clic en "Mar 17" | ✅ vista diaria del 17 (Quirófano), sin `vista` en la URL |
| 15 | "Semana siguiente con pedidos →" | ✅ salta a la semana del 23 al 29 con un solo pedido (el 13, Alimentación, pendiente 10). Ahí "siguiente" queda deshabilitado. Dos veces "anterior" lleva a la semana del 9 al 15 (D-7), con "anterior" deshabilitado |
| 16 | Volver a "Día" desde `vista=semana&fecha=D+1` | ✅ `fecha=2026-11-20` (no el lunes) |
| 17 | Condición Excepción + "Revisar pendientes" | ✅ filtran las celdas (Alimentación desaparece; con pendientes solo quedan Hemoterapia 18, Médicos 19 y 22, y Quirófano 19 y 20). Una semana vacía muestra "No hay pedidos en esta semana con estos filtros" con "Limpiar filtros" |
| 18 | `vista=semana` con un miércoles y con el domingo | ✅ los dos muestran la semana del 16 al 22 |
| 19 | `vista=foo` | ✅ vista diaria |
| 9b | (extra) En la vista semanal, elegir D+8 en el calendario | ✅ `vista=semana&fecha=2026-11-27`, semana del 23 al 29 |
| 20 | Sin rango, invertido, 63 días | ✅ 400 con `fecha_desde`/`fecha_hasta`; "No puede ser anterior a fecha_desde."; "El rango no puede superar los 62 días." |
| 21 | `authorization-dates` D-7..D+8 | ✅ los 7 días con los conteos exactos de la tabla de S6 |
| 22 | `adjacent` D..D; D+8..D+8 | ✅ `{anterior: D-1, siguiente: D+1}`; `siguiente: null` |
| 23 | `week?fecha=D`; sin `fecha` | ✅ 200 con la forma de 5.4; 400 `fields.fecha` |
| 24 | Los tres como jefe, comedor y sin token | ✅ 403, 403, 401 |
| 25 | Red lenta (route de Playwright con 1,5 s) y red caída | ✅ mientras carga, "anterior/siguiente" con `aria-disabled` y un clic no navega. El calendario muestra todos los días deshabilitados, `aria-busy` y "Cargando días con pedidos…", y después habilita 7. Con la red caída: "No se pudo calcular la navegación." (con `aria-describedby`), la bandeja sigue mostrando sus pedidos y el calendario muestra su `ErrorState` con reintentar |

En la consola solo aparecen los `ERR_INTERNET_DISCONNECTED` provocados en el paso 25.

**Visual:** a 1280 px la matriz semanal entra sin scroll horizontal. En la vista semanal, la navegación ocupa dos renglones ("Semana siguiente con pedidos" baja). A 390 px el popover del calendario entra en la pantalla y la matriz scrollea dentro de `.table-wrap`.

## Pendientes y decisiones

Decisiones de la sesión (desvíos menores del plan):

- **`aria-disabled` en vez de `disabled` en "anterior/siguiente con pedidos".** Con `disabled`, el botón perdía el foco al recalcular (cada clic lo deshabilita mientras carga) o al llegar al extremo. Con `aria-disabled` conserva el foco, se ve deshabilitado y el clic no hace nada. "Hoy" sigue con `disabled`, como antes.
- **La grilla del calendario es un `<table>` sin `role="grid"`.** `grid` promete navegación con flechas, que queda para S11. Por ahora se usan Tab entre los días habilitados, Enter y Escape.
- **El foco inicial del calendario** va después de la primera carga, porque hasta entonces todos los días están deshabilitados.
- **El popover también se cierra cuando el foco sale con Tab** (sin moverlo).
- **Schemas en `web/schemas/autorizacion_fechas.py`** (el plan lo permitía).
- **401/403 de los endpoints nuevos** en parametrizaciones propias de `test_api_autorizacion_fechas.py`.
- **Datos de la bandeja con la vista adentro** (`{ vista, pagina | semana }`), para no dibujar la matriz con datos de la vista diaria durante el cambio.
- **Paso 0 automatizado** (comparación byte a byte de 115.235 casos) en vez de comparar a ojo en el navegador.
- **Paso 25** con route de Playwright en vez de "Slow 3G" de DevTools.
- La pista "Cantidades" en la vista semanal va solo debajo de la matriz.

Para otras slices y para revisar:

- **Carga:** el refactor `consulta_pedidos` toca `core/pedido.py`. Hay que avisarle al responsable y señalarle `tests/unit/test_list_pedidos.py` y `test_pedido_controller.py`. La caracterización fijó un detalle: `tipo_comida` solo se recorta (`" cena "` filtra, `"Cena"` da "Tipo de comida invalido"), mientras que `estado` y `condicion` además se pasan a minúsculas.
- **S7:** totales en la vista semanal y filtros de comida y menú (se suman a `semana_de` vía `consulta_pedidos`).
- **S10:** CSV de la vista semanal.
- **S11:**
  - flechas dentro del calendario y de la matriz;
  - matriz en móvil (lista por día);
  - navegación de la vista semanal en un solo renglón;
  - "Ampliaciones de la API" con `/authorization-dates/adjacent` y `/authorizations/week`;
  - el permiso `autorizacion_dates` en el servidor de la cátedra (rol existente: asignarlo desde Roles y permisos o con un script).
- **Opcional:** aplicar `condicion` a las marcas y a la navegación. Hoy, con `condicion=excepcion`, "Siguiente con pedidos" puede llevar a un día que la bandeja muestra vacío; está documentado.
