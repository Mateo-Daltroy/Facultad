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
public class Jugador extends Empleado{
    private int partidos;
    private int goles;

    public Jugador(int partidos, int goles, String nom, double sueldo, int ant) {
        super(nom, sueldo, ant);
        this.partidos = partidos;
        this.goles = goles;
    }

    public double calcularEfectividad(){
        return(this.getGoles() / this.getPartidos());
    }
    
    public double calcularSueldoACobrar(){
        double aux = 0;
        double promedio = this.getGoles() / this.getPartidos(); 
        if (promedio > 0.5){
            aux += this.getSueldo();
        }
        aux += super.calcularSueldoACobrar();
        return(aux);
    }
    
    public int getPartidos() {
        return partidos;
    }

    public void setPartidos(int partidos) {
        this.partidos = partidos;
    }

    public int getGoles() {
        return goles;
    }

    public void setGoles(int goles) {
        this.goles = goles;
    }
}
