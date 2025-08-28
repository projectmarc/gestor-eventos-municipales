package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.Pelicula;
import gov.municipio.domain.model.enums.Rol;
import jakarta.persistence.*;

import java.time.LocalDate;
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
    private final List<Pelicula> peliculasEnOrden;
    
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
        this.peliculasEnOrden = validarPeliculas(peliculasEnOrden);
        this.incluyeCharlasPosteriores = incluyeCharlasPosteriores;
    }

    // Constructor protegido para JPA
    protected CicloDeCine() {
        super();
        this.peliculasEnOrden = null;
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
            return List.copyOf(peliculas);
        }
        
        if (peliculas.stream().anyMatch(p -> p == null)) {
            throw new IllegalArgumentException("Ninguna película puede ser nula");
        }
        if (peliculas.size() != peliculas.stream().distinct().count()) {
            throw new IllegalArgumentException("No se permiten películas repetidas en el ciclo");
        }
        return List.copyOf(peliculas); // Inmutable y orden preservado
    }
}
