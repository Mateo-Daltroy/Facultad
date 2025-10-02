package tp5.ejercicio4;

import tp5.ejercicio1.Edge;
import tp5.ejercicio1.Graph;
import tp5.ejercicio1.Vertex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class VisitaOslo {

    public List<String> paseoEnBici (Graph<String> grafo, String destino, int maxTiempo, List<String> lugaresRestringidos) {
        boolean[] marca = new boolean[grafo.getSize()]; // java inicializa en false
        initMarca(grafo, marca, lugaresRestringidos);
        Vertex<String> fin = grafo.search(destino);
        Vertex<String> inicio = grafo.search("Ayuntamiento");
        if ((fin == null) || (inicio == null) || (grafo.isEmpty())) return new LinkedList<>();

        List<String> lista = new LinkedList<>();
        busqueda(grafo, inicio.getPosition(), fin.getPosition(), marca, lista, new LinkedList<String>(), maxTiempo, 0);

        return lista;
    }

    private void busqueda(Graph<String> grafo, int pos, int posFin, boolean[] marca, List<String> lista, List<String> listaAct, int maxTiempo, int tiempoAct) {
        if (lista.isEmpty()) {
            marca[pos] = true;
            Vertex<String> verticeIt = grafo.getVertex(pos);
            listaAct.addLast(verticeIt.getData());
            List<Edge<String>> aristas = grafo.getEdges(verticeIt);
            Iterator<Edge<String>> it = aristas.iterator();
            while (it.hasNext()) {
                Edge<String> arista = it.next();
                Vertex<String> target = arista.getTarget();
                int j = target.getPosition();
                if (!marca[j]) { // si no visite el nodo
                    if (j == posFin) {
                        lista.addAll(listaAct);
                    } else if (tiempoAct + arista.getWeight() <= maxTiempo) {
                        busqueda(grafo, j, posFin, marca, lista, listaAct, maxTiempo, tiempoAct + arista.getWeight());
                    }
                }
            }
            listaAct.remove(verticeIt.getData());
            marca[pos] = false;
        }
    }

    private void initMarca(Graph<String> grafo, boolean[] marca, List<String> lugaresRestringidos) {
        for(String lugar: lugaresRestringidos) {
            Vertex<String> vert = grafo.search(lugar);
            if (vert != null)
                marca[vert.getPosition()] = true;
        }
    }
}
