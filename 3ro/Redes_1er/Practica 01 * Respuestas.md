
# Respuestas

1. Que es una red? Cual es el principal objetivo para construir una red?
  * Una red es un conjunto de computadoras conectadas con el fin de compartir datos entre ellas (?

2. Que es Internet? Describa los principales componentes que permiten su funcionamiento
  * El internet es la red mas grande que existe hoy en el mundo, conectando la gran mayoria de dispositivos que se utilizan hoy en dia. Siendo sus componentes:
    * Host/Sistema Terminal: este sistema es utilizado directamente por un consumidor que desea enviar o recibir datos de otro host dentro de la misma red
    * ISP: el Internet Service Provider es una empresa que se encarga de hacer las monturas fisicas del cable o la logistica del wifi que conecta con el internet a los sistemas terminales.
    * Router: un router es un dispositivo que actua como canal para conectar dos computadoras de manera indirecta

3. Que son las RFCs?
  * Las RFCs (Request For Comments) son documentos que forman normas/estandares/protocolos establecidos por la comunidad para mantener las interfaces del internet compatibles entre ellas.

4. Que es un protocolo?
  * Un protocolo es una serie de pasos estandarizados que se toman para asegurar una comunicacion exitosa, por ejemplo cuando alguien quiere preguntarle la hora a otra persona es costumbre saludar primero para establecer que "estamos en una conversacion" donde luego se envian los datos pertinentes a la comunicacion (en este caso la hora) y luego se continua con el protocolo agradeciendo/despidiendose, lo cual establece "este es el fin de la conversacion"

5. Por que dos maquinas con distintos sistemas operativos pueden formar parte de una misma red?
  * Dos maquinas con distintos sistemas operativos pueden formar parte de una misma red debido a que la comunicacion sucede a traves de protocolos que no solo establecen como deben establecer contacto los dispositivos, sino que tambien definen el formato en el que los datos deben enviarse. De manera tal que cada sistema operativo se comunique a traves del protocolo para mantener interoperabilidad y luego acomoda el formato de los datos para funcionar dentro de su sistema

6. Cuales son las 2 categorias en las que pueden clasificarse a los sistemas finales o End Systems? De un ejemplo del rol de cada uno en alguna aplicacion distribuida que corra sobre Internet.
  * Las dos categorias en las que pueden clasificarse a los hosts es como Cliente y como Servidor. Un host cliente es quien pide un servicio del servidor, ya sea porque no tiene los datos necesarios (una request a una base de datos) o porque el cliente no tiene la capacidad de computo (las busquedas de google se computan desde el lado servidor) para realizar la operacion en una cantidad de tiempo razonable, un cliente no tiene que ser obligatoriamente un usuario final, dado que existen comunicaciones M2M (machine to machine) en las que una de las maquinas necesita datos por lo cual actua como cliente. Por el otro lado el servidor es el que recibe la request inicial, realiza por lo general la mayoria de la operacion (busqueda en la db, computo de la operacion) y retorna los datos solicitados por el cliente para que los utilice como desee. Un ejemplo podria ser el uso del Search Engine de Google, como se explico en los parentesis de la definicion de los roles de los End Systems.

7. Cual es la diferencia entre una red conmutada de paquetes de una red conmutada de circuitos?
  * Una red conmutada de paquetes recibe y reenvia los paquetes a traves de routers, la informacion llega a estos routers por demanda. Es decir, se envian paquetes directamente a los puntos intermedios de comunicacion entre los sistemas terminales y estos esperan en una cola para el momento en el que puedan ser atendidos.
  * Una red conmutada de circuitos por el otro lado primero establece una conexion y se reserva asi una cierta velocidad de transmision que mantiene una conexion constante por la que enviar datos de manera garantizada

8. Analice que tipo de red es una red de telefonia y que tipo de red es internet
  * Una red de telefonia cuando esta en uso representa una red conmutada de circuitos por los que a traves de llamadas uno reserva cierto ancho de banda para poder comunicarse de manera ininterrumpida con otra persona. Por el otro lado el internet es una red conmutada de paquetes donde se acepta la espera y la interrupcion de los mensajes a cambio de que el completo de internet maneje la mayor cantidad de datos posible.

9. Describa brevemente las distintas alternativas que conoce para acceder a Internet en su hogar
  * En mi hogar conozco solo dos alternativas para conectarme al internet, a traves de conexion wifi y a traves de conexion por cable ehternet

10. Que ventajas tiene una implementacion basada en capas o niveles?
  * La gran ventaja de utilizar un enfoque basado en capas o niveles sobre cualqeuir sistema de gran tamano y/o complejidad como lo es las redes, es que se pueden estudiar y entender las capas individualmente para luego abordarlas todas como un conjunto explorando sus relaciones una vez ya son entendidas. Incluso fuera del estudio, el uso de capas permite que se reemplazen y se desarrollen nuevas tecnologias dentro del sistema general siempre que las interfaces de servicios se mantengan, es decir, bajo los previos inputs, se retornan los mismos datos, permitiendo el cambio de la implementacion interna y la adicion de nuevas funcionalidades con mayor facilidad.

11. Como se llama la PDU de cada una de las siguientes capas: Aplicacion, Transporte, Red y Enlace?
  * ?

12. Que es la encapsulacion? Si una capa realiza la encapsulacion de datos, que capa del nodo receptor realizara el proceso inverso?
  * La encapsulacion es el proceso por el cual una capa N recibe el dato de la capa N-1 y agrega a ella los datos necesarios para cumplir con su funcion:
    * La capa de Transporte agrega header de transporte(TCP), transformando el *Mensaje* en un *Segmento*
    * La capa de Red agrega header de red(IP), transformando el *Segmento* en *Datagrama*
    * La capa de Enlace agrega el header de link(?), transformando el *Datagrama* en *Trama*
  Una vez finalizado el encapsulamiento de enlace, el dispositivo puede enviar el dato al siguente host/router de manera que la misma capa dentro del nuevo dispositivo realice el proceso inverso

13. Describa cuales son las funciones de cada una de las capas del stack TCP/IP o protocolo de Internet
  * Capa de Aplicacion: La capa de aplicacion se encarga de representar la interfaz con el sistema terminal a traves de la cual el usuario o la maquina va a ingresar/solicitar los datos pertinentes al mensaje y contenido deseado del servidor
  * Capa de Transporte: La capa de transporte agrega datos de contexto necesarios para saber desde que IP a que IP se deben de enviar los datos, ademas de contener el protocolo de seguridad TCP que divide el mensaje en segmentos para ser enviados a traves de la red como paquetes y se asegura de que todos los paquetes lleguen al host destino.
  * Capa de Red: La capa de red se encarga de generar la ruta que conecta las direcciones IP provistas por la capa de transporte.
  * Capa de Enlace: La capa de enlace recibe la direccion del proximo nodo(router/host) al que enviarle los datos y envia/recibe los datos fisicamente

14. Compare el modelo OSI con la implementacion TCP/IP
  * La mayor diferencia entre el modelo OSI y la implementacion TCP/IP se encuentra en las capas de Presentacion y Sesion que se encargan de compresion, cifrado, descripcion (presentacion) de los datos y la delimitacion y sincronizacion de estos intercambios. Dentro del modelo OSI estas capas existen como requerimientos para cada transaccion de datos a traves de redes (no necesariamente en el internet, debido a que el modelo OSI lo precede), dado que en el internet estas funciones demostraron ser inecesarias como requisito minimo de transferencia, se decidio que la implementacion del modelo TCP/IP no tendria en cuenta ninguna de estas actividades y dejaria a responsabilidad del desarrollador manejar la compresion, cifrado y descripcion de los datos dentro de la capa de aplicacion solo para los datos que el considere que estas funciones sean necesarias.
  Ademas de esto, el modelo TCP/IP se abstrae de la capa Fisica, incorporandola dentro de la capa de Enlace
