package ar.edu.unlp.info.oo1.ej15_distribuidoraElectrica;

import java.time.LocalDate;

public class FacturaDeCobro {

	private LocalDate fechaEmision;
	private Usuario user;
	private double montoFinal;
	private boolean bonificado;
	private CuadroTarifario fuenteDePrecios;
	
	//Hay un problema en que el usuario genere la factura, y es que no puede proveer el CuadroTarifario.
	//Entonces en el constructor de FacturaDeCobro no se pueden generar directamente los valores de montoFinal y bonificado
	//Por eso guardo el Consumo, para ejecutar determinarBonus y determinarMonto una vez llegue el CuadroTarifario
	private Consumo consumoDelUsuario;
	
	public FacturaDeCobro (Consumo C, LocalDate fecha, Usuario U) {
			this.user = U;
			this.fechaEmision = fecha;
			this.consumoDelUsuario = C;
	}
	
	private void determinarMonto (Consumo C) {
		this.montoFinal = C.getEnergiaActiva() * this.fuenteDePrecios.getPrecioKWh();
		if (this.bonificado) {
			this.montoFinal = this.montoFinal * 0.9;
		}
	}
	
	// Esta funcion podria retornar un booleano, deberia?
	// El enunciado dice que debe contener el dato de si aplica la bonificacion o no
	private void determinarBonus (Consumo C) {
		double aux = Math.sqrt(Math.pow(C.getEnergiaActiva(), 2) + Math.pow(C.getEnergiaReactiva(), 2));
		aux = C.getEnergiaActiva() / aux;
		if (aux > 0.8) {
			this.bonificado = true;
		} else {
			this.bonificado = false;
		}
	}

	public double getMontoFinal() {
		return montoFinal;
	}

	public void setFuenteDePrecios(CuadroTarifario fuenteDePrecios) {
		this.fuenteDePrecios = fuenteDePrecios;
		//Originalmente estas funciones estaban en el constructor, pero como un Usuario no puede proveer el CuadroTarifario
		//Se calculan una vez que lo recibe de otro objeto (tests en este caso)
		this.determinarBonus(this.consumoDelUsuario);
		this.determinarMonto(this.consumoDelUsuario);
	}
}
