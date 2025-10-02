package TP2;

import Testers.*;

public class MainTP2 {
    public static void main(String[] args) {
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

/* Main Ej7
        Ej7Parcial aux = new Ej7Parcial(GeneradorArbBin.generarDesordenado(LectorPrimitivo.leerInteger()));
        System.out.println(aux.isLeftTree(1));
        System.out.println(aux.isLeftTree(7));
        System.out.println(aux.isLeftTree(2));
        System.out.println(aux.isLeftTree(-5));
        System.out.println(aux.isLeftTree(19));
        System.out.println(aux.isLeftTree(-3));
 */

/* Main Ej8
        BinaryTree<Integer> arbol1 = GeneradorArbBin.generarDesordenado(LectorPrimitivo.leerInteger());
        BinaryTree<Integer> arbol2 = GeneradorArbBin.generarDesordenado(LectorPrimitivo.leerInteger());
        System.out.println(Ej8Parcial.esPrefijo(arbol1, arbol2));
 */

/* Main Ej9
        BinaryTree<Record> res = Ej9Parcial.sumAndDif(GeneradorArbBin.generarDesordenado(LectorPrimitivo.leerInteger()));
        res.printPreOrden();
 */