/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repaso4;

/**
 *
 * @author mateo
 */
import PaqueteLectura.GeneradorAleatorio;
import PaqueteLectura.Lector;

public class Implementacion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CoroHileras H;
        CoroSemi S;
        Director D;
        Corista C;
        String nom; 
        int dni; int edad; int aux;
        int filas; int columnas;
        GeneradorAleatorio.iniciar();
        System.out.print("Introducir nombre del coro: ");
        nom = Lector.leerString();
        System.out.print("Introducir dni del director del coro: ");
        dni = Lector.leerInt();
        System.out.print("Introducir edad del director del coro: ");
        edad = Lector.leerInt();
        System.out.print("Introducir antiguedad del director del coro: ");
        aux = Lector.leerInt();
        D = new Director(aux, dni, edad);
        System.out.print("Introducir cantidad de coristas en semicirculo: ");
        columnas = Lector.leerInt();

        S = new CoroSemi(columnas ,D, nom);
        
        for (int i = 0; i < columnas; i++){
            System.out.print("Introducir edad del corista: ");
            edad = Lector.leerInt();
            System.out.print("Introducir dni del corista: ");
            dni = Lector.leerInt();
            System.out.print("Introducir tono del corista: ");
            aux = Lector.leerInt();
            C = new Corista(aux, dni, edad);
            S.agregarCorista(C);
        }
        
        System.out.print("Introducir cantidad de hileras: ");
        filas = Lector.leerInt();
        
        H = new CoroHileras(filas, columnas, D, nom);
        
        for (int i = 0; i < (filas * columnas); i++){
            System.out.print("Introducir edad del corista: ");
            edad = Lector.leerInt();
            System.out.print("Introducir dni del corista: ");
            dni = Lector.leerInt();
            System.out.print("Introducir tono del corista: ");
            aux = Lector.leerInt();
            C = new Corista(aux, dni, edad);
            H.agregarCorista(C);
        }
        System.out.println(S.bienFormado());
        System.out.println(H.bienFormado());
        System.out.println(S);
        System.out.println(H);
    }
    
}
