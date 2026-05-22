package ar.edu.unlp.info.oo1.ej11_inversor;

import java.util.List;
import java.util.ArrayList;

public class Inversor {
	private String nombre;
	private ArrayList<Inversion> inversiones = new ArrayList<>();
	
	public double valorActual() {
		return(this.inversiones.stream().mapToDouble(I -> I.valorActual()).sum());
	}
	
	public void agregarInversion(Inversion I) {
		this.inversiones.add(I);
	}
}
