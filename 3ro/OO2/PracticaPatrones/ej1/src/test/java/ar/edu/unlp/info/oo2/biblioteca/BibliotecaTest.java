package ar.edu.unlp.info.oo2.biblioteca;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BibliotecaTest {
    // Para completar
    Biblioteca b;
    Socio s1;

    @BeforeEach
    public void setup() throws Exception {
        b = new Biblioteca();
        s1 = new Socio("Juan Rodriguez", "puto@gmail.com", "2211-2");
    }

    @Test
    public void testExportJSON() {
        s1 = new Socio("Juan Rodriguez", "puto@gmail.com", "2211-2");
        b.agregarSocio(new Socio("Patricio Rivero", "pato@gmail.com", "1111-2"));
        b.agregarSocio(s1);
        System.out.println(b.exportarSocios());
    }
}
