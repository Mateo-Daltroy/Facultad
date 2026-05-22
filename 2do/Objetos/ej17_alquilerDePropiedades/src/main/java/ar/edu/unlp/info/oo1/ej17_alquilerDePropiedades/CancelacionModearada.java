package ar.edu.unlp.info.oo1.ej17_alquilerDePropiedades;

import java.time.LocalDate;

public class CancelacionModearada implements PoliticaCancelacion {

	public double reembolsarReserva(DateLapse D, double precioPorNoche) {
		double montoTotal = D.sizeInDays() * precioPorNoche;
		if (D.getFrom().compareTo(LocalDate.now()) >= 14) {
			return(montoTotal);
		} else if (D.getFrom().compareTo(LocalDate.now()) >= 2) {
			return(montoTotal * 0.05);
		} else {
			return(0);
		}
	}
}
