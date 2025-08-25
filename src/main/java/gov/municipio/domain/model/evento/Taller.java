package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.ModalidadTaller;
import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.Persona;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "talleres")
public class Taller extends Evento {
    @Enumerated(EnumType.STRING)
    private ModalidadTaller modalidad;

    @Column(nullable = false)
    private int cupoMaximo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Persona instructor;

    public Taller(){}

    public Taller(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, ModalidadTaller modalidad, int cupoMaximo, Persona instructor) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.modalidad = modalidad;
        this.cupoMaximo = cupoMaximo;
    }

    public ModalidadTaller getModalidad() {
        return modalidad;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.INSTRUCTOR || rol == Rol.ASISTENTE;
    }
}
