package ar.edu.unlp.info.oo1.Parcial1Obj;

public class Sede {

	private String nombre;
	private double precioPorDia;
	private int cantDias;
	
	public double getPrecioTotal() {
		return(this.precioPorDia * this.cantDias);
	}
}
