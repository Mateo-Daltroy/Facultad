package ar.edu.unlp.info.oo1.ej20_liquidacionHaberes;

import java.time.LocalDate;

public abstract class Contrato {

	protected LocalDate inicio;
	private int aniosaux;
	
	public Contrato (int anios) {
		this.aniosaux = anios;
	}
	
	public int calcularLong() {
		return(this.aniosaux); //temporal, no se como calcular la longitud
	}
	
	public abstract double calcularBasico(Empleado e);
	public abstract boolean isVencido();
}
