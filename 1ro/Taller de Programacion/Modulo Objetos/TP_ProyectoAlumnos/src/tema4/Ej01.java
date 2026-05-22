/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tema4;

/**
 *
 * @author mateo
 */
import PaqueteLectura.Lector;

public class Ej01 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Circulo C;
        Triangulo T;
        String rell; String linea; double l1;
        double l2; double l3; double r;
        System.out.print("Introducir lado 1 del triangulo: ");
        l1 = Lector.leerDouble();
        System.out.print("Introducir lado 2 del triangulo: ");
        l2 = Lector.leerDouble();
        System.out.print("Introducir lado 3 del triangulo: ");
        l3 = Lector.leerDouble();
        System.out.print("Introducir color de relleno del triangulo: ");
        rell = Lector.leerString();
        System.out.print("Introducir color de linea del triangulo: ");
        linea = Lector.leerString();
        T = new Triangulo(l1, l2, l3, rell, linea);
        System.out.print("Introducir radio del circulo: ");
        r = Lector.leerDouble();
        System.out.print("Introducir color de relleno del circulo: ");
        rell = Lector.leerString();
        System.out.print("Introducir color de linea del circulo: ");
        linea = Lector.leerString();
        C = new Circulo(r, rell, linea);
        System.out.println(T);
        System.out.println(C);
        C.despintar();
        T.despintar();
        System.out.println(T);
        System.out.println(C);
    }
    
}
