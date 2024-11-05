package ar.edu.unlp.info.oo1.ej9_cuentaGanchos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CajaDeAhorroTest {
	CajaDeAhorro Testeado;
	
	@BeforeEach
	void setup() throws Exception {
		Testeado = new CajaDeAhorro();
		Testeado.depositar(500); // 2% impuesto -> saldo 500
	}
	
	@Test
	public void testExtraer() {
		assertTrue(Testeado.extraer(480));
		assertFalse(Testeado.extraer(100));
	}
	
	@Test
	public void testTransferencia() {
		CuentaCorriente C = new CuentaCorriente();
		assertTrue(Testeado.transferirACuenta(480, C));
		assertFalse(Testeado.extraer(1));
	}
	
	@Test
	public void testDepositar() {
		assertEquals(Testeado.getSaldo(), 490, 0.02);
	}
}
