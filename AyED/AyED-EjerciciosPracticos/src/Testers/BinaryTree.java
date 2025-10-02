package Testers;

import TP1.Ejercicio8.Queue;

public class BinaryTree<T> implements Arbol<T> {
    private T data;
    private BinaryTree<T> leftChild;
    private BinaryTree<T> rightChild;

    public BinaryTree () {
        this.data = null;
    }

    public BinaryTree (T data) {
        this.data = data;
    }

    public int contarHojas() {
        int aux = 0;
        if (!this.isEmpty()) {
            if (this.isLeaf()) {
                return(1);
            } else {
                if (this.hasLeftChild()) {
                    aux+= this.leftChild.contarHojas();
                }
                if (this.hasRightChild()) {
                    aux+= this.rightChild.contarHojas();
                }
            }
        }
        return(aux);
    }

    public BinaryTree<T> espejo() {
        BinaryTree<T> res = new BinaryTree<>(this.data);
        if (!this.isEmpty()) {
            if (this.hasRightChild()) {
                res.addLeftChild(this.rightChild.espejo());
            }
            if (this.hasLeftChild()) {
                res.addRightChild(this.leftChild.espejo());
            }
        }
        return(res);
    }

    public void entreNiveles(int base, int max) {
        int nivel = 0;
        Queue<BinaryTree<T>> cola = new Queue<>();
        cola.enqueue(this);
        cola.enqueue(null);
        while (!cola.isEmpty()) {
            BinaryTree<T> datoAct = cola.dequeue();
            if (datoAct != null) {
                // Aca van las acciones particulares?
                if (nivel >= base && nivel <= max) {
                    System.out.print(datoAct.data);
                }
                if (datoAct.hasLeftChild()) {
                    cola.enqueue(datoAct.getLeftChild());
                }
                if (datoAct.hasRightChild()) {
                    cola.enqueue(datoAct.getRightChild());
                }
            } else {
                if (!cola.isEmpty()) {
                    nivel++;
                    System.out.println();
                    cola.enqueue(null);
                }
            }
        }
    }

    // Imprime todos los valores en orden
    public void printEnOrden() {
        if (!this.isEmpty()) {
            if (this.hasLeftChild()) {
                this.getLeftChild().printEnOrden();
            }
            System.out.print(String.format("%3s", this.toString()) + ", ");
            if (this.hasRightChild()) {
                this.getRightChild().printEnOrden();
            }
        }
    }

    // Imprime todos los valores en postOrden
    public void printPosOrden() {
        if (!this.isEmpty()) {
            if (this.hasLeftChild()) {
                this.getLeftChild().printPosOrden();
            }
            if (this.hasRightChild()) {
                this.getRightChild().printPosOrden();
            }
            System.out.print(this.toString() + ", ");
        }
    }

    // Imprime todos los valores en preOrden
    public void printPreOrden() {
        if (!this.isEmpty()) {
            System.out.print(this.toString() + ", ");
            if (this.hasLeftChild()) {
                this.getLeftChild().printPreOrden();
            }
            if (this.hasRightChild()) {
                this.getRightChild().printPreOrden();
            }
        }
    }
    // Get / Set / Funciones Basicas
    public T getData() {
        return(this.data);
    }

    public void setData(T data) {
        this.data = data;
    }

    public BinaryTree<T> getLeftChild () {
        return(this.leftChild);
    }

    public void addLeftChild(BinaryTree<T> leftChild) {
        this.leftChild = leftChild;
    }

    public BinaryTree<T> getRightChild() {
        return rightChild;
    }

    public void addRightChild(BinaryTree<T> rightChild) {
        this.rightChild = rightChild;
    }

    public void removeLeftChild() {
        this.leftChild = null;
    }

    public void removeRightChild() {
        this.rightChild = null;
    }

    public boolean hasLeftChild() {
        return !(this.leftChild == null);
    }

    public boolean hasRightChild() {
        return !(this.rightChild == null);
    }

    public boolean isLeaf() {
        return((this.rightChild == null) && (this.leftChild == null));
    }

    public boolean isEmpty() {
        return(this.isLeaf() && this.data == null);
    }

    @Override
    public String toString() {
        return(this.data.toString());
    }
}
