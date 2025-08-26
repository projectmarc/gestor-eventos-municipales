package gov.municipio.application;

public class PersonaService {
    public void registrar(gov.municipio.domain.model.Persona persona) {
        // Lógica para registrar la persona (e.g., guardarla en una base de datos)
        // Por ahora, solo simulamos el registro con un print
        System.out.println("Registrando persona: " + persona.getApellido() + ", " + persona.getNombre());
    }
}
