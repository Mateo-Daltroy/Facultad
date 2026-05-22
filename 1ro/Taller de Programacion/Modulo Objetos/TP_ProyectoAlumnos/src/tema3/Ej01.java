/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tema3;

/**
 *
 * @author mateo
 */
import PaqueteLectura.Lector;

public class Ej01 {
    
    public static void main(String [] args){
        double [] vector = new double [3];
        String rell;
        String linea;
        Triangulo T;
        for (int i = 0; i < 3; i++){
            System.out.print("Ingresar lado " + (i + 1) + ": ");
            vector[i] = Lector.leerDouble();
        }
        System.out.print("Ingresar color de relleno: ");
        rell = Lector.leerString();
        System.out.print("Ingresar color de linea: ");
        linea = Lector.leerString();
        T = new Triangulo (vector[0], vector[1], vector[2], rell, linea);
        System.out.print(T);
    }
}
