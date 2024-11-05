package ar.edu.unlp.info.oo1.ej12_volumenYSuperficieDeSolidos;

import java.util.ArrayList;
import java.util.List;

public class ReporteDeConstruccion {
	
	ArrayList<Pieza> Piezas = new ArrayList<Pieza>();
	
	public double getVolumenDeMaterial(String nom) {
		double aux = this.Piezas.stream().filter( P -> P.getMaterial() == nom)
					.mapToDouble(P -> P.getVolumen()).sum();
		return(aux);
	}
	
	public double getSuperficieDeColor(String col) {
		double aux = this.Piezas.stream().filter(P -> P.getColor() == col)
					.mapToDouble( P -> P.getSuperficie()).sum();
		return(aux);
	}
	
	public void agregarPieza(Pieza P) { 
		this.Piezas.add(P);
	}
}
