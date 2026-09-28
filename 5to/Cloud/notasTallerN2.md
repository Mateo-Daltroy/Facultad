
# Creando instancia
> A la hora de crear la instancia, los datos anteriores habian sido borrados asi que comenzamos a generar 
> la instancia desde 0

* Elegimos los campos especificados en el punto 1 del primer ejercicio
  * Creamos un nuevo key-pair
  * Creamos el nuevo security group, con el nombre correspondiente y las configuraciones por defecto


Referenciamos el documento del taller de la clase pasada para recordar el comando preciso para conectarnos a traves de ssh, pero tuvimos problemas a la hora de conectarnos. Nos referimos a la configuracion del security group de la instancia y encontramos que faltaba la regla para ssh (creemos que tal vez la borramos por accidente a la hora de crearla), por lo que la creamos y luego pudimos conectarnos sin problemas.


https://sololinux.es/configuracion-paso-a-paso-de-un-servidor-web-apache-en-linux/


Instalamos apache2, intentamos conectarnos a traves del browser, el cual no nos respondio porque no tenemos abierto el puerto 80 para conexiones HTTP. Al ver esto intentamos hacer un ping para checkear si funcionaba, el cual tampoco nos respondio porque no teniamos el puerto para ICMP configurado tampoco, con esto nos percatamos que faltaba configurar las reglas y procedimos a hacerlo.

Luego testeamos las conexiones de ICMP y HTTP, funcionaron.

Sobreescribimos el archivo HTML para personalizarlo

Dimos de baja la VM, la volvimos a levantar, checkeamos nuevamente los protocolos pero esta vez no funcionaron debido a que como la IP es dinamica, cambio y la IP anterior dejo de contestar

Generamos la IP Elastica y la configuramos con la instancia 1, probamos que todo funcione y estaba flama

Como sabiamos que ibamos a necesitar acceder a la nueva instancia por todos los mismos protocolos que la vm anterior, decidimos utilizar el mismo security group de la instancia 1. A la vez, usamos el mismo key-pair


Para modificar los index.html hubo que modificar los permisos usando el comando chmod 777.

Luego procedimos a re asociar la ip elastica que teniamos reservada de la instancia 1 a las 2.     

Nos dio un error porque intentamos re asociar la IP elastica de la instancia 1 (que seguia corriendo) a una instancia nueva, sin haber desasociado la primera.

Asociamos nuevmaente como corresponde y probamos los protocolos contra la misma IP estatica y ambos funcionaron lo unico que cambio fue que la instancia que nos contesto fue diferente   

Cambiamos el puerto del 80 al 8000, intentamos conectarnos, fallo e identificamos que el problema es que no tenemos el puerto 8000 expuesto en la VM. Despues usamos un curl desde la propia vm a nuestro puerto 8000 para asegurarnos de que el servicio funcionaba, ante la respuesta positiva, nos aseguramos que el servidor esta andando. 

Entonces modificamos la regla al grupo de seguridad a "Custom TCP" con el puerto 8000, una vez hecho este cambio, pudimos acceder a la pagina web desde un browser externo sin problemas.

Liberamos la IP elastica y terminamos las 2 instancias
