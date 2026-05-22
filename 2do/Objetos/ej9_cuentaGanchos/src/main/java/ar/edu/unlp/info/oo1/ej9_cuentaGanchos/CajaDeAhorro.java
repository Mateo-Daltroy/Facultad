package ar.edu.unlp.info.oo1.ej9_cuentaGanchos;

public class CajaDeAhorro extends Cuenta{
	
	public void extraerSinControlar(double monto) {
		super.extraerSinControlar(monto*1.02);
	}
	
	public void depositar(double monto) {
		super.depositar(monto*0.98);
	}
	
	protected boolean puedeExtraer(double monto) {
		return(this.getSaldo() >= (monto * 1.02));
	}
}