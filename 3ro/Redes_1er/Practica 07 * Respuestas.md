# Practica 7 - Capa de Red - Direccionamiento



1.
* La capa de red presta servicios a la capa de transporte sobre
* La PDU de la capa de red es el Datagrama
* 



2.
  El protocolo IP se considera un protocolo de mejor esfuerzo porque no garantiza la llegada de los datos



3.
  Una cadena IP esta compuesta por 32 bits, dicho esto, cada clase de red tiene a su disposicion una distinta cantidad de direcciones para utilizar dependiendo de su la definicion de su primer octeto:
  A.
    0XXXXXXX._24*X_
  * Las redes de clase A son aquellas que tienen un 0 en su bit mas significativo y utilizan los siguientes 7 bits para representar las diferetes subredes, por ende pueden tener 2^7 subredes y 2^24 (-2*#subredes utilizadas) hosts, representados por los bits restantes de los 32 totales

B.
  10XXXXXX.XXXXXXXX._16*X_
  * Las redes de clase B son aquellas que tienen la cadena 10 en sus bits mas significativos y usan los siguientes 14 bits para representar las diferentes redes, por ende pueden tener 2^14 subredes y 2^16 (-2*#su) hosts

C.
  110XXXXX.XXXXXXXX.XXXXXXXX._8*X_
  * Las redes de clase C son aquellas que tienen la cadena 110 en sus bits mas significativos y usan los siguientes 21 bits para representar las diferentes redes, por ende pueden tener 2^8 subredes y 2^8 (-2*#su) hosts

D.
  1110XXXX.XXXXXXXX.XXXXXXXX.XXXXXXXX
  * Las redes de clase D se utilizan para hacer multicast

E.
  1111XXXX.XXXXXXXX.XXXXXXXX.XXXXXXXX
  * Las redes de clase E son reservadas para investigacion y otros fines



4.
  Una subred es una definicion de una mascara agregada, llamada mascara de subred que se utiliza para poder asignar una cantidad de hosts menor a multiples redes, disminuyendo asi el desperdicio de direcciones IP.
  Si una empresa necesita 255 hosts, teniendo en cuenta que todos deben entrar en una misma red y que la primer y ultima direccion se reservan se puede ver que nos falta una direccion para poder guardarlo en una red de clase C, por ende tendriamos que utilizar una de clase B. Donde de repente nos encontramos con 65536 hosts, de los cuales desperdiciamos 65281.
  Las subredes nos permiten utilizar mascaras para generar hosts que solo incrementen en potencias directas de 2 en vez de las potencias 8/16/24, asegurando asi que se utilicen un mayor porcentaje de las direcciones IP disponibles



5.
  > ?



6.
A)
  Direccion         | Primer Oct | Clase
  172.16.58.223/26  -> 10101100 -> Clase B
  163.10.5.49/27    -> 10100011 -> Clase B
  128.10.1.0/23     -> 10000000 -> Clase B
  10.1.0.0/24       -> 00001010 -> Clase A
  8.40.11.179/12    -> 00001000 -> Clase A

B)
R = Red
S = Subred
H = Host

172.16.58.223/26    -> 10101100.00010000.00111010.11011111
  -> 26 bits routeo
  -> RRRRRRRR.RRRRRRRR.SSSSSSSS.SSHHHHHH
  -> 10101100.00010000.00111010.11011111
  -> 10101100.00010000.00111010.11 = Subred

163.10.5.49/27      -> 10100011.00001010.00000101.00110001
  -> 27 bits de routeo
  -> RRRRRRRR.RRRRRRRR.SSSSSSSS.SSSHHHHH
  -> 10100011.00001010.00000101.00110001
  -> 10100011.00001010.00000101.001 = Subred

128.10.1.0/23       -> 10000000.00010010.00000001.00000000
  -> 23 bits de routeo
  -> RRRRRRRR.RRRRRRRR.SSSSSSSH.HHHHHHHH
  -> 10000000.00010010.00000001.0000000
  -> 10000000.00010010.0000000 = Subred

10.1.0.0/24         -> 00001010.00000001.00000000.00000000
  -> 24 bits de routeo
  -> RRRRRRRR.RRRRRRRR.SSSSSSSS.HHHHHHHH
  -> 00001010.00000001.00000000.00000000
  -> 00001010.00000001.00000000 = Subred

8.40.11.179/12      -> 00001000.00101000.00001011.10110011
  -> 12 bits de routeo
  -> RRRRRRRR.SSSSHHHH.HHHHHHHH.HHHHHHHH
  -> 00001000.00101000.00001011.10110011
  -> 00001010.0000 = Subred

C)
  172.16.58.223/26  -> 32 - 26 = 6  -> 2^6 - 2
  163.10.5.49/27    -> 32 - 27 = 5  -> 2^5 - 2
  128.10.1.0/23     -> 32 - 23 = 9  -> 2^9 - 2
  10.1.0.0/24       -> 32 - 24 = 8  -> 2^8 - 2
  8.40.11.179/12    -> 32 - 12 = 20 -> 2^20 - 2

D)
172.16.58.223/26
  -> RRRRRRRR.RRRRRRRR.SSSSSSSS.SSHHHHHH
  -> 10101100.00010000.00111010.11111111 = Broadcast

163.10.5.49/27
  -> RRRRRRRR.RRRRRRRR.SSSSSSSS.SSSHHHHH
  -> 10100011.00001010.00000101.00111111 = Broadcast

128.10.1.0/23
  -> RRRRRRRR.RRRRRRRR.SSSSSSSH.HHHHHHHH
  -> 10000000.00010010.00000001.1111111 = Broadcast

10.1.0.0/24
  -> RRRRRRRR.RRRRRRRR.SSSSSSSS.HHHHHHHH
  -> 00001010.00000001.00000000.11111111 = Broadcast

8.40.11.179/12
  -> RRRRRRRR.SSSSHHHH.HHHHHHHH.HHHHHHHH
  -> 00001000.00101111.11111111.11111111 = Broadcast

E)
172.16.58.223/26
  -> RRRRRRRR.RRRRRRRR.SSSSSSSS.SSHHHHHH
  -> 10101100.00010000.00111010.11000001 = Minima direccion
  -> 10101100.00010000.00111010.11111110 = Maxima direccion

163.10.5.49/27
  -> RRRRRRRR.RRRRRRRR.SSSSSSSS.SSSHHHHH
  -> 10100011.00001010.00000101.00100001 = Minima direccion
  -> 10100011.00001010.00000101.00111110 = Maxima direccion

128.10.1.0/23
  -> RRRRRRRR.RRRRRRRR.SSSSSSSH.HHHHHHHH
  -> 10000000.00010010.00000001.0000001 = Minima direccion
  -> 10000000.00010010.00000001.1111110 = Maxima direccion

10.1.0.0/24
  -> RRRRRRRR.RRRRRRRR.SSSSSSSS.HHHHHHHH
  -> 00001010.00000001.00000000.00000001 = Minima direccion
  -> 00001010.00000001.00000000.11111110 = Maxima direccion

8.40.11.179/12
  -> RRRRRRRR.SSSSHHHH.HHHHHHHH.HHHHHHHH
  -> 00001000.00100000.00000000.00000001 = Minima direccion
  -> 00001000.00101111.11111111.11111110 = Maxima direccion



7)
128.50.10.0 -> 1000000.00110010.00001010.00000000

A)
  - No entiendo si la falta de un /n indica que no hay subnet, si no lo implica la respuesta es:
  No podemos saber si la direccion es de red o de host sin sbaer primero hasta donde llega la capa de subred
  - Si la falta de /N indica que no esta dividida en subredes:
  Sabemos que la direccion IP es de Clase B de manera que la direccion de host se compone por los ultimos 2 octetos. Esta cadena no es todos 0 o todos 1, lo cual nos indica que la direccion no es de default ni broadcast respectivamente, por lo que es una direccion de host

B)
  Clase B
  Mascara de clase:
  11111111.11111111.00000000.00000000

C)
  Cantidad de hosts posibles:
  2^16

D)
  Cantidad de bits necesarios para representar 513 subredes: 10

  Cantidad de subredes asignables: 10 -> 2^10 = 1024

  Cantidad de hosts por subred: 2^6 = 64 - 2 = 62

  Direccion de la subred 710 {
  direccion de subred 710 => Representacion BSS 709 => 1011000101

  1111111.11111111.00000000.00000000 => Mascara de clase
  1111111.11111111.11111111.11000000 => Mascara de subred
  1000000.00110010.10110001.01000000 => Direccion de subred 710
  1000000.00110010.10110001.01111111 => Direccion de broadcast 710



8)
  195.200.45.0/24 -> 11000011.11001000.00101100.00000000
  11000011.11001000.00101100.00000000 => Direccion de red
  11111111.11111111.11111111.11110000 => Mascara

  11000011.11001000.00101100.00000000 => Direccion de subred 1
  11000011.11001000.00101100.00010000 => Direccion de subred 2
  11000011.11001000.00101100.00100000 => Direccion de subred 3
  11000011.11001000.00101100.00110000 => Direccion de subred 4
  11000011.11001000.00101100.01000000 => Direccion de subred 5
  11000011.11001000.00101100.01010000 => Direccion de subred 6
  11000011.11001000.00101100.01100000 => Direccion de subred 7
  11000011.11001000.00101100.01110000 => Direccion de subred 8
  11000011.11001000.00101100.10000000 => Direccion de subred 9

  11000011.11001000.00101100.00000000 => Direccion de subred 1
  11000011.11001000.00101100.00001111 => Direccion de broadcast
  11000011.11001000.00101100.00000001 => Direccion asignable minima
  11000011.11001000.00101100.00001110 => Direccion asignable maxima
  Rango: 1-14



9)
A)
  La asignacion de direcciones IP esta en su mayoria correcta, hay dos direcciones que estan asignadas mal dado que estan fuera de rango.
  - El router A tiene eth1 asignado como 172.26.22.3, siendo que la subred corresponde a 172.26.22.0/30, sabemos que 172.26.22.3 esta asignado a la direccion de broadcast porque es la direccion asignable maxima
  - El router C tiene eth1 asignado como 172.17.10.17, pero siendo que 172.17.10.0/28, sabemos que la direccion de broadcast es 172.17.10.16,y la direccion asignada se pasa del rango

B)
  10.0.10.1/24 => red de clase A, 8 bits de clase, con 24 bits de routeo => 16 bits de subredes => 2^16 posibles subredes

C)
  Las redes que usa el router B para conectarse con el internet son publicas, todo el resto son privadas



10)
  El Class Inter Domain Routing es un metodo de organizacion de las direcciones IP, dado que utilizando las mascaras de subred las clases pierden mucha relevancia, se pueden ignorar de manera tal que multiples redes consecutivas se pueden agrupar sin mucha dificultad, minimizando el tamaño de las tablas de routeo.



11)
  198.10.0.0/22



12)
  200.56.168.0/21 ->
  11001000.00111000.10101000.00000000 <= Representacion binaria
  11111111.11111111.11111111.00000000 <= Mascara Default (?
  No entiendo mucho esto pero me parece que quiere decir que mi rango es:
  11001000.00111000.10101000.00000000 -
  11001000.00111000.10101111.00000000
  200.56.168.0/24 - 200.56.175.0/24

  Parece que la mascara default es la que le corresponderia por la clase?



13)
  Equivale a listar todas las direcciones de red de clase B, si.
  Para listar todas las redes de clase A utilizarias 0.0.0.0/1



14)
  Variable Length Subset Masking es una practica que se utiliza para darle a distintas direcciones IP distintos tamaños de mascara con el fin de que cada subred contenga la cantidad de hosts minimamente necesaria, en vez de que todas las subredes de una red se vean obligadas a tener la misma capacidad que la de mayor cantidad de hosts



15)
  El mecanismo es un algoritmo recursivo que consiste en:
  1. Asignar una subred del menor tamaño posible a la red con mayor cantidad de hosts
  2. Revisar si la subred restante es suficiente para satisfacer la cantidad de subredes que necesitan hosts
  3. De no serlo, repetir el proceso con la siguiente subred de mayor tamaño



16) 205.10.192.0/19
  A.
  No es posible asignar las subredes correspondientes a la topologia utilizando subnetting sin VLSM debido a que la red C cuenta con 1530 hosts, lo cual demanda que la subred se divida en 4 (101.11111010, 11 bits necesarios para representar 1530, 19 de red = 30, quedan 2 bits para subnetting), y siendo que los routers necesitan direcciones tambien, no es posible.

  B.
  Red C: 1530 => 101.11111010 =>
  11001101.00001010.11000000.00000000 - Direccion de Clase
  11111111.11111111.11100000.00000000 - Mascara de Red
  11111111.11111111.11111000.00000000 - Mascara de subred
  205.10.192.0 - 205.10.199.255 ? Red de 1530 hosts

  Red A: 128 => 10000000 =>
  11001101.00001010.11000000.00000000 - Direccion de Clase
  11111111.11111111.11111000.00000000 - Mascara de subred C
  11111111.11111111.11111111.00000000 - Nueva mascara subred A
  205.10.200.0 - 205.10.200.255 ? Red de 128 hosts

  Red B: 20 => 10100 =>
  11001101.00001010.11000000.00000000 - Direccion de Clase
  11111111.11111111.11111111.00000000 - Mascara de subred A
  11111111.11111111.11111111.11100000 - Nueva mascara subred B
  205.10.201.0 - 205.10.201.31 ? Red de 20 hosts

  Red D: 7 => 111 =>
  11001101.00001010.11000000.00000000 - Direccion de Clase
  11111111.11111111.11111111.11100000 - Mascara subred B
  11111111.11111111.11111111.11110000 - Nueva mascara subred D
  205.10.201.32 - 205.10.201.47 ? Red de 7 hosts

  Peer to Peer: 2 de 2 => 10 =>
  11001101.00001010.11000000.00000000 - Direccion de Clase
  11111111.11111111.11111111.11110000 - Mascara subred D
  11111111.11111111.11111111.11111100 - Nueva Mascara de subred P2P
  205.10.201.48 - 205.10.201.51 ? Red de 2 hosts

  C)
  11001101.00001010.11001001.00110100 - 205.10.201.52   - Base
  11001101.00001010.11011111.11111111 - 205.10.223.255  - Limite

  11001101.00001010.11001001.00110100 - 205.10.201.52   - Base
  11001101.00001010.11001001.00110111 - 205.10.201.55   - Limite CIDR1

  11001101.00001010.11001001.00111000 - 205.10.201.56   - Base
  11001101.00001010.11001001.00111111 - 205.10.201.63   - Limite CIDR2

  11001101.00001010.11001001.01000000 - 205.10.201.64   - Base
  11001101.00001010.11001001.01111111 - 205.10.201.127  - Limite CIDR3

  11001101.00001010.11001001.10000000 - 205.10.201.128  - Base
  11001101.00001010.11001001.11111111 - 205.10.201.255  - Base



17)
  ?



18)
  ?



19)
  ICMP es un protocolo de control utilizado para obtener informacion de diagnostico sobre los fallos en las comunicaciones que utilizan el protocolo IP

  A.
  I. Ping usa el tipo ECHO_REQUEST
  II. La respuesta de un ping es ECHO_RESPONSE

  B.
  Traceroute es un comando que manipula el campo TTL con el proposito de conseguir que cada gateway por el que pasa un paquete devuelva un TIME_EXCEEDED, de esta manera se pueden recibir las respuestas y listarlas para leer la ruta que sigue el paquete.

  C.
  Utiliza 30 saltos
  I. la opcion -n no resuelve nombres de host
  II. Los * aparecen cuando no se recive un TIME_EXCEEDED para una señal enviada

  D.
  No, no se puede confirmar cual de ellos toma un camino distinto

20)
  El bloque 127/8 se utiliza para hacer loopback, permita hacer busquedas realistas a traves de la red hacia tu propia computadora.

  En ambos casos cuando haces un ping a 127/8 te responde tu propia computadora

21)
  ifconfig y route son dos comandos obsoletos que ahora son reempazados con distintos parametros del comando ip
  - ip addr
  - ip link
  - ip -s link (para tener estadisticas)
  - ip route (para route)

