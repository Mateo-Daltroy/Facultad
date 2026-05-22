package ar.edu.unlp.info.oo1.ej12_volumenYSuperficieDeSolidos;

public class Cilindro extends Pieza {

	private int radio;
	private int altura;
	
	public Cilindro(int unRadio, int unaAltura) {
		this.radio = unRadio;
		this.altura = unaAltura;
	}
	
	public double getVolumen() {
		return(Math.PI * Math.pow(this.radio, 2) * this.altura);
	}
	
	public double getSuperficie() {
		return((2* Math.PI * this.altura * this.radio) + (2 * Math.PI * Math.pow(radio, 2)));
	}
}
