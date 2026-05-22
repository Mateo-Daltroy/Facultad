/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repaso6;

/**
 *
 * @author mateo
 */
public class Torneo {
    private Fecha[] vectorF;
    private int dimFF;
    private String nombre;

    public Torneo(int dimFF, String nombre, int cantGoleadores) {
        this.dimFF = dimFF;
        this.nombre = nombre;
        this.vectorF = new Fecha[dimFF];
        for (int i = 0; i < dimFF; i++){
            this.vectorF[i] = new Fecha(cantGoleadores);
        }
    }
    
    public void agregarGoleador(Goleador G, int X){
        this.vectorF[X - 1].agregarGoleador(G);
    }
    
    public Goleador menosGoles(int X){
        return(this.vectorF[X - 1].menosGoles());
    }
    
    public int goleadoresTot(){
        int cont = 0;
        for (int i = 0; i < this.dimFF; i++){
            cont += this.vectorF[i].getDimLG();
        }
        return(cont);
    }
    
    public String toString(){
        String aux = "TORNEO 2024: " + this.nombre + "\n";
        for (int i = 0; i < this.dimFF; i++){
            aux += "Fecha #" + (i+1) + " | " + this.vectorF[i].toString();
        }
        return(aux);
    }
}
