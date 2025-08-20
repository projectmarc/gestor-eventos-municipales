package gov.municipio.domain.model.evento;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

public class CicloDeCine extends Evento {
    private List<String> peliculas;
    private String ordenProyeccion;
    private boolean incluyeCharlaPosterior;

    public CicloDeCine(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, List<String> peliculas, String ordenProyeccion, boolean incluyeCharlaPosterior) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.peliculas = peliculas;
        this.ordenProyeccion = ordenProyeccion;
        this.incluyeCharlaPosterior = incluyeCharlaPosterior;
    }

    public List<String> getPeliculas() {
        return peliculas;
    }

    public String getOrdenProyeccion() {
        return ordenProyeccion;
    }

    public boolean isIncluyeCharlaPosterior() {
        return incluyeCharlaPosterior;
    }

}
