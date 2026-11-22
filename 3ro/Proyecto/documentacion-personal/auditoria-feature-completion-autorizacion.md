# Auditoría de completitud: Módulo de Autorización

- **Rama auditada:** `feature/autorizacion-pedidos` (97 commits sobre `origin/development`)
- **Fecha:** 20/11/2026
- **Alcance:** completitud funcional del módulo según el enunciado (Etapa 2 › Módulo de Autorización, sus endpoints de Etapa 1 y las Ampliaciones de la API correspondientes). La calidad del código queda fuera de esta auditoría.

## Veredicto

**El módulo está completo.** Cada requerimiento funcional, nota y endpoint sugerido del enunciado tiene implementación en el backend y en el portal. No falta ninguna funcionalidad. Hay **un desvío menor** respecto del texto del enunciado (la pantalla inicial del rol) y algunas observaciones sobre la verificación, que se detallan al final.

| Bloque | Estado |
|---|---|
| Consulta de pedidos (vista diaria y semanal, calendario, filtros, pendientes) | ✅ Completo |
| Autorización (individual, masiva, totales previos, rechazo, corrección) | ✅ Completo |
| Pedidas y autorizadas por separado, totales | ✅ Completo |
| Gestión de cupos | ✅ Completo |
| Almuerzos de residentes | ✅ Completo |
| Períodos anteriores en solo lectura | ✅ Completo |
| Exportación CSV (dos listados) | ✅ Completo |
| Notas del enunciado (409 por concurrencia, trazabilidad, etc.) | ✅ Completo |
| Permisos (solo Servicio de Alimentación) | ✅ Completo |
| Pantalla inicial del rol | ⚠️ Desvío menor |

## Cómo se verificó

1. Se compararon los requerimientos del enunciado con las rutas, vistas y servicios del portal (`portal/src/modules/autorizacion/`) y con los blueprints de la API (`admin/src/web/controlers/`).
2. Se corrieron las suites de tests:
   - **Backend:** 815 pasan y 4 fallan. Ningún fallo pertenece al módulo (ver Observaciones). Solo `test_api_autorizaciones.py` tiene 105 tests, todos en verde.
   - **Portal (Vitest):** 157 de 157 pasan.
   - **Build de producción del portal** (`npm run build`): compila sin errores.
3. Para algunos puntos de interfaz se tomó como evidencia la verificación en navegador que documentan los reportes de cada slice.

## Detalle por requerimiento

### 1. Consulta de pedidos

| Requerimiento | Implementación | Estado |
|---|---|---|
| Vistas diarias y semanales con navegación entre períodos | `SelectorVista` (Día/Semana), `PeriodoNav`; vista semanal lunes a domingo con `GET /api/authorizations/week` | ✅ |
| Consultar solo los períodos con pedidos (calendario con días marcados) | `CalendarioDias` usa `GET /api/authorization-dates` (conteo de pendientes, autorizados y rechazados por día). Los botones anterior/siguiente saltan los días vacíos con `GET /api/authorization-dates/adjacent` | ✅ |
| Filtrar por sector, tipo de comida, tipo de menú, condición y estado | `FiltrosAutorizacion` (los 5 filtros, incluidos los sectores inactivos con pedidos históricos). Aplican a la vista diaria, a la semanal y a los totales | ✅ |
| Identificar claramente los pendientes (estado confirmado) | Badge "Pendiente"; los pendientes van primero en la bandeja. Botón "Revisar pendientes" / "Ver todos" | ✅ |
| Listado paginado (Etapa 1) | `Paginator` en la vista diaria | ✅ |

### 2. Autorización

| Requerimiento | Implementación | Estado |
|---|---|---|
| Detalle por persona y comida, con tipo de menú y condición Regular/Excepción | `MatrizSolicitudes`: una fila por persona, una celda por comida con menú y condición (también "Generada" para los residentes) | ✅ |
| Autorizar o no autorizar cada solicitud individualmente | Checkbox por solicitud; el `PUT` envía la decisión de cada solicitud | ✅ |
| Autorizaciones masivas (todo, solo regulares, etc.) | `AtajosSeleccion`: Todo, Solo regulares, Ninguno. Además se puede alternar por comida (columna) o por persona (fila) | ✅ |
| Mostrar antes de confirmar los totales pedidos y los que quedarían autorizados | `TotalesComparados` con la columna "A autorizar" en vivo y desglose por menú. El diálogo de confirmación resume "X de Y" por comida | ✅ |
| Rechazar el pedido completo con motivo obligatorio | `DialogoRechazo`. La API responde 400 si falta el motivo, si está vacío o si supera el largo máximo. El motivo se le muestra al Jefe de Servicio en `GET /api/requests/{id}` | ✅ |
| Corregir una autorización ya hecha | Botón "Corregir autorización", que reutiliza el mismo `PUT`. Se habilita solo para hoy o fechas futuras y sin entregas. Cuenta los cambios y pide confirmación | ✅ |
| Confirmación antes de autorizar, rechazar y corregir | `useConfirm` y el diálogo de rechazo | ✅ |
| Evitar envíos duplicados | Los botones quedan `disabled` mientras `enviando` | ✅ |

### 3. Cantidades y totales

| Requerimiento | Implementación | Estado |
|---|---|---|
| Pedidas y autorizadas por separado; no se sobrescriben | Bandeja ("autorizadas / pedidas"), detalle y semana. Test `test_put_no_cambia_lo_pedido` | ✅ |
| Autorizadas calculadas a partir de las solicitudes nominadas; sin carga manual de totales | La interfaz solo tiene checkboxes; la API calcula los totales | ✅ |
| Totales por sector, fecha, comida y tipo de menú | `TotalesPeriodo` con "Agrupar por" Comida, Sector, Menú, Condición y Día (Día solo en la vista semanal) sobre `GET /api/summaries/authorizations`. Se agrupa por una dimensión a la vez; el cruce sector × comida × menú está en el CSV de totales | ✅ |

### 4. Gestión de cupos

| Requerimiento | Implementación | Estado |
|---|---|---|
| Listar, crear y modificar el cupo de cada sector y tipo de comida | Ruta `/cupos`: `MatrizCupos` (sector × comida) y `EditorCuposSector`, que crea o modifica según exista el cupo | ✅ |
| Reglas de la API (sector inactivo 400, duplicado 409, cantidad > 0, no retroactivo, se elimina el 400 por "menor a lo entregado") | `test_api_cupos.py` y `test_cupos_no_retroactivos.py`, incluido `test_update_below_delivered_is_allowed` | ✅ |
| Sector Residentes sin cupos | `test_create_cupo_residentes_400` y `test_update_cupo_residentes_400` | ✅ |

### 5. Almuerzos de residentes

| Requerimiento | Implementación | Estado |
|---|---|---|
| Generar en una acción el almuerzo autorizado de todos los residentes activos para una fecha o un rango | Ruta `/residentes` con `POST /api/resident-lunches`. Flujo: rango y excluidos, vista previa (simulación), confirmación y generación | ✅ |
| Excluir personas puntuales | `ListaResidentes`, parámetro `excluir` | ✅ |
| No consumen cupo; omite a quien ya tiene almuerzo en otro sector; agrega solo los faltantes; 400 por fechas pasadas o si falta el sector Residentes | `test_api_almuerzos_residentes.py` (22 tests) | ✅ |
| Aparecen como un sector más en la bandeja, los totales y los CSV | `test_aparece_en_la_bandeja`, `test_totales_separan_generadas` y `test_residentes_en_ambos_csv` | ✅ |

### 6. Períodos anteriores

| Requerimiento | Implementación | Estado |
|---|---|---|
| Consultarlos en modo solo lectura; no se puede autorizar, rechazar ni corregir | La API devuelve `acciones.motivo_bloqueo = dia_anterior` y la vista muestra "Período anterior: solo lectura." sin botones. El `PUT` y el `reject` responden 409 (`test_put_dia_anterior_409`, `test_reject_dia_anterior_409`, `test_correccion_dia_anterior_409`) | ✅ |
| Consultar autorizaciones históricas | Navegación por fechas pasadas, más el historial de cambios por pedido (`GET /api/authorizations/{id}/history`, componente `HistorialAutorizacion`) | ✅ |

### 7. Exportación CSV

| Requerimiento | Implementación | Estado |
|---|---|---|
| Detalle de personas autorizadas: apellido y nombre, DNI, sector, desayuno, almuerzo, merienda, cena, tipo de menú | `GET /api/authorized-meals?formato=csv`. Test `test_detalle_columnas_y_filas` | ✅ |
| Totales por sector con viandas por comida y tipo de menú | `GET /api/summaries/*?formato=csv`, matriz por sector (`test_matriz_por_sector`) | ✅ |
| Respeta los filtros aplicados; rango obligatorio; 400 y 403 | `DialogoExportar` toma los filtros de la bandeja y pide el rango. Tests `test_filtros_csv`, `test_csv_sin_rango_400` y `test_permisos_csv` | ✅ |

Los botones de exportación se deshabilitan, con el motivo a la vista, cuando el filtro de estado es Pendiente o Rechazado, porque esos estados no tienen comidas autorizadas para listar.

### 8. Notas del enunciado

| Nota | Estado |
|---|---|
| Lo pedido no se sobrescribe al autorizar o corregir | ✅ |
| Cada corrección queda registrada con usuario, fecha, hora y estado anterior de cada solicitud modificada | ✅ `test_put_crea_historial` y `test_correccion_historial_solo_de_las_que_cambian`; se ve en el historial |
| Corrección solo para hoy o fechas futuras y sin comidas entregadas | ✅ `test_correccion_con_entregas_409` |
| 409 si el pedido cambió mientras se revisaba (control con `modificado_en`); la interfaz informa y recarga | ✅ `test_put_version_vieja_409` y `test_put_jefe_cambio_personas_409`. En la vista, el 409 muestra un aviso y llama a `load()` en autorizar, rechazar y corregir |
| Un pedido rechazado no puede autorizarse; los ya rechazados no pueden volver a rechazarse | ✅ `test_put_rechazado_409` y `test_reject_rechazado_409` |

### 9. Permisos

| Requerimiento | Estado |
|---|---|
| Solo el rol Servicio de Alimentación accede al módulo | ✅ Las 4 rutas declaran `meta.roles: [ALIMENTACION]` y la API responde 403 a los demás roles (`test_only_alimentacion_can_authorize`, `test_solo_alimentacion_403`, `test_permisos` y otros) |

### 10. Endpoints sugeridos

| Endpoint | Estado |
|---|---|
| `GET /api/authorizations`, `GET` y `PUT /api/authorizations/{id}`, `POST /api/authorizations/{id}/reject` | ✅ |
| `GET`, `POST` y `PUT /api/quotas`, `GET /api/sectors`, `GET /api/menu-types` | ✅ |
| `GET /api/summaries/authorizations` (con filtro y agrupamiento por condición) | ✅ |
| `POST /api/resident-lunches` | ✅ |
| `GET /api/authorization-dates` | ✅ |
| Endpoints extra no pedidos: `/authorizations/week`, `/authorizations/{id}/history`, `/authorization-dates/adjacent` | ✅ Suman funcionalidad |

## Desvío y observaciones

### ⚠️ Desvío menor: la pantalla inicial no filtra los pendientes

El enunciado (Login y manejo de sesión) dice que el Servicio de Alimentación debe entrar a los **"pedidos pendientes de autorización del día"**. Hoy, después del login, entra a `/autorizaciones` sin filtro de estado. Ve **todos** los pedidos del día, con los pendientes primero y resaltados, y para quedarse solo con los pendientes tiene que pulsar "Revisar pendientes".

Desde lo funcional, el usuario igual identifica los pendientes de inmediato, pero el texto del enunciado pide el filtro aplicado. **Corrección sugerida:** que `ROLE_HOME_ROUTE` del rol lleve a `autorizacion-pendientes` con `query: { estado: 'confirmado' }`, o que el guard agregue ese query al redirigir. Es un cambio de una línea y no afecta al resto de la bandeja.

### Observaciones sobre la verificación (no son funcionalidades faltantes)

1. **Tests de backend que fallan:** son 4 y ninguno es del módulo.
   - 3 en `test_home_controller.py` (home de la aplicación privada): fallan con `no such table: settings`.
   - 1 en `test_api_resumenes.py::test_core_resumen_por_mes_y_semana`: falla porque la suite corrió contra SQLite y `date_trunc` solo existe en PostgreSQL. El agrupamiento por mes o semana no lo usa este módulo, pero conviene correr ese test contra PostgreSQL, el motor de producción, para confirmar que pasa.
2. **Recorrido en navegador de S1 (bandeja diaria):** su reporte deja varios pasos marcados "⏳ navegador" (link compartido, Atrás, URL inválida, pantalla de acceso denegado). Los slices siguientes se construyeron sobre la bandeja y la usaron en sus recorridos, así que la funcionalidad se ejercitó en la práctica. Aun así, conviene repasar esos pasos una vez antes de la defensa.
3. **Lint del portal:** hay 3 errores de ESLint, todos en `modules/carga-pedidos/components/SelectorPeriodo.vue`, fuera de este módulo.

## Conclusión

El Módulo de Autorización cumple con **todos** los requerimientos funcionales, notas, permisos y endpoints que el enunciado le asigna, incluidos los cambios de las Ampliaciones de la API: corrección, días anteriores, motivo obligatorio, control de concurrencia, fechas con pedidos, almuerzos de residentes, condición en los resúmenes y CSV. Lo único pendiente es el ajuste menor de la pantalla inicial filtrada a pendientes, más los repasos de verificación de la sección anterior.
