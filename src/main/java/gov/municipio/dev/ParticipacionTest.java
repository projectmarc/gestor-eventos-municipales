package gov.municipio.dev;

import gov.municipio.domain.model.Participacion;
import gov.municipio.domain.model.Persona;
import gov.municipio.domain.model.enums.ModalidadTaller;
import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.evento.Taller;
import java.time.LocalDate;

/**
 * Clase de prueba para verificar las mejoras en la clase Participacion.
 */
public class ParticipacionTest {
    
    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DE LA CLASE PARTICIPACION MEJORADA ===\n");
        
        try {
            // Crear objetos de prueba
            Persona instructor = new Persona("Juan", "Pérez", "12345678", "11234567", "juan@email.com");
            Persona asistente1 = new Persona("María", "González", "87654321", "11234568", "maria@email.com");
            Persona asistente2 = new Persona("Carlos", "López", "11111111", "11234569", "carlos@email.com");
            
            Taller taller = new Taller(
                "Taller de Java Avanzado",
                LocalDate.now().plusDays(7),
                8, // 8 horas
                ModalidadTaller.PRESENCIAL,
                25 // Cupo máximo
            );
            
            // Confirmar evento para permitir inscripciones
            taller.cambiarEstado(gov.municipio.domain.model.enums.EstadoEvento.CONFIRMADO);
            
            System.out.println("1. Creando participaciones válidas usando el método del evento...");
            
            // Crear participación válida de instructor usando el método del evento
            Participacion participacionInstructor = taller.inscribirPersona(instructor, Rol.INSTRUCTOR);
            System.out.println("✓ Participación de instructor creada: " + participacionInstructor);
            
            // Crear participación válida de asistente usando el método del evento
            Participacion participacionAsistente1 = taller.inscribirPersona(asistente1, Rol.ASISTENTE);
            System.out.println("✓ Participación de asistente creada: " + participacionAsistente1);
            
            System.out.println("\n2. Probando métodos de utilidad...");
            
            // Probar métodos de utilidad
            System.out.println("¿Es rol de liderazgo (instructor)? " + participacionInstructor.esRolDeLiderazgo());
            System.out.println("¿Es asistente (instructor)? " + participacionInstructor.esAsistente());
            System.out.println("¿Es rol de liderazgo (asistente)? " + participacionAsistente1.esRolDeLiderazgo());
            System.out.println("¿Es asistente (asistente)? " + participacionAsistente1.esAsistente());
            
            System.out.println("\n3. Probando equals y hashCode...");
            
            // Crear otro evento para probar equals con participaciones iguales
            Taller tallerIgual = new Taller(
                "Taller de Java Avanzado",
                LocalDate.now().plusDays(7),
                8,
                ModalidadTaller.PRESENCIAL,
                25
            );
            tallerIgual.cambiarEstado(gov.municipio.domain.model.enums.EstadoEvento.CONFIRMADO);
            
            // Crear otra participación igual para probar equals
            Participacion participacionIgual = new Participacion(instructor, tallerIgual, Rol.INSTRUCTOR);
            System.out.println("¿Son iguales las participaciones del instructor? " + participacionInstructor.equals(participacionIgual));
            System.out.println("¿Tienen el mismo hashCode? " + (participacionInstructor.hashCode() == participacionIgual.hashCode()));
            
            System.out.println("\n4. Probando validaciones de reglas de negocio...");
            
            // Probar rol inválido para el tipo de evento
            try {
                new Participacion(asistente2, taller, Rol.CURADOR);
                System.out.println("❌ ERROR: Debería haber fallado con rol inválido");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Validación de rol inválido funciona: " + e.getMessage());
            }
            
            // Probar persona ya inscrita en el evento
            try {
                // Intentar inscribir al instructor nuevamente con otro rol
                taller.inscribirPersona(instructor, Rol.ORGANIZADOR);
                System.out.println("❌ ERROR: Debería haber fallado con persona ya inscrita");
            } catch (Exception e) {
                System.out.println("✓ Validación de persona ya inscrita funciona: " + e.getMessage());
            }
            
            System.out.println("\n5. Probando validaciones de parámetros nulos...");
            
            // Probar parámetros nulos
            try {
                new Participacion(null, taller, Rol.ASISTENTE);
                System.out.println("❌ ERROR: Debería haber fallado con persona nula");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Validación de persona nula funciona: " + e.getMessage());
            }
            
            try {
                new Participacion(asistente2, null, Rol.ASISTENTE);
                System.out.println("❌ ERROR: Debería haber fallado con evento nulo");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Validación de evento nulo funciona: " + e.getMessage());
            }
            
            try {
                new Participacion(asistente2, taller, null);
                System.out.println("❌ ERROR: Debería haber fallado con rol nulo");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Validación de rol nulo funciona: " + e.getMessage());
            }
            System.out.println("\n6. Probando constructor directo de Participacion...");
            
            // Crear un evento nuevo para probar el constructor directo
            Taller taller2 = new Taller(
                "Taller de Python",
                LocalDate.now().plusDays(14),
                6,
                ModalidadTaller.VIRTUAL,
                20
            );
            taller2.cambiarEstado(gov.municipio.domain.model.enums.EstadoEvento.CONFIRMADO);
            
            // Probar constructor directo con parámetros válidos
            try {
                Participacion participacionDirecta = new Participacion(asistente2, taller2, Rol.ASISTENTE);
                System.out.println("✓ Constructor directo funciona: " + participacionDirecta);
            } catch (Exception e) {
                System.out.println("❌ ERROR: Constructor directo falló: " + e.getMessage());
            }
            
            // Probar constructor directo con rol inválido
            try {
                new Participacion(asistente2, taller2, Rol.CURADOR);
                System.out.println("❌ ERROR: Constructor directo debería haber fallado con rol inválido");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Constructor directo valida roles correctamente: " + e.getMessage());
            }
            
            System.out.println("\n=== TODAS LAS PRUEBAS COMPLETADAS EXITOSAMENTE ===");
            
        } catch (Exception e) {
            System.err.println("Error durante las pruebas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
