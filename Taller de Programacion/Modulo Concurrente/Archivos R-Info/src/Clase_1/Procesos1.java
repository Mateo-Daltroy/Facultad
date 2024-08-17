/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clase_1;

import rInfo.Robot;

/**
 *
 * @author Alumno
 */
public abstract class Procesos1 extends Robot{

    public Procesos1(int id) {
        super(id);
    }
    
    public int juntarFlores(){
        int cant = 0;
        while (hayFlorEnLaEsquina()){
            tomarFlor();
            cant++;
        }
        return(cant);
    }
}
