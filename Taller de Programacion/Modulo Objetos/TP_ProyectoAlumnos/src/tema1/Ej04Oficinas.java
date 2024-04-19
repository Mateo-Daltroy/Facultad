/*
4- Un edificio de oficinas está conformado por 8 pisos (1..8) y 4 oficinas por piso
(1..4). Realice un programa que permita informar la cantidad de personas que 
concurrieron a cada oficina de cada piso. Para esto, simule la llegada de personas al 
edificio de la siguiente manera: a cada persona se le pide el nro. de piso y nro. de 
oficina a la cual quiere concurrir. La llegada de personas finaliza al indicar un nro. 
de piso 9. Al finalizar la llegada de personas, informe lo pedido.
 */
package tema1;

/**
 *
 * @author mateo
 */
import PaqueteLectura.Lector;

public class Ej04Oficinas {
    
    public static void main(String [] args){
        int[][] matriz = new int[4][8]; // Java inicializa en null
		int piso;
		int ofi;
		System.out.print("Introducir piso: ");
		piso = (Lector.leerInt() - 1);
		while (piso != 8) {
			System.out.print("Introducir Oficina: ");
			ofi = (Lector.leerInt() - 1);
			System.out.print("Introducir Cantidad: ");
			matriz[ofi][piso] = Lector.leerInt();
			System.out.print("Introducir piso: ");
			piso = (Lector.leerInt() - 1);
		}
		for (int i = 0; i < 4; i++){
			System.out.println("--------------------------------------------------------------------------------------");
			for (int j = 0; j < 8; j++){
				System.out.print("(" + (i+1) + ", " + (j+1) + ") " + matriz[i][j] + " | ");
			}
			System.out.println();
		}
    }
    
}
