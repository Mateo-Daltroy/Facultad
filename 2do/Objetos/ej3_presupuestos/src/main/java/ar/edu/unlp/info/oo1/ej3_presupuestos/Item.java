package ar.edu.unlp.info.oo1.ej3_presupuestos;

public class Item {
	private String detalle;
	private int cantidad;
	private double costoUnitario;
	
	public Item(String detalle, int cantidad, double costoUnitario) {
		this.cantidad = cantidad;
		this.detalle = detalle;
		this.costoUnitario = costoUnitario;
	}
	
	public double costo() {
		return(this.cantidad * this.costoUnitario);
	}

	public double getCostoUnitario() {
		return costoUnitario;
	}
}
