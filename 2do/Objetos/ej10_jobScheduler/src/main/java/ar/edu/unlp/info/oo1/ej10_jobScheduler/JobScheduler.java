package ar.edu.unlp.info.oo1.ej10_jobScheduler;

import java.util.ArrayList;
import java.util.List;

public class JobScheduler {

	protected GestorTrabajos jobs;
	private boolean estrategiaDefinida = false;

    public JobScheduler () {
        this.jobs = new GestorTrabajosFIFO();
    }

    public void schedule(JobDescription job) {
        this.jobs.add(job);
    }

    public void unschedule(JobDescription job) {
        if (job != null) {
            this.jobs.remove(job);
        }
    }

    public List<JobDescription> getJobs(){ 
        return jobs.getLista();
    }

    public void setStrategy(GestorTrabajos unoDefinido) { 
    //public void setStrategy(String aStrategy) { 
    	if (this.estrategiaDefinida) {
    		System.out.println("Ya se definio la estrategia de este scheduler");
    	} else {
    		this.jobs = unoDefinido;
    		/*this.estrategiaDefinida = true;
	        switch (aStrategy) {
	        case "FIFO":
	        	jobs = new GestorTrabajosFIFO();
	        case "LIFO":
	        	jobs = new GestorTrabajosLIFO();
	        case "HighestPriority":
	        	jobs = new GestorTrabajosPriority();
	        case "MostEffort":
	        	jobs = new GestorTrabajosEffort();
	        }*/
    	}
    }

    public JobDescription next() {
        JobDescription nextJob = this.jobs.next(); //Esta llamada parece no funcionar
        System.out.println("1");
        return(nextJob);
    }

}
