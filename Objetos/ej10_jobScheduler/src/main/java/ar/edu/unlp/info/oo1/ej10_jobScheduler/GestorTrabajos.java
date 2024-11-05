package ar.edu.unlp.info.oo1.ej10_jobScheduler;

import java.util.ArrayList;
import java.util.List;

public abstract class GestorTrabajos {
	protected ArrayList<JobDescription> lista = new ArrayList<>();
	
	public boolean checkLista() {
		if (lista.isEmpty()) {
			System.out.println("No hay mas trabajos en la cola");
			return false;
		} else {
			return true;
		}
	}
	
	// Esto esta mal?
	public void add(JobDescription J) {
		this.lista.add(J);
	}
	
	public void remove(JobDescription J) {
		this.lista.remove(J);
	}
	
	// Rompe Encapsulamiento
	public List<JobDescription> getLista(){
		return(this.lista);
	}
		
	public abstract JobDescription next();

}
