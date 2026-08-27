
# Template

| Destination | Mask | Gateway | Iface |
| :---: | :---: | :---: | :---: |
| - | - | - | - |
| 0.0.0.0 | /0 | - | - |


# Explicacion Practica

- Router A

| Destination | Mask | Gateway | Iface |
| :---: | :---: | :---: | :---: |
|172.26.22.0 | /30 | - | eth1 |
| 192.168.5.0 | /24 | - | eth0 |
| 191.26.145.0 | /24 | 172.26.22.1 | eth1 |
| 172.17.10.0 | /28 | 172.26.22.1 | eth1 |
| 10.0.10.0 | /24 | 172.26.22.1 | eth1 |
| 0.0.0.0 | /0 | 172.26.22.1 | eth1 |


- Router B

| Destination | Mask | Gateway | Iface |
| :---: | :---: | :---: | :---: |
| 191.26.145.0 | /24 | - | eth2|
| 172.26.22.0 | /30 | - | eth0 |
| 172.17.10.0 | /28 | - | eth1 |
| 192.168.5.0 | /24 | 172.26.22.2 | eth0 |
| 10.0.10.0 | /24 | 172.17.10.1 | eth1 |
| 0.0.0.0 | /0 | 191.26.145.1 | eth2 |


# Ejercicio 5
La tabla original de la imagen tiene los siguientes errores:
1. Le falta la entrada para la subred de 10.0.08/30
2. La entrada de 10.0.0.16/30 intenta usar el next-hop de 10.0.0.10, al cual Rtr-D deberia tener acceso, pero no lo tiene porque el salto no esta configurado
3. La entrada de 10.0.0.12 tiene la mascara de subred en el next-hop
4. La entrada de 205.10.128.0 tiene dos errores
  4.1. Dice 205.10.128.0 en vez de 205.10.0.128 (asumo que la intencion es apuntar a esa direccion, de no ser asi, esa entrada no deberia existir)
  4.2. El next-hop de la entrada es en realidad la direccion del Rtr-D dentro de la subred, next-hop deberia tener el valor de 10.0.0.1
5. La entrada de 205.20.0.193 tambien tiene dos errores
  5.1. Es una direccion de host, lo cual no esta necesariamente mal, pero a las direcciones de host se les asigna una mascara de /32 para direccionar a ellas explicitamente. Asumo que a lo que intentaba apuntar es a la direccion de red en lugar del host.
  5.2. Utiliza la salida de 10.0.0.1 a traves de eth5, cuando su next-hop deberia ser 10.0.0.5 a traves de eth0
6. El mismo error que la entrada de 10.0.0.16, utiliza un next hop que es correcto pero no esta configurado dentro de esta tabla.
7. Falta el default gateway para resolver las direcciones no encontradas

Aca esta la tabla escrita correctamente

| Destination | Mask | Gateway | Iface |
| :---: | :---: | :---: | :---: |
| 153.10.20.128 | /27 | - | eth1 |
| 10.0.0.4 | /30 | - | eth0 |
| 10.0.0.0 | /30 | - | eth5 |
| 10.0.0.8 | /30 | - | eth3 |
| 10.0.0.12 | /30 | 10.0.0.5 | eth0 |
| 10.0.0.16 | /30 | 10.0.0.10 | eth3 |
| 205.10.0.128 | /25 | 10.0.0.1 | eth5 |
| 205.20.0.128 | /26 | 10.0.0.5 | eth0 |
| 205.20.0.192 | /26 | 10.0.0.5 | eth0 |
| 163.10.5.64 | /27 | 10.0.0.10 | eth3 |
| 0.0.0.0 | /0 | 10.0.0.10 | eth3 |


## Ej C
> Teniendo en cuenta que esto construye sobre la configuracion en la que la salida al 
> default gateway sucede a traves del router C, lo que sucederia es que se generaria
> un bucle infinito donde le router C y D se envian los paquetes entre ellos hasta
> que le termine el TTL al paquete

## Ej D 
> Si, es posible aplicar sumarizacion en la tabla del router D, se pueden sumarizar 
> las entradas de 205.20.0.128/26 y 205.20.0.192/26 en una sola entrada debido a que 
> se cumplen las siguientes condiciones:
1. Comparten mismo gateway
2. Comparten mismo Iface 
3. Las direcciones son consecutivas

De esta manera, despues de hacer un AND sobre las direcciones podemos sumarizar esas dos entradas a una unica entrada:
  205.20.0.128  /25  10.0.0.5  eth0

## Ej E
> La sumarizacion aplicada en el punto anterior no se podria aplicar en el router B 
> debido a que estas entradas no comparten la misma Iface dentro de este router (eth0 
> y eth2).

## Ej F

| Destination | Mask | Gateway | Iface |
| :---: | :---: | :---: | :---: |
| 205.20.0.128 | /26 | - | eth2 |
| 205.20.0.192 | /26 | - | eth0 |
| 10.0.0.4 | /30 | - | eth1 |
| 10.0.0.12 | /30 | - | eth3 |
| 153.10.20.128 | /27 | 10.0.0.6 | eth1 |
| 10.0.0.8 | /30 | 10.0.0.6 | eth1 |
| 10.0.0.0 | /30 | 10.0.0.13 | eth3 |
| 10.0.0.16 | /30 | 10.0.0.13 | eth3 |
| 205.10.0.128 | /25 | 10.0.0.13 | eth3 |
| 163.10.5.64 | /27 | 10.0.0.13 | eth3 |
| 0.0.0.0 | /0 | 10.0.0.13 | eth3 |


## Ej G
> Es posible restablecer el acceso a internet a traves de cambiar el default gateway 
> de ISP-2 a rtr-A



# Ejercicio 6

## A
Sabemos que el mensaje quiere llegar desde 10.0.5.20 hasta 10.0.7.20 y volver, para hacer eso sigue el siguiente trayecto:
1. El paquete llega desde pc-b a router2, buscando 10.0.7.20
2. 10.0.7.20 no se corresponde con ninguna de las entradas de router2, por lo que envia el paquete a router1 a traves de su default gateway
3. 10.0.7.20 coincide con la entrada de 10.0.0.0/16 (anteultima), por lo que llega a router3 a traves de eth1
4. Desde router3, el mensaje se envia al switch por eth2 y llega a pc-c
5. pc-c envia el mensaje de vuelta con destino a 10.0.5.20, el cual llega a router3
6. router3 no tiene una entrada para 10.0.5.20, por lo que se envia a router4 por default gateway
7. router4 tiene una entrada para 10.0.5.20 (la ultima), por lo que se envia a router2 por eth0
8. router2 envia el mensaje al switch por eth2 y este llega a pc-b

## B
Igual que el A, pero haciendo la vuelta primero y luego la ida

## C
Voy a escribirlo de manera simplificada 
1. pc-c => router3
2. router3 => router4 (default gateway)
3. router4 => descarta, no tiene ninguna entrada que lo lleve ahi

##
1. pc-b => router2
2. router2 => router1 (default gateway)
3. router1 => router2 (default gateway)
4. Repetir paso 2, hasta que se termine el TTL





# Ejercicio XX

## A
> Ilustrando solo las conexiones incompletas

PC-A
192.168.1.2:49273 <=> 190.50.10.63:80  // PC-A <-> SRV-A
192.168.1.2:37484 <=> 190.50.10.63:25  // PC-A <-> SRV-A
192.168.1.2:51238 <=> 190.50.10.81:8080  // PC-A <-> SRV-B

PC-B
192.168.1.3:52734 <=> 190.50.10.81:8081  // PC-B <-> SRV-B
192.168.1.3:39275 <=> 190.50.10.81:8080  // PC-B <-> SRV-B

RTR-1
192.168.1.2:51238 <=> 205.20.0.29:16345



# Ejercicios de repaso

## Ejercicio 12

### Red C y D
Teniendo en cuenta que queresmos desperdiciar la menor cantidad de redes posibles y que las redes C y D tienen que ser publicas, usamos el bloque 224.10.0.64/26 y le hacemos subnetting para proveer las direcciones necesarias.

Siguiendo la anteultima regla, comenzamos con la red D:
La red D tiene 16 hosts, por lo cual contando la direccion de red y la de broadcast, necesitamos espacio para 18 hosts. Eso se comprende dentro de 5 bits (32 hosts maximo), de esa manera le asignamos a la red D la subred: 200.30.55.64/27

La red C la vamos a crear usando parte del restante del subnetting anterior, siendo que la red C solo necesita 16 direcciones en total, podemos direccionar todos los hosts con tan solo 4 bits. Asignamos consecutivamente a la red D, para ahorrar espacio y facilitar la sumarizacion de rutas: 200.30.55.96/28

### Red A y B
Respetando el principio de desperdiciar la menor cantidad de IP posibles y teniendo en cuenta que la estipulacion de las redes que deben ser publicas no incluye la A y B, es conveniente asignarles redes privadas.

La red A necesita 7 bits para direccionar 100 hosts, la unica red privada que dispone de esa capacidad en la tabla es 192.168.10.0/24. Entonces le asignamos a la red A la subred: 192.168.10.0/25

La red B tambien necesita 7 bits para direccionar 70 hosts, por lo que le asignamos el restante del bloque, usandolo completamente. La subred de B es: 192.168.10.128/25

### Point to Point
Por ultimo hay una conexion entre el RouterC y el RouterD que necesita de una red, viendo el patron que tienen las point to point de utilizar las direcciones privadas de 10.10.10.0/27, usamos la consecutiva a la mayor red P2P asignada. En este caso la conexion RouterE/C usa hasta la IP 10.10.10.15 como broadcast, por lo que le asignamos al P2P de RouterC/D la subred 10.10.10.16/30

### Red de routers
Hay una red no marcada a la cual le falta una asignacion, que es la red de los routers. Esta tiene 3 hosts (cada interfaz de router debe tener su propia direccion IP), por lo que 3 bits son necesarios teniendo en cuenta broadcast + dir de red. 
Seguimos con el direccionamiento sobre 10.10.10.0/27. Usamos el siguiente multiplo de 8 libre para direccionar: 10.10.10.24/29



## Ejercicio 13
- Red A (todos tienen la misma mascara /25)
  - RouterA (eth2): 192.168.10.1  
  - PC-A: 192.168.10.2
  - PC-B: 192.168.10.3
  - PC-C: 192.168.10.4
  - PC-D: 192.168.10.5
- Red B (todos tienen la misma mascara /25)
  - RouterB (eth0): 192.168.10.129
  - PC-E: 192.168.10.130
- Red C (todos tienen la misma mascara /28)
  - RouterC (eth2): 200.30.55.97
  - MailServer: 200.30.55.98
  - WebServer: 200.30.55.99
- Red D (todos tienen la misma mascara /27)
  - RouterD (eth1): 200.30.55.65
  - WebServer2: 200.30.55.66
  - DNSResolver: 200.30.55.67
- Red de Routers (todos tienen la misma mascara /29)
  - RouterA (eth0): 10.10.10.25 
  - RouterB (eth1): 10.10.10.26  
  - RouterE (eth0): 10.10.10.27
- Red Point to Point (todos tienen la misma mascara /30)
  - RouterC (eth1): 10.10.10.17
  - RouterD (eth1): 10.10.10.18

## Ejercicio 14
- Red A - 192.168.10.0/25
- Red B - 192.168.10.128/25 
- Red C - 200.30.55.96/28
- Red D - 200.30.55.64/27

> Router E

| Destination | Mask | Next-Hop | Iface |
| :---: | :---: | :---: | :---: |
| 10.10.10.24 | /29 | - | eth0 |
| 10.10.10.4 | /30 | - | eth3 |
| 10.10.10.12 | /30 | - | eth1 |
| 10.10.10.0 | /30 | - | eth2 |
| 192.168.10.0 | /25 | 10.10.10.25 | eth0 |
| 192.168.10.128 | /25 | 10.10.10.26 | eth0 |
| 200.30.55.96 | /28 | 10.10.10.14 | eth1 |
| 200.30.55.64 | /27 | 10.10.10.2 | eth2 |
| 10.10.10.16 | /30 | 10.10.10.14 | eth1 |
| 10.10.10.8 | /30 | 10.10.10.6 | eth3 |
| 172.16.0.1 | /24 | 10.10.10.6 | eth3 |
| 0.0.0.0 | /0 | 10.10.10.6 | eth3 |



> Router E Sumarizado

| Destination | Mask | Next-Hop | Iface |
| :---: | :---: | :---: | :---: |
| 10.10.10.24 | /29 | - | eth0 |
| 10.10.10.12 | /30 | - | eth1 |
| 10.10.10.4 | /30 | - | eth3 |
| 10.10.10.0 | /30 | - | eth2 |
| 10.10.10.8 | /30 | 10.10.10.14 | eth1 |
| 10.10.10.16 | /30 | 10.10.10.14 | eth1 |
| 192.168.10.0 | /25 | 10.10.10.25 | eth0 |
| 192.168.10.128 | /25 | 10.10.10.26 | eth0 |
| 200.30.55.96 | /28 | 10.10.10.14 | eth1 |
| 200.30.55.64 | /27 | 10.10.10.2 | eth2 |
| 172.16.0.1 | /24 | 10.10.10.6 | eth3 |
| 0.0.0.0 | /0 | 10.10.10.6 | eth3 |



> Router BORDER

| Destination | Mask | Next-Hop | Iface |
| :---: | :---: | :---: | :---: |
| 172.16.0.1 | /24 | - | eth1 |
| 10.10.10.4 | /30 | - | eth2 |
| 10.10.10.8 | /30 | - | eth0 |
| 10.10.10.24 | /29 | 10.10.10.5 | eth2 |
| 10.10.10.12 | /30 | 10.10.10.9 | eth0 |
| 10.10.10.16 | /30 | 10.10.10.9 | eth0 |
| 10.10.10.0 | /30 | 10.10.10.5 | eth2 |
| 192.168.10.0 | /25 | 10.10.10.5 | eth2 |
| 192.168.10.128 | /25 | 10.10.10.5 | eth2 |
| 200.30.55.96 | /28 | 10.10.10.9 | eth0 |
| 200.30.55.64 | /27 | 10.10.10.9 | eth0 |
| 0.0.0.0 | /0 | 172.16.0.1 | eth1 |



> Router BORDER Sumarizado

| Destination | Mask | Next-Hop | Iface |
| :---: | :---: | :---: | :---: |
| 172.16.0.1 | /24 | - | eth1 |
| 10.10.10.4 | /30 | - | eth2 |
| 10.10.10.8 | /30 | - | eth0 |
| 200.30.55.64 | /26 | 10.10.10.9 | eth0 |
| 10.10.10.0 | /27 | 10.10.10.9 | eth0 |
| 10.10.10.0 | /30 | 10.10.10.5 | eth2 |
| 10.10.10.24 | /29 | 10.10.10.5 | eth2 |
| 192.168.10.0 | /24 | 10.10.10.5 | eth2 |
| 0.0.0.0 | /0 | 172.16.0.1 | eth1 |


