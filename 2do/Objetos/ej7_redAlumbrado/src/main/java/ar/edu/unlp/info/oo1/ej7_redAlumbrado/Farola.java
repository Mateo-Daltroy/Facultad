package ar.edu.unlp.info.oo1.ej7_redAlumbrado;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.*;

public class Farola {
	private boolean prendida = false;
	private List<Farola> vecinos = new ArrayList<>();
	
	public void pairWithNeighbor(Farola otraFarola) {
		if (!this.vecinos.contains(otraFarola)) {
			this.vecinos.add(otraFarola);
			otraFarola.pairWithNeighbor(this);
		}
	}
	
	public void turnOn() {
		if (this.isOff()) {
			this.prendida = true;
			this.vecinos.stream().forEach(f -> f.turnOn());
			/*for (Farola f: vecinos) {
				f.turnOn();
			} */
		}
	}
	
	public void turnOff() {
		if (this.isOn()) {
			this.prendida = false;
			this.vecinos.stream().forEach(f -> f.turnOff());
		}
	}
	
	public boolean isOff() {
		return(!this.prendida);
	}
	
	public boolean isOn() {
		return(this.prendida);
	}
	
	public List<Farola> getNeighbors() {
		return(this.vecinos);
	}
}
