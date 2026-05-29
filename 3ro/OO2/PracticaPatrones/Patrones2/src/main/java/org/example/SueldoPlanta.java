package org.example;

public class SueldoPlanta extends SueldoTemplate {
    private int antiguedad;

    public SueldoPlanta(boolean casado, double cantHijos, int ant) {
        super(casado, cantHijos);
        this.antiguedad = ant;
    }

    protected double sueldoBasico() {
        return 50000;
    }

    @Override
    protected double adicional() {
        return super.adicional() + (this.antiguedad * 2000);
    }
}
