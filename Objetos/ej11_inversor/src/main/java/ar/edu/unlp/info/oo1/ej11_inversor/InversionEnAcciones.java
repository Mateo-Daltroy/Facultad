package ar.edu.unlp.info.oo1.ej11_inversor;

public class InversionEnAcciones implements Inversion {
	private String nombre;
	private int unidades;
	private double valorUnitario;
	
	public InversionEnAcciones (String unNombre, int unasUnidades, double unValor) {
		this.nombre = unNombre;
		this.unidades = unasUnidades;
		this.valorUnitario = unValor;
	}
	
	public double valorActual() {
		return(this.unidades * this.valorUnitario);
	}
}
