package org.example;

public class SueldoTemporario extends SueldoTemplate {
    private int horasTrabajadas;

    public SueldoTemporario(boolean casado, double cantHijos, int horas) {
        super(casado, cantHijos);
        this.horasTrabajadas = horas;
    }

    protected double sueldoBasico() {
        return 20000 + 300 * horasTrabajadas;
    }
}
