package TP2;

import Testers.*;

public class Ej8Parcial {
    public static boolean esPrefijo(BinaryTree<Integer> arbol1, BinaryTree<Integer> arbol2) {
        boolean aux = true;
        if (arbol1.getData() != arbol2.getData()) {
            return(false); // Los datos no coinciden
        }
        if (arbol1.hasLeftChild()) {
            if (arbol2.hasLeftChild()) {
                aux = aux && esPrefijo(arbol1.getLeftChild(), arbol2.getLeftChild()); // aux = los sub-arboles izq son prefijos
            } else {
                return(false); // No tienen la misma estructura por izquierda
            }
        }
        if (arbol1.hasRightChild()) {
            if (arbol2.hasRightChild()) {
                aux = aux && esPrefijo(arbol1.getRightChild(), arbol2.getRightChild()); // aux = los sub-arboles izq ^ derecho son prefijos
            } else {
                return(false); // No tienen la misma estructura por derecha
            }
        }
        return(aux);
    }
}
