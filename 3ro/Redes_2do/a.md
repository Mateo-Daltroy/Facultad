
// El 103 tiene un next hop distinto, podria sumarizar 102 con 100/23, haciendo un "agujero" dentro de la red para 103, pero me parece recordar que en la catedra no se tiende a hacer eso

| Destination | Mask | Next-Hop | Iface |
| :---: | :---: | :---: | :---: |
| 192.168.100.0 | /23 | 10.0.0.1 | eth0 |
| 192.168.102.0 | /24 | 10.0.0.1 | eth0 |
| 192.168.103.0 | /24 | 10.0.0.2 | eth1 |


// Primero sumarizas las primeras dos entradas, te queda 10.40.64.0/25, lo cual es sumarizable con 10.50.64.128/25

| Destination | Mask | Next-Hop | Iface |
| :---: | :---: | :---: | :---: |
| 10.50.64.0 | /24 | 172.16.1.5 | eth2 |





| Destination | Mask | Next-Hop | Iface |
| :---: | :---: | :---: | :---: |
| 172.16.0.0 | /24 | 192.168.255.1 | eth0 |
| 172.16.1.0 | /24 | 192.168.255.1 | eth0 |
| 172.16.2.0 | /24 | 192.168.255.1 | eth0 |
| 172.16.3.0 | /24 | 192.168.255.1 | eth0 |
| 172.16.4.0 | /24 | 10.10.10.2 | eth1 |
| 172.16.5.0 | /24 | 192.168.255.1 | eth0 |
| 172.16.6.0 | /24 | 192.168.255.1 | eth0 |
| 172.16.7.0 | /24 | 192.168.255.1 | eth0 |


// Hay dos maneras de sumarizar la tabla de arriba, podes hacer una sumarizacion que junte todas y hacerle una entrada mas especifica para 172.16.4.0 o solo sumarizar hasta 172.16.3.0 y otra de 172.16.6.0 a 172.16.7.0

| Destination | Mask | Next-Hop | Iface |
| :---: | :---: | :---: | :---: |
| 172.16.0.0 | /23 | 192.168.255.1 | eth0 |
| 172.16.3.0 | /24 | 192.168.255.1 | eth0 |
| 172.16.4.0 | /24 | 10.10.10.2 | eth1 |
| 172.16.5.0 | /24 | 192.168.255.1 | eth0 |
| 172.16.6.0 | /23 | 192.168.255.1 | eth0 |



| Destination | Mask | Next-Hop | Iface |
| :---: | :---: | :---: | :---: |
| 172.16.0.0 | /21 | 192.168.255.1 | eth0 |
| 172.16.4.0 | /24 | 10.10.10.2 | eth1 |


