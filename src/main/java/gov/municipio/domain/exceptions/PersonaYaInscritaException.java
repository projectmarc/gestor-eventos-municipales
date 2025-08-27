package gov.municipio.domain.exceptions;

/**
 * Excepción lanzada cuando una persona ya está inscrita en un evento
 * y se intenta inscribir nuevamente.
 */
public class PersonaYaInscritaException extends EventoException {
    
    public PersonaYaInscritaException(String message) {
        super(message);
    }
}
