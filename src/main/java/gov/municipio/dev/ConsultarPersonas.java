package gov.municipio.dev;

import gov.municipio.application.PersonaService;
import gov.municipio.domain.model.Persona;
import gov.municipio.persistence.JpaUtil;
import gov.municipio.persistence.persona.JpaPersonaRepository;
import gov.municipio.persistence.persona.PersonaRepository;

import java.util.List;

/**
 * Clase simple para consultar qué personas están en la base de datos.
 */
public class ConsultarPersonas {
    
    public static void main(String[] args) {
        System.out.println("=== Consultando personas en la base de datos ===");
        
        try {
            // Configurar dependencias
            PersonaRepository personaRepository = new JpaPersonaRepository();
            PersonaService personaService = new PersonaService(personaRepository);
            
            // Obtener todas las personas
            List<Persona> personas = personaService.obtenerTodasLasPersonas();
            
            System.out.println("\nPersonas encontradas: " + personas.size());
            System.out.println("=====================================");
            
            if (personas.isEmpty()) {
                System.out.println("No hay personas registradas.");
            } else {
                for (Persona persona : personas) {
                    System.out.println(persona);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error al consultar personas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JpaUtil.closeEntityManagerFactory();
        }
    }
}
