/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tema4;

/**
 *
 * @author mateo
 */
public class Estacion {
    private Anio [] A;
    private String nom; private String lat; 
    private String lon; private int inicio;
    private int dimF;

    public Estacion(String nom, String lat, String lon, int inicio,  int dimF) {
        this.nom = nom;
        this.lat = lat;
        this.lon = lon;
        this.inicio = inicio;
        this.dimF = dimF;
        this.A = new Anio[dimF];
        // Preguntar por inicializacion
        for(int i = 0; i < dimF; i++){
            this.A[i] = new Anio();
        }
    }
    
    public void registrar(int anio, int mes, double temp){
        A[anio - this.inicio].registrarTemp(mes, temp);
    }
    
    public double obtener(int anio, int mes){
        double aux = A[anio - this.inicio].getTemp(mes);
        return(aux);
    }
    
    public String mesMayor(){
        String aux;
        int mes = -1;
        int anio = -1;
        double max = -200;
        for (int i = 0; i < this.dimF; i++){
            for (int j = 0; j < 12; j++){
                if (A[i].getTemp(j) > max){
                    max = A[i].getTemp(j);
                    mes = j + 1;
                    anio = this.inicio + i;
                }
            }
        }
        aux = ("La mayor temperatura se registro durante el mes " + mes + " en el año " + anio);
        return(aux);
    }
    
    public String promedioAnios(){
        String aux;
        aux = this.nom + " (" + this.lat + "  -  " + this.lon + "): \n";
        for (int i = 0; i < this.dimF; i++){
            aux += "- Año " + (i + this.inicio) + ": "
                    + this.A[i].tempPromedio() + " C; \n";
        }
        return(aux);
    }
    
    public String promedioMeses(){
        String aux = "";
        double [] vector = new double[12]; // Java inicializa en 0
        for (int i = 0; i  < 12; i++){
            for (int j = 0; j < this.dimF; j++){
                vector[i] += (this.A[j].getTemp(i) / this.dimF);
            }
        }
        aux = this.agregarMeses(aux, vector);
        return(aux);
    }
    
    public String agregarMeses(String aux, double [] vector){
        // Esto podria implementarse en un for, si la consigna no pidiese 
        // los meses escritos como "Enero" "Febrero" etc.
        aux = "Enero: " + vector[0] + " C; \n"; 
        aux += "Febrero: " + vector[1] + " C; \n"; 
        aux += "Marzo: " + vector[2] + " C; \n"; 
        aux += "Abril: " + vector[3] + " C; \n"; 
        aux += "Mayo: " + vector[4] + " C; \n"; 
        aux += "Junio: " + vector[5] + " C; \n"; 
        aux += "Julio: " + vector[6] + " C; \n"; 
        aux += "Agosto: " + vector[7] + " C; \n"; 
        aux += "Septiembre: " + vector[8] + " C; \n"; 
        aux += "Octubre: " + vector[9] + " C; \n"; 
        aux += "Noviembre: " + vector[10] + " C; \n"; 
        aux += "Diciembre: " + vector[11] + " C; \n"; 
        return(aux);
    }
}
