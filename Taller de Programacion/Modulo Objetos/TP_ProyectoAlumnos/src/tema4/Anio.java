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
public class Anio {
    private double [] vect = new double [12];

    public Anio() {
        for (int i = 0; i < 12; i++){
            this.vect[i] = 5000;
        }
    }
    
    public double tempPromedio(){
        double aux = 0;
        for (int i = 0; i < 12; i++){
            aux+= this.vect[i];
        }
        aux = aux / 12.0;
        return(aux);
    }
    
    public void registrarTemp(int indice, double temp){
        this.vect[indice - 1] = temp;
    }
    
    public double getTemp(int mes){
        double aux = this.vect[mes - 1];
        return(aux);
    }
}
