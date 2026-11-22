# Informe: alcance y justificación del tamaño del Módulo de Autorización

- **Proyecto:** Sistema para la gestión del servicio de alimentación, Etapa 2
- **Módulo:** Autorización de pedidos (rol Servicio de Alimentación)
- **Rango medido:** desde el merge de `feature/reportes` en `development` (`eaa3d5e`) hasta el último commit de `feature/autorizacion-pedidos` antes de integrarse (`4ed7585`). Son 101 commits.
- **Fecha:** 20/11/2026

---

## 1. Resumen

El módulo suma **14.420 líneas agregadas** (y 328 eliminadas) en 87 archivos. Ese número junta cosas de naturaleza muy distinta:

| Categoría | Líneas | % | Qué es |
|---|---:|---:|---|
| Tests automatizados (backend y portal) | 4.335 | 30% | 376 tests nuevos o ampliados. No es código que se ejecute en producción. |
| Documentación de la API (`openapi.yaml`) | 488 | 3% | Contrato de los endpoints nuevos y de los que cambiaron. |
| Dependencias (`package-lock.json`) | 261 | 2% | Generado por npm, no escrito a mano. |
| Datos de prueba (`seeds.py`) | 390 | 3% | Pedidos, cupos y residentes de ejemplo para mostrar el módulo. |
| **Backend (`admin/src`)** | **~1.870** | 13% | Reglas de negocio, endpoints y validaciones. |
| **Portal (`portal/src/modules/autorizacion`)** | **~7.080** | 49% | Interfaz Vue del Servicio de Alimentación. |

El **código de producción es de unas 8.950 líneas**, y cubre **15 requerimientos funcionales** de la consigna, más 5 notas con reglas de negocio y 3 ampliaciones de la API. Da un promedio de unas 600 líneas por requerimiento, sumando backend e interfaz. Cada requerimiento además tiene sus tests.

---

## 2. Qué pide la consigna

La sección "Módulo de Autorización" de la Etapa 2 pide, textualmente:

**Consulta de pedidos**
1. Vistas diarias y semanales, con navegación entre períodos.
2. Consultar solo los períodos con pedidos, marcando en el calendario los días con datos.
3. Filtrar por sector, tipo de comida, tipo de menú, condición (Regular/Excepción) y estado.
4. Identificar claramente los pedidos pendientes (estado confirmado).

**Autorización**

5. Detalle del pedido por persona y comida, con tipo de menú y condición.
6. Autorizar o no autorizar cada solicitud individualmente.
7. Autorizaciones masivas, con accesos rápidos como "todas las regulares" o "el pedido completo".
8. Mostrar, antes de confirmar, los totales pedidos y los que quedarían autorizados.
9. Rechazar un pedido completo con motivo obligatorio.
10. Corregir una autorización ya hecha, en las condiciones de las Notas.

**Gestión y consulta**

11. Ver por separado las cantidades pedidas y las autorizadas.
12. Totales por sector, fecha, comida y tipo de menú.
13. Gestionar los cupos por sector y comida: listar, crear y modificar.
14. Generar en una sola acción el almuerzo de todos los residentes activos, para una fecha o un rango, excluyendo personas puntuales y sin consumir cupos.
15. Exportar a CSV, respetando los filtros, (a) el detalle de personas autorizadas y (b) los totales por sector, comida y menú.

Además, 16. consultar períodos anteriores en modo de solo lectura.

**Notas, que son reglas de negocio obligatorias:**
- Lo pedido nunca se sobrescribe con lo autorizado.
- Lo autorizado se calcula desde las solicitudes nominadas. No hay carga manual de totales.
- Los días anteriores a hoy no se pueden autorizar ni rechazar.
- Una corrección solo se permite si el pedido es de hoy o futuro y no tiene entregas. Cada corrección queda registrada con usuario, fecha y hora.
- **Concurrencia:** si el pedido cambió mientras se revisaba, porque otro usuario lo rechazó o el Jefe lo modificó, la API responde 409 y la interfaz lo informa y recarga.

**Ampliaciones de la API:** `GET /api/authorization-dates`, `POST /api/resident-lunches`, y `formato=csv` en `authorized-meals` y `summaries`.

Además, la consigna general exige una **interfaz responsive y mobile first**, y **controles de acceso y validaciones tanto en la interfaz como en el backend**.

---

## 3. Trazabilidad: requerimiento → código

Las líneas son del código de producción, sin tests. El service HTTP compartido (`autorizacionService.js`, 709 líneas) se cuenta aparte porque sirve a los bloques A, B y C.

### A. Consulta: bandeja, vistas, calendario y filtros (req. 1–4 y 16)

| Capa | Archivos | Líneas |
|---|---|---:|
| Backend | `autorizacion_fechas.py` (días con pedidos, fechas adyacentes, semana sector × día), `api_autorizacion_fechas.py`, `schemas/autorizacion_fechas.py`, listado y filtros en `pedido.py` y `sector.py` | ~440 |
| Portal | `BandejaAutorizacionView` (338), `CalendarioDias` (327), `PeriodoNav` (218), `FiltrosAutorizacion` (167), `TablaSemana` (162), `TablaPedidos` (98), `SelectorVista` (52), `filtros.js` (177), `calendario.js` (76) | 1.615 |

**Por qué ocupa esto:**
- Son **dos vistas distintas**:
  - La diaria es una tabla paginada de pedidos.
  - La semanal es una matriz sectores × 7 días, donde cada celda enlaza al pedido y cada encabezado a la vista diaria.
- La regla "solo períodos con datos" se resolvió con:
  - un calendario mensual que marca los días con pedidos y cuántos tiene cada día por estado;
  - botones "anterior/siguiente con pedidos", que saltan los días vacíos. Para eso hizo falta un endpoint específico (`/authorization-dates/adjacent`).
- Los 5 filtros se reflejan en la URL (`parseFiltros` / `aQuery`). Así, recargar la página, compartir el link o volver atrás conserva la vista exacta.
- Pendientes destacados: los confirmados salen primero (orden del backend) y resaltados. Además, la pantalla inicial del rol arranca filtrada en pendientes.

### B. Detalle, autorización, rechazo y corrección (req. 5–11 y Notas)

| Capa | Archivos | Líneas |
|---|---|---:|
| Backend | `autorizacion.py` (reglas, autorizar, rechazar, versión, bloqueo de fila), `autorizacion_cambio.py` (historial auditable), `schemas/autorizaciones.py`, `api_autorizaciones.py`, `reloj.py` | ~530 |
| Portal | `DetallePedidoView` (536), `MatrizSolicitudes` (442), `TotalesComparados` (229), `DialogoRechazo` (173), `HistorialAutorizacion` (162), `AtajosSeleccion` (67), `seleccion.js` (191), `useSeleccionSolicitudes.js` (103) | 1.903 |

Es el núcleo del módulo y el bloque con más reglas:
- **Matriz persona × comida**, por día, con menú y condición de cada solicitud. Los pedidos semanales y mensuales tienen varios días, así que la matriz se arma por cada día.
- **Selección masiva en varios niveles:**
  - todo el pedido, solo regulares o ninguno (atajos);
  - una columna entera (una comida para todas las personas);
  - una fila entera (todas las comidas de una persona);
  - celda por celda.

  Los grupos tienen estado "parcial" (checkbox indeterminado). Todo vive en funciones puras (`seleccion.js`) testeadas por separado.
- **Totales antes de confirmar** (req. 8): pedidos, ya autorizados y "quedarían autorizados" con la selección actual, por comida y desglosados por menú.
- **Rechazo** con motivo obligatorio, validado en los dos extremos (largo máximo, sin espacios vacíos), y con un resumen de lo que se rechaza.
- **Corrección** (req. 10): el mismo flujo, pero arranca desde lo autorizado y cuenta los cambios.
- **Historial auditable:** cada decisión guarda quién, cuándo, el valor anterior y el nuevo, y el tipo de operación (autorización, corrección o rechazo). Se muestra agrupado por operación.
- **Reglas de fecha y estado en un solo lugar:** `acciones_permitidas()` decide qué se puede hacer hoy y, si no se puede nada, por qué (período anterior, tiene entregas, ya rechazado…). La usan el GET, para mostrar u ocultar botones con el motivo, y también el PUT y el POST, para rechazar con 409. Así, interfaz y backend no pueden contradecirse.
- **Concurrencia (Nota 5), resuelta en dos capas:**
  1. *Bloqueo optimista:* el portal manda la versión que vio (`modificado_en`). Si no coincide, devuelve 409.
  2. *Bloqueo pesimista:* `SELECT … FOR UPDATE` sobre la fila del pedido. Evita que dos revisores simultáneos pasen ambos la verificación.

  Ante un 409, la interfaz avisa y recarga el pedido, como pide la consigna.
- **Integridad del body:** el PUT exige una decisión sobre *todas* las solicitudes del pedido, y *solo* sobre ellas. Así no quedan solicitudes sin decidir ni se cuelan IDs ajenos.

### C. Totales del período (req. 12)

| Capa | Archivos | Líneas |
|---|---|---:|
| Backend | ajustes en `resumen.py` (el endpoint `summaries/authorizations` ya existía) | ~10 |
| Portal | `TotalesPeriodo` (250), `totales.js` (142) | 392 |

Calcula los totales del día o la semana visible, con todos los filtros y de **todas las páginas** (no solo la visible). Se pueden agrupar por sector, fecha, comida o menú. Carga por separado, así que un error en totales no rompe la bandeja.

### D. Gestión de cupos (req. 13)

| Capa | Archivos | Líneas |
|---|---|---:|
| Backend | reutiliza la API de cupos de la Etapa 1, con un ajuste en `cupo.py` | ~5 |
| Portal | `CuposView` (298), `EditorCuposSector` (274), `MatrizCupos` (205), `cupos.js` (192), `cuposService.js` (111) | 1.080 |

Es una pantalla completa: matriz sectores × comidas, con búsqueda sin acentos, orden, paginación en la URL y un editor por sector que detecta cambios y avisa antes de descartarlos. Además respeta dos reglas: los sectores inactivos no se editan, y el sector Residentes no consume cupos.

### E. Almuerzos de residentes (req. 14)

| Capa | Archivos | Líneas |
|---|---|---:|
| Backend | `residentes.py` (336), `schemas/residentes.py` (87), `api_residentes.py` (23) | 446 |
| Portal | `AlmuerzosResidentesView` (377), `ResultadoGeneracion` (137), `ListaResidentes` (92), `residentes.js` (110), `residentesService.js` (160) | 876 |

Parece una sola acción, pero tiene muchos casos borde:
- **Simulación previa:** muestra cuántos almuerzos se generarían por fecha y a quién se omitiría, antes de confirmar.
- **Exclusión** de residentes puntuales, con búsqueda en la lista.
- **No duplicar:**
  - omite a quien ya tiene el almuerzo de esa fecha en otro sector;
  - omite a quien ya lo tiene generado (la operación es idempotente).

  En ambos casos informa el motivo.
- **Mutex:** un `FOR UPDATE` sobre el sector Residentes evita que dos generaciones simultáneas creen pedidos duplicados.
- Límites validados en los dos extremos: rango de hasta 31 días y sin fechas pasadas.

### F. Exportaciones CSV (req. 15)

| Capa | Archivos | Líneas |
|---|---|---:|
| Backend | `exportacion.py` (151), `csv_export.py` (32), `formato=csv` en `api_comidas.py`, `schemas/entregas.py` y `entrega.py` | ~425 |
| Portal | `DialogoExportar` (276), `exportacion.js` (141), `exportacionService.js` (39) | 456 |

Son dos listados con formatos distintos:
- **Detalle por persona:** una fila por persona y día, con las 4 comidas y el menú, ordenada por apellido sin distinguir mayúsculas ni acentos.
- **Matriz por sector:** viandas por comida × menú, con totales.

El diálogo arranca con los filtros de la bandeja ya aplicados, los muestra, valida el rango (hasta 62 días) y nombra el archivo según el tipo y las fechas.

### G. Infraestructura compartida del portal

| Archivo | Líneas | Rol |
|---|---:|---|
| `services/autorizacionService.js` | 709 | Las 11 llamadas a la API de bandeja, detalle y totales, más la conversión y validación de cada respuesta |
| `routes.js` | 49 | Rutas del módulo con su permiso requerido |

### Totales por bloque

| Bloque | Backend | Portal | Total |
|---|---:|---:|---:|
| A. Consulta | ~440 | 1.615 | ~2.055 |
| B. Autorización | ~530 | 1.903 | ~2.433 |
| C. Totales | ~10 | 392 | ~402 |
| D. Cupos | ~5 | 1.080 | ~1.085 |
| E. Residentes | 446 | 876 | 1.322 |
| F. Exportaciones | ~425 | 456 | ~881 |
| G. Compartido | — | 758 | 758 |
| **Total** | **~1.870** | **7.080** | **~8.950** |

---

## 4. Endpoints del módulo

| Método | Ruta | Para qué |
|---|---|---|
| GET | `/api/authorizations` | Bandeja paginada con 5 filtros |
| GET | `/api/authorizations/week` | Vista semanal sector × día |
| GET | `/api/authorizations/{id}` | Detalle con acciones permitidas hoy |
| GET | `/api/authorizations/{id}/history` | Historial auditable |
| PUT | `/api/authorizations/{id}` | Autorizar o corregir (control de versión → 409) |
| POST | `/api/authorizations/{id}/reject` | Rechazar con motivo (control de versión → 409) |
| GET | `/api/authorization-dates` | Días con pedidos para el calendario |
| GET | `/api/authorization-dates/adjacent` | Anterior y siguiente fecha con pedidos |
| POST | `/api/resident-lunches` | Generar o simular almuerzos de residentes |
| GET | `/api/authorized-meals?formato=csv` | Exportación del detalle por persona |
| GET | `/api/summaries/authorizations` (también CSV) | Totales y exportación por sector |
| GET/POST/PUT | `/api/quotas` | Cupos (de la Etapa 1, consumidos desde el portal) |

Todos están documentados en `openapi.yaml`, y son 488 líneas de esa diferencia.

---

## 5. Por qué el portal pesa más que el backend

En un módulo así, la proporción de aproximadamente 4 a 1 entre portal y backend es esperable:

1. **Es un módulo de interfaz intensiva.** El backend resuelve cada regla una vez. La interfaz, en cambio, tiene que *mostrar* cada regla: botones habilitados o no con su motivo, estados parciales, totales en vivo, diálogos de confirmación, mensajes de error por campo, estados de carga y vacío.
2. **Cada archivo `.vue` incluye tres lenguajes.** De las 4.880 líneas en componentes:

   | Parte | Líneas | % |
   |---|---:|---:|
   | Plantilla HTML | ~1.680 | 34% |
   | Estilos | ~1.210 | 25% |
   | Lógica | ~1.990 | 41% |

   Los estilos son `scoped` por componente, sin bloques copiados entre archivos.
3. **Responsive y mobile first** (exigencia general): las matrices persona × comida y sector × día necesitan otra disposición en pantallas chicas.
4. **Accesibilidad:** 95 atributos `aria-*` y 18 `role`, `caption` en las tablas, foco gestionado tras las acciones, Escape para cerrar popovers y estado de los checkboxes de grupo. Nada de eso se ve, pero ocupa líneas.
5. **Lógica pura separada de los componentes.** Selección, totales, filtros, calendario, cupos, residentes y exportación están en módulos `.js` sin Vue (~1.030 líneas). Así se pueden testear sin montar componentes, y los componentes quedan más simples.

Como referencia, el módulo de carga de pedidos del mismo portal ocupa unas 2.200 líneas para **un** flujo principal, la carga de un pedido. Autorización cubre seis bloques funcionales distintos (A–F), más reglas de concurrencia y auditoría.

---

## 6. Los tests (4.335 líneas)

| Suite | Tests | Líneas |
|---|---:|---:|
| Backend, archivos nuevos (`pytest`) | 218 | ~2.400 |
| Backend, archivos ampliados (resúmenes, entregas, pedidos) | +~20 | ~520 |
| Portal (`vitest`), lógica pura | 158 | 1.415 |

Qué cubren, entre otras cosas:
- Cada 409: versión vieja, pedido rechazado por otro, día anterior, pedido con entregas.
- Cada 400 del motivo de rechazo y del rango de residentes y de exportación.
- Los permisos por rol (403).
- La idempotencia de la generación de residentes.
- El orden sin acentos del CSV.
- Que lo pedido no cambie al autorizar.

Todos pasan:
- `vitest`: 158/158 en el módulo.
- `pytest`: los 4 fallos que quedan son anteriores y ajenos al módulo (`resumen` y `home_controller`).

Estas líneas **no son código de la funcionalidad**. Son la evidencia de que las reglas de las Notas se cumplen. Restarlas de las 14.420 es la primera corrección que hay que hacerle al número.

---

## 7. Preguntas probables y cómo responderlas

**"14.000 líneas es muchísimo para un módulo."**
De esas 14.420, 5.474 (38%) son tests, contrato OpenAPI, datos de ejemplo y un lockfile generado. El código que corre en producción son ~8.950 líneas, para 16 requerimientos, 5 reglas de negocio y 3 ampliaciones de la API (sección 3).

**"¿Por qué el frontend es tan grande?"**
Por la sección 5: es la pantalla operativa del rol, los `.vue` incluyen HTML y CSS, y es responsive y accesible. Además hay 6 pantallas o flujos distintos: bandeja, detalle, cupos, residentes, exportación e historial.

**"¿No hay código repetido?"**
Las reglas están centralizadas:
- `acciones_permitidas()` es la única fuente de qué se puede hacer con un pedido;
- `version_de()` es la única forma de obtener la versión;
- `seleccion.js` y `totales.js` concentran la lógica de selección y totales.

Los componentes con nombres parecidos resuelven cosas distintas. Por ejemplo, `TablaPedidos` es la bandeja diaria, mientras que `TablaSemana` es la matriz sector × día. Y `TotalesPeriodo` (totales del período con agrupamientos) es otra cosa que `TotalesComparados` (pedido contra autorizado dentro del detalle).

**"¿Por qué el service del portal tiene 700 líneas?"**
Además de hacer 11 llamadas a la API, **valida y convierte cada respuesta en un solo lugar**. Las vistas reciben datos con la forma y los nombres del portal (`porMenu`, `autorizadas: null` si está pendiente…), y si el backend devuelve algo incoherente (por ejemplo, `total ≠ regular + excepción + generada`), el error aparece ahí y no como un número equivocado en pantalla. Es una decisión deliberada de robustez en un dominio donde un total mal mostrado se traduce en viandas de más o de menos.
*Si preguntan si se podría haber hecho más liviano:* sí. Es la parte que admite simplificación, unas 400 líneas. Conviene reconocerlo con naturalidad en vez de negarlo.

**"¿Por qué tanto control de concurrencia?"**
Porque la consigna lo pide explícitamente (Nota 5: responder 409 y recargar). El bloqueo de fila evita la condición de carrera entre la verificación de versión y la escritura, que la verificación sola no cubre.

**"¿Qué parte fue la más compleja?"**
La autorización masiva con totales en vivo y la corrección. La interfaz tiene que reflejar exactamente las mismas reglas que el backend aplica, y si hay discrepancia gana el backend con un 409.

---

## 8. Puntos honestos a tener presentes

Para no quedar expuesto si alguien revisa el código en detalle:
- **Capa de validación del service del portal:** ~400 líneas que se podrían aligerar (sección 7).
- **Comentarios:** ~750 líneas (aprox. 10% del portal). Documentan reglas y decisiones, pero algunos son redundantes.
- **Algunos cálculos se hacen en el cliente y también en el backend.** Por ejemplo, el desglose por día y menú del detalle. Parte es necesaria para mostrar totales en vivo antes de confirmar; otra parte se podría haber servido desde la API.

Aun sacando todo eso, el portal no bajaría de unas 6.000 líneas. El tamaño responde, en su gran mayoría, al alcance pedido.
