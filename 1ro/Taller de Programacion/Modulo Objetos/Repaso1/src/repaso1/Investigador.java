/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repaso1;

/**
 *
 * @author mateo
 */
public class Investigador {
    private String nom;
    private int categ;
    private String esp;
    private Subsidio [] vector = new Subsidio[5];
    private int dimL;

    public Investigador(String nom, int categ, String esp) {
        this.nom = nom;
        this.categ = categ;
        this.esp = esp;
        this.dimL = 0;
    }
    
    public void agregarSubsidio(Subsidio unSubsidio){
        if (this.getDimL() < 5){
            this.vector[this.getDimL()] = unSubsidio;
            this.dimL++;
        }
    }
    
    public double dineroOtorgado(){
        double aux = 0;
        for (int i = 0; i < this.getDimL(); i++){
            if (this.vector[i].isOtorgado())
                aux += this.vector[i].getMonto();
        }
        return(aux);
    }
    
    public void otorgarSubsidios(){
        for (int i = 0; i < this.getDimL(); i++){
            this.vector[i].setOtorgado(true);
        }
    }
    
    public String toString(){
        String aux = "";
        aux += this.getNom() + ", " + this.getCateg() + ", " + 
                this.getEsp() + ", " + this.dineroOtorgado() + "\n";
        return(aux);
    }

    public int getDimL() {
        return dimL;
    }

    public int getCateg() {
        return categ;
    }

    public String getEsp() {
        return esp;
    }

    public String getNom() {
        return nom;
    }
}
