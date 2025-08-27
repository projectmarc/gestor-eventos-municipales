package gov.municipio.dev;

import gov.municipio.domain.model.Persona;
import gov.municipio.domain.model.evento.*;
import gov.municipio.domain.model.enums.*;
import gov.municipio.domain.exceptions.*;

import java.time.LocalDate;
/**
 * Clase de prueba para demostrar las mejoras del modelo de dominio rico de Evento.
 */
public class EventoDomainTest {
    
    public static void main(String[] args) {
        System.out.println("=== Pruebas del Modelo de Dominio Rico - Evento ===\n");
        
        try {
            // Crear personas para las pruebas
            Persona instructor = new Persona("Carlos", "Rodríguez", "12345678", "1122334455", "carlos@email.com");
            Persona asistente1 = new Persona("Ana", "García", "87654321", "5544332211", "ana@email.com");
            Persona asistente2 = new Persona("Pedro", "López", "11223344", "9988776655", "pedro@email.com");
            Persona organizador = new Persona("María", "González", "44556677", "7766554433", "maria@email.com");
            
            // === Prueba 1: Validaciones en la creación de eventos ===
            System.out.println("--- Prueba 1: Validaciones en la creación ---");
            
            try {
                new Taller("", LocalDate.now().plusDays(1), 4, true, ModalidadTaller.PRESENCIAL, 20);
            } catch (IllegalArgumentException e) {
                System.out.println("✅ Validación correcta: " + e.getMessage());
            }
            
            try {
                new Taller("Taller de Java", LocalDate.now().minusDays(1), 4, true, ModalidadTaller.PRESENCIAL, 20);
            } catch (IllegalArgumentException e) {
                System.out.println("✅ Validación correcta: " + e.getMessage());
            }
            
            try {
                new Taller("Taller de Java", LocalDate.now().plusDays(1), -2, true, ModalidadTaller.PRESENCIAL, 20);
            } catch (IllegalArgumentException e) {
                System.out.println("✅ Validación correcta: " + e.getMessage());
            }
            
            // === Prueba 2: Transiciones de estado ===
            System.out.println("\n--- Prueba 2: Transiciones de estado ---");
            
            Taller taller = new Taller("Taller de Programación", LocalDate.now().plusDays(5), 6, true, ModalidadTaller.PRESENCIAL, 2);
            System.out.println("Estado inicial: " + taller.getEstado());
            
            // Transición válida: PLANIFICACION -> CONFIRMADO
            taller.cambiarEstado(EstadoEvento.CONFIRMADO);
            System.out.println("✅ Estado después de confirmar: " + taller.getEstado());
            
            // Intentar transición inválida: CONFIRMADO -> FINALIZADO (sin pasar por EN_EJECUCION)
            try {
                taller.cambiarEstado(EstadoEvento.FINALIZADO);
            } catch (EstadoEventoInvalidoException e) {
                System.out.println("✅ Transición inválida bloqueada: " + e.getMessage());
            }
            
            // === Prueba 3: Inscripciones y validaciones ===
            System.out.println("\n--- Prueba 3: Inscripciones y validaciones ---");
            
            // Inscribir instructor
            var participacionInstructor = taller.inscribirPersona(instructor, Rol.INSTRUCTOR);
            System.out.println("✅ Instructor inscrito: " + participacionInstructor.getPersona().getNombreCompleto());
            
            // Inscribir asistente 1
            var participacionAsistente1 = taller.inscribirPersona(asistente1, Rol.ASISTENTE);
            System.out.println("✅ Asistente 1 inscrito: " + participacionAsistente1.getPersona().getNombreCompleto());
            
            // Verificar cupo disponible
            System.out.println("Lugares disponibles: " + taller.getLugaresDisponibles());
            
            // Inscribir asistente 2 (completar cupo)
            var participacionAsistente2 = taller.inscribirPersona(asistente2, Rol.ASISTENTE);
            System.out.println("✅ Asistente 2 inscrito: " + participacionAsistente2.getPersona().getNombreCompleto());
            System.out.println("Lugares disponibles después: " + taller.getLugaresDisponibles());
            
            // Intentar inscribir cuando el cupo está lleno
            try {
                taller.inscribirPersona(organizador, Rol.ASISTENTE);
            } catch (CupoMaximoSuperadoException e) {
                System.out.println("✅ Cupo máximo respetado: " + e.getMessage());
            }
            
            // Intentar inscribir persona duplicada
            try {
                taller.inscribirPersona(asistente1, Rol.ASISTENTE);
            } catch (PersonaYaInscritaException e) {
                System.out.println("✅ Inscripción duplicada bloqueada: " + e.getMessage());
            }
            
            // === Prueba 4: Validación de roles por tipo de evento ===
            System.out.println("\n--- Prueba 4: Validación de roles por tipo de evento ---");
            
            Exposicion exposicion = new Exposicion("Exposición de Arte Moderno", LocalDate.now().plusDays(10), 
                8, true, "Pintura Contemporánea");
            exposicion.cambiarEstado(EstadoEvento.CONFIRMADO);
            
            // Rol válido para exposición
            exposicion.inscribirPersona(instructor, Rol.CURADOR);
            System.out.println("✅ Curador inscrito en exposición");
            
            // Rol inválido para exposición
            try {
                exposicion.inscribirPersona(asistente1, Rol.INSTRUCTOR);
            } catch (RolInvalidoParaEventoException e) {
                System.out.println("✅ Rol inválido bloqueado: " + e.getMessage());
            }
            
            // === Prueba 5: Métodos de consulta ===
            System.out.println("\n--- Prueba 5: Métodos de consulta ---");
            
            System.out.println("Instructores en el taller: " + 
                taller.getPersonasPorRol(Rol.INSTRUCTOR).size());
            System.out.println("Asistentes en el taller: " + 
                taller.getPersonasPorRol(Rol.ASISTENTE).size());
            
            System.out.println("¿Taller está activo? " + taller.estaActivo());
            System.out.println("¿Ana está inscrita? " + taller.estaPersonaInscrita(asistente1));
            System.out.println("¿Organizador está inscrito? " + taller.estaPersonaInscrita(organizador));
            
            // === Prueba 6: Estados del evento ===
            System.out.println("\n--- Prueba 6: Estados del evento ---");
            
            taller.cambiarEstado(EstadoEvento.EN_EJECUCION);
            System.out.println("¿Taller está activo? " + taller.estaActivo());
            
            taller.cambiarEstado(EstadoEvento.FINALIZADO);
            System.out.println("¿Taller está cerrado? " + taller.estaCerrado());
            
            // Intentar inscribir en evento finalizado
            try {
                taller.inscribirPersona(organizador, Rol.ASISTENTE);
            } catch (InscripcionNoPermitidaException e) {
                System.out.println("✅ Inscripción en evento finalizado bloqueada: " + e.getMessage());
            }
            
            System.out.println("\n=== Todas las pruebas completadas exitosamente ===");
            
        } catch (Exception e) {
            System.err.println("Error durante las pruebas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
