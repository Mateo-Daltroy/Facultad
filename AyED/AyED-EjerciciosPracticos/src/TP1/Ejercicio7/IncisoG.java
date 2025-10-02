package TP1.Ejercicio7;

import java.util.LinkedList;
import java.util.List;

public class IncisoG {

    public List<Integer> calcularSucesion (int n) {
        List<Integer> lista = new LinkedList<>();
        lista.add(n);
        if (n != 1) {
           if (esImpar(n)) {
               n = (3*n) + 1;
           } else {
               n = n/2;
           }
           lista.addAll(calcularSucesion(n));
        }
        return(lista);
    }

    private boolean esImpar(int n) {
        return (n % 2 == 1);
    }
}
