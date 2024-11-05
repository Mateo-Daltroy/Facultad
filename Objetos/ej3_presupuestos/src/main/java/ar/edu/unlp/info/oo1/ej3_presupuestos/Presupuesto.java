package ar.edu.unlp.info.oo1.ej3_presupuestos;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

public class Presupuesto {
	private LocalDate fecha;
	private String cliente;
	private List<Item> listaItems = new ArrayList<Item>();
	
	public Presupuesto(String cliente) {
		this.cliente = cliente;
		fecha = LocalDate.now();
	}
	
	public void agregarItem(Item item) {
		this.listaItems.add(item);
	}
	
	public double calcularTotal() {
		return (listaItems.stream().mapToDouble(Item::costo).sum());
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public String getCliente() {
		return cliente;
	}
	
}
