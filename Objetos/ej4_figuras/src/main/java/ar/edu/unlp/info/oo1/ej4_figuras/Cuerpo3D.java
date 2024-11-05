package ar.edu.unlp.info.oo1.ej4_figuras;

public class Cuerpo3D {
	private Forma caraBasal;
	private double altura;
	
	public double getVolumen() {
		return(this.caraBasal.getArea() * this.altura);
	}
	
	public double getSuperficieExterior() {
		return((2 * this.caraBasal.getArea()) + (this.caraBasal.getPerimetro() * this.altura));
	}
	
	public void setCaraBasal (Forma forma) {
		this.caraBasal = forma;
	}
	
	public double getAltura() {
		return altura;
	}
	
	public void setAltura(double altura) {
		this.altura = altura;
	}
	
}
