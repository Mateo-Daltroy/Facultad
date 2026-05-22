/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clase_1;

/**
 *
 * @author Alumno
 */
import rInfo.*;

public class ImpE1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Definir Areas
        Area A1 = new AreaC(1,1,100,100);
        A1.agregarFlores(100);
        
        // Preparar Robots
        Robot r1 = new Robot1_1(1);
        A1.agregarRobot(r1);
        
        // Iniciar Programa
        r1.iniciar(1, 1);
    }
    
}
