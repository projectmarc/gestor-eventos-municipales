package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.Persona;
import gov.municipio.domain.model.enums.Rol;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "exposiciones")
public class Exposicion extends Evento {
    @Column(nullable = false)
    private String disciplinaArtistica;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curador_id")
    private Persona curador;

    public Exposicion() {}

    public Exposicion(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, 
                      String disciplinaArtistica) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.disciplinaArtistica = disciplinaArtistica;
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.CURADOR || rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR;
    }

    public void asignarCurador(Persona curador) {
        if (curador == null) {
            throw new IllegalArgumentException("El curador no puede ser nulo");
        }
        this.curador = curador;
        // También crear la participación
        inscribirPersona(curador, Rol.CURADOR);
    }

    // Getters y Setters
    public String getDisciplinaArtistica() {
        return disciplinaArtistica;
    }

    public void setDisciplinaArtistica(String disciplinaArtistica) {
        this.disciplinaArtistica = disciplinaArtistica;
    }

    public Persona getCurador() {
        return curador;
    }

    public void setCurador(Persona curador) {
        this.curador = curador;
    }
}
