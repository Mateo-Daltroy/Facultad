package TP2;

import Testers.*;

public class Ej9Parcial {

    public static BinaryTree<Record> sumAndDif(BinaryTree<Integer> arbol) {
        if (arbol.isEmpty()) {
            return(null);
        }
        return(generarArbol(arbol, 0, 0));
    }

    private static BinaryTree<Record> generarArbol(BinaryTree<Integer> arbol, int suma, int padre) {
        int sumaAux = suma + arbol.getData();
        int diffAux = arbol.getData() - padre;
        Record r = new Record(sumaAux, diffAux);
        BinaryTree<Record> arb = new BinaryTree<>(r);
        if (arbol.hasLeftChild()) {
            arb.addLeftChild(generarArbol(arbol.getLeftChild(), sumaAux, arbol.getData()));
        }
        if (arbol.hasRightChild()) {
            arb.addRightChild(generarArbol(arbol.getRightChild(), sumaAux, arbol.getData()));
        }
        return(arb);
    }
}
