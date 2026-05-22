 package ar.edu.unlp.info.oo1.Parcial1Obj;

import java.time.LocalDate;

public class DateLapse {
	private LocalDate from;
	private LocalDate to;
	
	public DateLapse (LocalDate aFrom, LocalDate aTo) {
		this.from = aFrom;
		this.to = aTo;
	}
	
	public int sizeInDays() {
		return(this.to.compareTo(this.from));
	}
	
	public boolean includesDate(LocalDate other) {
		if (other.isAfter(this.from) && other.isBefore(this.to)) {
			return(true);
		} else {
			return(false);
		}
	}
	
	public LocalDate getFrom() {
		return from;
	}
	public LocalDate getTo() {
		return to;
	}
}
