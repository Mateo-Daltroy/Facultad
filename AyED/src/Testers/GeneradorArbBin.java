package Testers;

import java.util.Comparator;
import java.util.Scanner;
import java.util.function.Supplier;

public class GeneradorArbBin implements ArbolGenerator {
    private static Scanner s = new Scanner(System.in);

    public static <T> BinaryTree<T> generarDesordenado(Supplier<T> generador) {
        BinaryTree<T> nodo = new BinaryTree<>();
        System.out.println("Ingresar dato: ");
        T dato = generador.get();
        nodo.setData(dato);
        System.out.print("Hijo Izquierdo nodo " + dato.toString() + "? S/n: ");
        String opt = s.nextLine();
        if (!opt.equalsIgnoreCase("n")) {
            System.out.println("Dentro de Hijo Izquierdo: ");
            nodo.addLeftChild(generarDesordenado(generador));
        }
        System.out.print("Hijo Derecho nodo " + nodo.getData().toString() + "? S/n: ");
        opt = s.next();
        if (!opt.equalsIgnoreCase("n")) {
            System.out.println("Dentro de Hijo Derecho: ");
            nodo.addRightChild(generarDesordenado(generador));
        }
        return(nodo);
    }

    public static <T> BinaryTree<T> generarOrdenado(Supplier<T> generador, Comparator<T> comp) {
        BinaryTree<T> arbol = new BinaryTree<>();
        String corte = "Y";
        while(!corte.equalsIgnoreCase("n")) {
            T dato = generador.get();
            insertarOrdenado(arbol, dato, comp);
            System.out.print("Ingresar otro dato? S/n: ");
            corte = s.nextLine();
        }
        return(arbol);
    }

    private static <T> void insertarOrdenado(BinaryTree<T> nodo, T dato, Comparator<T> comp) {
        if (nodo.isEmpty()) {
            nodo.setData(dato);
        } else if (comp.compare(dato, nodo.getData()) <= 0) { // dato es menor o igual al del nodo
            insertarOrdenado(nodo.getLeftChild(), dato, comp);
        } else {
            insertarOrdenado(nodo.getRightChild(), dato, comp);
        }
    }

    public static <T extends Comparable<T>> BinaryTree<T> generarOrdenado(Supplier<T> generador) {
        BinaryTree<T> arbol = new BinaryTree<>();
        String corte = "Y";
        while(!corte.equalsIgnoreCase("n")) {
            T dato = generador.get();
            insertarOrdenado(arbol, dato);
            System.out.print("Ingresar otro dato? S/n: ");
            corte = s.nextLine();
        }
        return(arbol);
    }

    private static <T extends Comparable<T>> void insertarOrdenado(BinaryTree<T> nodo, T dato) {
        if (nodo.isEmpty()) {
            nodo.setData(dato);
        } else if (dato.compareTo(nodo.getData()) <= 0) { // dato es menor o igual al del nodo
            insertarOrdenado(nodo.getLeftChild(), dato);
        } else {
            insertarOrdenado(nodo.getRightChild(), dato);
        }
    }
}
