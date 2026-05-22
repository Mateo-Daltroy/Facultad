package ar.edu.unlp.info.oo1.ej10_jobScheduler;

public class GestorTrabajosEffort extends GestorTrabajos {
	
	public JobDescription next() {
		System.out.println("5");
		JobDescription aux = lista.stream().max((a1, a2) -> Double.compare(a1.getEffort(), a2.getEffort())).orElse(null); 
		this.lista.remove(aux);
		return(aux);
	}
}
