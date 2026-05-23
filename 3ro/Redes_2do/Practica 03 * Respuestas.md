# Practica 3 - Capa de Aplicacion DNS

1. El DNS (Domain Name System) es una base de datos distribuida de aliases que cumplen el objetivo de permitirle a las personas escribir direcciones mas naturales y faciles de recordar que las IP

2.
  * Un root server es un servidor que contiene la raiz del internet (.) y es capaz de redirigir a todos los TLDs (Top Level Domain)
  * Un generic TLD son un subset de todos los TLDs que hay, particularmente los que no pertenecen a ninguna nacion o lenguaje. Los ejemplos mas comunes son com, net, org, biz e info

3.
  Una respuesta autoritativa es cualquier respuesta a una consulta que venga de un servidor que es "dueño" (responsable de mantener) del dato en si. siguiendo un ejemplo de dos consultas de un cliente a www.unlp.com:
  1. El cliente le hace una consulta recursiva al servidor local
  2. El servidor locla le hace una consulta iterativa al raiz
  3. El servidor raiz desponde con la direccoin del servidor .com (AUTORITATIVO)
  4. El servidor local le hace una consulta iterativa al TLD .com
  5. El servidor TLD responde la direccion del servidor .unlp (AUTORITATIVO)
  6. El servidor local le hace una consulta al servidor autoritativo .unlp
  7. El servidor autoritativo responde la IP del nombre de dominio (AUTORITATIVO)
  8. El servidor local le responde la cliente haciendole llegar la IP (NO AUTORITATIVO)
  9. EL cliente hace una segunda consulta por el mismo DNS
  10. El servidor local tiene el dato cacheado, por lo que responde inmediatamente (NO AUTORITATIVO)

4. Una consulta DNS recursiva (comunmente la comunicacion entre un cliente y un servidor local) tiene la caracteristica de que el cliente no resuelve las partes de la direccion el mismo, sino que es el receptor de la consulta DNS el cual se encarga de hacer multiples consultas iterativas (a los servidores raiz, TLD y autoritativos) para llegar a la direccion que contiene la IP deseada.

5. Un resolver o servidor local, es aquel que comunmente recibe la consulta DNS recursiva de parte del cliente y se encarga de "resolver" la direccion IP preguntando por los servidores DNS intermediarios en nombre del cliente.

6.
  a. "A" corresponde a Address, lo cual quiere decir que el tipo de dato que se esta guardando es la direccion IPv4 en Valor de el DNS cuyo nombre se encuentra en Nombre

  b. "MX" corresponde a Mail Exchange, una direccion IP que corresponde a un correo electronico

  c. "PTR" corresponde a Pointer, y actua de manera opuesta al Address, contiene en su nombre direcciones IPv4 con valores que corresponden con los nombres de direcciones DNS que fueron utilizadas

  d. "AAAA" corresponde tambien a Address, pero en este caso utilizando el protocolo IPv6, el cual necesita cuatro veces la cantidad de espacio

  e. "SRV" corresponde a Service, y se utiliza para establecer puertos de servicios.

  f. "NS" corresponde a Name Server, y apunta a cual es el nombre del host de un servidor DNS que contiene la direccion IP correspondiente a los hosts del dominio

  g. "CNAME" corresponde a Canonical Name, y se utiliza para establecer alias para un mismo nombre DNS.

  h. "SOA" corresponde a Start of Authority, este registro se utiliza para contener metadata relevante para el servidor DNS, como el responsable del mantenimiento, cuanto tiempo falta para que sea necesario otro refresh de data y hace cuanto fue el ultimo. Este es obligatorio de tener en todos los servidores, comunmente en el primer registro.

7. Un dominio suele tener mas de un servidor DNS por dos motivos principales:
  * Proteccion ante fallos: Si hay multiples servidores espejo de unos a otros es mucho mas facil mantener la integridad de los datos ante un error y tambien mantiene el sistema funcionand incluso si se cae uno o varios espejos del mismo servidor
  * Velocidad de acceso: Estos servidores DNS estan distribuidos alrededor del mundo con el objetivo de tambien reducir la latencia con la cual los peiddos DNS pueden transformarse en IPs

8. Los servidores de nombres autoritativos tienen dos versiones, primario y secundario
  - Primario: Almacena la copia principal de los regsitros DNS de un dominio
  - Secundario: Mantiene una copia de seguridad de los registros y sincroniza con el servidor primario para mayor disponibilidad (mediante la transferencia de zona)

9.
