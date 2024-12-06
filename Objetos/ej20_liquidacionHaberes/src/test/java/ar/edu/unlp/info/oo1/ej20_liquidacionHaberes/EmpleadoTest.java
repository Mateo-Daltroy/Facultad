package ar.edu.unlp.info.oo1.ej20_liquidacionHaberes;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmpleadoTest {

	Empleado testeado;
	
	@BeforeEach
	public void setup() throws Exception {
		testeado = new Empleado(true, true);
	}
	
	@Test
	public void testSumarContrato() {
		ContratoHoras ContratoVen = new ContratoHoras(500, 10, LocalDate.of(2024, 9, 30), 3);
		ContratoPlanta ContratoVig = new ContratoPlanta(200, 50, 1000, 3);
		ContratoHoras ContratoFalse = new ContratoHoras(200, 200, LocalDate.of(2024, 12, 12), 5);
		assertTrue(testeado.sumarContrato(ContratoVen));
		assertTrue(testeado.sumarContrato(ContratoVig));
		assertFalse(testeado.sumarContrato(ContratoFalse));
	}
	
	@Test
	public void testGenerarRecibo() {
		this.testSumarContrato();
		ReciboDeSueldo R = testeado.generarRecibo();
		assertEquals(R.getAntiguedad(), 6);
		assertEquals(R.getmontoTot(), (1250 * 1.3), 0.02);
	}
}
