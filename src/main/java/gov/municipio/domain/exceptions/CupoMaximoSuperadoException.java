package gov.municipio.domain.exceptions;

/**
 * Excepción lanzada cuando se supera el cupo máximo permitido para un evento.
 */
public class CupoMaximoSuperadoException extends EventoException {
    
    public CupoMaximoSuperadoException(String message) {
        super(message);
    }
}
