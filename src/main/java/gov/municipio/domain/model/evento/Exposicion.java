package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.Persona;
import gov.municipio.domain.model.enums.Rol;
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "exposiciones")
public class Exposicion extends Evento {
    @Column(nullable = false)
    private String disciplinaArtistica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curador_id")
    private Persona curador;

    public Exposicion(){}
    
    public Exposicion(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, String disciplinaArtistica) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.disciplinaArtistica = disciplinaArtistica;
    }

    public String getDisciplinaArtistica() {
        return disciplinaArtistica;
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.CURADOR || rol == Rol.ASISTENTE;
    }
}
