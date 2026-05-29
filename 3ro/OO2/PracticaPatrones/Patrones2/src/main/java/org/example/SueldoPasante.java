package org.example;

public class SueldoPasante extends SueldoTemplate {
    private int examenesRendidos;

    public SueldoPasante(boolean casado, double cantHijos, int exam) {
        super(casado, cantHijos);
        this.examenesRendidos = exam;
    }

    protected double sueldoBasico() {
        return 20000;
    }

    @Override
    protected double adicional() {
        return this.examenesRendidos * 2000;
    }
}
