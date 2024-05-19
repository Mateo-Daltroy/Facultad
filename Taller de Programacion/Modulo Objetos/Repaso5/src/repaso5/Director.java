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
public class Director extends Persona {
    private double viaticos;

    public Director(double viaticos, int dni, int ingreso, double sueldoB, String nom) {
        super(dni, ingreso, sueldoB, nom);
        this.viaticos = viaticos;
    }
    
    public double sueldoACobrar(){
        double aux = super.sueldoACobrar();
        aux += this.viaticos;
        return(aux);
    }
}
