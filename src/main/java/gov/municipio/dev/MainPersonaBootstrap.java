package gov.municipio.dev;

import gov.municipio.application.PersonaService;
import gov.municipio.domain.model.Persona;
import gov.municipio.persistence.JpaUtil;
import gov.municipio.persistence.persona.JpaPersonaRepository;
import gov.municipio.persistence.persona.PersonaRepository;

import java.util.List;

/**
 * Clase de bootstrap para probar la funcionalidad de persistencia de Persona.
 * Esta clase es solo para pruebas de desarrollo.
 */
public class MainPersonaBootstrap {
    
    public static void main(String[] args) {
        System.out.println("=== Iniciando prueba de persistencia de Persona ===");
        
        try {
            // Configurar las dependencias manualmente (sin framework DI)
            PersonaRepository personaRepository = new JpaPersonaRepository();
            PersonaService personaService = new PersonaService(personaRepository);
            
            // Probar el registro de personas
            System.out.println("\n--- Registrando personas ---");
            
            Persona persona1 = personaService.registrarPersona(
                "Juan Carlos", 
                "Pérez", 
                "12345678", 
                "1122334455", 
                "juan.perez@email.com"
            );
            System.out.println("Persona 1 registrada: " + persona1);
            
            Persona persona2 = personaService.registrarPersona(
                "María", 
                "González", 
                "87654321", 
                "5544332211", 
                "maria.gonzalez@email.com"
            );
            System.out.println("Persona 2 registrada: " + persona2);
            
            // Probar la búsqueda por DNI
            System.out.println("\n--- Buscando por DNI ---");
            personaService.buscarPersonaPorDni("12345678")
                .ifPresentOrElse(
                    persona -> System.out.println("Encontrada: " + persona),
                    () -> System.out.println("No se encontró persona con DNI 12345678")
                );
            
            // Probar la búsqueda por ID
            System.out.println("\n--- Buscando por ID ---");
            personaService.buscarPersonaPorId(1L)
                .ifPresentOrElse(
                    persona -> System.out.println("Encontrada: " + persona),
                    () -> System.out.println("No se encontró persona con ID 1")
                );
            
            // Probar la obtención de todas las personas
            System.out.println("\n--- Listando todas las personas ---");
            List<Persona> todasLasPersonas = personaService.obtenerTodasLasPersonas();
            System.out.println("Total de personas registradas: " + todasLasPersonas.size());
            todasLasPersonas.forEach(System.out::println);
            
            // Probar la actualización de datos de contacto
            System.out.println("\n--- Actualizando datos de contacto ---");
            if (!todasLasPersonas.isEmpty()) {
                Persona primeraPersona = todasLasPersonas.get(0);
                Persona personaActualizada = personaService.actualizarDatosContacto(
                    primeraPersona.getId(), 
                    "9988776655", 
                    "nuevo.email@test.com"
                );
                System.out.println("Persona actualizada: " + personaActualizada);
            }
            
            // Probar validación de DNI duplicado
            System.out.println("\n--- Probando validación de DNI duplicado ---");
            try {
                personaService.registrarPersona(
                    "Pedro", 
                    "López", 
                    "12345678", // DNI duplicado
                    "1111111111", 
                    "pedro.lopez@email.com"
                );
                System.out.println("ERROR: Se permitió registrar DNI duplicado");
            } catch (IllegalStateException e) {
                System.out.println("Validación correcta: " + e.getMessage());
            }
            
            // Listar personas finales
            System.out.println("\n--- Estado final de personas ---");
            List<Persona> personasFinales = personaService.obtenerTodasLasPersonas();
            personasFinales.forEach(System.out::println);
            
            System.out.println("\n=== Prueba completada exitosamente ===");
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar el EntityManagerFactory
            JpaUtil.closeEntityManagerFactory();
            System.out.println("EntityManagerFactory cerrado.");
        }
    }
}
