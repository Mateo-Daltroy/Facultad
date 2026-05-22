/*
3- Se realizará un casting para un programa de TV. El casting durará a lo sumo 5 días y en
cada día se entrevistarán a 8 personas en distinto turno.
a) Simular el proceso de inscripción de personas al casting. A cada persona se le pide
nombre, DNI y edad y se la debe asignar en un día y turno de la siguiente manera: las
personas primero completan el primer día en turnos sucesivos, luego el segundo día y así
siguiendo. La inscripción finaliza al llegar una persona con nombre “ZZZ” o al cubrirse los
40 cupos de casting.
Una vez finalizada la inscripción:
b) Informar para cada día y turno asignado, el nombre de la persona a entrevistar.
NOTA: utilizar la clase Persona. Pensar en la estructura de datos a utilizar. Para comparar
Strings use el método equals.
 */
package tema2;

/**
 *
 * @author mateo
 */
import PaqueteLectura.Lector;
import PaqueteLectura.GeneradorAleatorio;

public class Ej03 {
    
    public static void main(String [] args){
        Persona [][] matriz = new Persona[5][8]; // Java inicializa en null
        int edad; int dni; int dimL = 0;
        int i = 0; int j = 0; int i2 = 0; int j2 = 0;
        System.out.print("Ingresar nombre: ");
        String nom = Lector.leerString();
        while ((dimL < 40) && !(nom.equals("ZZZ"))){
            edad = GeneradorAleatorio.generarInt(101);
            dni = GeneradorAleatorio.generarInt(501);
            matriz[i][j] = new Persona(nom, dni, edad);
            j++;
            if (j == 8) {
                j = 0;
                i++;
            }
            dimL++;
            if (dimL < 40){
                System.out.print("Ingresar nombre: ");
                nom = Lector.leerString();
            }
        }
        while ((i2 != i) || (j2 != j)){
            System.out.println("Dia " + (i2+1) + ", Turno " + (j2 + 1) + " tiene nombre: " + matriz[i2][j2].getNombre());
            j2++;
            if (j2 == 8) {
                j2 = 0;
                i2++;
            }
        }
    }
}
