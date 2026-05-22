package ar.edu.unlp.info.oo1.ej20_liquidacionHaberes;

public class ContratoPlanta extends Contrato{

	private double sueldoMensual;
	private double montoHijos;
	private double montoConyuge;
	
	public ContratoPlanta(double hijos, double conyuge, double sueldo, int anios) {
		super(anios);
		this.montoConyuge = conyuge;
		this.montoHijos = hijos;
		this.sueldoMensual = sueldo;
	}
	
	public double calcularBasico(Empleado e) {
		double sueldo = this.sueldoMensual;
		if (e.isHijos()) {
			sueldo += this.montoHijos;
		}
		if (e.isConyuge()) {
			sueldo += this.montoConyuge;
		}
		return(sueldo);
	}
	
	public boolean isVencido() {
		return(false);
	}
}
