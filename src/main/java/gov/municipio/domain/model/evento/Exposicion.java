package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.Rol;
import java.time.LocalDate;
public class Exposicion extends Evento{
    private String disciplinaArtistica;

    public Exposicion(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, String disciplinaArtistica) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.disciplinaArtistica = disciplinaArtistica;
    }

    public String getDisciplinaArtistica() {
        return disciplinaArtistica;
    }

    public boolean validarRol(Rol rol) {
        return rol == Rol.CURADOR || rol == Rol.PARTICIPANTE;
    }
}
