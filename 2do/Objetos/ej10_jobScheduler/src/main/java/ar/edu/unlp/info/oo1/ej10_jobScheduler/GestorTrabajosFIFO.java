package ar.edu.unlp.info.oo1.ej10_jobScheduler;

public class GestorTrabajosFIFO extends GestorTrabajos {
	
	public JobDescription next() {
		System.out.println("2");
		if (this.checkLista()) {
			JobDescription aux = lista.getFirst();
			System.out.println(aux.toString());
			lista.remove(aux);
			return(aux);
		} else {
			System.out.println("null");
			return(null); //preguntar por este return
		}
	}

}
