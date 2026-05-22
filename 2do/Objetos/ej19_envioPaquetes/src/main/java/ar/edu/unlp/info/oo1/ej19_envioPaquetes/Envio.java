package ar.edu.unlp.info.oo1.ej19_envioPaquetes;

import java.time.LocalDate;

public abstract class Envio {

	protected LocalDate fecha = null;
	protected String dirOrigen = null;
	protected String dirDestino = null;
	protected int peso = -1;
	
	public Envio (LocalDate unaFecha, String unOrigen, String unDestino, int unPeso) {
		this.fecha = unaFecha;
		this.dirOrigen = unOrigen;
		this.dirDestino = unDestino;
		this.peso = unPeso;
	}
	
	public abstract double calcularPrecio();
	
	public LocalDate getFecha() {
		return this.fecha;
	}
}
