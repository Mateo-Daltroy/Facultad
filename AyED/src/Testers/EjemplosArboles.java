package Testers;

public class EjemplosArboles {
    public static void main(String[] args) {
        // GenerarDesordenado te permite armar el arbol como quieras, sin que tenga que seguir un orden especifico
        // Pero vos tenes que hacerte cargo de manejar los Hijos
        BinaryTree<Integer> arbolBinario = GeneradorArbBin.generarDesordenado(LectorPrimitivo.leerInteger());
        arbolBinario.printEnOrden();

        // GenerarOrdenado crea y guarda el dato en la posicion correspondiente dependiendo del valor
        // Las clases Wrapper tienen un comparador implementado de base
        // Si queres usar una clase programada, vas a tener que implementar un comparador y pasarlo también por parametro
        arbolBinario = GeneradorArbBin.generarOrdenado(LectorPrimitivo.leerInteger());
        arbolBinario.printEnOrden();

        // Arbol General solo tiene implementado generarDesordenado
        GeneralTree<String> arbolGeneral = GeneradorArbGen.generarDesordenado(LectorPrimitivo.leerString());
        arbolGeneral.printPosOrden();
    }
}
