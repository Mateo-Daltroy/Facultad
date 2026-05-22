package ar.edu.unlp.info.oo1.ejercicio2Bal;

public class Producto {
	private double peso;
	private double precioPorKilo;
	private String descripcion;
	
	public Producto() {
	}
	
	public Producto(double p, double ppk, String S) {
		this.peso = p;
		this.precioPorKilo = ppk;
		this.descripcion = S;
	}
	
	public double getPrecio() {
		return(this.peso * this.precioPorKilo);
	}
	
	public double getPeso() {
		return peso;
	}

	public double getPrecioPorKilo() {
		return precioPorKilo;
	}

	public void setPrecioPorKilo(double precioPorKilo) {
		this.precioPorKilo = precioPorKilo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}
	
}
