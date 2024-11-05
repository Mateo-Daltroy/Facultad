package ar.edu.unlp.info.oo1.ej4_figuras;

public class Cuadrado implements Forma{
	private double lado;

	public double getPerimetro() {
		return(this.lado * 4);
	}
	
	public double getArea() {
		return(this.lado * this.lado);
	}
	
	public double getLado() {
		return lado;
	}

	public void setLado(double lado) {
		this.lado = lado;
	}
	
}
