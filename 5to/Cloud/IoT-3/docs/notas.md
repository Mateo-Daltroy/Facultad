### Creando la instancia para NodeRED

- Creamos una instancia sin utilizar el script
- la tiramos para ahorrarnos el pasaje del archvio mediante ssh
- ahora la creamos con el script y nos dio error por limire de vcpu alcanzado
- eliminamos la instancia anterior y ahi funciono
- nos conectamos por ssh a la nueva instancia
- instalamos net-tools
- configuramos el security group con tcp puerto 1880 (NodeRED)

### Creando instancia SimuPi

- Al crearla nos volvio a tirar el problema de maximo de vcpu
- Entonces la creamos en sao pablo (antes estabamos en ohio), lo que requirio crear un nuevo key pair
- No podemos limitar el puerto 1883 al security group de la instancia de NodeRED debido a que estan en regiones diferentes por lo que se nos dijo que obviaramos este paso. En su lugar pusimos la subred correspondiente a la instancia de ohio. Notar que: en caso de apar la instancia de ohio el 1883 quedaria inaccesible
- bajamos el repo
- lo pusimos a correr
- probamos healt
- tunel ssh para poder levantar el simulador desde el localhost:5000 de nuestra maquina
- entramos a localhost:5000 y vemos que abre simupi

### Probando conexion

- probamos con mqqtin y debug en node red que la conexion entre nodered y simupi funcione

### Dashboard

- Se nos dijo de obviar el punto 7 por complejidad, que no era para la practica
