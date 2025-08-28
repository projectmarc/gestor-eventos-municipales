package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.Rol;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "exposiciones")
public class Exposicion extends Evento {

    public Exposicion(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, Integer cupoMaximo) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion, cupoMaximo);
    }

    // Constructor protegido para JPA
    protected Exposicion() {
        super();
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.CURADOR || rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR;
    }

    @Override
    protected List<Rol> rolesResponsablesObligatorios() {
        return List.of(Rol.ORGANIZADOR, Rol.CURADOR);
    }
}
