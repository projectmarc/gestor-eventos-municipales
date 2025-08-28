package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.Pelicula;
import gov.municipio.domain.model.enums.Rol;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ciclos_de_cine")
public class CicloDeCine extends Evento {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ciclo_cine_pelicula",
        joinColumns = @JoinColumn(name = "ciclo_cine_id"),
        inverseJoinColumns = @JoinColumn(name = "pelicula_id")
    )
    @OrderColumn(name = "orden")
    private List<Pelicula> peliculasEnOrden;
    
    @Column(nullable = false)
    private final boolean incluyeCharlasPosteriores;

    public CicloDeCine(String nombre,
                       LocalDate fechaInicio,
                       int duracionEstimadaHoras,
                       boolean requiereInscripcion,
                       Integer cupoMaximo,
                       List<Pelicula> peliculasEnOrden,
                       boolean incluyeCharlasPosteriores) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion, cupoMaximo);
        this.peliculasEnOrden = new ArrayList<>(validarPeliculas(peliculasEnOrden));
        this.incluyeCharlasPosteriores = incluyeCharlasPosteriores;
    }

    // Constructor protegido para JPA
    protected CicloDeCine() {
        super();
        this.peliculasEnOrden = new ArrayList<>();
        this.incluyeCharlasPosteriores = false;
    }

    @Override
    public boolean validarRol(Rol rol) {
        // Solo organizadores y asistentes
        return rol == Rol.ORGANIZADOR || rol == Rol.ASISTENTE;
    }

    @Override
    protected List<Rol> rolesResponsablesObligatorios() {
        return List.of(Rol.ORGANIZADOR);
    }

    public List<Pelicula> getPeliculasEnOrden() {
        // Vista inmutable: no se puede alterar desde afuera
        return Collections.unmodifiableList(peliculasEnOrden);
    }

    public boolean incluyeCharlasPosteriores() {
        return incluyeCharlasPosteriores;
    }

    private List<Pelicula> validarPeliculas(List<Pelicula> peliculas) {
        Objects.requireNonNull(peliculas, "La lista de películas no puede ser nula");
        
        // Permitir lista vacía para eventos básicos (se agregarán películas después)
        if (peliculas.isEmpty()) {
            return new ArrayList<>(peliculas);
        }
        
        if (peliculas.stream().anyMatch(p -> p == null)) {
            throw new IllegalArgumentException("Ninguna película puede ser nula");
        }
        if (peliculas.size() != peliculas.stream().distinct().count()) {
            throw new IllegalArgumentException("No se permiten películas repetidas en el ciclo");
        }
        return new ArrayList<>(peliculas); // Mutable pero con contenido validado
    }
    
    /**
     * Agrega una película al final del ciclo.
     * @param pelicula la película a agregar
     * @throws IllegalArgumentException si la película es nula o ya está en el ciclo
     * @throws IllegalStateException si el evento no está en estado PLANIFICACION
     */
    public void agregarPelicula(Pelicula pelicula) {
        if (!estaEnPlanificacion()) {
            throw new IllegalStateException("Solo se pueden agregar películas en estado PLANIFICACION");
        }
        Objects.requireNonNull(pelicula, "La película no puede ser nula");
        
        if (peliculasEnOrden.contains(pelicula)) {
            throw new IllegalArgumentException("La película ya está incluida en el ciclo");
        }
        
        peliculasEnOrden.add(pelicula);
    }
    
    /**
     * Quita una película del ciclo.
     * @param pelicula la película a quitar
     * @throws IllegalArgumentException si la película es nula o no está en el ciclo
     * @throws IllegalStateException si el evento no está en estado PLANIFICACION
     */
    public void quitarPelicula(Pelicula pelicula) {
        if (!estaEnPlanificacion()) {
            throw new IllegalStateException("Solo se pueden quitar películas en estado PLANIFICACION");
        }
        Objects.requireNonNull(pelicula, "La película no puede ser nula");
        
        if (!peliculasEnOrden.contains(pelicula)) {
            throw new IllegalArgumentException("La película no está incluida en el ciclo");
        }
        
        peliculasEnOrden.remove(pelicula);
    }
}
