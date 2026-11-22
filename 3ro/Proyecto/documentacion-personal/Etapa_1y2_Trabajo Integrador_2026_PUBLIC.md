# Sistema para la gestión del servicio de alimentación

## Trabajo integrador Cursada 2026

FECHA DE ENTREGA 16/10/2026

**Destinatario:** Hospital Interzonal de Agudos Especializado en Pediatría "Sor María Ludovica".  Servicio de Alimentación y Dietética

# Contexto

El Servicio de Alimentación y Dietética del Hospital Interzonal de Agudos Especializado en Pediatría **“Sor María Ludovica”** tiene a su cargo la gestión de las comidas destinadas al personal autorizado de los diferentes sectores/ servicios del hospital.

Para organizar esta actividad es necesario coordinar tres instancias de un mismo circuito:

* La declaración de las personas o cantidades de viandas requeridas por cada servicio/sector.  
* La revisión y autorización de esos pedidos por parte del Servicio de Alimentación.  
* La entrega física de las comidas y el registro de quién las retira.

Actualmente, estas actividades se realizan mediante planillas en papel, listados impresos, comunicaciones telefónicas y controles manuales. Cada sector informa sus necesidades, el Servicio de Alimentación revisa y ajusta las cantidades solicitadas, y el personal del comedor verifica posteriormente las autorizaciones al momento de efectuar la entrega.

La falta de integración entre estas etapas provoca diferentes dificultades operativas y administrativas:

* Pedidos duplicados o superiores a las necesidades reales.  
* Inconsistencias entre las cantidades solicitadas, autorizadas y entregadas.  
* Cambios de personal o de guardias que no son comunicados oportunamente.  
* Necesidad de realizar verificaciones y llamados entre los distintos sectores.  
* Falta de trazabilidad sobre quién solicitó, autorizó y retiró cada comida.  
* Dificultades para controlar los diferentes tipos de menú.  
* Escasa disponibilidad de información consolidada para auditoría, planificación y control del gasto.  
* Desperdicio de alimentos como consecuencia de sobrepedidos o entregas no registradas.

Deberá permitir gestionar de manera independiente el **desayuno**, **almuerzo, merienda** y la **cena**, así como distinguir diferentes tipos de menú, a saber:

* General.  
* Vegetariano.  
* Celíaco.

Para resolver esta problemática se propone desarrollar una aplicación web que digitalice el circuito completo de solicitud, autorización y entrega de comidas. El sistema será utilizado por tres actores principales:

* Los **Jefes de Servicio**, responsables de declarar las necesidades de alimentación de su sector.  
* El **Servicio de Alimentación**, encargado de revisar, ajustar y autorizar los pedidos.  
* El **Sector Comedor**, responsable de verificar las autorizaciones y entregar las comidas.

Cada actor dispondrá de una vista específica dentro del sistema, de acuerdo con sus responsabilidades y permisos. Sin embargo, todos trabajarán sobre información integrada: los pedidos cargados por los Jefes de Servicio serán revisados por el Servicio de Alimentación, y únicamente las cantidades autorizadas estarán disponibles para su entrega en el comedor.

La solución deberá mantener diferenciadas las cantidades **pedidas**, **autorizadas** y **entregadas**, registrar el historial de las operaciones realizadas y permitir conocer en todo momento el estado de cada solicitud. De esta manera, se buscará mejorar la coordinación entre las áreas, reducir errores y desperdicios, y garantizar la trazabilidad integral del proceso.

# Funcionalidades de la aplicación (se dividirá en dos etapas)

El desarrollo se organizará en dos etapas. La primera comprenderá la aplicación privada de administración, el modelo de datos y la API del sistema. La segunda comprenderá la aplicación web utilizada por los tres roles operativos —Jefe de Servicio, Servicio de Alimentación y Sector Comedor— e implementará el circuito de solicitud, autorización y entrega de comidas.

A continuación se describen, en líneas generales, las principales funcionalidades esperadas para cada actor.

### **Jefe de Servicio**

El Jefe de Servicio será responsable de registrar y consultar los pedidos de comidas correspondientes a su sector.

El sistema deberá permitirle:

* Consultar  e incorporar el personal asociado a su sector y eventuales personas de tipo Residente a los pedidos de su sector.  
* Registrar pedidos nominados de comidas, identificando a cada persona mediante nombre, apellido y DNI.  
* Gestionar de manera independiente las solicitudes de desayuno, almuerzo, merienda y cena.  
* Utilizar automáticamente el tipo de menú asociado a cada persona.  
* Realizar cargas con periodicidad diaria, semanal o mensual.  
* Visualizar los cupos correspondientes a su sector para cada tipo de comida.  
* Identificar las solicitudes que quedan comprendidas dentro del cupo Regular y aquellas que correspondan a Excepción.  
* Consultar y modificar los pedidos de su sector mientras éstos no hayan sido autorizados o rechazados por el Servicio de Alimentación.  
* Consultar pedidos actuales e históricos.

Cuando un sector tenga más de un Jefe de Servicio, todos ellos podrán consultar y gestionar los pedidos correspondientes a dicho sector.

### **Servicio de Alimentación**

El Servicio de Alimentación será responsable de revisar los pedidos realizados por los distintos sectores y determinar qué solicitudes quedan autorizadas.

El sistema deberá permitirle:

* Consultar los pedidos realizados por todos los sectores.  
* Visualizar las solicitudes correspondientes a desayuno, almuerzo, merienda y cena de manera independiente.  
* Identificar claramente las solicitudes clasificadas como Regulares y aquellas realizadas por Excepción.  
* Autorizar o no autorizar de manera nominada las solicitudes realizadas.  
* Realizar autorizaciones individuales o masivas para agilizar la operatoria.  
* Rechazar completamente un pedido cuando corresponda.  
* Mantener diferenciada la información originalmente solicitada de la posteriormente autorizada.  
* Administrar los cupos correspondientes a cada sector y tipo de comida.  
* Consultar pedidos y autorizaciones actuales e históricas.  
* Obtener totales y resúmenes por sector, fecha, tipo de comida y tipo de menú.  
* Exportar listados y consultar información consolidada para control y planificación.

Las cantidades autorizadas deberán obtenerse a partir de las solicitudes nominadas efectivamente autorizadas y no deberán sobrescribir la información del pedido original.

### **Sector Comedor**

El Sector Comedor será responsable de consultar las comidas autorizadas y registrar las entregas realizadas.

El sistema deberá permitirle:

* Buscar personas mediante DNI, nombre o apellido.  
* Consultar las comidas autorizadas para cada persona en una fecha determinada.  
* Identificar de manera independiente si una persona tiene autorizado desayuno, almuerzo, merienda o cena.  
* Visualizar el tipo de menú correspondiente.  
* Verificar si una comida autorizada ya fue entregada.  
* Registrar la entrega de una o varias comidas en una misma operación.  
* Permitir que una persona retire comidas correspondientes a otras personas autorizadas.  
* Registrar quién fue el responsable efectivo del retiro.  
* Impedir que una misma comida autorizada sea entregada más de una vez.  
* Consultar las entregas realizadas durante el día.  
* Visualizar totales autorizados y entregados.

### **Requerimientos funcionales generales**

Independientemente del actor que utilice cada funcionalidad, el sistema deberá:

* Gestionar usuarios, roles y permisos de acceso.  
* Mantener un registro de los sectores o servicios del hospital y sus responsables.  
* Mantener un registro del personal que podrá ser incluido en los pedidos de comidas.  
* Permitir la carga manual y la importación masiva de personal.  
* Contemplar los distintos tipos de personal y tipos de menú definidos para el sistema.  
* Mantener todos los pedidos de comida de forma nominada.  
* Gestionar desayuno, almuerzo, merienda y cena de manera independiente.  
* Mantener diferenciadas las solicitudes realizadas, las autorizaciones otorgadas y las entregas efectivamente registradas.  
* Mantener la condición Regular o Excepción asignada a cada solicitud durante el posterior circuito de autorización y entrega.  
* Registrar fecha, hora y usuario responsable de las operaciones relevantes.  
* Conservar la información histórica necesaria para garantizar la trazabilidad del circuito.  
* Proporcionar resúmenes, estadísticas e historiales por período, sector, tipo de comida y tipo de menú.  
* Implementar controles de acceso y validaciones tanto en la interfaz como en el backend.  
* Proporcionar interfaces adaptadas a las funciones y permisos correspondientes a cada tipo de usuario.

La distribución detallada de estas funcionalidades entre la primera y la segunda etapa se especificará posteriormente.

## Componentes de la aplicación

* **Backend en Python y Flask:** implementará la lógica de negocio, las validaciones del lado del servidor, la autenticación, el control de roles y permisos, y el acceso a la información almacenada. También será responsable de aplicar las reglas correspondientes a la carga, autorización y entrega de comidas.  
* **Base de datos PostgreSQL:** almacenará la información de usuarios, roles, permisos, sectores, personal, solicitudes, detalles de pedidos, cupos, autorizaciones, entregas e historial de operaciones. Para la interacción con la base de datos se utilizará SQLAlchemy como ORM.  
* **API para consultas e integración:** el backend desarrollado en Flask expondrá los endpoints necesarios para que la aplicación frontend pueda consultar y modificar la información del sistema. La comunicación se realizará mediante solicitudes HTTP y el intercambio de datos en formato JSON.  
* **Aplicación frontend en Vue.js 3:** se desarrollará una aplicación web que consumirá la API implementada en Flask. La aplicación contará con diferentes vistas y funcionalidades según el rol del usuario autenticado:  
  * Jefe de Servicio.  
  * Servicio de Alimentación.  
  * Sector Comedor.  
* **Interfaz responsive y mobile first:** las pantallas deberán adaptarse a computadoras de escritorio, notebooks, tablets y teléfonos móviles. La vista utilizada por el Sector Comedor deberá estar especialmente optimizada para tablets y ofrecer una modalidad de interacción tipo kiosco, con controles de tamaño adecuado y teclado numérico en pantalla para realizar búsquedas por DNI.

# **Etapa 1**

**ENTREGA 16/10/2026**

## **Alcance : Aplicación Privada y API**

Durante la primera etapa se desarrollará la **aplicación privada de administración**, el modelo de datos y la **API** necesaria para implementar el circuito completo de solicitud, autorización y entrega de comidas.

La aplicación privada estará destinada a usuarios con rol **Administrador**. Permitirá gestionar la información estructural y de configuración utilizada por las diferentes áreas del sistema, incluyendo usuarios, roles, sectores, personal, tipos de menú y otros parámetros generales.

El Administrador tendrá acceso completo a los módulos de la aplicación privada y será responsable de:

* Gestionar usuarios.  
* Asignar roles a los usuarios.  
* Activar o bloquear cuentas.  
* Gestionar sectores o servicios.  
* Asignar responsables a los sectores.  
* Gestionar el personal del hospital.  
* Administrar tipos de personal.  
* Administrar tipos de menú de comidas.  
* Configurar otros parámetros generales requeridos por el sistema.

La aplicación se desarrollará utilizando **Python y Flask**, con **Jinja** como motor de plantillas, **PostgreSQL** como base de datos y **SQLAlchemy** como ORM.

Además de la aplicación privada, durante esta etapa se implementará una **API** que exponga las operaciones necesarias para las interfaces operativas que se desarrollarán en Vue.js durante la segunda etapa.

## **Roles del sistema**

Quedarían definidos cuatro roles:

* **Administrador:** gestiona la configuración general, los usuarios, sectores, personal y parámetros del sistema.  
* **Jefe de Servicio:** registra y consulta los pedidos correspondientes al personal de su sector.  
* **Servicio de Alimentación:** revisa, ajusta, autoriza o rechaza pedidos y administra los cupos.  
* **Sector Comedor:** consulta las autorizaciones vigentes y registra las entregas realizadas.

El Administrador podrá crear usuarios y asignar cualquiera de los tres roles operativos. También podrá crear otros usuarios administradores.

1. ## **Layout**

### **Descripción**

Implementar un **layout base** para todas las vistas de la aplicación privada de administración. Este layout funcionará como contenedor principal y deberá garantizar una estructura visual uniforme, permitiendo que cada módulo reemplace únicamente el contenido central sin modificar los elementos generales de navegación.

El layout deberá implementarse mediante una plantilla base de **Jinja**, que será extendida por las diferentes vistas de la aplicación Flask. De esta manera, los elementos comunes —encabezado, navegación, mensajes y datos de la sesión— deberán definirse una sola vez.

![][image1]

\* El layout es solo un ejemplo, carece de opciones relevantes. Cada grupo es libre de la implementación de su interfaz.

### **Requerimientos funcionales**

* Implementar un menú de navegación lateral o superior con enlaces a los módulos de la aplicación privada:  
  * Gestión de usuarios.  
  * Gestión de sectores o servicios.  
  * Gestión de personal.  
  * Gestión de tipos de menú.  
  * Parámetros generales del sistema, en caso de incorporarse.  
* El menú deberá indicar visualmente cuál es el módulo o sección activa.  
* Implementar una barra superior que incluya:  
  * Nombre o logo de la aplicación.  
  * Nombre y apellido del usuario autenticado.  
  * Menú de cuenta con las opciones:  
    * Ver perfil.  
    * Cerrar sesión.  
* Incorporar un área central destinada al contenido particular de cada módulo.  
* Incorporar un mecanismo común para mostrar mensajes de información, confirmación, advertencia o error generados por Flask.

2. ## **Módulo de usuarios**

### **Descripción**

Implementar el módulo de usuarios de la aplicación privada, accesible únicamente para usuarios con rol **Administrador**. Este módulo permitirá crear, consultar, actualizar y eliminar usuarios del sistema, asignarles un rol y, cuando corresponda, asociarlos a un sector del hospital.

### **Requerimientos funcionales**

* Operaciones CRUD de usuarios.  
* Validar que no existan dos usuarios con el mismo correo electrónico.  
* Campos mínimos para cada usuario:  
  * Correo electrónico.  
  * Nombre.  
  * Apellido.  
  * DNI  
  * Contraseña.  
  * Activo: Sí \| No.  
  * Rol:  
    * Administrador.  
    * Jefe de Servicio.  
    * Servicio de Alimentación.  
    * Sector Comedor.  
  * ~~Sector, obligatorio únicamente para usuarios con rol Jefe de Servicio.~~  
  * Fecha de creación.  
  * Fecha de última modificación.  
* Reglas para la asignación de sectores:  
  * ~~Un usuario con rol Jefe de Servicio deberá estar asociado a un único sector.~~  
  * Un sector podrá tener uno o más usuarios con rol Jefe de Servicio.  
  * Todos los Jefes asociados a un mismo sector podrán cargar y consultar pedidos correspondientes a dicho sector.  
  * Los usuarios con rol Administrador, Servicio de Alimentación o Sector Comedor no tendrán un sector asociado.  
  * Si se modifica el rol de un Jefe de Servicio, deberá eliminarse su asociación con el sector.  
  * No se podrá asociar un usuario a un sector inactivo.  
  * No se podrá desactivar un sector con usuarios activos.  
* Funcionalidad de búsqueda por:  
  * Correo electrónico.  
  * Nombre o apellido.  
  * DNI  
  * Activo: Sí \| No.  
  * Rol.  
  * Sector o servicio.  
* Los filtros deberán poder combinarse entre sí.  
* Resultados ordenables por:  
  * Fecha de creación.  
  * Apellido y nombre.  
  * Correo electrónico.  
* El ordenamiento deberá permitir sentido ascendente y descendente.  
* Paginación del lado del servidor, con un máximo de **25 registros por página**.  
* En la edición de un usuario:  
  * La contraseña no será obligatoria.  
  * Si el campo de contraseña queda vacío, deberá conservarse la contraseña actual.  
  * Si se ingresa una nueva contraseña, deberá almacenarse utilizando un hash seguro.  
* No se podrá eliminar un usuario que tenga pedidos, autorizaciones, entregas u otras operaciones asociadas. En esos casos deberá desactivarse mediante la funcionalidad de bloqueo definida en el módulo siguiente.

### **Notas**

* Validar en el cliente:  
  * Campos requeridos.  
  * Formato de correo electrónico.  
  * Formato y longitud de nombre y apellido.  
  * Longitud mínima y confirmación de contraseña.  
  * Selección obligatoria de un sector cuando el rol sea Jefe de Servicio.  
* Validar en el servidor:  
  * Campos requeridos.  
  * Formato y longitud de los campos.  
  * Unicidad del correo electrónico.  
* Las contraseñas deberán almacenarse mediante un algoritmo seguro de hash y nunca podrán mostrarse en formularios, listados, registros de log o respuestas de la API.  
* Se deberá mostrar un feedback claro ante errores de validación y ante el resultado de las operaciones realizadas.

### **Permisos**

* Solamente los usuarios con rol **Administrador** podrán acceder a este módulo.

3. ## **Gestión de Roles, Permisos y Bloqueo de Usuarios**

### **Descripción**

Implementar las funcionalidades de asignación de roles, control de permisos y bloqueo o desbloqueo de usuarios.

Cada usuario tendrá asignado **un único rol**, que determinará las funcionalidades a las que puede acceder. Los permisos deberán utilizarse para controlar las acciones específicas que cada rol puede realizar, tanto en la aplicación privada desarrollada con Flask y Jinja como en los endpoints de la API.

Los roles definidos para el sistema serán:

* Administrador.  
* Jefe de Servicio.  
* Servicio de Alimentación.  
* Sector Comedor.

### Requerimientos funcionales

* Asignar o desasignar roles a un usuario (Administrador).  
* Bloquear/desbloquear usuarios:  
  * Un usuario bloqueado no puede iniciar sesión.  
  * Los usuarios con rol Administrador no pueden ser bloqueados.  
* Implementar permisos siguiendo el patrón modulo\_accion. Ejemplos para este módulo:  
  * user\_index → listar usuarios  
  * user\_new → crear usuario  
  * user\_update → actualizar usuario  
  * user\_destroy → eliminar usuario  
  * user\_show → ver detalle de usuario

### Notas

* Los permisos y roles pueden administrarse directamente desde la base de datos en esta etapa del proyecto. Es decir, no hace falta hacer una interfaz gráfica para agregar permisos y roles, pero deberán existir en la base de datos.  
* **Es importante entender el porqué del uso de esta solución para implementar la autorización y seguir el esquema de forma correcta. Este tema será explicado oportunamente en los horarios de práctica.**

![][image2]

Figura 2\. Posible esquema para el manejo de usuarios.

### Permisos

* Todas las operaciones de roles y permisos sólo pueden ser realizadas por un usuario con rol **Administrador**.

4. ## **Login y manejo de sesiones**

Descripción

Implementar el sistema de autenticación y manejo de sesiones para la aplicación privada (Administración).

Requerimientos funcionales

* Formulario de login para la aplicación privada que permita iniciar sesión con email y contraseña.  
* Validación de credenciales contra la base de datos.  
* Manejo seguro de contraseñas (hash).  
* Creación y manejo de sesiones para mantener el estado del usuario autenticado.  
* Verificación de sesión y permisos en cada módulo.  
* Implementar cierre de sesión.

Notas

* **No se permite el uso de librerías externas que abstraigan el proceso completo de login (por ejemplo, flask-login)**. La implementación debe ser manual para comprender y controlar cada paso del flujo de autenticación.  
* Implementar verificación de permisos antes de acceder a funcionalidades restringidas. Esta verificación se debe realizar para todas las secciones del sistema de Administración.  
* Las cookies de sesión deben ser seguras y tener fecha de expiración.  
* **Deben utilizar** la librería **Flask-Session** para almacenar las sesiones del lado del servidor.  
* La autenticación en la aplicación privada será obligatoria para acceder cualquier parte de la aplicación de administración.

5. ## **Gestión de Sectores**

### **Descripción**

Implementar el módulo de sectores del hospital. Este módulo permitirá crear, consultar, actualizar y eliminar los sectores a los que pertenecen el personal y los Jefes de Servicio.

### **Requerimientos funcionales**

* Operaciones CRUD de sectores.  
* Validar que no existan dos sectores con el mismo nombre.  
* Campos mínimos para cada sector:  
  * Nombre.  
  * Descripción.  
  * Activo: Sí \| No.  
  * Fecha de creación.  
  * Fecha de última modificación.  
* Permitir asociar uno o más usuarios con rol **Jefe de Servicio** a un sector.  
* Cada Jefe de Servicio podrá estar asociado a un único sector.  
* Un sector podrá tener varios Jefes de Servicio.  
* Los Jefes de Servicio asociados a un mismo sector podrán cargar y consultar pedidos de ese sector.  
* Funcionalidad de búsqueda por:  
  * Nombre.  
  * Estado: Activo \| Inactivo.  
  * Jefe de Servicio asignado.  
* Resultados ordenables por:  
  * Nombre.  
  * Fecha de creación.  
* El ordenamiento deberá permitir sentido ascendente y descendente.  
* Paginación del lado del servidor, con un máximo de **25 registros por página**.

### **Notas**

* Validar en el cliente:  
  * Campos requeridos.  
  * Formato y longitud de los campos.  
* Validar en el servidor:  
  * Campos requeridos.  
  * Unicidad del nombre.  
  * Existencia de los usuarios seleccionados.  
  * Que los usuarios asignados tengan rol Jefe de Servicio.  
  * Que un Jefe de Servicio no se encuentre asociado a otro sector.  
* No se podrá eliminar un sector que tenga personal, usuarios o pedidos asociados. En esos casos deberá desactivarse.  
* Un sector inactivo no podrá utilizarse para nuevas asignaciones de personal ni para nuevos pedidos.  
* La desactivación de un sector no deberá eliminar sus registros históricos.

### **Permisos**

* Solamente los usuarios con rol **Administrador** podrán acceder a este módulo.


6. ## **Gestión del Personal del Hospital**

### **Descripción**

Implementar el módulo de personal del hospital. Este módulo permitirá registrar, consultar, actualizar e importar las personas que podrán ser incluidas en los pedidos nominados de comidas.

Importante : El personal registrado no necesariamente tendrá una cuenta para acceder al sistema. Los **usuarios** representan a quienes utilizan la aplicación, mientras que el **personal** representa a las personas para las que se solicitan y entregan comidas. Una excepción es el caso de los Jefes de Servicio que son usuarios registrados y a la vez pueden ser personal autorizado para las comidas.

El sistema deberá contemplar que algunas personas, particularmente los **residentes**, pueden rotar entre distintos sectores del hospital por lo tanto no tendrán sector asignado y podrá incorporarse a pedidos de cualquier sector.

### **Requerimientos funcionales**

* Operaciones CRUD de personal.  
* Campos mínimos para cada persona:  
  * DNI.  
  * Nombre.  
  * Apellido.  
  * Tipo de personal. \[\*1\]  
  * Sector (No aplica para residentes) \[\*2\]  
  * Menu\[\*3\]  
  * Activo: Sí \| No.  
  * Fecha de creación.  
  * Fecha de última modificación.  
* Validar que no existan dos personas con el mismo DNI .  
* El personal fijo está asociado a un único sector a la vez. Para cambiar de sector alcanza con reemplazar ese valor;  
* El personal de tipo Residente no tiene sector asignado obligatoriamente y puede incorporarse al pedido nominado de cualquier sector  
* Para incorporar una persona de tipo Personal fijo a un pedido, su sector debe coincidir con el sector del Jefe de Servicio que realiza la carga. Los Residentes pueden incorporarse a un pedido de cualquier sector sin esa validación  
* Permitir la búsqueda por:  
  * DNI.  
  * Nombre o apellido.  
  * Sector.  
  * Tipo de personal.  
  * Estado.  
* Resultados ordenables por:  
  * Apellido y nombre.  
  * DNI.  
  * Fecha de creación.  
* Paginación del lado del servidor, con un máximo de 25 registros por página.  
* Permitir la importación masiva de personal mediante un archivo CSV. Para esta funcionalidad se espera que se defina un formato de CSV, luego el usuario administrador cargue el personal en él y cuando se importe desde el sistema se añadan el personal nominado en él. Es importante validar cada registro/persona e informar el error en caso de suceder. 

### **Notas**

* Validar en el cliente y en el servidor:  
  * Campos requeridos.  
  * Formato de DNI.  
  * Unicidad de DNI   
  * Existencia del tipo de personal.  
  * Existencia de los sectores asignados.  
* No se podrá eliminar una persona que tenga pedidos o entregas asociados. En esos casos deberá marcarse como inactiva.  
* Una persona inactiva no podrá incorporarse a nuevos pedidos, pero deberá mantenerse visible en los registros históricos.  
* El cambio de sector de una persona no deberá modificar pedidos, autorizaciones o entregas registrados anteriormente.  
* Cada persona tendrá asociado un tipo de menú predeterminado en su ficha de Personal. Al incorporarla a un pedido, el sistema utilizará automáticamente dicho tipo de menú y lo almacenará en el detalle del pedido, sin necesidad que el Jefe de servicio deba asignarlo manualmente. Posteriores cambios en la ficha de la persona no deben modificar pedidos históricos.  
* La importación masiva deberá informar los registros procesados, importados y rechazados, indicando el motivo de cada error.

### **Permisos**

* Solamente los usuarios con rol **Administrador, Jefe de Servicio y Servicio de Alimentación** podrán gestionar el personal y sus asignaciones desde la aplicación.

\[\*1\] Sobre el Tipo de Personal:  
**Tipos de personal**

Los tipos de personal serán datos predefinidos, por ejemplo:

* Residente.  
* Personal Fijo.

Deberán almacenarse obligatoriamente en una tabla independiente de la base de datos y relacionarse con el personal mediante una clave foránea, garantizando la integridad referencial. Los valores podrán cargarse mediante migraciones, scripts de inicialización o datos precargados. Opcionalmente, el grupo podrá implementar un CRUD en la aplicación privada para que el Administrador gestione los tipos de personal.

\[\*2\] **Sobre los Sectores/Servicios**

Los sectores/servicios existentes son los siguientes. Instanciar a modo de ejemplo alguno o la totalidad de los mismos. A saber:

Medicos Internos, Medicos De Guardia (Urg)., Quirofano , Equipo De Guardia , Intendencia , Policia , Mantenimiento , Electricidad, Conmutador, Admision De Camas, Prep De Materiales, Laboratorio De Guardia, Economia Y Gestion , Hemoterapia , Secretarias De Guardia Estadistica, Rayos, Esterilizacion, Ssp, Mensajeria , Ate, Limpieza , Cefli , Lavadero, Salud Mental , Quirofano Cardiovascular , Alimentacion, Direccion, Deposito Central, Alegria Intensiva, Estadistica, Neurocirugia, Hermanas, Residentes Jefes, Niño Sano 63 Y 16, Genetica Idip

\[\*3\] Sobre el Tipo de Menu:

### **Tipos de menú**

Los tipos de menú serán datos predefinidos:

* General.  
* Vegetariano.  
* Celiaco.

Deberán almacenarse obligatoriamente en una tabla independiente de la base de datos y relacionarse con el Personal y, según el modelo adoptado, con la información histórica correspondiente a las solicitudes de comidas. Los valores podrán cargarse mediante migraciones, scripts de inicialización o datos precargados. Opcionalmente, el grupo podrá implementar un CRUD en la aplicación privada para que el Administrador gestione los tipos de menú.

7. ## **Cupos**

   

**Descripción**

Implementar el módulo de cupos por sector. Este módulo permitirá definir, para cada sector y tipo de comida (desayuno, almuerzo, merienda y cena), la cantidad máxima de personas que podrán incorporarse como solicitudes regulares.

Todos los pedidos de comidas serán nominados, por lo que cada comida solicitada deberá estar asociada a una persona identificada.

El cupo no representa una modalidad diferente de pedido, sino que se utilizará como límite para determinar si cada persona incorporada a un pedido corresponde a una **solicitud regular o a una solicitud por excepción**.

**Requerimientos funcionales** 

* Operaciones CRUD de cupos.  
* Campos mínimos para cada cupo:  
  * Sector.  
  * Tipo de comida:  
    * Desayuno.  
    * Almuerzo.  
    * Merienda.  
    * Cena.  
  * Cantidad de comensales.  
  * Fecha de creación.  
  * Fecha de última modificación.  
* El cupo será independiente del tipo de menú.  
* Para cada fecha y tipo de comida, las personas incorporadas al pedido hasta alcanzar el cupo disponible serán consideradas **regulares**.  
* Las personas incorporadas una vez alcanzado el cupo serán consideradas **excepciones**.  
* El sistema deberá identificar automáticamente esta condición durante la carga del pedido.  
* No se podrá crear un cupo para un sector inactivo.  
* No podrán coexistir dos cupos vigentes para el mismo sector y el mismo tipo de comida   
* Funcionalidad de búsqueda por:  
  * Sector.  
  * Tipo de comida.  
* Resultados ordenables por:  
  * Sector.

Paginación del lado del servidor, con un máximo de 25 registros por página.

### **Notas**

* Validar en el cliente y en el servidor:  
  * Campos requeridos.  
  * Que la cantidad sea mayor que cero.  
  * Existencia y estado activo del sector.  
* La modificación de un cupo no deberá alterar retroactivamente pedidos que ya se encuentren confirmados, autorizados o entregados.  
* El tipo de menú no modifica el consumo del cupo. Cada persona ocupa una unidad del cupo independientemente de que corresponda menú General, Vegetariano o Celíaco.

### **Permisos**

Los cupos serán administrados por usuarios con rol **Servicio de Alimentación** mediante la API. El Jefe de Servicio podrá consultar los correspondientes a su sector, pero no modificarlos.

**Sobre los pedidos de comida**

Los pedidos deberán permitir representar de manera independiente las solicitudes correspondientes a **desayuno, almuerzo, merienda y cena** para cada **sector** y **fecha**. Todas las solicitudes serán nominadas, por lo que cada comida solicitada deberá quedar asociada a una persona identificada.

Para cada solicitud deberá poder determinarse, como mínimo, el sector, la fecha, el tipo de comida, la persona, el tipo de menú y su condición **Regular** o **Excepción**.

La organización interna de esta información y el modelo de datos utilizado quedarán a criterio de cada grupo, siempre que permitan cumplir las reglas de negocio y mantener la trazabilidad requerida.

La estructura de la API podrá adaptarse al modelo de datos y a las decisiones de diseño adoptadas por cada grupo. Recuerden que los endpoints propuestos en este documento deberán considerarse como una guía y podrán ajustarse, agruparse o complementarse cuando resulte necesario, siempre que se mantengan las funcionalidades, reglas de negocio y restricciones de acceso definidas.

8. ## **Ajustes del sistema**

Este módulo permitirá administrar la configuración del sistema, como mínimo deberá contemplar la siguiente configuración:

* Cantidad de elementos por página en los listados del sistema (todos los listados deberán respetar este valor para el paginado).  
* Posibilidad de deshabilitar el sitio para mantenimiento  
* Mensaje de mantenimiento de sitio

La configuración del sistema sólo podrá modificarla un usuario con el rol de Administrador.

9. ## **API**

### **Descripción**

Implementar una API que permita acceder a las funcionalidades necesarias para las tres interfaces que se desarrollarán en Vue.js durante la segunda etapa:

* Jefe de Servicio.  
* Servicio de Alimentación.  
* Sector Comedor.

NOTAS:

- La siguiente lista es una guía pero que el/la estudiante pueda modificar lo definido o agregar en caso que se considere, según lo requiera la solución implementada.  
- Todos los endpoints deben manejar los errores siguiendo el estándar HTTP, devolviendo el código de estado que corresponda a cada situación (por ejemplo 400 para datos inválidos, 401 para falta de autenticación o token inválido, 403 para falta de permisos, 404 para recursos inexistentes, etc) junto con el detalle del error en el cuerpo de la respuesta.  
- Salvo el login, todos los endpoints deben estar protegidos con autenticación: la ausencia o invalidez del token debe rechazarse con 401 antes de evaluar cualquier otra regla de negocio, y los permisos según el rol del usuario autenticado deben verificarse en el propio endpoint, no solo ocultarse en la interfaz. 

### **Autenticación**

* `POST /api/auth/login` — iniciar sesión.  
* `POST /api/auth/logout` — cerrar sesión.  
* `GET /api/auth/me` — obtener los datos del usuario autenticado.  
- **Rol(es)**: cualquier usuario autenticado de las tres interfaces operativas.  
- **Entrada**: token de acceso en el header Authorization (Bearer).  
- **Salida**: id, nombre, apellido, email, rol, sector asociado (si corresponde).  
- **Reglas** de negocio: validar que el token sea válido y no haya expirado.  
- **Errores**: 401 si falta el token, es inválido o expiró.

### **Sectores y datos generales**

* #### `GET /api/sectors — listar sectores.`  

- **Rol(es)**: Servicio de Alimentación, Sector Comedor. El Jefe de Servicio no lo necesita: opera únicamente sobre su propio sector, ya disponible mediante \`GET /api/auth/me\`.    
- **Entrada**: parámetro de query opcional \`estado\` (\`activo\` por defecto; \`todos\` para incluir inactivos, necesario al consultar períodos históricos).    
- **Salida**: listado de sectores (id, nombre, descripción, estado).    
- **Reglas de negocio**: por defecto solo deben listarse sectores activos, para no ofrecerlos como opción en nuevos cupos o autorizaciones.    
- **Errores**: 403 si lo invoca un usuario con rol Jefe de Servicio.  

* #### ``GET /api/sectors/{sector_id}` — obtener un sector.``  

- **Rol(es)**: Servicio de Alimentación, Sector Comedor.    
- **Entrada**: \`sector\_id\` en la URL.    
- **Salida**: datos completos del sector, incluyendo inactivos.    
- **Reglas de negocio**: sin restricción por estado — un sector desactivado debe poder consultarse para mantener trazabilidad histórica; solo se restringe su uso para nuevas asociaciones.    
- **Errores**: 404 si el sector no existe.  

* #### `GET /api/menu-types — listar tipos de menú.`  

- **Rol(es)**: cualquier usuario autenticado de las tres interfaces operativas (Jefe de Servicio, Servicio de Alimentación, Sector Comedor); se usa para poblar los combos de selección de menú y funcionalidades que lo requieran .    
- **Entrada**: ninguna.    
- **Salida**: listado completo de tipos de menú predefinidos.    
- **Reglas de negocio**: ninguna; catálogo fijo, sin filtro de activo/inactivo previsto en esta etapa.    
- **Errores**: —  

* #### `GET /api/personnel-types — listar tipos de personal.`  

- **Rol(es)**: cualquier usuario autenticado de las tres interfaces operativas.   
- **Entrada**: ninguna.    
- **Salida**: listado completo de tipos de personal predefinidos.    
- **Reglas de negocio**: ninguna.    
- **Errores**: —  

* #### `GET /api/configuration — consultar parámetros generales necesarios para las interfaces.`  

- **Rol(es)**: cualquier usuario autenticado.    
- **Entrada**: ninguna.    
- **Salida**:   
- **Reglas de negocio**: pendiente de cerrar la definición de "Ajustes del sistema".   
- **Errores**: —

### **Personal**

* `GET /api/personnel` — buscar y listar personal.  
- **Rol(es)**: Jefe de Servicio (limitado a su propio sector y del listado de residentes, ver Reglas de negocio), Servicio de Alimentación, Sector Comedor.  
- **Entrada**: filtros por query — DNI, nombre o apellido, sector, tipo de personal, estado; paginación (máximo 25 por página).  
- **Salida**: listado paginado de personas con sus datos básicos y el sector.  
- **Reglas de negocio**: un Jefe de Servicio solo puede ver personal con asignación vigente en su propio sector y, adicionalmente, buscar personas de tipo Residente, sin importar qué sector pida por parámetro — el backend debe forzarlo según el usuario autenticado, no confiar en lo que envíe el cliente; Servicio de Alimentación y Sector Comedor pueden buscar en todos los sectores; por defecto listar solo personas activas.  
- **Errores**: 400 si los filtros son inválidos.  
* `GET /api/personnel/{person_id}` — obtener una persona.  
- **Rol(es)**: Jefe de Servicio (solo si la persona tiene asignación vigente en su sector o si es de Tipo residente), Servicio de Alimentación, Sector Comedor.  
- **Entrada**: person\_id en la URL.  
- Salida: datos completos de la persona, incluyendo el sector y el tipo de personal.  
- **Reglas de negocio**: si lo llama un Jefe de Servicio y la persona no tiene asignación vigente en su sector o no es residente, debe rechazarse aunque la persona exista, para no exponer datos de personal de otros sectores.  
- **Errores**: 404 si no existe; 403 si un Jefe de Servicio consulta a alguien fuera de su sector.  
* `GET /api/sectors/{sector_id}/personnel` — listar el personal de un sector.  
- **Rol(es)**: Jefe de Servicio (solo su propio sector\_id), Servicio de Alimentación, Sector Comedor.  
- **Entrada**: sector\_id en la URL; parámetro de query opcional fecha (por defecto la fecha actual); paginación.  
- **Salida**: listado del personal activo perteneciente al sector.   
- **Reglas de negocio:** un Jefe de Servicio que pida un sector\_id distinto al propio debe recibir 403\.  
- **Errores**: 403 si un Jefe de Servicio pide un sector distinto al propio; 404 si el sector no existe.  
* `POST /api/personnel — crear una persona.`   
- **Rol(es)**:Rol(es): Jefe de Servicio, Servicio de Alimentación.  
- **Entrada**: DNI, nombre, apellido, tipo de personal, tipo menu y sector cuando corresponda.  
- **Salida**: datos de la persona creada.  
- **Reglas de negocio**:  
  - No debe existir otra persona con el mismo DNI  
  - Un Jefe de Servicio solo podrá crear personas de tipo Personal Fijo asociadas a su propio sector. El sector se determinará a partir del usuario autenticado y no deberá confiarse en un `sector_id` enviado por el cliente.  
  - Las personas de tipo Residente no tendrán sector asociado.  
  - La creación o modificación de Residentes quedará a cargo del **Servicio de Alimentación**. El Jefe de Servicio solamente podrá buscarlos y agregarlos a sus pedidos.  
  - El tipo de personal indicado deberá existir.  
  - La nueva persona quedará activa por defecto.  
- **Errores**: 400 si faltan datos obligatorios o alguno de los datos es inválido.; 409 si ya existe una persona con el mismo DNI ; 403 si un Jefe de Servicio intenta crear personal fuera de su sector o una persona de tipo Residente

	

* `PUT /api/personnel/{person_id} — modificar una persona.`  
- **Rol(es)**:Rol(es): Jefe de Servicio, Servicio de Alimentación.  
- **Entrada**: `person_id` en la URL y los datos modificables de la persona.  
- **Salida**: datos actualizados de la persona.  
- **Reglas de negocio**:  
  - Un Jefe de Servicio solo podrá modificar personas de tipo Personal Fijo pertenecientes a su propio sector.  
  - Un Jefe de Servicio no podrá modificar Residentes ni trasladar una persona a otro sector.  
  - El Servicio de Alimentación podrá modificar Personal Fijo y Residentes.  
  - No deberá permitirse modificar el DNI de forma que se genere una duplicación con otra persona registrada.  
  - Si se modifica el tipo de personal de Personal Fijo a Residente, deberá eliminarse su asociación con el sector.  
  - Si una persona pasa de Residente a Personal Fijo, deberá indicarse obligatoriamente el sector al que pertenece.  
  - Las modificaciones de los datos de una persona no deberán alterar pedidos, autorizaciones o entregas registrados anteriormente.  
- **Errores**: 404 si la persona no existe; 400 si los datos ingresados son inválidos; 403 si un Jefe de Servicio intenta modificar una persona que no pertenece a su sector o una persona de tipo Residente; 409 si el DNI ingresado corresponde a otra persona.


* `DELETE /api/personnel/{person_id} — eliminar o desactivar una persona.`  
- **Rol(es)**:Rol(es): Jefe de Servicio, Servicio de Alimentación.  
- **Entrada**: `person_id` en la URL.   
- **Salida**: confirmación de la operación realizada.   
- **Reglas de negocio:**   
  - Un Jefe de Servicio solo podrá realizar la operación sobre Personal Fijo perteneciente a su propio sector.  
  - Si la persona no tiene pedidos, autorizaciones, entregas u otros registros históricos asociados, podrá eliminarse.  
  - Si existen registros históricos asociados, la persona no deberá eliminarse físicamente y deberá marcarse como inactiva.  
  - Una persona inactiva no podrá incorporarse a nuevos pedidos, pero deberá continuar disponible para consultas históricas.  
  - Los pedidos, autorizaciones y entregas registrados previamente no deberán modificarse como consecuencia de esta operación.  
- **Errores**: 404 si la persona no existe; 403 si un Jefe de Servicio intenta modificar una persona que no pertenece a su sector o una persona de tipo Residente; 

### 

### **Pedidos de comidas**

#### `GET /api/requests — listar pedidos.`

- Rol(es): Jefe de Servicio.  
- Entrada: filtros por query — rango de fechas, tipo de comida, tipo de menú, estado (confirmado/autorizado/rechazado); paginación.  
- Salida: listado paginado de pedidos con sus totales por comida y tipo de menú.  
- Reglas de negocio: el sector se determina por el usuario autenticado, nunca por un parámetro que mande el cliente — un Jefe de Servicio solo ve los pedidos de su propio sector, y los ve junto con el resto de los Jefes de ese sector.  
- Errores: —

  #### 

  #### `POST /api/requests — crear un pedido.`

- Rol(es): Jefe de Servicio.  
- Entrada: periodicidad (diaria, semanal o mensual), fecha o rango de fechas, y el detalle por comida (desayuno/almuerzo/merienda/cena): la lista de personas (person\_id) incluidas. El sector se toma del usuario autenticado, no se recibe del cliente.  
- Reglas de negocio: cada persona de tipo Personal fijo incluida debe tener como sector el del Jefe de Servicio que realiza la carga; los Residentes pueden incluirse sin esa validación; no debe permitirse cargar personas inactivas; las personas se cargan dentro del cupo asignado al sector para esa comida hasta agotarlo, y las que excedan ese límite quedan marcadas como excepción. No deberá registrarse más de una vez una misma solicitud para la combinación persona, sector, fecha y tipo de comida.  
- Errores: 400 si faltan datos o una persona no tiene asignación vigente en la fecha de la comida; 409 si ya existe un pedido para el mismo sector y fecha 

  #### `GET /api/requests/{request_id} — obtener un pedido.`

- Rol(es): Jefe de Servicio, solo si el pedido pertenece a su sector.  
- Entrada: request\_id en la URL.  
- Salida: detalle completo del pedido — cantidades pedidas por comida y tipo de menú, personas incluidas, estado; si ya fue revisado por Servicio de Alimentación, incluir también las cantidades autorizadas como referencia de solo lectura, sin mezclarlas con las pedidas.  
- Reglas de negocio: —  
- Errores: 404; 403 si el pedido no pertenece al sector del Jefe de Servicio que consulta.

  #### `PUT /api/requests/{request_id} — modificar un pedido.`

- Rol(es): Jefe de Servicio, solo del sector propio.  
- Entrada: los mismos campos que la creación.  
- Salida: el pedido actualizado.  
- Reglas de negocio: solo podrá modificarse un pedido en estado confirmado. Al modificar las personas incluidas, el sistema deberá recalcular la condición Regular o Excepción de las solicitudes según el cupo actual del sector. Un pedido autorizado o rechazado no podrá ser modificado por el Jefe de Servicio.  
- Errores: 404 si el pedido no existe; 403 si no pertenece al sector del usuario; 409 si el pedido ya fue autorizado o rechazado.

  #### `DELETE /api/requests/{request_id} — eliminar un pedido`

- Rol(es): Jefe de Servicio, sector propio.  
- Entrada: request\_id en la URL.  
- Salida: confirmación vacía.  
- Reglas de negocio: solo podrá eliminarse un pedido en estado confirmado, mientras no haya sido autorizado o rechazado por el Servicio de Alimentación.  
- Errores: 404 si el pedido no existe; 403 si no pertenece al sector del usuario; 409 si el pedido ya fue autorizado o rechazado.

### **Autorización de pedidos**

#### `GET /api/authorizations — listar pedidos pendientes o autorizados.`

- Rol(es): Servicio de Alimentación.  
- Entrada: filtros por query — rango de fechas, sector, tipo de comida, tipo de menú, estado (confirmado/autorizado/rechazado); paginación. Etapa 2 pide vistas diarias y semanales, y consultar únicamente los períodos que tengan pedidos registrados, así que el filtro por fecha debería poder devolver solo fechas con datos en vez de un calendario vacío.  
- Salida: listado paginado de pedidos confirmados por los Jefes de Servicio, con las cantidades pedidas y, si ya se ajustaron, las autorizadas, siempre por separado.  
- Reglas de negocio: los pedidos en estado confirmado se consideran pendientes de autorización. Los totales deberán calcularse a partir de las solicitudes nominadas.  
- Errores: —

  #### `GET /api/authorizations/{request_id} — obtener el detalle de un pedido para su autorización.`

- Rol(es): Servicio de Alimentación.  
- Entrada: request\_id en la URL.  
- Salida: detalle completo discriminado por comida y tipo de menú, con cantidades pedidas y autorizadas por separado, y las personas incluidas.  
- Reglas de negocio: —  
- Errores: 404 si el pedido no existe.

  #### `PUT /api/authorizations/{request_id} — autorizar un pedido`

- Rol(es): Servicio de Alimentación.  
- Entrada: listado de las solicitudes nominadas del pedido indicando cuáles quedan autorizadas y cuáles no autorizadas.  
- Salida: pedido actualizado con las personas y comidas autorizadas, junto con los totales resultantes.  
- Reglas de negocio: solo podrán autorizarse solicitudes que formen parte del pedido original. La autorización deberá realizarse de manera independiente para cada persona y tipo de comida. La interfaz podrá permitir la selección individual o masiva de solicitudes, manteniendo en todos los casos el registro nominado de cada autorización. Las cantidades autorizadas se calcularán automáticamente a partir de las solicitudes autorizadas.  
- Errores: 404 si el pedido no existe; 409 si el pedido ya está autorizado o rechazado; 400 si se intenta autorizar una solicitud que no pertenece al pedido.

  #### `POST /api/authorizations/{request_id}/reject — rechazar un pedido.`

- Rol(es): Servicio de Alimentación.  
- Entrada: request\_id en la URL; posiblemente un motivo de rechazo, para trazabilidad (no está pedido en el documento pero es un dato barato de guardar).  
- Salida: el pedido en estado rechazado.  
- Reglas de negocio: un pedido rechazado no debe quedar disponible para entrega, pero sí visible en el historial.  
- Errores: 409 si ya estaba autorizado.

### **Cupos por sector**

#### `GET /api/quotas — listar cupos.`

- Rol(es): Servicio de Alimentación (gestión); Jefe de Servicio, solo lectura del cupo de su propio sector (Etapa 2 pide "visualizar el cupo asignado a su sector... mientras se realiza la carga"); Sector Comedor, para consultas en la interfaz.  
- Entrada: filtros por query — sector, tipo de comida; paginación.  
- Salida: listado de cupos (sector, comida, cantidad).  
- Reglas de negocio: un Jefe de Servicio solo puede ver los cupos de su propio sector, igual que con /api/sectors.  
- Errores: 403 si un Jefe de Servicio pide cupos de otro sector.

  #### `POST /api/quotas — crear un cupo.`

- Rol(es): Servicio de Alimentación.  
- Entrada: sector, comida, cantidad  
- Salida: el cupo creado.  
- Reglas de negocio: no debe poder crearse un cupo para un sector inactivo, igual que el resto de las asociaciones del sistema con sectores.  
- Errores: 400 si el sector está inactivo o los datos son inválidos.

  #### `GET /api/quotas/{quota_id} — obtener un cupo.`

- Rol(es): Servicio de Alimentación; Jefe de Servicio, solo el propio; Sector Comedor.  
- Entrada: quota\_id en la URL.  
- Salida: datos completos del cupo.  
- Reglas de negocio: mismo scoping por sector que en el listado para el Jefe de Servicio.  
- Errores: 404; 403 según corresponda.

  #### `PUT /api/quotas/{quota_id} — modificar un cupo.`

- Rol(es): Servicio de Alimentación.  
- Entrada: los mismos campos que la creación.  
- Salida: el cupo actualizado.  
- Reglas de negocio: la modificación de un cupo se aplicará únicamente a los pedidos que se registren con posterioridad al cambio. No deberá modificar la condición Regular o Excepción de las solicitudes pertenecientes a pedidos ya registrados. No podrá existir más de un cupo para la misma combinación de sector y tipo de comida.   
- **Errores: 400 si la nueva cantidad es menor** ~~a lo ya entregado en el período.~~ **a la cantidad ya autorizada o entregada para ese sector, fecha y tipo de comida.**

### **Consulta de comidas autorizadas**

#### `GET /api/authorized-meals — consultar comidas autorizadas por fecha, DNI, sector o tipo de comida.`

- Rol(es): Sector Comedor (uso principal, para verificar autorizaciones al momento de la entrega); Servicio de Alimentación.  
- Entrada: filtros por query — fecha, DNI,NyA, sector, tipo de comida.  
- Salida: listado de personas autorizadas para esa combinación, con el tipo de menú autorizado y si ya fue entregada esa comida o no, para evitar entregas duplicadas.  
- Reglas de negocio: solo deben aparecer pedidos en estado autorizado; debe indicarse cuánto de lo autorizado ya fue entregado.  
- Errores: 400 si no se envía ningún filtro, para evitar traer todo el sistema de una.

  #### 

  #### `GET /api/authorized-meals/{person_id} — consultar las comidas autorizadas para una persona.`

- Rol(es): Sector Comedor, Servicio de Alimentación.  
- Entrada: person\_id en la URL; parámetro de query fecha (por defecto la fecha actual).  
- Salida: las comidas autorizadas para esa persona en esa fecha (desayuno/almuerzo/merienda/cena, tipo de menú, si ya fue entregada).  
- Reglas de negocio: igual que el endpoint anterior.  
- Errores: 404 si la persona no existe.

### **Entregas**

#### `GET /api/deliveries — listar entregas.`

- Rol(es): Sector Comedor (las del turno en curso o consultas propias); Servicio de Alimentación (para reportes).  
- Entrada: filtros por query — fecha, sector, tipo de comida; paginación.  
- Salida: responsable y listado de entregas registradas.  
- Reglas de negocio: —  
- Errores: —

  #### `POST /api/deliveries — registrar una entrega.`

- Rol(es): Sector Comedor.  
- Entrada: personas incluidas en el retiro (person\_id de cada una), tipo de comida, tipo de menú entregado, y quién es el responsable del retiro (puede ser una de las personas incluidas o alguien que retira en representación de otras).  
- Salida: la entrega registrada, con fecha y hora, usuario del Sector Comedor que la efectuó, personas incluidas y responsable del retiro.  
- Reglas de negocio: cada persona incluida debe tener autorización vigente para esa comida y fecha que todavía no haya sido entregada, para no permitir retirar dos veces lo mismo; fecha, hora y usuario se completan automáticamente en el servidor, nunca los envía el cliente. Debe permitirse registrar la entrega de comidas correspondientes a una o varias personas en una misma operación   
- 409 si alguna persona ya tiene esa comida entregada; 400 si alguna persona no tiene autorización vigente para esa comida y fecha.

  #### `GET /api/deliveries/{delivery_id} — obtener el detalle de una entrega.`

- Rol(es): Sector Comedor, Servicio de Alimentación.  
- Entrada: delivery\_id en la URL.  
- Salida: detalle completo de la entrega.  
- Reglas de negocio: —  
- Errores: 404\.

  #### `GET /api/deliveries/summary — obtener el resumen de entregas de una fecha o tipo de comida.`

- Rol(es): Sector Comedor (para ver el total autorizado y entregado durante el día, según Etapa 2); Servicio de Alimentación.  
- Entrada: fecha o tipo de comida; sector opcional.  
- Salida: totales autorizados vs. entregados, discriminados por comida y tipo de menú.  
- Reglas de negocio: —  
- Errores: —

### **Resúmenes**

#### `GET /api/summaries/requests — obtener el resumen de pedidos.`

- Rol(es): Servicio de Alimentación; Jefe de Servicio, limitado a su propio sector.  
- Entrada: filtros por query — rango de fechas, sector, tipo de comida, tipo de menú, agrupamiento (día/semana/mes, sector, comida, tipo de menú).  
- Salida: totales de comidas pedidas, agrupados según el filtro.  
- Reglas de negocio: si lo llama un Jefe de Servicio, se limita a su propio sector.  
- Errores: —

  #### `GET /api/summaries/authorizations — obtener el resumen de autorizaciones.`

- Rol(es): Servicio de Alimentación.  
- Entrada: los mismos filtros que el resumen de pedidos.  
- Salida: totales pedidos vs. autorizados, con la diferencia entre ambos.  
- Reglas de negocio: —  
- Errores: —

  #### 

  #### `GET /api/summaries/deliveries — obtener el resumen de entregas.`

- Rol(es): Servicio de Alimentación.  
- Entrada: los mismos filtros que el resumen de pedidos.  
- Salida: totales autorizados vs. entregados, con la diferencia entre ambos.  
- Reglas de negocio: —  
- Errores: —

# 

# **Requisitos técnicos y pautas de trabajo**

## 1 Requisitos técnicos

* El prototipo debe desarrollarse utilizando **Python**, **JavaScript**, **HTML5**, **CSS3** y **PostgreSQL**, respetando el modelo en capas **MVC**.  
* Seguir las guías de estilo de Python y documentar todo el código Python con **docstrings**.  
* Uso obligatorio de **Jinja** como motor de plantillas para la aplicación privada.  
* La aplicación privada debe desarrollarse con el framework **Flask**.  
* Validar todos los datos de entrada tanto en el cliente como en el servidor.  
  * Las validaciones del lado del servidor deben implementarse en un módulo aparte que reciba los datos y devuelva el resultado.  
  * Ejemplos de validaciones: solo números en un DNI, solo caracteres válidos para un nombre, opciones de lista que pertenezcan al conjunto permitido, etc.  
* **Contraseñas**: deben almacenarse utilizando hash seguro (bcrypt, Argon2 o equivalente).  
  **Seguridad contra inyección SQL**: toda interacción con la base de datos debe realizarse a través de **SQLAlchemy** para evitar inyecciones SQL.  
* Se permite el uso de librerías que facilitan tareas (conexiones a servicios externos, parseo, patrones de buenas prácticas, validaciones de datos, etc.), siempre que todos los miembros del equipo puedan explicar su funcionamiento y propósito. Consultar con el ayudante si el uso de la misma está permitido.  
* **Login:** no se permite el uso de librerías que abstraiga completamente el proceso (por ejemplo, Flask-Login), para asegurar comprensión de la implementación.  
* Para la interacción con la base de datos, utilizar **SQLAlchemy** como ORM para mantener una capa de abstracción.  
* Aplicar buenas prácticas de **semántica web en HTML5**, usando correctamente las etiquetas del lenguaje.  
* Versionar todas las entregas con **git**, siguiendo **Versionado Semántico** (por ejemplo, etapa 1 → v1.x.x).  
* Se permite el uso de frameworks CSS como **Bootstrap**, **Foundation**, **Bulma** o **Tailwind CSS**.  
* **Manejo de logs**: implementar registro básico de eventos y errores en backend para facilitar depuración.  
* **Control de dependencias**: mantener actualizados y controlados pyproject.toml (Poetry) y package.json con versiones definidas para evitar incompatibilidades.  
* Todas las vistas deben ser **responsive** contemplando al menos tres resoluciones cómo mínimo (si quiere utilizar las sugeridas por el framework que utilice comuníquelo al ayudante):  
  * \< 767px (móviles)  
  * 768px – 1024px (tablets)  
  * \> 1025px (portátiles y escritorio)

  ## 2 Pautas de trabajo y entrega

* La entrega es obligatoria. Todos los integrantes deben participar en la defensa.  
* El ayudante evaluará el progreso y participación mediante consultas online y seguimiento en **GitLab**.  
* El proyecto se deberá realizar en grupos de **cinco integrantes**.  
* Deben ser visibles los aportes de cada integrante tanto en **GitLab** como en la defensa.  
* Si bien las tareas deberán dividirse entre los integrantes para organizar el trabajo, todos los miembros del equipo deberán comprender el funcionamiento integral del sistema y ser capaces de responder preguntas sobre cualquiera de sus componentes.  
* La defensa será virtual, salvo que se acuerde presencial por falta de medios técnicos (**micrófono y cámara**).  
* El trabajo será evaluado desde el **servidor de la cátedra**. No se aceptarán entregas fuera del tiempo y forma establecidos.  
* Funcionalidades no terminadas en la etapa 1 podrán completarse en la etapa siguiente.

  ## 3 Información del servidor

* **Lenguaje:** Python 3.14.4  
* **Servidor web:** nginx/1.28.3 (Ubuntu)  
* **Dependencias Python:** Poetry 2.4.1  
* **Base de datos:** PostgreSQL 16

# **Etapa 2**

**ENTREGA 20/11/2026**

Durante la segunda etapa se desarrollará la aplicación web que utilizarán los tres roles operativos del sistema:

* Jefe de Servicio.  
* Servicio de Alimentación.  
* Sector Comedor.

La aplicación deberá desarrollarse utilizando [Vue.js](http://Vue.js) 3 y consumirá exclusivamente la API del sistema implementada durante la primera etapa, con las ampliaciones necesarias descriptas en esta etapa. Los usuarios con rol Administrador continuarán utilizando la aplicación privada desarrollada en Flask.

Se deberá implementar una única aplicación frontend, con diferentes vistas, rutas y funcionalidades según el rol del usuario autenticado. En los requisitos técnicos, esta aplicación se denomina aplicación pública o portal.

Todos los módulos de esta etapa deberán respetar las reglas de negocio y los permisos ya implementados en la API. Las validaciones de la interfaz mejoran la experiencia de uso, pero no reemplazan las del backend. Cuando un módulo necesite un dato o una operación que la API no ofrece, el grupo deberá incorporar el endpoint correspondiente, documentarlo en la sección Ampliaciones de la API y protegerlo con los mismos criterios de autenticación y permisos.

## **Arquitectura de la aplicación frontend**

### **Descripción**

Definir la estructura base de la aplicación Vue.js: organización del proyecto, ruteo, manejo del estado y comunicación con la API.

### **Requerimientos funcionales**

* Crear el proyecto con Vite y Vue.js 3\.  
* Utilizar Vue Router para definir las rutas de cada módulo. Cada ruta deberá declarar los roles que pueden acceder a ella.  
* Centralizar las llamadas a la API en un único cliente HTTP que:  
  * Agregue el token en el header Authorization (Bearer) de cada solicitud.  
  * Tome la URL base de la API desde una variable de entorno, sin valores fijos en el código.  
  * Interprete los códigos de error de la API de forma uniforme, según lo definido en Manejo de errores y validaciones.  
* Organizar el código por módulo (carga de pedidos, autorización, entrega y reportes), separando vistas, componentes reutilizables y servicios de acceso a la API.

### **Notas**

* La elección de la librería de componentes visuales o del framework CSS es libre (por ejemplo Vuetify, PrimeVue, Bootstrap o Tailwind CSS), siempre que se cumplan los requerimientos de diseño responsive y de modo kiosco.  
* La aplicación deberá poder compilarse para producción y desplegarse en el entorno de prueba provisto por la cátedra.

## **Login y manejo de sesión**

### **Descripción**

Implementar la autenticación de la aplicación web contra la API, utilizando los endpoints POST /api/auth/login, POST /api/auth/logout, GET /api/auth/me y un nuevo endpoint POST /api/auth/google para el inicio de sesión con Google (ver Ampliaciones de la API).

### **Requerimientos funcionales**

* Formulario de login con email y contraseña.  
* Permitir iniciar sesión con una cuenta de Google como alternativa al email y la contraseña, mediante un botón "Iniciar sesión con Google" en el formulario de login (Google Identity Services).  
* Enviar el token de identidad (ID token) que devuelve Google al endpoint POST /api/auth/google, que lo valida y devuelve el mismo token de acceso que el login con email y contraseña.  
* Permitir el ingreso con Google solo si el email de la cuenta corresponde a un usuario registrado, activo y con rol Jefe de Servicio, Servicio de Alimentación o Sector Comedor. Si no existe, informar que debe solicitar el alta al Administrador.  
* Al iniciar sesión, guardar el token recibido y obtener los datos del usuario con GET /api/auth/me.  
* Redirigir a cada usuario a la pantalla inicial de su rol:  
  * Jefe de Servicio: carga de pedidos de su sector.  
  * Servicio de Alimentación: pedidos pendientes de autorización del día.  
  * Sector Comedor: pantalla de búsqueda del modo kiosco.  
* Impedir el acceso a la aplicación web a usuarios con rol Administrador y a usuarios bloqueados, mostrando un mensaje claro.  
* Proteger todas las rutas, salvo el login, con guards de navegación que verifiquen que exista una sesión y que el rol del usuario tenga acceso a la ruta. Si no tiene acceso, mostrar una página de acceso denegado.  
* Ante una respuesta 401 de la API (token ausente, inválido o expirado), cerrar la sesión local y redirigir al login, conservando la ruta a la que se intentaba acceder para volver a ella después de autenticarse.  
* Implementar el cierre de sesión con POST /api/auth/logout, eliminando el token y los datos del usuario almacenados en el cliente.

### **Notas**

* El token nunca deberá incluirse en la URL ni mostrarse en pantalla o en la consola del navegador.  
* El grupo deberá justificar dónde almacena el token (memoria, sessionStorage, localStorage o cookie) y qué riesgos implica cada opción.  
* En las tablets del Sector Comedor la sesión puede permanecer abierta durante todo un turno. El tiempo de expiración del token deberá definirse teniendo en cuenta ese uso.  
* El backend deberá verificar el ID token de Google (firma, emisor, audiencia igual al Client ID de la aplicación y vencimiento) antes de emitir el token de acceso. Nunca deberá confiar en un email enviado directamente por el cliente.  
* El login con Google no crea usuarios: las cuentas siguen siendo dadas de alta por el Administrador desde la aplicación privada.  
* El Client ID de Google se configurará mediante variables de entorno, y el grupo deberá registrar en Google Cloud Console los orígenes autorizados (entorno local y servidor de la cátedra).  
* Puede utilizarse una librería para verificar el ID token (por ejemplo, google-auth), pero la emisión del token de acceso y el manejo de la sesión seguirán siendo una implementación propia, según los requisitos técnicos.  
* Google Identity Services solo admite orígenes servidos por HTTPS, salvo localhost, por lo que el login con Google deberá probarse en un entorno con HTTPS.

### **Permisos**

* Podrán iniciar sesión los usuarios activos con rol Jefe de Servicio, Servicio de Alimentación o Sector Comedor, tanto con email y contraseña como con su cuenta de Google.

## **Layout y navegación**

### **Descripción**

Implementar un layout base para las vistas de la aplicación web, con el mismo criterio que el de la aplicación privada: los elementos comunes se definen una sola vez y cada módulo reemplaza únicamente el contenido central.

### **Requerimientos funcionales**

* Menú de navegación que muestre solo las opciones habilitadas para el rol del usuario autenticado y que indique visualmente la sección activa.  
* Barra superior con nombre o logo de la aplicación, nombre y apellido del usuario, su rol y, para el Jefe de Servicio, el sector asociado. Deberá incluir la opción de cerrar sesión.  
* Mecanismo común para mostrar mensajes de información, confirmación, advertencia y error.  
* Indicadores de carga mientras se espera la respuesta de la API, y mensajes de estado vacío cuando una consulta no devuelve resultados.  
* En pantallas chicas, el menú deberá colapsarse en un menú desplegable o en una barra inferior.  
* La vista del Sector Comedor utilizará un layout propio de modo kiosco, sin menú lateral, descripto en el Módulo de Entrega de pedidos.

### **Notas**

* Ocultar opciones del menú no reemplaza el control de permisos de la API ni los guards de las rutas.

## **Manejo de errores y validaciones**

### **Descripción**

Definir un criterio común para validar los datos en la interfaz y para mostrar los errores devueltos por la API en todos los módulos.

### **Requerimientos funcionales**

* Validar en el cliente, antes de enviar cada formulario, los campos requeridos, los formatos (DNI, fechas y correo electrónico) y los rangos, mostrando el mensaje junto a cada campo.  
* Mostrar los errores de la API según su código de estado:  
  * 400: mostrar el detalle de los datos inválidos junto a los campos correspondientes, cuando la API lo informe.  
  * 401: cerrar la sesión y redirigir al login.  
  * 403: informar que el usuario no tiene permisos para realizar la operación.  
  * 404: informar que el recurso no existe o fue eliminado.  
  * 409: informar el conflicto (por ejemplo, pedido ya autorizado o comida ya entregada) y recargar los datos afectados.  
  * 503: mostrar la pantalla de mantenimiento.  
  * Errores de red o 500: mostrar un mensaje genérico y permitir reintentar la operación.  
* Pedir confirmación antes de las operaciones que no se pueden deshacer: eliminar un pedido, rechazar un pedido, confirmar una autorización y confirmar una entrega.  
* Evitar envíos duplicados deshabilitando los botones de acción mientras la solicitud está en curso.


## **Módulo de Carga de Pedidos**

### **Descripción**

Permitirá a un Jefe de Servicio autenticado registrar, consultar y modificar los pedidos de comidas correspondientes a su sector, y gestionar el listado de personal a su cargo.

### **Requerimientos funcionales**

* Personal del sector y residentes:  
  * Al ingresar, listar todas las personas activas asignadas a su sector, para que de forma ágil les asigne la comida correspondiente.  
  * Agregar, editar o eliminar ficha de una persona y asignarlo a su sector. Aplica solo a personas de tipo Personal Fijo, con los datos y validaciones definidos en la primera etapa.  
  * Buscar entre el listado de residentes por nombre/apellido o DNI para asignarle la comida correspondiente. Al confirmar, el sistema validará que esa persona no tenga ya asignada la misma comida en la misma fecha por otro sector. Los residentes no se crean ni se modifican desde este módulo: su alta y modificación quedan a cargo del Servicio de Alimentación.  
* Selección de comidas:  
  * Seleccionar desayuno, almuerzo, merienda y cena de forma independiente para cada persona.  
  * Mostrar el tipo de menú de cada persona, precargado desde su ficha de Personal. El Jefe de Servicio no lo modifica en el pedido: si es incorrecto, deberá corregir la ficha de la persona.  
  * Permitir acciones masivas, como seleccionar una comida para todas las personas listadas o todas las comidas para una persona.  
* Periodicidad:  
  * Realizar pedidos de comida en forma diaria, semanal o mensual. Es decir, que en una misma acción se pueda realizar el pedido de comida para el día siguiente, o bien para toda la semana o incluso todo el mes. Esto se da porque hay sectores que tienen poco movimiento de personal y se repiten las asignaciones de viandas a lo largo de la semana/mes.  
  * En las cargas semanales o mensuales, la selección se aplica a cada día del período, con la posibilidad de excluir días puntuales (por ejemplo, francos o fines de semana).  
  * Cada sector tiene un único pedido por fecha, que agrupa las cuatro comidas. Una carga semanal o mensual genera un pedido por cada día del período. Si alguno de esos días ya tiene pedido, la carga no se registra y la interfaz ofrece abrir el pedido existente para editarlo.  
  * Mostrar el detalle por día antes de confirmar, para que el Jefe de Servicio vea qué solicitudes se van a registrar.  
* Cupos y totales:  
  * Visualizar el cupo asignado a su sector para cada tipo de comida.  
  * Visualizar, mientras se realiza la carga, los totales por comida y tipo de menú, y cuántas solicitudes quedan dentro del cupo (Regular) y cuántas lo exceden (Excepción).  
  * Identificar visualmente qué personas quedan como Excepción. La condición definitiva la calcula el backend al registrar el pedido, y la interfaz deberá mostrar la que devuelve la API.  
  * Las solicitudes se consideran regulares en el orden en que se incorporan al pedido, hasta completar el cupo de cada comida; las siguientes quedan como Excepción (ver Ampliaciones de la API).  
* Consulta de pedidos:  
  * Consultar las solicitudes actuales y anteriores del sector, con filtros por rango de fechas, tipo de comida, tipo de menú y estado.  
  * Mostrar el detalle de cada pedido: personas, comidas, tipo de menú y condición Regular o Excepción. Si el pedido ya fue revisado, mostrar también lo autorizado como dato de solo lectura, separado de lo pedido, y el motivo si fue rechazado.  
  * Modificar o eliminar un pedido únicamente mientras se encuentre en estado confirmado y antes de su horario de corte.

### **Notas**

* El sector siempre lo determina la API a partir del usuario autenticado. La interfaz no deberá permitir elegir otro sector.  
* Cuando un sector tenga más de un Jefe de Servicio, todos ven y gestionan los mismos pedidos. Si ya existe el pedido de una fecha, se modifica ese pedido en lugar de crear otro. Si la API rechaza una carga porque alguna fecha ya tiene pedido (409), la interfaz deberá informar esas fechas y ofrecer editar el pedido existente.  
* No se podrán incorporar personas inactivas ni personas de tipo Personal Fijo de otro sector.  
* Los cambios posteriores en el cupo o en la ficha de una persona no modifican los pedidos ya registrados.  
* Los pedidos de una fecha solo pueden cargarse, modificarse o eliminarse hasta el horario de corte, que es la hora del día anterior configurada en Ajustes del sistema (ver Ampliaciones de la API). No se pueden cargar pedidos para fechas pasadas. La interfaz deberá mostrar el horario de corte y no ofrecer fechas cuyo corte ya pasó.  
* El almuerzo de los residentes lo genera el Servicio de Alimentación para todos ellos (ver Módulo de Autorización). Por eso, el Jefe de Servicio normalmente solicita para los residentes las demás comidas, como la cena. Si carga el almuerzo de un residente que ya lo tiene generado, la API lo rechaza (409).

### **Permisos**

* Solamente los usuarios con rol Jefe de Servicio podrán acceder a este módulo, limitado a su propio sector.

### **Endpoints sugeridos**

* GET /api/auth/me, GET /api/sectors/{sector\_id}/personnel y GET /api/personnel para listar y buscar personal.  
* POST, PUT y DELETE /api/personnel para gestionar las fichas del personal del sector.  
* GET /api/quotas y GET /api/menu-types.  
* GET, POST, PUT y DELETE /api/requests, y GET /api/summaries/requests.

## **Módulo de Autorización**

### **Descripción**

Permitirá al personal del Servicio de Alimentación autenticado consultar los pedidos realizados por todos los sectores, autorizar o no autorizar cada solicitud nominada, rechazar pedidos completos y administrar los cupos.

### **Requerimientos funcionales**

* Consulta de pedidos:  
  * Consultar pedidos mediante vistas diarias y semanales, con navegación entre períodos.  
  * Consultar únicamente los períodos que tengan pedidos registrados, por ejemplo marcando en el calendario los días con datos.  
  * Filtrar por sector, tipo de comida, tipo de menú, condición (Regular o Excepción) y estado.  
  * Identificar claramente los pedidos pendientes de autorización (estado confirmado).  
* Autorización:  
  * Mostrar el detalle del pedido por persona y comida, con el tipo de menú y la condición Regular o Excepción de cada solicitud.  
  * Autorizar o no autorizar cada solicitud de manera individual.  
  * Realizar autorizaciones masivas para agilizar la operatoria. Esto es, autorizar distintos tipos de comida a muchas personas en la misma acción, sin necesidad de ir una por una. Se sugieren accesos rápidos como autorizar todas las solicitudes regulares o autorizar el pedido completo.  
  * Mostrar, antes de confirmar, los totales pedidos y los que quedarían autorizados.  
  * Rechazar un pedido completo, indicando el motivo del rechazo, que es obligatorio.  
  * Corregir la autorización de un pedido ya autorizado, en las condiciones indicadas en las Notas.  
* Visualizar las cantidades pedidas y autorizadas por separado.  
* Visualizar totales por sector, fecha, comida y tipo de menú.  
* Gestionar los cupos de comidas por sector: listar, crear y modificar el cupo de cada sector y tipo de comida.  
* Generar en una sola acción el almuerzo autorizado de todos los residentes activos para una fecha o un rango de fechas, con la posibilidad de excluir personas puntuales. Estos almuerzos no consumen cupos de los sectores.  
* Consultar períodos anteriores en modo de solo lectura.  
* Exportar listados a formato CSV, respetando los filtros aplicados:  
  * Listado detalle de personas autorizadas: apellido y nombre, DNI, sector, desayuno, almuerzo, merienda, cena y tipo de menú (general, vegetariano o celíaco).  
  * Listado con totales por sector, detallando la cantidad de viandas por comida y tipo de menú.

### **Notas**

* Las cantidades pedidas no deberán sobrescribirse cuando se modifiquen las cantidades autorizadas. Ej: si el Sector X solicitó 14 viandas y se le autorizaron solo 12, ambos valores deben quedar almacenados independientemente y mostrarse por separado.  
* Las cantidades autorizadas se calculan a partir de las solicitudes nominadas autorizadas. La interfaz no permitirá cargar totales a mano.  
* Se considera período anterior todo día previo a la fecha actual. En esos días no se podrán autorizar ni rechazar pedidos.  
* Una autorización puede corregirse mientras el pedido sea de la fecha actual o de una fecha futura y ninguna de sus comidas se haya entregado. Cada corrección conserva las cantidades pedidas y queda registrada con el usuario, la fecha y la hora.  
* Si el pedido cambió mientras se revisaba, porque otro usuario del Servicio de Alimentación lo rechazó o el Jefe de Servicio lo modificó, la API responderá con 409\. La interfaz deberá informarlo y recargar el pedido.

### **Permisos**

* Solamente los usuarios con rol Servicio de Alimentación podrán acceder a este módulo.

### **Endpoints sugeridos**

* GET /api/authorizations, GET y PUT /api/authorizations/{request\_id}, y POST /api/authorizations/{request\_id}/reject.  
* GET, POST y PUT /api/quotas, GET /api/sectors y GET /api/menu-types.  
* GET /api/summaries/authorizations.  
* POST /api/resident-lunches, para generar los almuerzos de los residentes (ver Ampliaciones de la API).  
* PUT /api/authorizations/{request\_id} y POST /api/authorizations/{request\_id}/reject con los cambios sobre corrección, días anteriores y motivo de rechazo descriptos en Ampliaciones de la API.  
* GET /api/authorized-meals y GET /api/summaries/authorizations con el parámetro formato=csv, para las exportaciones (ver Ampliaciones de la API).  
* GET /api/authorization-dates, para marcar en el calendario los días con pedidos (ver Ampliaciones de la API).

## **Módulo de Entrega de pedidos**

### **Descripción**

Permitirá a un usuario del Sector Comedor autenticado consultar las comidas autorizadas y registrar las entregas realizadas. Deberá diseñarse especialmente para su utilización en una tablet y ofrecer una modalidad de interacción tipo kiosco.

### **Modo kiosco**

Se denomina modo kiosco a una interfaz pensada para que un usuario la opere de pie, en una tablet y solo con el tacto, realizando siempre la misma tarea de forma rápida y sin distracciones. En este sistema se aplica únicamente a la vista del Sector Comedor.

Se espera que la vista cumpla con lo siguiente:

* Pantalla y navegación:  
  * Ocupar toda la pantalla, sin menú lateral, barra de navegación ni opciones ajenas a la entrega de comidas.  
  * Presentar un flujo lineal y siempre igual: buscar a la persona, revisar sus comidas autorizadas y confirmar la entrega.  
  * Ofrecer un acceso discreto, por ejemplo un ícono en una esquina, para consultar las entregas del turno, ver los totales del día y cerrar sesión.  
* Controles táctiles:  
  * Botones y áreas de toque de al menos 48 x 48 píxeles, con separación suficiente para evitar toques accidentales.  
  * Textos legibles a un brazo de distancia. El DNI, el nombre de la persona y sus comidas autorizadas deberán ser los elementos más destacados.  
  * No depender de interacciones que requieran precisión, como pasar el mouse por encima de un elemento, menús desplegables pequeños o doble clic.  
* Teclado numérico en pantalla:  
  * Mostrar un teclado propio con los dígitos del 0 al 9, una tecla para borrar el último dígito, otra para limpiar el campo y otra para buscar. ( idealmente evitar que se abra el teclado del sistema operativo de la tablet al tocar el campo de DNI.)  
  * Validar mientras se escribe que el DNI contenga solo números y tenga una longitud válida.  
* Información a la vista:  
  * Mostrar en forma destacada la comida en curso (desayuno, almuerzo, merienda o cena), determinada por la hora y los horarios configurados, con la posibilidad de cambiarla manualmente.  
  * Indicar el estado de cada comida con color, ícono y texto a la vez (autorizada pendiente, entregada o no autorizada), para que se distinga aunque no se perciban los colores.  
  * Mostrar un mensaje claro de confirmación al registrar la entrega, o de error si no pudo registrarse.  
* Vuelta al inicio:  
  * Volver automáticamente a la pantalla de búsqueda pocos segundos después de confirmar una entrega.  
  * Volver a la pantalla de búsqueda, descartando la operación en curso, luego de un tiempo sin actividad (por ejemplo, 60 segundos), para que la persona siguiente no vea los datos de la anterior.  
* Uso continuo:  
  * Mantener la sesión del usuario del Sector Comedor durante todo el turno, sin pedir el login en cada operación.  
  * Informar en pantalla si se pierde la conexión con la API, en lugar de quedar sin respuesta.(por ejemplo, implementando en la API un healthCheck)

Ejemplo:

![][image3]

\* Es ilustrativo, cada grupo tiene la libertad de aplicar su diseño.

### **Requerimientos funcionales**

* Implementar la vista en modo kiosco, según lo descripto en el apartado Modo kiosco.  
* Búsqueda:  
  * Buscar una o varias personas mediante DNI y, como alternativa, por nombre o apellido.  
  * Agregar varias personas a una misma operación de retiro.  
  * Buscar por sector para listar las personas de ese sector con comidas autorizadas pendientes y seleccionar varias en una misma operación, para agilizar el caso en que una persona retira las viandas de su sector.  
* Consultar si cada persona está autorizada para desayuno, almuerzo, merienda o cena en la fecha actual, y si esa comida ya fue entregada.  
* Visualizar el tipo de menú autorizado.  
* Distinguir con claridad, mediante color e ícono, las comidas autorizadas pendientes, las ya entregadas y las no autorizadas.  
* Operación de retiro:  
  * Seleccionar las comidas a entregar de cada persona incluida.  
  * Cuando el retiro incluya varias personas, designar a un responsable del retiro, que podrá ser una de las personas incluidas o un tercero identificado por DNI y nombre.  
  * Mostrar un resumen antes de confirmar: personas, comidas, tipos de menú y responsable del retiro.  
  * Confirmar la entrega.  
* Registrar automáticamente:  
  * Fecha y hora.  
  * Usuario del Sector Comedor.  
  * Personas incluidas.  
  * Responsable del retiro.  
  * Comida entregada.  
  * Tipos de menú.  
* Consultar las entregas realizadas durante el turno (desayuno, almuerzo, merienda o cena).  
* Visualizar el total autorizado y entregado durante el día, por comida y tipo de menú.

### **Notas**

* La fecha, la hora y el usuario los registra el servidor. El tipo de menú entregado es el que figura en la autorización: la interfaz no lo envía ni permite cambiarlo.  
* No se podrá entregar dos veces la misma comida autorizada. Si la API responde con 409, la interfaz deberá indicar qué persona y qué comida ya fueron entregadas.  
* Solo podrán entregarse comidas autorizadas para la fecha actual.  
* La entrega siempre es nominal: cada comida entregada queda asociada a la persona autorizada, aunque la retire otra persona como responsable del retiro. No se registran entregas por cantidad o por sector sin identificar a cada persona.  
* La pantalla no deberá mostrar más datos personales que los necesarios para identificar a la persona.

### **Permisos**

* Solamente los usuarios con rol Sector Comedor podrán registrar entregas. El Servicio de Alimentación podrá consultarlas desde el módulo de Reportes y Estadísticas.

### **Endpoints sugeridos**

* GET /api/authorized-meals y GET /api/authorized-meals/{person\_id}.  
* GET y POST /api/deliveries, GET /api/deliveries/{delivery\_id} y GET /api/deliveries/summary.  
* POST /api/deliveries con los cambios sobre el tipo de menú y el responsable del retiro descriptos en Ampliaciones de la API.

## **Reportes y Estadísticas**

### **Descripción**

Implementar un módulo de reportes y estadísticas que permita al **Servicio de Alimentación** consultar información consolidada sobre los pedidos, autorizaciones y entregas de comidas.

La información deberá obtenerse mediante la API y presentarse mediante tablas, indicadores y gráficos.

### **Requerimientos funcionales**

* Permitir filtrar la información por:  
  * Rango de fechas.  
  * Sector o servicio.  
  * Tipo de comida: Desayuno\|Almuerzo \| Merienda \| Cena.  
  * Tipo de menú. General \| Vegetariano \| Celíaco   
  * Estado del pedido.  
  * Condición: Regular \| Excepción.  
* Mostrar, como mínimo, los siguientes indicadores:  
  * Cantidad total de comidas pedidas regulares y por excepción.  
  * Cantidad total de comidas autorizadas.  
  * Cantidad total de comidas entregadas.  
  * Diferencia entre cantidades pedidas y autorizadas.  
  * Diferencia entre cantidades autorizadas y entregadas.  
* Incluir reportes agrupados por:  
  * Día, semana o mes.  
  * Sector.  
  * Tipo de comida.  
  * Tipo de menú.  
* Incluir, como mínimo, los siguientes gráficos:  
  * Comparación entre comidas pedidas, autorizadas y entregadas.  
  * Distribución de comidas por sector.  
  * Distribución por tipo de menú (general, vegetariano, celíaco)  
  * Evolución de las cantidades durante el período seleccionado.  
* Mostrar tablas con el detalle de los datos utilizados en los gráficos.  
* Permitir exportar los resultados a formato CSV.  
* Los reportes deberán respetar los filtros seleccionados.

### **Notas**

* La librería de gráficos queda a criterio del grupo.  
* Los totales deberán calcularse en el backend. La interfaz no deberá descargar el detalle completo de un período para sumarlo en el navegador.  
* Los valores exportados a CSV deberán coincidir con los que se muestran en pantalla para los mismos filtros.  
* Los almuerzos de residentes generados por el Servicio de Alimentación se muestran agrupados como Residentes en los reportes por sector.

### **Permisos**

* Solamente los usuarios con rol Servicio de Alimentación podrán acceder a este módulo.

### **Endpoints sugeridos**

* GET /api/summaries/requests, GET /api/summaries/authorizations y GET /api/summaries/deliveries, con la separación entre regulares y excepciones descripta en Ampliaciones de la API.  
* GET /api/deliveries/summary.  
* GET /api/sectors y GET /api/menu-types para poblar los filtros.  
* El parámetro formato=csv de los endpoints de resumen, para exportar los resultados (ver Ampliaciones de la API).


## **Modo mantenimiento y configuración**

### **Descripción**

La aplicación web deberá respetar los parámetros generales definidos por el Administrador en la aplicación privada, en particular el modo mantenimiento.

### **Requerimientos funcionales**

* Al iniciar la aplicación, consultar GET /api/configuration para obtener los parámetros que necesita la interfaz, por ejemplo la cantidad de registros por página, los horarios de cada comida, el horario de corte de los pedidos y el estado de mantenimiento.  
* Cuando el sistema esté en modo mantenimiento, la API responderá con 503 y la aplicación mostrará una pantalla de mantenimiento con el mensaje configurado por el Administrador, sin permitir operar.  
* Al finalizar el mantenimiento, la aplicación deberá permitir volver a operar sin perder la sesión, si el token sigue vigente.

### **Notas**

* Los parámetros se obtienen de GET /api/configuration, que no requiere autenticación para que la pantalla de mantenimiento pueda mostrarse antes del login (ver Ampliaciones de la API). Si la aplicación privada todavía no permite configurarlos, el grupo deberá incorporarlos en el módulo Ajustes del sistema.

## **Diseño responsive y accesibilidad**

### **Requerimientos funcionales**

* Diseño mobile first: todas las pantallas deberán poder usarse en teléfonos móviles, tablets, notebooks y computadoras de escritorio, contemplando como mínimo las tres resoluciones definidas en los requisitos técnicos: menos de 767px (móviles), de 768px a 1024px (tablets) y más de 1025px (portátiles y escritorio).  
* Las tablas con muchas columnas deberán adaptarse en pantallas chicas, por ejemplo mostrándose como tarjetas o con desplazamiento horizontal dentro de la tabla.  
* El Módulo de Entrega de pedidos deberá optimizarse para tablets, tanto en orientación horizontal como vertical.  
* Los controles deberán tener un tamaño apto para uso táctil y un contraste de color suficiente, y todos los campos de formulario deberán tener su etiqueta.  
* Las fechas se mostrarán en formato dd/mm/aaaa y los textos de la interfaz estarán en español.

## **Ampliaciones de la API**

La API definida en la Etapa 1 no se reduce: se actualiza según sea necesario. Los endpoints nuevos o los cambios que requiera la aplicación web se documentan en esta sección, con el mismo formato que en la Etapa 1, y deberán implementarse con sus mismos criterios de autenticación, permisos y manejo de errores.

Endpoints sugeridos: la siguiente lista es una guía para que el/la estudiante pueda modificar lo definido o agregar en caso que se considere, según lo requiera la solución implementada.

### **Autenticación**

#### `POST /api/auth/google — iniciar sesión con una cuenta de Google.`

* Rol(es): no requiere autenticación previa, igual que POST /api/auth/login. Lo utilizan los usuarios de las tres interfaces operativas.  
* Entrada: el ID token devuelto por Google Identity Services, en el cuerpo de la solicitud en formato JSON.  
* Salida: el mismo token de acceso y los mismos datos del usuario que devuelve POST /api/auth/login.  
* Reglas de negocio: verificar la firma del ID token, el emisor (Google), que la audiencia coincida con el Client ID configurado y que no esté vencido; buscar al usuario por el email verificado de la cuenta de Google; permitir el ingreso solo a usuarios activos con rol Jefe de Servicio, Servicio de Alimentación o Sector Comedor; no crear usuarios automáticamente.  
* Errores: 400 si falta el ID token; 401 si el ID token es inválido o expiró; 403 si el email no corresponde a un usuario activo con un rol habilitado para la aplicación web.

### **Sectores y datos generales**

#### `GET /api/configuration — consultar parámetros generales necesarios para las interfaces.`

* Cambio sobre la Etapa 1: completa la Salida, que había quedado pendiente, y deja de requerir autenticación, para que la aplicación pueda mostrar la pantalla de mantenimiento antes del login.  
* Rol(es): cualquier usuario, autenticado o no.  
* Entrada: ninguna.  
* Salida: cantidad de registros por página; horario de inicio y fin de cada comida (desayuno, almuerzo, merienda y cena); horario de corte para cargar, modificar o eliminar pedidos, expresado como la hora límite del día anterior a la fecha del pedido; estado del modo mantenimiento (activo o inactivo) y el mensaje a mostrar mientras esté activo.  
* Reglas de negocio: los valores los administra el Administrador desde la aplicación privada, en Ajustes del sistema. No deberá devolver datos sensibles. Mientras el modo mantenimiento esté activo, todos los demás endpoints de la API responderán 503 con el mensaje configurado.  
* Errores: —

### **Personal**

#### `GET /api/personnel, GET /api/personnel/{person_id} y GET /api/sectors/{sector_id}/personnel — sector actual en lugar de asignación vigente.`

* Aclaración sobre la Etapa 1: el personal no tiene asignaciones con fecha. Donde la Etapa 1 dice que una persona tiene asignación vigente en el sector del Jefe de Servicio, debe entenderse que es de tipo Personal Fijo y su sector actual es el del Jefe, o que es de tipo Residente.  
* Cambio sobre la Etapa 1: se elimina el parámetro opcional fecha de GET /api/sectors/{sector\_id}/personnel, que siempre lista el personal activo cuyo sector actual es el indicado.

### **Pedidos de comidas**

#### `POST /api/requests y PUT /api/requests/{request_id} — validación de residentes entre sectores.`

* Cambio sobre la Etapa 1: además de la regla ya definida para la combinación persona, sector, fecha y tipo de comida, no deberá registrarse una solicitud para una persona de tipo Residente si esa persona ya tiene la misma comida en la misma fecha en un pedido de otro sector o en los almuerzos generados por el Servicio de Alimentación.  
* Errores adicionales: 409 si se da el caso anterior, indicando la persona, la fecha, la comida y el sector que ya la tiene asignada.

  #### `POST /api/requests — un pedido por sector y fecha.`

* Aclaración sobre la Etapa 1: cada sector tiene un único pedido por fecha, que agrupa las cuatro comidas. Una carga semanal o mensual crea un pedido por cada día del período.  
* Reglas de negocio: si alguno de los días del período ya tiene pedido para el sector, no se registra ningún día de la carga. Para agregar comidas o personas a un día que ya tiene pedido se usa PUT /api/requests/{request\_id}, mientras el pedido esté en estado confirmado. Dentro de un pedido se mantiene la regla de no repetir la combinación persona y tipo de comida.  
* Errores: 409 si alguna fecha ya tiene pedido, indicando cada fecha y el request\_id del pedido existente, para que la interfaz ofrezca editarlo.  
* Además, el error 400 de la Etapa 1 por persona sin asignación vigente en la fecha de la comida pasa a ser 400 si alguna persona está inactiva o es de tipo Personal Fijo de otro sector, ya que el personal no tiene asignaciones con fecha.

  #### `POST, PUT y DELETE /api/requests — horario de corte y fechas pasadas.`

* Cambio sobre la Etapa 1: no se pueden crear pedidos para fechas pasadas ni para fechas cuyo horario de corte ya pasó, y un pedido tampoco puede modificarse ni eliminarse una vez pasado su horario de corte.  
* Reglas de negocio: el horario de corte lo define el Administrador en Ajustes del sistema y se informa en GET /api/configuration. Por ejemplo, con un corte a las 18:00, el pedido del jueves puede cargarse, modificarse o eliminarse hasta el miércoles a las 18:00. En las cargas semanales o mensuales, si alguno de los días ya superó su horario de corte, no se registra ningún día de la carga. La validación se hace siempre en el servidor, con su propia hora.  
* Errores adicionales: 400 si alguna fecha ya pasó o superó su horario de corte, indicando cuáles.

  #### `POST y PUT /api/requests — criterio para determinar las excepciones.`

* Aclaración sobre la Etapa 1: para cada fecha y tipo de comida, las solicitudes se consideran regulares en el orden en que fueron incorporadas al pedido, hasta completar el cupo del sector; las siguientes quedan como excepción.  
* Reglas de negocio: al modificar un pedido, las solicitudes que se mantienen conservan su orden de incorporación y las nuevas se agregan al final. Si se quita una solicitud regular, la primera excepción de esa comida pasa a ser regular.

### **Autorización de pedidos**

#### `PUT /api/authorizations/{request_id} — corrección de autorizaciones y días anteriores.`

* Cambio sobre la Etapa 1: un pedido ya autorizado puede volver a editarse mientras sea de la fecha actual o de una fecha futura y ninguna de sus comidas haya sido entregada.  
* Reglas de negocio: cada corrección conserva las cantidades pedidas, recalcula las autorizadas y registra el usuario, la fecha, la hora y el estado anterior de cada solicitud modificada. Los pedidos de días anteriores no pueden autorizarse ni corregirse, y un pedido rechazado no puede autorizarse.  
* Errores: el 409 de la Etapa 1 deja de aplicarse a los pedidos ya autorizados. Ahora corresponde 409 si el pedido es de un día anterior, si alguna de sus comidas ya fue entregada o si el pedido fue rechazado.

  #### `POST /api/authorizations/{request_id}/reject — motivo obligatorio y días anteriores.`

* Cambio sobre la Etapa 1: el motivo de rechazo pasa a ser obligatorio y se devuelve en el detalle del pedido, también en GET /api/requests/{request\_id} para el Jefe de Servicio.  
* Reglas de negocio: no pueden rechazarse pedidos de días anteriores ni pedidos ya rechazados. Para dejar sin efecto un pedido ya autorizado se corrige su autorización marcando las solicitudes como no autorizadas.  
* Errores adicionales: 400 si falta el motivo; 409 si el pedido es de un día anterior o ya estaba rechazado.

  #### `PUT /api/authorizations/{request_id} — cambios simultáneos sobre un pedido.`

* Cambio sobre la Etapa 1: el detalle de un pedido incluye la fecha y hora de su última modificación, y PUT /api/authorizations/{request\_id} debe recibirla junto con las solicitudes autorizadas.  
* Reglas de negocio: si el pedido se modificó después de que el Servicio de Alimentación lo abrió, por ejemplo porque el Jefe de Servicio agregó o quitó personas, la autorización no se registra para que se revise la versión actual.  
* Errores adicionales: 409 si la fecha y hora de última modificación enviada no coincide con la actual del pedido.

  #### `GET /api/authorization-dates — fechas con pedidos registrados.`

* Rol(es): Servicio de Alimentación.  
* Entrada: rango de fechas y, opcionalmente, sector y estado.  
* Salida: listado de las fechas del rango que tienen pedidos, con la cantidad de pedidos pendientes, autorizados y rechazados de cada una.  
* Reglas de negocio: se usa para marcar en el calendario los días con datos y navegar solo entre ellos en las vistas diarias y semanales.  
* Errores: 400 si falta el rango de fechas o es inválido; 403 si lo invoca un usuario sin rol Servicio de Alimentación.

  #### `POST /api/resident-lunches — generar los almuerzos de los residentes.`

* Rol(es): Servicio de Alimentación.  
* Entrada: fecha o rango de fechas y, opcionalmente, la lista de personas (person\_id) a excluir.  
* Salida: cantidad de almuerzos generados y listado de los omitidos, con el motivo de cada uno.  
* Reglas de negocio: genera una solicitud de almuerzo ya autorizada por cada persona activa de tipo Residente y cada fecha del rango; no consume cupos de ningún sector; omite a quienes ya tengan el almuerzo de esa fecha asignado en un pedido de un sector; registra la fecha, la hora y el usuario que realizó la operación. Los almuerzos se registran, ya autorizados, en el pedido de cada fecha del sector Residentes; si ese pedido ya existe, solo se agregan los residentes que falten. El sector Residentes lo crea el Administrador como cualquier otro sector, no tiene Jefes de Servicio ni cupos asociados, y permite que estos almuerzos aparezcan como un sector más en GET /api/authorized-meals, en los totales y en los reportes.  
* Errores: 400 si el rango de fechas es inválido o incluye fechas pasadas; 400 si no existe el sector Residentes; 403 si lo invoca un usuario sin rol Servicio de Alimentación.

### **Cupos por sector**

#### `PUT /api/quotas/{quota_id} — validación de la nueva cantidad.`

* Cambio sobre la Etapa 1: se elimina el error 400 cuando la nueva cantidad es menor a lo ya entregado en el período, porque la modificación de un cupo solo se aplica a los pedidos que se registren después del cambio y no afecta pedidos ni entregas existentes.  
* Errores: 400 si la cantidad no es mayor que cero o el sector está inactivo; 404 si el cupo no existe; 409 si ya existe otro cupo para el mismo sector y tipo de comida.

### **Entregas**

#### `POST /api/deliveries — tipo de menú y responsable del retiro.`

* Cambio sobre la Etapa 1: la entrada ya no incluye el tipo de menú entregado. El servidor lo toma de la solicitud autorizada de cada persona; si el cliente lo envía, se ignora.  
* Entrada: personas incluidas en el retiro (person\_id de cada una), tipo de comida y responsable del retiro: el person\_id de una de las personas incluidas o, si se trata de un tercero que no está registrado como personal, su DNI y su nombre y apellido.  
* Reglas de negocio: el responsable es obligatorio cuando el retiro incluye más de una persona; si incluye una sola, esa persona es el responsable por defecto.  
* Errores adicionales: 400 si falta el responsable en un retiro de varias personas o si el DNI del tercero no tiene un formato válido.

### **Resúmenes**

#### `GET /api/summaries/requests, GET /api/summaries/authorizations y GET /api/summaries/deliveries — condición Regular o Excepción.`

* Cambio sobre la Etapa 1: agregar el filtro opcional condición (regular, excepción o todas) y el agrupamiento por condición, y devolver en la salida los totales de solicitudes regulares y por excepción por separado, además del total general.  
* Reglas de negocio: los almuerzos de residentes generados por el Servicio de Alimentación pertenecen al sector Residentes y no tienen condición Regular o Excepción, por lo que se informan por separado de esos dos totales.  
* Errores adicionales: 400 si el valor del filtro de condición o del agrupamiento es inválido.

### **Exportaciones**

#### `GET /api/authorized-meals y GET /api/summaries/* — exportación a CSV.`

* Cambio sobre la Etapa 1: estos endpoints aceptan el parámetro opcional formato=csv. Con ese parámetro devuelven el resultado completo para los filtros recibidos, sin paginación, como archivo CSV.  
* Rol(es): Servicio de Alimentación.  
* Reglas de negocio: se aplican los mismos filtros, permisos y validaciones que en la consulta paginada, y el rango de fechas es obligatorio. En GET /api/authorized-meals, el CSV tiene una fila por persona con las columnas apellido y nombre, DNI, sector, desayuno, almuerzo, merienda, cena y tipo de menú. En los resúmenes, el CSV contiene los mismos totales y agrupamientos que la respuesta en JSON.  
* Errores adicionales: 400 si falta el rango de fechas o el valor de formato es inválido; 403 si lo invoca un usuario sin rol Servicio de Alimentación.

# **Requisitos técnicos y pautas de trabajo**

## 1 Requisitos técnicos

* El prototipo debe desarrollarse utilizando **Python**, **JavaScript**, **HTML5**, **CSS3** y **PostgreSQL**, respetando el modelo en capas **MVC**.  
* Seguir las guías de estilo de Python y documentar todo el código Python con **docstrings**.  
* Uso obligatorio de **Jinja** como motor de plantillas para la aplicación privada.  
* La aplicación privada debe desarrollarse con el framework **Flask**.  
* Validar todos los datos de entrada tanto en el cliente como en el servidor.  
  * Las validaciones del lado del servidor deben implementarse en un módulo aparte que reciba los datos y devuelva el resultado.  
  * Ejemplos de validaciones: solo números en un DNI, solo caracteres válidos para un nombre, opciones de lista que pertenezcan al conjunto permitido, etc.  
* **Contraseñas**: deben almacenarse utilizando hash seguro (bcrypt, Argon2 o equivalente).  
  **Seguridad contra inyección SQL**: toda interacción con la base de datos debe realizarse a través de **SQLAlchemy** para evitar inyecciones SQL.  
* Se permite el uso de librerías que facilitan tareas (conexiones a servicios externos, parseo, patrones de buenas prácticas, validaciones de datos, etc.), siempre que todos los miembros del equipo puedan explicar su funcionamiento y propósito. Consultar con el ayudante si el uso de la misma está permitido.  
* **Login:** no se permite el uso de librerías que abstraiga completamente el proceso (por ejemplo, Flask-Login), para asegurar comprensión de la implementación.  
* Para la interacción con la base de datos, utilizar **SQLAlchemy** como ORM para mantener una capa de abstracción.  
* Aplicar buenas prácticas de **semántica web en HTML5**, usando correctamente las etiquetas del lenguaje.  
* Versionar todas las entregas con **git**, siguiendo **Versionado Semántico** (por ejemplo, etapa 1 → v1.x.x).  
* Se permite el uso de frameworks CSS como **Bootstrap**, **Foundation**, **Bulma** o **Tailwind CSS**.  
* **Manejo de logs**: implementar registro básico de eventos y errores en backend para facilitar depuración.  
* **Control de dependencias**: mantener actualizados y controlados pyproject.toml (Poetry) y package.json con versiones definidas para evitar incompatibilidades.  
* Todas las vistas deben ser **responsive** contemplando al menos tres resoluciones cómo mínimo (si quiere utilizar las sugeridas por el framework que utilice comuníquelo al ayudante):  
  * \< 767px (móviles)  
  * 768px – 1024px (tablets)  
  * \> 1025px (portátiles y escritorio)  
* Se debe utilizar VueJs (versión 3.5.X) como framework de desarrollo web para la aplicación pública/portal.

  ## 2 Pautas de trabajo y entrega

* La entrega es obligatoria. Todos los integrantes deben participar en la defensa.  
* El ayudante evaluará el progreso y participación mediante consultas online y seguimiento en **GitLab**.  
* El proyecto se deberá realizar en grupos de **cinco integrantes**.  
* Deben ser visibles los aportes de cada integrante tanto en **GitLab** como en la defensa.  
* La defensa será virtual, salvo que se acuerde presencial por falta de medios técnicos (**micrófono y cámara**).  
* El trabajo será evaluado desde el **servidor de la cátedra**. No se aceptarán entregas fuera del tiempo y forma establecidos.  
* Funcionalidades no terminadas en la etapa 1 podrán completarse en la etapa siguiente.

  ## 3 Información del servidor

* **Lenguaje:** Python 3.14.4  
* **Servidor web:** nginx/1.28.3 (Ubuntu)  
* **Dependencias Python:** Poetry 2.4.1  
* **Base de datos:** PostgreSQL 16

[image1]: <data:image/png;base64,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>

[image2]: <data:image/png;base64,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>

[image3]: <data:image/png;base64,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>