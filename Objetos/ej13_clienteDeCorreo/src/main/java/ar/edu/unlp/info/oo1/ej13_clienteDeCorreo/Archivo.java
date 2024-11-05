package ar.edu.unlp.info.oo1.ej13_clienteDeCorreo;

public class Archivo {
	private String nombre;
	
	public Archivo(String unNombre) {
		this.nombre = unNombre;
	}
	
	public int tamano() {
		return(this.nombre.length());
	}
}
