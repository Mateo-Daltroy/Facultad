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
public class Gira extends Recital{
    private String nom;
    private Fecha [] fechas;
    private int dimFechas;
    private int dimLG = 0; 
    private int actual = 0;

    public Gira(String nom, int dimF, String nomBanda, int dimFechas) {
        super(nomBanda, dimF);
        this.nom = nom;
        this.dimFechas = dimFechas;
        this.fechas = new Fecha[dimFechas];
    }
    
    public void agregarFecha(Fecha F){
        this.fechas[this.getDimLG()] = F;
        this.dimLG++;
    }
    
    public void actuar(){
        for (int i = 0; i < this.dimFechas; i++){
            String aux = "";
            aux += "Buenas noches " + this.fechas[this.getActual()].getCiudad() + "\n";
            System.out.println(aux);
           super.actuar();
           this.actual++;
        }
    }
    
    public double calcularCosto(){
        double aux;
        aux = 30000 * this.getDimFechas();
        return aux;
    }

    public int getActual() {
        return actual;
    }

    public int getDimFechas() {
        return dimFechas;
    }

    public int getDimLG() {
        return dimLG;
    }
}
