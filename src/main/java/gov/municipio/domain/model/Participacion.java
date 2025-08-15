package gov.municipio.domain.model;

import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.evento.Evento;

public class Participacion {
    private Persona persona;
    private Evento evento;
    private Rol rol;

    public Participacion(Persona persona, Evento evento, Rol rol) {
        if(persona == null || evento == null || rol == null) {
            throw new IllegalArgumentException("Los parámetros no pueden ser nulos");
        }
        this.persona = persona;
        this.evento = evento;
        this.rol = rol;
    }

    public Persona getPersona() {
        return persona;
    }

    public Evento getEvento() {
        return evento;
    }

    public Rol getRol() {
        return rol;
    }
}
