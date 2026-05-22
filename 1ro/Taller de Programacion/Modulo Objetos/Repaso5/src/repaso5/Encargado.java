/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repaso5;

/**
 *
 * @author mateo
 */
public class Encargado extends Persona {
    private int cantEmp;

    public Encargado(int cantEmp, int dni, int ingreso, double sueldoB, String nom) {
        super(dni, ingreso, sueldoB, nom);
        this.cantEmp = cantEmp;
    }
    
    public double sueldoACobrar(){
        double aux = super.sueldoACobrar();
        aux += (this.cantEmp * 1000);
        return(aux);
    }
}
