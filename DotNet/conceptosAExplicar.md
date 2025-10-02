
Lista de conceptos

- [x] Que es una Clase/Abstracta/Interfaz
    - Una Clase es una unidad logica que contiene campos, propiedades y comportamiento
    - Una Clase abstracta es una clase que no se puede implementar, pero puede declarar campos y propiedades tanto como metodos que seran obligatorios para todas las clases que decidan implementarlos
    - Una Interfaz es un tipo de referencia que forma un conjunto de firmas/contratos a los que se compromete a cumplir una clase cuando la implementa con el fin de poder ser tratado de manera generica

- [x] Inversion de dependencias
    - El principio de inversion de dependencias corresponde a la I de los principios SOLID, consiste en que ninguna clase deberia de depender de ninguna implementacion en especifico, sino que las dependencias deberian de ser a interfaces, para reducir el acoplamiento y maximizar la reemplazabilidad de las partes de un sistema.

- [x] Inyeccion de dependencias
    - La inyeccion de dependencias es un patron en el cual se garantiza que ninguna clase dependa de otra implementacion sino de alguna interfaz. Para garantizar esto es necesario que otra clase tome la responsabilidad de instanciar las implementaciones de las interfaces para luego pasarselas a la clase que necesita la interfaz a traves de parametro por su constructor

- [x] Por que casos de uso
    - El uso de casos de uso como clases separadas tiene que ver con reducir el acoplamiento entre las funcionalidades y mantener el principio de unica responsabilidad.

- [x] Arquitectura limpia
    - La arquitectura limpia se corresponde a aplicar conceptos de la inyeccion de dependencias a los bloques logicos de funcionalidad de nuestro software, dividiendo en responsabilidades o intereses.

- [x] LINQ
    - LINQ es una serie de sintaxis implementadas en C# que permiten hacer consultas de SQLite 

- [x] Entity Framework
    - El Entity Framework Core es un ORM (Object Relational Mapper) que se encarga de trasnformar los objetos que tenemos en el codigo en tablas con campos en nuestra base de datos
    - Primero se genera un Modelo, que contiene dentro una clase de entidad que tambien estamodelada en la base de datos todas las variables necesarias para coordinarse (los campos de la tabla/clase). A traves de las cuales se genera la sesion con la base de datos

    - Uno puede generar las tablas en la base de datos a traves de primero codificarlas en C# y luego permitir que el EF termine de crear los modelos con las tablas en las bases de datos. A este enfoque se le llama "Code First"
    - Como alternativa al "Code First" existe el concepto de "Migraciones", que permiten generar modificaciones incrementales a las bases de datos y volver hacia atras por migraciones.

- [x] Di Container
    - El Di Container (Contenedor de Inyeccion de Dependencias) es una clase que cumple el rol de contener todas las implementaciones de las interfaces que necesitan las clases del sistema para funionar en un punto centralizado. De manera que sea facil intercambiar las piezas dentro del programa
    

- [x] Singleton, Transitient y Scoped
    - Utilizar un DiC nos permite tambien determinar si una clase es un "Singleton", siendo esto una clase de la que solo se tiene una instancia dentro de todo el sistema
    - Ademas del "Singleton", se puede utilizar un "Transitient" que hace lo opuesto, cada vez que se inyecta el transitient en alguna dependencia, se crea un nuevo objeto.
    - Por ultimo se puede utilizar una variable "Scoped" que actua como un Transitient de ambiente, lo cual es util en las paginas web de ASP .Net porque actuan como instancias de servicio de sesion, que se refrescan entre pestañas y cada vez que se reinicia la pagina

