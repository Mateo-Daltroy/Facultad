package TP3;

import java.util.List;
import Testers.GeneradorArbGen;
import Testers.GeneralTree;
import Testers.LectorPrimitivo;

public class ProcesadorGematria {

    public static void main(String[] args) {
        GeneralTree<Integer> arb = GeneradorArbGen.generarDesordenado(LectorPrimitivo.leerInteger());
        arb.printPreOrden();
        System.out.println(contarGematria(arb, 110));
    }


    public static int contarGematria(GeneralTree<Integer> arb, int valor) {
        int resta = valor - arb.getData();
        if (arb.isLeaf() && resta == 0) {
            return(1);
        } else {
            int cont = 0;
            if (resta > 0) {
                for(GeneralTree<Integer> nodo: arb.getChildren()) {
                    cont += contarGematria(nodo, resta);
                }
            }
            return(cont);
        }
    }

    /*
    public static int contarGematria(GeneralTree<Integer> ag, int valor) {
        int resta = valor - ag.getData();
        if (ag.isLeaf() && resta == 0)
            return 1;
        else {
            int cont = 0;
            if (resta > 0) {
                List<GeneralTree<Integer>> children = ag.getChildren();
                for (GeneralTree<Integer> child: children) {
                    cont = cont + contarGematria(child, resta);
                }
            }
            return cont;
        }
    } */
}