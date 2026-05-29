package ar.edu.unlp.info.oo2.biblioteca;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONSimpleExporter implements Exporter {

    @Override
    public String exportar(List<Socio> socios) {
        JSONArray lista = new JSONArray();
        socios.stream().forEach(s -> {
            JSONObject currSocio = new JSONObject();
            currSocio.put("nombre", s.getNombre());
            currSocio.put("email", s.getEmail());
            currSocio.put("legajo", s.getLegajo());
            lista.add(currSocio);
        });
        return lista.toJSONString();
    }
}
