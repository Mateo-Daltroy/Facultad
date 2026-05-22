package TP3;

import TP1.Ejercicio8.Queue;
import Testers.GeneradorArbGen;
import Testers.GeneralTree;
import Testers.LectorPrimitivo;

import java.util.ArrayList;
import java.util.List;

public class RecorridosAG {
    public static void main(String[] args) {
        GeneralTree<Integer> arb = GeneradorArbGen.generarDesordenado(LectorPrimitivo.leerInteger());
        // Me confundi y no los hice impares jeje pero es un if(dato %2 == 1)
        imprimirLista(numerosMayoresQuePreOrden(arb, 50));
        imprimirLista(numerosMayoresQuePostOrden(arb, 50));
        imprimirLista(numerosImparesMayoresQueInOrden(arb, 50));
        imprimirLista(numerosMayoresQuePorNiveles(arb, 50));
    }

    public static List<Integer> numerosMayoresQuePorNiveles(GeneralTree<Integer> arb, int num) {
        List<Integer> lista = new ArrayList<>();
        Queue<GeneralTree<Integer>> cola = new Queue<>();
        cola.enqueue(arb);
        while (!cola.isEmpty()) {
            GeneralTree<Integer> nodoAct = cola.dequeue();
            for (GeneralTree<Integer> nodo: nodoAct.getChildren()) {
                cola.enqueue(nodo);
            }
            lista.addLast(nodoAct.getData());
        }
        return(lista);
    }

    public static List<Integer> numerosMayoresQuePreOrden(GeneralTree<Integer> arb, int num) {
        List<Integer> lista = new ArrayList<>();
        numerosMayoresQuePreOrden(arb, num, lista);
        return(lista);
    }

    private static void numerosMayoresQuePreOrden(GeneralTree<Integer> arb, int num, List<Integer> lista) {
        lista.addLast(arb.getData());
        for(GeneralTree<Integer> nodo: arb.getChildren()) {
            numerosMayoresQuePreOrden(nodo, num, lista);
        }
    }

    public static List<Integer> numerosMayoresQuePostOrden(GeneralTree<Integer> arb, int num) {
        List<Integer> lista = new ArrayList<>();
        numerosMayoresQuePostOrden(arb, num, lista);
        return(lista);
    }

    private static void numerosMayoresQuePostOrden(GeneralTree<Integer> arb, int num, List<Integer> lista) {
        for(GeneralTree<Integer> nodo: arb.getChildren()) {
            numerosMayoresQuePostOrden(nodo, num, lista);
        }
        lista.addLast(arb.getData());
    }

    public static List<Integer> numerosImparesMayoresQueInOrden(GeneralTree<Integer> arb, int num) {
        List<Integer> lista = new ArrayList<>();
        numerosImparesMayoresQueInOrden(arb, num, lista);
        return(lista);
    }

    public static void numerosImparesMayoresQueInOrden(GeneralTree<Integer> arb, int num, List<Integer> lista) {
        List<GeneralTree<Integer>> hijos = arb.getChildren();
        if (arb.hasChildren()) {
            numerosImparesMayoresQueInOrden(hijos.get(0), num, lista);
        }
        if (arb.getData() % 2 == 1) {
            lista.add(arb.getData());
        }
        for (int i = 1; i < hijos.size(); i++) {
            numerosImparesMayoresQueInOrden(hijos.get(i), num, lista);
        }
    }

    private static void imprimirLista(List<Integer> lista) {
        System.out.print("Lista: ");
        for(Integer i: lista) {
            System.out.print(i + ", ");
        }
        System.out.println();
    }
}
