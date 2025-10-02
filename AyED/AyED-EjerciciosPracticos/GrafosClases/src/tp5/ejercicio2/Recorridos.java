package tp5.ejercicio2;

import tp5.ejercicio1.Graph;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import tp5.ejercicio1.*;

public class Recorridos<T>{

    public List<T> dfs(Graph<T> grafo) {
        List<T> lista = new ArrayList<>();
        boolean[] marcas = new boolean[grafo.getSize()]; // java inicializa en false
        for (int i = 0; i < grafo.getSize(); i++) {
            if (!marcas[i]) {
                dfs(grafo, lista, i, marcas);
            }
        }
        return(lista);
    }

    public void dfs(Graph<T> grafo, List<T> lista, int i, boolean[] marcas) {
        marcas[i] = true;
        Vertex<T> vertice = grafo.getVertex(i);
        List<Edge<T>> aristas = grafo.getEdges(vertice);
        lista.add(vertice.getData());
        for (Edge<T> arista: aristas) {
            int pos = arista.getTarget().getPosition();
            if (!marcas[pos]) {
                dfs(grafo, lista, pos, marcas);
            }
        }
    }

    public List<T> bfs(Graph<T> grafo) {
        List<T> lista = new ArrayList<>();
        boolean[] marca = new boolean[grafo.getSize()];
        for (int i = 0; i < grafo.getSize(); i++) {
            if (!marca[i]) {
                lista.addAll(bfs(i, grafo, marca));
            }
        }
        return(lista);
    }

    public List<T> bfs(int i, Graph<T> grafo, boolean[] marca) {
        marca[i] = true;
        List<T> lista = new ArrayList<>();
        Queue<Vertex<T>> cola = new LinkedList<>();
        cola.add(grafo.getVertex(i));
        while (!cola.isEmpty()) {
            Vertex<T> vertice = cola.poll();
            lista.add(vertice.getData());
            List<Edge<T>> ady = grafo.getEdges(vertice);
            for (Edge<T> arista: ady) {
                if (!marca[arista.getTarget().getPosition()]) {
                    marca[arista.getTarget().getPosition()] = true;
                    cola.add(arista.getTarget());
                }
            }
        }
        return(lista);
    }

    /*
    public List<T> bfs(Graph<T> grafo) {
        List<T> lista = new ArrayList<>();
        Queue<Vertex<T>> cola = new LinkedList<>();
        boolean[] marcas = new boolean[grafo.getSize()]; // java inicializa en false
        cola.add(grafo.getVertex(1));
        while (!cola.isEmpty()) {
            Vertex<T> vertice = cola.poll();
            int pos = vertice.getPosition();
            marcas[pos] = true;
            lista.add(vertice.getData());
            List<Edge<T>> ady = grafo.getEdges(vertice);
            for (Edge<T> arista: ady) {
                if (!marcas[arista.getTarget().getPosition()]) {
                    cola.add(arista.getTarget());
                }
            }
        }
        return (lista);
    }
     */
}
