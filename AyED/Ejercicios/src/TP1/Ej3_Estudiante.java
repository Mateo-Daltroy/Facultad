package TP1;

public class Ej3_Estudiante extends Ej3_Persona {
    private int comision;
    private String direccion;

    @Override
    public String tusDatos(){
        String aux = super.tusDatos();
        aux += ". Comision " + this.getComision() + ", dir " + this.getDireccion();
        return(aux);
    }
    public int getComision() {
        return comision;
    }

    public void setComision(int comision) {
        this.comision = comision;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
