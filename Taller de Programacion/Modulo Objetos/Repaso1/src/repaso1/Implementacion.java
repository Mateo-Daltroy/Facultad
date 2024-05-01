/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repaso1;

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
        Proyecto P;
        Investigador I;
        Subsidio S;
        int cod = GeneradorAleatorio.generarInt(15); 
        String nom = GeneradorAleatorio.generarString(5); 
        String director = GeneradorAleatorio.generarString(5);
        P = new Proyecto(nom, cod, director);
        for (int i = 0; i < 3; i++){
            System.out.print("Ingresar nombre del investigador " + (i+1) +": ");
            nom = Lector.leerString();
            String esp = GeneradorAleatorio.generarString(5);
            int categ = 1 + GeneradorAleatorio.generarInt(5);
            I = new Investigador(nom, categ, esp);
            P.agregarInvestigador(I);
            for (int j = 0; j < 2; j++){
                System.out.print("Insertar monto del subsidio: ");
                double monto = Lector.leerDouble();
                String motivo = GeneradorAleatorio.generarString(10);
                S = new Subsidio(monto, motivo);
                P.agregarSubsidio(i, S);
            }
        }
        System.out.print("Ingresar nombre del investigador al que otorgar subsidios: ");
        P.otorgarTodos(Lector.leerString());
        System.out.print(P);
    }
}
