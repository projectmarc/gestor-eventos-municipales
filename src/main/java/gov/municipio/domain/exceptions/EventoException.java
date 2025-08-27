package gov.municipio.domain.exceptions;

/**
 * Excepción base para errores relacionados con eventos.
 */
public abstract class EventoException extends RuntimeException {
    
    public EventoException(String message) {
        super(message);
    }
    
    public EventoException(String message, Throwable cause) {
        super(message, cause);
    }
}
