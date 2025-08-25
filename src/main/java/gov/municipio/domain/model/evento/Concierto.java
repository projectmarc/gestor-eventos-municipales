package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.enums.TipoEntrada;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "conciertos")
public class Concierto extends Evento {
    @ElementCollection
    @CollectionTable(name = "concierto_artistas", joinColumns = @JoinColumn(name = "concierto_id"))
    @Column(name = "artista")
    private List<String> artistas = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEntrada tipoEntrada;
    
    private String generoMusical;
    private int capacidadMaxima;

    public Concierto() {}

    public Concierto(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, 
                     List<String> artistas, TipoEntrada tipoEntrada, String generoMusical, int capacidadMaxima) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.artistas = artistas != null ? artistas : new ArrayList<>();
        this.tipoEntrada = tipoEntrada;
        this.generoMusical = generoMusical;
        this.capacidadMaxima = capacidadMaxima;
    }


    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR || rol == Rol.ARTISTA;
    }

    public List<String> getArtistas() {
        return artistas;
    }

    public String getGeneroMusical() {
        return generoMusical;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }
}