package gov.municipio.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta cambiar un evento a un estado no válido.
 */
public class EstadoEventoInvalidoException extends EventoException {
    
    public EstadoEventoInvalidoException(String message) {
        super(message);
    }
}
