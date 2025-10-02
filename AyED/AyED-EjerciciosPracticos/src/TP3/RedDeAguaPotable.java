package TP3;

import Testers.GeneralTree;
import TP1.Ejercicio8.Queue;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class RedDeAguaPotable {
    private GeneralTree<Character> arbol;

    public RedDeAguaPotable(GeneralTree<Character> arb) {
        this.arbol = arb;
    }

    public double minimoCaudalRecursivo(double caudal) {
        HashMap<GeneralTree<Character>, Double> mapa = new HashMap<>();
        return(minimoCaudalRecursivo(this.arbol, caudal));
    }

    public double minimoCaudalRecursivo(GeneralTree<Character> arbol, double caudal) {
        double aux = 9999;
        if (arbol.isLeaf()) {
            return(caudal);
        } else {
            if (arbol.hasChildren()) {
                double caudalHijos = caudal / arbol.getChildren().size();
                for (GeneralTree<Character> child : arbol.getChildren()) {
                    aux = Math.min(aux, minimoCaudalRecursivo(child, caudalHijos));
                }
            }
        }
        return(aux);
    }

    public double minimoCaudalPorNivel(double caudal) {
        double minimo = caudal + 1;
        //Caudales de c/u
        HashMap<GeneralTree<Character>, Double> mapa = new HashMap<>();
        //Set cola
        Queue<GeneralTree<Character>> cola = new Queue<>();
        cola.enqueue(this.arbol);
        cola.enqueue(null);
        mapa.put(this.arbol, caudal);
        while (!cola.isEmpty()) {
            GeneralTree<Character> datoAct = cola.dequeue();
            if (datoAct != null) {
                if (datoAct.isLeaf()) {
                    System.out.println("Hoja " + datoAct.getData() + " " + mapa.get(datoAct));
                    if (mapa.get(datoAct) < minimo) {
                        minimo = mapa.get(datoAct);
                    }
                } else if (datoAct.hasChildren()) {
                    double caudalHijos = mapa.get(datoAct) / datoAct.getChildren().size();
                    for (GeneralTree<Character> child : datoAct.getChildren()) {
                        mapa.put(child, caudalHijos);
                        cola.enqueue(child);
                    }
                }
            } else {
                if (!cola.isEmpty()) {
                    cola.enqueue(null);
                }
            }
        }
        return(minimo);
    }
}
