package ar.edu.unlp.info.oo1.ej12_volumenYSuperficieDeSolidos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrismaRectangularTest {
	
	PrismaRectangular Testeado;
	
	@BeforeEach
	public void setup() throws Exception{
		Testeado = new PrismaRectangular(4, 4, 4);
	}
	
	@Test
	public void testGetVolumen() {
		assertEquals(Testeado.getVolumen(), 64, 0.02);
	}
	
	@Test
	public void testGetSuperficie() {
		assertEquals(Testeado.getSuperficie(), 96, 0.02);
	}
}
