package ar.edu.unlp.info.oo1.ej12_volumenYSuperficieDeSolidos;

public class PrismaRectangular extends Pieza {

	private int ladoMayor;
	private int ladoMenor;
	private int altura;
	
	public PrismaRectangular(int ladoMen, int ladoMay, int h) {
		this.altura = h;
		this.ladoMayor = ladoMay;
		this.ladoMenor = ladoMen;
	}
	
	public double getVolumen() { 
		return(this.altura * this.ladoMayor * this.ladoMenor);
	}
	
	public double getSuperficie() {
		return(2 * (this.ladoMenor * this.ladoMayor + this.ladoMayor * this.altura + this.altura * this.ladoMenor));
	}
}
