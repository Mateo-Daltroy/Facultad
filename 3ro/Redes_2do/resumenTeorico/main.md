
# Modelo TCP/IP
El modelo tcp/ip es una manera de dividir logicamente las funciones que cumplen diversos protocolos que vuelven posible la comunicacion de distintos dispositivos, comunmente llamados hosts. Las capas del modelo TCP/IP son las siguientes 4 (diferente a las 8 capas que utiliza el modelo OSI):

## Aplicacion
La capa de aplicacion es con la que interactuan directamente las aplicaciones, su comunicacion es de proceso a proceso, abstrayendose de los puertos utilizando los servicios que le provee la capa de _Transporte_.

## Transporte
La capa de transporte es la responsable de dividir y juntar el mensaje en dimensiones manejables a traves de la capa de _Red_ (proceso conocido como multiplexacion y demultiplexacion). Ademas, si se asegura cierto control de flujo, errores y congestion, se hace en esta capa a traves del protocolo TCP. Esta capa se comunica process to process.

## Red
La capa de red provee un servicio de Best Effort a la capa superior, donde asegura que va a intentar hacer llegar el datagrama a su host destino, mas no asegura que este vaya a llegar. El protocolo IP (predominante en esta capa) busca el host a traves de hop by hop.

## Enlace
La capa de enlace establece la conexion entre la direccion logica provista por el protocolo de la capa de red con los dispositivos fisicos y sus metodos de comunicacion. Ethernet, WiFi y ARP son protocolos de esta capa.
