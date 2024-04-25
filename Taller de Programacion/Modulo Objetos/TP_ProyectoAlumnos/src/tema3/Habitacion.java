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
import PaqueteLectura.GeneradorAleatorio;

public class Habitacion {
    private double precio = 2000 + GeneradorAleatorio.generarDouble(6000);
    private Persona cliente;
    private boolean ocupado = false;

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Persona getCliente() {
        return cliente;
    }

    public void setCliente(Persona cliente) {
        this.cliente = cliente;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }
    
    public String toString(){
        String aux = "";
        aux += this.precio + ", ";
        if (this.isOcupado()){
            aux += "ocupada, " + this.cliente;
        } else {
            aux += "libre";
        }
        aux += "}";
        return(aux);
    }
}
