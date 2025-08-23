package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.enums.TipoEntrada;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public boolean validarCupo() {
        if (capacidadMaxima <= 0) return true; // Sin límite si es 0 o negativo
        return getAsistentesInscritos() < capacidadMaxima;
    }

    public void agregarArtista(String artista) {
        if (artista == null || artista.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del artista no puede estar vacío");
        }
        if (artistas.contains(artista.trim())) {
            throw new IllegalArgumentException("El artista ya está en la lista");
        }
        artistas.add(artista.trim());
    }

    public void removerArtista(String artista) {
        if (!artistas.remove(artista)) {
            throw new IllegalArgumentException("El artista no está en la lista");
        }
    }

    public boolean tieneArtista(String artista) {
        return artistas.contains(artista);
    }

    public int getCantidadArtistas() {
        return artistas.size();
    }

    public boolean esConciertoPago() {
        return tipoEntrada == TipoEntrada.DE_PAGO;
    }

    public boolean esConciertoGratuito() {
        return tipoEntrada == TipoEntrada.GRATUITA;
    }

    public boolean tieneCapacidadLimitada() {
        return capacidadMaxima > 0;
    }

    public double getPorcentajeOcupacion() {
        if (capacidadMaxima <= 0) return 0.0;
        return (getAsistentesInscritos() * 100.0) / capacidadMaxima;
    }

    public boolean estaCompleto() {
        return capacidadMaxima > 0 && getAsistentesInscritos() >= capacidadMaxima;
    }

    public boolean estaCasiCompleto() {
        return getPorcentajeOcupacion() >= 90.0;
    }

    // Getters y Setters
    public List<String> getArtistas() {
        return artistas;
    }

    public void setArtistas(List<String> artistas) {
        this.artistas = artistas != null ? artistas : new ArrayList<>();
    }

    public TipoEntrada getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(TipoEntrada tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    public String getGeneroMusical() {
        return generoMusical;
    }

    public void setGeneroMusical(String generoMusical) {
        this.generoMusical = generoMusical;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public String getArtistaPrincipal() {
        return artistas.isEmpty() ? null : artistas.get(0);
    }

    public int getCapacidadDisponible() {
        return capacidadMaxima - getAsistentesInscritos();
    }
}