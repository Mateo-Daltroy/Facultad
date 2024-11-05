package ar.edu.unlp.info.oo1.ej15_distribuidoraElectrica;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FacturaDeCobroTest {

	FacturaDeCobro Testeado;
	Usuario U;
	CuadroTarifario precios;
	
	@BeforeEach
	public void setup() throws Exception {
		U = new Usuario();
		precios = new CuadroTarifario(20);
	}
	
	@Test
	public void testMontoSinBonus() {
		Consumo C = new Consumo(5, 5);
		U.agregarConsumo(C);
		Testeado = U.generarFactura();
		Testeado.setFuenteDePrecios(precios);
		assertEquals(Testeado.getMontoFinal(), 100, 0.02);
	}

	@Test
	public void testMontoConBonus() {
		Consumo C = new Consumo(5, 3);
		U.agregarConsumo(C);
		Testeado = U.generarFactura();
		Testeado.setFuenteDePrecios(precios);
		assertEquals(Testeado.getMontoFinal(), 90, 0.02);
	}
}
