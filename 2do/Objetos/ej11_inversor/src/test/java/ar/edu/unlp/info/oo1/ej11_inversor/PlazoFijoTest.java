package ar.edu.unlp.info.oo1.ej11_inversor;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlazoFijoTest {
	
	PlazoFijo Testeado;
	
	@BeforeEach
	public void setup() throws Exception {
		LocalDate Dia = (LocalDate.of(2024, 10, 15));
		Testeado = new PlazoFijo(Dia, 100, 0.02);
	}
	
	@Test
	public void testValorActual() { // Al usar el valor de LocalDate.now(), el assert deberia actualizarse teniendo en cuenta la fecha
		assertEquals(Testeado.valorActual(), 114, 0.02);
	}
}
