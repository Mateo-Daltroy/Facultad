/*
3- Escriba un programa que defina una matriz de enteros de tamaño 5x5. Inicialice
la matriz con números aleatorios entre 0 y 30.
Luego realice las siguientes operaciones:
- Mostrar el contenido de la matriz en consola.
- Calcular e informar la suma de los elementos de la fila 1
- Generar un vector de 5 posiciones donde cada posición j contiene la suma
de los elementos de la columna j de la matriz. Luego, imprima el vector.
- Lea un valor entero e indique si se encuentra o no en la matriz. En caso de
encontrarse indique su ubicación (fila y columna) en caso contrario
imprima “No se encontró el elemento”.
 */
package tema1;

//Paso 1. importar la funcionalidad para generar datos aleatorios
import PaqueteLectura.GeneradorAleatorio;
import PaqueteLectura.Lector;

public class Ej03Matrices {

    public static void main(String[] args) {
        //Paso 2. iniciar el generador aleatorio
        GeneradorAleatorio.iniciar();
        int i;
        int j;
        int tot = 0;
        int busq;
        boolean encontrado = false;
        int [] vector = new int[5];
        //Paso 3. definir la matriz de enteros de 5x5 e iniciarla con nros. aleatorios 
        int[][] matriz = new int[5][5];
        for (i = 0; i < 5; i++){
            for (j = 0; j < 5; j++){
                matriz[i][j] = GeneradorAleatorio.generarInt(31);
            }
        }
        //Paso 4. mostrar el contenido de la matriz en consola
         for (i = 0; i < 5; i++){
             System.out.println("---------------------------------------------------------------------");
            for (j = 0; j < 5; j++){
                System.out.print("("+ i + ", " +  j + ")" + matriz[i][j]);
                System.out.print(" | ");
            }
            System.out.println();
        }
        //Paso 5. calcular e informar la suma de los elementos de la fila 1
        for (i = 0; i < 5; i++){
            tot+= matriz[0][i];
        }
        System.out.println("La primera fila tiene un total de: " + tot);
        //Paso 6. generar un vector de 5 posiciones donde cada posición j contiene la suma de los elementos de la columna j de la matriz. 
        //        Luego, imprima el vector.
        for (i = 0; i < 5; i++)
            vector[i] = 0;
        for (i = 0; i < 5; i++){
            for (j = 0; j < 5; j++){
                vector[i] += matriz[j][i];
            }
        }
        for (i = 0; i < 5; i++)
            System.out.print(vector[i] + ", ");
        //Paso 7. lea un valor entero e indique si se encuentra o no en la matriz. En caso de encontrarse indique su ubicación (fila y columna)
        //   y en caso contrario imprima "No se encontró el elemento".
        busq = Lector.leerInt();
        i = -1;
        j = 0;
        while ((encontrado == false) && (i < 5)){
            i++;
            while ((encontrado == false) && (j < 5)){
                if (matriz[i][j] == busq){
                    encontrado = true;
                } else {
                    j++;
                }
            }
        }
        if (encontrado){
            System.out.println("(" + i + ", " + j + ")");
        } else{
            System.out.println("No se encontro el elemento");
        }
    }
}
