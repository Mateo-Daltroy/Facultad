package ar.edu.unlp.info.oo1.ejercicio2Bal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Ticket {
	private LocalDate fecha;
	private int cantidadDeProductos;
	private double pesoTotal;
	private double precioTotal;
	private List<Producto> productos = new ArrayList<Producto>();
	
	public Ticket(LocalDate fecha, int cant, double peso, double precio, List<Producto> productos) {
		this.fecha = fecha;
		this.cantidadDeProductos = cant;
		this.pesoTotal = peso;
		this.precioTotal = precio;
		this.productos = productos;
	}
	
	public double impuesto() {
		return(this.precioTotal * 0.21);
	}

	public double getPesoTotal() {
		return pesoTotal;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public int getCantidadDeProductos() {
		return cantidadDeProductos;
	}

	public List<Producto> getProductos() {
		return productos;
	}

	public double getPrecioTotal() {
		return precioTotal;
	}
}
