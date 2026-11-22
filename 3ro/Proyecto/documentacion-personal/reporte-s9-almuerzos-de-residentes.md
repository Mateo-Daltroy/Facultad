# Reporte S9: Almuerzos de residentes

- **Fecha:** 20/11/2026
- **Rama:** `feat/autorizacion-s9` (sale de `feature/autorizacion-pedidos`, `8a93a73`, con S8 mergeada). Mergeada en local con `--no-ff` (`3d1f317`). **No pusheada.**
- **Commits:**
  - `aef3f9c` test: almuerzos de residentes y condición generada
  - `1b5400a` feat(api): condición generada en solicitudes, totales y resúmenes
  - `1124092` feat(api): POST /api/resident-lunches
  - `50b8169` feat(api): el sector Residentes no tiene cupos
  - `34bff2d` docs(api): resident-lunches y condición generada
  - `6089f95` feat(portal): condición generada en la bandeja y el detalle
  - `5a8a8b7` feat(portal): generación de almuerzos de residentes
  - `e4b580d` feat(portal): Residentes sin edición en la matriz de cupos

## Alcance

Se hizo todo lo que pide el plan (`plan-s9-almuerzos-de-residentes.md`):

- **Generación de almuerzos** (`POST /api/resident-lunches`, permiso `almuerzo_residentes_new`): crea o completa un pedido **diario y autorizado** del sector Residentes por fecha. Cada solicitud tiene condición `generada`, el menú de la ficha, quién la autorizó y cuándo, y una fila de historial `tipo=generacion`. Omite a quien ya tiene almuerzo en un pedido no rechazado de otro sector (`almuerzo_en_otro_sector`) y a quien ya lo tiene generado (`ya_generado`). Con `simular: true` es la vista previa: el mismo código, sin escribir nada. La operación bloquea la fila del sector Residentes (`FOR UPDATE`) mientras corre.
- **La condición `generada` en todo el módulo de Autorización** (sección 0 del plan): `CHECK` de la base, `condiciones_validas`, `totals()`, resúmenes (`*_generadas`), parsers del portal, badge "Generada", filtro de la bandeja, columna "Generadas" en `TotalesComparados` (solo si hay alguna), fila "Generada" en el panel del período y "Solo regulares" (no desmarca las generadas).
- **El sector Residentes no tiene cupos:** la API responde 400 en `POST` y `PUT /api/quotas`, y en `/cupos` la fila Residentes tiene Editar deshabilitado con el motivo.
- **409 cuando un Jefe carga un almuerzo ya generado:** ya funcionaba (`validate_solicitudes_duplicadas`). Se agregó un test que lo documenta y no se tocó Carga.

**Criterio "Listo cuando":** ✅ "los almuerzos aparecen como sector Residentes en la bandeja y en los totales, separados de regular y excepción". Lo cubren `test_aparece_en_la_bandeja` y `test_totales_separan_generadas`, y los pasos 8 y 9 de la prueba manual.

## Cambios

### Base de datos

- `CHECK` de `condicion` (`ck_solicitud_condicion`) con `generada`. **Requiere `reset-db`** (la app usa `create_all`).
- Permiso nuevo `almuerzo_residentes_new` (rol `alimentacion`) en `core/seeds.py`. Los seeds **no** generan almuerzos de residentes.
- **La base `admin/dev.db` del usuario se reseteó y se volvió a sembrar** (con permiso del usuario), así que ya tiene el esquema y el permiso nuevos.

### Backend (`code/admin`)

- `core/residentes.py` (nuevo): `RangoGeneracion.parse` (único parser del rango: `desde ≤ hasta`, hasta 31 días, sin fechas pasadas), los omitidos como unión discriminada (`AlmuerzoEnOtroSector | YaGenerado`) y `generar_almuerzos_residentes`. Separa la planificación (`_planificar`, pura) de la aplicación (`_aplicar`, con un solo commit). Ante un 400 o un 409 hace `rollback` para liberar el bloqueo.
- `core/sector.py`: `NOMBRE_SECTOR_RESIDENTES`, `es_sector_residentes` y `get_sector_residentes`. El 400 tiene un mensaje distinto si el sector no existe y si está inactivo.
- `web/schemas/residentes.py` y `web/controlers/api_residentes.py` (nuevos). Responde 201 al generar (aunque sean 0) y 200 al simular. El blueprint se registra en `web/__init__.py`.
- `docs/openapi.yaml`: ruta y schema `ResidentLunchesResult`, `generada` en los enums de `condicion`, en `MealTotals` y en los resúmenes, y el 400 de cupos para Residentes.

**Archivos de otros integrantes (cambios aditivos):**

- `web/schemas/pedidos.py` (Carga): `totals()` inicializa `generada: 0`. Sin esto, `meal[solicitud.condicion] += 1` daba `KeyError` con `generada`.
- `core/resumen.py` y `web/schemas/resumenes.py` (Reportes): se agregaron `generada` en `CONDICIONES`, `_generada` y los campos `pedidas_generadas`, `autorizadas_generadas` y `entregadas_generadas` (después del de excepción). Así, el JSON y el CSV los traen.
- `core/cupo.py`: `validate_sector_cupo` responde 400 para el sector Residentes.

### Frontend (`code/portal/src/modules/autorizacion/`)

- `residentes/` (nuevo):
  - `residentes.js`: funciones puras.
  - `services/residentesService.js`: parseo estricto (`aResultado`).
  - Componentes `ListaResidentes.vue` y `ResultadoGeneracion.vue`, y la vista `AlmuerzosResidentesView.vue`.
- `routes.js`: `/residentes` (`residentes-almuerzos`, `order: 27`, solo Alimentación).
- `filtros.js` (`CONDICIONES`), `autorizacionService.js` (`aTotales` exige `total = regular + excepcion + generada`), `seleccion.js`, `MatrizSolicitudes.vue` (`badge--info`) y `TotalesComparados.vue`.
- `cupos/cupos.js`: `motivoNoEditable(sector)`, que reemplaza a la constante `MOTIVO_INACTIVO` de `MatrizCupos.vue`. Si el sector es Residentes y además está inactivo, se muestra el motivo de inactivo.

### Tests

- **Backend:** `poetry run pytest` da **760 passed y 4 failed**. Las 4 son las fallas previas de SQLite (`test_core_resumen_por_mes_y_semana` y las 3 de `test_home_controller`). Se agregaron 34 tests:
  - `test_api_almuerzos_residentes.py` (nuevo, 29 casos con parametrizaciones): todos los de la tabla 5.3 del plan, más `test_rango_de_31_dias_201` y `test_sin_autenticacion_401`.
  - Al final de `test_api_cupos.py`: `test_create_cupo_residentes_400` y `test_update_cupo_residentes_400`.
  - Al final de `test_api_resumenes.py`: `test_core_resumenes_separan_generadas`, `test_core_filtro_condicion_generada` y `test_summary_generadas_api`.
- **Tests existentes ajustados solo en la forma** (se agregó la clave `generada: 0`, el campo `*_generadas: 0` o la columna del CSV):
  - `test_api_pedidos.py`: `test_create_marks_regular_until_quota_then_exception` y `test_reviewed_request_shows_authorized_amounts_apart`;
  - `test_api_autorizaciones.py`: `test_authorize_by_meal_and_returns_totals`;
  - `test_api_resumenes.py`: `test_core_resumen_por_comida_y_filtros`, `test_core_resumen_entregas`, `test_summary_includes_totals` y `test_export_csv`.
- **Portal:**
  - `npm test`: 136 passed. Se agregaron `residentes.test.js` (incluye `aResultado`) y casos nuevos en `seleccion.test.js`, `filtros.test.js` y `cupos.test.js`.
  - `npm run build`: OK.
  - `npm run lint`: falla con 3 errores previos en `carga-pedidos/components/SelectorPeriodo.vue` (variables sin usar). No es de esta slice y no se tocó. Los archivos de S9 no tienen errores.

## Prueba manual

**Preparación:** igual que en S8, instancias propias en una base temporal: Flask en el 5055 y Vite en el 5199 (`reset-db` + `seeds` del 20/11, D = 20/11). Pasos de API con un script, y de navegador con Playwright (Chromium headless) como `alimentacion@test.com`. Al terminar se apagaron. Después se reseteó la base del usuario y quedaron corriendo Flask en el 5000 y Vite en el 5173.

| # | Resultado |
|---|---|
| 1 | ✅ La sidebar muestra Autorizaciones, Cupos, **Residentes** y Reportes. `/residentes` lista los 6 residentes en orden alfabético |
| 2 | ✅ "rios" muestra solo a Ríos. "3410" muestra a Acosta, Benítez, Ríos y Suárez (Perez y Fernandez tienen otro DNI). "Limpiar búsqueda" vuelve a los 6 |
| 3 | ✅ Desde = D-1, Hasta < Desde y 32 días: error por campo y el foco va al primer campo con error. **0 POST** |
| 4 | ✅ POST con `simular: true` → 200. "Se generarían 23 almuerzos". Por día: D 5, D+1 6, D+2 6 y D+3 6, todos "Nuevo". 1 omitido: Perez, "Ya tiene almuerzo en Médicos Internos (pedido 4)". Acosta no figura |
| 5 | ✅ Al excluir a Ríos, la vista previa queda "desactualizada" y Generar se deshabilita. La nueva vista previa da 19 |
| 6 | ✅ El diálogo de confirmación muestra el rango con fechas largas. → 201, con el toast "Se generaron 19 almuerzos.". Se crearon los pedidos **14, 15, 16 y 17** |
| 7 | ✅ Vista previa: 4 generados y 20 omitidos. Generar: 4, en los mismos pedidos 14 a 17 |
| 8 | ✅ En la bandeja de D aparece Residentes (Autorizado, 5/5). Con Condición = Generada, solo ese pedido. En el panel por Condición, la fila **Generada: 5 pedidas, 0 pendientes y 5 autorizadas**. Por Sector, aparece la fila Residentes |
| 9 | ✅ `agrupar=condicion`: fila `generada` con 23 pedidas y 23 autorizadas. `autorizadas_generadas = 23` y `autorizadas_regulares = 16` (no las incluye). Por sector, Residentes = 23 |
| 10 | ✅ El detalle del pedido 14 muestra 5 badges "Generada" y "Residente", la columna "Generadas" (Regular y Excepción en 0) y el historial "Generación" con Ana Alimentación. "Corregir autorización" está habilitado |
| 11 | ✅ "Solo regulares" deja las 5 marcadas. Al desmarcar a Acosta (PUT) → 200, y el historial muestra "Corrección" |
| 12 | ✅ Volver a generar D: 0 generados, Acosta como "Ya generado" y el mismo `modificado_en` |
| 13 | ✅ `jefe.medicos@test.com` cargando el almuerzo de Fernandez para D+2 → 409 ("La persona 4 ya tiene una solicitud de almuerzo en esas fechas"). La cena → 201 |
| 14 | ✅ En `/cupos`, la fila Residentes tiene Editar deshabilitado con el motivo. `POST /api/quotas` con `sector_id` 7 → 400 `business_rule` con `sector_id` |
| 15 | ✅ `fecha_desde` = D-1 → 400 con `fecha_desde`. `excluir: [9]` → 400 `business_rule` con `excluir`. `excluir: [1, 1]` → 400 `validation_error` |
| 16 | ✅ Con el sector renombrado a "Residentes X" (en la base temporal, desde SQLite), la página muestra "No existe el sector Residentes: lo tiene que crear el Administrador.". Después se volvió a renombrar |
| 17 | ✅ Dos sesiones (`alimentacion` y `alimentacion2`) pidieron la vista previa de D+4 (6 cada una). La primera genera 6. La segunda genera 0, con el aviso "El resultado difiere de la vista previa…" y los 6 como "Ya generado" |
| 18 | ✅ `comedor` y `jefe.medicos` no ven "Residentes" en el menú, y `/residentes` los lleva a `/acceso-denegado`. Con su token, el POST → 403 |
| 19 | ✅ Por inspección del código: `fieldset`/`legend`, `label`, `aria-invalid` y `aria-describedby`, `aria-label` por checkbox, contador con `aria-live`, `caption` y `th scope` en las tablas, y foco en el primer error (verificado en el paso 3) |
| 20 | ✅ La bandeja, el detalle común, Cupos y Reportes cargan sin errores de consola. Reportes ignora los campos `*_generadas`. Los tests de Carga y Reportes pasan |

## Pendientes y decisiones

- **Mensaje del 409 de Carga:** no dice el sector ni la fecha, como pide el enunciado (sección 8 del plan). Queda para quien mantiene Carga.
- **Reportes:** no muestra `*_generadas` ni ofrece "Generada" en su filtro Condición. Al agrupar por condición, la fila sale con el valor crudo `generada`.
- **`fields.excluir`** sigue el formato común de errores (`{campo: [mensaje]}`). Los ids van en el mensaje ("No son residentes activos: [9]."), no como una lista aparte.
- **`ResultadoGeneracion.sector_name`:** el resultado de dominio lleva el nombre real del sector Residentes, para que `ya_generado` informe el nombre tal como está guardado.
- **Si se renombra el sector Residentes,** la generación deja de funcionar (400). Sigue abierta la pregunta del artefacto sobre agregar un indicador en `Sector`.
- En el navegador aparece un toast "Invalid PrimeUI License" de una dependencia que ya estaba (PrimeVue, de Reportes). No es de S9.
