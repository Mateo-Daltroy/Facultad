
# Repaso REDES
> [!note] Proposito
> Este repaso consiste en una puesta en practica de recoleccion de todo lo que recuerdo de redes para
> contrastar con el material y revisar que conceptos tengo fuertes o flojos


# Modelo TCP/IP
El modelo TCP/IP es una manera de conceptualizar las comunicaciones de diversos protocolos que forman parte del proceso para hacer llegar un mensaje a traves del internet como un sistema por capas. Es similar al modelo OSI, con la principal diferencia de que tiene menos capas (4 contra 8), agrupando funcionalidades. El modelo TCP/IP es el utilizado por nuestra catedra, por ende nos vamos a apegar exclusivamente a este a la hora de dividir y estudiar las capas y sus protocolos. 

La comunicacion de un paquete, desde el host sender hasta la llegada al host receiver pasa por 4 capas, de las cuales cada una le suma cierta informacion (generalmente en forma de headers) necesaria para ejecutar la funcionalidad apropiada del servicio que cada una provee:
- Aplicacion: La capa de aplicacion es la que utilizan los procesos para comunicarse directamente entre si, toma el servicio de la capa de transporte para llegar de proceso a proceso. Se encarga de ser la interfaz de comunicacion de los procesos, manejando caches de datos logicos, autenticacion y el envio de recursos (archivos, correos y data en general).
- Transporte: La capa de transporte se encarga de contener los datos que desea enviar el proceso, dividirlos en segmentos que sean transportables por la capa inferior para luego recolectarlos y reensamblarlos en un unico mensaje. Tambien es el que organiza las multiples comunicaciones que puede tener un mismo host, ya sean orientadas a conexiones o comunicaciones sueltas. 
- Red: La capa de red es la encargada de tomar los segmentos que provee la capa de transporte y buscar el camino logico de saltos bajo los cuales estos segmentos pueden llegar a su destino. En el modelo TCP/IP, el protocolo IP es el unico que opera dentro de esta capa.
- Enlace: La capa de enlace se encarga de conectar estas direcciones logicas (IPs) en identificadores univocos de los dispositivos (MACs), para que estos dispositivos puedan comunicarse a traves de sus propios medios de comunicacion disponibles (WiFi, Ethernet, Bluetooth, etc.)


# Capa de Aplicacion
Dentro de la materia se ven X distintos protocolos de la capa de aplicacion, cuyas funcionalidades se pueden resumir de la siguiente manera:
1. HTTP: HyperText Tranfer Protocol, originalmente pensado para enviar tan solo texto, hoy es el dialecto por defecto de las comunicaciones a traves del internet. Manejando envio de codigo (js), estilos (css) y hasta en algunos casos envio de archivos. Es un protocolo sin orientacion a conexion
2. FTP: File Transfer Protocol, protocolo utilizado para enviar y recibir archivos. Actualmente se encuentra mayoritariamente deprecado, aunque es historicamente relevante. Orientado a conexion
3. DNS: Domain Name System, base de datos distribuida que contiene un diccionario replicado con distintas direcciones IP correspondientes a algunos nombres mnemotecnamente mas memorables.
4. SMTP/POP/IMAP: Simple Mail Transfer Protocol / ? / ?, diferentes protocolos que abarcan la tarea de enviar correo a traves de internet, con distintos enfoques sobre autoridad sobre los correos y su replicacion. 

## HTTP
El protocolo HTTP es actualmente el protocolo mas utilizado dentro de las comunicaciones en internet. Nacio para transferir hipertexto a traves del internet, pero hoy comprende la comunicacion por defecto de todos los recursos enviados alrededor de paginas web.
Es un protocolo manejado alrededor de solicitar recursos y recibirlos en una comunicacion a traves de requests y responses state-less, no orientados a la conexion, es decir, cada par request-response es independiente de sus previas y siguientes comunicaciones (hablando exclusivamente a nivel de especificacion del protocolo, a nivel logico algunas comunicaciones podrian ser dependientes de recursos de otras comunicaciones).
Las requests y responses se configuran a traves de "headers", pares clave-valor que el receptor de la comunicacion debe interpretar para tratar adecuadamente el contenido en el "body" de la comunicacion o responder con el recurso correcto.

El protocolo HTTP tiene diferentes versiones notables, con las siguientes diferencias:
- HTTP/1.0: El protocolo base, lo tomamos como referencia para las siguientes versiones
- HTTP/1.1: Cambia el valor por defecto del header connection-close a false, lo cual persiste la conexion TCP para reducir el overhead de los futuros pedidos
- HTTP/2: Principal mejora, agrega pipelining que permite que salgan multiples requests sin tener que esperar las respuestas secuencialmente. Ademas se cambia el formato de las comunicaciones de texto plano por una encondificacion binaria que utiliza pseudo-headers para reemplazar la linea de request/response, volviendo las comunicaciones mas eficientes
- HTTP/3: HTTP3 consta de cambios dentro de la implementacion para dejar de comunicarse por TCP para comunicarse a traves de QUIC, un protocolo de capa de transporte que funciona sobre UDP para proveer una comunicacion mas veloz, manteniendo la fiabilidad de la comunicacion

## FTP
El File Transfer Protocol es un protocolo que actualmente casi no se utiliza, consta de un protocolo formado para la transferencia de archivos a traves del internet. Cuenta con dos modos, el pasivo y el activo, donde::
>> Repasar

- El protocolo funciona a traves de TCP


## DNS
El Domain Name Service es un servicio provisto por el ICANN a traves de una base de datos distribuida que contiene diversos valores para configurar y encontrar servicios sobre direcciones IP correspondientes a nombres que actuan como aliases.
Este servicio se provee por una red de tablas con registros DNS en forma de arbol, que es recorrida con forma de grafo direccional a traves de punteros. El cliente hace una consulta recursiva a un _Resolver DNS_ que se encarga de hacer consultas iterativas
