package TP2;

import Testers.BinaryTree;

public class Ej2_Test {
    public static void main(String[] args){
        BinaryTree<Integer> aux = new BinaryTree<>();
        //aux = BinaryTree.inicializar();
        System.out.println("Hojas: " + aux.contarHojas()); // 3
        System.out.print("Orden espejo: "); // 6, 5, 4, 3, 2, 1
        aux.espejo().printEnOrden();
        System.out.println();
        System.out.println("EN 11");
        aux.entreNiveles(1, 1); // 2, 3
        System.out.println("EN 05");
        aux.entreNiveles(0, 5); // 1, 2, 3, 4, 5, 6
    }
}
