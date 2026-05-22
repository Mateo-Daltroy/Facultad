package ar.edu.unlp.info.oo1.ej15_distribuidoraElectrica;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Usuario {
	
	private String nombre;
	private String domicilio;
	private ArrayList<Consumo> consumos = new ArrayList<>();
	
	public FacturaDeCobro generarFactura() {
		FacturaDeCobro f = new FacturaDeCobro(this.consumos.getLast(), LocalDate.now(), this);
		return(f);
	}
	
	public void agregarConsumo (Consumo C) {
		this.consumos.add(C);
	}
}
