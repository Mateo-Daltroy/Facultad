package TP3;

import Testers.GeneralTree;
import TP1.Ejercicio8.Queue;

public class AnalizadorArbol {

    public static double devolverMaximoPromedio(GeneralTree<AreaEmpresa> arbol) {
        double maximo = -1;
        int cantNod = 0;
        int totAct = 0;
        Queue<GeneralTree<AreaEmpresa>> cola = new Queue<>();
        cola.enqueue(arbol);
        cola.enqueue(null);
        while (!cola.isEmpty()) {
            GeneralTree<AreaEmpresa> datoAct = cola.dequeue();
            if (datoAct != null) {
                cantNod++;
                totAct += datoAct.getData().getDelay();
                if (datoAct.hasChildren()) {
                    for (GeneralTree<AreaEmpresa> child: datoAct.getChildren()) {
                        cola.enqueue(child);
                    }
                }
            } else {
                if (!cola.isEmpty()) {
                    cola.enqueue(null);
                }
                if ((double) totAct / cantNod > maximo) {
                    maximo = (double) totAct / cantNod;
                }
                totAct = 0;
                cantNod = 0;
            }
        }
        return(maximo);
    }
}
