package ar.edu.unlp.info.oo1.ej15_distribuidoraElectrica;

public class Consumo {
	
	private double energiaActiva;
	private double energiaReactiva;
	
	public Consumo (double enA, double enR) {
		this.energiaActiva = enA;
		this.energiaReactiva = enR;
	}

	public double getEnergiaActiva() {
		return energiaActiva;
	}

	public double getEnergiaReactiva() {
		return energiaReactiva;
	}
}
