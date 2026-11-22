# Plan S9: Almuerzos de residentes (Módulo de Autorización)

> **BORRADOR v1 (20/11/2026).** Decisiones cerradas. Los estilos de la página y de las tablas se ajustan visualmente durante la implementación.
>
> Plan de implementación de la novena slice del "Plan de desarrollo — Módulo de Autorización" (artefacto). Además de lo que pide el artefacto, S9 tiene que **hacer convivir una tercera condición (`generada`) con el código que hoy asume solo Regular y Excepción**, y cerrar que el sector Residentes no tenga cupos (ver sección 0).
>
> Referencias:
> - Slice anterior: `documentacion-personal/plan-s8-gestion-de-cupos.md` y `reporte-s8-gestion-de-cupos.md`.
> - Datos de demo: `documentacion-personal/seeds-per-slice.md` (tablas "Sectores y cupos", "Personal" y de pedidos, y sección S9).
> - Enunciado, `Etapa_1y2_Trabajo Integrador_2026_PUBLIC.md`:
>   - Módulo de Autorización: "Generar en una sola acción el almuerzo autorizado de todos los residentes activos para una fecha o un rango de fechas, con la posibilidad de excluir personas puntuales. Estos almuerzos no consumen cupos de los sectores".
>   - Módulo de Carga: "El almuerzo de los residentes lo genera el Servicio de Alimentación para todos ellos […]. Si carga el almuerzo de un residente que ya lo tiene generado, la API lo rechaza (409)".
>   - Módulo de Reportes: "Los almuerzos de residentes generados por el Servicio de Alimentación se muestran agrupados como Residentes en los reportes por sector".
>   - Ampliaciones de la API, `POST /api/resident-lunches`:
>     - **Entrada:** fecha o rango de fechas y, opcionalmente, la lista de `person_id` a excluir.
>     - **Salida:** cantidad generada y listado de omitidos con su motivo.
>     - **Reglas:**
>       - una solicitud de almuerzo **ya autorizada** por cada residente activo y fecha;
>       - no consume cupos;
>       - omite a quien ya tiene el almuerzo de esa fecha en un pedido de un sector;
>       - registra fecha, hora y usuario;
>       - se registra en el pedido de cada fecha del sector Residentes, y si ese pedido ya existe solo se agregan los que faltan;
>       - el sector Residentes lo crea el Administrador y **no tiene Jefes ni cupos**.
>     - **Errores:** 400 si el rango es inválido o incluye fechas pasadas, 400 si no existe el sector Residentes y 403 si no es Alimentación.
>   - Ampliaciones de la API, `POST /api/requests y PUT /api/requests/{request_id}`: un residente no puede tener la misma comida en la misma fecha en otro sector "o en los almuerzos generados por el Servicio de Alimentación" (409).
>   - Ampliaciones de la API, Resúmenes: "los almuerzos de residentes generados por el Servicio de Alimentación pertenecen al sector Residentes y **no tienen condición Regular o Excepción, por lo que se informan por separado de esos dos totales**".
> - Artefacto, S9:
>   - **Backend:**
>     - `get_sector_residentes()`;
>     - `generar_almuerzos_residentes(fecha_desde, fecha_hasta, excluir, usuario_id, ahora)` crea o completa el pedido autorizado del sector Residentes por fecha, con solicitudes `condicion=generada`, y omite a quien ya tiene almuerzo en otro sector;
>     - CHECK de condición con `generada`, historial `tipo=generacion` y permiso `almuerzo_residentes_new`.
>   - **Frontend:** `AlmuerzosResidentesView` con rango de fechas, lista de residentes activos con "excluir", vista previa de cantidad, confirmación y reporte de generados y omitidos con motivo.
>   - **Tests:** sin sector Residentes (400), fechas pasadas (400), omitidos y segunda ejecución que solo agrega faltantes.
>   - **Listo cuando:** los almuerzos aparecen como sector Residentes en la bandeja y en los totales, separados de regular y excepción.
>
> **Precondición:** S8 está terminada y mergeada en `feature/autorizacion-pedidos` (el último merge de `git log` es el de `feat/autorizacion-s8`). **Si no lo está, no se empieza S9**: se avisa al usuario y se detiene. Este plan usa estos nombres del código actual:
>
> - Backend (`code/admin/src`):
>   - `core/solicitud.py`:
>     - `Solicitud` (`condicion` con CHECK `ck_solicitud_condicion` IN `('regular','excepcion')`, `autorizada`, `autorizado_por` y `autorizado_en`);
>     - `condiciones_validas = {"regular", "excepcion"}`. La usan `consulta_pedidos` (filtro de la bandeja), `AutorizacionQuerySchema` y el schema de `autorizacion_fechas`.
>   - `core/pedido.py`:
>     - `Pedido` (`creado_por`, `revisado_por`, `sector_id`, `periodicidad`, `fecha_desde`, `fecha_hasta`, `estado`, `revisado_en`, `modificado_en` y relación `solicitudes`). **Todavía cubre un rango**: "un pedido por sector y fecha" es de Carga y no se hizo.
>     - `validate_solicitudes_duplicadas` (Carga): bloquea a una persona con la misma comida y fecha en cualquier pedido **no rechazado**. Ya cubre el 409 del Jefe contra un almuerzo generado (ver sección 0).
>   - `core/autorizacion_cambio.py`: `AutorizacionCambio` (`solicitud_id`, `pedido_id`, `usuario_id`, `momento`, `autorizada_antes` y `autorizada_despues`). Su `tipo` **ya admite** `generacion` (CHECK `ck_autorizacion_cambio_tipo`).
>   - `core/autorizacion.py`: `acciones_permitidas`, `revisar_pedido` (corrección de un autorizado), `version_de`, `_bloquear` (patrón `with_for_update` + `populate_existing`) y `_cerrar_revision`.
>   - `core/sector.py`: `Sector` (`name`, `active` y `deleted`) y `get_sector_by_id`.
>   - `core/personal.py`: `Personal` (`activo`, `es_residente`, `tipo_menu_id`, `apellido`, `nombre` y `dni`), `list_personal` y `get_personal_by_id`. `core/enum_tables.py`: `TIPO_RESIDENTE = "Residente"`.
>   - `core/cupo.py`: `validate_sector_cupo` (400 si el sector no existe o está inactivo, `field="sector_id"`; lo usan `create_cupo` y `update_cupo`).
>   - `core/resumen.py` (de Reportes):
>     - `CONDICIONES = ("regular", "excepcion", "todas")`;
>     - `CAMPOS_PEDIDOS`, `CAMPOS_AUTORIZACIONES` y `CAMPOS_ENTREGAS` (dan la forma de `totales` y las columnas del CSV);
>     - `resumen_pedidos`, `resumen_autorizaciones` y `resumen_entregas_periodo`;
>     - `_regular` y `_excepcion`.
>   - `core/reloj.py`: `ahora()` y `hoy()` (`America/Argentina/Buenos_Aires`). `core/errors.py`: `BusinessRuleError(message, field=None)` (400 `business_rule`), `ConflictError` (409) y `NotFoundError` (404).
>   - `web/schemas/pedidos.py` (de Carga): `totals(solicitudes)` arma `{comida: {"total", "regular", "excepcion", "por_menu"}}` y hace `meal[solicitud.condicion] += 1` (**KeyError con `generada`**). La usan `GET /api/requests*` y `GET /api/authorizations*`.
>   - `web/schemas/resumenes.py`: `ResumenQuerySchema` (`condicion` con `OneOf(CONDICIONES)`).
>   - `web/controlers/api_autorizacion_fechas.py`: patrón de blueprint (`Blueprint(..., url_prefix=...)`, `@permission_required`), registrado con `create_api_blueprint(...)` en `web/__init__.py`. `web/controlers/api_autorizaciones.py` usa `g.current_user.id` y `reloj.ahora()`.
>   - `web/schemas/pagination.py`: `paginated` → `{ items, page, per_page, total, next }`.
>   - `core/seeds.py`:
>     - `PERMISSIONS` y `ROLES`;
>     - el sector "Residentes" (id 7, activo, sin cupos ni pedidos);
>     - 6 residentes activos (ids 1 y 4 a 8).
>   - Tests:
>     - `tests/unit/test_api_pedidos.py` (fixtures `client` y `data`, con `quirofano` y su cupo de almuerzo 2, `pediatria`, `gomez`, `perez`, `fernandez` (residente), `jefe`, `alimentacion` y `comedor`; helpers `login_as`, `nuevo`, `crear` y `count`);
>     - `tests/unit/test_api_autorizaciones.py` (`fijar_hoy`, `fijar_ahora`, `detalle`, `put_todas`, `cambios_de`, `DIA`, `EN_DIA` y `sin_zona`);
>     - `tests/unit/test_api_resumenes.py` y `tests/unit/test_api_cupos.py`.
>   - `admin/docs/openapi.yaml`.
> - Portal (`code/portal/src`):
>   - `modules/autorizacion/routes.js`: Autorizaciones `order: 20`, Cupos `order: 25` (S8). Reportes usa `order: 30`.
>   - `modules/autorizacion/filtros.js`: `CONDICIONES` (`regular`, `excepcion`), `COMIDAS`, `TIPOS_CAMBIO` (ya tiene `generacion`), `POR_PAGINA`, `parseFiltros`, `formatearDiaCorto` y `parseFechaISO`.
>   - `modules/autorizacion/services/autorizacionService.js`:
>     - `aTotales` (exige `total`, `regular` y `excepcion`), `aSolicitud` (exige la condición en `CONDICIONES`), `aDesgloseMenu` (`fila[celda.condicion] += 1`) y `aGrupoResumen` (`agrupar=condicion`);
>     - helpers internos `respuestaInvalida`, `esEntero`, `esObjeto` y `esTexto` (no exportados).
>   - `modules/autorizacion/seleccion.js`: `soloRegulares`, `seleccionInicial` y `totalesAAutorizar` (que inicializa `{ total, regular, excepcion, porMenu }`).
>   - `modules/autorizacion/components/MatrizSolicitudes.vue` (badge de condición) y `TotalesComparados.vue` (columnas Regular y Excepción).
>   - `modules/autorizacion/totales.js` (orden y etiqueta por `CONDICIONES`) y `components/TotalesPeriodo.vue`.
>   - `modules/autorizacion/cupos/` (S8): `cupos.js` (`matrizDeCupos`, `normalizarTexto`) y `components/MatrizCupos.vue` (Editar deshabilitado con motivo en los inactivos).
>   - `composables/useAsyncData.js`, `composables/useConfirm.js`, `stores/notifications.js` (toasts), `components/LoadingState.vue`, `ErrorState.vue` y `EmptyState.vue`, y `api/errors.js` (`ApiError` con `type`, `status` y `fields`; `ERROR_TYPES`).
>   - Vitest (`npm test`).
>
> Si algo de esto no coincide con el código, **manda el código**: se adapta el plan sin renombrar lo existente. En particular, los nombres internos de S8 (`cupos/`) se toman de lo que quedó mergeado.

## 0. Qué ya existe y qué hace S9

| Parte | Estado | Qué hace S9 |
|---|---|---|
| Historial `tipo='generacion'` | **Ya está** en el CHECK de `autorizacion_cambios`, y el panel de historial del portal ya muestra "Generación" | Lo usa |
| Sector "Residentes" | **Ya está** en los seeds (id 7) | Lo ubica por nombre (`get_sector_residentes`) |
| 409 cuando un Jefe carga el almuerzo de un residente ya generado | **Ya funciona**: `validate_solicitudes_duplicadas` (Carga) rechaza cualquier solicitud de la misma persona, comida y fecha en un pedido no rechazado, y el pedido de Residentes es un pedido más | Un test que lo documenta. **No** se toca Carga. El mensaje del 409 no dice sector ni fecha (lo pide el enunciado): va a Pendientes de Carga |
| Generación de almuerzos | **Falta** | Dominio, endpoint, permiso y página del portal |
| Condición `generada` | **Falta**, y agregarla **rompe** código existente: el CHECK, `totals()` (KeyError), los parsers estrictos del portal (la bandeja y el detalle del pedido de Residentes tirarían error) y `soloRegulares` (desautorizaría todo el pedido) | Se agrega en la base, el dominio, los resúmenes y todo el módulo de Autorización del portal |
| "El sector Residentes no tiene cupos" | **No se cumple**: hoy se le pueden crear cupos desde la API y desde la matriz de S8 | 400 en la API y Editar deshabilitado en la matriz |

**Por qué `generada` y no "regular":** lo exige la regla de Resúmenes citada arriba. Además, "Regular" significa "dentro del cupo" (sección 7 del enunciado) y Residentes no tiene cupo: con la regla vigente ("sin cupo, todas son excepción") serían excepción, no regulares. Guardarlas como `regular` inflaría los totales regulares de Reportes.

## 1. Objetivo

Al terminar S9:

- El usuario de Alimentación ve **"Residentes"** en la sidebar (entre Cupos y Reportes) y entra a **`/residentes`**. Los otros roles no la ven y la ruta los manda a "Acceso denegado".
- En la página elige un **rango de fechas** (por defecto, hoy a hoy) y ve la **lista de residentes activos**, cada uno con un checkbox **"Excluir"**.
- **"Ver vista previa"** muestra, sin escribir nada, cuántos almuerzos se generarían por fecha y la **lista de omitidos con su motivo**:
  - "Ya tiene almuerzo en <sector> (pedido N)";
  - "Ya generado (pedido N)".
- **"Generar N almuerzos"** pide confirmación, genera y muestra el **reporte** (generados por fecha con link a cada pedido y omitidos con su motivo).
- Los almuerzos quedan en un **pedido diario del sector Residentes**, en estado **Autorizado**, con condición **Generada**, y se ven:
  - en la **bandeja**, como un pedido más del sector Residentes, con la opción "Generada" en el filtro Condición;
  - en el **detalle**, con el badge "Generada", columna "Generadas" en los totales, historial "Generación" y la corrección habilitada como cualquier autorizado ("Solo regulares" **no** desautoriza las generadas);
  - en el **panel "Totales del período"**, con una fila "Generada" al agrupar por Condición y una fila "Residentes" al agrupar por Sector;
  - en `GET /api/summaries/*`, separados de regulares y excepciones (`pedidas_generadas`, `autorizadas_generadas` y `entregadas_generadas`).
- En la matriz de cupos (S8), la fila Residentes tiene **Editar deshabilitado** con el motivo, y la API responde 400 si se intenta crear o modificar un cupo de Residentes.

**Criterio "Listo cuando" del artefacto:** "los almuerzos aparecen como sector Residentes en la bandeja y en los totales, separados de regular y excepción". Se verifica con tests de backend (sección 5.3) y en el navegador (sección 7.2, pasos 7 a 9).

## 2. Decisiones

| Tema | Decisión | Motivo |
|---|---|---|
| Condición de los generados | `condicion = 'generada'` (tercer valor del CHECK y de `condiciones_validas`) | Regla de Resúmenes del enunciado. El artefacto lo decide igual ("condición nula" descartada: obligaría a interpretar `NULL` en cada consulta) |
| Alcance de `generada` | **Completo** en Autorización: base, `totals()`, `resumen.py`, parsers, badge, filtro de la bandeja, columna en `TotalesComparados`, fila en el panel y atajo "Solo regulares". **No** se tocan las vistas de Reportes ni de Carga | Decisión del usuario. Los cambios en archivos de otros integrantes (`schemas/pedidos.py`, `core/resumen.py` y `schemas/resumenes.py`) son **aditivos**: una clave o un valor más, sin cambiar lo que ya devolvían |
| Sector Residentes | Se ubica **por nombre**: `NOMBRE_SECTOR_RESIDENTES = "Residentes"`, comparado sin espacios en los extremos y sin mayúsculas (`casefold`), solo entre sectores no eliminados. Va en `core/sector.py` (no en el módulo nuevo) para que `core/cupo.py` lo use sin import circular | Artefacto ("ubicarlo sin cambiar el ABM de la aplicación privada"). El enunciado dice que lo crea el Administrador como cualquier sector |
| Sin sector o sector inactivo | 400 `business_rule` en los dos casos, con mensajes distintos: "No existe el sector Residentes: lo tiene que crear el Administrador." / "El sector Residentes está inactivo: lo tiene que activar el Administrador." | El enunciado pide 400 si no existe. Uno inactivo tampoco puede recibir pedidos (`validate_sector_pedido`) |
| Rango | `fecha_desde` obligatoria y `fecha_hasta` opcional (si falta, igual a `fecha_desde`). Exige `desde ≤ hasta`, **hasta 31 días** y `desde ≥ hoy` (`ahora.date()`, zona de `core/reloj`). Hoy está permitido. **Sin** horario de corte | Decisión del usuario (31, como una carga mensual: ≤ ~930 solicitudes con 30 residentes). El enunciado solo prohíbe fechas pasadas, y el corte es una regla de la carga de los Jefes |
| Excluir | Lista de `person_id` sin repetidos. Si alguno **no es un residente activo** (no existe, está eliminado, es Personal Fijo o está inactivo) → 400 `business_rule` con `fields.excluir` y los ids, ordenados. No se escribe nada | Decisión del usuario: no se "excluye" en silencio a alguien que nunca se iba a generar |
| Quiénes se generan | Personas **activas**, **no eliminadas** y de tipo **Residente**, menos las excluidas | Enunciado |
| "Ya tiene almuerzo en otro sector" | Una solicitud de **almuerzo** de esa persona y fecha en un pedido **no rechazado** de otro sector, **autorizada o no** | Es el mismo criterio de `validate_solicitudes_duplicadas` (Carga). Si fueran distintos, un residente podría terminar con dos almuerzos. La cena u otras comidas no bloquean |
| Omitidos | Union discriminada por `motivo`: `almuerzo_en_otro_sector` (con `sector_name` y `request_id` del otro pedido) y `ya_generado` (con `request_id` del pedido de Residentes). Los excluidos **no** aparecen | Decisión del usuario: una segunda corrida explica por qué generó 0. Los excluidos los eligió el usuario: no son omisiones |
| Pedido de Residentes | Uno **diario** por fecha (`periodicidad='diaria'`, `fecha_desde == fecha_hasta`), en estado `autorizado`, con `creado_por`, `revisado_por` y `revisado_en` del usuario que genera y `modificado_en = ahora`. Si ya existe, se le agregan las solicitudes que faltan y se actualizan `modificado_en`, `revisado_por` y `revisado_en` (la generación cuenta como la última revisión). Si no falta nadie, el pedido **no** se toca | Enunciado ("si ese pedido ya existe, solo se agregan los residentes que falten"). Mover `modificado_en` hace que quien lo tenga abierto para corregirlo reciba el 409 de siempre |
| Pedido de Residentes inesperado | Si para alguna fecha del rango hay un pedido **no rechazado** del sector Residentes que **no es diario** o **no está autorizado**, o hay más de uno → 409 `conflict` ("El pedido de Residentes del <fecha> (pedido N) no es un pedido generado: revisarlo antes de generar."), sin escribir nada | No debería pasar (Residentes no tiene Jefes), pero en la app privada o en datos viejos es posible. No se pisa un dato en silencio |
| Solicitudes | Cada una: `tipo_comida='almuerzo'`, `condicion='generada'`, el `tipo_menu_id` de la ficha, `autorizada=True`, `autorizado_por=usuario` y `autorizado_en=ahora`, más una fila de `AutorizacionCambio(tipo='generacion', autorizada_antes=None, autorizada_despues=True)` | Enunciado (registrar fecha, hora y usuario) y artefacto (historial `tipo=generacion`). El menú se copia igual que en Carga |
| Cupos | No se leen ni se modifican | "No consumen cupos" |
| Concurrencia | La operación bloquea la **fila del sector Residentes** (`select(Sector).where(...).with_for_update()`) antes de leer pedidos y solicitudes. Una segunda generación simultánea espera y después ve lo generado (`ya_generado`). En SQLite el `FOR UPDATE` se ignora, igual que en `_bloquear` | Sin esto, dos generaciones simultáneas podrían crear dos pedidos de Residentes para la misma fecha (no hay UNIQUE). Bloquear el sector es un mutex natural y no requiere cambio de esquema |
| Simulación | `simular: true` en el body. La función de dominio **planifica** (lee y calcula) y solo si no simula **aplica** (escribe y hace commit). Con `simular` no se agrega nada a la sesión. Mismas validaciones y mismos errores | Decisión del usuario: la vista previa es exacta (incluye los omitidos) y no puede desincronizarse de la ejecución, porque es el mismo código |
| Códigos | **201** al generar (aunque se generen 0) y **200** al simular | Una ejecución real es una creación; una simulación es una consulta |
| Todo o nada | Un solo `commit` al final. Cualquier error (400 o 409) deja la base igual | Igual que `revisar_pedido` |
| Salida | `{ simulacion, generados, por_fecha: [{ fecha, generados, request_id }], omitidos: [{ person_id, apellido, nombre, dni, fecha, motivo, sector_name, request_id }] }`. `por_fecha` trae **todas** las fechas del rango en orden. `request_id` es `null` solo en una simulación para una fecha sin pedido de Residentes. `omitidos` va ordenado por fecha, apellido, nombre e id | La salida del enunciado más lo que la página necesita (links y desglose por fecha) |
| Permiso | `almuerzo_residentes_new`, solo para `alimentacion` | Artefacto y enunciado (403 si no es Alimentación) |
| Atajo "Solo regulares" | Deja en `true` las regulares **y las generadas**, y en `false` las excepciones. El nombre del botón no cambia | Decisión del usuario. El pedido de Residentes solo admite corrección: desmarcar las generadas desautorizaría el pedido entero con un clic |
| Totales en el portal | `aTotales` exige `generada` y verifica `total == regular + excepcion + generada`. `TotalesComparados` muestra la columna "Generadas" **solo si** algún total la tiene > 0. El panel del período muestra la fila "Generada" al agrupar por Condición (sale de `CONDICIONES`) | No se agregan columnas vacías a los pedidos de los demás sectores |
| Cupos de Residentes | `validate_sector_cupo`: 400 `field="sector_id"` ("El sector Residentes no tiene cupos: sus almuerzos se generan desde Alimentación.") en POST y PUT, después de los chequeos de existencia y de inactivo. En la matriz de S8, la fila Residentes se muestra con Editar deshabilitado y el motivo, como un sector inactivo | Decisión del usuario. El enunciado: Residentes "no tiene Jefes de Servicio ni cupos asociados" |
| Residentes en el portal | `esSectorResidentes(nombre)` en `residentes/residentes.js`, con la misma comparación que el backend (`trim` + minúsculas `'es'`) | La API de sectores no expone un indicador. Es la contraparte de `NOMBRE_SECTOR_RESIDENTES` |
| Página | Ruta propia `/residentes` (`name: 'residentes-almuerzos'`), `meta: { roles: [ROLES.ALIMENTACION], title: 'Almuerzos de residentes', menu: { label: 'Residentes', order: 27 } }`, en la carpeta nueva `modules/autorizacion/residentes/` | Decisión del usuario, igual que S8 con cupos |
| Estado de la página | El formulario (rango y excluidos) **no** va en la URL | Es una acción, no una consulta para compartir |
| Vista previa vigente | Se guarda la consulta con la que se pidió (`desde`, `hasta` y el conjunto de excluidos). Si el formulario cambia, la vista previa se marca "desactualizada" y "Generar" se deshabilita hasta pedirla de nuevo | Que lo confirmado sea lo que se vio |
| Resultado distinto de la vista previa | Se muestra siempre el resultado real. Si `generados` difiere de la vista previa, aviso: "El resultado difiere de la vista previa: alguien cargó o generó almuerzos mientras tanto." | Entre la vista previa y la generación pueden cambiar los datos |
| Lista de residentes | `GET /api/personnel-types` (id del tipo "Residente") + `GET /api/personnel?tipo_personal_id=<id>&estado=activo`, todas las páginas. Búsqueda local por apellido, nombre o DNI (sin acentos ni mayúsculas, con `normalizarTexto` de `../cupos/cupos`) | Endpoints que ya existen y ya están permitidos a `alimentacion` (`personal_index`, `user_auth`) |
| Parseo de las respuestas | Estricto, con un `respuestaInvalida` propio del submódulo (los helpers de `autorizacionService.js` no se exportan ni se editan para eso) | Igual que S8 |
| Tests existentes | Los que comparan **la forma exacta** de `totals()` o de los resúmenes (diccionarios completos, cabecera del CSV) se actualizan **solo** agregando la clave nueva con 0 o la columna nueva. No se cambia ningún caso ni ninguna lógica. Se listan en el reporte | El cambio es aditivo y esos tests fijan la forma |
| Esquema | CHECK de `condicion` con `generada`. **Requiere `reset-db`** (la app usa `create_all`, sin migraciones) | Igual que S3 con `autorizacion_cambios` |
| Rama | `feat/autorizacion-s9` desde `feature/autorizacion-pedidos`, merge local `--no-ff` al terminar | Igual que S1 a S8 |

## 3. Fuera de alcance

- Cambios en Carga de Pedidos (`core/pedido.py`, `api_pedidos.py` y `modules/carga-pedidos/`), salvo la clave aditiva de `totals()` en `web/schemas/pedidos.py`. En particular, el texto del 409 por residente duplicado.
- Vistas de Reportes (`modules/reportes/`): no muestran los campos `*_generadas` ni ofrecen "Generada" en su filtro.
- UNIQUE `(sector_id, fecha)` en `pedidos` y el paso a "un pedido por sector y fecha".
- Exclusiones permanentes o recordadas: "Excluir" vale solo para esa corrida.
- Deshacer una generación. Para dejar sin efecto un almuerzo generado se **corrige** el pedido de Residentes desde el detalle, como cualquier autorizado.
- Horario de corte para la generación.
- CSV (S10) y vista responsive específica (S11; alcanza con `.table-wrap`).
- Tests de componentes Vue: solo funciones puras y parsers.

## 4. Git

```sh
cd code
git switch feature/autorizacion-pedidos
git log --oneline -3          # confirmar el merge de feat/autorizacion-s8; si no está, DETENERSE
git status                    # árbol limpio
git switch -c feat/autorizacion-s9
```

Commits sugeridos, en este orden:

1. `test: almuerzos de residentes y condición generada` (los tests nuevos de la sección 5.3, en rojo)
2. `feat(api): condición generada en solicitudes, totales y resúmenes`
3. `feat(api): POST /api/resident-lunches`
4. `feat(api): el sector Residentes no tiene cupos`
5. `docs(api): resident-lunches y condición generada`
6. `feat(portal): condición generada en la bandeja y el detalle`
7. `feat(portal): generación de almuerzos de residentes`
8. `feat(portal): Residentes sin edición en la matriz de cupos`

Si el primer commit en rojo complica la historia, se puede unir con el 2 o el 3. Al terminar: `git switch feature/autorizacion-pedidos && git merge --no-ff feat/autorizacion-s9`. **No pushear** `feat/autorizacion-s9`. `feature/autorizacion-pedidos` se pushea solo si lo pide el usuario.

## 5. Backend (`code/admin`)

### 5.1 Código

#### `core/sector.py`

```python
NOMBRE_SECTOR_RESIDENTES = "Residentes"


def es_sector_residentes(sector: Sector) -> bool:
    """el sector de los almuerzos generados: por nombre (sin espacios en los extremos ni
    mayúsculas) y no eliminado"""


def get_sector_residentes() -> Sector:
    """el sector Residentes activo. BusinessRuleError (400) si no existe o está inactivo,
    con los mensajes de la sección 2. si hubiera más de uno con ese nombre (no debería:
    validate_sector_name lo impide), RuntimeError"""
```

#### `core/solicitud.py`

- `condiciones_validas = {"regular", "excepcion", "generada"}`.
- El CHECK `ck_solicitud_condicion` pasa a `condicion IN ('regular', 'excepcion', 'generada')`.
- Docstring del módulo: la condición `generada` es la de los almuerzos de residentes, que no consumen cupo.
- `consulta_pedidos` no cambia: el filtro de la bandeja acepta `generada` porque usa `condiciones_validas`.

#### `core/residentes.py` (nuevo)

```python
MAX_DIAS_GENERACION = 31


@dataclass(frozen=True)
class RangoGeneracion:
    desde: date
    hasta: date

    @staticmethod
    def parse(desde: date, hasta: date | None, hoy: date) -> "RangoGeneracion":
        """único parser del rango: hasta=None -> desde; 400 (BusinessRuleError) si
        hasta < desde (field fecha_hasta), si supera MAX_DIAS_GENERACION (field fecha_hasta)
        o si desde < hoy (field fecha_desde, "No se pueden generar almuerzos para fechas pasadas.")"""

    def dias(self) -> tuple[date, ...]: ...


@dataclass(frozen=True)
class PersonaOmitida:
    person_id: int
    apellido: str
    nombre: str
    dni: str


@dataclass(frozen=True)
class AlmuerzoEnOtroSector:
    persona: PersonaOmitida
    fecha: date
    sector_name: str
    request_id: int


@dataclass(frozen=True)
class YaGenerado:
    persona: PersonaOmitida
    fecha: date
    request_id: int          # el pedido de Residentes de esa fecha


Omitido = AlmuerzoEnOtroSector | YaGenerado


@dataclass(frozen=True)
class GeneradosDelDia:
    fecha: date
    generados: int
    request_id: int | None   # None solo al simular una fecha sin pedido de Residentes


@dataclass(frozen=True)
class ResultadoGeneracion:
    simulacion: bool
    por_fecha: tuple[GeneradosDelDia, ...]   # todas las fechas del rango, en orden
    omitidos: tuple[Omitido, ...]            # por fecha, apellido, nombre e id

    @property
    def generados(self) -> int: ...


def generar_almuerzos_residentes(fecha_desde: date, fecha_hasta: date | None,
                                 excluir: frozenset[int], usuario_id: int,
                                 ahora: datetime, simular: bool) -> ResultadoGeneracion:
    """genera (o simula) los almuerzos autorizados de los residentes activos para el rango.

    orden de los chequeos: rango (400) -> sector Residentes (400) -> bloqueo de la fila del
    sector -> excluir (400) -> pedidos de Residentes inesperados (409).
    todo o nada: un solo commit; con simular=True no se escribe nada."""
```

Implementación sugerida: separar **planificación** (pura, testeable sin base) de **aplicación**.

1. `rango = RangoGeneracion.parse(fecha_desde, fecha_hasta, ahora.date())`.
2. `sector = get_sector_residentes()`. Después, bloquear su fila: `select(Sector).where(Sector.id == sector.id).with_for_update().execution_options(populate_existing=True)`.
3. `residentes = _residentes_activos()`: activos, no eliminados y de tipo Residente, ordenados por apellido, nombre e id. `_validar_excluir(excluir, {r.id for r in residentes})`: los ids de `excluir` que no están → 400 `field="excluir"` con la lista ordenada.
4. **Una consulta** para los almuerzos en otros sectores: solicitudes `almuerzo` de esos residentes, con fecha en el rango, en pedidos con `estado != 'rechazado'` y `sector_id != sector.id` → `{(personal_id, fecha): (sector_name, pedido_id)}`. Si hubiera dos para la misma clave, se toma el de menor `pedido_id`.
5. **Una consulta** para los pedidos de Residentes no rechazados que se superponen con el rango. Si alguno no es diario, no está autorizado o hay dos para la misma fecha → 409. Resultado: `{fecha: Pedido}` y el conjunto `{(personal_id, fecha)}` de sus almuerzos.
6. `_planificar(rango, residentes - excluidos, ocupados, ya_generados, pedidos_por_fecha)` (pura) → para cada fecha, la lista de personas a generar y los omitidos (primero `almuerzo_en_otro_sector`, después `ya_generado`).
7. Si `simular`: devolver el resultado (`request_id` = id del pedido existente o `None`).
8. Si no: `_aplicar(...)`. Para cada fecha con personas a generar:
   - crear el `Pedido(usuario_id, sector.id, "diaria", fecha, fecha)` si no existe, con `estado = "autorizado"`, o tomar el existente;
   - `revisado_por = usuario_id`, `revisado_en = ahora` y `modificado_en = ahora`;
   - agregar las `Solicitud` (`condicion="generada"`, menú de la ficha) con `autorizada`, `autorizado_por` y `autorizado_en`;
   - `db.session.flush()` y una fila de `AutorizacionCambio` por solicitud.

   Un solo `db.session.commit()` al final. El resultado lleva los ids reales.

Notas:
- Si un `BusinessRuleError` o un `ConflictError` cortan después del bloqueo, se hace `rollback` para liberarlo. Usar el mismo criterio que `_commit` / el manejo de errores de `core/autorizacion.py`, sin capturar excepciones genéricas.
- `ahora` lo pasa el controlador (`reloj.ahora()`), así los tests lo fijan.

#### `web/schemas/residentes.py` (nuevo)

- `GenerarAlmuerzosInputSchema`:
  - `unknown = RAISE`;
  - `fecha_desde` (`Date`, `required`);
  - `fecha_hasta` (`Date`, `load_default=None`);
  - `excluir` (`List(Integer(strict=True, validate=Range(min=1)))`, `load_default=[]`, con `@validates` que rechaza repetidos: "Hay personas repetidas.");
  - `simular` (`Boolean(truthy={True}, falsy={False})`, `load_default=False`).
  - `post_load` → `PedidoGeneracion` (dataclass frozen: `fecha_desde`, `fecha_hasta`, `excluir: frozenset[int]` y `simular`).
- `dump_resultado(resultado: ResultadoGeneracion) -> dict` con la forma de la sección 2. `motivo` sale del tipo con `match` (`AlmuerzoEnOtroSector` → `"almuerzo_en_otro_sector"`, `YaGenerado` → `"ya_generado"`, y `TypeError` en otro caso). Para `ya_generado`, `sector_name` es el nombre del sector Residentes.

#### `web/controlers/api_residentes.py` (nuevo)

```python
api_residentes_bp = Blueprint("residentes", __name__, url_prefix="/resident-lunches")


@api_residentes_bp.post("")
@permission_required("almuerzo_residentes_new")
def generar():
    pedido = generar_almuerzos_input_schema.load(request.get_json())   # o el helper de JSON que usen los otros POST
    resultado = generar_almuerzos_residentes(..., g.current_user.id, reloj.ahora(), pedido.simular)
    return jsonify(dump_resultado(resultado)), 200 if resultado.simulacion else 201
```

Se registra en `create_api_blueprint(...)` de `web/__init__.py`, igual que `api_autorizacion_fechas_bp`. Hay que confirmar que ese helper registra los handlers de errores de dominio. Para leer el body JSON se usa el mismo mecanismo que `api_autorizaciones.authorize` (415 si no es JSON).

#### `web/schemas/pedidos.py` (de Carga, cambio aditivo)

- `totals()` inicializa cada comida con `{"total": 0, "regular": 0, "excepcion": 0, "generada": 0, "por_menu": {}}`. Nada más cambia.

#### `core/resumen.py` y `web/schemas/resumenes.py` (de Reportes, cambio aditivo)

- `CONDICIONES = ("regular", "excepcion", "generada", "todas")`. El filtro `condicion=generada` y el agrupamiento por condición (fila `generada`) salen solos.
- `_generada = Solicitud.condicion == "generada"`.
- `resumen_pedidos` suma `pedidas_generadas`, `resumen_autorizaciones` suma `autorizadas_generadas` y `resumen_entregas_periodo` suma `entregadas_generadas`.
- `CAMPOS_PEDIDOS`, `CAMPOS_AUTORIZACIONES` y `CAMPOS_ENTREGAS` incluyen el campo nuevo **después** del de excepción. Así, `totales` y el CSV lo traen.

#### `core/cupo.py`

- `validate_sector_cupo`: después de los chequeos de existencia y de inactivo, `if es_sector_residentes(sector): raise BusinessRuleError("El sector Residentes no tiene cupos: sus almuerzos se generan desde Alimentación.", field="sector_id")`.

#### `core/seeds.py`

- `PERMISSIONS += ["almuerzo_residentes_new"]` y `ROLES["alimentacion"] += ["almuerzo_residentes_new"]`. Los pedidos de demo no cambian: los seeds **no** generan almuerzos de residentes, para que la prueba manual parta de cero.

### 5.2 `admin/docs/openapi.yaml`

- Ruta nueva `POST /resident-lunches`:
  - body y respuestas 200 (simulación) y 201;
  - errores 400 (`validation_error`, y `business_rule` con `fecha_desde`, `fecha_hasta` y `excluir`, o sin campo para el sector), 401, 403 y 409;
  - la descripción resume las reglas de la sección 2 (incluido "no consume cupos" y el criterio de omisión).
- `generada` en los enums de `condicion`: solicitud, filtros de `/authorizations`, `/authorizations/week`, `/authorization-dates` (si lo documenta) y `/summaries/*`.
- La clave `generada` en el objeto de totales por comida (`totales` y `autorizados`).
- `pedidas_generadas`, `autorizadas_generadas` y `entregadas_generadas` en los resúmenes, con la frase del enunciado: "los almuerzos de residentes generados no tienen condición Regular o Excepción y se informan por separado".
- `POST` y `PUT /quotas`: el 400 para el sector Residentes.

### 5.3 Tests

Archivo nuevo **`tests/unit/test_api_almuerzos_residentes.py`**:
- Importa `client`, `data`, `login_as`, `nuevo`, `crear` y `count` de `test_api_pedidos` y `fijar_ahora`, `detalle` y `put_todas` de `test_api_autorizaciones` (igual que hace `test_api_autorizaciones`).
- Fixture `residentes`:
  - crea el sector `Residentes`;
  - suma a `data.fernandez` dos residentes activos (`Acosta`, `Rios`) y uno inactivo (`Baja`);
  - fija `ahora` el 20/10/2026 a las 09:00 (`EN_DIA`) y hace login como `alimentacion`.
- Helper `generar(client, **body)`.

| Test | Preparación | Esperado |
|---|---|---|
| `test_sin_sector_residentes_400` | Sin crear el sector | 400 `business_rule`, mensaje "No existe el sector Residentes…"; sin pedidos nuevos |
| `test_sector_residentes_inactivo_400` | Sector creado inactivo | 400, mensaje de inactivo |
| `test_fecha_pasada_400` | `fecha_desde` = 19/10 | 400 con `fields.fecha_desde`; sin escrituras |
| `test_rango_invalido_400` | Parametrizado: `hasta < desde`; 32 días | 400 con `fields.fecha_hasta`. **31 días → 201** (caso límite) |
| `test_body_invalido_400` | Parametrizado: sin `fecha_desde`, `excluir` no lista, ids repetidos, `simular: "true"`, campo desconocido | 400 `validation_error` |
| `test_excluir_no_residente_activo_400` | `excluir` con `gomez` (fijo), `Baja` (inactivo) y 9999 | 400 `business_rule` con `fields.excluir` y los tres ids; sin escrituras |
| `test_permisos` | Parametrizado: `jefe`, `comedor` | 403; sin escrituras |
| `test_genera_y_omite_almuerzo_en_otro_sector` | El Jefe de Quirófano carga el almuerzo del 20/10 con Fernandez (`crear`). Rango 20/10–21/10 | 201; `generados == 5`. `omitidos == [Fernandez, 20/10, almuerzo_en_otro_sector, "Quirofano", <id>]`. Dos pedidos de Residentes diarios, `autorizado`, con `revisado_por`. Cada solicitud: `generada`, `autorizada`, menú de la ficha (Fernandez vegetariano). Historial: 5 filas `generacion` (`None → True`). `Baja` no aparece |
| `test_rechazado_no_bloquea` | Igual, con el pedido de Quirófano rechazado | Fernandez se genera el 20/10 |
| `test_no_autorizada_en_otro_sector_omite` | Pedido de Quirófano autorizado con el almuerzo de Fernandez en `False` | Fernandez omitido (`almuerzo_en_otro_sector`) |
| `test_cena_en_otro_sector_no_bloquea` | Pedido de Quirófano con la **cena** de Fernandez | Fernandez se genera |
| `test_excluir` | `excluir: [Rios]` | Rios no está en generados ni en omitidos |
| `test_segunda_corrida_agrega_faltantes` | 1.ª corrida con `excluir: [Rios]` a las 09:00; 2.ª sin excluir a las 10:00 | 2.ª: `generados == 1 por día`, el mismo `request_id` por fecha, `modificado_en`, `revisado_en` = 10:00, `ya_generado` para los demás. 3.ª corrida: 0 generados, todos `ya_generado`, `modificado_en` sin cambios |
| `test_simular_no_escribe` | `simular: true` | 200, `simulacion: true`; `count(Pedido)`, `count(Solicitud)` y `count(AutorizacionCambio)` sin cambios; `request_id: null` en fechas nuevas. Después, la corrida real devuelve los mismos `generados` y `omitidos` |
| `test_pedido_residentes_inesperado_409` | Un `Pedido` confirmado del sector Residentes para el 20/10 (creado directo con el modelo) | 409; sin escrituras |
| `test_no_consume_cupo` | Cupo de almuerzo 1 en Quirófano, sin cupo en Residentes | Todas las generadas son `generada` (ni regular ni excepción). Un pedido nuevo de Quirófano para otra fecha con 2 almuerzos sigue dando 1 regular y 1 excepción |
| `test_corregir_pedido_generado` | Después de generar, `put_todas` desautorizando a Acosta | 200, corrección con historial `correccion`. Una nueva corrida no vuelve a agregar a Acosta (`ya_generado`) |

Criterio "Listo cuando", en el mismo archivo:

| Test | Esperado |
|---|---|
| `test_aparece_en_la_bandeja` | `GET /api/authorizations?fecha_desde=…&fecha_hasta=…` (20/10) incluye el pedido de `sector_name == "Residentes"` con `totales.almuerzo == {total: n, regular: 0, excepcion: 0, generada: n, por_menu: …}` y `autorizados` igual. `condicion=generada` devuelve solo ese pedido |
| `test_totales_separan_generadas` | `GET /api/summaries/authorizations?…&agrupar=condicion` tiene la fila `generada`, separada de `regular` y `excepcion`, y su suma = total. `agrupar=sector` tiene la fila Residentes. `totales.autorizadas_generadas == n` y `autorizadas_regulares` no las cuenta. `condicion=generada` filtra solo esas |
| `test_jefe_no_puede_cargar_almuerzo_generado` | Después de generar el 21/10, el Jefe de Quirófano carga el almuerzo del 21/10 con Fernandez → 409 (sin modificar Carga). La cena del 21/10 con Fernandez → 201 |

Agregados al final de archivos existentes (sin modificar los casos que ya están):
- `tests/unit/test_api_cupos.py`:
  - `test_create_cupo_residentes_400`: POST con el sector Residentes → 400 `business_rule` con `sector_id`;
  - `test_update_cupo_residentes_400`: cupo de Residentes creado directo con el modelo y `PUT` → 400; la cantidad no cambia.
- `tests/unit/test_api_resumenes.py`: los tres resúmenes con solicitudes `generada` (creadas con la función de dominio o directo con el modelo):
  - `*_generadas` correctos;
  - `*_regulares` y `*_excepcion` no las cuentan;
  - `condicion=generada` filtra;
  - `condicion=foo` sigue en 400;
  - la cabecera del CSV trae la columna nueva.

Ajustes de forma en tests existentes (solo la clave o la columna nueva):
- `test_api_pedidos.py` (diccionarios de `totals()` cerca de las líneas 156 y 332);
- `test_api_autorizaciones.py` (cerca de la línea 246);
- `test_api_resumenes.py` (`totales` completos y cabecera del CSV).

Si algún otro test falla **solo** por la forma, se ajusta igual y se lista en el reporte. Si falla por otra cosa, **se corrige el código**, no el test.

## 6. Portal (`code/portal`)

### 6.1 Archivos

```
src/modules/autorizacion/
├── routes.js                                   (modificado) ruta /residentes con meta.menu
├── filtros.js                                  (modificado) 'generada' en CONDICIONES
├── seleccion.js / seleccion.test.js            (modificado) soloRegulares y totalesAAutorizar
├── services/autorizacionService.js             (modificado) aTotales y aDesgloseMenu con generada
├── components/MatrizSolicitudes.vue            (modificado) badge "Generada"
├── components/TotalesComparados.vue            (modificado) columna "Generadas" si hay
├── cupos/                                      (modificado, de S8) Residentes sin Editar
└── residentes/                                 (nuevo)
    ├── residentes.js                           funciones puras
    ├── residentes.test.js
    ├── services/residentesService.js           residentes activos y POST /api/resident-lunches
    ├── components/ListaResidentes.vue          tabla con "Excluir" y búsqueda
    ├── components/ResultadoGeneracion.vue      vista previa y reporte
    └── views/AlmuerzosResidentesView.vue
```

No se tocan `router/index.js`, `constants/`, `api/`, `components/` globales ni los módulos `carga-pedidos/`, `reportes/` y `entrega/`. No se agregan dependencias.

### 6.2 Condición `generada` en el módulo

- **`filtros.js`:** `CONDICIONES` suma `{ valor: 'generada', etiqueta: 'Generada' }` al final. Con eso:
  - `FiltrosAutorizacion` ofrece la opción;
  - `parseFiltros` acepta `condicion=generada`;
  - `totales.js` ordena y etiqueta la fila "Generada";
  - `aGrupoResumen` y `aSolicitud` la aceptan.
- **`autorizacionService.js`:**
  - `aTotales` exige `generada` (entero ≥ 0), verifica `total === regular + excepcion + generada` (si no, `respuestaInvalida`) y devuelve `{ total, regular, excepcion, generada, porMenu }`;
  - `aDesgloseMenu` inicializa `generada: 0`;
  - se actualizan los typedefs (`Celda.condicion`, `DesgloseMenu`).
- **`seleccion.js`:**
  - `soloRegulares` → `celda.condicion !== 'excepcion'`, con el docstring "Regulares y generadas en true, excepciones en false". `seleccionInicial` no cambia de comportamiento: un pedido confirmado nunca tiene generadas;
  - `totalesAAutorizar` inicializa `generada: 0`.
- **`MatrizSolicitudes.vue`:** el badge de `generada` usa la clase `badge--info`, la de `excepcion` sigue con `badge--warning` y la de regular, sin clase. El texto sale de `CONDICIONES`.
- **`TotalesComparados.vue`:** columna "Generadas" (encabezado, filas por comida y menú, y total) con `v-if` cuando el total de generadas de lo pedido es > 0.
- **Tests:**
  - `seleccion.test.js`: las formas esperadas suman `generada: 0`; casos nuevos: `soloRegulares` con generadas → `true` y `totalesAAutorizar` cuenta generadas;
  - `filtros.test.js`: `condicion=generada` válido;
  - si hay tests del parser de totales, el caso de suma inconsistente.

### 6.3 `residentes/residentes.js` (puro)

```js
import { normalizarTexto } from '../cupos/cupos'   // de S8; si quedó con otro nombre, usar ese

export const MAX_DIAS_GENERACION = 31

export const MOTIVOS_OMISION = [
  { valor: 'almuerzo_en_otro_sector', etiqueta: 'Ya tiene almuerzo en otro sector' },
  { valor: 'ya_generado', etiqueta: 'Ya generado' },
]

/** misma regla que NOMBRE_SECTOR_RESIDENTES del backend (trim + minúsculas 'es') */
export function esSectorResidentes(nombreSector) {}

/**
 * { desde: string, hasta: string } (texto de los inputs) y hoy ('yyyy-MM-dd') ->
 *   { tipo: 'ok', rango: { desde, hasta } }
 * | { tipo: 'errores', errores: { desde?: string, hasta?: string } }
 * Reglas (las mismas del servidor, que es quien decide): desde obligatoria y fecha válida;
 * hasta vacía = desde; desde >= hoy; hasta >= desde; como máximo MAX_DIAS_GENERACION días.
 */
export function parseRango(textos, hoy) {}

/** ['yyyy-MM-dd', ...] de desde a hasta, inclusive */
export function diasDelRango(rango) {}

/** residentes cuyo apellido, nombre o DNI contiene q (normalizado); q vacío = todos */
export function buscarResidentes(residentes, q) {}

/** clave comparable de una consulta { rango, excluir: Set<number> } (para saber si la vista previa sigue vigente) */
export function claveConsulta(consulta) {}

/** texto del motivo: "Ya tiene almuerzo en Médicos Internos (pedido 4)" / "Ya generado (pedido 14)" */
export function textoOmision(omitido) {}

/** "23 almuerzos en 4 días · 1 omitido" (singulares y plurales) */
export function resumenResultado(resultado) {}
```

`residentes.test.js` cubre:
- `esSectorResidentes`: "Residentes", " residentes ", "RESIDENTES" → true; "Residentes Jefes" → false.
- `parseRango`: válido, `hasta` vacía, `desde` pasada, `hasta < desde`, 31 días (ok), 32 días (error), textos inválidos.
- `diasDelRango`: uno y varios días, cruce de mes.
- `buscarResidentes`: "rios" encuentra "Ríos", búsqueda por DNI parcial y vacío.
- `claveConsulta`: el orden de los excluidos no importa; cambiar una fecha o un excluido cambia la clave.
- `textoOmision` y `resumenResultado`, con singular y plural.

### 6.4 `residentes/services/residentesService.js`

```js
/**
 * Residentes activos: GET /api/personnel-types (id del tipo con nombre 'Residente'; si no
 * existe, Error) + GET /api/personnel?tipo_personal_id=<id>&estado=activo, todas las páginas.
 * -> [{ id, apellido, nombre, dni, menu }] ordenado por apellido y nombre (localeCompare 'es')
 */
export async function residentesActivos() {}

/**
 * POST /api/resident-lunches { fecha_desde, fecha_hasta, excluir, simular } -> Resultado
 * @returns {Promise<{ simulacion: boolean, generados: number,
 *   porFecha: { fecha: string, generados: number, requestId: number | null }[],
 *   omitidos: { personaId, apellido, nombre, dni, fecha, motivo, sector, requestId }[] }>}
 */
export async function generarAlmuerzos({ rango, excluir, simular }) {}
```

Parseo estricto de la respuesta (`aResultado`):
- `simulacion` es booleano y es igual a lo enviado;
- `por_fecha` trae exactamente las fechas de `diasDelRango(rango)`, en orden, con `generados` entero ≥ 0, y `request_id` entero ≥ 1, o `null` solo si `simulacion`;
- `generados` es igual a la suma de `por_fecha`;
- cada omitido trae `person_id` entero ≥ 1, textos no vacíos, `fecha` dentro del rango, `motivo` en `MOTIVOS_OMISION`, `sector_name` texto y `request_id` entero ≥ 1.

Los residentes se parsean igual (`id`, `apellido`, `nombre`, `dni` y `tipo_menu`), y `listar` recorre `next` como `sectores()`.

### 6.5 `residentes/components/ListaResidentes.vue`

- Props: `residentes`, `excluidos` (`Set<number>`) y `deshabilitada`. Emite `cambiar(excluidosNuevos)`.
- `<label for="residentes-buscar">Buscar residente</label>` + `<input type="search">`, con filtrado local (`buscarResidentes`).
- `<table class="table">` en `.table-wrap`, con `<caption>` visualmente oculto "Residentes activos".
- Columnas:
  - Excluir: checkbox con `aria-label="Excluir a <Apellido, Nombre>"`;
  - Apellido y nombre (`<th scope="row">`);
  - DNI;
  - Menú.
- Contador con `aria-live="polite"`: "N residentes · M excluidos". Si hay búsqueda: "Mostrando K de N".
- Sin resultados: "Ningún residente coincide con «q»" y "Limpiar búsqueda".

### 6.6 `residentes/components/ResultadoGeneracion.vue`

- Props: `resultado`, `modo` (`'vista-previa'` | `'resultado'`) y `desactualizada` (solo en vista previa).
- Título:
  - vista previa: "Vista previa: se generarían N almuerzos";
  - resultado: "Se generaron N almuerzos".
  - Debajo, `resumenResultado`. Si `desactualizada`, aviso "Cambiaste el rango o los excluidos: pedí la vista previa de nuevo."
- Tabla "Por día": Día (`formatearDiaCorto`), Almuerzos y Pedido.
  - En la columna Pedido va un link a `autorizacion-detalle` con el `requestId`.
  - En la vista previa sin pedido, se muestra "Nuevo".
  - En el resultado, el link solo aparece si `generados > 0` o el pedido ya existía.
- Tabla "Omitidos" (si hay): Día, Persona (apellido y nombre), DNI y Motivo (`textoOmision`, con link al pedido).
- Sin omitidos: "Sin omitidos."

### 6.7 `residentes/views/AlmuerzosResidentesView.vue`

- `<h1>` "Almuerzos de residentes" y un párrafo: "Genera el almuerzo ya autorizado de cada residente activo para cada día del rango. No consume cupos. Se omite a quien ya tiene el almuerzo de ese día en otro sector."
- Carga `residentesActivos()` con `useAsyncData`. Estados:
  - `LoadingState`;
  - `ErrorState` con reintentar;
  - `EmptyState` "No hay residentes activos". En ese caso, el formulario no se muestra.
- Formulario (`<form>` con `<fieldset>` y `<legend>` "Días"):
  - `<input type="date">` Desde y Hasta, con `<label>`, `aria-invalid` y `aria-describedby` en los errores. Desde arranca en hoy, calculado en el cliente (es solo una sugerencia: decide el servidor) y Hasta, vacío ("igual a Desde");
  - debajo, `ListaResidentes`.
- Botón **"Ver vista previa"** (submit):
  - `parseRango`; si hay errores, se marcan y el foco va al primer campo con error;
  - si no, `generarAlmuerzos({ ..., simular: true })` y se guarda `{ clave: claveConsulta(consulta), resultado }`;
  - mientras corre, los botones se deshabilitan y muestran "Calculando…".
- `ResultadoGeneracion` en modo vista previa, con `desactualizada = claveConsulta(actual) !== vistaPrevia.clave`.
- Botón **"Generar N almuerzos"**:
  - visible con una vista previa;
  - deshabilitado si está desactualizada o si `generados === 0` (en ese caso, el texto "No hay almuerzos para generar");
  - `useConfirm`: "Se van a generar N almuerzos autorizados del <desde> al <hasta>. ¿Continuar?";
  - después, `generarAlmuerzos({ ..., simular: false })` → toast "Se generaron N almuerzos". La vista previa se reemplaza por `ResultadoGeneracion` en modo resultado, con el aviso de diferencia si `generados` no coincide con la vista previa;
  - después de generar, el formulario queda como estaba. Una nueva vista previa muestra los `ya_generado`.
- Errores del POST (vista previa o generación):
  - `ApiError` `VALIDATION` con `fields`:
    - `fecha_desde` → error en Desde;
    - `fecha_hasta` → error en Hasta;
    - `excluir` → mensaje sobre la lista ("Algunos excluidos ya no son residentes activos: recargá la lista"), con un botón para recargar;
    - sin campos → mensaje general en el formulario (por ejemplo, el del sector Residentes).
  - `CONFLICT` (409): mensaje general con el texto de la API.
  - 401, 403 y 503 los maneja el cliente HTTP.
  - Cualquier otro error se propaga a un `ErrorState` del bloque de resultado ("No se pudo generar", con el mensaje).
- Al desmontar o mientras hay una petición en curso, no se lanzan dos a la vez: botones deshabilitados.

### 6.8 `cupos/` (S8): Residentes sin Editar

- Donde S8 decide que una fila no es editable (los sectores inactivos), se agrega el caso `esSectorResidentes(sector.nombre)`, con el motivo "Residentes no tiene cupos: sus almuerzos se generan desde la página Residentes". El motivo va en `title` y en texto visible pequeño, igual que en los inactivos.
- Si el sector está inactivo **y** es Residentes, se muestra el motivo de inactivo.
- La fila sigue visible (tenga o no cupos), así que el conteo de filas del paso 1 de S8 (6 con los seeds) no cambia.
- Test en `cupos.test.js` (o donde S8 pruebe la edición por fila): la fila Residentes no es editable y tiene ese motivo.

### 6.9 `routes.js`

```js
{
  path: '/residentes',
  name: 'residentes-almuerzos',
  component: () => import('./residentes/views/AlmuerzosResidentesView.vue'),
  meta: {
    roles: [ROLES.ALIMENTACION],
    title: 'Almuerzos de residentes',
    menu: { label: 'Residentes', order: 27 },
  },
},
```

## 7. Verificación

### 7.1 Automática

```sh
cd code/admin && poetry run pytest          # nuevos en verde; las fallas previas de SQLite siguen igual (ver reportes S1 a S8)
cd code/portal && npm run lint && npm run build && npm test
```

### 7.2 Manual desde el portal

**Hay cambio de esquema (CHECK de `condicion`) y de permisos:** hace falta `reset-db` + seeds. **No** se resetea la base del usuario: se levantan instancias propias en otros puertos con una base temporal, como en S3, S6, S7 y S8, y se apagan al terminar. En el reporte se avisa que la base del usuario necesita `reset-db` + seeds.

- **Usuarios:** `alimentacion@test.com` / `alim1234`, `alimentacion2@test.com`, `jefe.medicos@test.com` / `jefe1234` y `comedor@test.com` / `comedor1234`.
- **Datos:** sección S9 de `seeds-per-slice.md`:
  - 6 residentes activos (Perez, Fernandez, Benítez, Ríos, Acosta y Suárez);
  - Perez tiene almuerzo en D (pedido 4, Médicos Internos);
  - Acosta tiene almuerzo en D en el pedido 8, que está **rechazado**;
  - Benítez, Fernandez, Suárez y Ríos solo tienen cenas en otros sectores.

| # | Acción | Esperado |
|---|---|---|
| 1 | Entrar como Alimentación | "Residentes" aparece en la sidebar entre Cupos y Reportes. `/residentes` lista los 6 residentes activos en orden alfabético |
| 2 | Buscar "rios", después "3410" | Solo Ríos; los residentes con DNI 3410000x. "Limpiar búsqueda" vuelve a los 6 |
| 3 | Validación en el cliente: Desde = D-1; Hasta < Desde; un rango de 32 días | Errores por campo, **sin** peticiones (pestaña Red) |
| 4 | Rango D a D+3, "Ver vista previa" | Un POST con `simular: true` y respuesta 200. "Se generarían 23 almuerzos". Por día: D 5, D+1 6, D+2 6 y D+3 6, todos "Nuevo". 1 omitido: Perez, D, "Ya tiene almuerzo en Médicos Internos (pedido 4)". Acosta **no** está omitido (el pedido 8 está rechazado) |
| 5 | Marcar "Excluir" en Ríos | La vista previa queda "desactualizada" y "Generar" se deshabilita. Nueva vista previa: 19 y 1 omitido (Ríos no figura) |
| 6 | "Generar 19 almuerzos" → confirmar | POST 201. Toast "Se generaron 19 almuerzos". Resultado con un link a cada uno de los 4 pedidos nuevos. Anotar sus ids en el reporte |
| 7 | Quitar la exclusión de Ríos, vista previa y generar | Vista previa: 4 (uno por día, Ríos) y 20 omitidos (19 "Ya generado" + Perez). Generar: 4 en los **mismos** pedidos |
| 8 | **Criterio "Listo cuando", bandeja:** ir a Autorizaciones en D | Aparece el pedido del sector **Residentes**, Autorizado, con 5 almuerzos. Filtro Condición → "Generada": solo ese pedido. Panel "Totales del período" agrupado por Condición: filas Regular, Excepción y **Generada (5 pedidas, 5 autorizadas)**. Por Sector: fila Residentes. La suma sigue cerrando con la tabla |
| 9 | **Criterio "Listo cuando", totales de la API:** `curl` a `/api/summaries/authorizations?fecha_desde=D&fecha_hasta=D+3&agrupar=condicion` | Fila `generada` con 23 pedidas y 23 autorizadas (19 del paso 6 + 4 del paso 7), separada de `regular` y `excepcion`. `totales.autorizadas_generadas == 23` y `autorizadas_regulares` no las incluye. Con `agrupar=sector`, fila Residentes con 23 |
| 10 | Abrir el detalle del pedido de Residentes de D | Matriz con 5 personas, almuerzo con badge "Generada" y "Residente". Totales con la columna "Generadas" y Regular / Excepción en 0. Historial: "Generación" con el usuario y la hora. Botón "Corregir autorización" habilitado |
| 11 | Corregir: "Solo regulares" | **No** desmarca nada (las generadas quedan autorizadas). Desmarcar a Acosta y confirmar → 200; historial "Corrección" |
| 12 | Volver a generar D a D | 0 generados; Acosta figura como "Ya generado" (no se vuelve a agregar). El pedido no cambia (mismo `modificado_en`) |
| 13 | Como `jefe.medicos@test.com`, cargar un pedido para D+2 con el almuerzo de Fernandez (si el horario de corte lo impide, editar el pedido 11 de D+3 y sumarle el almuerzo de Fernandez) | 409 (la API rechaza el almuerzo ya generado). Con la **cena** de Fernandez, se puede |
| 14 | Como Alimentación, en `/cupos` | La fila Residentes tiene Editar deshabilitado con el motivo. `curl` POST `/api/quotas` con `sector_id` 7 → 400 `business_rule` con `sector_id` |
| 15 | Error del servidor: con `curl`, POST con `fecha_desde` = D-1; con `excluir: [9]` (personal fijo); con `excluir: [1, 1]` | 400 con `fecha_desde`; 400 `business_rule` con `excluir: [9]`; 400 `validation_error` |
| 16 | Sin sector Residentes: en la instancia temporal, renombrar el sector desde la app privada (por ejemplo, "Residentes X"), pedir la vista previa y después volver a renombrarlo | Mensaje general "No existe el sector Residentes…" en la página |
| 17 | Concurrencia: dos pestañas con la vista previa del mismo rango nuevo (D+4); generar en una y después en la otra | La segunda genera 0 y muestra el aviso "El resultado difiere de la vista previa…" con todos "Ya generado" |
| 18 | Entrar como `comedor@test.com` y `jefe.medicos@test.com` | "Residentes" no aparece; `/residentes` lleva a "Acceso denegado". `curl` POST con su token → 403 |
| 19 | Accesibilidad (inspección o lector) | `legend`, `label`, `aria-invalid`, `aria-label` de cada checkbox, contador con `aria-live`, `caption` y `th` con `scope` en las tablas, y el foco al primer error |
| 20 | Regresión: bandeja, detalle de un pedido común, Cupos, Reportes (pantalla y CSV) y carga de pedidos del Jefe | Igual que antes. En Reportes, los totales regulares no incluyen los generados |

### 7.3 Criterio de terminado

- 7.1 y 7.2 pasan completos, incluidos los pasos de navegador. Los omitidos, con su motivo en el reporte.
- La rama está mergeada en local a `feature/autorizacion-pedidos` y **no** está pusheada.
- `seeds-per-slice.md`, sección S9, actualizada con:
  - los números reales de los pasos 4 a 7 y los ids de los pedidos de Residentes generados;
  - que los seeds no generan almuerzos de residentes, y que la generación se prueba con la página;
  - la nota de que S9 cambia el esquema (CHECK de `condicion`) y el permiso nuevo: hay que correr `reset-db` + seeds.
  - En la tabla de usuarios o permisos, si figura, `almuerzo_residentes_new`.
- Está escrito el reporte `documentacion-personal/reporte-s9-almuerzos-de-residentes.md` con la plantilla del artefacto:
  - Encabezado.
  - Alcance, incluidas la condición `generada` en todo el módulo y la regla de cupos de Residentes (sección 0).
  - Cambios:
    - "base de datos: CHECK de `condicion` con `generada`; requiere `reset-db`";
    - los archivos de otros integrantes que se tocaron (`schemas/pedidos.py`, `core/resumen.py`, `schemas/resumenes.py` y `core/cupo.py`) y por qué;
    - los tests existentes ajustados solo en la forma.
  - Prueba manual con el resultado de cada paso.
  - Pendientes y decisiones.

## 8. Pendientes para otras slices

- **Carga:** el 409 por residente duplicado tiene que indicar "la persona, la fecha, la comida y el sector que ya la tiene asignada" (Ampliaciones de la API). Hoy dice solo la persona y la comida. Coordinar con quien mantiene Carga.
- **Carga:** `ETIQUETAS_CONDICION` del portal de Carga no tiene `generada`. Hoy no se ve, porque ningún Jefe accede al pedido de Residentes. Si Carga muestra solicitudes de otros sectores, agregarla.
- **Reportes:** mostrar `*_generadas` en los indicadores y ofrecer "Generada" en el filtro Condición de su pantalla. La API ya lo devuelve.
- **UNIQUE** `(sector_id, fecha)` en `pedidos` cuando Carga pase a "un pedido por sector y fecha". Hoy la generación se protege con el bloqueo del sector.
- **Renombrar el sector Residentes** desde la app privada deja de generar (400). Evaluar un indicador en `Sector` si el grupo lo aprueba (pregunta abierta del artefacto).
- **S10:** los CSV de resúmenes ya traen las columnas `*_generadas` (salen de `CAMPOS_*`). El CSV de detalle por persona tiene que listar los almuerzos generados como sector Residentes.
- **S11:**
  - la página Residentes en móvil (lista larga con checkbox) y la columna "Generadas" de `TotalesComparados` en pantallas chicas;
  - "Ampliaciones de la API": `POST /api/resident-lunches` y la condición `generada`.
