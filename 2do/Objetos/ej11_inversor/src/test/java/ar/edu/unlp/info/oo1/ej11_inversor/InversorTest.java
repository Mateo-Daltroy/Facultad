package ar.edu.unlp.info.oo1.ej11_inversor;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class InversorTest {
	
	Inversor Testeado;
	
	@BeforeEach
	public void setup() throws Exception {
		Testeado = new Inversor();
		InversionEnAcciones Accion = new InversionEnAcciones("Amazon", 3, 50);
		LocalDate Dia = (LocalDate.of(2024, 10, 15));
		PlazoFijo Plazo = new PlazoFijo(Dia, 100, 0.02);
		Testeado.agregarInversion(Accion); // No testea agregar inversion, deberia?
		Testeado.agregarInversion(Plazo);
	}
	
	@Test
	public void testValorActual() {
		assertEquals(Testeado.valorActual(), 264, 0.02);
	}
	
}
