package gov.municipio.domain.exception;

/**
 * Excepción específica para violaciones de reglas de negocio en eventos
 */
public class EventoBusinessException extends DomainException {
    
    public EventoBusinessException(String message) {
        super(message);
    }
    
    public EventoBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
