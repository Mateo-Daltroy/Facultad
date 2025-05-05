package Testers;

import java.io.Console;
import java.util.LinkedList;
import java.util.List;

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
			
		return 0;
	}
	
	public int nivel(T dato){
		return 0;
	  }

	public int ancho(){
		
		return 0;
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