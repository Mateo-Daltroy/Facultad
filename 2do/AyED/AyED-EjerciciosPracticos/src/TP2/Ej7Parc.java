package TP2;

import Testers.BinaryTree;

public class Ej7Parc {
    private BinaryTree<Integer> arb;

    public Ej7Parc(BinaryTree<Integer> unArb) {
        this.arb = unArb;
    }

    public boolean isLeftTree(int num) {
        BinaryTree<Integer> nodo = this.buscarNodo(num, this.arb);
        if (nodo == null) {
            return false; //Si no se encontro el nodo, false
        }
        return(isLeftTree(nodo));
    }

    private boolean isLeftTree(BinaryTree<Integer> arb) {
        int hijosUnicosIzq = -1;
        int hijosUnicosDer = -1;
        if (arb.hasLeftChild()) {
            hijosUnicosIzq += contarHijosUnicos(arb.getLeftChild());
        }
        if (arb.hasRightChild()) {
            hijosUnicosDer += contarHijosUnicos(arb.getRightChild());
        }
        return (hijosUnicosIzq > hijosUnicosDer);
    }

    private int contarHijosUnicos(BinaryTree<Integer> arb) {
        int hijosUnicos = 0;
        if (arb.hasRightChild()) {
            hijosUnicos += contarHijosUnicos(arb.getRightChild());
        }
        if (arb.hasLeftChild()) {
            hijosUnicos += contarHijosUnicos(arb.getLeftChild());
        }
        if ((arb.hasLeftChild() && !arb.hasRightChild()) || (!arb.hasLeftChild() && arb.hasRightChild())) {
            hijosUnicos++;
        }
        return(hijosUnicos);
    }

    private BinaryTree<Integer> buscarNodo(int num, BinaryTree<Integer> arb) {
        BinaryTree<Integer> aux;
        if (arb.getData() == num) {
            return(arb);
        }
        if (arb.hasLeftChild()) {
            aux = buscarNodo(num, arb.getLeftChild());
            if (aux != null) {
                return(aux);
            }
        }
        if (arb.hasRightChild()) {
            aux = buscarNodo(num, arb.getRightChild());
            if (aux != null) {
                return(aux);
            }
        }
        return (null);
    }
}
