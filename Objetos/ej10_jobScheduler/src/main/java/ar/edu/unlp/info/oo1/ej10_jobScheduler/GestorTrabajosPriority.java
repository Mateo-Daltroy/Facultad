package ar.edu.unlp.info.oo1.ej10_jobScheduler;

public class GestorTrabajosPriority extends GestorTrabajos {
	
	public JobDescription next() {
		System.out.println("4");
		JobDescription aux = lista.stream().max((a1, a2) -> Integer.compare(a1.getPriority(), a2.getPriority())).orElse(null);
		this.lista.remove(aux);
		return(aux);
	}
	
}
