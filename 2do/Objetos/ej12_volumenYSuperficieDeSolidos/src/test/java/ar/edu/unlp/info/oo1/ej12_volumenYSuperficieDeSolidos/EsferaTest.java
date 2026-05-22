package ar.edu.unlp.info.oo1.ej12_volumenYSuperficieDeSolidos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EsferaTest {
	
	Esfera Testeado;
	
	@BeforeEach
	public void setup() throws Exception {
		Testeado = new Esfera(4);
	}
	
	@Test
	public void testGetVolumen() {
		assertEquals(Testeado.getVolumen(), 268.08, 0.02);
	}
	
	@Test
	public void testGetSuperficie() {
		assertEquals(Testeado.getSuperficie(), 201.06, 0.02);
	}
}
