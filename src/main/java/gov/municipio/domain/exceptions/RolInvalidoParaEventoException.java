package gov.municipio.domain.exceptions;

/**
 * Excepción lanzada cuando se asigna un rol no válido para un tipo de evento específico.
 */
public class RolInvalidoParaEventoException extends EventoException {
    
    public RolInvalidoParaEventoException(String message) {
        super(message);
    }
}
