package tp5.ejercicio3;

import tp5.ejercicio1.Edge;
import tp5.ejercicio1.Graph;
import tp5.ejercicio1.Vertex;
import tp5.ejercicio1.adjList.AdjListGraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Mapa {
    private Graph<String> grafo;

    public Mapa (Graph<String> map) {
        this.grafo = map;
    }

    // Parte 1
    public List<String> devolverCamino(String ciudad1, String ciudad2) {
        List<String> lista = new ArrayList<>();
        Vertex<String> vertice = this.grafo.search(ciudad1);
        boolean[] marca = new boolean[grafo.getSize()];
        dfs(vertice.getPosition(), ciudad2, marca, lista);
        return(lista);
    }

    private void dfs(int pos, String objetivo, boolean[] marca, List<String> lista) {
        marca[pos] = true;
        Vertex<String> vertice = this.grafo.getVertex(pos);
        lista.add(vertice.getData());
        List<Edge<String>> ady = this.grafo.getEdges(vertice);
        for (Edge<String> arista: ady) {
            Vertex<String> vert = arista.getTarget();
            if (!marca[vert.getPosition()] && !lista.contains(objetivo)) {
                dfs(vert.getPosition(), objetivo, marca, lista);
            }
        }
        if (!lista.contains(objetivo)) {
            lista.remove(vertice.getData());
        }
    }

    // Parte 2
    public List<String> devolverCaminoExceptuando(String ciudad1, String ciudad2, List<String> ciudades) {
        List<String> lista = new ArrayList<>();
        Vertex<String> vertice = this.grafo.search(ciudad1);
        boolean[] marca = new boolean[grafo.getSize()];
        marcarCiudades(ciudades, marca);
        dfs(vertice.getPosition(), ciudad2, marca, lista);
        return(lista);
    }

    private void marcarCiudades(List<String> ciudades, boolean[] marca) {
        for (String ciudad : ciudades)
            marca[this.grafo.search(ciudad).getPosition()] = true;
    }

    // Parte 3
    public List<String> caminoMasCorto(String ciudad1, String ciudad2) {
        Vertex<String> origen = this.grafo.search(ciudad1);
        Vertex<String> destino = this.grafo.search(ciudad2);

        int[] dist = new int[this.grafo.getSize()];
        Vertex<String>[] previos = new Vertex[this.grafo.getSize()];
        boolean[] marca = new boolean[this.grafo.getSize()];

        initDist(dist, ciudad1);
        dijkstraMin(previos, marca, dist, destino);
        return(formarLista(previos, origen, destino));
    }

    private List<String> formarLista(Vertex<String>[] previos, Vertex<String> origen, Vertex<String> destino) {
        List<String> lista = new LinkedList<>();
        Vertex<String> vertAct = destino;
        while ((vertAct != null) && (vertAct != origen)) {
            lista.addFirst(vertAct.getData());
            vertAct = previos[vertAct.getPosition()];
        }
        if (vertAct == null) return new ArrayList<>(); //no hay camino
        lista.addFirst(vertAct.getData());
        return(lista);
    }

    private void initDist(int[] dist, String origen) {
        for (int i = 0; i < dist.length; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        dist[this.grafo.search(origen).getPosition()] = 0;
    }

    private void dijkstraMin(Vertex<String>[] previos, boolean[] marca, int[] dist, Vertex<String> destino) {
        int i = 0;
        Vertex<String> vertAct = encontrarMenorNoVisitado(marca, dist);
        while(i < marca.length && vertAct != destino) {
            marca[vertAct.getPosition()] = true;
            for (Edge<String> arista : this.grafo.getEdges(vertAct)) {
                Vertex<String> ady = arista.getTarget();
                int pos = ady.getPosition();
                if (!marca[pos]) {
                    if (dist[pos] > dist[vertAct.getPosition()] + arista.getWeight()) {
                        dist[pos] = dist[vertAct.getPosition()] + arista.getWeight();
                        previos[pos] = vertAct;
                    }
                }
            }
            vertAct = encontrarMenorNoVisitado(marca, dist);
            i++;
        }
    }

    private Vertex<String> encontrarMenorNoVisitado(boolean[] marca, int[] dist) {
        int distMin = Integer.MAX_VALUE;
        Vertex<String> minAct = null;
        for (int i = 0; i < dist.length; i++) {
            if (!marca[i] && dist[i] <= distMin) {
                distMin = dist[i];
                minAct = this.grafo.getVertex(i);
            }
        }
        return minAct;
    }

    // parte 4
    // Asumo que tanqueAuto es la capacidad maxima del tanque y que arranca su camino lleno
    public List<String> caminoSinCargarCombustible(String ciudad1, String ciudad2, int tanqueAuto) {
        Vertex<String> origen = this.grafo.search(ciudad1);
        Vertex<String> destino = this.grafo.search(ciudad2);

        int[] dist = new int[this.grafo.getSize()];
        Vertex<String>[] previos = new Vertex[this.grafo.getSize()];
        boolean[] marca = new boolean[this.grafo.getSize()];

        initDist(dist, ciudad1);
        dijkstraMin(previos, marca, dist, destino);

        if (dist[destino.getPosition()] > tanqueAuto) return new LinkedList<>();
        return formarLista(previos, origen, destino);

    }

    // parte 5
    // este intento hacerlo con dfs recursivo solo para practicar, aunque es muy facil de hacer con dijkstra
    /* El intento quedo a la mitad porque es cualquier cosa, aguante dijkstra
    public List<String> caminoConMenorCargaCombustible(String ciudad1, String ciudad2, int tanqueAuto) {
        Vertex<String> origen = this.grafo.search(ciudad1);
        Vertex<String> destino = this.grafo.search(ciudad2);
        if (origen == null || destino == null || this.grafo.isEmpty()) return new LinkedList<>();

        List<String> lista = new LinkedList<>();
        boolean[] marca = new boolean[this.grafo.getSize()];
        int[] dist = new int[this.grafo.getSize()];
        initDist(dist, ciudad1);
        int[] recargas = new int[this.grafo.getSize()];
        initRecargas(recargas);

        dfsRecursivo(origen.getPosition(), marca, dist, recargas, origen, lista, new LinkedList<String>(), tanqueAuto);
        return caminoMenor(recargas, origen, destino);
    }

    private void dfsRecursivo(int pos, boolean[] marca, int[] dist, int[] recargas,
                              Vertex<String> destino, List<String> lista, List<String> listaAct, int tanqueAuto) {
        marca[pos] = true;
        Vertex<String> dato = this.grafo.getVertex(pos);
        List<Edge<String>> aristas = this.grafo.getEdges(dato);
        Iterator<Edge<String>> it = aristas.iterator();
        while (it.hasNext()) {
            Edge<String> arista = it.next();
            int j = arista.getTarget().getPosition();

            // Calcular costo viaje
            int costoKm = dist[pos] + arista.getWeight();
            int costoRec = recargas[pos];
            if (costoKm > tanqueAuto) {
                costoKm = costoKm - tanqueAuto;
                costoRec++;
            }

            if (!marca[j]) {
                if ((costoRec < recargas[j]) || (costoRec == recargas[j] && costoKm < dist[j])) {
                    recargas[j] = costoRec;
                    dist[j] = costoKm;
                    listaAct.addLast(arista.getTarget().getData());
                    dfsRecursivo(j, marca, dist, recargas, destino, lista, listaAct, tanqueAuto);
                }
            }
        }
    }*/

    private void initRecargas(int[] recargas) {
        for(int i : recargas) {
            i = 0;
        }
    }

    public static void main(String[] args) {
        Graph<String> mapaCiudades = new AdjListGraph<String>();
        Vertex<String> v1 = mapaCiudades.createVertex("La Plata"); //Casa Caperucita
        Vertex<String> v2 = mapaCiudades.createVertex("Ensenada"); //Claro 3
        Vertex<String> v3 = mapaCiudades.createVertex("Berisso"); //Claro 1
        Vertex<String> v4 = mapaCiudades.createVertex("Berazategui"); //Claro 2
        Vertex<String> v5 = mapaCiudades.createVertex("Florencio Varela"); //Claro 5
        Vertex<String> v6 = mapaCiudades.createVertex("Presidente Peron"); //Claro 4
        Vertex<String> v7 = mapaCiudades.createVertex("San Vicente"); //Casa Abuelita
        mapaCiudades.connect(v1, v2, 4);
        mapaCiudades.connect(v2, v1, 4);
        mapaCiudades.connect(v1, v3, 3);
        mapaCiudades.connect(v3, v1, 3);
        mapaCiudades.connect(v1, v4, 4);
        mapaCiudades.connect(v4, v1, 4);
        mapaCiudades.connect(v2, v5, 15);
        mapaCiudades.connect(v5, v2, 15);
        mapaCiudades.connect(v3, v5, 3);
        mapaCiudades.connect(v5, v3, 3);
        mapaCiudades.connect(v4, v3, 4);
        mapaCiudades.connect(v3, v4, 4);
        mapaCiudades.connect(v4, v5, 11);
        mapaCiudades.connect(v5, v4, 11);
        mapaCiudades.connect(v4, v6, 10);
        mapaCiudades.connect(v6, v4, 10);
        mapaCiudades.connect(v4, v3, 4);
        mapaCiudades.connect(v3, v4, 4);
        mapaCiudades.connect(v5, v7, 4);
        mapaCiudades.connect(v7, v5, 4);
        mapaCiudades.connect(v6, v7, 9);
        mapaCiudades.connect(v7, v6, 9);

        Mapa mapa = new Mapa(mapaCiudades);

        System.out.print("LISTA DEVOLVER CAMINO: ");
        System.out.println(mapa.devolverCamino("La Plata", "San Vicente"));

        List<String> ciudades = new ArrayList<>();
        ciudades.add("Berisso");
        ciudades.add("Berazategui");

        System.out.print("LISTA DEVOLVER EXCEPTUANDO: ");
        System.out.println(mapa.devolverCaminoExceptuando("La Plata", "San Vicente", ciudades));

        System.out.print("LISTA CAMINO MAS CORTO (SEGUN DISTANCIA): ");
        System.out.print(mapa.caminoMasCorto("La Plata", "San Vicente"));

        System.out.println("");
    }
}
