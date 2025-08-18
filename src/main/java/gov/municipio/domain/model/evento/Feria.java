package gov.municipio.domain.model.evento;
import gov.municipio.domain.model.enums.TipoLugar;
import java.time.LocalDate; 

public class Feria extends Evento{
    private int cantidadStands;
    private TipoLugar tipoLocacion;

    public Feria(int cantidadStands, TipoLugar tipoLocacion, String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.cantidadStands = cantidadStands;
        this.tipoLocacion = tipoLocacion;
    }

    public int getCantidadStands() {
        return cantidadStands;
    }

    public TipoLugar getTipoLocacion() {
        return tipoLocacion;
    }
}
