package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnfrentamientoTest {

    Tijera t;
    Piedra pi;
    Papel pa;

    @BeforeEach
    public void setup() throws Exception {
        t = new Tijera();
        pi = new Piedra();
        pa = new Papel();
    }

    @Test
    public void testTijera() {
        Tijera t2 = new Tijera();
        assertEquals("Empate", t.enfrentar(t2));
        assertEquals("Tijera", t.enfrentar(pa));
        assertEquals("Piedra", t.enfrentar(pi));
    }

    @Test
    public void testPapel() {
        Papel p2 = new Papel();
        assertEquals("Empate", pa.enfrentar(p2));
        assertEquals("Papel", pa.enfrentar(pi));
        assertEquals("Tijera", pa.enfrentar(t));
    }

    @Test
    public void testPiedra() {
        Piedra p2 = new Piedra();
        assertEquals("Empate", pi.enfrentar(p2));
        assertEquals("Papel", pi.enfrentar(pa));
        assertEquals("Piedra", pi.enfrentar(t));
    }
}
