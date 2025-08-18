package gov.municipio.domain.model.evento;
import gov.municipio.domain.model.enums.TipoLugar;

public class Feria extends Evento{
    private int cantidadStands;
    private TipoLugar tipoLocacion;

    public Feria(int cantidadStands, TipoLugar tipoLocacion) {
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
