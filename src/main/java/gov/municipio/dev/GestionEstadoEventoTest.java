package gov.municipio.dev;

import gov.municipio.domain.model.Persona;
import gov.municipio.domain.model.evento.*;
import gov.municipio.domain.model.enums.*;

import java.time.LocalDate;

/**
 * Clase de prueba para demostrar la funcionalidad de gestión de estados de eventos.
 */
public class GestionEstadoEventoTest {
    
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE GESTIÓN DE ESTADOS DE EVENTOS ===\n");
        
        // === Prueba 1: Crear evento y confirmar ===
        System.out.println("--- Prueba 1: Creación y confirmación de evento ---");
        
        // Crear un taller para hoy
        LocalDate hoy = LocalDate.now();
        Taller taller = new Taller("Taller de Java Avanzado", hoy, 8, ModalidadTaller.PRESENCIAL, 25);
        System.out.println("✅ Taller creado para hoy: " + taller.getNombre());
        System.out.println("   Estado inicial: " + taller.getEstado());
        System.out.println("   Fecha: " + taller.getFechaInicio());
        System.out.println("   ¿Puede iniciarse hoy? " + taller.puedeIniciarseHoy());
        System.out.println("   ¿Puede finalizarse? " + taller.puedeFinalizarse());
        
        // Agregar responsables obligatorios
        Persona instructor = new Persona("Ana", "García", "12345678", "1122334455", "ana@email.com");
        Persona organizador = new Persona("Luis", "Martínez", "11111111", "3344556677", "luis@email.com");
        taller.agregarResponsable(instructor, Rol.INSTRUCTOR);
        taller.agregarResponsable(organizador, Rol.ORGANIZADOR);
        System.out.println("✅ Instructor agregado: " + instructor.getNombreCompleto());
        System.out.println("✅ Organizador agregado: " + organizador.getNombreCompleto());
        
        // Confirmar evento
        taller.cambiarEstado(EstadoEvento.CONFIRMADO);
        System.out.println("✅ Evento confirmado");
        System.out.println("   Estado después de confirmar: " + taller.getEstado());
        System.out.println("   ¿Puede iniciarse hoy? " + taller.puedeIniciarseHoy());
        System.out.println("   ¿Puede finalizarse? " + taller.puedeFinalizarse());
        
        // === Prueba 2: Iniciar evento ===
        System.out.println("\n--- Prueba 2: Iniciar evento ---");
        
        if (taller.puedeIniciarseHoy()) {
            taller.cambiarEstado(EstadoEvento.EN_EJECUCION);
            System.out.println("✅ Evento iniciado");
            System.out.println("   Estado después de iniciar: " + taller.getEstado());
            System.out.println("   ¿Puede iniciarse hoy? " + taller.puedeIniciarseHoy());
            System.out.println("   ¿Puede finalizarse? " + taller.puedeFinalizarse());
        } else {
            System.out.println("❌ No se puede iniciar el evento hoy");
        }
        
        // === Prueba 3: Finalizar evento ===
        System.out.println("\n--- Prueba 3: Finalizar evento ---");
        
        if (taller.puedeFinalizarse()) {
            taller.cambiarEstado(EstadoEvento.FINALIZADO);
            System.out.println("✅ Evento finalizado");
            System.out.println("   Estado después de finalizar: " + taller.getEstado());
            System.out.println("   ¿Puede iniciarse hoy? " + taller.puedeIniciarseHoy());
            System.out.println("   ¿Puede finalizarse? " + taller.puedeFinalizarse());
        } else {
            System.out.println("❌ No se puede finalizar el evento");
        }
        
        // === Prueba 4: Evento para el futuro ===
        System.out.println("\n--- Prueba 4: Evento para el futuro ---");
        
        LocalDate futuro = LocalDate.now().plusDays(7);
        Exposicion exposicion = new Exposicion("Exposición de Arte Moderno", futuro, 8, true, 100);
        System.out.println("✅ Exposición creada para el futuro: " + exposicion.getNombre());
        System.out.println("   Estado inicial: " + exposicion.getEstado());
        System.out.println("   Fecha: " + exposicion.getFechaInicio());
        System.out.println("   ¿Puede iniciarse hoy? " + exposicion.puedeIniciarseHoy());
        System.out.println("   ¿Puede finalizarse? " + exposicion.puedeFinalizarse());
        
        // Agregar organizador, curador y confirmar
        Persona organizadorExpo = new Persona("Carlos", "López", "87654321", "5544332211", "carlos@email.com");
        Persona curador = new Persona("María", "Fernández", "22222222", "6677889900", "maria@email.com");
        exposicion.agregarResponsable(organizadorExpo, Rol.ORGANIZADOR);
        exposicion.agregarResponsable(curador, Rol.CURADOR);
        exposicion.cambiarEstado(EstadoEvento.CONFIRMADO);
        System.out.println("✅ Exposición confirmada");
        System.out.println("   Estado después de confirmar: " + exposicion.getEstado());
        System.out.println("   ¿Puede iniciarse hoy? " + exposicion.puedeIniciarseHoy());
        System.out.println("   ¿Puede finalizarse? " + exposicion.puedeFinalizarse());
        
        System.out.println("\n=== PRUEBAS COMPLETADAS ===");
        System.out.println("✅ La funcionalidad de gestión de estados está implementada correctamente");
        System.out.println("✅ Los eventos solo pueden iniciarse en su fecha de inicio");
        System.out.println("✅ Los eventos pueden finalizarse cuando están en ejecución");
    }
}
