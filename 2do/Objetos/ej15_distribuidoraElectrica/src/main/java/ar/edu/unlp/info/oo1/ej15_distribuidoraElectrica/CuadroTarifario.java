package ar.edu.unlp.info.oo1.ej15_distribuidoraElectrica;

public class CuadroTarifario {
	
	private double preciokWh;
	
	public CuadroTarifario(double precio) {
		this.preciokWh = precio;
	}

	public double getPrecioKWh() {
		return preciokWh;
	}

	public void setPreciokWh(double preciokWh) {
		this.preciokWh = preciokWh;
	}
}
