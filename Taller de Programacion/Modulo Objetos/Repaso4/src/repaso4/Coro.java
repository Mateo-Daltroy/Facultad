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
public abstract class Coro {
    private Director D;
    private String nom;

    public Coro(Director D, String nom) {
        this.D = D;
        this.nom = nom;
    }
    
    public abstract void agregarCorista(Corista C);
    public abstract boolean estaLleno();
    public abstract boolean bienFormado();
    
    public String toString(){
        String aux = "";
        aux += "Nombre: " + this.nom + ", Director: " + this.D.toString();
        return(aux);
    }
}
