package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.Rol;
import java.time.LocalDate;
import java.util.List;

public class Concierto extends Evento {
    private String artistaPrincipal;
    private String generoMusical;
    private int capacidadMaxima;

    public Concierto(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, String artistaPrincipal, String generoMusical, int capacidadMaxima) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.artistaPrincipal = validarArtistaPrincipal(artistaPrincipal);
        this.generoMusical = validarGeneroMusical(generoMusical);
        this.capacidadMaxima = validarCapacidadMaxima(capacidadMaxima);
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR || rol == Rol.ARTISTA;
    }

    @Override
    protected List<Rol> rolesResponsablesObligatorios() {
        return List.of(Rol.ORGANIZADOR);
    }

    @Override
    protected int getCupoMaximo() {
        return this.capacidadMaxima;
    }

    public String getArtistaPrincipal() {
        return artistaPrincipal;
    }

    public String getGeneroMusical() {
        return generoMusical;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    private String validarArtistaPrincipal(String artistaPrincipal) {
        if (artistaPrincipal == null || artistaPrincipal.trim().isEmpty()) {
            throw new IllegalArgumentException("El artista principal no puede ser nulo ni vacío");
        }
        return artistaPrincipal.trim();
    }

    private String validarGeneroMusical(String generoMusical) {
        if (generoMusical == null || generoMusical.trim().isEmpty()) {
            throw new IllegalArgumentException("El género musical no puede ser nulo ni vacío");
        }
        return generoMusical.trim();
    }

    private int validarCapacidadMaxima(int capacidadMaxima) {
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad máxima debe ser mayor a 0");
        }
        if (capacidadMaxima > 100000) {
            throw new IllegalArgumentException("La capacidad máxima no puede exceder 100,000 personas");
        }
        return capacidadMaxima;
    }
}