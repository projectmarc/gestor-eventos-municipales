package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.Pelicula;
import java.util.List;
import java.time.LocalDate;

public class CicloDeCine extends Evento {
    private List<Pelicula> peliculas;
    private String ordenProyeccion;
    private boolean incluyeCharlaPosteriores;

    public CicloDeCine(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, List<Pelicula> peliculas, String ordenProyeccion, boolean incluyeCharlaPosteriores) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.peliculas = peliculas;
        this.ordenProyeccion = ordenProyeccion;
        this.incluyeCharlaPosteriores = incluyeCharlaPosteriores;
    }

    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    public String getOrdenProyeccion() {
        return ordenProyeccion;
    }

    public boolean isIncluyeCharlaPosteriores() {
        return incluyeCharlaPosteriores;
    }

}
