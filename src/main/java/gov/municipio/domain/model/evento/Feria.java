package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.enums.TipoLugar;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ferias")
public class Feria extends Evento {
    @Column(nullable = false)
    private int cantidadStands;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoLugar tipoLocacion;

    public Feria() {}

    public Feria(int cantidadStands, TipoLugar tipoLocacion, String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.cantidadStands = cantidadStands;
        this.tipoLocacion = tipoLocacion;
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ORGANIZADOR || rol == Rol.ASISTENTE;
    }

    public int getCantidadStands() {
        return cantidadStands;
    }

    public TipoLugar getTipoLocacion() {
        return tipoLocacion;
    }
}
