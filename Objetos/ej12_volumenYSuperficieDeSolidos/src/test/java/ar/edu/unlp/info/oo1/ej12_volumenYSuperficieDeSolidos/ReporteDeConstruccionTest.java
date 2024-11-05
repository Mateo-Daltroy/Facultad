package ar.edu.unlp.info.oo1.ej12_volumenYSuperficieDeSolidos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReporteDeConstruccionTest {
	
	ReporteDeConstruccion Testeado;
	
	@BeforeEach
	public void setup() throws Exception {
		Testeado = new ReporteDeConstruccion();
		Cilindro C = new Cilindro(4, 4);
		C.setColor("Azul");
		C.setMaterial("Aluminio");
		Testeado.agregarPieza(C);
		Esfera E = new Esfera(4);
		E.setColor("Azul");
		E.setMaterial("Oro");
		Testeado.agregarPieza(E);
		PrismaRectangular P = new PrismaRectangular(4, 4, 4);
		P.setColor("Rojo");
		P.setMaterial("Aluminio");
		Testeado.agregarPieza(P);
	}
	
	@Test
	public void testGetVolumenDeMaterial() {
		assertEquals(Testeado.getVolumenDeMaterial("Aluminio"), 265.06, 0.02);
	}
	
	public void testGetSuperficieDeColor() {
		assertEquals(Testeado.getSuperficieDeColor("Azul"), 402.12, 0.02);
	}
}
