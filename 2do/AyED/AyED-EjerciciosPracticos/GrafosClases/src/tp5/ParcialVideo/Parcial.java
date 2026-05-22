package tp5.ParcialVideo;

import tp5.ejercicio1.Edge;
import tp5.ejercicio1.Graph;
import tp5.ejercicio1.Vertex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Parcial {

    public int resolver(Graph<Sitio> sitios, int tiempo) {
        Vertex<Sitio> inicio = encontrar(sitios, "ENTRADA");

        if (sitios.isEmpty() || inicio == null)
            return 0;
        boolean[] marca = new boolean[sitios.getSize()];
        int max = 0;
        if (inicio.getData().tiempo < tiempo)
            dfs(sitios, inicio.getPosition(), marca, max, 0, tiempo, 0);
        return(max);
    }

    //Opcion 1, hacer una lista que guarde cada sitio y despues retornar lista.size()
    //Opcion 2, hacer un wrapper para maxAct para que el dato interno se sepa por referencia
    private void dfs(Graph<Sitio> sitios, int pos, boolean[] marca, int maxAct, int cont, int tiempoMax, int tiempoAct) {
        marca[pos] = true;
        cont++;
        if (cont > maxAct)
            maxAct = cont;
        tiempoAct += sitios.getVertex(pos).getData().tiempo;
        List<Edge<Sitio>> ady = sitios.getEdges(sitios.getVertex(pos));
        Iterator<Edge<Sitio>> it = ady.iterator();
        while (it.hasNext()) {
            Edge<Sitio> arista = it.next();
            int j = arista.getTarget().getPosition();
            if (!marca[j]) {
                if (tiempoMax <= tiempoAct + sitios.getVertex(j).getData().tiempo + arista.getWeight()) {
                    dfs(sitios, j, marca, maxAct, cont, tiempoMax, tiempoAct + arista.getWeight());
                }
            }
        }
        cont--;
        marca[pos] = false;
        tiempoAct-= sitios.getVertex(pos).getData().tiempo;
    }

    private Vertex<Sitio> encontrar(Graph<Sitio> sitios, String nombre) {
        List<Vertex<Sitio>> vertices = sitios.getVertices();
        for (Vertex<Sitio> sitio : vertices) {
            if (sitio.getData().nombre.equals(nombre))
                return sitio;
        }
        return(null);
    }
}
