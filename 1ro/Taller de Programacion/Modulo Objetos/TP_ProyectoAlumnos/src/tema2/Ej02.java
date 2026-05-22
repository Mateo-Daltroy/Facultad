/*
Utilizando la clase Persona. Realice un programa que almacene en un vector a lo sumo
15 personas. La información (nombre, DNI, edad) se debe generar aleatoriamente hasta
obtener edad 0. Luego de almacenar la información:
- Informe la cantidad de personas mayores de 65 años.
- Muestre la representación de la persona con menor DNI. 
*/
package tema2;

/**
 *
 * @author mateo
 */
import PaqueteLectura.GeneradorAleatorio;

public class Ej02 {
    
    public static void main(String [] args){
        Persona [] vector = new Persona [15]; // Java inicializa en null
        int dimL = 0;
        int edad = GeneradorAleatorio.generarInt(101);
        String nom;
        int dni;
        int mayores = 0;
        int menor = 500;
        while ((edad != 0) && (dimL < 15)){
            nom = GeneradorAleatorio.generarString(5);
            dni = GeneradorAleatorio.generarInt(500);
            vector[dimL] = new Persona(nom, dni, edad);
            dimL++;
            edad = GeneradorAleatorio.generarInt(101);
        }
        for (int i = 0; i < dimL; i++){
            System.out.println(vector[i].toString()); // Checkear si se guarda bien el modulo
            if (vector[i].getEdad() > 65)
                mayores++;
            if (vector[i].getDNI() < menor)
                menor = vector[i].getDNI();
        }
        System.out.println("Hay " + mayores + " mayores de 65.");
        System.out.println("El menor dni es: " + menor);
    }
}
