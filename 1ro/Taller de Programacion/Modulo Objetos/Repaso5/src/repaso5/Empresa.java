/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repaso5;

/**
 *
 * @author mateo
 */
public class Empresa {
    private String nom;
    private String dir;
    private Director D;
    private int dimF;
    private Encargado[] vector;

    public Empresa(String nom, String dir, Director D, int dimF) {
        this.nom = nom;
        this.dir = dir;
        this.D = D;
        this.dimF = dimF;
        this.vector = new Encargado[dimF]; //Java inicializa en null
    }
    
    public void asignarEncargado(Encargado E, int X){
        this.vector[X-1] = E;
    }
    
    public String toString(){
        String aux = this.nom + ", " + this.dir + ". \n" + this.D.toString();
        for (int i = 0; i < this.dimF; i++){
            aux += "Sucursal: " + (i + 1);
            if (this.vector[i] == null){
                aux += "Sin Encargado. \n";
            } else {
                aux += this.vector[i].toString();
            }
        }
        return(aux);
    }
}
