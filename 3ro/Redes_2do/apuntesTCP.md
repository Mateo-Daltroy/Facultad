
# TCP Flow-Control
> [!note] Definicion
> El control de flujo TCP es un conjunto de algoritmos y protocolos utilizados para asegurarse
> de que la velocidad a la que llegan los datos no sobrecargue al receptor

* Cada host tiene un buffer de Recepcion _Rx_, y un buffer de Transferencia _Tx_, en una comunicacion entre dos hosts, el buffer Rx de uno deberia ser equivalente al buffer Tx del otro


# TCP Error Control
> [!note] Aclaraciones
> Dentro de las clases de control de errores en TCP, se abarcan distintos esquemas, de los cuales importan
> GBN (go back n) y SR (selective repeat)

## Elementos relevantes
- Buffer de emision
- Buffer de recepcion

## Nomenclatura
- RTT: Round Trip Time, el tiempo que le toma a un emisor de paquetes para recibir la confirmacion de un paquete
- BW: Bandwidth Digital, capacidad que se puede enviar en una comunicacion a la vez
- MSS: Max Segment Size, autodescriptivo

## Fuentes de error
- Perdida de datos/ack
- Duplicacion de datos/ack
- Corrupcion de datos/ack
- Desorden de datos/ack (debido a diferentes rutas que pueden tomar los paquetes relacionados, debido al control de congestion)

## Sliding window
> [!note] Definicion
> El sliding window es un modelo donde envio multiples paquetes a la vez antes de recibir confirmaciones,
> envio mensajes en base a un tamano variable llamado Ventana, y conforme voy recibiendo confirmaciones de
> segmentos voy moviendo la ventana (recibo confirmaciones de 3 segmentos, mi ventana se mueve 3 segmentos
> para adelante)

El sliding window tiene dos implementaciones:
1. Go Back N
2. Selective Repeat

### Go Back N
> Solo tolera envio de paquetes en orden
