package gov.municipio.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta inscribir a una persona en un evento
 * en estado no válido para inscripciones.
 */
public class InscripcionNoPermitidaException extends EventoException {
    
    public InscripcionNoPermitidaException(String message) {
        super(message);
    }
}
