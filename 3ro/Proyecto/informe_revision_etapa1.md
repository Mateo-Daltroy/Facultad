# Informe de revisión — Etapa 1 (entrega 16/10/2026)

Revisión de `code/admin/` contra `Etapa1_Trabajo Integrador_2026.pdf`.
Rama revisada: `feat/crud-personal` (HEAD `7eb8b0b`). Fecha: 15/10/2026.

Leyenda: ✅ cumple · ⚠️ cumple parcialmente / con defectos · ❌ no está implementado

---

## 0. Resumen ejecutivo

**Lo más urgente para mañana (ordenado por gravedad):**

1. **Se perdieron rutas de usuarios en un merge.** El merge `39ed716` ("resolver conflicto de merge en archivos de gestión de usuarios") borró de `usuarios.py` las rutas `/usuarios/<id>/rol` (asignar rol) y `/usuarios/<id>/bloquear`. Por eso **fallan 8 tests** de `test_usuarios_controller.py` (devuelven 404). La plantilla `usuarios/rol.html` sigue apuntando a `usuarios.update_role`, que ya no existe. Las versiones originales están en el commit `bafcfff`.
2. **Alta de usuario Jefe de Servicio sin sector.** `usuarios.create` valida que se elija un sector pero **no se lo pasa** a `auth.create_user(...)` (`src/web/controlers/usuarios.py`, llamada a `create_user`), así que el usuario queda sin sector.
3. **Se puede bloquear a un Administrador.** `cambiar_estado` invierte `active` sin pasar por `validators.validate_block`, y en el form de edición también se puede poner "Inactivo" a un admin. El enunciado lo prohíbe explícitamente.
4. **No existe el borrado de usuarios** (la "D" del CRUD). `user_destroy` se usa para el bloqueo.
5. **Faltan módulos enteros:** Ajustes del sistema (sección 8) y Ver perfil. Los links "Tipos de menú", "Parámetros generales" y "Ver perfil" apuntan a `#`.
6. **Faltan endpoints de la API:** `/api/configuration`, `/api/authorizations*`, `/api/authorized-meals*`, `/api/deliveries*`, `/api/summaries/*`. Tampoco hay modelo de Entregas.
7. **`seed_pedidos` nunca hace `commit`**: los pedidos de prueba no se guardan.

Tests: `356 passed, 8 failed` (todos los fallos vienen del punto 1).

---

## 1. Layout (sección 1)

| Requisito | Estado | Detalle |
|---|---|---|
| Plantilla base Jinja extendida por todas las vistas | ✅ | `templates/layout.html` |
| Menú lateral con enlaces a los módulos | ⚠️ | Usuarios, Roles, Sectores y Personal funcionan. **Tipos de menú** y **Parámetros generales** son `href="#"` |
| Indicar el módulo activo | ⚠️ | Funciona en usuarios, roles y sectores; **falta en "Personal"** (el `<a>` no tiene el `{% if ... %}activo{% endif %}`) |
| Barra superior: nombre/logo de la app | ⚠️ | Dice "Grupo 24", no el nombre de la aplicación |
| Nombre y apellido del usuario autenticado | ✅ | Sale de la sesión |
| Menú de cuenta: Ver perfil | ❌ | `href="#"`, no existe la vista |
| Menú de cuenta: Cerrar sesión | ✅ | POST a `auth.logout` |
| Mecanismo común de mensajes flash | ✅ | `_flashes.html` con categorías |

---

## 2. Módulo de usuarios (sección 2)

| Requisito | Estado | Detalle |
|---|---|---|
| Crear | ⚠️ | Ver bugs más abajo |
| Listar / consultar | ⚠️ | Hay listado, pero **no hay vista de detalle** (`user_show`) |
| Actualizar | ⚠️ | Ver bugs |
| **Eliminar** | ❌ | No hay ruta. Además falta la regla "no eliminar si tiene pedidos/operaciones → bloquear" |
| Email único | ⚠️ | Se valida, pero en la edición el email **no se pasa a minúsculas** (en el alta sí). Si se edita a `Foo@x.com`, `find_user_by_email` busca `foo@x.com` y **ese usuario no puede volver a loguearse** |
| Campos mínimos (email, nombre, apellido, DNI, contraseña, activo, rol, sector, fechas) | ✅ | Modelo `User` + `BaseModel` (created_at / updated_at) |
| Jefe de Servicio con un único sector | ❌ en el alta | El `sector_id` no se guarda al crear (bug #2 del resumen) |
| Otros roles sin sector | ⚠️ | Se limpia en la edición, pero en el alta no aplica porque nunca se guarda sector |
| Cambiar el rol de un Jefe → quitar sector | ✅ | En `update` |
| No asociar a sector inactivo | ⚠️ | El `<select>` solo muestra activos, pero **el servidor no lo verifica** en create/update (sí lo hacía `assign_role`, que quedó sin ruta) |
| No desactivar un sector con usuarios activos | ❌ | Ver sección 5 |
| Búsqueda: email, nombre/apellido, DNI, activo, rol, sector, combinables | ✅ | |
| Ordenar por fecha de creación, apellido y nombre, email (asc/desc) | ⚠️ | Funciona, pero los links de ordenamiento **pierden los filtros `role_id` y `sector_id`** |
| Paginación en servidor, máx. 25 | ✅ | |
| Edición: contraseña opcional, se mantiene si va vacía, se hashea | ✅ | |
| Validación en cliente | ⚠️ | Hay `required`, `type=email`, `pattern` en DNI y `minlength=6`. Falta `maxlength`/formato en nombre y apellido, y que el sector sea obligatorio cuando el rol es Jefe |
| **Validación en servidor** (requeridos, formato y longitud, unicidad) | ❌ | Solo se valida la confirmación de contraseña, el sector del Jefe y el email único. **No** se validan requeridos, formato de email, DNI numérico, longitudes ni largo mínimo de contraseña. `role_id` vacío → `create_user(role=None)` → **500 por IntegrityError**. El enunciado pide además que estas validaciones estén **en un módulo aparte** (requisitos técnicos) |
| Contraseña nunca en formularios/listados/log/API | ✅ | `usuario_schema` no la expone |
| Solo el Administrador accede | ✅ | Por permisos `user_*` |

**Otros puntos de código:**
- `core/auth/__init__.py` y `core/auth/user.py` tienen **dos `list_users` distintas**. El controlador usa la de `user.py`; la otra quedó muerta.
- `usuarios/rol.html` tiene un `<script>` inline (va contra la regla del grupo de no usar scripts inline) y apunta a una ruta inexistente.

---

## 3. Roles, permisos y bloqueo (sección 3)

| Requisito | Estado | Detalle |
|---|---|---|
| Un único rol por usuario | ✅ | `User.role_id` |
| 4 roles en la BD | ✅ | Seeds |
| Tablas `roles`, `permissions`, `role_permissions` | ✅ | |
| Permisos `modulo_accion` | ✅ | |
| **Asignar/desasignar rol** (Administrador) | ⚠️ | Se puede cambiar el rol desde el form de edición, pero la ruta dedicada `/usuarios/<id>/rol`, que aplicaba `validate_role_assignment` (no cambiar el propio rol, sector activo, etc.), **se perdió en el merge** |
| Usuario bloqueado no puede loguearse | ✅ | Web y API |
| **Admins no pueden ser bloqueados** | ❌ | `cambiar_estado` y `update` no lo verifican (bug #3 del resumen) |
| Bloqueo con permiso propio | ⚠️ | Existe el permiso `user_block`, pero el toggle usa `user_destroy` |
| Permisos de ejemplo `user_index/new/update/destroy/show` | ⚠️ | Falta **`user_show`** en los seeds (tampoco hay vista) |
| Solo el Admin gestiona roles y permisos | ✅ | `role_index` / `role_update` son exclusivos |

---

## 4. Login y sesiones (sección 4)

| Requisito | Estado | Detalle |
|---|---|---|
| Login con email y contraseña | ✅ | |
| Hash seguro (bcrypt) | ✅ | |
| Sesión con Flask-Session del lado del servidor | ✅ | `SESSION_TYPE='cachelib'` (FileSystemCache) |
| Sin flask-login | ✅ | Implementación manual |
| Cookies seguras con expiración | ✅ | `HTTPONLY`, `SAMESITE=Lax`, 1 h de vida, `SECURE` en producción |
| Login obligatorio en toda la app privada | ✅ | `before_request` global |
| Verificación de permisos por módulo | ✅ | `permission_required` en cada ruta. `has_permission` además corta a los usuarios bloqueados con la sesión abierta |
| Logout | ✅ | |

**Observaciones:**
- `SECRET_KEY` tiene como valor por defecto `'mysecretkey'`. Además firma los JWT de la API (PyJWT avisa en los tests que la clave es corta). Hay que asegurarse de definirla en el servidor de la cátedra.
- Los `register_error_handler` de 401/403/404/500 se registran **dos veces** en `web/__init__.py`. No rompe nada (gana el segundo), pero el primer bloque sobra.
- Las sesiones se guardan en `src/flask_session/` o en `admin/flask_session/`, según desde dónde se lance la app, porque `cache_dir='flask_session'` es una ruta relativa al directorio actual. Git las ignora, pero conviene usar una ruta absoluta, por ejemplo con `BASE_DIR`.

---

## 5. Gestión de sectores (sección 5)

| Requisito | Estado | Detalle |
|---|---|---|
| CRUD | ✅ | La baja es lógica (`deleted=True`) |
| Nombre único | ⚠️ | La comparación **distingue mayúsculas** ("Urgencias" y "urgencias" pueden coexistir) |
| Campos (nombre, descripción, activo, fechas) | ✅ | Aunque hay `created_at` (de BaseModel) y además `inserted_at`; conviene unificar |
| **Asociar Jefes de Servicio a un sector desde este módulo** | ❌ | Solo se hace desde usuarios. No hay selección de usuarios en el form de sector, así que tampoco se valida en servidor "existencia de los usuarios", "que tengan rol Jefe" ni "que no estén en otro sector" |
| Búsqueda por nombre y estado | ✅ | |
| **Búsqueda por Jefe de Servicio asignado** | ❌ | `list_sectors(manager_user_id=...)` existe en el core, pero el controlador no lo usa y la vista no tiene filtro |
| Ordenar por nombre / fecha de creación (asc/desc) | ✅ | |
| Paginación máx. 25 | ✅ | |
| Validación en cliente (requeridos, longitud) | ✅ | `required`, `maxlength` |
| **No eliminar un sector con personal, usuarios o pedidos → desactivar** | ❌ | `delete_sector` hace la baja sin chequear nada |
| **No desactivar un sector con usuarios activos** | ❌ | `update_sector` no lo verifica |
| Sector inactivo no usable en nuevas asignaciones | ⚠️ | Cupos y pedidos lo validan. **Personal no** (ver sección 6). Usuarios solo lo filtra en el `<select>` |
| Solo el Administrador | ✅ | |

**Código:** `controlers/sector.py` conserva ~40 líneas comentadas de un "esqueleto de autenticación" que ya no sirve. También hay dos rutas `POST /sectores/<id>` (update y delete); Flask toma la primera, así que un POST a esa URL siempre actualiza. Funciona porque el form de borrado usa `/eliminar`, pero confunde.

---

## 6. Gestión del personal (sección 6)

Es el módulo más completo y prolijo.

| Requisito | Estado | Detalle |
|---|---|---|
| CRUD + campos mínimos | ✅ | |
| DNI único y con formato | ✅ | Se normaliza a 7 u 8 dígitos |
| Residente sin sector / Fijo con sector | ✅ | `validate_sector_for_tipo` |
| Búsqueda por DNI, nombre/apellido, sector, tipo y estado | ✅ | |
| Ordenar por apellido y nombre, DNI, fecha de creación | ✅ | |
| Paginación máx. 25 | ✅ | |
| Importación CSV con reporte (procesados, importados, rechazados y motivo) | ✅ | Con descarga de las filas rechazadas |
| Validación en cliente | ✅ | |
| Validación en servidor: existencia de los sectores | ⚠️ | `get_sector_by_id` solo descarta los eliminados: **se puede asignar un sector inactivo** con un POST armado a mano o por la API. El CSV sí filtra los activos |
| **No eliminar si tiene pedidos o entregas → marcar inactiva** | ❌ | `delete_personal` tiene un `TODO`. Ahora que existe `Solicitud` ya se puede implementar: si hay solicitudes, `activo=False` en lugar de `deleted=True` |
| Persona inactiva no se suma a pedidos | ✅ | `validate_personas` |
| Tipo de menú copiado al detalle del pedido | ✅ | `Solicitud.tipo_menu_id` |
| Permisos: Admin, Jefe y Alimentación | ✅ | Comedor solo lectura |
| Tipos de personal y de menú en tablas independientes con FK | ✅ | `tipos_personal`, `tipos_menu` |

---

## 7. Cupos (sección 7)

| Requisito | Estado | Detalle |
|---|---|---|
| CRUD (web y API) | ✅ | |
| Campos (sector, tipo de comida, cantidad, fechas) | ✅ | |
| Cantidad > 0, sector activo, sin duplicados sector+comida | ✅ | En servidor. `validate_sector_cupo` no mira `deleted`, pero un sector eliminado no aparece en el `<select>` |
| Búsqueda por sector y tipo de comida, ordenar por sector, paginación | ✅ | |
| Cálculo automático Regular/Excepción | ✅ | `build_solicitudes` |
| Modificar el cupo no altera pedidos anteriores | ✅ | La condición se guarda en cada solicitud |
| Permisos: Alimentación gestiona por API; Jefe solo lee el suyo | ✅ | `sector_scope` |
| `PUT /api/quotas`: 400 si la nueva cantidad es menor a lo ya entregado | ❌ | No hay entregas todavía |

---

## 8. Ajustes del sistema (sección 8)

| Requisito | Estado |
|---|---|
| Cantidad de elementos por página (que respeten **todos** los listados) | ❌ — hoy está fijo en 25 en cada módulo |
| Deshabilitar el sitio por mantenimiento | ❌ |
| Mensaje de mantenimiento | ❌ |
| Solo el Administrador lo modifica | ❌ |

Falta el módulo entero: modelo, vista, permisos y su uso en `paginate`/`before_request`.

---

## 9. API (sección 9)

Infraestructura: ✅ JWT Bearer, 401 antes que cualquier otra regla, 403 por permiso dentro del endpoint, errores JSON uniformes `{error: {code, message, fields}}`, CORS, validación con Marshmallow y OpenAPI en `docs/openapi.yaml`.

| Endpoint | Estado | Notas |
|---|---|---|
| `POST /api/auth/login`, `POST /api/auth/logout`, `GET /api/auth/me` | ✅ | |
| `GET /api/sectors`, `GET /api/sectors/{id}` | ✅ | Jefe → 403 (no tiene `sector_index`) |
| `GET /api/menu-types`, `GET /api/personnel-types` | ✅ | |
| `GET /api/configuration` | ❌ | Depende de la sección 8 |
| `GET/POST /api/personnel`, `GET/PUT/DELETE /api/personnel/{id}` | ⚠️ | Bien en general. El DELETE hereda el TODO de la sección 6 (no pasa a inactiva si tiene historial) |
| `GET /api/sectors/{id}/personnel` | ⚠️ | No acepta el parámetro `fecha` |
| `GET/POST /api/requests`, `GET/PUT/DELETE /api/requests/{id}` | ✅ | El sector sale del usuario. Hay 409 por duplicado y por estado |
| `GET /api/authorizations`, `GET/PUT /api/authorizations/{id}`, `POST .../reject` | ❌ | El modelo ya tiene `autorizada`, `autorizado_por`, `motivo_rechazo`, etc., pero no hay lógica ni endpoints |
| `GET/PUT/POST /api/quotas`, `GET /api/quotas/{id}` | ✅ | |
| `GET /api/authorized-meals`, `GET /api/authorized-meals/{person_id}` | ❌ | |
| `GET/POST /api/deliveries`, `GET /api/deliveries/{id}`, `GET /api/deliveries/summary` | ❌ | **No existe modelo de Entrega** |
| `GET /api/summaries/requests`, `/authorizations`, `/deliveries` | ❌ | |

> El enunciado dice que los endpoints son "una guía" y que lo no terminado en la etapa 1 puede completarse en la etapa 2. Aun así, los de autorizaciones, entregas y resúmenes forman parte del "circuito completo" que pide el alcance de la etapa 1. Si no llegan, conviene avisarle al ayudante antes de la defensa.

---

## 10. Modelo de datos y reglas generales

| Requisito | Estado | Detalle |
|---|---|---|
| Pedidos nominados, separados por comida | ✅ | `Pedido` → `Solicitud` (persona, fecha, comida, menú, condición) |
| Pedido / autorizado / entregado diferenciados | ⚠️ | Pedido y autorizado sí (campos separados en `Solicitud`). **Entregado no existe** |
| No duplicar solicitud persona+fecha+comida | ⚠️ | Solo se valida en la aplicación (`validate_solicitudes_duplicadas`). No hay `UniqueConstraint` en la BD, así que dos requests concurrentes podrían duplicarla |
| Fecha, hora y usuario de las operaciones relevantes | ⚠️ | `creado_por`, `revisado_por`, `autorizado_por` y timestamps sí. **No hay tabla de historial/auditoría de operaciones** |
| PostgreSQL + SQLAlchemy | ✅ | Desarrollo local con SQLite (`.env`), producción con `DATABASE_URL` (psycopg 3) |

---

## 11. Requisitos técnicos y pautas

| Requisito | Estado | Detalle |
|---|---|---|
| Python / Flask / Jinja / SQLAlchemy | ✅ | |
| MVC por capas | ✅ | `core/` (modelo), `web/controlers`, `templates` |
| Docstrings en todo el código Python | ⚠️ | Muy despareja: `usuarios.py`, `auth.py` (controlador), `cupo.py` (core) y `handlers/error.py` casi no tienen |
| Validaciones de servidor en un módulo aparte | ⚠️ | Personal, sectores, cupos y pedidos validan en `core/`, y la API en `schemas/`. **Usuarios valida inline en el controlador** |
| bcrypt | ✅ | |
| HTML5 semántico | ✅ | `aside`, `nav`, `header`, `main`, `article`. Hay bastantes `style=""` inline |
| **Versionado semántico (etapa 1 → v1.x.x)** | ❌ | Solo existen los tags `v0.1.0` y `v0.2.0`. **Hay que crear `v1.0.0`** al entregar |
| **Manejo de logs** | ❌ | No hay `logging` ni `app.logger` en todo `src/`. El enunciado pide un registro básico de eventos y errores |
| Dependencias controladas (Poetry) | ✅ | `pyproject.toml` + `poetry.lock`. Ojo: pide `requires-python >=3.12` y el servidor usa 3.14.4 (compatible) |
| Responsive (<767, 768–1024, >1025) | ✅ | Hay `@media` para móvil y tablet en `styles.css` |
| Font Awesome desde CDN | ✅ | Permitido |

---

## 12. Seeds

- `seed_pedidos` **no hace `db.session.commit()`** al final: los 3 pedidos de prueba no se guardan (flask-sqlalchemy-lite no hace commit automático).
- Aunque se guardaran, esos pedidos **no tienen solicitudes**, así que no sirven para probar el detalle ni los totales.
- Los sectores de ejemplo (`buffet`, `administracion`, ...) no salen de la lista del enunciado (Médicos Internos, Quirófano, Intendencia, ...). El enunciado pide "instanciar alguno o la totalidad de los mismos".
- Falta el permiso `user_show`, y no hay permisos para tipos de menú ni configuración.
- Usuario de prueba `js@js.com` con contraseña `1234` y rol `comedor` aunque se llama "jefe". Sirve, pero no es un Jefe de Servicio listo para probar.

---

## 13. Lista de acciones sugeridas (por prioridad)

**Antes de entregar (bloqueantes o de poco esfuerzo):**
1. Recuperar `update_role` y `bloquear` desde `bafcfff` en `usuarios.py`, y que vuelvan a pasar los 8 tests.
2. Pasar `sector_id=sector_id` a `auth.create_user` en `usuarios.create`.
3. En `cambiar_estado` y en `update`, usar `validate_block` para no bloquear administradores.
4. Pasar el email a `.strip().lower()` en `usuarios.update`.
5. Validar en servidor los campos de usuario (requeridos, formato, longitud, rol existente) en un módulo aparte, por ejemplo `core/auth/validators.py`.
6. Agregar `db.session.commit()` en `seed_pedidos`.
7. Marcar como activo el link "Personal" en el layout.
8. Crear el tag `v1.0.0`.
9. Agregar logging básico (`logging.basicConfig` + `app.logger` en los handlers de error 500 y en login/logout).

**Si hay tiempo:**
10. Implementar la eliminación de usuarios, con la regla de historial → bloqueo.
11. `delete_personal`: si tiene solicitudes, pasarlo a inactivo en lugar de borrarlo. Validar que el sector esté activo.
12. Sectores: no eliminar si tiene personal, usuarios o pedidos; no desactivar si tiene usuarios activos; filtro por Jefe; asignar Jefes desde el form.
13. Módulo de Ajustes del sistema + `GET /api/configuration`.
14. Vista "Ver perfil".

**Etapa 2 (avisar al ayudante):**
15. Autorizaciones, comidas autorizadas, entregas (modelo + API), resúmenes, historial de operaciones y `UniqueConstraint` en `Solicitud`.
