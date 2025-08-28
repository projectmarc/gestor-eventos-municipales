package gov.municipio.domain.model;

import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.evento.Evento;

import java.util.Objects;

/**
 * Representa la participación de una persona en un evento con un rol específico.
 * Modelo simple: las validaciones de negocio (estado, cupo, duplicados, rol permitido)
 * se centralizan en Evento. Aquí solo validamos nulos.
 *
 * Identidad: una persona tiene UN solo rol por evento -> equals/hashCode por (persona, evento).
 */
public class Participacion {
    private final Persona persona;
    private final Evento evento;
    private final Rol rol;

    // Se mantiene público por estar en otro paquete que Evento;
    // la regla es: crearla SIEMPRE a través de métodos de Evento.
    public Participacion(Persona persona, Evento evento, Rol rol) {
        if (persona == null) throw new IllegalArgumentException("La persona no puede ser nula");
        if (evento == null)  throw new IllegalArgumentException("El evento no puede ser nulo");
        if (rol == null)     throw new IllegalArgumentException("El rol no puede ser nulo");
        this.persona = persona;
        this.evento = evento;
        this.rol = rol;
    }

    public Persona getPersona() { return persona; }
    public Evento getEvento()   { return evento; }
    public Rol getRol()         { return rol; }

    public boolean esRolDeLiderazgo() {
        return rol == Rol.ORGANIZADOR || rol == Rol.INSTRUCTOR ||
               rol == Rol.CURADOR     || rol == Rol.ARTISTA;
    }
    public boolean esAsistente() { return rol == Rol.ASISTENTE; }

    // Un solo rol por evento -> identidad por persona+evento
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participacion p)) return false;
        return Objects.equals(persona, p.persona) &&
               Objects.equals(evento,  p.evento);
    }
    @Override
    public int hashCode() {
        return Objects.hash(persona, evento);
    }

    @Override
    public String toString() {
        return String.format("Participacion{persona='%s', evento='%s', rol=%s}",
                persona != null ? persona.getNombreCompleto() : "null",
                evento  != null ? evento.getNombre()         : "null",
                rol);
    }
}
