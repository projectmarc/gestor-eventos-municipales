package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.ModalidadTaller;
import gov.municipio.domain.model.enums.Rol;
import java.time.LocalDate;

public class Taller extends Evento{
    private ModalidadTaller modalidad;
    private int cupoMaximo;

    public Taller(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, ModalidadTaller modalidad, int cupoMaximo) {
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
