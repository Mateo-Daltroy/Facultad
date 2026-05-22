/*
2- Escriba un programa que lea las alturas de los 15 jugadores de un equipo de
básquet y las almacene en un vector. Luego informe:
- la altura promedio
- la cantidad de jugadores con altura por encima del promedio
*/
package tema1;

//Paso 1: Importar la funcionalidad para lectura de datos
import PaqueteLectura.Lector;

public class Ej02Jugadores {

  
    public static void main(String[] args) {
        //Paso 2: Declarar la variable vector de double 
        //Paso 3: Crear el vector para 15 double 
        double[] vector = new double[15];
        //Paso 4: Declarar indice y variables auxiliares a usar
        int i;
        double tot = 0;
        double prom;
        //Paso 6: Ingresar 15 numeros (altura), cargarlos en el vector, ir calculando la suma de alturas
        for (i=0; i<15; i++){
            System.out.print("Altura del jugador " + (i+1) + ": ");
            vector[i] = Lector.leerDouble();
            tot+= vector[i];
        }
        //Paso 7: Calcular el promedio de alturas, informarlo
        System.out.println(tot);
        prom = (tot/15.0);
        System.out.print("El promedio de altura es " + prom);
        //Paso 8: Recorrer el vector calculando lo pedido (cant. alturas que están por encima del promedio)
        tot = 0;
        for (i=0; i<15; i++){
            if (vector[i] > prom)
                tot++;
        }
        //Paso 9: Informar la cantidad.
        System.out.println(" y " + tot + " jugadores la superan");
    }
    
}
