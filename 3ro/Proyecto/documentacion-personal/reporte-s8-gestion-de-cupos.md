# Reporte S8: Gestión de cupos

- **Fecha:** 20/11/2026
- **Rama:** `feat/autorizacion-s8` (sale de `feature/autorizacion-pedidos`, `e8c51c3`, con S7 mergeada). Mergeada en local con `--no-ff`. **No pusheada.**
- **Commits:**
  - `ea29870` test: reglas de PUT /api/quotas y cupos no retroactivos
  - `d9dd307` docs(api): errores de PUT /api/quotas según Etapa 2
  - `8c83976` feat(portal): matriz de cupos por sector con edición en un popover

## Alcance

El plan (`plan-s8-gestion-de-cupos.md`) tiene **menos backend que el artefacto** (sección 0 del plan), y así se hizo:

- **`PUT /api/quotas/{id}` ya cumplía la regla de Etapa 2.** `update_cupo` nunca tuvo el 400 por "cantidad menor a lo entregado", y ya existían el 409 por duplicado y los 400 por sector inactivo o cantidad ≤ 0. No se tocó `core/cupo.py`, `api_cupos.py` ni `schemas/cupos.py`. Se agregaron los tests que faltaban y la descripción en `openapi.yaml`.
- **"Un cupo modificado no cambia la condición de pedidos ya cargados" también ya se cumplía:** la condición se guarda en cada `Solicitud` al crear el pedido. Se agregó un archivo de tests para el criterio.
- **El trabajo de la slice está en el portal:** pantalla **`/cupos`** ("Cupos" en la sidebar, entre Autorizaciones y Reportes, solo para Alimentación). Tiene:
  - matriz sector × comida, con los sectores activos y los inactivos que tienen cupos;
  - búsqueda sin acentos ni mayúsculas, orden por sector y paginación de 25, con `q`, `orden` y `page` en la URL;
  - un popover por fila para crear y modificar los cupos de las cuatro comidas.

**Criterio "Listo cuando":** ✅ "un cupo modificado no cambia la condición de pedidos ya cargados". Se bajó Quirófano/desayuno de 3 a 1 desde `/cupos`, y los pedidos 5 y 9 siguen con 3 regulares y 2 excepciones en el desayuno. El pedido nuevo de Quirófano para D+2 con 5 desayunos quedó con 1 regular y 4 excepciones. Lo cubren `test_cupos_no_retroactivos.py` y el paso 6.

## Cambios

### Base de datos

**Sin cambios** de esquema, seeds ni permisos (`alimentacion` ya tenía `cupo_index`, `cupo_show`, `cupo_new` y `cupo_update`).

### Backend (`code/admin`)

- `docs/openapi.yaml`: `description` de `PUT /quotas/{quota_id}`, con la no retroactividad y los errores 400, 404 y 409 de Etapa 2.
- Sin cambios en `src/`.

### Frontend (`code/portal/src/modules/autorizacion/`)

| Archivo | Qué hace |
|---|---|
| `routes.js` | Ruta `/cupos` (`name: 'cupos'`, `roles: [ALIMENTACION]`, `menu: { label: 'Cupos', order: 25 }`) |
| `cupos/cupos.js` (nuevo) | Funciones puras: `matrizDeCupos`, `normalizarTexto`, `buscarFilas`, `ordenarFilas`, `paginarFilas`, `parseQueryCupos`, `aQueryCupos`, `leerCantidad`, `textosDeFila`, `cambiosDeFila`, `hayCambios`, más `etiquetaDeComida` y `textoCambios` |
| `cupos/services/cuposService.js` (nuevo) | `listarCupos` (todas las páginas), `crearCupo`, `modificarCupo` y `guardarCambios` (en secuencia, se corta en el primer error), con parseo estricto (`aCupo`). `mensajeDeFallo` elige el mensaje de `fields` |
| `cupos/components/MatrizCupos.vue` (nuevo) | Tabla con `caption`, encabezado Sector ordenable con `aria-sort`, "—" con texto para lector de pantalla, columna Editar y el popover en la celda de la fila activa. Devuelve el foco al botón Editar al cerrar |
| `cupos/components/EditorCuposSector.vue` (nuevo) | El popover: `form`, `fieldset` con `legend`, 4 campos con `label`, `aria-invalid` y `aria-describedby`, "Modificado el …", texto de no retroactividad, guardado, toasts y errores por campo |
| `cupos/views/CuposView.vue` (nuevo) | Carga sectores y cupos en paralelo, búsqueda (con `router.replace`), contador `aria-live`, paginador, estado del editor y guardas de navegación con cambios sin guardar |

No se tocó `router/index.js`, `filtros.js`, `autorizacionService.js`, componentes globales ni `carga-pedidos/`. Se reusan `sectores()`, `COMIDAS`, `POR_PAGINA` y `formatearFechaHora` importándolos.

### Tests

Backend:

- `tests/unit/test_api_cupos.py` (+5):
  - `test_update_for_inactive_sector_returns_400`;
  - `test_update_with_invalid_cantidad_returns_400` (0, −1 y `"5"`);
  - `test_update_changes_updated_at`.
- `tests/unit/test_cupos_no_retroactivos.py` (nuevo, 6):
  - bajar y subir el cupo no cambia un pedido confirmado (base y `GET /api/authorizations/{id}`);
  - un pedido autorizado no cambia ni la condición, ni `autorizada`, ni `modificado_en`;
  - un pedido nuevo usa el cupo actual;
  - crear un cupo no cambia un pedido cargado;
  - `test_update_below_delivered_is_allowed`.
- Los tests existentes no se modificaron. Las fixtures se importan de `test_api_pedidos.py` y `test_api_autorizaciones.py`, como ya hacía `test_api_autorizaciones.py`. No hizo falta moverlas a `conftest.py`.

Portal: `cupos/cupos.test.js` (nuevo, 41 tests, con los datos de los seeds y el ejemplo obligatorio de Quirófano).

Resultados:

- `poetry run pytest`: **726 passed, 4 failed**. Las 4 son las fallas previas de SQLite (`test_core_resumen_por_mes_y_semana` y las 3 de `test_home_controller`).
- `npm test`: **106 passed**.
- `npm run build`: ✅
- `npm run lint`: solo los 3 errores **previos** de `carga-pedidos/components/SelectorPeriodo.vue`. El módulo no tiene errores y pasa prettier.

## Prueba manual

**Preparación:** instancias propias para no tocar los datos del usuario: Flask en el 5055 con una base temporal (`reset-db` + `seeds` del 20/11, D = 20/11) y Vite en el 5199 apuntando a ella. Recorrido con Playwright (Chromium headless) como `alimentacion@test.com`. Al terminar se apagaron.

**Antes de la prueba** (`GET /api/authorizations/{id}`): pedidos 5 y 9, desayuno = `{regular: 3, excepcion: 2}`.

| # | Acción | Resultado |
|---|---|---|
| 1 | Entrar como Alimentación | ✅ Sidebar: Autorizaciones, **Cupos**, Reportes. 6 filas (Alimentación, Hemoterapia, Intendencia, Médicos Internos, Quirófano y Residentes) con los 13 cupos de la tabla; 11 celdas "—" con su texto oculto. Rayos no aparece. Residentes, todo en "—" |
| 2 | Buscar "quiro", "MÉDICOS", "médicos int" y "xyz" | ✅ Solo Quirófano; solo Médicos Internos; el espacio se conserva en el campo (`q=médicos+int`); "Ningún sector coincide con «xyz»" con "Limpiar búsqueda". `history.length` no cambia al escribir. Contador "6 sectores" / "4 de 6 sectores" |
| 3 | Clic en "Sector" | ✅ Z→A, `orden=desc`, `aria-sort="descending"`. Recargar conserva `q` y `orden`. `?page=99&orden=foo` muestra las 6 filas A→Z |
| 4 | Paginación | ✅ Con 22 sectores activos más (creados en la base temporal): "Página 1 de 2" con 25 filas; Siguiente → `page=2` con 3 filas; `page=99` → "Página 2 de 2"; buscar "prueba" (22 filas) oculta el paginador y borra `page` |
| 5 | Hemoterapia: desayuno 4 | ✅ Un `POST`, toast "Cupos de Hemoterapia guardados (1 cambio).", la celda muestra 4 y el foco vuelve a Editar |
| 6 | **Criterio "Listo cuando"**: Quirófano desayuno 3 → 1 | ✅ Un `PUT /api/quotas/6`. Pedidos 5 y 9: **3 regulares y 2 excepciones** (sin cambio). Pedido nuevo 14 (`jefe.quirofano`, D+2, 5 desayunos): **1 regular y 4 excepciones**. Capturas del detalle de los tres en el portal |
| 7 | Quirófano: merienda 5 y cena 35 | ✅ `POST` merienda y después `PUT /api/quotas/5` (cena), en ese orden; toast con "(2 cambios)" |
| 8 | Guardado parcial (Intendencia: desayuno 3 = PUT, cena 2 = POST) | ✅ `PUT` con 500 (route de Playwright): "No se pudo guardar. Ocurrió un error inesperado…" en el popover y **no** se hizo el `POST`. Con el `POST` en 409: desayuno guardado (celda en 3), error en Cena y toast "Se guardaron 1 de 2 cambios; falló Cena: Ya existe un cupo para este sector y tipo de comida" |
| 9 | 409 real con dos sesiones | ✅ La segunda muestra el 409 en Cena; al recargar, Cena muestra "Modificado el …" (el cupo de 5 de la otra sesión) y conserva el 2 escrito. Guardar de nuevo hace un `PUT` y la celda pasa a 2 |
| 10 | Validación en el cliente: `0`, `-1`, `2.5`, vaciar Cena y `abc` | ✅ 4 errores por campo con `aria-invalid`, foco en el primero, **sin peticiones**. Guardar deshabilitado sin cambios |
| 11 | `curl`: `PUT` con `cantidad: 0`; `POST` para Rayos | ✅ 400 `validation_error` con `fields.cantidad`; 400 `business_rule` con `fields.sector_id`. Además: `POST` Quirófano/desayuno → 409, `PUT` como jefe → 403 |
| 12 | Hemoterapia inactiva | ✅ Badge "Inactivo", Editar deshabilitado con `title` y el texto "Sector inactivo: no se pueden crear ni modificar sus cupos". Reactivada al terminar. Ver la nota de abajo |
| 13 | Pedido 7 (desayuno entregado a 2): Alimentación/desayuno 50 → 1 | ✅ 200, la celda muestra 1 |
| 14 | Teclado | ✅ Enter abre, con foco en Desayuno y `aria-expanded="true"`. Tab recorre los campos. Clic afuera **no** cierra. Escape cierra y el foco vuelve a "Editar cupos de Quirófano". Con cambios, Editar en Hemoterapia muestra "Guardá o cancelá los cambios de Quirófano." Cambiar el orden con la fila visible no pregunta. Buscar algo que oculta la fila pide confirmación: "Seguir editando" deja la URL y el campo como estaban; "Descartar" cierra y navega |
| 15 | `jefe.quirofano@test.com` | ✅ Sidebar: Carga de pedidos, Personal. `/cupos` → `/acceso-denegado` |
| 16 | Accesibilidad | ✅ `caption` "Cupos por sector y comida", 6 `th[scope=col]`, 6 `th[scope=row]`, `aria-sort`, 6 Editar con nombre propio, `legend`, `label`, `aria-invalid`/`aria-describedby` y contador `aria-live` |
| 17 | Regresión | ✅ La bandeja carga sus pedidos y Reportes muestra "Reportes y estadísticas" |

No hubo errores de página (`pageerror`) en ningún recorrido.

**Visual:** a 1280 px la matriz entra entera y el popover (460 px, campos en 2 columnas) se abre debajo del botón Editar y sale del contenedor de la tabla. A 390 px la tabla scrollea dentro de `.table-wrap` y el editor ocupa lugar dentro de la celda, con los campos en una columna. Igual que en S7, la página tiene scroll horizontal a 390 px. Viene del layout y ya estaba antes.

## Pendientes y decisiones

Decisiones de la sesión (desvíos menores del plan):

- **`type="text" inputmode="numeric"` en vez de `type="number"`.** Con `type="number"` el navegador entrega `''` cuando el texto no es un número ("abc" en Firefox), y Vue convierte el valor a número. Así `leerCantidad` no ve lo que escribió el usuario. Con texto, el parser recibe el texto tal cual y rechaza `abc`, `2.5` y `1e3` con su mensaje. El teclado numérico del móvil se mantiene.
- **Campo de búsqueda con estado propio.** La URL guarda `q` recortado. Si el campo leyera la URL directamente, se borraría el espacio mientras se escribe "médicos int". El campo vuelve al valor de la URL solo si la navegación se cancela (cambios sin guardar), no cuando la URL no cambia (falla `duplicated`). Ese caso fue un bug de la primera versión, encontrado en el paso 2.
- **Guardas de ruta para los cambios sin guardar.** La regla "cerrar si no hay cambios; con cambios, confirmar solo si la fila deja de verse" está en `onBeforeRouteUpdate`. Cubre la búsqueda, el orden, el paginador y también Atrás/Adelante. `onBeforeRouteLeave` pide confirmación al salir de `/cupos` con cambios.
- **La matriz no se desmonta al recargar.** `LoadingState` y `ErrorState` reemplazan la tabla solo en la primera carga. Si falla una recarga, el error aparece arriba de la tabla, con Reintentar, y el popover conserva lo escrito.
- **Popover en 2 columnas.** Con los cuatro campos en una columna, el popover era muy alto y el toast de error (que no se cierra solo) tapaba Guardar y Cancelar al pie de la pantalla.
- **Un commit de portal en vez de dos.** La matriz renderiza el editor y la vista usa los dos, así que separarlos dejaba un commit que no compilaba.
- **`test_update_below_delivered_is_allowed`** quedó en `test_cupos_no_retroactivos.py` y no en `test_api_cupos.py`. La fixture `data` de cupos no tiene personal ni menús para armar un pedido y una entrega. Los números de los tests de no retroactividad salen de la fixture de pedidos (almuerzo, cupo 2, 3 personas → 2 regulares y 1 excepción) en vez de "desayuno 3 con 5 personas". El criterio que verifican es el mismo.
- **Errores no previstos al guardar** (500, red, respuesta inválida): `guardarCambios` los propaga. El popover los muestra con "No se pudo guardar" y los registra con `console.error` (es el límite de la acción). Igual se recarga la matriz.

Notas y pendientes:

- **Desactivar un sector de los seeds** desde la app privada falla con "hay 1 usuario activo asociados (Jefes de Servicio)". Para el paso 12 se cambió `active` directamente en la base temporal. Para la demo hay que bloquear al jefe antes o usar Rayos con un cupo.
- **Mensajes de validación en inglés:** el 400 de marshmallow (`"Must be greater than or equal to 1."`) llega en inglés. En la pantalla casi no se ve, porque el cliente valida antes, pero aparecería con datos que pasen el cliente y no el servidor.
- **UNIQUE `(sector_id, tipo_comida)` en la base** y **token de versión para cupos** (hoy el último `PUT` gana): ver la sección 8 del plan.
- **Coordinación con Carga:** `update_pedido` recalcula la condición de un pedido **confirmado** con el cupo actual cuando el Jefe lo modifica. Es la regla de "modificar pedido" del enunciado y es distinta de "cambiar el cupo no altera pedidos registrados".
- **S9:** Residentes aparece en la matriz sin cupos y con Editar habilitado. Evaluar si se oculta o se deshabilita.
- **S11:** la matriz y el popover en el móvil; "Ampliaciones de la API" (`PUT /api/quotas`, solo documentación).
