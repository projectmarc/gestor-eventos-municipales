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
        this.peliculas = peliculas != null ? new ArrayList<>(peliculas) : new ArrayList<>();
        this.ordenProyeccion = ordenProyeccion;
        this.incluyeCharlaPosteriores = incluyeCharlaPosteriores;
        
        // Validar datos específicos después de inicializar los atributos
        validarDatosEspecificos();
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ORGANIZADOR || rol == Rol.ASISTENTE;
    }

    public List<Pelicula> getPeliculas() {
        return new ArrayList<>(peliculas); // Copia defensiva
    }

    public String getOrdenProyeccion() {
        return ordenProyeccion;
    }

    public boolean incluyeCharlaPosteriores() {
        return incluyeCharlaPosteriores;
    }

    @Override
    protected void validarDatosEspecificos() {
        super.validarDatosEspecificos();
        
        if (peliculas == null || peliculas.isEmpty()) {
            throw new IllegalArgumentException("El ciclo de cine debe tener al menos una película");
        }
        if (peliculas.size() > 20) {
            throw new IllegalArgumentException("El ciclo de cine no puede tener más de 20 películas");
        }
        if (ordenProyeccion == null || ordenProyeccion.trim().isEmpty()) {
            throw new IllegalArgumentException("El orden de proyección no puede ser nulo ni vacío");
        }
        
        // Validar que todas las películas sean válidas
        for (Pelicula pelicula : peliculas) {
            if (pelicula == null) {
                throw new IllegalArgumentException("Las películas del ciclo no pueden ser nulas");
            }
        }
    }
}
