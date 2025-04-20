package TP1.Ejercicio7;

import java.util.LinkedList;

public class IncisoI {
    private static int elemento = 0;

    public int sumarLinkedList(LinkedList<Integer> lista) {
        int cont = 0;
        int actual = lista.get(elemento);
        if (lista.size() > (elemento + 1)) {
            elemento++;
            cont = sumarLinkedList(lista);
        }
        return(cont + actual);
    }
}
