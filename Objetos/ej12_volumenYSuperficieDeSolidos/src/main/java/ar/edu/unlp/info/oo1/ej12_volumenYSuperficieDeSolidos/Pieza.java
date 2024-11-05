package ar.edu.unlp.info.oo1.ej12_volumenYSuperficieDeSolidos;

public abstract class Pieza {
	
	private String material;
	private String color;
	
	public abstract double getVolumen();
	public abstract double getSuperficie();
	
	public String getMaterial() {
		return material;
	}
	
	public void setMaterial(String material) {
		this.material = material;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
}
