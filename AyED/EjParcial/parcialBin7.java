
El método devuelve true si el subárbol cuya raíz es “num”, tiene en su subárbol izquierdo una cantidad mayor estricta de árboles con un único hijo que en susubárbol derecho. Y false en caso contrario. Consideraciones:
    ●Si “num” no se encuentra en el árbol, devuelve false.

    ●Si el árbol con raíz “num” no cuenta con una de sus ramas, considere que en esa rama hay -1 árboles con único hijo.


public class ParcialArboles {
    private BinaryTree<Integer> arb;

    public boolean isLeftTree(int num) {
	BinaryTree<Integer> arbol = encontrarNodo(arb, num);
	if (arbol == null) { // No se encontro el nodo
	    return(false);
	}
	return(isLeftTree(arbol));
    }

    public boolean isLeftTree(BinaryTree<Integer> arbol) {
	int hijosIzq;
	int hijosDer;
	if (arbol.hasLeftChild()) {
	    hijosIzq = contarHijos(arbol.getLeftChild());
	} else {
	    hijosIzq = -1;
	}
	if (arbol.hasRightChild()) {
	    hijosDer = contarHijos(arbol.getRightChild());
	} else {
	    hijosDer = -1;
	}
	return(hijosIzq > hijosDer);
    }

    public int contarHijos(BinaryTree<Integer> arbol) {
	int cantHijos = 0;
	int aux = 0;
	if (arbol.hasLeftChild()) {
	    aux += contarHijos(arbol.getLeftChild());
	    cantHijos++;
	}
	if (arbol.hasRightChild()) {
	    aux += contarHijos(arbol.getRightChild());
	    cantHijos++;
	}
	if (cantHijos == 1) {
	    aux++;
	}
	return(aux);
    }

    public BinaryTree<Integer> encontrarNodo(BinaryTree<Integer> arb, int num) {
	BinaryTree<Integer> aux = null;
	if (arb.getData() == num) {
	    return(arb);
	}
	if (arb.hasLeftChild()) {
	    aux = encontrarNodo(arb.getLeftChild(), num);
	    if (aux != null) { //Este if es necesario para poder buscar tambien en la derecha, pero la derecha no lo necesita
		return(aux);
	    }
	}
	if (arb.hasRightChild()) {
	    aux = encontrarNodo(arb.getRightChild(), num);
	}
	return(aux);
    }
}
