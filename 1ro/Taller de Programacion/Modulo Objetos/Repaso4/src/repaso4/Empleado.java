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
public class Empleado {
    private int dni;
    private int edad;

    public Empleado(int dni, int edad) {
        this.dni = dni;
        this.edad = edad;
    }
    
    public String toString(){
        String aux = "";
        aux += "dni: " + this.dni + ", edad: " + this.edad;
        return(aux);
    }
}
