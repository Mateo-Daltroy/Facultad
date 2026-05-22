package ar.edu.unlp.info.oo1.ej6_mamiferos;

import java.time.LocalDate;

public class Mamifero {
	private String identificador;
	private String especie;
	private LocalDate fechaNacimiento;
	private Mamifero madre;
	private Mamifero padre;
	
	public Mamifero (String unNombre) {
		this.identificador = unNombre;
		this.fechaNacimiento = LocalDate.now();
	}
	
	public Mamifero () {
		
	}
	
	public Boolean tieneComoAncestroA(Mamifero unMamifero) {
		boolean res = false;
		if ((unMamifero == this.getMadre()) || (unMamifero == this.getPadre())) {
			return(true);
		} else {
			if (this.getMadre() != null) {
				res = this.getMadre().tieneComoAncestroA(unMamifero);
			}
			if (!res && this.getPadre() != null) {
				res = this.getPadre().tieneComoAncestroA(unMamifero);
			}
		}
		return(res);
	}
	
	public Mamifero getAbueloMaterno() {
		if (this.getMadre() != null) {
			return(this.getMadre().getPadre());
		} else {
			return(null);
		}
	}
	
	public Mamifero getAbueloPaterno() {
		if (this.getPadre() != null) {
			return(this.getPadre().getPadre());
		} else {
			return(null);
		}
	}
	
	public Mamifero getAbuelaMaterna() {
		if (this.getMadre() != null) {
			return(this.getMadre().getMadre());
		} else {
			return(null);
		}
	}
	
	public Mamifero getAbuelaPaterna() {
		if (this.getPadre() != null) {
			return(this.getPadre().getMadre());
		} else {
			return(null);
		}
	}
	
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getEspecie() {
		return especie;
	}
	public void setEspecie(String especie) {
		this.especie = especie;
	}
	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	public Mamifero getMadre() {
		return madre;
	}
	public void setMadre(Mamifero madre) {
		this.madre = madre;
	}
	public Mamifero getPadre() {
		return padre;
	}
	public void setPadre(Mamifero padre) {
		this.padre = padre;
	}
	
	
}
