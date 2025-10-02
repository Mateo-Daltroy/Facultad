package Testers;

import java.io.Console;
import java.util.LinkedList;
import java.util.List;
import TP1.Ejercicio8.Queue;

public class GeneralTree<T> implements Arbol<T>{

	private T data;
	private List<GeneralTree<T>> children = new LinkedList<GeneralTree<T>>(); 

	public GeneralTree() {
		
	}
	public GeneralTree(T data) {
		this.data = data;
	}

	public GeneralTree(T data, List<GeneralTree<T>> children) {
		this(data);
		this.children = children;
	}	
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<GeneralTree<T>> getChildren() {
		return this.children;
	}
	
	public void setChildren(List<GeneralTree<T>> children) {
		if (children != null)
			this.children = children;
	}
	
	public void addChild(GeneralTree<T> child) {
		this.getChildren().add(child);
	}

	public boolean isLeaf() {
		return !this.hasChildren();
	}
	
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}
	
	public boolean isEmpty() {
		return this.data == null && !this.hasChildren();
	}

	public void removeChild(GeneralTree<T> child) {
		if (this.hasChildren())
			children.remove(child);
	}
	
	public int altura() {	 
		if (this.hasChildren()) {
			return (this.children.stream().mapToInt(c -> c.altura()).max().orElse(0)) + 1;
		}
		return 0;
	}
	
	public int nivel(T dato){
		int aux = -1;
		if (dato == this.data) {
			return(0);
		}
		if (this.hasChildren()) {
			aux = this.children.stream().mapToInt(c -> c.nivel(dato)).max().orElse(0);
			if (aux != -1) { //Se encontro el dato entre los hijos
				aux++;
			}
		}
		return (aux);
	  }

	public int ancho(){
		Queue<GeneralTree<T>> cola = new Queue<>();
		int anchoAct = 0;
		int anchoMax = -1;
		cola.enqueue(this);
		cola.enqueue(null);
		while (!cola.isEmpty()) {
			GeneralTree<T> datoAct = cola.dequeue();
			if (datoAct != null) {
				anchoAct++;
				if (datoAct.hasChildren()) {
					for(GeneralTree<T> child: datoAct.getChildren()) {
						cola.enqueue(child);
					}
				}
			} else {
				if (anchoAct > anchoMax) {
					anchoMax = anchoAct;
				}
				anchoAct = 0;
				if (!cola.isEmpty()) {
					cola.enqueue(null);
				}
			}
		}
		return anchoMax;
	}

	public GeneralTree<T> buscarNodo(T data) {
		if (this.data == data) {
			return (this);
		} else if (this.hasChildren()) {
			for (GeneralTree<T> child: this.getChildren()) {
				if (child.buscarNodo(data) != null) {
					return(child.buscarNodo(data));
				}
			}
		}
		return(null);
	}

	public boolean esAncestro(T a, T b) {
		GeneralTree<T> nodoA = buscarNodo(a);
		if (nodoA != null) { // a existe en el arbol
			return(nodoA.soyAncestro(b));
		}
		return(false);
	}

	public boolean soyAncestro(T b) {
		boolean soy = false;
		if (this.getData() == b) {
			return(true);
		} else {
			if (this.hasChildren()) {
				for (GeneralTree<T> child : this.getChildren()) {
					soy = soy || child.soyAncestro(b);
				}
			}
		}
		return(soy);
	}

	@Override
	public void printPreOrden() {
		System.out.print("Arbol General PreOrden: ");
		this.printPreOrden(this);
		System.out.println();
	}

	private void printPreOrden(GeneralTree<T> arb) {
		if (!this.isEmpty()) {
			System.out.print(arb.getData().toString() + ", ");
			for (GeneralTree<T> c:  arb.getChildren()) {
				this.printPreOrden(c);
			}
		}
	}

	@Override
	public void printPosOrden() {
		System.out.print("Arbol General PosOrden: ");
		this.printPosOrden(this);
		System.out.println();
	}

	private void printPosOrden(GeneralTree<T> arb) {
		if (!this.isEmpty()) {
			for (GeneralTree<T> c:  arb.getChildren()) {
				this.printPosOrden(c);
			}
			System.out.print(arb.getData().toString() + ", ");
		}
	}
}