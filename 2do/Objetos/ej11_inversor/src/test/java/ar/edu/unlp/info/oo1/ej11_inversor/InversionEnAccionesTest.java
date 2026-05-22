package ar.edu.unlp.info.oo1.ej11_inversor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class InversionEnAccionesTest {
	
	InversionEnAcciones Testeado;
	
	@BeforeEach
	public void setup() throws Exception {
		Testeado = new InversionEnAcciones("Amazon", 3, 50);
	}
	
	@Test
	public void testValorActual() {
		assertEquals(Testeado.valorActual(), 150, 0.02);
	}
}
