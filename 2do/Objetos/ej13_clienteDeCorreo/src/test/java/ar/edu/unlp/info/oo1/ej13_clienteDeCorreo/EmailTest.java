package ar.edu.unlp.info.oo1.ej13_clienteDeCorreo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class EmailTest {
	
	Email Testeado;
	
	@BeforeEach
	public void setup() throws Exception {
		Testeado = new Email("Problema", "Testeando");
		Archivo A = new Archivo("Prueba 1");
		Archivo B = new Archivo("Pru 2");
		Testeado.agregarArchivo(A);
		Testeado.agregarArchivo(B);
	}
	
	@Test
	public void testEspacioOcupado() {
		assertEquals(Testeado.espacioOcupado(), 30);
	}
	
	@Test
	public void testContiene() {
		assertTrue(Testeado.contiene("Problema"));
		assertFalse(Testeado.contiene("Prueba 1"));
		assertTrue(Testeado.contiene("Testeando"));
	}
}
