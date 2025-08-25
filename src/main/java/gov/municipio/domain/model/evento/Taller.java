package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.ModalidadTaller;
import gov.municipio.domain.model.enums.Rol;
import java.time.LocalDate;

public class Taller extends Evento {
    private ModalidadTaller modalidad;
    private int cupoMaximo;

    public Taller(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, ModalidadTaller modalidad, int cupoMaximo) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.modalidad = validarModalidad(modalidad);
        this.cupoMaximo = validarCupoMaximo(cupoMaximo);
    }

    @Override
    protected void validarCupoDisponible() {
        int asistentesActuales = getCantidadParticipantesPorRol(Rol.ASISTENTE);
        if (asistentesActuales >= cupoMaximo) {
            throw new IllegalArgumentException("El taller ha alcanzado su cupo máximo de " + cupoMaximo + " asistentes");
        }
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.INSTRUCTOR || rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR;
    }

    public void cambiarCupoMaximo(int nuevoCupo) {
        if (getEstado().ordinal() >= 2) { // EN_EJECUCION o FINALIZADO
            throw new IllegalArgumentException("No se puede cambiar el cupo de un taller en ejecución o finalizado");
        }
        
        int asistentesActuales = getCantidadParticipantesPorRol(Rol.ASISTENTE);
        if (nuevoCupo < asistentesActuales) {
            throw new IllegalArgumentException("El nuevo cupo (" + nuevoCupo + ") no puede ser menor que los asistentes actuales (" + asistentesActuales + ")");
        }
        
        this.cupoMaximo = validarCupoMaximo(nuevoCupo);
    }

    public int getCuposDisponibles() {
        return cupoMaximo - getCantidadParticipantesPorRol(Rol.ASISTENTE);
    }

    public boolean tieneCupoDisponible() {
        return getCuposDisponibles() > 0;
    }

    // Validaciones privadas
    private ModalidadTaller validarModalidad(ModalidadTaller modalidad) {
        if (modalidad == null) {
            throw new IllegalArgumentException("La modalidad del taller no puede ser nula");
        }
        return modalidad;
    }

    private int validarCupoMaximo(int cupo) {
        if (cupo <= 0) {
            throw new IllegalArgumentException("El cupo máximo debe ser positivo");
        }
        if (cupo > 500) {
            throw new IllegalArgumentException("El cupo máximo no puede superar los 500 asistentes");
        }
        return cupo;
    }

    // Getters
    public ModalidadTaller getModalidad() {
        return modalidad;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    @Override
    public String toString() {
        return String.format("Taller{nombre='%s', modalidad=%s, cupo=%d/%d, fechaInicio=%s, estado=%s}",
                getNombre(), modalidad, getCantidadParticipantesPorRol(Rol.ASISTENTE), cupoMaximo, 
                getFechaInicio(), getEstado());
    }
}
