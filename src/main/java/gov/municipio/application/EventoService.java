package gov.municipio.application;

import gov.municipio.domain.model.evento.*;
import gov.municipio.persistence.evento.EventoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para la gestión de eventos.
 * Orquesta las operaciones de dominio y persistencia.
 */
public class EventoService {
    
    private final EventoRepository eventoRepository;
    
    /**
     * Constructor que recibe el repositorio por inyección de dependencias manual.
     * 
     * @param eventoRepository el repositorio de eventos
     */
    public EventoService(EventoRepository eventoRepository) {
        if (eventoRepository == null) {
            throw new IllegalArgumentException("EventoRepository no puede ser nulo");
        }
        this.eventoRepository = eventoRepository;
    }
    
    /**
     * Crea un nuevo evento básico según el tipo especificado.
     * Solo crea eventos con los datos básicos, usando valores por defecto para datos específicos.
     * 
     * @param tipoEvento el tipo de evento ("Feria", "Concierto", "Exposición", "Taller", "Ciclo de cine")
     * @param nombre el nombre del evento
     * @param fechaInicio la fecha de inicio
     * @param duracionEstimada la duración en horas
     * @param requiereInscripcion si requiere inscripción
     * @param cupoMaximo el cupo máximo (obligatorio si requiere inscripción)
     * @return el evento creado con su ID asignado
     * @throws IllegalArgumentException si algún parámetro no es válido
     */
    public Evento crearEventoBasico(String tipoEvento, String nombre, LocalDate fechaInicio, 
                                   int duracionEstimada, boolean requiereInscripcion, Integer cupoMaximo) {
        
        Evento evento = switch (tipoEvento) {
            case "Feria" -> new Feria(1, gov.municipio.domain.model.enums.TipoLugar.TECHADO, 
                                     nombre, fechaInicio, duracionEstimada, requiereInscripcion, cupoMaximo);
            
            case "Concierto" -> new Concierto(nombre, fechaInicio, duracionEstimada, requiereInscripcion, 
                                             java.util.Collections.emptyList(), cupoMaximo);
            
            case "Exposición" -> new Exposicion(nombre, fechaInicio, duracionEstimada, requiereInscripcion, 
                                               cupoMaximo);
            
            case "Taller" -> new Taller(nombre, fechaInicio, duracionEstimada, 
                                       gov.municipio.domain.model.enums.ModalidadTaller.PRESENCIAL, cupoMaximo);
            
            case "Ciclo de cine" -> new CicloDeCine(nombre, fechaInicio, duracionEstimada, requiereInscripcion, 
                                                   cupoMaximo, java.util.Collections.emptyList(), false);
            
            default -> throw new IllegalArgumentException("Tipo de evento no válido: " + tipoEvento);
        };
        
        return eventoRepository.save(evento);
    }
    
    /**
     * Busca un evento por su ID.
     * 
     * @param id el ID del evento
     * @return Optional con el evento si existe
     */
    public Optional<Evento> buscarEventoPorId(Long id) {
        return eventoRepository.findById(id);
    }
    
    /**
     * Obtiene todos los eventos registrados.
     * 
     * @return lista de todos los eventos ordenados por fecha de inicio
     */
    public List<Evento> obtenerTodosLosEventos() {
        return eventoRepository.findAll();
    }
    
    /**
     * Actualiza un evento existente.
     * Solo permite actualizar eventos que estén en estado PLANIFICACION.
     * 
     * @param evento el evento a actualizar
     * @return el evento actualizado
     * @throws IllegalStateException si el evento no está en PLANIFICACION
     */
    public Evento actualizarEvento(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo");
        }
        if (!evento.estaEnPlanificacion()) {
            throw new IllegalStateException("Solo se pueden editar eventos en estado PLANIFICACION");
        }
        return eventoRepository.save(evento);
    }
}
