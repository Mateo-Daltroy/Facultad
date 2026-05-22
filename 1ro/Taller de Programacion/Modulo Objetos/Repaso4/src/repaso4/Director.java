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
public class Director extends Empleado {
    private int antiguedad;

    public Director(int antiguedad, int dni, int edad) {
        super(dni, edad);
        this.antiguedad = antiguedad;
    }

    public String toString() {
        String aux = "";
        aux += "Director con " + super.toString() + " y antiguedad: " + this.antiguedad + "\n";
        return(aux);
    }
}
