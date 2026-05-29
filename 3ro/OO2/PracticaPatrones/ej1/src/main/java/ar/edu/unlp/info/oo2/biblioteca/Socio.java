package ar.edu.unlp.info.oo2.biblioteca;

public class Socio {
    private String nombre;
    private String legajo;
    private String email;

    public Socio(String nombre, String email, String legajo) {
        this.nombre = nombre;
        this.email = email;
        this.legajo = legajo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public boolean contentEquals(Socio s) {
        if (s.getNombre() != this.nombre)
            return false;
        if (s.getLegajo() != this.legajo)
            return false;
        if (s.getEmail() != this.email)
            return false;
        return true;
    }
}
