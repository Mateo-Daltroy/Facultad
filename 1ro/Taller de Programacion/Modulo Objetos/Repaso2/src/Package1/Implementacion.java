/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Package1;

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
        GeneradorAleatorio.iniciar();
        Estacionamiento E;
        Auto A;
        String nom = GeneradorAleatorio.generarString(4);
        String direccion = GeneradorAleatorio.generarString(4); 
        String apertura = GeneradorAleatorio.generarString(4); 
        String cierre = GeneradorAleatorio.generarString(4); 
        int plazas = 3; 
        int pisos = 3;
        E = new Estacionamiento(nom, direccion, apertura, cierre, plazas, pisos);
        for (int i = 0; i < 6; i++){
            nom = GeneradorAleatorio.generarString(2);
            System.out.print("Ingresar patente del auto " + (i+1) + ": ");
            String pat = Lector.leerString();
            A = new Auto(pat, nom);
            System.out.print("Ingresar piso del auto " + (i+1) + ": ");
            int piso = Lector.leerInt();
            System.out.print("Ingresar plaza del auto " + (i+1) + ": ");
            int plaza = Lector.leerInt();
            E.agregarAuto(A, piso, plaza);
        }
        System.out.println(E.cantEnPlaza(1));
        System.out.println("Ingresar patente a buscar: ");
        System.out.println(E.buscarPat(Lector.leerString()));
        System.out.println(E);
    }
    
}
