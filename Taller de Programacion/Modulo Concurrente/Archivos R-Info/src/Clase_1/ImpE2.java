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

public class ImpE2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Avenida Calle

        // Areas
        Area a1 = new AreaP(10, 10, 10, 19);
        Area a2 = new AreaP(10, 20, 19, 20);
        Area a3 = new AreaP(20, 11, 20, 20);
        Area a4 = new AreaP(11, 10, 20, 10);
        
        // Robots
        Robot r1 = new Robot2_1(1);
        Robot r2 = new Robot2_2(2);
        Robot r3 = new Robot2_3(3);
        Robot r4 = new Robot2_4(4);
        a1.agregarRobot(r1);
        a2.agregarRobot(r2);
        a3.agregarRobot(r3);
        a4.agregarRobot(r4);
        a1.agregarPapeles(3);
        a2.agregarPapeles(6);
        a3.agregarPapeles(10);
        a4.agregarPapeles(12);
        
        // Iniciar
        r1.iniciar(10, 10);
        r2.iniciar(10, 20);
        r3.iniciar(20, 20);
        r4.iniciar(20, 10);
    }
    
}
