package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.Rol;
import java.time.LocalDate;

public class Concierto extends Evento {
    private String artistaPrincipal;
    private String generoMusical;
    private int capacidadMaxima;

    public Concierto(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, String artistaPrincipal, String generoMusical, int capacidadMaxima) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.artistaPrincipal = artistaPrincipal;
        this.generoMusical = generoMusical;
        this.capacidadMaxima = capacidadMaxima;
        
        // Validar datos específicos después de inicializar los atributos
        validarDatosEspecificos();
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR || rol == Rol.ARTISTA;
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

    @Override
    protected void validarDatosEspecificos() {
        super.validarDatosEspecificos();
        
        if (artistaPrincipal == null || artistaPrincipal.trim().isEmpty()) {
            throw new IllegalArgumentException("El artista principal no puede ser nulo ni vacío");
        }
        if (generoMusical == null || generoMusical.trim().isEmpty()) {
            throw new IllegalArgumentException("El género musical no puede ser nulo ni vacío");
        }
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad máxima debe ser mayor a 0");
        }
        if (capacidadMaxima > 100000) {
            throw new IllegalArgumentException("La capacidad máxima no puede exceder 100,000 personas");
        }
    }
}