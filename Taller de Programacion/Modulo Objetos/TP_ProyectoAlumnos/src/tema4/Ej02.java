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

public class Ej02 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Jugador J;
        Entrenador E;
        String nom; int ant; double sueldo;
        int gol; int partido; int camp;
        
        System.out.print("Introducir nombre del Jugador: ");
        nom = Lector.leerString();
        System.out.print("Introducir antiguedad del Jugador: ");
        ant = Lector.leerInt();
        System.out.print("Introducir sueldo basico del Jugador: ");
        sueldo = Lector.leerDouble();
        System.out.print("Introducir partidos del Jugador: ");
        partido = Lector.leerInt();
        System.out.print("Introducir goles del Jugador: ");
        gol = Lector.leerInt();
        J = new Jugador(partido, gol, nom, sueldo, ant);
        
        System.out.print("Introducir nombre del Entrenador: ");
        nom = Lector.leerString();
        System.out.print("Introducir antiguedad del Entrenador: ");
        ant = Lector.leerInt();
        System.out.print("Introducir sueldo basico del Entrenador: ");
        sueldo = Lector.leerDouble();
        System.out.print("Introducir campeonatos ganados del Entrenador: ");
        camp = Lector.leerInt();
        E = new Entrenador(camp, nom, sueldo, ant);
        
        System.out.println(J);
        System.out.println(E);
    }
    
}
