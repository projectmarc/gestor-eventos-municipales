package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.ModalidadTaller;
import gov.municipio.domain.model.enums.Rol;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "talleres")
public class Taller extends Evento {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModalidadTaller modalidad;

    public Taller(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, ModalidadTaller modalidad, Integer cupoMaximo) {
        super(nombre, fechaInicio, duracionEstimadaHoras, true, cupoMaximo); // Los talleres SIEMPRE requieren inscripción
        this.modalidad = validarModalidad(modalidad);
    }

    // Constructor protegido para JPA
    protected Taller() {
        super();
    }

    public ModalidadTaller getModalidad() {
        return modalidad;
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.INSTRUCTOR || rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR;
    }

    @Override
    protected List<Rol> rolesResponsablesObligatorios() {
        return List.of(Rol.ORGANIZADOR, Rol.INSTRUCTOR);
    }

    private ModalidadTaller validarModalidad(ModalidadTaller modalidad) {
        if (modalidad == null) {
            throw new IllegalArgumentException("La modalidad del taller no puede ser nula");
        }
        return modalidad;
    }
}
