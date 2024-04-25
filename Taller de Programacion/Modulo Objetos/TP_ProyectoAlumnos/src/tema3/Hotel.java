/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tema3;

/**
 *
 * @author mateo
 */
public class Hotel {
    private Habitacion [] hab;
    private int dimF;

    public Hotel(int N) {
        this.hab = new Habitacion [N]; // Java inicializa en null
        for (int i = 0; i < N; i++){
            this.hab[i] = new Habitacion();
        }
        this.dimF = N;
    }
    
    public void agregarCliente(Persona C, int X){
        //this.hab[X-1] = new Habitacion(); // Hacer prueba / preguntar en clase
        this.hab[X-1].setCliente(C);
        this.hab[X-1].setOcupado(true);
    }
    
    public void aumentarPrecios(double monto){
        for (int i = 0; i < dimF; i++){
            if (this.hab[i] != null){
                this.hab[i].setPrecio(this.hab[i].getPrecio() + monto);
            }
        }
    }
    
    public String toString(){
        String aux = "";
        for (int i = 0; i < this.dimF; i++){
            aux += "{Habitacion " + (i+1) + ": " + this.hab[i] + "\n";
        }
        return(aux);
    }
}
