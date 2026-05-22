package ar.edu.unlp.info.oo1.ej9_cuentaGanchos;

public class CuentaCorriente extends Cuenta{
	private double descubierto;
	
	public CuentaCorriente() {
		descubierto = 0;
	}
	
	public boolean puedeExtraer(double monto) {
		if (this.getSaldo() - monto >= descubierto) {
			return(true);
		}
		return(false);
	}

	public double getDescubierto() {
		return descubierto;
	}

	public void setDescubierto(double descubierto) {
		this.descubierto = descubierto;
	}
	
}
