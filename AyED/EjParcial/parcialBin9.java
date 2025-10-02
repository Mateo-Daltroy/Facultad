// Clase utilizada en el arbol generado
public class Registro {
    private int suma;
    private int diff;

    public registro (int suma, int diff) {
	this.suma = suma;
	this.diff = diff;
    }

}

public class ParcialArboles {

    public BinaryTree<Registro> sumAndDif(BinaryTree<Integer> arbol) {
	if (arbol.isEmpty()) {
	    return(null);
	}
	return(generarArbol(arbol, 0, 0));
    }

    public BinaryTree<Registro> generarArbol(BinaryTree<Integer> arbol, int suma, int padre) {
	int sumaAux = suma + arbol.getData();
	int diffAux = arbol.getData() - padre; 
	Registro r = new Registro(sumaAux, diffAux);
	BinaryTree<Registro> arb = new BinaryTree<>(r);
	if (arbol.hasLeftChildren()) {
	    arb.addLeftChild(generarArbol(arbol.getLeftChild(), sumaAux, arbol.getData()));
	}
	if (arbol.hasRightChildren()) {
	    arb.addRightChild(generarArbol(arbol.getRightChild(), sumaAux, arbol.getData()));
	}
	return(arb);
    }

}
