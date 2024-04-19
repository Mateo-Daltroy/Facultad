/*
5- El dueño de un restaurante entrevista a cinco clientes y les pide que califiquen 
(con puntaje de 1 a 10) los siguientes aspectos: (0) Atención al cliente (1) Calidad 
de la comida (2) Precio (3) Ambiente.
Escriba un programa que lea desde teclado las calificaciones de los cinco clientes 
para cada uno de los aspectos y almacene la información en una estructura. Luego 
imprima la calificación promedio obtenida por cada aspecto
*/

package tema1;

import PaqueteLectura.Lector;

public class Ej05Restaurante {

    public static void main(String[] args) {
		int matriz [][] = new int [5][4]; // Java inicializa en null
		int i; 
		int j; 
		int tot = 0;
		double prom;
		for (i=0; i < 5; i++) {
			System.out.println("cliente " + (i+1));
			System.out.print("ingrese (1 al 10) Atención al cliente: ");
			matriz[i][0] = Lector.leerInt();
			System.out.print("ingrese (1 al 10) Calidad de la comida: ");
			matriz[i][1] = Lector.leerInt();
			System.out.print("ingrese (1 al 10) Precio: ");
			matriz[i][2] = Lector.leerInt();
			System.out.print("ingrese (1 al 10) Ambiente: ");
			matriz[i][3] = Lector.leerInt();
			}
		for (i = 0; i < 4; i++){
			for (j = 0; j < 5; j++){
				tot+= matriz[j][i];
			}
			prom = (tot / 5.0);
			System.out.println("El promedio de el aspecto " + i + " es " + prom);
			tot = 0;
		}
	}
}
