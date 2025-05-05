package TP1.Ejercicio7;

import java.util.ArrayList;

public class IncisoH {
    public void invertirArrayList(ArrayList<Integer> lista) {
        int num = lista.removeFirst();
        if (lista.isEmpty()) {
            invertirArrayList(lista);
        }
        lista.addLast(num);
    }
}
