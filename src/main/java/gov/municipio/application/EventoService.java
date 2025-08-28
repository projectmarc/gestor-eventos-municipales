package gov.municipio.application;

import gov.municipio.domain.model.ArtistaBanda;
import gov.municipio.domain.model.Pelicula;
import gov.municipio.domain.model.evento.*;
import gov.municipio.domain.model.enums.EstadoEvento;
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
     * Busca un evento por su ID cargando las participaciones de forma eager.
     * Útil para las pantallas de gestión donde se necesita acceder a las participaciones.
     * 
     * @param id el ID del evento
     * @return Optional con el evento si existe, con las participaciones cargadas
     */
    public Optional<Evento> buscarEventoPorIdConParticipaciones(Long id) {
        return eventoRepository.findByIdWithParticipaciones(id);
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
     * Obtiene los eventos que ocurren en un rango de fechas específico.
     * Útil para mostrar eventos en el calendario mensual.
     * 
     * @param fechaInicio la fecha de inicio del rango (inclusive)
     * @param fechaFin la fecha de fin del rango (inclusive)
     * @return lista de eventos que tienen su fecha de inicio dentro del rango especificado
     */
    public List<Evento> obtenerEventosPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        return eventoRepository.findAll().stream()
            .filter(evento -> {
                LocalDate fechaEvento = evento.getFechaInicio();
                return !fechaEvento.isBefore(fechaInicio) && !fechaEvento.isAfter(fechaFin);
            })
            .collect(java.util.stream.Collectors.toList());
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
    
    /**
     * Confirma un evento cambiando su estado de PLANIFICACION a CONFIRMADO.
     * Valida que el evento tenga todos los responsables obligatorios antes de confirmarlo.
     * 
     * @param eventoId el ID del evento a confirmar
     * @return el evento confirmado con todas las participaciones cargadas
     * @throws IllegalArgumentException si el evento no existe
     * @throws IllegalStateException si el evento no está en PLANIFICACION o no cumple las condiciones para ser confirmado
     */
    public Evento confirmarEvento(Long eventoId) {
        Optional<Evento> eventoOpt = buscarEventoPorIdConParticipaciones(eventoId);
        if (eventoOpt.isEmpty()) {
            throw new IllegalArgumentException("Evento no encontrado con ID: " + eventoId);
        }
        
        Evento evento = eventoOpt.get();
        if (!evento.estaEnPlanificacion()) {
            throw new IllegalStateException("Solo se pueden confirmar eventos en estado PLANIFICACION. Estado actual: " + evento.getEstado());
        }
        
        // El método cambiarEstado del evento ya valida que tenga los responsables obligatorios
        evento.cambiarEstado(EstadoEvento.CONFIRMADO);
        
        // Guardar y devolver el evento recargado con participaciones
        eventoRepository.save(evento);
        
        // Recargar para asegurar que todas las relaciones estén disponibles
        return buscarEventoPorIdConParticipaciones(eventoId).orElseThrow(
            () -> new IllegalStateException("Error al recargar el evento después de confirmarlo")
        );
    }
    
    /**
     * Inicia un evento cambiando su estado de CONFIRMADO a EN_EJECUCION.
     * Solo permite iniciar el evento si está confirmado y la fecha actual es la fecha de inicio.
     * 
     * @param eventoId el ID del evento a iniciar
     * @return el evento iniciado con todas las participaciones cargadas
     * @throws IllegalArgumentException si el evento no existe
     * @throws IllegalStateException si el evento no puede iniciarse hoy
     */
    public Evento iniciarEvento(Long eventoId) {
        Optional<Evento> eventoOpt = buscarEventoPorIdConParticipaciones(eventoId);
        if (eventoOpt.isEmpty()) {
            throw new IllegalArgumentException("Evento no encontrado con ID: " + eventoId);
        }
        
        Evento evento = eventoOpt.get();
        if (!evento.puedeIniciarseHoy()) {
            throw new IllegalStateException("El evento no puede iniciarse. Debe estar CONFIRMADO y ser la fecha de inicio del evento.");
        }
        
        evento.cambiarEstado(EstadoEvento.EN_EJECUCION);
        
        // Guardar y devolver el evento recargado con participaciones
        eventoRepository.save(evento);
        
        return buscarEventoPorIdConParticipaciones(eventoId).orElseThrow(
            () -> new IllegalStateException("Error al recargar el evento después de iniciarlo")
        );
    }
    
    /**
     * Finaliza un evento cambiando su estado de EN_EJECUCION a FINALIZADO.
     * 
     * @param eventoId el ID del evento a finalizar
     * @return el evento finalizado con todas las participaciones cargadas
     * @throws IllegalArgumentException si el evento no existe
     * @throws IllegalStateException si el evento no está EN_EJECUCION
     */
    public Evento finalizarEvento(Long eventoId) {
        Optional<Evento> eventoOpt = buscarEventoPorIdConParticipaciones(eventoId);
        if (eventoOpt.isEmpty()) {
            throw new IllegalArgumentException("Evento no encontrado con ID: " + eventoId);
        }
        
        Evento evento = eventoOpt.get();
        if (!evento.puedeFinalizarse()) {
            throw new IllegalStateException("Solo se pueden finalizar eventos que estén EN_EJECUCION. Estado actual: " + evento.getEstado());
        }
        
        evento.cambiarEstado(EstadoEvento.FINALIZADO);
        
        // Guardar y devolver el evento recargado con participaciones
        eventoRepository.save(evento);
        
        return buscarEventoPorIdConParticipaciones(eventoId).orElseThrow(
            () -> new IllegalStateException("Error al recargar el evento después de finalizarlo")
        );
    }
    
    /**
     * Actualiza las participaciones de un evento (inscripciones/bajas de asistentes y responsables).
     * No valida el estado del evento ya que las reglas de negocio están en el dominio.
     * 
     * @param evento el evento con las participaciones modificadas
     * @return el evento actualizado con todas las participaciones cargadas
     * @throws IllegalArgumentException si el evento no existe
     */
    public Evento actualizarParticipaciones(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo");
        }
        
        // Guardar el evento (las validaciones de negocio están en el dominio)
        eventoRepository.save(evento);
        
        // Recargar para asegurar que todas las relaciones estén disponibles
        return buscarEventoPorIdConParticipaciones(evento.getId()).orElseThrow(
            () -> new IllegalStateException("Error al recargar el evento tras actualizar participaciones")
        );
    }
    
    /**
     * Agrega una película a un ciclo de cine.
     * 
     * @param eventoId el ID del evento (debe ser un CicloDeCine)
     * @param pelicula la película a agregar
     * @return el evento actualizado
     * @throws IllegalArgumentException si el evento no es un CicloDeCine o la película es inválida
     * @throws IllegalStateException si el evento no está en PLANIFICACION
     */
    public Evento agregarPeliculaACiclo(Long eventoId, Pelicula pelicula) {
        Optional<Evento> eventoOpt = buscarEventoPorIdConParticipaciones(eventoId);
        if (eventoOpt.isEmpty()) {
            throw new IllegalArgumentException("Evento no encontrado con ID: " + eventoId);
        }
        
        Evento evento = eventoOpt.get();
        if (!(evento instanceof CicloDeCine)) {
            throw new IllegalArgumentException("El evento no es un Ciclo de Cine");
        }
        
        CicloDeCine ciclo = (CicloDeCine) evento;
        ciclo.agregarPelicula(pelicula);
        
        return eventoRepository.save(ciclo);
    }
    
    /**
     * Quita una película de un ciclo de cine.
     * 
     * @param eventoId el ID del evento (debe ser un CicloDeCine)
     * @param pelicula la película a quitar
     * @return el evento actualizado
     * @throws IllegalArgumentException si el evento no es un CicloDeCine o la película no está en el ciclo
     * @throws IllegalStateException si el evento no está en PLANIFICACION
     */
    public Evento quitarPeliculaDeCiclo(Long eventoId, Pelicula pelicula) {
        Optional<Evento> eventoOpt = buscarEventoPorIdConParticipaciones(eventoId);
        if (eventoOpt.isEmpty()) {
            throw new IllegalArgumentException("Evento no encontrado con ID: " + eventoId);
        }
        
        Evento evento = eventoOpt.get();
        if (!(evento instanceof CicloDeCine)) {
            throw new IllegalArgumentException("El evento no es un Ciclo de Cine");
        }
        
        CicloDeCine ciclo = (CicloDeCine) evento;
        ciclo.quitarPelicula(pelicula);
        
        return eventoRepository.save(ciclo);
    }
    
    /**
     * Agrega un artista/banda a un concierto.
     * 
     * @param eventoId el ID del evento (debe ser un Concierto)
     * @param artistaBanda el artista/banda a agregar
     * @return el evento actualizado
     * @throws IllegalArgumentException si el evento no es un Concierto o el artista/banda es inválido
     * @throws IllegalStateException si el evento no está en PLANIFICACION
     */
    public Evento agregarArtistaBandaAConcierto(Long eventoId, ArtistaBanda artistaBanda) {
        Optional<Evento> eventoOpt = buscarEventoPorIdConParticipaciones(eventoId);
        if (eventoOpt.isEmpty()) {
            throw new IllegalArgumentException("Evento no encontrado con ID: " + eventoId);
        }
        
        Evento evento = eventoOpt.get();
        if (!(evento instanceof Concierto)) {
            throw new IllegalArgumentException("El evento no es un Concierto");
        }
        
        Concierto concierto = (Concierto) evento;
        concierto.agregarArtistaBanda(artistaBanda);
        
        return eventoRepository.save(concierto);
    }
    
    /**
     * Quita un artista/banda de un concierto.
     * 
     * @param eventoId el ID del evento (debe ser un Concierto)
     * @param artistaBanda el artista/banda a quitar
     * @return el evento actualizado
     * @throws IllegalArgumentException si el evento no es un Concierto o el artista/banda no está en el concierto
     * @throws IllegalStateException si el evento no está en PLANIFICACION
     */
    public Evento quitarArtistaBandaDeConcierto(Long eventoId, ArtistaBanda artistaBanda) {
        Optional<Evento> eventoOpt = buscarEventoPorIdConParticipaciones(eventoId);
        if (eventoOpt.isEmpty()) {
            throw new IllegalArgumentException("Evento no encontrado con ID: " + eventoId);
        }
        
        Evento evento = eventoOpt.get();
        if (!(evento instanceof Concierto)) {
            throw new IllegalArgumentException("El evento no es un Concierto");
        }
        
        Concierto concierto = (Concierto) evento;
        concierto.quitarArtistaBanda(artistaBanda);
        
        return eventoRepository.save(concierto);
    }
}
