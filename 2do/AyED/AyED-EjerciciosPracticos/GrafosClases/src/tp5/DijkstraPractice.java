package tp5;

import tp5.ejercicio1.Edge;
import tp5.ejercicio1.Graph;
import tp5.ejercicio1.Vertex;

import java.util.ArrayList;
import java.util.List;

// Implementa el algoritmo de Dijkstra base, explota con negativos y no es optimo para grafos aciclicos
public class DijkstraPractice {
    private Graph<String> grafo;

    public DijkstraPractice(Graph<String> grafo) {
        this.grafo = grafo;
    }

    public List<String> caminoMasCorto(String s, String w) {
        boolean[] marca = new boolean[this.grafo.getSize()]; // guarda si fueron visitados, inicializado en false automaticamente
        int[] minimo = new int[this.grafo.getSize()]; // guarda el camino minimo
        Vertex<String>[] anterior = new Vertex[this.grafo.getSize()];
        inicializarMinimo(minimo, s, this.grafo.getVertices());
        dijkstra(marca, minimo, anterior, s, w);
        return(formarLista(anterior, s, w));
    }

    private void dijkstra(boolean[] visitados, int[] minimo, Vertex<String>[] anterior, String s, String w) {
        for (int i = 0; i < minimo.length; i++) {
            Vertex<String> menor = menorDesconocidoLista(visitados, minimo);
            if (menor == null) break;
            visitados[menor.getPosition()] = true;
            for (Edge<String> arista: this.grafo.getEdges(menor)) {
                Vertex<String> act = arista.getTarget();
                int pos = act.getPosition();
                if (!visitados[pos] && (minimo[pos] > minimo[menor.getPosition()] + arista.getWeight())) {
                    minimo[pos] = minimo[menor.getPosition()] + arista.getWeight();
                    anterior[pos] = menor;
                }
            }
        }
    }

    private List<String> formarLista(Vertex<String>[] anterior, String s, String w) {
        List<String> lista = new ArrayList<>();
        Vertex<String> nodo = this.grafo.search(w);
        while ((nodo != null) && (nodo != this.grafo.search(s))) {
            lista.addFirst(nodo.getData());
            nodo = anterior[nodo.getPosition()];
        }
        if (nodo == null) { // no hay camino
            return(new ArrayList<>());
        }
        lista.addFirst(nodo.getData());
        return(lista);
        // esto funciona en los casos felices, pero en el caso de que:
        // llegue un grafo vacio/sin camino -> explota
    }

    private void inicializarMinimo(int[] minimo, String s, List<Vertex<String>> lista) {
        for (int i = 0; i < minimo.length; i++) {
            if (lista.get(i).getData().equals(s)) {
                minimo[i] = 0;
            }
            else {
                minimo[i] = Integer.MAX_VALUE;
            }
        }
    }

    private Vertex<String> menorDesconocidoLista(boolean[] visitados, int[] minimo) {
        int min = Integer.MAX_VALUE;
        Vertex<String> vertice = null; // nunca llega null pero es para el compilador
        for (int i = 0; i < minimo.length; i++) {
            if (!visitados[i] && minimo[i] <= min) {
                min = minimo[i];
                vertice = this.grafo.getVertex(i);
            }
        }
        return(vertice);
    }
}
