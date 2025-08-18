package gov.municipio.domain.model.evento;
import java.time.LocalDate;
public class Concierto extends Evento {
    private String artistaPrincipal;
    private String generoMusical;
    private int capacidadMaxima;

    public Concierto(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requierInscripcion, String artistaPrincipal, String generoMusical, int capacidadMaxima) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requierInscripcion);
        this.artistaPrincipal = artistaPrincipal;
        this.generoMusical = generoMusical;
        this.capacidadMaxima = capacidadMaxima;
    }

}