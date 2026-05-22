CaminoMinGrafoPositivosYNegativos (Grafo G, vertice S) {
    Distancia S = 0;
    Encolar S;
    Mientras (Cola no vacia) {
        Desencolar en U;
        For (vertices adyacentes W : U) {
            if (Distancia W > Distancia U + Camino entre W y U) {
                Actualizo Dw y Previo w;
                if (w no en cola) { Encolar W }
            }
        }
    }
}
