package ar.edu.unlp.info.oo1.ej20_liquidacionHaberes;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Empleado {

	private String nombre; //nom y ap
	private String cuil;
	private LocalDate nacimiento;
	private boolean hijos;
	private boolean conyuge;
	private ArrayList<Contrato> contratos = new ArrayList<Contrato>();
	
	public Empleado(boolean hijos, boolean conyuge) {
		this.hijos = hijos;
		this.conyuge = conyuge;
	}
	
	public int calcularAnt() {
		return(this.contratos.stream().mapToInt(Cont -> Cont.calcularLong()).sum());
	}
	
	public ReciboDeSueldo generarRecibo() {
		ReciboDeSueldo aux = new ReciboDeSueldo(this.nombre, this.cuil, this.calcularAnt(), this.contratos.getFirst().calcularBasico(this));
		return(aux);
	}
	
	public boolean sumarContrato(Contrato C) {
		if (this.contratos.stream().allMatch(Cont -> Cont.isVencido())) {
			this.contratos.addFirst(C);
			return(true);
		} else {
			System.out.println("No se puede agregar un nuevo contrato, ya hay uno vigente");
			return(false);
		}
	}
	
	public boolean isHijos() {
		return hijos;
	}

	public boolean isConyuge() {
		return conyuge;
	}
}
