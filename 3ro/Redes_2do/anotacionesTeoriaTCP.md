
## Puertos
PDU = Protocol Data Unit

MUX/DEMUX conecta los puertos (proxess-to-process)

## TCP vs UDP
* TCP no es necesariamente siempre mejor que UDP, TCP garantiza transferencias sin perdida de paquetes a cambio de un protocolo mas pesado. Por el otro lado UDP es mas rapido, gracias a que no se encarga de asegurarse de que todos los paquetes lleguen, de manera que despues tendra que implementar el software el comportamiento en el caso de que falten paquetes

# UDP
* Protocolo minimalista, rapido como consecuencia
* PDU -> Datagrama
* Provee MUX/DEMUX y deteccion de algunos errores
* No requiere establecimiento de conexion
> Encuentra su mayor uso en aplicaciones de voz/streaming y transporte de transporte (QUIC)

### Headers Datagrama UDP
1. Source port (16 bits)
2. Destination port (16 bits)
3. Length (16 bits)
4. UDP Checksum (16 bits) (Opcional)
5. Data

# TCP
* Protocolo confiable, ordenado, con buffering
* PDU -> Semento
* Orientado a streams
* Provee MUX/DEMUX
* Incrementa Overhead por confiabilidad
* Requiere establecimiento de conexoin
> Utilizado mayormente en HTTP/SMTP/FTP/SSH


