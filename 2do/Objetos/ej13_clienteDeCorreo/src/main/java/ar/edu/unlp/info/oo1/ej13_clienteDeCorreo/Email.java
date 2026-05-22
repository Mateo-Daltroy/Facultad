package ar.edu.unlp.info.oo1.ej13_clienteDeCorreo;

import java.util.ArrayList;
import java.util.List;

public class Email {
	private String titulo;
	private String cuerpo;
	private ArrayList<Archivo> adjuntos = new ArrayList<>();
	
	public Email (String unTitulo, String unCuerpo) {
		this.titulo = unTitulo;
		this.cuerpo = unCuerpo;
	}
	
	public ArrayList<Archivo> adjuntos() {
		return(this.adjuntos); // Return de la estructura?
	}
	
	public int espacioOcupado() {
		int archivos = this.adjuntos.stream().mapToInt(A -> A.tamano()).sum();
		return(this.titulo.length() + this.cuerpo.length() + archivos);
	}
	
	public boolean contiene(String texto) {
		if (this.titulo.contains(texto) || this.cuerpo.contains(texto)) {
			return(true);
		} else {
			return(false);
		}
	}
	
	public String getTitulo() {
		return titulo;
	}

	public String getCuerpo() {
		return cuerpo;
	}
	
	// Modulo de testeo
	public void agregarArchivo (Archivo A) {
		this.adjuntos.add(A);
	}

}
