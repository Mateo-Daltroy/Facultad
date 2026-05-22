package tp5;

import tp5.ejercicio1.Edge;
import tp5.ejercicio1.Graph;
import tp5.ejercicio1.Vertex;

import java.util.*;

public class DijkstraTopologico {
    private Graph<String> grafo;

    public DijkstraTopologico(Graph<String> grafo) {
        this.grafo = grafo;
    }

    public List<String> dijkstra (String ciudad1, String ciudad2) {
        if (this.grafo.isEmpty()) return new ArrayList<>();
        Vertex<String>[] topol = topologizar();
        int[] dist = new int[this.grafo.getSize()];
        initDist(dist);
        Vertex<String>[] previos = new Vertex[this.grafo.getSize()];
        //dijkstra(previos, this.grafo.search(ciudad1), this.grafo.search(ciudad2));
        return(formarLista(previos, ciudad1, ciudad2));
    }

    private void initDist (int[] dist) {
        dist[0] = 0;
        for (int i = 1; i < this.grafo.getSize(); i++) {
            dist[i] = Integer.MAX_VALUE;
        }
    }

    private List<String> formarLista(Vertex<String>[] previos, String ciudad1, String ciudad2) {
        List<String> lista = new ArrayList<>();
        Vertex<String> datoAct = previos[this.grafo.search(ciudad2).getPosition()];
        while ((datoAct != null) && (!datoAct.getData().equals(ciudad1))) {
            lista.addFirst(datoAct.getData());
            datoAct = previos[datoAct.getPosition()];
        }
        if (datoAct == null) return new ArrayList<>(); //no hay camino valido
        lista.addFirst(datoAct.getData());
        lista.addLast(this.grafo.search(ciudad2).getData());
        return(lista);
    }

    private Vertex<String>[] topologizar() {
        Queue<Vertex<String>> cola = new LinkedList<>();
        Vertex<String>[] topologia = new Vertex[this.grafo.getSize()];
        boolean[] marca = new boolean[this.grafo.getSize()];
        dfs(this.grafo.getVertex(0), cola, marca);//implementar en preorden
        for (int i = 0; i < this.grafo.getSize(); i++){
            topologia[i] = cola.remove();
        }
        return topologia;
    }

    private void dfs(Vertex<String> vertice, Queue<Vertex<String>> cola, boolean[] marca) {
        marca[vertice.getPosition()] = true;
        cola.add(vertice);
        for (Edge<String> ady : this.grafo.getEdges(vertice)) {
            Vertex<String> verAct = ady.getTarget();
            if (!marca[verAct.getPosition()]) {
                dfs(verAct, cola, marca);
            }
        }
    }
}
