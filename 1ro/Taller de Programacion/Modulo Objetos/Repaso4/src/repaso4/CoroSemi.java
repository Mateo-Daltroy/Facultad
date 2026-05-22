/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repaso4;

/**
 *
 * @author mateo
 */
public class CoroSemi extends Coro {
    private int dimF;
    private int dimL = 0;
    private Corista[] vector;

    public CoroSemi(int dimF, Director D, String nom) {
        super(D, nom);
        this.dimF = dimF;
        this.vector = new Corista[dimF]; //Java inicializa en null
    }
    
    public void agregarCorista(Corista C){
        this.vector[this.dimL] = C;
        this.dimL++;
    }
    
    public boolean estaLleno(){
        if (this.getDimL() == this.getDimF())
            return(true);
        else
            return(false);
    }
    
    public boolean bienFormado(){
        boolean aux = true;
        for (int i = 0; i < (this.getDimF() - 1); i++){
            if (this.vector[i].getTono() < this.vector[i+1].getTono())
                aux = false;
        }
        return(aux);
    }
    
    public String toString(){
        String aux = "";
        aux += super.toString();
        for (int i = 0; i < this.getDimL(); i++){
            aux += this.vector[i].toString();
        }
        return(aux);
    }
    
    public int getDimL() {
        return dimL;
    }

    public int getDimF() {
        return dimF;
    }
}
