/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repaso6;

/**
 *
 * @author mateo
 */
import PaqueteLectura.Lector;

public class Implementacion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Torneo T;
        Goleador G;
        Fecha F;
        String nom; int dimFG; String equipo;
        int goles; int dimFF;
        System.out.print("Introducir Nombre del torneo: ");
        nom = Lector.leerString();
        System.out.print("Introducir Fechas del torneo: ");
        dimFF = Lector.leerInt();
        System.out.print("Introducir Goleadores por Fecha del torneo: ");
        dimFG = Lector.leerInt();
        T = new Torneo(dimFF, nom, dimFG);
        
        for (int i = 0; i < 5; i++){ // cantidad arbitraria elegida para testeo
            System.out.print("Introducir Nombre del Goleador: ");
            nom = Lector.leerString();
            System.out.print("Introducir Equipo del Goleador: ");
            equipo = Lector.leerString();
            System.out.print("Introducir Goles del Goleador: ");
            goles = Lector.leerInt();
            G = new Goleador(nom, equipo, goles);
            System.out.print("Introducir Fecha del Goleador: ");
            T.agregarGoleador(G, Lector.leerInt());
        }
        System.out.println(T.menosGoles(dimFF));
        System.out.print(T);
    }
    
}
