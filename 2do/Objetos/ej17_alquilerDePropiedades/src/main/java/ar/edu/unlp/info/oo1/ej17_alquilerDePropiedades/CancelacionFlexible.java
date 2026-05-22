package ar.edu.unlp.info.oo1.ej17_alquilerDePropiedades;

public class CancelacionFlexible implements PoliticaCancelacion {

	public double reembolsarReserva(DateLapse D, double precioPorNoche) {
		return(D.sizeInDays() * precioPorNoche);
	}
}
