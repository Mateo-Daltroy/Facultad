/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repaso4;

/**
 *
 * @author mateo
 */
public class Corista extends Empleado {
    private int tono;

    public Corista(int tono, int dni, int edad) {
        super(dni, edad);
        this.tono = tono;
    }

    public int getTono() {
        return tono;
    }
    
    public String toString(){
        String aux = "";
        aux += "Corista " + super.toString() + " y tono fundamental " + this.getTono() + "\n";
        return(aux);
    }
}
