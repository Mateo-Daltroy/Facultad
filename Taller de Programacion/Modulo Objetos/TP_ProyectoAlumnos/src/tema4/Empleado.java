/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tema4;

/**
 *
 * @author mateo
 */
public abstract class Empleado {
    private String nom;
    private double sueldo;
    private int ant;

    public Empleado(String nom, double sueldo, int ant) {
        this.nom = nom;
        this.sueldo = sueldo;
        this.ant = ant;
    }
    
    public abstract double calcularEfectividad();
   
    public double calcularSueldoACobrar(){
        /* Preguntar si esto es correcto o seria mejor dejar esto como clase
        abstracta para garantizar que todos los empleados tengan la funcion
        (aun asi usaria este modulo bajo otro nombre?)
        */
        double aux;
        aux = this.getAnt() * 0.1;
        aux = this.getSueldo() + (this.getSueldo() * aux);
        return(aux);
    }
    
    public String toString(){
        String aux = "";
        aux += "Empleado " + this.getNom() + " con un sueldo a cobrar de "
                + this.calcularSueldoACobrar() + " y una efectividad de " + 
                this.calcularEfectividad();
        return (aux);
    }

    public int getAnt() {
        return ant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getSueldo() {
        return sueldo;
    }

    public void setSueldo(double sueldo) {
        this.sueldo = sueldo;
    }

    public void setAnt(int ant) {
        this.ant = ant;
    }
}
