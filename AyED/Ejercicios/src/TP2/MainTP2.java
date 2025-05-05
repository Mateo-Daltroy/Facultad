package TP2;

import Testers.LectorPrimitivo;
import Testers.GeneradorArbBin;

public class MainTP2 {
    public static void main(String[] args) {
        Ej7Parc aux = new Ej7Parc(GeneradorArbBin.generarDesordenado(LectorPrimitivo.leerInteger()));
        System.out.println(aux.isLeftTree(1));
        System.out.println(aux.isLeftTree(7));
        System.out.println(aux.isLeftTree(2));
        System.out.println(aux.isLeftTree(-5));
        System.out.println(aux.isLeftTree(19));
        System.out.println(aux.isLeftTree(-3));
    }
}

/* Main Ej3
        ContadorArbol aux = new ContadorArbol(BinaryTree.inicializar());
        System.out.println(aux.numerosParesIn());
        System.out.println(aux.numerosParesPost());
 */

/* Main Ej5
        ProfundidadDeArbolBinario test = new ProfundidadDeArbolBinario(BinaryTree.inicializar());
        System.out.println(test.sumaElementosProfundidad(0));
        System.out.println(test.sumaElementosProfundidad(1));
        System.out.println(test.sumaElementosProfundidad(2));
        System.out.println(test.sumaElementosProfundidad(3));
 */

/* Main Ej6
        BinaryTree<Integer> testGen = GeneradorArbBin.generarIntegerDesordenado();
        System.out.println("Arbol original:");
        testGen.printEnOrden();
        System.out.println();
        Transformacion aux = new Transformacion(testGen);
        System.out.println("Arbol suma:");
        aux.suma().printEnOrden();
 */