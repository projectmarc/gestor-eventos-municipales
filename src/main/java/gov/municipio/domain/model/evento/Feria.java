package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.enums.TipoLugar;
import java.time.LocalDate;
import java.util.List;

public class Feria extends Evento {
    private int cantidadStands;
    private TipoLugar tipoLugar;

    public Feria(int cantidadStands, TipoLugar tipoLugar, String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, Integer cupoMaximo) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion, cupoMaximo);
        this.cantidadStands = validarCantidadStands(cantidadStands);
        this.tipoLugar = validarTipoLugar(tipoLugar);
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ORGANIZADOR || rol == Rol.ASISTENTE;
    }

    @Override
    protected List<Rol> rolesResponsablesObligatorios() {
        return List.of(Rol.ORGANIZADOR);
    }

    public int getCantidadStands() {
        return cantidadStands;
    }

    public TipoLugar getTipoLugar() {
        return tipoLugar;
    }

    private int validarCantidadStands(int cantidadStands) {
        if (cantidadStands <= 0) {
            throw new IllegalArgumentException("La cantidad de stands debe ser mayor a 0");
        }
        if (cantidadStands > 500) {
            throw new IllegalArgumentException("La cantidad de stands no puede exceder 500");
        }
        return cantidadStands;
    }

    private TipoLugar validarTipoLugar(TipoLugar tipoLugar) {
        if (tipoLugar == null) {
            throw new IllegalArgumentException("El tipo de lugar no puede ser nulo");
        }
        return tipoLugar;
    }
}
