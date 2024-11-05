package ar.edu.unlp.info.oo1.ej17_alquilerDePropiedades;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class DateLapseTest {

	DateLapse Testeado;
	
	@BeforeEach
	public void setup() throws Exception { 
		Testeado = new DateLapse(LocalDate.of(2024, 10, 23), LocalDate.of(2024, 10, 31));
	}
	
	@Test
	public void overlapsTest() {
		DateLapse antes = new DateLapse(LocalDate.of(2024, 10, 15), LocalDate.of(2024, 10, 22));
		DateLapse despues = new DateLapse(LocalDate.of(2024, 11, 8), LocalDate.of(2024, 11, 9));
		DateLapse dentro = new DateLapse(LocalDate.of(2024, 10, 24), LocalDate.of(2024, 10, 26));
		DateLapse sobre = new DateLapse(LocalDate.of(2024, 10, 10), LocalDate.of(2024, 11, 6));
		assertTrue(Testeado.overlaps(dentro));
		assertTrue(Testeado.overlaps(sobre));
		assertFalse(Testeado.overlaps(antes));
		assertFalse(Testeado.overlaps(despues));
	}
}
