package TP1.Ejercicio8;

import java.util.ArrayList;
import java.util.List;

public class Queue<T> extends Sequence {

    private List<T> data;
    //Preguntar por el protected (lo necesita enqueueFirst)
    protected void enqueueFirst(T data) {
        this.data.addFirst(data);
    }

    public Queue() {
        this.data = new ArrayList<T>();
    }

    public void enqueue(T dato) {
        this.data.addLast(dato);
    }

    public T dequeue() {
        return(this.data.removeFirst());
    }

    public T head() {
        return(this.data.getFirst());
    }

    public int size() {
        return(this.data.size());
    }

    public boolean isEmpty() {
        return (this.data.isEmpty());
    }

    @Override
    public String toString() {
        String aux = "";
        for (T elem: this.data) {
            aux+= elem.toString() + ", ";
        }
        return(aux);
    }
}
