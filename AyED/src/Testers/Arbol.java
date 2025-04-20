package Testers;

public interface Arbol<T> {
    public void printPosOrden();
    public void printPreOrden();
    public boolean isEmpty();
    public boolean isLeaf();
    public T getData();
    public void setData(T data);
}
