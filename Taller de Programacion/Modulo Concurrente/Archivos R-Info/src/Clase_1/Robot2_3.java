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

public class Robot2_3 extends Procesos2 {

    public Robot2_3(int id) {
        super(id);
    }
    
    public void comenzar(){
        int cont;
        for (int i = 0; i < 2; i++)
            derecha();
        cont = recorrido();
        informar(this.getId() + " " + cont);
    }
}
