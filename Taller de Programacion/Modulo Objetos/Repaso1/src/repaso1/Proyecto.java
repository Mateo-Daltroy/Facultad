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
public class Proyecto {
    private Investigador [] vector = new Investigador[50];
    private String nom;
    private int cod;
    private String director;
    private int dimL;

    public Proyecto(String nom, int cod, String director) {
        this.nom = nom;
        this.cod = cod;
        this.director = director;
        this.dimL = 0;
    }
    
    public void agregarInvestigador(Investigador unInvestigador){
        if (this.getDimL() < 50){
            this.vector[this.getDimL()] = unInvestigador;
            this.dimL++;
        } else {
            System.out.println("Investigadores en maxima capacidad.");
        }
    }
    
    public void agregarSubsidio(int pos, Subsidio unSubsidio){
        this.vector[pos].agregarSubsidio(unSubsidio);
    }
    
    public double dineroTotalOtorgado(){
        double aux = 0;
        for (int i = 0; i < this.getDimL(); i++){
            aux += this.vector[i].dineroOtorgado();
        }
        return(aux);
    }
    
    public void otorgarTodos (String nombreCompleto){
        int cont = 0;
        boolean encontrado = false;
        while (!(encontrado) && (cont < this.getDimL())){
            if (this.vector[cont].getNom().equals(nombreCompleto)){
                this.vector[cont].otorgarSubsidios();
                encontrado = true;
            }
            cont++;
        }
    }
    
    public String toString(){
        String aux = "";
        aux += "Proyecto: " + this.getNom() + ", codigo " + this.getCod() 
                + ", director " + this.getDirector() + ", presupuesto " 
                + this.dineroTotalOtorgado() + "\n Investigadores: \n";
        for (int i = 0; i < this.getDimL(); i++){
            aux += this.vector[i].toString();
        }
        return(aux);
    }

    public int getDimL() {
        return dimL;
    }

    public String getNom() {
        return nom;
    }

    public int getCod() {
        return cod;
    }

    public String getDirector() {
        return director;
    }
}
