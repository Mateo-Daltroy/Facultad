/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Package1;
/**
 *
 * @author mateo
 */
public class Auto {
    private String patente;
    private String nom;

    public Auto(String patente, String nom) {
        this.patente = patente;
        this.nom = nom;
    }
    
    public String toString(){
        return(this.getPatente() + ", de " + this.getNom() + ", ");
    }

    public String getPatente() {
        return patente;
    }

    public String getNom() {
        return nom;
    }
}
