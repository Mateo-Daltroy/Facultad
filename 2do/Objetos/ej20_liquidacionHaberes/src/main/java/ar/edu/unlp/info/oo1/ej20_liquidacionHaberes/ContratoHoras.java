package ar.edu.unlp.info.oo1.ej20_liquidacionHaberes;

import java.time.LocalDate;

public class ContratoHoras extends Contrato{

	private double sueldoHora;
	private int horasMes;
	private LocalDate fechafin;
	
	public ContratoHoras(double unSueldo, int horas, LocalDate fecha, int anios) {
		super(anios);
		this.sueldoHora = unSueldo;
		this.horasMes = horas;
		this.fechafin = fecha;
	}
	
	public double calcularBasico(Empleado e) {
		return(this.sueldoHora * this.horasMes);
	}
	
	public boolean isVencido() {
		return(this.fechafin.isBefore(LocalDate.now()));
	}
}
