package ar.edu.unlp.info.oo1.ejercicio2Bal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Balanza {
	private int cantidadDeProductos;
	private double precioTotal;
	private double pesoTotal;
	private List<Producto> productos = new ArrayList<Producto>();
	
	public void ponerEnCero() {
		this.pesoTotal = 0;
		this.precioTotal = 0;
		this.cantidadDeProductos = 0;
		this.productos.clear();
	}
	
	public void agregarProducto(Producto P) {
		this.precioTotal += P.getPrecio();
		this.pesoTotal += P.getPeso();
		this.productos.add(P);
		this.cantidadDeProductos++;
	}
	
	public Ticket emitirTicket() {
		Ticket T = new Ticket(LocalDate.now(), this.cantidadDeProductos, this.pesoTotal, this.precioTotal, this.productos);
		return(T);
	}

	public int getCantidadDeProductos() {
		return cantidadDeProductos;
	}

	public List<Producto> getProductos() {
		return productos;
	}
	// No es necesario almacenar el precio ni peso total ahora, pero es eficiente
	// A la hora de no tener que recorrer la lista cada vez que se desea el valor

	public double getPrecioTotal() {
		return precioTotal;
	}

	public double getPesoTotal() {
		return pesoTotal;
	}
}
