package ar.edu.unlp.info.oo1.ej13_clienteDeCorreo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClienteDeCorreoTest {

	ClienteDeCorreo Testeado;
	Email E1;
	Email E2;
	
	@BeforeEach
	public void setup() throws Exception {
		Testeado = new ClienteDeCorreo();
		Carpeta C = new Carpeta("Carpeta 1");
		Testeado.agregarCarpeta(C);
		E1 = new Email("Algo", "Otra Cosa");
		C.agregar(E1);
		E2 = new Email("Objetos", "Programacion orientada a");
		Testeado.recibir(E2);
	}
	
	@Test
	public void testEspacioOcupado() {
		assertEquals(Testeado.espacioOcupado(), 44);
	}
	
	@Test
	public void testBuscar() {
		assertEquals(Testeado.buscar("Otra Cosa"), E1); // Test busca en Cuerpo y Carpeta
		assertEquals(Testeado.buscar("Objetos"), E2); // Test busca en Titulo e Inbox
		assertNull(Testeado.buscar("Algo que no existe"));
	}
}
