package ar.edu.unlp.info.oo1.ej9_cuentaGanchos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CuentaCorrienteTest {
	
	CuentaCorriente Testeado;
	
	@BeforeEach
	void setup() throws Exception {
		Testeado = new CuentaCorriente();
		Testeado.setDescubierto(-500);
	}
	
	@Test
	public void TestExtraer() {
		assertTrue(Testeado.extraer(500));
		assertFalse(Testeado.extraer(1));
	}
	
	@Test
	public void TestTransferencia() {
		CajaDeAhorro C = new CajaDeAhorro();
		assertTrue(Testeado.transferirACuenta(200, C));
		assertFalse(Testeado.transferirACuenta(301, C));
	}
	
	@Test
	public void TestDeposito() {
		Testeado.depositar(100);
		assertEquals(Testeado.getSaldo(), 100, 0.02);
	}

}
