package ar.edu.unlp.info.oo1.ej14_intervaloDeTiempo;

import java.time.LocalDate;

public class DateLapseB {

	private LocalDate from;
	private int sizeInDays;
	
	public DateLapseB (LocalDate unaFecha, LocalDate fechaLimite) {
		this.from = unaFecha;
		this.sizeInDays = fechaLimite.compareTo(unaFecha);
		System.out.println(this.sizeInDays);
	}
	
	public boolean includesDate(LocalDate fecha) {
		System.out.println(this.from.plusDays(sizeInDays));
		if (fecha.isAfter(this.from) && fecha.isBefore(this.from.plusDays(sizeInDays))) {
			return(true);
		} else {
			return(false);
		}
	}
	
	public LocalDate getFrom() {
		return from;
	}
	
	public LocalDate getTo() {
		return (this.from.plusDays(this.sizeInDays));
	}
	
	public int sizeInDays() {
		return sizeInDays;
	}
}
