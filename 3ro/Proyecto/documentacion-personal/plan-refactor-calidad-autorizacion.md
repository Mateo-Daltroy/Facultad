# Plan de refactor de calidad: Módulo de Autorización

- **Rama base:** `feature/autorizacion-pedidos`. Crear `refactor/autorizacion-calidad` desde ella.
- **Fecha de la auditoría:** 20/11/2026
- **Alcance:** calidad del código del módulo: backend (`admin/src/core`, `admin/src/web`) y portal (`portal/src/modules/autorizacion`). La completitud funcional ya se auditó en `auditoria-feature-completion-autorizacion.md`.

## Veredicto de la auditoría

**El código está en buen estado y no necesita un refactor estructural.** Las reglas de negocio tienen una sola fuente: `acciones_permitidas`, la versión opaca del pedido y los parsers del rango y del motivo. Los datos externos se convierten una sola vez, en el borde: los schemas de marshmallow en la API y las funciones `a*()` de los servicios del portal. Las escrituras son todo o nada, con bloqueo de fila. La cobertura de tests es alta: 157/157 en Vitest, la suite del módulo en pytest en verde y ESLint sin errores en el módulo.

Hay tres tipos de problemas, en orden de importancia:

1. **Incumplimientos de la consigna.** Son baratos de corregir y son los que puede marcar la cátedra: faltan docstrings, el formato de fecha no es el pedido y quedan comentarios con jerga interna.
2. **Código duplicado** entre archivos del módulo: tres parsers de rango, dos `_commit`, dos generadores de CSV y los mismos helpers de parseo en tres servicios del portal.
3. **Archivos que concentran demasiado**, como `DetallePedidoView.vue`, `filtros.js` y `autorizacionService.js`. Son mejoras de legibilidad y no son urgentes.

> **Sobre el plazo:** la entrega es hoy (20/11/2026). Si no hay margen, conviene hacer **solo la Fase 1**: es de bajo riesgo y apunta a lo que se evalúa. Las fases 2 y 3 pueden quedar para después de la entrega o para la defensa.

---

## Reglas para el agente que ejecute este plan

1. Trabajar en `refactor/autorizacion-calidad` y hacer **un commit por paso**, con Conventional Commits en español (`refactor(api): …`, `refactor(portal): …`, `docs(api): …`).
2. **No cambiar el contrato de la API**: rutas, nombres de campos, códigos de estado y textos de error que comparan los tests. La única excepción es el paso 2.4, que requiere coordinar con el equipo.
3. Después de cada paso, correr y dejar en verde:
   - `cd admin && poetry run pytest -q`. Hoy fallan 4 tests ajenos al módulo: 3 de `test_home_controller.py` y `test_api_resumenes.py::test_core_resumen_por_mes_y_semana`, que falla en SQLite por `date_trunc`. **No pueden aparecer fallos nuevos.**
   - `cd portal && npx vitest run && npx eslint src/modules/autorizacion && npm run build`
4. Si un paso rompe tests que comparan mensajes o formas, se adapta el código para conservar el comportamiento. Los tests no se editan, salvo que el paso lo indique.
5. Seguir las reglas globales del usuario:
   - *Parse, don't validate*: cada dato se convierte una sola vez y se pasan tipos precisos.
   - Capturar solo excepciones con nombre: nada de `except Exception` que oculte errores. El `rollback` + `raise` de `_commit` está permitido.

---

## Fase 1: cumplimiento de la consigna (prioridad alta, riesgo bajo)

### 1.1 Docstrings en todo el código Python del módulo

La consigna pide *"documentar todo el código Python con docstrings"*. Ruff (`--select D1`) encuentra **82 faltantes** en los archivos del módulo: 37 clases, 21 métodos, 11 clases `Meta`, 9 funciones y 4 módulos.

- Archivos: `core/autorizacion_cambio.py`, `core/autorizacion_fechas.py`, `core/exportacion.py`, `core/residentes.py`, `web/controlers/api_autorizaciones.py`, `web/controlers/api_comidas.py`, `web/schemas/autorizacion_fechas.py`, `web/schemas/autorizaciones.py`, `web/schemas/residentes.py`, `web/schemas/entregas.py`, `web/csv_export.py` y los demás de la lista del paso 3.
- Agregar un docstring de una línea, que diga qué es o qué hace, a cada dataclass, schema, endpoint (`index`, `show`, `reject`), función `dump_*` y módulo. Las clases `Meta` de marshmallow (D106) se pueden ignorar.
- Respetar el estilo del módulo: español y minúscula inicial.
- Verificación (desde `admin/`, con ruff instalado en un venv aparte; no agregarlo a `pyproject.toml` sin consultar):
  ```
  ruff check --select D100,D101,D102,D103 <archivos del módulo>
  ```
  tiene que dar 0.

### 1.2 Fechas en formato dd/mm/aaaa

La consigna (Diseño responsive y accesibilidad) dice: *"Las fechas se mostrarán en formato dd/mm/aaaa"*. Hoy el portal muestra "jueves 19 de noviembre de 2026" (`formatearFechaLarga`, `formatearDiaYMes` y `formatearSemana` en `filtros.js`) en el detalle, la bandeja, el calendario, la semana, los totales y los residentes.

- **Decisión del equipo antes de ejecutar.** Propuesta: conservar el día de la semana y mostrar el número en el formato pedido, por ejemplo "jueves 19/11/2026". La semana quedaría "lunes 16/11/2026 al domingo 22/11/2026". El título del mes del calendario ("noviembre 2026") puede quedar como está, porque no es una fecha.
- Cambiar solo las funciones de formato de `filtros.js` (o de `fechas.js` si el paso 3.1 ya se hizo) y actualizar sus tests en `filtros.test.js`.
- `formatearFechaHora` ya usa `dd/MM/yyyy HH:mm` y queda igual.

### 1.3 Sacar la jerga de planificación de los comentarios

Hay 22 líneas agregadas que nombran slices internos ("S9", "S10", "S4", "antes de S6"). El código entregado no tiene que depender de los planes.

- Buscar con `git diff origin/development...HEAD -- admin/src portal/src | grep -nE '^\+.*\bS[0-9]{1,2}\b'`.
- Reescribir cada comentario describiendo la funcionalidad. Por ejemplo, `(S10)` pasa a "exportaciones CSV" y `(residentes, S9)` pasa a "(almuerzos de residentes)". En los tests, el nombre de los `describe` se puede dejar.

### 1.4 (Funcional, fuera del refactor) Pantalla inicial filtrada a pendientes

Ya está informado en la auditoría de completitud. Se lista acá para que no quede olvidado: `ROLE_HOME_ROUTE` del rol Alimentación tiene que llevar a `autorizacion-pendientes` con `query: { estado: 'confirmado' }`.

---

## Fase 2: duplicación (prioridad media, riesgo bajo o medio)

### 2.1 Backend: un solo tipo de rango de fechas

Hay tres clases casi idénticas:

- `RangoFechas` en `core/autorizacion_fechas.py` (máximo 62 días o sin máximo).
- `RangoGeneracion` en `core/residentes.py` (máximo 31 días, sin fechas pasadas, `hasta=None` equivale a `desde`).
- `RangoExportacion` en `core/exportacion.py` (máximo 62 días).

Las tres validan `hasta >= desde` y el tope de días, y las tres tienen `dias()` o algo equivalente.

- Crear `core/rango_fechas.py` con `RangoFechas` (dataclass `frozen`), `dias()` y un único `parse(desde, hasta, *, max_dias: int | None, no_antes_de: date | None = None)`.
- **Conservar los textos de error exactos** que comparan los tests. `test_api_autorizacion_fechas.py` espera `"No puede ser anterior a fecha_desde."` y `"El rango no puede superar los 62 días."`. Los de residentes y exportación también tienen tests; revisarlos con `grep`.
  - Si los textos difieren entre los tres usos, unificarlos al de `RangoFechas` solo cuando ningún test compare el texto viejo. Si alguno lo compara, aceptar un parámetro opcional con el texto.
- Reemplazar los tres usos. `RangoGeneracion.parse(desde, None, hoy)` pasa a `RangoFechas.parse(desde, hasta or desde, max_dias=MAX_DIAS_GENERACION, no_antes_de=hoy)`.
- Mantener las constantes `MAX_DIAS_*` en cada módulo de dominio.

### 2.2 Backend: helpers de sesión duplicados

`_commit()` está repetido, igual, en `core/autorizacion.py` y en `core/residentes.py`.

- Moverlo a `core/database.py` como `commit_o_rollback()`, con docstring, y usarlo desde los dos archivos.
- Mantener la forma actual: `except Exception: rollback(); raise`. Es una limpieza con *re-raise*, permitida por las reglas.

### 2.3 Backend: una sola definición del orden de las comidas

Las comidas en orden están definidas tres veces:

- `ORDEN_COMIDAS` en `core/autorizacion_cambio.py`.
- `COMIDAS` en `core/exportacion.py`, con un `assert` que se ejecuta al importar el módulo.
- El set `tipos_comida_validos` en `core/cupo.py`.

Cambios:

- Definir `COMIDAS_EN_ORDEN: tuple[str, ...] = ("desayuno", "almuerzo", "merienda", "cena")` en `core/cupo.py`, junto a `tipos_comida_validos`, y derivar el set de la tupla: `tipos_comida_validos = frozenset(COMIDAS_EN_ORDEN)`. Verificar que nadie modifique el set: hoy es un `set` mutable.
- Usar la tupla en `autorizacion_cambio.py` y en `exportacion.py`, y eliminar el `assert` del import.

### 2.4 Backend: un solo generador de CSV (coordinar con quien hizo Reportes)

Hay dos implementaciones:

- `web/csv_export.respuesta_csv`: separador `;`, protección contra inyección de fórmulas (OWASP) y BOM.
- `api_resumenes._csv`: separador `,`, **sin** protección contra inyección y con el BOM y el `Response` copiados.

Cambios:

- Hacer que `api_resumenes._csv` arme la cabecera y las filas y delegue en `respuesta_csv`.
- **Esto cambia el separador de los CSV de resúmenes de `,` a `;`.** Los usa el módulo Reportes (`reportesService.js`). Antes de ejecutarlo, confirmar con el equipo. Si no hay acuerdo, agregar a `respuesta_csv` un parámetro `delimitador` y que resúmenes conserve `,`: igual gana la protección contra inyección.
- Revisar `test_api_resumenes.py::test_export_csv` y los demás tests de CSV.

### 2.5 Portal: primitivas de parseo compartidas

Estas funciones están repetidas en `services/autorizacionService.js`, `cupos/services/cuposService.js` y `residentes/services/residentesService.js`:

- `respuestaInvalida`
- `esObjeto`
- `esTexto`
- `esEntero`
- `aId` / `aIdPositivo`
- `aCantidad`
- `aFechaHora`
- `aFecha`
- `esUnoDe`

Además, el bucle "todas las páginas" está repetido en `sectores()` y `listarCupos()`.

- Crear `portal/src/modules/autorizacion/services/parseo.js` con esas primitivas, cada una con JSDoc de una línea, y una función `todasLasPaginas(path, params, aItem)` que valide `items` y `next` como hoy hace `listarCupos`, que es la versión más estricta.
- Reemplazar las copias locales en los tres servicios. No cambiar los mensajes `Respuesta inválida de la API: …`, porque algún test puede compararlos; revisarlo con `grep`.
- Agregar `parseo.test.js` con casos mínimos: entero negativo, texto vacío, fecha imposible y `next` que no avanza.
- Fuera de alcance: `reportes` y `carga-pedidos` tienen sus propias copias de `/sectors` y `/menu-types`. **No tocarlas** sin coordinar.

### 2.6 Portal: buscar etiquetas en los catálogos

El patrón `CATALOGO.find((opcion) => opcion.valor === valor).etiqueta` aparece 9 veces y el patrón `.some((o) => o.valor === x)` aparece 5 veces. Están en `MatrizSolicitudes`, `HistorialAutorizacion`, `TotalesPeriodo`, `cupos.js`, `exportacion.js` y `filtros.js`.

- Agregar al módulo de catálogos dos funciones:
  - `opcionDe(catalogo, valor)`: lanza `Error` si el valor no existe, como hoy `datosDeEstado`.
  - `esOpcionDe(catalogo, valor)`.
- Reemplazar los usos. `datosDeEstado` y `textoDeBloqueo` pasan a ser `opcionDe(ESTADOS, v)` y `opcionDe(MOTIVOS_BLOQUEO, v).texto`.
- `exportacion.js:102` usa `?.etiqueta ?? valor` a propósito, como fallback para mostrar. Dejarlo así o hacerlo explícito.

---

## Fase 3: legibilidad (prioridad baja, riesgo medio)

### 3.1 Portal: separar `filtros.js`

Hoy `filtros.js` mezcla tres responsabilidades: los catálogos (`ESTADOS`, `COMIDAS`, `CONDICIONES`, `MOTIVOS_BLOQUEO`, `TIPOS_CAMBIO`, `VISTAS`, `AGRUPAMIENTOS_TOTALES`, `POR_PAGINA`), las utilidades de fecha (`hoyISO`, `sumarDiasISO`, `parseFechaISO`, `formatear*`) y el estado de la URL (`parseFiltros`, `aQuery`).

- Separarlo en tres archivos:
  - `catalogos.js`: las constantes y `opcionDe` y `esOpcionDe` del paso 2.6.
  - `fechas.js`: las utilidades y los formatos.
  - `filtros.js`: solo `parseFiltros` y `aQuery`.
- Actualizar todos los imports. **No** dejar re-exports de compatibilidad.
- Separar también `filtros.test.js`.
- Mover a `fechas.js` el `fechaCorta` de `DetallePedidoView.vue` (`iso.split('-').reverse().join('/')`), como `formatearFecha(iso)` con `date-fns` y un test.

### 3.2 Portal: aligerar `DetallePedidoView.vue` (535 líneas)

1. **Manejo de errores repetido.** `autorizar`, `confirmarRechazo` y `guardarCorreccion` tienen el mismo `try/catch`: si no es `ApiError`, lo relanzan; ante un 409, avisan y recargan; ante un 400, muestran el error; en `finally`, apagan `enviando`. Extraer una función local `enviar(operacion, { alConflicto, alValidacion })` que conserve las diferencias de cada caso:
   - El rechazo cierra el diálogo ante un 409 y muestra el error en `errorMotivo` ante un 400.
   - La corrección apaga `corrigiendo` ante un 409.
2. **Textos.** Mover `textoConfirmacion`, `detallePorComida`, `textoCorreccion`, `textoCambios` y `plural` a `autorizacion/textos.js`, como funciones puras, con `textos.test.js` que cubra singular, plural y casos borde (0 cambios, todas, ninguna). `residentes/residentes.js` tiene su propio `plural`: usar el compartido.
3. **Navegación.** Mover `esBandeja`, `rutaAnterior`, `destino`, `volver` y `volverABandeja` a `composables/useVolverABandeja.js`.
4. En `resumenRechazo` hay un `throw` dentro de un `computed`. Pasarlo a la función `rechazar()`, que es la que abre el diálogo, o garantizar el invariante de otra forma.

Resultado esperado: la vista queda con el estado, el `template` y el cableado, en menos de unas 300 líneas.

### 3.3 Portal: dividir `autorizacionService.js` (717 líneas)

Partirlo por área, manteniendo la regla "se parsea una sola vez, en el servicio":

- `services/autorizacionService.js`: bandeja, detalle, autorizar, rechazar e historial.
- `services/calendarioService.js`: `fechasConPedidos`, `fechasAdyacentes` y `semanaDeAutorizaciones`.
- `services/totalesService.js`: `paramsDeTotales`, `totalesDelPeriodo` y su parseo.
- `services/catalogosService.js`: `sectores` y `tiposDeMenu`.

Cada archivo importa las primitivas de `parseo.js` (paso 2.5).

### 3.4 Backend: tipos más precisos

1. `FechasQuerySchema` y `SemanaQuerySchema` (`web/schemas/autorizacion_fechas.py`) devuelven `dict` desde `post_load`, que después se pasa con `**` al core. Hacer que devuelvan dataclasses `frozen`, como ya hacen `RevisionAutorizacion`, `ConsultaPaginada` y `PedidoGeneracion`, y que el controlador pase los campos con nombre. En `adjacent()` desaparece el `consulta.pop("rango")`.
2. Definir `EstadoPedido = Literal["confirmado", "autorizado", "rechazado"]` en `core/pedido.py` y usarlo en las firmas nuevas del módulo: `CeldaSemana.estado`, `fechas_con_pedidos` y `semana_de`. **No** cambiar la columna ni `consulta_pedidos`, que comparte Carga de Pedidos.
3. En `exportacion.detalle_por_persona`, el acumulador `grupos` es un `dict` con claves `"datos"`, `"comidas"` y `"menus"`. Reemplazarlo por una dataclass privada `_Grupo`.
4. En `autorizacion_cambio.historial_de`, el código accede a las filas por índice (`fila[0]`, `grupo[0][3]`, `f[1]`). Desempaquetar con nombres (`cambio, solicitud, persona, usuario`) para que se lea mejor.

### 3.5 Portal: exportación fuera de la bandeja

`BandejaAutorizacionView.vue` contiene el estado y el manejo de errores del diálogo de exportación: `exportacion`, `abrirExportacion`, `cerrarExportacion`, `exportar` y `manejarErrorExportacion`. Moverlos a `exportacion/useExportacionCsv.js`, que recibe `filtros` y los catálogos.

---

## Fuera de alcance (anotado, no hacer en esta rama)

- `date.today()` en `core/entrega.py` (líneas 83 y 216), `api_entregas.py:55` y `api_comidas.py:56`. Debería ser `reloj.hoy()`, que es la hora de Buenos Aires, igual que el resto del módulo. Pertenece a Entregas; avisarle a quien lo hizo.
- Los 3 errores de ESLint en `carga-pedidos/components/SelectorPeriodo.vue`.
- El test `test_core_resumen_por_mes_y_semana`, que falla en SQLite. Hay que correrlo contra PostgreSQL.
- Los catálogos `/sectors` y `/menu-types` están repetidos entre los módulos del portal. Para unificarlos hace falta acordar un servicio común en `src/services`.

## Criterio de terminado

- La Fase 1 está completa y verificada (ruff D1 = 0, sin "S\d" en los comentarios y las fechas en dd/mm/aaaa).
- Cada paso de las fases 2 y 3 que se haga tiene su commit, y las suites quedan como al empezar o mejor: mismos fallos preexistentes, ninguno nuevo y más tests en el portal.
- `npm run build` compila sin errores.
- Un reporte corto en `documentacion-personal/reporte-refactor-calidad-autorizacion.md` con los pasos hechos, los omitidos y por qué, y los resultados de las suites.
