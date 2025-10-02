package tp5;

import tp5.ejercicio1.Edge;
import tp5.ejercicio1.Graph;
import tp5.ejercicio1.Vertex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ParcialDos {

    public Map<Integer, Boolean> nivelPopularidad(Graph<String> red, String usuario, int distancia, int umbral) {
        Vertex<String> base = red.search(usuario);
        if (base == null) return null;
        boolean[] marca = new boolean[red.getSize()];
        int cantidad = bfs(red, base, distancia, marca);
        HashMap<Integer, Boolean> resultado = new HashMap<>();
        if (cantidad >= umbral) {
            resultado.put(cantidad, true);
        } else {
            resultado.put(cantidad, false);
        }
        return(resultado);
    }

    private int bfs (Graph<String> red, Vertex<String> base, int distancia, boolean[] marca) {
        int contador = 0;
        int distAct = 0;
        Queue<Vertex<String>> cola = new LinkedList<>();
        marca[base.getPosition()] = true;
        cola.add(base);
        cola.add(null);
        while (!cola.isEmpty() && distAct < distancia) {
            Vertex<String> vertAct = cola.poll();
            if (vertAct == null) {
                distAct++;
                if (!cola.isEmpty())
                    cola.add(null);
            } else {
                marca[vertAct.getPosition()] = true;
                for (Edge<String> arista : red.getEdges(vertAct)) {
                    if (distAct == distancia-1) {
                        contador++;
                    } else if (!marca[arista.getTarget().getPosition()]) {
                        cola.add(arista.getTarget());
                    }
                }
            }
        }
        return contador;
    }
}
