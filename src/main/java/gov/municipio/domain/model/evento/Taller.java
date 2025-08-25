package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.Persona;
import gov.municipio.domain.model.enums.ModalidadTaller;
import gov.municipio.domain.model.enums.Rol;
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

    public Taller() {}

    public Taller(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, 
                  ModalidadTaller modalidad, int cupoMaximo) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.modalidad = modalidad;
        this.cupoMaximo = cupoMaximo;
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.INSTRUCTOR || rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR;
    }

    @Override
    public boolean validarCupo() {
        if (cupoMaximo <= 0) return true; // Sin límite si es 0 o negativo
        return getAsistentesInscriptos() < cupoMaximo;
    }

    public void asignarInstructor(Persona instructor) {
        if (instructor == null) {
            throw new IllegalArgumentException("El instructor no puede ser nulo");
        }
        this.instructor = instructor;
        // También crear la participación
        inscribirPersona(instructor, Rol.INSTRUCTOR);
    }

    // Getters y Setters
    public ModalidadTaller getModalidad() {
        return modalidad;
    }

    public void setModalidad(ModalidadTaller modalidad) {
        this.modalidad = modalidad;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public Persona getInstructor() {
        return instructor;
    }

    public void setInstructor(Persona instructor) {
        this.instructor = instructor;
    }

    public int getCupoDisponible() {
        return cupoMaximo - getAsistentesInscriptos();
    }
}
