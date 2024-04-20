/*
Realice un programa que cree un objeto persona con datos leídos desde teclado. Luego
muestre en consola la representación de ese objeto en formato String.
 */
package tema2;

/**
 *
 * @author mateo
 */
import PaqueteLectura.Lector;

public class Ej01 {
    
    public static void main(String [] args){
        Persona P;
        System.out.print("Introducir nombre de la persona: ");
        String nom = Lector.leerString();
        System.out.print("Introducir dni de la persona: ");
        int dni = Lector.leerInt();
        System.out.print("Introducir edad de la persona: ");
        int edad = Lector.leerInt();
        P = new Persona(nom, dni, edad);
        System.out.println(P.toString());
    }   
}
