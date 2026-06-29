
Las comunicaciones de TCP siguen este flujo de vida (simplificado)

# Conexion
El establecimiento de una conexion TCP sigue un 3-way handshake, donde cada uno de los pasos se ve de la siguiente manera (asumiendo que el dispositivo A le hace un pedido de conexion a B):
1. A -> B: SYN, SEQ=ISN (Initial Sequence Number es opcional, en este ejemplo asumimos que comienza en 0) // A solicita conexion con B
2. B -> A: SYN, ACK=1, SEQ=ISN // B confirma la conexion con A, tambien asumimos un ISN de 0
3. A -> B: ACK=1, SEQ=1 // A confirma que recibio la confirmacion de B, este paso ya puede contener datos si se desea


# Comunicaciones
Una vez establecida la conexion, el caso feliz de un intercambio de datos (no se pierde ningun dato) se ve de la siguiente manera (siguiendo con A y B):
1. A -> B: SEQ=X, ACK=Y, LEN=Z  // Len es una abstraccion, representa la cantidad de bytes de contenido enviado
2. B -> A: SEQ=Y, ACK=X+Z, LEN=K // ACK=X+Z representa la confirmacion de que se recibieron los datos, si A no recibe este ACK despues de un tiempo, vuelve a enviarlo. LEN=K es opcional en este caso, pero B puede hacer piggybacking para enviar datos de respuesta de ser necesario


# Closing
El cierre puede suceder en 3 o 4 pasos, dependiendo de si el receptor del cierre quiere cerrar a la vez, sucede asi:

## 3-Way Close
1. A -> B: FIN // Quiero cerrar la conexion
2. B -> A: FIN, ACK // Perfecto, cerrando...
3. A -> B: ACK // Recibi tu cierre, cerrando yo tambien...

## 4-Way Close 
1. A -> B: FIN // Quiero cerrar la conexion
2. B -> A: ACK // Entendido, yo todavia tengo datos que enviarte
3. B -> A: SEQ=X, LEN=Y // Aca estan los datos que me faltan
4. A -> B: ACK=X+Y // Recibido
--- Se vuelve a repetir desde el paso 3 hasta que B ya no necesita enviar nada mas ---
5. B -> A: FIN // Ahora si, termine, cerrando...
6. A -> B: ACK // Entendido, cerrando...
