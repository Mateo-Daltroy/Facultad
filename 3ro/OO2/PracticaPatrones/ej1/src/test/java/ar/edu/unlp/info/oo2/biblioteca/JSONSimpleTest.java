package ar.edu.unlp.info.oo2.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JSONSimpleTest {
    // Para completar
    List<Socio> socios;
    JSONSimpleExporter jsExport;

    @BeforeEach
    public void setup() throws Exception {
        socios = new ArrayList<>();
        socios.add(new Socio("Juan Rodriguez", "gonchi@gmail.com", "2211-2"));
        socios.add(new Socio("Patricio Rivero", "patoRiv@gmail.com", "1111-2"));
        jsExport = new JSONSimpleExporter();
    }

    @Test
    public void testExportJSON() throws Exception {
        JSONParser parser = new JSONParser();
        List<Socio> parsedSocios = new ArrayList<>();

        JSONArray arr = (JSONArray) parser.parse(jsExport.exportar(socios));
        for (Object obj : arr) {
            JSONObject socioObj = (JSONObject) obj;
            String nombre = (String) socioObj.get("nombre");
            String email = (String) socioObj.get("email");
            String legajo = (String) socioObj.get("legajo");
            parsedSocios.add(new Socio(nombre, email, legajo));
        }

        boolean result = socios.stream()
                .allMatch(socio -> parsedSocios.stream()
                        .anyMatch(parsedSocio -> parsedSocio.contentEquals(socio)));
        assertTrue(result);
        assertEquals(socios.size(), parsedSocios.size());
    }
}
