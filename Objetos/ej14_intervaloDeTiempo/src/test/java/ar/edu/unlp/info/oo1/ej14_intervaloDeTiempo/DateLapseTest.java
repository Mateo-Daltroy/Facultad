package ar.edu.unlp.info.oo1.ej14_intervaloDeTiempo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class DateLapseTest {

	DateLapse Testeado;
	
	@BeforeEach
	public void setup() throws Exception {
		Testeado = new DateLapse(LocalDate.of(2024, 10, 16), LocalDate.now());
	}
	
	@Test
	public void testSizeInDays() {
		assertEquals(Testeado.sizeInDays(), 7);
	}
	
	@Test
	public void testIncludesDate() {
		assertTrue(Testeado.includesDate(LocalDate.of(2024, 10, 20)));
		assertFalse(Testeado.includesDate(LocalDate.of(2024, 9, 10)));
	}
}
