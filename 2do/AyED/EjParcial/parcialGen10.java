import java.utils.ArrayList;

public class ParcialArboles {

    public static List<Integer> resolver(GeneralTree<Integer> arbol) {
	if (arbol.isEmpty()) { return null; }
	List<Integer> aux = resolverRecusivo(arbol, 0);
	aux = borrarCeros(aux);
	return(aux);
    }

    public static List<Integer> resolverRecursivo(GeneralTree<Integer> arbol, int nivel) {
	List<Integer> aux = new ArrayList<>();
	if (arbol.hasChildren()) {
	    int valorMax = -1;
	    List<Integer> hijosMax;
	    for (GeneralTree<Integer> child : arbol.getChildren()) {
		List<Integer> temp = resolverRecursivo(child, (nivel+1) );
		if (calcularValor(temp, nivel-1 ) > valorMax) {
		    hijosMax = temp;
		    valorMax = calcularValor(hijosMax, nivel-1 );
		}
	    }
	    aux.addAll(hijosMax);
	}
	aux.addFirst(arbol.getData());
	return(aux);
    }

    public static int calcularValor(List<Integer> lista, int nivel) {
	int tot = 0;
	for(int i = 0; i < lista.size(); i++) { // no uso un forEach porque quiero utilizar i
	    tot += lista.get(i) * (nivel + i);
	}
    }

    public static List<Integer> borrarCeros(List<Integer> lista) {
	for(Integer cero : lista) {
	    if (cero == 0) {
		lista.remove(cero);
	    }
	}
	return(lista);
    }

}
