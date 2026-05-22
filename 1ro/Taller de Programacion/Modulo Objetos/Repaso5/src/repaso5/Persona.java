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
public abstract class Persona {
    private int dni;
    private int ingreso;
    private double sueldoB;
    private String nom;

    public Persona(int dni, int ingreso, double sueldoB, String nom) {
        this.dni = dni;
        this.ingreso = ingreso;
        this.sueldoB = sueldoB;
        this.nom = nom;
    }
    
    public double sueldoACobrar(){
        double aux = this.sueldoB;
        if (this.ingreso <= 2004)
            aux += (aux * 0.1);
        return(aux);
    }
    
    public String toString(){
        String aux = this.nom + ", " + this.dni + ", " + this.sueldoACobrar() + "\n";
        return(aux);
    }
}
