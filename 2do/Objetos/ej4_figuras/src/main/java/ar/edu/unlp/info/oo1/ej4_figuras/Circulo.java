package ar.edu.unlp.info.oo1.ej4_figuras;

public class Circulo implements Forma{
	private double radio;
	
	public double getPerimetro() {
		return(this.radio * 2 * Math.PI);
	}
	
	public double getArea() {
		return(Math.PI * Math.pow(this.radio, 2));
	}
	
	public double getDiametro() {
		return (this.radio * 2);
	}
	public void setDiametro(double diametro) {
		this.radio = (diametro / 2);
	}
	public double getRadio() {
		return radio;
	}
	public void setRadio(double radio) {
		this.radio = radio;
	}
}
