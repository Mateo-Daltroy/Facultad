package TP2;

import Testers.BinaryTree;

public class Transformacion {
    private BinaryTree<Integer> arb;

    public Transformacion(BinaryTree<Integer> arb) {
        this.arb = arb;
    }

    public BinaryTree<Integer> suma() {
        if (!this.arb.isEmpty()) {
            return(suma(arb));
        }
        else return null;
    }

    private BinaryTree<Integer> suma(BinaryTree<Integer> arb) {
        BinaryTree<Integer> nodo = new BinaryTree<>();
        int tot = 0;
        if (arb.hasLeftChild()) {
            tot += sumarArbol(arb.getLeftChild());
            nodo.addLeftChild(suma(arb.getLeftChild()));
        }
        if (arb.hasRightChild()) {
            tot += sumarArbol(arb.getRightChild());
            nodo.addRightChild(suma(arb.getRightChild()));
        }
        nodo.setData(tot);
        return(nodo);
    }

    private int sumarArbol(BinaryTree<Integer> arb) {
        int tot = arb.getData();
        if (arb.hasRightChild()) {
            tot += sumarArbol(arb.getRightChild());
        }
        if (arb.hasLeftChild()) {
            tot += sumarArbol(arb.getLeftChild());
        }
        return(tot);
    }
}
