package ar.edu.unlp.info.oo1.Ej_9Ganchos;

public class CajaDeAhorro extends Cuenta{
	
	public void extraerSinControlar(double monto) {
		super.extraerSinControlar(monto*1.02);
	}
	
	public void depositar(double monto) {
		super.depositar(monto*0.98);
	}
	
	protected boolean puedeExtraer(double monto) {
		if (this.getSaldo() >= (monto * 1.02)) {
			return true;
		}
		return false;
	}
}
