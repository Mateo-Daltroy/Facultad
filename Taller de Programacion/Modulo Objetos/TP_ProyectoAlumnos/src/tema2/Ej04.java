/*
4- Sobre un nuevo programa, modifique el ejercicio anterior para considerar que:
a) Durante el proceso de inscripción se pida a cada persona sus datos (nombre, DNI, edad)
y el día en que se quiere presentar al casting. La persona debe ser inscripta en ese día, en el
siguiente turno disponible. En caso de no existir un turno en ese día, informe la situación.
La inscripción finaliza al llegar una persona con nombre “ZZZ” o al cubrirse los 40 cupos
de casting.
Una vez finalizada la inscripción:
b) Informar para cada día: la cantidad de inscriptos al casting ese día y el nombre de la
persona a entrevistar en cada turno asignado.
 */
package tema2;

/**
 *
 * @author mateo
 */
import PaqueteLectura.Lector;
import PaqueteLectura.GeneradorAleatorio;

public class Ej04 {
    
    public static void main(String [] args){
        Persona [][] matriz = new Persona[5][8]; // Java inicializa en null
        int []dimLV = new int[5];
        int dimL = 0; int i;
        String nom; int dni; int edad;
        int dia;
        for (i = 0; i < 5; i++){
            dimLV[i] = 0;
        }
        System.out.print("Ingresar nombre: ");
        nom = Lector.leerString();
        while ((dimL < 40) && !(nom.equals("ZZZ"))){
            dni = GeneradorAleatorio.generarInt(501);
            edad = GeneradorAleatorio.generarInt(101);
            System.out.print("Ingresar dia deseado: ");
            dia = (Lector.leerInt() - 1);
            while (dimLV[dia] == 8){
                System.out.print("Dia no disponible, ingresar otro: ");
                dia = (Lector.leerInt() - 1);
            }
            matriz[dia][dimLV[dia]] = new Persona(nom, dni, edad);
            dimLV[dia]++;
            dimL++;
            if (dimL < 40){
                System.out.print("Ingresar Nombre: ");
                nom = Lector.leerString();
            }
        }
        for (i = 0; i < 5; i++){
            for (int j = 0; j < dimLV[i]; j++){
                System.out.println("Dia " + (i+1) + ", Turno " + (j + 1) + " tiene nombre: " + matriz[i][j].getNombre());
            }
        }
    }
}
