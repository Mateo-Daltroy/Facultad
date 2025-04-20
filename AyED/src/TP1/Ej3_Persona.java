package TP1;

public abstract class Ej3_Persona {
    private String nom;
    private String ap;
    private String email;

    public String tusDatos() {
        String aux = "Nombre: " + this.getNom() + ", Apellido: " + this.getAp() + " y email: " + this.getEmail();
        return(aux);
    }
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAp() {
        return ap;
    }

    public void setAp(String ap) {
        this.ap = ap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
