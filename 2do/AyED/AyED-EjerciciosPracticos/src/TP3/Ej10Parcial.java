package TP3;

import Testers.*;
import java.util.List;
import java.util.ArrayList;

public class Ej10Parcial {

    public static List<Integer> resolver(GeneralTree<Integer> arbol) {
        if (arbol.isEmpty()) { return null; }
        List<Integer> aux = resolverRecursivo(arbol, 0);
        aux = borrarCeros(aux);
        return(aux);
    }

    private static List<Integer> resolverRecursivo(GeneralTree<Integer> arbol, int nivel) {
        List<Integer> aux = new ArrayList<>();
        if (arbol.hasChildren()) {
            int valorMax = -1;
            List<Integer> hijosMax = new ArrayList<>(); // New ArrList... para deshacerse de un "no inicializado" del compilador
            for (GeneralTree<Integer> child : arbol.getChildren()) {
                List<Integer> temp = resolverRecursivo(child, (nivel+1) );
                if (calcularValor(temp, nivel-1 ) > valorMax) {
                    hijosMax = temp;
                    //hijosMax = new ArrayList<>();
                    //hijosMax.addAll(temp);
                    valorMax = calcularValor(hijosMax, nivel-1 );
                }
            }
            aux.addAll(hijosMax);
        }
        aux.addFirst(arbol.getData());
        return(aux);
    }

    private static int calcularValor(List<Integer> lista, int nivel) {
        int tot = 0;
        for(int i = 0; i < lista.size(); i++) { // no uso un forEach porque quiero utilizar i
            tot += lista.get(i) * (nivel + i);
        }
        return(tot);
    }

    public static List<Integer> borrarCeros(List<Integer> lista) {
        ArrayList<Integer> elemARemover = new ArrayList<>();
        for(int i = 0; i < lista.size(); i++) {
            if (lista.get(i) == 0) {
                elemARemover.add(i);
            }
        }
        for(Integer cero : elemARemover) {
            lista.remove(cero);
        }
        return(lista);
    }
}
