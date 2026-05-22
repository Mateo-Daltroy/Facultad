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
public class CoroHileras extends Coro {
    private int F;
    private int C;
    private int dimL = 0;
    private Corista[][] matriz;

    public CoroHileras(int F, int C, Director D, String nom) {
        super(D, nom);
        this.F = F;
        this.C = C;
        this.matriz = new Corista[F][C]; //Java inicializa en null
    }
    
    public void agregarCorista(Corista C){
        int aux = this.dimL;
        int Fila = 0;
        int Columna = 0;
        while (aux > 0) {
            while ((Columna < this.getC()) && (aux > 0)){
                Columna++;
                aux--;
            }
            if (Columna >= this.getC()){
                Fila++;
                Columna = 0;
            }
        }
        this.matriz[Fila][Columna] = C;
        this.dimL++;
    }
    
    public boolean estaLleno(){
        if ((this.getC() * this.getF()) == this.getDimL())
            return(true);
        else
            return(false);
    }
    
    public boolean bienFormado(){
        boolean aux = true;
        for (int i = 0; i < (this.getF() - 1); i++){
            if(this.matriz[0][i].getTono() < this.matriz[0][i].getTono())
                aux = false;
        }
        if (aux){
            for (int i = 0; i < this.getF(); i++){
                for(int j = 0; j < (this.getC() - 1); j++){
                    if (this.matriz[i][j].getTono() != this.matriz[i][j+1].getTono())
                        aux = false;
                }
            }
        }
        return(aux);
    }
    
    public String toString(){
        String aux = "";
        aux += super.toString();
        int cont = 0;
        int i = 0;
        int j = 0;
        while (cont < this.getDimL()){
            aux += this.matriz[i][j].toString();
            i++;
            if (i >= this.getF()){
                i = 0;
                j++;
            }
            cont++;
        }
        return(aux);
    }

    public int getDimL() {
        return dimL;
    }

    public int getF() {
        return F;
    }

    public int getC() {
        return C;
    }
}
