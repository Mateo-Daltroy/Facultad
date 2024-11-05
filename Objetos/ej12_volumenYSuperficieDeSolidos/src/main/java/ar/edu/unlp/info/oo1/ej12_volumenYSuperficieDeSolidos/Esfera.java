package ar.edu.unlp.info.oo1.ej12_volumenYSuperficieDeSolidos;

public class Esfera extends Pieza {
	
	private double radio;

	public Esfera (int unRadio) {
		this.radio = unRadio;
	}
	
	public double getVolumen() {
		return((4.0/3) * Math.PI * Math.pow(radio, 3));
	}
	
	public double getSuperficie() {
		return(4 * Math.PI * Math.pow(radio, 2));
	}
}
