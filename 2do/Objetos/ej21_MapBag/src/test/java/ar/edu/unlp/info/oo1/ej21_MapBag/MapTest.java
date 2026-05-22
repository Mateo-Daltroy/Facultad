package ar.edu.unlp.info.oo1.ej21_MapBag;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

public class MapTest {

	Map<String, Integer> Testeado;
	
	@BeforeEach
	public void setup() throws Exception {
		Testeado = new HashMap<>();
		Testeado.put("Lionel Messi", 111);
		Testeado.put("Gabriel Batistuta", 56);
		Testeado.put("Kun Aguero", 42);
	}
	
	@Test
	public void testEliminar() {
		assertTrue(Testeado.containsKey("Kun Aguero"));
		Testeado.remove("Kun Aguero");
		assertFalse(Testeado.containsKey("Kun Aguero"));
	}
	
	@Test
	public void testActualizar() {
		assertEquals(Testeado.get("Lionel Messi"), 111);
		Testeado.replace("Lionel Messi", 112);
		assertEquals(Testeado.get("Lionel Messi"), 112);
	}
	
	@Test
	public void testTotal() {
		// por que no funciona stream().sum()?
		assertEquals(Testeado.values().stream().mapToInt(value -> value).sum(), 209);
	}
}
