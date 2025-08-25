package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.enums.TipoLugar;
import java.time.LocalDate;

public class Feria extends Evento {
    private int cantidadStands;
    private TipoLugar tipoLugar;

    public Feria(int cantidadStands, TipoLugar tipoLugar, String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.cantidadStands = cantidadStands;
        this.tipoLugar = tipoLugar;
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ORGANIZADOR || rol == Rol.ASISTENTE;
    }

    public int getCantidadStands() {
        return cantidadStands;
    }

    public TipoLugar getTipoLugar() {
        return tipoLugar;
    }
}
