package ar.edu.unlp.info.oo1.ej20_liquidacionHaberes;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContratoHorasTest {

	ContratoHoras testeado;
	
	@BeforeEach
	public void setup() throws Exception { 
		testeado = new ContratoHoras(100, 4, LocalDate.now(), 5);
	}
	
	@Test
	public void testCalcularBasico() {
		assertEquals(testeado.calcularBasico(null), 400, 0.02);
	}
}
