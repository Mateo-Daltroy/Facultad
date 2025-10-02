package tp5;

import tp5.ejercicio1.Edge;
import tp5.ejercicio1.Graph;
import tp5.ejercicio1.Vertex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DijkstraQueue {
    private Graph<String> grafo;

    public DijkstraQueue (Graph<String> grafo) {
        this.grafo = grafo;
    }

    public List<String> dijkstra(String ciudad1, String ciudad2) {
        List<String> lista = new ArrayList<>();
        if (!grafo.isEmpty()) {
            int[] dist = new int[grafo.getSize()];
            initDistancia(dist);
            Vertex<String> origen = grafo.search(ciudad1); // se asume que se encuentra
            Vertex<String>[] previo = new Vertex[grafo.getSize()];
            dist[origen.getPosition()] = 0;
            dijkstra(origen, dist, previo);
            lista = formarLista(previo, ciudad1, ciudad2);
        }
        return(lista);
    }

    private void dijkstra(Vertex<String> origen, int[] distancias, Vertex<String>[] previo) {
        Queue<Vertex<String>> cola = new LinkedList<>();
        cola.add(origen);
        while (!cola.isEmpty()) {
            Vertex<String> act = cola.remove();
            for (Edge<String> arista : this.grafo.getEdges(act)) {
                Vertex<String> ady = arista.getTarget();
                if (distancias[ady.getPosition()] > distancias[act.getPosition()] + arista.getWeight()) {
                    previo[ady.getPosition()] = act;
                    distancias[ady.getPosition()] = distancias[act.getPosition()] + arista.getWeight();
                    if (!cola.contains(ady)) {
                        cola.add(ady);
                    }
                }
            }
        }
    }

    private List<String> formarLista (Vertex<String>[] previo, String origen, String destino) {
        Vertex<String> act = this.grafo.search(destino); // se asume existe
        List<String> lista = new ArrayList<>();
        while ((act != null) && (act.getData() != origen)) {
            lista.addFirst(act.getData());
            act = previo[act.getPosition()];
        }
        if (act == null) return new ArrayList<>(); // no se encontro un camino
        lista.addFirst(origen);
        return(lista);
    }

    private void initDistancia(int[] dist) {
        for (int i = 0; i < dist.length; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
    }
}
