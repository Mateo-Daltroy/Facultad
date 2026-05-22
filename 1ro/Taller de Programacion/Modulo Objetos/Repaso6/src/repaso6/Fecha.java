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
public class Fecha {
    private Goleador[] vectorG;
    private int dimLG;
    private int dimFG;

    public Fecha(int dimFG) {
        this.dimFG = dimFG;
        this.dimLG = 0;
        this.vectorG = new Goleador[dimFG]; // Java inicializa goleadores en null
    }
    
    public void agregarGoleador(Goleador G){
        this.vectorG[this.getDimLG()] = G;
        this.dimLG++;
    }
    
    public Goleador menosGoles(){
        Goleador aux = null;
        int golMin = 999;
        for(int i = 0; i < this.getDimLG(); i++){
            if (this.vectorG[i].getGoles() < golMin){
                aux = this.vectorG[i];
                golMin = aux.getGoles();
            }
        }
        return(aux);
    }
    
    public String toString(){
        String aux = this.getDimLG() + " | Goleadores: ";
        for(int i = 0; i < this.getDimLG(); i++){
            aux += this.vectorG[i].toString() + " ";
        }
        aux += "\n";
        return(aux);
    }

    public int getDimLG() {
        return dimLG;
    }
}
