package gov.municipio.domain.validator;

import gov.municipio.domain.exception.DomainException;
import gov.municipio.domain.model.Persona;

/**
 * Validador para entidades Persona
 */
public class PersonaValidator {
    
    public static void validate(Persona persona) {
        if (persona == null) {
            throw new DomainException("La persona no puede ser nula");
        }
        
        validateNombre(persona.getNombre());
        validateApellido(persona.getApellido());
        validateDni(persona.getDni());
        validateEmail(persona.getEmail());
    }
    
    private static void validateNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DomainException("El nombre es obligatorio");
        }
        if (nombre.length() > 50) {
            throw new DomainException("El nombre no puede exceder 50 caracteres");
        }
    }
    
    private static void validateApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new DomainException("El apellido es obligatorio");
        }
        if (apellido.length() > 50) {
            throw new DomainException("El apellido no puede exceder 50 caracteres");
        }
    }
    
    private static void validateDni(int dni) {
        if (dni <= 0) {
            throw new DomainException("El DNI debe ser un número positivo");
        }
        if (String.valueOf(dni).length() < 7 || String.valueOf(dni).length() > 8) {
            throw new DomainException("El DNI debe tener entre 7 y 8 dígitos");
        }
    }
    
    private static void validateEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$")) {
                throw new DomainException("El formato del email no es válido");
            }
        }
    }
}
