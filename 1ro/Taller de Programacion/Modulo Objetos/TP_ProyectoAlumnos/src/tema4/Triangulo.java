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
public class Triangulo extends Figura {
    private double lado1;
    private double lado2;
    private double lado3;

    public Triangulo(double lado1, double lado2, double lado3, String relleno, String linea) {
        super(relleno, linea);
        this.lado1 = lado1;
        this.lado2 = lado2;
        this.lado3 = lado3;
    }
    
    public double calcularArea(){ //Devuelve NAN
        double s = (this.lado1 + this.lado2 + this.lado3);
        s = s / 2.0;
        double area;
        area = s * (s - this.lado1) * (s - this.lado2) * (s - this.lado3);
        area = Math.sqrt(area);
        return(area);
    }
    
    public double calcularPerimetro(){
        double aux = (this.lado1 + this.lado2 + this.lado3);
        return (aux);
    }

    public double getLado1() {
        return lado1;
    }

    public void setLado1(double lado1) {
        this.lado1 = lado1;
    }

    public double getLado2() {
        return lado2;
    }

    public void setLado2(double lado2) {
        this.lado2 = lado2;
    }

    public double getLado3() {
        return lado3;
    }

    public void setLado3(double lado3) {
        this.lado3 = lado3;
    }
}
