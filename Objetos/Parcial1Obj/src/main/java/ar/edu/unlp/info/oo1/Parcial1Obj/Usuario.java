package ar.edu.unlp.info.oo1.Parcial1Obj;

import java.util.ArrayList;
import java.time.LocalDate;

public class Usuario {
	
	private String nombre;
	private ArrayList<Entrada> entradas;
	
	public Usuario (String unNombre) {
		this.nombre = unNombre;
		this.entradas = new ArrayList<>();
	}
	
	public Entrada comprarEntrada(Evento unEvento, boolean unSeguro) {
		Entrada E = new Entrada(unEvento, unSeguro, LocalDate.now());
		this.entradas.add(E);
		return(E);
	}
	
	public double getMontoTotal(DateLapse lapso) {
		return (this.entradas.stream().filter(E -> lapso.includesDate(E.getFechaEvento())).mapToDouble(E -> E.getCosto()).sum());
	}
	
	public Entrada getSiguienteEvento() {
		ArrayList<Entrada> listaAux = (this.entradas.stream().filter(E -> !E.eventoTranscurrido())
				.sorted((ex1, ex2) ->  ex1.getFechaEvento().compareTo(ex2.getFechaEvento())).collect(null));
		return(listaAux.getFirst());
	}

}
