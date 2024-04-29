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
public class Entrenador extends Empleado {
    private int camp;

    public Entrenador(int camp, String nom, double sueldo, int ant) {
        super(nom, sueldo, ant);
        this.camp = camp;
    }
    
    public double calcularEfectividad(){
        return(this.getCamp() / this.getAnt());
    }
    
    public double calcularSueldoACobrar(){
        double aux = 0;
        if ((this.getCamp() > 0) && (this.getCamp() < 5)){
            aux += 5000;
        } else if (this.getCamp() < 11) {
            aux += 30000;
        } else if (this.getCamp() > 10) {
            aux += 50000;
        }
        aux += super.calcularSueldoACobrar();
        return(aux);
    }

    public int getCamp() {
        return camp;
    }

    public void setCamp(int camp) {
        this.camp = camp;
    }
}
