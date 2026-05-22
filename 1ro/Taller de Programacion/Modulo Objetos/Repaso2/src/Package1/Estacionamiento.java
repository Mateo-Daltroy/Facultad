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
public class Estacionamiento {
    private String nom;
    private String direccion;
    private String apertura;
    private String cierre;
    private Auto[][] matriz;
    private int plaza;
    private int pisos;
    // Piso filas, plaza columna [Piso][Plaza]

    public Estacionamiento(String nom, String direccion) {
        this.nom = nom;
        this.direccion = direccion;
        this.apertura = "8:00";
        this.cierre = "21:00";
        this.plaza = 10;
        this.pisos = 5;
        this.matriz = new Auto[this.pisos][this.plaza]; // Java inicializa las posiciones en null
    }

    public Estacionamiento(String nom, String direccion, String apertura, String cierre, int plaza, int pisos) {
        this.nom = nom;
        this.direccion = direccion;
        this.apertura = apertura;
        this.cierre = cierre;
        this.plaza = plaza;
        this.pisos = pisos;
        this.matriz = new Auto[this.pisos][this.plaza]; // Java inicializa las posiciones en null
    }
    
    public void agregarAuto(Auto A, int piso, int plaza){
        this.matriz[piso - 1][plaza - 1] = A;
    }
    
    public String buscarPat(String busq){
        for (int i = 0; i < this.getPlaza(); i++){
            for (int j = 0; j < this.getPisos(); j++){
                if (!(this.matriz[j][i] == null) && (this.matriz[j][i].getPatente().equals(busq)))
                    return("Piso " + (j+1) + " Plaza" + (i+1));
            }
        }
        return("Auto Inexistente");
    }
    
    public String toString(){
        String aux = "";
        for (int i = 0; i < this.getPisos(); i++){
            aux += "Piso " + (i+1) + ": \n";
            for (int j = 0; j < this.getPlaza(); j++){
                aux += "Plaza " + (j+1) + ": ";
                if (this.matriz[i][j] == null){
                    aux += "libre, ";
                } else {
                    aux += matriz[i][j].toString();
                }
            }
            aux += "\n";
        }
        return(aux);
    }
    
    public int cantEnPlaza(int plaza){
        int cont = 0;
        for (int i = 0; i < this.getPisos(); i++){
            if (!(this.matriz[i][plaza - 1] == null))
                cont++;
        }
        return(cont);
    }

    public int getPlaza() {
        return plaza;
    }

    public int getPisos() {
        return pisos;
    }
    
}
