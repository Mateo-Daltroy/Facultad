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
import rInfo.Robot;

public abstract class Procesos2 extends Robot{

    public Procesos2(int id) {
        super(id);
    }
    
    public int juntarPap(){
        int cont = 0;
        while (hayPapelEnLaEsquina()){
            tomarPapel();
            cont++;
        }
        return(cont);
    }
    
    public int recorrido(){
        int cont = 0;
        for(int i = 0; i < 9; i++){
            cont += juntarPap();
            mover();
        }
        cont += juntarPap();
        return(cont);
    }
}
