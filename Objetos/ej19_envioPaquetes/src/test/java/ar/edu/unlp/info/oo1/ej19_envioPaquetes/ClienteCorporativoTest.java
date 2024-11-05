package ar.edu.unlp.info.oo1.ej19_envioPaquetes;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClienteCorporativoTest {

	ClienteCorporativo testeado;
	
	@BeforeEach
	public void setup() throws Exception {
		LocalDate fecha = LocalDate.of(2024, 10, 10);
		EnvioInterurbano envIU = new EnvioInterurbano(99, fecha, "Calle 55, 970", "Calle 14 215", 50);
		EnvioLocal envL = new EnvioLocal(true, fecha, "Calle 55, 970", "Calle 14 215", 500);
		fecha = LocalDate.of(2024, 12, 1);
		EnvioInternacional envIN = new EnvioInternacional(fecha, "Argentina, La Plata, Calle 55, 970", "Uruguay, Montevideo, Calle 14 215", 500);
		testeado = new ClienteCorporativo();
		testeado.agregarEnvio(envIU);
		testeado.agregarEnvio(envIN);
		testeado.agregarEnvio(envL);
	}
	
	@Test
	public void testMontoAPagar() {
		LocalDate from = LocalDate.of(2024, 10, 8);
		LocalDate to = LocalDate.of(2024, 10, 20);
		DateLapse d = new DateLapse(from, to);
		assertEquals(testeado.montoAPagar(d), 2500, 0.02);
	}
	
}
