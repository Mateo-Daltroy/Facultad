package org.example;

public abstract class SueldoTemplate {
    private boolean casado;
    private double cantHijos;

    public SueldoTemplate(boolean casado, double cantHijos) {
        this.cantHijos = cantHijos;
        this.casado = casado;
    }

    public double sueldo() {
        return this.sueldoBasico() + this.adicional() - this.descuento();
    }

    protected double descuento() {
        double basico = this.sueldoBasico() * 0.87;
        double adicional = this.adicional() * 0.95;
        return basico + adicional;
    }

    protected double adicional() {
        double hijoBonus = this.cantHijos * 2000;
        return casado ? (hijoBonus + 5000) : hijoBonus;
    }

    protected abstract double sueldoBasico();
}
