package ar.edu.unlp.info.oo1.Parcial1Obj;

import java.time.LocalDate;

public abstract class Evento {

	private String nombre;
	private LocalDate fecha;
	private String temaPrincipal;
	private double precioInscripcion;
	private double precioRemera;
	
	public Evento (String unNombre, LocalDate unaFecha, String unTema, double pIns, double pRem) {
		this.fecha = unaFecha;
		this.nombre = unNombre;
		this.temaPrincipal = unTema;
		this.precioInscripcion = pIns;
		this.precioRemera = pRem;
	}
	
	public double getPrecio(LocalDate fecha) {
		if (this.fecha.equals(fecha)) {
			return((this.precioInscripcion * 1.2) + this.precioRemera);
		}
		return(this.precioInscripcion + this.precioRemera);
	}
	
	public LocalDate getFecha() {
		return this.fecha;
	}
}
