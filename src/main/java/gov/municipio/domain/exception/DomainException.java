package gov.municipio.domain.exception;

/**
 * Excepción base para errores de lógica de negocio del dominio
 */
public class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
