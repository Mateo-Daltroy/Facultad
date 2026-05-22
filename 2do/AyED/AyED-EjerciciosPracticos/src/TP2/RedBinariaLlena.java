package TP2;

import Testers.BinaryTree;

public class RedBinariaLlena {
    private BinaryTree<Integer> red;

    public RedBinariaLlena(BinaryTree<Integer> red) {
        this.red = red;
    }

    public int maxRetardo() {
        if (!red.isEmpty()) {
            return(maxRetardo(this.red));
        }
        return(0);
    }

    public int maxRetardo(BinaryTree<Integer> red) {
        int maxHI = 0;
        int maxHD = 0;
        if (red.hasLeftChild()) {
            maxHI = maxRetardo(red.getLeftChild());
        }
        if (red.hasRightChild()) {
            maxHD = maxRetardo(red.getRightChild());
        }
        return(Math.max(maxHI, maxHD) + red.getData());
    }
}
