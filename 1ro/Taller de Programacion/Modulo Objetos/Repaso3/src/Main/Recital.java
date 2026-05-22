/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

/**
 *
 * @author mateo
 */
public abstract class Recital {
    private String nomBanda;
    private String [] temas;
    private int dimF;
    private int dimL = 0;

    public Recital(String nomBanda, int dimF) {
        this.nomBanda = nomBanda;
        this.dimF = dimF;
        this.temas = new String[dimF];
    }
    
    public void agregarTema(String unTema){
        this.temas[this.getDimL()] = unTema;
        this.dimL++;
    }
    
    public void actuar(){
        String aux = "";
        for (int i = 0; i < this.getDimL(); i++){
            aux += "y ahora tocaremos " + this.temas[i] + "\n";
        }
        System.out.println(aux);
    }
    
    public abstract double calcularCosto();

    public int getDimF() {
        return dimF;
    }

    public int getDimL() {
        return dimL;
    }

    public String getNomBanda() {
        return nomBanda;
    }

    public void setNomBanda(String nom) {
        this.nomBanda = nom;
    }
}
