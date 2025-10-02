package TP1.Ejercicio8;

public class CircularQueue<T> extends Queue<T> {
    public CircularQueue () {
        super();
    }

    public T shift() {
        T aux = (T) this.dequeue();
        this.enqueue(aux);
        return(aux);
    }
}
