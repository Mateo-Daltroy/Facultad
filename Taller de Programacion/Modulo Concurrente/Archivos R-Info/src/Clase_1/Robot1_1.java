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
public class Robot1_1 extends Procesos1 {

    public Robot1_1(int id) {
        super(id);
    }
    
    public void comenzar(){
        int f = 0;
        int esqVac = 0;
        while (posCa() < 100){
            if (hayFlorEnLaEsquina())
                f += juntarFlores();
            else {
                esqVac++;
            }
            mover();
        }
        if (hayFlorEnLaEsquina())
            f += juntarFlores();
        else {
            esqVac++;
        }
        informar("Encontre " + f + " flores y " + esqVac + " esquinas vacias");
    }
}
