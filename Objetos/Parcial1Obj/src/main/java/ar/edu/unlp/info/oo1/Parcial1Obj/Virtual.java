package ar.edu.unlp.info.oo1.Parcial1Obj;

import java.time.LocalDate;

public class Virtual extends Evento {

	private double montoEnvio;
	
	public Virtual (String unNombre, LocalDate unaFecha, String unTema, double pIns, double pRem, double pEnv) {
		super(unNombre, unaFecha, unTema, pIns, pRem);
		this.montoEnvio = pEnv;
	}
	
	@Override
	public double getPrecio(LocalDate fecha) {
		return(super.getPrecio(fecha) + this.montoEnvio);
	}
}
