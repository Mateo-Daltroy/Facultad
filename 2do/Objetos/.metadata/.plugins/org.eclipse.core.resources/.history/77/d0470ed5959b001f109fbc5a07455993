package ar.edu.unlp.info.oo1.ej19_envioPaquetes;

import java.util.ArrayList;
import java.util.List;

public abstract class Cliente {

	private String nombre;
	private String direccion;
	private ArrayList<Envio> envios = new ArrayList<Envio>();
	
	public void agregarEnvio(Envio e) {
		this.envios.add(e);
	}
	
	public double montoAPagar(DateLapse d) {
		//Por que esto no funciona?
		//return(this.envios.stream().filter(d.includesDate(Env -> Env.getFecha())).mapToDouble(Env -> Env.calcularPrecio()).sum());
		
		ArrayList<Envio> validos = new ArrayList<Envio>();
		//Esto es pecado pero no se como filtrar por un booleano exterior a envio
		// Hacer un metodo en envio que devuelva un boolean pertenece(DateLapse d)?
		for (Envio E : this.envios) {
			if (d.includesDate(E.getFecha())) {
				validos.add(E);
			}
		}
		return(validos.stream().mapToDouble(Env -> Env.calcularPrecio()).sum());
	}
}