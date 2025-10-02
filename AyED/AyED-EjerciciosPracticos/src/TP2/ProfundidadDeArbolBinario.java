package TP2;

import TP1.Ejercicio8.Queue;
import Testers.BinaryTree;

public class ProfundidadDeArbolBinario {
    private BinaryTree<Integer> arb;

    public ProfundidadDeArbolBinario(BinaryTree<Integer> arb) {
        this.arb = arb;
    }

    public int sumaElementosProfundidad(int p) {
        int tot = 0;
        Queue<BinaryTree<Integer>> cola = new Queue<>();
        int profAct = 0;
        cola.enqueue(arb);
        cola.enqueue(null);
        while (profAct < p && !cola.isEmpty()) {
            BinaryTree<Integer> datoAct = cola.dequeue();
            if (datoAct != null) {
                if (datoAct.hasRightChild()) {
                    cola.enqueue(datoAct.getRightChild());
                }
                if (datoAct.hasLeftChild()) {
                    cola.enqueue(datoAct.getLeftChild());
                }
            } else if (!cola.isEmpty()){
                cola.enqueue(null);
                profAct++;
            }
        }
        if (!cola.isEmpty()) {
            BinaryTree<Integer> contado = cola.dequeue();
            while (contado != null) {
                tot += contado.getData();
                contado = cola.dequeue();
            }
        }
        return(tot);
    }
}
