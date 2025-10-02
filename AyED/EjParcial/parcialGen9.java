public class ParcialArboles {

    public static boolean esDeSeleccion(GeneralTree<Integer> arbol) {
	boolean aux = true;
	if (arbol.hasChildren()) {
	    int min = 9999;
	    int i = 0;
	    int max = arbol.getChildren().size();
	    List<GeneralTree<Integer>> child = arbol.getChildren();
	    while (aux && (i < max)) {
		GeneralTree<Integer> nodoAct = child.get(i);

		aux = esDeSeleccion(nodoAct); // Recursividad, todos los hijos tienen que ser de seleccion
		if (nodoAct.getData() < min) {
		    min = nodoAct.getData();
		    if (min < arbol.getData()) { return false } //Se encontro un nodo menor al padre entre los hijos
		}

		i++;
	    }
	}
	if (arbol.getData() != min) { return false } // El nodo menor no coincide con el padre
	return(aux);
    }
}


