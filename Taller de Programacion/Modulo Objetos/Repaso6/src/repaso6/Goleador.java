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
public class Goleador {
    private String nom;
    private String equipo;
    private int goles;

    public Goleador(String nom, String equipo, int goles) {
        this.nom = nom;
        this.equipo = equipo;
        this.goles = goles;
    }

    public int getGoles() {
        return goles;
    }
    
    public String toString(){
        String aux = "{" + this.nom + ", " + this.equipo + ", " + this.getGoles() + "}";
        return(aux);
    }
}
