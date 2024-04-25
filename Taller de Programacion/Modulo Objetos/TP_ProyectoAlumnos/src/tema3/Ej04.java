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
import PaqueteLectura.GeneradorAleatorio;

public class Ej04 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GeneradorAleatorio.iniciar();
        Hotel H;
        Persona P;
        int dimF; int cont = 0; String nom;
        int dni; int edad; int hab; double pre;
        System.out.print("Introducir cantidad de habitaciones del Hotel: ");
        dimF = Lector.leerInt();
        H = new Hotel(dimF);
        System.out.print("Introducir nombre del cliente: ");
        nom = Lector.leerString();
        while ((cont < dimF) && !(nom.equals("XXX"))){
            System.out.print("Ingresar dni del cliente: ");
            dni = Lector.leerInt();
            System.out.print("Ingresar edad del cliente: ");
            edad = Lector.leerInt();
            System.out.print("Ingresar habitacion del cliente: ");
            hab = Lector.leerInt();
            P = new Persona(nom, dni, edad);
            H.agregarCliente(P, hab);
            System.out.print("Ingresar nombre del cliente: ");
            nom = Lector.leerString();
        }
        System.out.println(H);
        System.out.println("Ingresar aumento de precio: ");
        pre = Lector.leerDouble();
        H.aumentarPrecios(pre);
        System.out.println(H);
    }
}
