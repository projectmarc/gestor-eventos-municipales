package gov.municipio.util;

import gov.municipio.domain.model.Persona;
import gov.municipio.domain.model.Pelicula;
import gov.municipio.domain.model.evento.*;
import gov.municipio.domain.model.enums.*;
import gov.municipio.domain.service.EventoService;
import gov.municipio.domain.service.PersonaService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Clase utilitaria para inicializar datos de prueba en la aplicación
 */
public class DataInitializer {
    
    private final PersonaService personaService;
    private final EventoService eventoService;
    
    public DataInitializer() {
        this.personaService = PersonaService.getInstance();
        this.eventoService = EventoService.getInstance();
    }
    
    public void inicializarDatosPrueba() {
        try {
            // Verificar si ya hay datos
            if (!personaService.listarTodas().isEmpty()) {
                System.out.println("Ya existen datos en la base de datos");
                return;
            }
            
            System.out.println("Inicializando datos de prueba...");
            
            // Crear personas de prueba
            Persona[] personas = crearPersonasPrueba();
            
            // Crear eventos de prueba
            crearEventosPrueba(personas);
            
            System.out.println("Datos de prueba inicializados exitosamente");
            
        } catch (Exception e) {
            System.err.println("Error al inicializar datos de prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Persona[] crearPersonasPrueba() {
        Persona[] personas = {
            new Persona("Juan", "Pérez", 12345678, "555-0101", "juan.perez@email.com"),
            new Persona("María", "González", 23456789, "555-0102", "maria.gonzalez@email.com"),
            new Persona("Carlos", "López", 34567890, "555-0103", "carlos.lopez@email.com"),
            new Persona("Ana", "Martínez", 45678901, "555-0104", "ana.martinez@email.com"),
            new Persona("Pedro", "Rodríguez", 56789012, "555-0105", "pedro.rodriguez@email.com"),
            new Persona("Laura", "Fernández", 67890123, "555-0106", "laura.fernandez@email.com"),
            new Persona("Miguel", "García", 78901234, "555-0107", "miguel.garcia@email.com"),
            new Persona("Sofia", "Ruiz", 89012345, "555-0108", "sofia.ruiz@email.com")
        };
        
        for (Persona persona : personas) {
            personaService.guardar(persona);
        }
        
        return personas;
    }
    
    private void crearEventosPrueba(Persona[] personas) {
        // Crear Feria
        Feria feria = new Feria("Feria del Libro", LocalDate.now().plusDays(30), 8, false, 
                               25, TipoLugar.TECHADO);
        feria = (Feria) eventoService.guardar(feria);
        eventoService.cambiarEstado(feria.getId(), EstadoEvento.CONFIRMADO);
        eventoService.inscribirPersona(feria.getId(), personas[0].getId(), Rol.ORGANIZADOR);
        
        // Crear Concierto
        Concierto concierto = new Concierto("Concierto de Rock", LocalDate.now().plusDays(15), 3, true,
                                          Arrays.asList("Banda Los Rockers", "Grupo Metal"), 
                                          TipoEntrada.DE_PAGO, "Rock", 500);
        concierto = (Concierto) eventoService.guardar(concierto);
        eventoService.cambiarEstado(concierto.getId(), EstadoEvento.CONFIRMADO);
        eventoService.inscribirPersona(concierto.getId(), personas[1].getId(), Rol.ORGANIZADOR);
        eventoService.inscribirPersona(concierto.getId(), personas[2].getId(), Rol.ARTISTA);
        
        // Crear Exposición
        Exposicion exposicion = new Exposicion("Exposición de Arte Moderno", LocalDate.now().plusDays(45), 
                                             120, false, "Pintura");
        exposicion = (Exposicion) eventoService.guardar(exposicion);
        eventoService.cambiarEstado(exposicion.getId(), EstadoEvento.CONFIRMADO);
        eventoService.inscribirPersona(exposicion.getId(), personas[3].getId(), Rol.CURADOR);
        eventoService.inscribirPersona(exposicion.getId(), personas[4].getId(), Rol.ORGANIZADOR);
        
        // Crear Taller
        Taller taller = new Taller("Taller de Fotografía", LocalDate.now().plusDays(20), 4, true,
                                 ModalidadTaller.PRESENCIAL, 15);
        taller = (Taller) eventoService.guardar(taller);
        eventoService.cambiarEstado(taller.getId(), EstadoEvento.CONFIRMADO);
        eventoService.inscribirPersona(taller.getId(), personas[5].getId(), Rol.INSTRUCTOR);
        
        // Inscribir algunos asistentes al taller
        eventoService.inscribirPersona(taller.getId(), personas[6].getId(), Rol.ASISTENTE);
        eventoService.inscribirPersona(taller.getId(), personas[7].getId(), Rol.ASISTENTE);
        
        // Crear Ciclo de Cine
        List<Pelicula> peliculas = Arrays.asList(
            new Pelicula("El Gran Lebowski", 117, "Hermanos Coen", "Comedia", "Una historia surrealista"),
            new Pelicula("Pulp Fiction", 154, "Quentin Tarantino", "Thriller", "Historias entrelazadas"),
            new Pelicula("El Padrino", 175, "Francis Ford Coppola", "Drama", "La saga de una familia mafiosa")
        );
        
        CicloDeCine cicloCine = new CicloDeCine("Ciclo de Cine Clásico", LocalDate.now().plusDays(60), 
                                             6, true, peliculas, true);
        cicloCine = (CicloDeCine) eventoService.guardar(cicloCine);
        eventoService.cambiarEstado(cicloCine.getId(), EstadoEvento.CONFIRMADO);
        eventoService.inscribirPersona(cicloCine.getId(), personas[0].getId(), Rol.ORGANIZADOR);
    }
}
