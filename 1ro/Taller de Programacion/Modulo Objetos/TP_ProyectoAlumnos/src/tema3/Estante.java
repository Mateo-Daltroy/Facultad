/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tema3;

/**
 *
 * @author mateo
 */
public class Estante {
    private Libro[] vector = new Libro[20]; // Java inicializa en null
    private int dimL = 0;
    
    public boolean estaLleno() {
        if (this.dimL == 20){
            return(true);
        } else {
            return(false);
        }
    }
    
    public int cantLibros() {
        return(this.dimL);
    }
    
    public void agregarLibro (Libro l){
        if (dimL < 20) {
            this.vector[this.dimL] = l;
            dimL++;    
        }
    }
    
    public Libro devolverLibro(String busq){
        for (int i = 0; i < dimL; i++){
            if (this.vector[i].getTitulo().equals(busq)){
                return(this.vector[i]);
            }
        }
        Libro fallido = new Libro();
        return(fallido);
    }
}
