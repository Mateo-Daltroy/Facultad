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
public class VisorFiguras {
    private int guardadas;
    private int capacidadMaxima = 5;
    private Figura[] vector;

    public VisorFiguras(){
        this.guardadas = 0;
        vector = new Figura[this.capacidadMaxima]; // Java inicializa en null
    }
    
    public void guardar(Figura f){
        if (this.getGuardadas() < 5){
            vector[this.getGuardadas()] = f;
            this.guardadas++;
        } 
    }
    
    public boolean quedaEspacio(){
        if (this.getGuardadas() == this.getCapacidadMaxima()){
            return(false);
        } else {
            return(true);
        }
    }

    public void mostrar(){
        for (int i = 0; i < this.getGuardadas(); i++){
            System.out.println(this.vector[i]);
        }
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int getGuardadas() {
        return this.guardadas;
    }
}
