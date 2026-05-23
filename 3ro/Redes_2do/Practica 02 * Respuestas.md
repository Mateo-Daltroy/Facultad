
# Practica 2

1. Cual es la funcion de la capa aplicacion?
  * La funcion de la capa aplicacion es proveer la interfaz para solicitar/proveer el contenido del cuerpo del mensaje y determinar que tipo de mensaje se esta mandando

2. Si dos procesos deben comunicarse
  a. Como podrian hacerlo si estan en diferentes maquinas?
  * Los procesos se comunican enviandose mensajes utilizando algun protocolo que ambos sean capaces de entender a traves de un puerto en cada una de las maquinas.
  b. y si estan en la misma maquina, que alternativas existen?
  * Si dos procesos estan en la misma maquina pueden comunicarse a traves pipes, mensajes y memoria compartida.

3. Explique brevemente como es el modelo Cliente/Servidor. De un ejemplo de un sistema Cliente/Servidor en la "vida cotidiana" y un ejemplo de un sistema informatico que siga el modelo Cliente/Servidor. Conoce algun otro modelo de comunicacion?
  * El modelo cliente servidor consiste en establecer una relacion entre dos maquinas donde uno toma el rol de cliente, que solicita datos o envia datos con una solicitud sobre que se debe hacer con ellos y otra maquina se encarga de acceder a la base de datos y hacer los computos necesarios para satisfacer el pedido del cliente.
  Un sistema Cliente/Servidor en la vida cotidiana podria ser un pedido de comida en un restaurante donde se hace el pedido, el servidor toma el pedido, lo interpreta, selecciona la comida apropiada, la cocina y la entrega al cliente.
  Un sistema Cliente/Servidor informatico de uso muy cotidiano es google, donde el cliente hace un pedido de busqueda de una cosa X y el servidor hace el computo del search engine y retorna los resultados con mayor probabilidad de ser lo que el cliente estaba buscando.
  Otro modelo de comunicacion muy util es el P2P, peer to peer, donde todos los hosts cumplen a la vez funcionalidad de cliente y de servidor.

4. Describa la funcionalidad de la entidad generica "Agente de usuario" o "User Agent"
  * Un "User Agent" es el software, version y/o sistema operativo utilizado por el cliente.

5. Que son y en que se diferencian HTML y HTTP?
  * HTML y HTTP son dos cosas completamente distintas, HTML es un tipo de archivo que significa HyperText Markup Language utilizado por las paginas para renderizar el contenido de las paginas. HTTP es el Hyper-Text Transfer Protocol y es utilizado para comunicar las paginas hechas en HTML.

6. HTTP tiene definido un formato de mensaje para los requerimientos y las respuestas
  * Ayuda: apartado "Formato de mensaje HTTP" de Kurose).

  a. Que informacion de la capa de aplicacion nos indica si un mensaje es de requerimiento o de respuesta para HTTP? Como esta compuesta dicha informacion? Para que sirven las cabeceras?
  * Para diferenciar un requerimiento de una respuesta, se puede analizar la primera linea, esta se distingue en cada uno siendo llamados "linea de solicitud" y "linea de estado" respectivamente.
  > Linea de solicitud: <Metodo (GET/POST/HEAD/etc)> <Recurso (filename)> <HTTP version>
  > Linea de estado: <HTTP version> <Codigo (200/404/403)> <Mensaje (OK/Not Found/Access Denied)
  * Las cabeceras sirven para presentar metadata adicional util para la interpretacion del mensaje

  b. Cual es su formato? (Ayuda: https://developer.mozilla.org/es/docs/Web/HTTP/Reference/Headers)
  * Requerimiento:
  <Metodo> <Recurso> <HTTP Version>
  <Cabeceras>
  <Cuerpo>

  * Respuesta:
  <HTTP Version> <Codigo> <Mensaje>
  <Cabeceras>
  <Cuerpo>

  c. Suponga que desea enviar un requerimiento con la version de HTTP 1.1 desde curl/7.74.0 a un sitio de ejemplo como www.misitio.com para obtener el recurso /index.html. En base a lo indicado, que informacion deberia enviarse mediante encabezados? indique como quedaria el requerimiento
  * Requerimiento
  GET /index.html HTTP 1.1
  Host: www.misitio.com
  User-Agent: curl/7.74.0
  Connection: keep-alive (opcional, pero toma este valor por defecto)

  <Datos>

7. Utilizando la VM, abra una terminal e investigue sobre el comando curl. Analice para que sirven los siguientes parametros (-IHXs).
  * curl es una herramienta para transmitir/recibir datos desde una URL usando cualquiera de los muchos protocolos que soporta.
  * -I: Dentro de los protocolos HTTP, FTP y FILE hace un fetch de solo los headers
  * -H: Permite agregar un header extra a la request HTTP que se le hace a un server
  * -X: Utilizado para especificar una request a un server HTTP. Se puede utilizar seguido por GET/POST/UPDATE/etc
  * -s: Silent, no muestra progreso o errores, solo el output que pediste. Tambien muestra stdout a menos que lo redirigas.

8. Ejecute el comando curl sin ningun parametro adicional y acceda a www.redes.unlp.edu.ar. Luego responda:
  a. Cuantos requerimientos realizo y que recibio? Pruebe redirigiendo la salida (>) del comando curl a un archivo con extension html y abrirlo con un navegador
  * Se realizo un solo requerimiento y se recibio uno solo tambien?
  * Utilizando el comando sin ningun parametro no puedo saber cuantos requermientos fueron realizados y recibidos, pero con -v se puede ver que se realizan 5 requerimientos y se reciben 9. Redirigiendo la salida a un archivo .html y abriendolo con el navegador se puede ver una de bienvenida a la materia de redes.

  b. Como funcionan los atributos href de los tag link e img en html?
  * href define el path de la direccion/archivo al que te routea cuando haces click en el

  c. Para visualizar la pagina completa con imagenes como en un navegador, alcanza con realizar un unico requerimiento?
  * No, hace falta hacer un requerimiento por cada imagen/archivo css y js

  d. Cuantos requerimientos serian necesarios para obtener una pagina que tiene dos CSS, dos Javascript y tres imagenes? Diferencia como funcionaria un navegador respecto al comando curl ejecutado previamente.
  > Depende de la version HTTP2 puede hacer todo en un solo requerimiento, mientras que HTTP1.0 y 1.1 
  > tienen que hacer un request por recurso
  * 1 del html
  * 2 de css
  * 2 de JS
  * 3 de las imagenes
  * 8 en total
  El comando curl necesita que le especifiques que haga un request de los multiples datos mientras que el navegador lo hace por vos

9. Ejecute a continuacion los siguientes comandos
  curl -v -s www.redes.unlp.edu.ar > /dev/null
  curl -I -v -s www.redes.unlp.edu.ar

  a. Que diferencias nota entre cada uno?
  * Estos dos comandos imprimen lo mismo pero la diferencia radica en lo que hacen por detras, el comando "-v -s > /dev/null" hace un requerimiento GET de la direccion ingresada, imprime los datos del header (agregados gracias a la opcion -v) y envia todo el html a /dev/null (lo cual descarta el resultado). Por el otro lado, "-I -v -s" hace un requerimiento HEAD, el cual solicita solo la informacion del header.

  b. que ocurre si en el primer comando se quita la redireccion a /dev/null? Por que no es necesario en el segundo comando?
  * Si no se hace la redireccion a /dev/null, el primer comando muestra todo el html (cuerpo) del requerimiento mientras que no es necesario dentro del segundo porque el parametro -I se asegura de que solo haga un fetch por el header

  c. Cuantas cabeceras viajaron en el requerimiento? Y en la respuesta?
  * En el requerimiento viajaron 3 cabeceras
   .> Host
   .> User-Agent
   .> Accept
  * En la respuesta viajaron 7 cabeceras
   .> Date
   .> Server
   .> Last-Modified
   .> ETag
   .> Accept-Ranges
   .> Content-Length
   .> Content-Type

10. Que indica la cabecera date?
  * La cabecera date indica el la hora y la fecha en la que fue generado el pedido/respuesta y fue enviada

11. En HTTP/1.0, como sabe el cliente que y recibio todo el objeto solicitado de manera completa? Y en HTTP/1.1?
  * En HTTP/1.0, si los valores se dejan en su valor predeterminado, el cliente sabe que recibio todo el objeto cuando se cierra la conexion.
  * En HTTP/1.1, se utiliza principalmente el header Content-Length para saber el tamaño del archivo que se va a recibir.

12. Investigue los distintos tipos de codigos de retorno de un servidor web y su significado. Considere que los mismos se clasifican en categorias (2XX, 3XX, 4XX, 5XX)
  * 2XX: Codigos de exito
  * 3XX: Codigos de movimiento
  * 4XX: Codigos de error de cliente
  * 5XX: Codigos de error de servidor

13. Utilizando curl, realice un requerimiento con el metodo HEAD al sitio www.redes.unlp.edu.ar e indique:
  a. Que informacion brinda la primera linea de la respuesta?
  * La primera linea de la respuesta nos indica la version de HTTP del servidor (1.1), el codigo de estado (200) y su mensaje (OK).

  b. Cuantos encabezados muestra la respuesta?
  * La pagina muestra 7 encabezados

  c. Que servidor web esta sirviendo la pagina?
  * Apache/2.4.56 (Unix)

  d. ¿El acceso a la página solicitada fue exitoso o no?
  * El acceso a la pagina solicitada fue exitoso, visto por el codigo 200

  e. ¿Cuándo fue la última vez que se modificó la página?
  * La ultima vez que se modifico la pagina fue el 19 de marzo de 2023

  f. Solicite la página nuevamente con curl usando GET, pero esta vez indique que quiere obtenerla sólo si la misma fue modificada en una fecha posterior a la que efectivamente fue modificada. ¿Cómo lo hace? ¿Qué resultado obtuvo? ¿Puede explicar para qué sirve?
  * Hacer un curl GET para un archivo solo si fue modificado despues de cierta fecha se hace con el parametro "-z", seguido por una fecha (ej. "20 Apr 2024"). Retorna el codigo de error 304 Not Modified, sin ningun cuerpo, lo cual nos indica que el archivo no fue modificado desde la fecha que se solicita.

14. Utilizando curl, acceda al sitio www.redes.unlp.edu.ar/restringido/index.php y siga las instrucciones y las pistas que vaya recibiendo hasta obtener la respuesta final. Será de utilidad para resolver este ejercicio poder analizar tanto el contenido de cada página como los encabezados.
  * 9d4 etc. lo hice, creeme

15. Utilizando la VM, realice las siguientes pruebas:
  a. Ejecute el comando ’curl www.redes.unlp.edu.ar/extras/prueba-http-1-0.txt’ y copie la salida completa (incluyendo los dos saltos de línea del final).

  b. Desde la consola ejecute el comando telnet www.redes.unlp.edu.ar 80 y luego pegue el contenido que tiene almacenado en el portapapeles. ¿Qué ocurre luego de hacerlo?
  * Utilizar telnet para hacer el pedido get a "www.redes.unlp.edu.ar/http/HTTP-1.1/ HTTP/1.0" genera un pedido total con headers incluidos

  c. Repita el proceso anterior, pero copiando la salida del recurso /extras/prueba-http-1-1.txt. Verifique que debería poder pegar varias veces el mismo contenido sin tener que ejecutar el comando telnet nuevamente.

16. En base a lo obtenido en el ejercicio anterior, responda:
  a. ¿Qué está haciendo al ejecutar el comando telnet?
  * Al ejecutar el comando telnet se establece una conexion con el servidor, que espera que le mandes una request

  b. ¿Qué método HTTP utilizó? ¿Qué recurso solicitó?
  * 1.0 el primer pedido, 1.1 el segundo, solicitaron el recurso www.redes.unlp.edu.ar/http/HTTP-1.1

  c. ¿Qué diferencias notó entre los dos casos? ¿Puede explicar por qué?
  * Hay una diferencia en que telnet te permite realizar multiples pedidos sin cortar la conexion, esto se debe a que el estandar por defecto en HTTP/1.1 es Conection: Keep-Alive

  d. ¿Cuál de los dos casos le parece más eficiente? Piense en el ejercicio donde analizó la cantidad de requerimientos necesarios para obtener una página con estilos, javascripts e imágenes. El caso elegido, ¿puede traer asociado algún problema?
  * El segundo caso es mas eficiente que el segundo teniendo en cuenta que permite el uso de llamadas consecuentes sin cortar la TCP.

17. En el siguiente ejercicio veremos la diferencia entre los métodos POST y GET. Para ello, será necesario utilizar la VM y la herramienta Wireshark. Antes de iniciar considere:
  * Capture los paquetes utilizando la interfaz con IP 172.28.0.1. (Menú “Capture ->Options”. Luego seleccione la interfaz correspondiente y presione Start).
  * Para que el analizador de red sólo nos muestre los mensajes del protocolo http introduciremos la cadena ‘http’ (sin las comillas) en la ventana de especificación de filtros de visualización (display-filter). Si no hiciéramos esto veríamos todo el tráfico que es capaz de capturar nuestra placa de red. De los paquetes que son capturados, aquel que esté seleccionado será mostrado en forma detallada en la sección que está justo debajo. Como sólo estamos interesados en http ocultaremos toda la información que no es relevante para esta práctica (Información de trama, Ethernet, IP y TCP). Desplegar la información correspondiente al protocolo HTTP bajo la leyenda “Hypertext Transfer Protocol”.
  * Para borrar la cache del navegador, deberá ir al menú “Herramientas->Borrar historial reciente”. Alternativamente puede utilizar Ctrl+F5 en el navegador para forzar la petición HTTP evitando el uso de caché del navegador.
  * En caso de querer ver de forma simplificada el contenido de una comunicación http, utilice el botón derecho sobre un paquete HTTP perteneciente al flujo capturado y seleccione la opción Follow TCP Stream.

  a. Abra un navegador e ingrese a la URL: www.redes.unlp.edu.ar e ingrese al link en la sección “Capa de Aplicación” llamado “Métodos HTTP”. En la página mostrada se visualizan dos nuevos links llamados: Método GET y Método POST. Ambos muestran un formulario como el siguiente:

  b. Analice el código HTML

  c. Utilizando el analizador de paquetes Wireshark capture los paquetes enviados y recibidos al presionar el botón Enviar.

  d. ¿Qué diferencias detectó en los mensajes enviados por el cliente?
  * El mensaje enviado de GET tenia los datos enviados a traves del url mientras que el POST envia un HTML form dentro del cuerpo del mensaje

  e. ¿Observó alguna diferencia en el browser si se utiliza un mensaje u otro?
  * no (?

18. Investigue cuál es el principal uso que se le da a las cabeceras Set-Cookie y Cookie en HTTP y qué relación tienen con el funcionamiento del protocolo HTTP.
  * Mirar nota cookies obsidian

19. ¿Cuál es la diferencia entre un protocolo binario y uno basado en texto? ¿De qué tipo de protocolo se trata HTTP/1.0, HTTP/1.1 y HTTP/2?

20. Responder las siguientes preguntas:
a. ¿Qué función cumple la cabecera Host en HTTP 1.1? ¿Existía en HTTP 1.0? ¿Qué sucede en HTTP/2? (Ayuda:https://undertow.io/blog/2015/04/27/An-in-depth-overview-of-HTTP2.html para HTTP/2)

  b. En HTTP/1.1, ¿es correcto el siguiente requerimiento?
      GET /index.php HTTP/1.1
      User-Agent: curl/7.54.0
  * No, este requerimiento no es correcto porque le hace falta la cabecera "Host"

  c. ¿Cómo quedaría en HTTP/2 el siguiente pedido realizado en HTTP/1.1 si se está usando https?
      GET /index.php HTTP/1.1
      Host: www.info.unlp.edu.ar

## Ejercicio de Parcial

curl -X ?? www.redes.unlp.edu.ar/??
> HEAD /metodos/ HTTP/??
> Host: www.redes.unlp.edu.ar
> User-Agent: curl/7.54.0
< HTTP/?? 200 OK
< Server: nginx/1.4.6 (Ubuntu)
< Date: Wed, 31 Jan 2018 22:22:22 GMT
< Last-Modified: Sat, 20 Jan 2018 13:02:41 GMT
< Content-Type: text/html; charset=UTF-8
< Connection: close

a. ¿Qué versión de HTTP podría estar utilizando el servidor?
  - El servidor podria estar utilizando HTTP 1.1, debido a la llegada del Header "Connection: close"

b. ¿Qué método está utilizando? Dicho método, ¿retorna el recurso completo solicitado?
  - El metodo utilizado es HEAD, y solo retorna los headers, no retorna el body del mensaje

c. ¿Cuál es el recurso solicitado?
  - El recurso solicitado es /metodos

d. ¿El método funcionó correctamente?
  - El metodo funciono correctemente, dado el "200 OK"

e. Si la solicitud hubiera llevado un encabezado que diga:
    If-Modified-Since: Sat, 20 Jan 2018 13:02:41 GMT
¿Cuál habría sido la respuesta del servidor web? ¿Qué habría hecho el navegador en este caso?
  - El servidor web habria respondido con un 304 Not Modified, dado que el contenido no fue modificado desde esa fecha. Como consecuencia el navegador web sabria que no hace falta actualizar su pedido por lo cual puede utilizar la pagina que tiene guardada en la cache.
