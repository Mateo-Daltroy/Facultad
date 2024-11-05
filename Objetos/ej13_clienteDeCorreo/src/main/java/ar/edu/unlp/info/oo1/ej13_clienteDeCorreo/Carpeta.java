package ar.edu.unlp.info.oo1.ej13_clienteDeCorreo;

import java.util.ArrayList;
import java.util.List;

public class Carpeta {
	private String nombre;
	private ArrayList<Email> emails = new ArrayList<>();
	
	public Carpeta (String nom) {
		this.nombre = nom;
	}
	
	public void agregar(Email email) {
		this.emails.add(email);
	}
	
	public boolean mover(Email email, Carpeta destino) {
		if (this.emails.stream().anyMatch(A -> A.equals(email))) {
			this.emails.remove(email);
			destino.agregar(email);
			return(true);
		} else {
			return (false);
		}
	}
	
	public int espacioOcupado() {
		return(emails.stream().mapToInt(A -> A.espacioOcupado()).sum());
	}
	
	public Email buscar(String texto) {
		return(this.emails.stream().filter
			(E -> (E.contiene(texto)))
			.findFirst().orElse(null));
	}

	public String getNombre() {
		return nombre;
	}
}
