DNS - sistema distribuido de forma jerarquica a lo largo de todo el mundo
  - Cumple la funcion de transofrmar nombres de dominio a direcciones IP
  - Servidor Local tiene distintos sinonimos - Servidor recursivo, resolver, DNS local

Servidores DNS que me faltaron:
  - Los servidores de nombres autoritativos tienen dos versiones, primario y secundario
    - Primario: Almacena la copia principal de los regsitros DNS de un dominio
    - Secundario: Mantiene una copia de seguridad de los registros y sincroniza con el servidor primario para mayor disponibilidad (mediante la transferencia de zona)
  - Open name servers: Servidores de DNS que funcionan como locales para cualquier cliente. Por ejemplo 8.8.8.8

- La respuesta es autoritativa solamente cuando la direccion IP llega de un servidor autoritativo (valga la redundancia), si pasa por un servidor local "pierde la autoritatividad"?
- No olvidarse el SOA en un servidor DNS

Registros DNS
SOA - Proporciona informacion sobre el servidor DNS de la zona
NS
A
AAAA
CNAME
MX
PTR
TXT
