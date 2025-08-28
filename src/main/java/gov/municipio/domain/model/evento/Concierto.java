package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.ArtistaBanda;
import gov.municipio.domain.model.enums.Rol;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "conciertos")
public class Concierto extends Evento {
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "concierto_artista_banda",
        joinColumns = @JoinColumn(name = "concierto_id"),
        inverseJoinColumns = @JoinColumn(name = "artista_banda_id")
    )
    private final List<ArtistaBanda> artistasBandas;

    public Concierto(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, List<ArtistaBanda> artistasBandas, Integer cupoMaximo) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion, cupoMaximo);
        this.artistasBandas = validarArtistsBandas(artistasBandas);
    }

    // Constructor protegido para JPA
    protected Concierto() {
        super();
        this.artistasBandas = null;
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR;
    }

    @Override
    protected List<Rol> rolesResponsablesObligatorios() {
        return List.of(Rol.ORGANIZADOR);
    }

    public List<ArtistaBanda> getArtistaBandas() {
        // Vista inmutable: no se puede alterar desde afuera
        return Collections.unmodifiableList(artistasBandas);
    }

    private List<ArtistaBanda> validarArtistsBandas(List<ArtistaBanda> artistasBandas) {
        Objects.requireNonNull(artistasBandas, "La lista de artistas/bandas no puede ser nula");
        
        // Permitir lista vacía para eventos básicos (se agregarán artistas después)
        if (artistasBandas.isEmpty()) {
            return List.copyOf(artistasBandas);
        }
        
        if (artistasBandas.stream().anyMatch(a -> a == null)) {
            throw new IllegalArgumentException("Ningún artista/banda puede ser nulo");
        }
        if (artistasBandas.size() != artistasBandas.stream().distinct().count()) {
            throw new IllegalArgumentException("No se permiten artistas/bandas repetidos en el concierto");
        }
        return List.copyOf(artistasBandas); // Inmutable y orden preservado
    }
}