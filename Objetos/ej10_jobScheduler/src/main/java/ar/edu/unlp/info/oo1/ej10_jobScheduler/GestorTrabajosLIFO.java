package ar.edu.unlp.info.oo1.ej10_jobScheduler;

public class GestorTrabajosLIFO extends GestorTrabajos{
	
	public JobDescription next() {
		System.out.println("3");
		if (this.checkLista()) { 
			JobDescription aux = lista.getLast();
			lista.remove(aux);
			return(aux);
		} else {
			return null;
		}
	}
}
