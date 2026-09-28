
# Clase 2 - Servicios de AWS
- Amazon provee servicios dispositivos "deprecados" (hardware antiguo) por un precio mucho menor al que se presentan el resto de los servicios, cuando esos dispositivos tienen suficientes recursos, podemos obviar los servicios mas nuevos
- Amazon ofrece una calculadora de uso, donde podes estimar cuanto va a costar en base a cierto consumo que vos tenes que predefinir
- Amazon cobra en cuanto a storage tanto por el almacenamiento como por las operaciones de read (proporcional al espacio)


## Amazon EC2 (Elastic Cloud Compute)
- Servicio de amazon que permite generar instancias (maquinas virtuales), amazon te permite permiso de root por instancia, una firewall que configurar y libertad para instalar cualquier software
- Una vez configurada una imagen de amazon para la instancia, se puede guardar como una Amazon Machine Image (AMI) para instanciar rapidamente sin tener que configurar

### Instancias de Uso General
> Existen instancias llamadas T2 y T3, con diversos tamanos con distintos recursos, estos acumulan creditos
> de CPU, vale la pena fijarse sobre como funcionan mas a fondo


## Amazon Storage
1. EBS: Elastic Block System? Almacenamiento en bloque, funciona como un disco/pendrive/etc, cada disco se corresponde con una instancia de EC2
1. S3: Simple Storage Service, guarda por objetos y los vuelve disponibles a traves de buckets como una URL
1. EFS: Elastic File System, Almacenamiento a nivel recurso de red, como una NAS para tus instancias


## Amazon CloudWatch
> Monitorizacion de recursos AWS y aplicaciones, recopila y visualiza graficaente metricas de diferentes 
> servicios
- Uno puede armar reglas, metricas y alarmas que se activen automaticamente para generar, destruir y operar de distintas maneras sobre las instancias de los servicios que tenemos dentro de AWS


## IPs elasticas
> Cambios logicos de IPs que cambian el container que se corresponde con la IP elastica, manteniendo una
> continuidad de direccion IP sobre diferencias instancias e actualizaciones de cara a la red
