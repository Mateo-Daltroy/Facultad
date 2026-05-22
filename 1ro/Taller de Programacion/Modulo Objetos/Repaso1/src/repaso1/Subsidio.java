/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repaso1;

/**
 *
 * @author mateo
 */
public class Subsidio {
    private boolean otorgado = false;
    private double monto;
    private String motivo;

    public Subsidio(double monto, String motivo) {
        this.monto = monto;
        this.motivo = motivo;
    }

    public boolean isOtorgado() {
        return otorgado;
    }

    public void setOtorgado(boolean otorgado) {
        this.otorgado = otorgado;
    }

    public double getMonto() {
        return monto;
    }
    
    public String getMotivo() {
        return motivo;
    }
}
