package ar.edu.unlp.info.oo1.ej20_liquidacionHaberes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContratoPlantaTest {

	ContratoPlanta testeado;
	Empleado EConH;
	Empleado EConC;
	Empleado EConHC;
	
	@BeforeEach
	public void setup() throws Exception {
		testeado = new ContratoPlanta(500, 100, 1000, 3);
		EConH = new Empleado(true, false);
		EConC = new Empleado(false, true);
		EConHC = new Empleado(true, true);
	}
	
	@Test
	public void testCalcularBasico() {
		assertEquals(testeado.calcularBasico(EConH), 1500, 0.02);
		assertEquals(testeado.calcularBasico(EConC), 1100, 0.02);
		assertEquals(testeado.calcularBasico(EConHC), 1600, 0.02);
	}
}
