package gov.municipio.domain.model;

import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.evento.Evento;
import java.util.Objects;

/**
 * Representa la participación de una persona en un evento con un rol específico.
 * Esta clase implementa las reglas de negocio para las participaciones en eventos municipales.
 */
public class Participacion {
    private Persona persona;
    private Evento evento;
    private Rol rol;

    /**
     * Constructor que crea una nueva participación con validaciones de reglas de negocio.
     * 
     * @param persona la persona que participa
     * @param evento el evento en el que participa
     * @param rol el rol de la persona en el evento
     * @throws IllegalArgumentException si algún parámetro es nulo o si las reglas de negocio no se cumplen
     */
    public Participacion(Persona persona, Evento evento, Rol rol) {
        validarParametros(persona, evento, rol);
        validarReglasDeNegocio(persona, evento, rol);
        
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

    /**
     * Verifica si esta participación es de un rol de liderazgo (organizador, instructor, curador, artista).
     * 
     * @return true si el rol es de liderazgo
     */
    public boolean esRolDeLiderazgo() {
        return rol == Rol.ORGANIZADOR || rol == Rol.INSTRUCTOR || 
               rol == Rol.CURADOR || rol == Rol.ARTISTA;
    }

    /**
     * Verifica si esta participación es de asistente.
     * 
     * @return true si el rol es asistente
     */
    public boolean esAsistente() {
        return rol == Rol.ASISTENTE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Participacion that = (Participacion) obj;
        return Objects.equals(persona, that.persona) &&
               Objects.equals(evento, that.evento) &&
               rol == that.rol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(persona, evento, rol);
    }

    @Override
    public String toString() {
        return String.format("Participacion{persona='%s', evento='%s', rol=%s}",
                persona != null ? persona.getNombreCompleto() : "null",
                evento != null ? evento.getNombre() : "null",
                rol);
    }

    // ========== MÉTODOS DE VALIDACIÓN PRIVADOS ==========

    /**
     * Valida que los parámetros no sean nulos.
     */
    private void validarParametros(Persona persona, Evento evento, Rol rol) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo");
        }
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
    }

    /**
     * Valida las reglas de negocio específicas para la participación.
     */
    private void validarReglasDeNegocio(Persona persona, Evento evento, Rol rol) {
        // Validar que el rol sea válido para el tipo de evento
        if (!evento.validarRol(rol)) {
            throw new IllegalArgumentException(
                String.format("El rol %s no es válido para el tipo de evento %s", 
                    rol, evento.getClass().getSimpleName()));
        }

        // Validar que la persona no esté ya inscrita en este evento
        validarPersonaNoInscrita(persona, evento);
    }

    /**
     * Valida que la persona no esté ya inscrita en el evento.
     * Una persona solo puede inscribirse una vez por evento.
     */
    private void validarPersonaNoInscrita(Persona persona, Evento evento) {
        if (evento.estaPersonaInscrita(persona)) {
            throw new IllegalArgumentException(
                String.format("La persona %s ya está inscrita en el evento %s", 
                    persona.getNombreCompleto(), evento.getNombre()));
        }
    }
}
