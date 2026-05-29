package ar.edu.unlp.info.oo2.biblioteca;

import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private List<Socio> socios;
    private Exporter exporter;

    public Biblioteca() {
        socios = new ArrayList<>();
        // El enunciado dice que no podes cambiar el codigo en la biblioteca ni pasar
        // por la clase VoorheesExporter, este cambio es necesario
        // exporter= new VoorheesExporter();
    }

    public void agregarSocio(Socio socio) {
        socios.add(socio);
    }

    /**
     * Retorna la representación JSON de la colección de socios.
     */
    public String exportarSocios() {
        return exporter.exportar(socios);
    }

    public Exporter getExporter() {
        return exporter;
    }

    public void setExporter(Exporter exporter) {
        this.exporter = exporter;
    }
}
