package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.ModalidadTaller;
import gov.municipio.domain.model.enums.Rol;
import java.time.LocalDate;
import java.util.List;

public class Taller extends Evento {
    private ModalidadTaller modalidad;
    private int cupoMaximo;

    public Taller(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, ModalidadTaller modalidad, int cupoMaximo) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.modalidad = validarModalidad(modalidad);
        this.cupoMaximo = validarCupoMaximo(cupoMaximo);
    }

    public ModalidadTaller getModalidad() {
        return modalidad;
    }

    public int getCupoMaximoTaller() {
        return cupoMaximo;
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.INSTRUCTOR || rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR;
    }

    @Override
    protected List<Rol> rolesResponsablesObligatorios() {
        return List.of(Rol.ORGANIZADOR, Rol.INSTRUCTOR);
    }

    @Override
    protected int getCupoMaximo() {
        return this.cupoMaximo;
    }

    private ModalidadTaller validarModalidad(ModalidadTaller modalidad) {
        if (modalidad == null) {
            throw new IllegalArgumentException("La modalidad del taller no puede ser nula");
        }
        return modalidad;
    }

    private int validarCupoMaximo(int cupoMaximo) {
        if (cupoMaximo <= 0) {
            throw new IllegalArgumentException("El cupo máximo debe ser mayor a 0");
        }
        if (cupoMaximo > 50) {
            throw new IllegalArgumentException("El cupo máximo no puede exceder 50 participantes");
        }
        return cupoMaximo;
    }
}
