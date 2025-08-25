package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.Pelicula;
import gov.municipio.domain.model.enums.Rol;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class CicloDeCine extends Evento {
    private List<Pelicula> peliculas;
    private String ordenProyeccion;
    private boolean incluyeCharlaPosteriores;

    public CicloDeCine(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, List<Pelicula> peliculas, String ordenProyeccion, boolean incluyeCharlaPosteriores) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.peliculas = peliculas != null ? peliculas : new ArrayList<>();
        this.ordenProyeccion = ordenProyeccion;
        this.incluyeCharlaPosteriores = incluyeCharlaPosteriores;
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ORGANIZADOR || rol == Rol.ASISTENTE;
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
