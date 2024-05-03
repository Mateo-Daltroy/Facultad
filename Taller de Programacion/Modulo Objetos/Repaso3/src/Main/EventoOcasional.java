/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

/**
 *
 * @author mateo
 */
public class EventoOcasional extends Recital {
    private String motivo;
    private String fecha;
    private String contratante;

    public EventoOcasional(String motivo, String fecha, String contratante, String nomBanda, int dimF) {
        super(nomBanda, dimF);
        this.motivo = motivo;
        this.fecha = fecha;
        this.contratante = contratante;
    }
    
    public void actuar(){
        String aux = "";
        if (this.getMotivo().equals("a beneficio")){
            aux+= "Recuerden colaborar con " + this.getContratante();
        } else if (this.getMotivo().equals("show de TV")){
            aux += "Saludos amigos televidentes";
        } else {
            aux += "Un feliz cumpleaños para " + this.getContratante();
        }
        System.out.println(aux);
        super.actuar();
    }
    
    public double calcularCosto(){
        if (this.getMotivo().equals("a beneficio")){
            return(0);
        } else if (this.getMotivo().equals("show de TV")){
            return(50000);
        } else {
            return(150000);
        }
    }

    public String getMotivo() {
        return motivo;
    }

    public String getContratante() {
        return contratante;
    }
}
