package ar.edu.unlp.info.oo1.ej20_liquidacionHaberes;

import java.time.LocalDate;

public class ReciboDeSueldo {

	private final String nombre;
	private final String cuil;
	private final int antiguedad;
	private final LocalDate fechaGen;
	private final double montoTot;
	
	public ReciboDeSueldo(String unNombre, String unCuil, int anti, double montoBasico) {
		this.nombre = unNombre;
		this.cuil = unCuil;
		this.antiguedad = anti;
		this.fechaGen = LocalDate.now();
		this.montoTot = this.generarTotal(anti, montoBasico);
	}
	
	private double generarTotal(int anti, double montoBasico) {
		if (anti < 5) {
			return(montoBasico);
		} else if (anti < 10) {
			return(montoBasico * 1.3);
		} else if (anti < 15) {
			return(montoBasico * 1.5);
		} else if (anti < 20) {
			return(montoBasico * 1.7);
		}
		return(montoBasico * 2);
	}
	
	public double getmontoTot() { 
		return this.montoTot;
	}
	
	public int getAntiguedad() {
		return this.antiguedad;
	}
}
