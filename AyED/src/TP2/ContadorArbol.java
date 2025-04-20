package TP2;

import Testers.BinaryTree;

import java.util.ArrayList;

public class ContadorArbol {
    private BinaryTree<Integer> arb;

    public ContadorArbol(BinaryTree<Integer> arb){
        this.arb = arb;
    }

    public ArrayList<Integer> numerosParesIn() {
        ArrayList<Integer> res = new ArrayList<>();
        if (!this.arb.isEmpty()) {
            ContadorArbol aux;
            if (this.arb.hasLeftChild()) {
                aux = new ContadorArbol(this.arb.getLeftChild());
                res.addAll(aux.numerosParesIn());
            }
            if (this.arb.getData() % 2 == 0) {res.add(this.arb.getData());}
            if (this.arb.hasRightChild()) {
                aux = new ContadorArbol(this.arb.getRightChild());
                res.addAll(aux.numerosParesIn());
            }
        }
        return(res);
    }

    public ArrayList<Integer> numerosParesPost() {
        ArrayList<Integer> res = new ArrayList<>();
        if (!this.arb.isEmpty()) {
            ContadorArbol aux;
            if (this.arb.hasLeftChild()) {
                aux = new ContadorArbol(this.arb.getLeftChild());
                res.addAll(aux.numerosParesPost());
            }
            if (this.arb.hasRightChild()) {
                aux = new ContadorArbol(this.arb.getRightChild());
                res.addAll(aux.numerosParesPost());
            }
            if (this.arb.getData() % 2 == 0) {res.add(this.arb.getData());}
        }
        return(res);
    }
}
