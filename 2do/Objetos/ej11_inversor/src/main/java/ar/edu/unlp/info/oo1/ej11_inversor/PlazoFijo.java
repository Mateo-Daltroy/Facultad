package ar.edu.unlp.info.oo1.ej11_inversor;

import java.time.LocalDate;

public class PlazoFijo implements Inversion{
	private LocalDate fechaDeConstitucion;
	private double montoDepositado;
	private double porcentajeDeInteresDiario;
	
	public PlazoFijo (LocalDate unaFecha, double unMonto, double unPorcentaje) {
		this.fechaDeConstitucion = unaFecha;
		this.montoDepositado = unMonto;
		this.porcentajeDeInteresDiario = unPorcentaje;
	}
	
	public double valorActual() {
		int aux = this.fechaDeConstitucion.compareTo(LocalDate.now());
		double total = (((-aux) * this.porcentajeDeInteresDiario) 
				* this.montoDepositado)
				+ this.montoDepositado;
		return (total);
	}
}
