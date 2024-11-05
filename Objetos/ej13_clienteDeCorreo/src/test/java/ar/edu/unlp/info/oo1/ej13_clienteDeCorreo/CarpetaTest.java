package ar.edu.unlp.info.oo1.ej13_clienteDeCorreo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CarpetaTest {
	
	Carpeta Testeado;
	Email E;
	
	@BeforeEach
	public void setup() throws Exception {
		Testeado = new Carpeta("Test");
		E = new Email("Contenido", "Checkear");
		Testeado.agregar(E);
	}
	
	@Test
	public void testEspacioOcupado() {
		assertEquals(Testeado.espacioOcupado(), 17);
	}
	
	@Test
	public void testBuscar() {
		assertEquals(Testeado.buscar("Contenido"), E);
		assertNull(Testeado.buscar("algo que no existe"));
		//Email EDiferente = new Email("Contenido", "Checkear");
		assertEquals(Testeado.buscar("Checkear"), E); // assertEquals busca igualdad o identidad?
													  // como reviso igualdad? "assertTrue(objeto.equals(otroObjeto));"?
	}
	
	@Test
	public void testMover() {
		Carpeta Carpeta2 = new Carpeta("TestMover");
		Testeado.mover(E, Carpeta2);
		assertNull(Testeado.buscar("Contenido")); // Revisa que ya no se encuentre en testeado, y que cuando se busca en un listado vacio retorne null
		assertEquals(Carpeta2.buscar("Checkear"), E); // Revisa que se encuentre en la carpeta destino
	}
}
