# Datos de seeding por slice — Módulo de Autorización

Datos de prueba que cargan los seeds en la branch `feature/autorizacion-pedidos` (commit `8e4bd56`, que sale de `development`). Este documento le dice a quien desarrolle cada slice del "Plan de desarrollo — Módulo de Autorización" qué datos tiene para la prueba manual desde el portal.

Fuente: `admin/src/core/seeds.py` (constantes `USERS`, `SECTORS`, `CUPOS`, `PERSONAL`, `PEDIDOS_DEMO` y la función `seed_pedidos_demo`).

## Cómo funcionan

- **Las fechas son relativas al día en que se corren los seeds.** En este documento `D` es ese día, `D-1` el anterior, `D+3` tres días después, y así. Ejemplo: si se corrieron el 19/11, `D-7` es el 12/11 y `D+8` es el 27/11.
- **Los pedidos de demo se cargan solo si la base no tiene ningún pedido.** Si se vuelven a correr los seeds sin resetear, no se duplican, pero las fechas siguen siendo las del día de la primera carga.
- **Los ids son estables:** después de `reset-db` + `seeds`, los pedidos van del 1 al 13 en el orden de la tabla general y el personal del 1 al 30.
- **Se crean con las funciones de dominio**, igual que lo haría la API: `create_pedido`, `autorizar_pedido`, `rechazar_pedido` y `create_entrega`. Por eso la condición regular o excepción sale de los cupos, y `revisado_por`, `autorizado_por` y `autorizado_en` quedan completos.
- **Todos los pedidos son de periodicidad diaria** (`fecha_desde == fecha_hasta`). Así siguen sirviendo cuando Carga de Pedidos pase a "un pedido por sector y fecha".
- **Los tests de pytest no usan estos datos.** Cada test arma sus propios fixtures.

Regenerar desde cero (dentro de `admin/`):

```
poetry run flask --app web reset-db
USER_NAME=admin@test.com USER_PASS=admin1234 poetry run flask --app web seeds
```

**Cuando una slice cambia el esquema**, por ejemplo con `Pedido.modificado_en`, la tabla `autorizacion_cambios` o el `CHECK` de `condicion`, hay que correr `reset-db` porque la app usa `create_all` y no agrega columnas nuevas a tablas existentes. Si el cambio afecta a los pedidos de demo, también hay que actualizar `seed_pedidos_demo`. Dos casos concretos:

- Los historiales de las autorizaciones y del rechazo de demo no se crean solos: hay que generarlos dentro de `autorizar_pedido` y `rechazar_pedido`, o en la función que los reemplace.
- `modificado_en` tiene que quedar asignado en los pedidos de demo.

## Usuarios

| Email | Contraseña | Rol | Sector | Para qué |
|---|---|---|---|---|
| `alimentacion@test.com` | `alim1234` | alimentacion | — | Usuario principal del módulo. Revisó todos los pedidos de demo |
| `alimentacion2@test.com` | `alim1234` | alimentacion | — | Segundo revisor: concurrencia y 409 por versión. Corrigió el pedido 12 en los seeds |
| `jefe.medicos@test.com` | `jefe1234` | jefe_servicio | Médicos Internos | Creador de los pedidos 1, 4 y 11 |
| `jefe.quirofano@test.com` | `jefe1234` | jefe_servicio | Quirófano | Creador de los pedidos 2, 5 y 9 |
| `jefe.intendencia@test.com` | `jefe1234` | jefe_servicio | Intendencia | Creador de los pedidos 6 y 10 |
| `jefe.hemoterapia@test.com` | `jefe1234` | jefe_servicio | Hemoterapia | Creador de los pedidos 3, 8 y 12. Ve el motivo del rechazo del pedido 8 |
| `jefe.alimentacion@test.com` | `jefe1234` | jefe_servicio | Alimentación | Creador de los pedidos 7 y 13 |
| `jefe.libre@test.com` | `jefe1234` | jefe_servicio | — | Jefe sin sector |
| `comedor@test.com` | `comedor1234` | comedor | — | Registró la entrega del pedido 7. Sirve para probar el 403 con otro rol |
| `bloqueado@test.com` | `bloqueado1234` | comedor (bloqueado) | — | Login bloqueado |
| `admin@test.com` | el de `USER_PASS` | administrador | — | No entra al portal (403 `role_not_allowed`). Usa la app privada |

## Sectores y cupos

| id | Sector | Activo | Cupos (desayuno / almuerzo / merienda / cena) |
|---|---|---|---|
| 1 | Médicos Internos | sí | 6 / 6 / 4 / 3 |
| 2 | Quirófano | sí | 3 / 3 / — / 40 |
| 3 | Intendencia | sí | 2 / 30 / — / — |
| 4 | Alimentación | sí | 50 / 100 / — / — |
| 5 | Hemoterapia | sí | — / 2 / 2 / — |
| 6 | Rayos | **no** | — |
| 7 | Residentes | sí | — (sin personal ni pedidos) |

Una comida sin cupo hace que todas las solicitudes de esa comida sean de excepción.

## Personal

Menús disponibles: General, Vegetariano y Celiaco. Los nombres tienen acentos (Álvarez, Ríos, Ibáñez, Suárez…), lo que sirve para probar el CSV.

| ids | Sector | DNI | Personas (menú) |
|---|---|---|---|
| 1, 4–8 | Residentes (sin sector) | 30111222, 33777888, 34100001–34100004 | Perez (G), Fernandez (V), Benítez (G), Ríos (C), Acosta (G), Suárez (V) |
| 9–16 | Médicos Internos | 27200001–27200008 | Álvarez (G), Castro (G), Domínguez (V), Giménez (G), Herrera (C), Ibáñez (G), Juárez (G), Ledesma (V) |
| 2, 17–20 | Quirófano | 28333444, 28300001–28300004 | Gomez (V), Molina (G), Navarro (G), Ortiz (C), Peralta (G) |
| 3, 21–23 | Intendencia | 25555666, 25400001–25400003 | Lopez (C), Quiroga (G), Romero (V), Sosa (G) |
| 24–27 | Hemoterapia | 29500001–29500004 | Torres (G), Vega (C), Villalba (G), Zapata (V) |
| 28–30 | Alimentación | 26600001–26600003 | Acuña (G), Bustos (G), Cabrera (C) |

## Pedidos (tabla general)

En "Excepciones" figuran los DNI de las solicitudes de excepción. "No aut." son las solicitudes con `autorizada = False`.

| id | Fecha | Sector | Estado | Solicitudes | Comidas | Excepciones | No aut. | Particularidad |
|---|---|---|---|---|---|---|---|---|
| 1 | D-7 | Médicos Internos | autorizado | 12 | desayuno, almuerzo | 0 | 0 | Pasado, todo autorizado |
| 2 | D-2 | Quirófano | autorizado | 10 | desayuno, almuerzo | 4 (28300003 y 28300004 en ambas) | 3 (las 2 del desayuno y 28300004 en el almuerzo) | Pasado, autorización parcial |
| 3 | D-1 | Hemoterapia | **confirmado** | 8 | almuerzo, merienda | 4 (29500003 y 29500004 en ambas) | — | Pendiente en un día pasado |
| 4 | D | Médicos Internos | confirmado | **29** | las cuatro | 10 | — | Matriz grande, con residentes: Perez (almuerzo) y Benítez (cena) |
| 5 | D | Quirófano | confirmado | 14 | desayuno, almuerzo, cena | 4 (28300003 y 28300004 en desayuno y almuerzo) | — | Residente Fernandez en la cena |
| 6 | D | Intendencia | autorizado | 8 | desayuno, almuerzo | 2 (25400002 y 25400003 en el desayuno) | 1 (25400003, desayuno) | Hoy, parcial, **sin entregas** |
| 7 | D | Alimentación | autorizado | 6 | desayuno, almuerzo | 0 | 0 | Hoy, **desayuno entregado** a 26600001 y 26600002 |
| 8 | D | Hemoterapia | **rechazado** | 5 | almuerzo | 3 | 5 (todas) | Motivo: "No se informó la guardia del día. Volver a cargar el pedido con el personal confirmado." Incluye al residente Acosta |
| 9 | D+1 | Quirófano | confirmado | 10 | desayuno, almuerzo | 4 | — | |
| 10 | D+1 | Intendencia | confirmado | 4 | almuerzo | 0 | — | Pedido chico, sin excepciones |
| 11 | D+3 | Médicos Internos | confirmado | 13 | almuerzo, cena | 4 (27200007 y 27200008 en el almuerzo; 27200004 y 34100004 en la cena) | — | Residente Suárez en la cena |
| 12 | D+3 | Hemoterapia | autorizado | 8 | almuerzo, merienda | 4 | 1 (29500003, almuerzo) | Futuro, parcial, sin entregas. **Corregido por `alimentacion2`**: autorizado con 29500003 y 29500004 sin almuerzo, después se autorizó el almuerzo de 29500004 |
| 13 | D+8 | Alimentación | confirmado | 10 | desayuno, almuerzo, cena | 4 (toda la cena: no hay cupo) | — | Semana siguiente. Residente Ríos en la cena |

Días **con** pedidos: D-7, D-2, D-1, D, D+1, D+3 y D+8. Días **sin** pedidos en el medio: D-6 a D-3, D+2 y D+4 a D+7.

Detalle de las excepciones del pedido 4. Los cupos de Médicos Internos son 6 / 6 / 4 / 3:

| Comida | Personas | Regulares | Excepciones |
|---|---|---|---|
| desayuno | los 8 de Médicos Internos | 6 | 27200007, 27200008 |
| almuerzo | los 8 + Perez | 6 | 27200007, 27200008, 30111222 |
| merienda | los primeros 6 | 4 | 27200005, 27200006 |
| cena | los primeros 5 + Benítez | 3 | 27200004, 27200005, 34100001 |

En total son 19 regulares y 10 excepciones.

---

## S1 — Bandeja diaria de pedidos

- **Bandeja de hoy (D):** tiene 5 pedidos, uno por sector salvo Rayos y Residentes, con los tres estados: 4 y 5 confirmados (pendientes, que tienen que verse resaltados), 6 y 7 autorizados, y 8 rechazado.
- **Navegación por día:**
  - D-1 muestra solo el pedido 3, pendiente.
  - D+1 muestra los pedidos 9 y 10.
  - D+2 está vacío: sirve para probar el estado vacío.
- **Filtro por sector:** Médicos Internos en D deja solo el pedido 4. En el selector, Rayos está inactivo y Residentes no tiene pedidos.
- **Filtro por estado en D:** `confirmado` → 4 y 5; `autorizado` → 6 y 7; `rechazado` → 8.
- **Filtro por condición en D:** `excepcion` → 4, 5, 6 y 8. Excluye al pedido 7, que tiene todo regular. `regular` → todos.
- **Pruebas de permisos:** con `jefe.quirofano@test.com` o `comedor@test.com` se prueba el 403 en `/api/authorizations`.

## S2 — Detalle del pedido en solo lectura

- **Matriz completa:** pedido 4. Tiene las cuatro comidas, 10 personas (8 fijas y 2 residentes), los tres menús y excepciones en todas las comidas. Los totales por comida y menú no son triviales.
- **"Lo autorizado aparte":**
  - Pedido 6: lo pedido es 8 y lo autorizado 7.
  - Pedido 2: lo pedido es 10 y lo autorizado 7.
  - Pedido 12: lo pedido es 8 y lo autorizado 7 (después de la corrección de los seeds).
- **Rechazado con motivo y revisor:** pedido 8.
- **Campo `entregada` por solicitud:** en el pedido 7, el desayuno de 26600001 y 26600002 está en `true` y el resto en `false`.
- **`acciones_permitidas`: un caso por combinación de estado y fecha.**

| Estado \ fecha | Pasada | Hoy | Futura |
|---|---|---|---|
| confirmado | 3 | 4, 5 | 9, 10, 11, 13 |
| autorizado sin entregas | 1, 2 | 6 | 12 |
| autorizado con entregas | — | 7 | — |
| rechazado | — | 8 | — |

## S3 — Autorizar solicitudes una por una

- **Pedidos para autorizar:**
  - El 5 (hoy, 14 solicitudes) y el 10 (D+1, 4 solicitudes) son los más cómodos para marcar a mano.
  - El 4 sirve para ver los totales en vivo con muchas filas.
- **Concurrencia (409 por versión):** abrir el pedido 9 o el 11 con `alimentacion@test.com` en una pestaña y con `alimentacion2@test.com` en otra, o el mismo usuario en dos pestañas. La segunda en confirmar recibe 409.
- **Día anterior (409):** el pedido 3 está confirmado en D-1.
- **Cobertura total (400):** mandar un PUT al pedido 10 con 3 de sus 4 solicitudes, o con un id de solicitud del pedido 9.
- **Totales pedidos sin cambios:** después de autorizar el 5, lo pedido tiene que seguir siendo 14.

## S4 — Atajos masivos y rechazo con motivo

- **"Solo regulares":** en el pedido 4 tiene que dejar 19 autorizadas y 10 sin autorizar (ver la tabla de excepciones de arriba). En el pedido 13, deja toda la cena sin autorizar.
- **Atajos "por comida" y "por persona":** usar el pedido 4. Las columnas tienen distinta cantidad de personas, y Perez y Benítez aparecen en una sola comida.
- **Rechazo exitoso:** pedidos 5, 9, 10, 11 o 13.
- **Rechazos que tienen que fallar:**
  - 409 por día anterior: pedido 3.
  - 409 porque ya está autorizado: pedidos 6 y 12. Desde S5 el mensaje sugiere corregir.
  - 409 porque ya está rechazado: pedido 8.
- **El Jefe ve el motivo:** entrar con `jefe.hemoterapia@test.com` y ver el pedido 8 (`GET /api/requests/8`). Para el pedido que se rechace en la prueba, entrar con el jefe de ese sector (ver la tabla de usuarios).

## S5 — Corrección y días anteriores

- **Corrección habilitada:**
  - Pedido 6: autorizado hoy, sin entregas. Se puede autorizar 25400003 en el desayuno.
  - Pedido 12: autorizado a futuro, con una corrección de `alimentacion2` desde los seeds (29500004, almuerzo: No autorizada → Autorizada). Queda 1 no autorizada (29500003, almuerzo).
- **Corrección sin cambios (400):** PUT al 12 con todas sus solicitudes, los mismos valores y su versión.
- **Corrección concurrente (409 por versión):** el 12 en dos pestañas (`alimentacion` y `alimentacion2`).
- **Reject sobre un autorizado (409):** 6 o 12, con "El pedido ya fue autorizado: para dejarlo sin efecto, corregí su autorización."
- **Corrección bloqueada por entregas (409):** pedido 7. Tiene el desayuno de 26600001 y 26600002 entregado (entrega del Comedor, `fecha = D`).
- **Solo lectura por fecha pasada:**
  - Pedidos 1 y 2, autorizados.
  - Pedido 3, que quedó pendiente: no se puede autorizar ni rechazar.
- **Historial** (`GET /api/authorizations/{id}/history` y el panel "Historial de cambios" del detalle): `reset-db` + `seeds` generan filas en `autorizacion_cambios` para los pedidos de demo revisados, porque los seeds usan `revisar_pedido` y `rechazar_pedido`: `tipo=autorizacion` para 1, 2, 6, 7 y 12, y `tipo=rechazo` para el 8 (una fila por solicitud, con `autorizada_antes` nulo). Además, el 12 tiene **una fila `tipo=correccion`** de `alimentacion2` (29500004, almuerzo, `false → true`), posterior a su autorización: su historial arranca con dos operaciones. El 3 (pendiente) y los confirmados devuelven `items: []`. Al corregir el 6 desde el portal se suman sus filas `tipo=correccion`.

## S6 — Calendario y vista semanal

- **Días con datos y conteos por estado:**

| Día | Pendientes | Autorizados | Rechazados |
|---|---|---|---|
| D-7 | 0 | 1 | 0 |
| D-2 | 0 | 1 | 0 |
| D-1 | 1 | 0 | 0 |
| D | 2 | 2 | 1 |
| D+1 | 2 | 0 | 0 |
| D+3 | 1 | 1 | 0 |
| D+8 | 1 | 0 | 0 |

- Verificado después de S5 (19/11/2026, `GET /api/authorization-dates?fecha_desde=D-7&fecha_hasta=D+8`): los conteos de la tabla coinciden. La corrección del 12 no cambia su estado.
- **"Anterior/siguiente con pedidos"** (`GET /api/authorization-dates/adjacent`, sin tope de distancia):
  - Desde D, la navegación hacia adelante recorre D+1 → D+3 → D+8 y saltea D+2 y D+4 a D+7. En D+8, "Siguiente con pedidos" queda deshabilitado.
  - Hacia atrás recorre D-1 → D-2 → D-7. En D-7, "Anterior con pedidos" queda deshabilitado.
  - **Con el filtro de sector Hemoterapia:** solo D-1, D y D+3 (pedidos 3, 8 y 12). Es el criterio "Listo cuando" de S6.
  - Con `estado=autorizado`: solo D-7, D-2, D y D+3.
- **Calendario** (`GET /api/authorization-dates`): en el mes de D quedan habilitados solo esos 7 días. D lleva los tres puntos (pendiente, autorizado y rechazado) y "2 pendientes, 2 autorizados, 1 rechazado"; los días con pendientes se resaltan.
- **Vista semanal** (`GET /api/authorizations/week`, semana lunes–domingo):
  - La semana de D muestra 5 sectores con estados mezclados.
  - En la semana de D+8 aparece un solo pedido: el 13 (Alimentación, pendiente, 10 pedidas).
  - Desde la semana de D, "Semana siguiente con pedidos" va a la de D+8 y "Semana anterior con pedidos" a la de D-7 (si D-2 cae en la misma semana que D, no hay otra entre medio).
- **Rango inválido (400):** pedir más de 62 días, o una `fecha_hasta` anterior a la `fecha_desde`, en `/authorization-dates`. Sin `fecha`, `/authorizations/week` da 400 (no 404).
- **Permiso nuevo `autorizacion_dates`** (rol `alimentacion` y `administrador`): `seed_auth` no agrega permisos a un rol que ya existe, así que en una base existente hay que correr `reset-db` + `seeds` (o asignarlo desde Roles y permisos). Sin él, el calendario y la navegación dan 403.

## S7 — Filtros completos y totales

- **Filtro por comida:**
  - `merienda` → pedidos 3, 4 y 12.
  - `cena` → pedidos 4, 5, 11 y 13.
- **Filtro por menú:** todos los pedidos tienen solicitudes de al menos dos menús, y Celiaco aparece en los 13. Por eso el filtro por menú casi no descarta pedidos, pero sí cambia la cantidad de solicitudes que se cuentan. Para un filtro por menú que descarte pedidos, hay que cargar uno nuevo con un solo menú.
- **Agrupamiento por condición:** los pedidos 4, 11 y 13 tienen mezcla de regulares y excepciones. Todavía no hay solicitudes `generada`: aparecen después de S9.
- **Pedidos vs. autorizados por separado:** sirven los pedidos 2, 6 y 12 (autorizados parciales) y el 8 (rechazado: 5 pedidas y 0 autorizadas).
- **Totales del período:** el rango D-7 a D+8 cubre los 13 pedidos y 137 solicitudes.
- **Backend:** `GET /api/summaries/authorizations` ya existía (lo hizo Reportes): S7 no lo modifica. El panel lo llama con los filtros de la bandeja y, sin filtro de estado, una segunda vez con `estado=confirmado` para los pendientes.
- **Semántica:** con filtros de comida, menú o condición, la bandeja muestra los pedidos que tienen **alguna** solicitud que los cumple (cada fila con el pedido completo) y el panel cuenta **solo las solicitudes** que los cumplen. Sin esos filtros, el Total del panel coincide con la suma de la columna Total de la bandeja.
- **Números reales del panel (agrupado por comida; Pedidas / Pendientes / Autorizadas / No autorizadas):**

  | Período | Desayuno | Almuerzo | Merienda | Cena | Total |
  |---|---|---|---|---|---|
  | D | 20 / 13 / 6 / 1 | 26 / 14 / 7 / 5 | 6 / 6 / 0 / 0 | 10 / 10 / 0 / 0 | **62 / 43 / 13 / 6** |
  | Semana de D (lunes a domingo) | 30 / 18 / 9 / 3 | 56 / 35 / 14 / 7 | 14 / 10 / 4 / 0 | 15 / 15 / 0 / 0 | **115 / 78 / 27 / 10** |
  | D-7 a D+8 | 39 / 21 / 15 / 3 | 65 / 38 / 20 / 7 | 14 / 10 / 4 / 0 | 19 / 19 / 0 / 0 | **137 / 88 / 39 / 10** |

  En D: 62 = 14 + 29 + 6 + 8 + 5 (pedidos 5, 4, 7, 6 y 8); las 6 no autorizadas son el desayuno no autorizado del pedido 6 más las 5 del rechazado (8). Con D = jueves, la semana va de D-3 a D+3, y por día da D-2: 10, D-1: 8, D: 62, D+1: 14, D+3: 21. Los números de la semana dependen del día de la semana de D.

## S8 — Gestión de cupos

- **Cupos existentes:** 13, listados en la tabla de sectores y cupos.
- **409 por duplicado:** crear Quirófano/desayuno, que ya existe.
- **400 por sector inactivo:** cualquier cupo para Rayos.
- **400 por cantidad ≤ 0:** cualquier sector.
- **Alta válida:** Hemoterapia/desayuno, Intendencia/cena o Alimentación/merienda no existen.
- **"Un cupo modificado no cambia la condición de pedidos ya cargados":**
  1. Bajar Quirófano/desayuno de 3 a 1.
  2. Comprobar que los pedidos 5 y 9 siguen con 3 regulares y 2 excepciones en el desayuno.
  3. Para ver el efecto, un pedido nuevo de Quirófano para D+2 (cargado por `jefe.quirofano@test.com`) tiene que tener 1 regular.
- **Entrega registrada:** solo la del pedido 7, para comprobar que el PUT ya no da 400 por "cantidad menor a lo entregado". Alimentación no tiene cupos de merienda ni cena.

Resultados de la prueba de S8 (20/11/2026, base temporal con los seeds del 20/11):

- **El backend ya cumplía la regla de Etapa 2:** `update_cupo` nunca tuvo el 400 por lo entregado. S8 solo agregó tests y la descripción en `openapi.yaml`.
- **Criterio "Listo cuando":** con Quirófano/desayuno bajado de 3 a 1 desde `/cupos`, los pedidos 5 y 9 siguen con **3 regulares y 2 excepciones** en el desayuno (base y `GET /api/authorizations/{id}`). Un pedido nuevo de Quirófano para D+2 con 5 desayunos (ids de personal 2 y 17–20, cargado por `jefe.quirofano@test.com`) quedó con **1 regular y 4 excepciones**.
- **Pedido 7:** bajar Alimentación/desayuno de 50 a 1 (debajo de las 2 entregas) responde 200.
- **Desactivar un sector para ver la fila "Inactivo":** la app privada no deja desactivar Hemoterapia (ni ningún sector de los seeds con jefe) porque tiene un Jefe de Servicio activo. Hay que bloquear o reasignar al jefe antes, o usar Rayos con un cupo cargado a mano.
- **La prueba cambia cupos de la base de demo** (pasos 5 a 13 del plan: Hemoterapia/desayuno, Quirófano/merienda y cena, Intendencia/desayuno y cena, Alimentación/desayuno). Para volver a los valores de la tabla: `reset-db` + `seeds` (ver arriba), o editarlos a mano desde `/cupos`.

## S9 — Almuerzos de residentes

- **Sector Residentes:** existe (id 7), está activo y no tiene pedidos ni cupos. Para probar el 400 "sin sector Residentes" hay que borrarlo o renombrarlo desde la app privada, o usar el fixture de test.
- **Residentes activos:** 6 (ids 1 y 4 a 8).
- **Quién ya tiene almuerzo en otro sector y tiene que aparecer en "omitidos":**
  - Perez (30111222) en D, por el pedido 4.
  - Nadie más tiene almuerzo cargado.
- **Casos que no son omisiones:**
  - Acosta (34100003) tiene almuerzo en D, pero dentro del pedido 8, que está **rechazado**. Tiene que generarse igual.
  - Benítez (D), Fernandez (D), Suárez (D+3) y Ríos (D+8) solo tienen **cena** en otros sectores. No bloquean el almuerzo.
- **Rango sugerido:** D a D+3. Se generan 23 almuerzos (6 residentes × 4 días, menos Perez en D) y queda 1 omitido.
  - Excluir a Ríos baja el resultado a 19 generados (se pierden sus 4 días).
  - Correrlo dos veces tiene que agregar solo los faltantes.
- **Fechas pasadas (400):** cualquier rango que empiece en D-1 o antes.
- **Conflicto con Carga (409):** después de generar, `jefe.medicos@test.com` intenta cargar el almuerzo de un residente para una fecha ya generada.
- **Cambio de esquema y permiso nuevo:** S9 agrega `generada` al `CHECK` de `condicion` y el permiso `almuerzo_residentes_new` (rol `alimentacion`). En una base anterior hay que correr `reset-db` + `seeds`: sin el reset, el `CHECK` viejo rechaza las solicitudes generadas, y sin el permiso, la generación da 403.
- **Los seeds no generan almuerzos de residentes:** la generación se prueba desde la página `/residentes` (o `POST /api/resident-lunches`), para que parta de cero.

Resultados de la prueba de S9 (20/11/2026, base temporal con los seeds del 20/11, D = 20/11):

- **Vista previa D a D+3:** 23 almuerzos (D 5, D+1 6, D+2 6, D+3 6) y 1 omitido: Perez en D, "Ya tiene almuerzo en Médicos Internos (pedido 4)". Acosta no figura (el pedido 8 está rechazado).
- **Excluyendo a Ríos:** 19. Al generar se crean los pedidos de Residentes **14 (D), 15 (D+1), 16 (D+2) y 17 (D+3)**, autorizados.
- **Segunda corrida sin excluir:** vista previa 4 generados y 20 omitidos (19 "Ya generado" + Perez). Genera 4 en los mismos pedidos 14 a 17.
- **Totales** (`/api/summaries/authorizations`, D a D+3, `agrupar=condicion`): fila `generada` con 23 pedidas y 23 autorizadas; `autorizadas_generadas = 23` y `autorizadas_regulares` no las incluye. Con `agrupar=sector`, Residentes = 23.
- **Corregir el pedido 14** desautorizando a Acosta y volver a generar D: 0 generados, Acosta como "Ya generado", el pedido no cambia.
- **Carga:** con D+2 generado, `jefe.medicos@test.com` cargando el almuerzo de Fernandez → 409; la cena → 201.

## S10 — Exportaciones CSV

- **Permiso nuevo `autorizacion_export`** (roles `alimentacion` y `administrador`): sin él, `formato=csv` en `GET /api/authorized-meals` da 403. En una base anterior hay que correr `reset-db` + `seeds` (o asignarlo desde Roles y permisos). No cambia el esquema.
- **Rango sugerido:** D-7 a D+8, que cubre los 13 pedidos. Las solicitudes autorizadas salen de los pedidos 1, 2, 6, 7 y 12.
- **CSV de detalle por persona:**
  - D-7 a D+8, sin residentes generados: **21 filas** (una por fecha + persona + sector).
  - En D salen las personas de los pedidos 6 y 7 (7 filas), con desayuno y almuerzo.
  - 25400003 (Sosa) aparece sin desayuno, porque no lo autorizaron.
- **CSV de totales por sector, D-7 a D+8, sin residentes generados:** Alimentación 6, Hemoterapia 7, Intendencia 7, Médicos Internos 12, Quirófano 7; Total 39. Solo D: Alimentación 6, Intendencia 7; Total 13.
- **Con residentes generados para D y D+1** (11 almuerzos): la semana de D suma la fila Residentes con 11 (General 5, Vegetariano 4, Celiaco 2) y `condicion=generada` de D a D+1 da 11 filas.
- **Acentos y BOM:** los apellidos Álvarez, Domínguez, Giménez, Ibáñez, Juárez, Ríos y Suárez tienen que verse bien en Excel o LibreOffice.
- **Filtros:**
  - Sector Intendencia en D → solo el pedido 6.
  - Menú Celiaco → Lopez (25555666) y Cabrera (26600003) en D.
- **CSV de totales:** se puede cruzar con el panel de S7 para el mismo rango.

## S11 — Cierre y recorrido guionado

Recorrido completo con los datos de demo:

1. Con `jefe.quirofano@test.com`, cargar un pedido para D+2. Ese día está vacío en los seeds.
2. Con `alimentacion@test.com`, autorizar ese pedido en forma parcial con "solo regulares".
3. Corregir el pedido 6 (Intendencia, D).
4. Rechazar el pedido 10 (Intendencia, D+1) con motivo, y verlo con `jefe.intendencia@test.com`.
5. Generar los almuerzos de residentes para D a D+3.
6. Exportar los dos CSV del rango D-7 a D+8.

Para la prueba responsive, el pedido 4 (10 personas × 4 comidas) es el caso más exigente de la matriz.
