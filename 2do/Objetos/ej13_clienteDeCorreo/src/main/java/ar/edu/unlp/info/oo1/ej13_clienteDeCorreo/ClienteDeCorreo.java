package ar.edu.unlp.info.oo1.ej13_clienteDeCorreo;

import java.util.ArrayList;
import java.util.List;

public class ClienteDeCorreo {
	private Carpeta inbox = new Carpeta("Inbox");
	private ArrayList<Carpeta> carpetas = new ArrayList<>();
	
	public void recibir(Email email) {
		this.inbox.agregar(email);
	}
	
	public Email buscar(String texto) { //Preguntar por el stream
		Email aux = this.carpetas.stream().map(C -> C.buscar(texto))
				.filter(E -> E != null).findFirst().orElse(null);
		System.out.println("1");
		if (aux == null) {
			System.out.println("3");
			aux = this.inbox.buscar(texto);
		}
		System.out.println("2");
		return(aux);
	}
	
	public int espacioOcupado() {
		return(this.carpetas.stream().mapToInt(C -> C.espacioOcupado()).sum() + this.inbox.espacioOcupado());
	}
	
	// Modulos de testing
	public void agregarCarpeta(Carpeta C) {
		this.carpetas.add(C);
	}
}
