package TP1;

public class Ej3_Profesor extends Ej3_Persona {
    private String catedra;
    private String facultad;

    @Override
    public String tusDatos() {
        String aux = super.tusDatos();
        aux += ". Catedra " + this.getCatedra() + ", en " + this.getFacultad();
        return(aux);
    }
    public String getCatedra() {
        return catedra;
    }

    public void setCatedra(String catedra) {
        this.catedra = catedra;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }
}
