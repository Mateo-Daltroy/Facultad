package ar.edu.unlp.info.oo1.ej19_envioPaquetes;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnvioInterurbanoTest {

	EnvioInterurbano testeado;
	
	@BeforeEach
	public void setup() throws Exception {
		LocalDate fecha = LocalDate.of(2024, 10, 10);
		testeado = new EnvioInterurbano(99, fecha, "Calle 55, 970", "Calle 14 215", 50);
	}
	
	@Test
	public void testCalcularPrecio() {
		assertEquals(testeado.calcularPrecio(), 1000, 0.02);
		testeado.setDistancia(500);
		assertEquals(testeado.calcularPrecio(), 1250, 0.02);
		testeado.setDistancia(501);
		assertEquals(testeado.calcularPrecio(), 1500, 0.02);
	}
}
