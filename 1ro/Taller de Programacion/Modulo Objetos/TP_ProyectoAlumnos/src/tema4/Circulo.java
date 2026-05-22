/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tema4;


/**
 *
 * @author mate
 */
public class Circulo extends Figura {
    private double radio;

    public Circulo(double radio, String relleno, String linea) {
        super(relleno, linea);
        this.radio = radio;
    }
    
    public double calcularPerimetro(){
        double aux;
        aux = 2 * Math.PI * this.getRadio();
        return(aux);
    }
    
    public double calcularArea() {
        double aux;
        aux = Math.PI * this.getRadio() * this.getRadio();
        return(aux);
    }

    public double getRadio() {
        return radio;
    }

    public void setRadio(double radio) {
        this.radio = radio;
    }
}
