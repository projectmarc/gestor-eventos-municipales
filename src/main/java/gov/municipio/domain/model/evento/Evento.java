package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.*;
import gov.municipio.domain.model.enums.EstadoEvento;
import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.exceptions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Clase abstracta que representa un evento cultural del municipio.
 * Implementa un modelo de dominio rico con reglas de negocio y validaciones.
 */
public abstract class Evento {
    private String nombre;
    private LocalDate fechaInicio;
    private int duracionEstimada;
    private EstadoEvento estado;
    private boolean requiereInscripcion;
    private List<Participacion> participaciones = new ArrayList<>();

    /**
     * Constructor principal que valida los datos del evento.
     * 
     * @param nombre nombre del evento (obligatorio, no vacío)
     * @param fechaInicio fecha de inicio (obligatoria, no puede ser en el pasado)
     * @param duracionEstimadaHoras duración estimada en horas (debe ser positiva)
     * @param requiereInscripcion si el evento requiere inscripción previa
     */
    public Evento(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion) {
        this.nombre = validarNombre(nombre);
        this.fechaInicio = validarFechaInicio(fechaInicio);
        this.duracionEstimada = validarDuracion(duracionEstimadaHoras);
        this.estado = EstadoEvento.PLANIFICACION;
        this.requiereInscripcion = requiereInscripcion;
    }

    /**
     * Constructor protegido para JPA.
     */
    protected Evento() {
        this.estado = EstadoEvento.PLANIFICACION;
    }

    /**
     * Inscribe una persona en el evento con el rol especificado.
     * Valida todas las reglas de negocio antes de permitir la inscripción.
     * 
     * @param persona la persona a inscribir
     * @param rol el rol que tendrá la persona en el evento
     * @return la participación creada
     * @throws InscripcionNoPermitidaException si el evento no permite inscripciones
     * @throws RolInvalidoParaEventoException si el rol no es válido para este tipo de evento
     * @throws PersonaYaInscritaException si la persona ya está inscrita
     * @throws CupoMaximoSuperadoException si se supera el cupo máximo
     */
    public Participacion inscribirPersona(Persona persona, Rol rol) {
        validarParametrosInscripcion(persona, rol);
        validarEstadoParaInscripcion();
        validarRolParaEvento(rol);
        validarPersonaNoInscrita(persona);
        validarCupoDisponible();

        Participacion participacion = new Participacion(persona, this, rol);
        participaciones.add(participacion);
        return participacion;
    }

    /**
     * Valida si un rol es válido para este tipo de evento.
     * Las subclases deben sobrescribir este método para definir sus roles específicos.
     * 
     * @param rol el rol a validar
     * @return true si el rol es válido para este tipo de evento
     */
    public abstract boolean validarRol(Rol rol);

    /**
     * Obtiene el cupo máximo del evento.
     * Por defecto no hay cupo máximo (devuelve Integer.MAX_VALUE).
     * Las subclases pueden sobrescribir para definir cupos específicos.
     * 
     * @return el cupo máximo del evento
     */
    protected int getCupoMaximo() {
        return Integer.MAX_VALUE;
    }

    /**
     * Verifica si hay cupo disponible en el evento.
     * 
     * @return true si hay cupo disponible
     */
    public boolean hayCupoDisponible() {
        if (!requiereInscripcion) {
            return true;
        }
        return getParticipacionesAsistentes().size() < getCupoMaximo();
    }

    /**
     * Obtiene el número de lugares disponibles en el evento.
     * 
     * @return número de lugares disponibles, o -1 si no hay límite
     */
    public int getLugaresDisponibles() {
        if (!requiereInscripcion) {
            return -1; // Sin límite
        }
        int cupoMaximo = getCupoMaximo();
        if (cupoMaximo == Integer.MAX_VALUE) {
            return -1; // Sin límite
        }
        return Math.max(0, cupoMaximo - getParticipacionesAsistentes().size());
    }

    /**
     * Cambia el estado del evento validando las transiciones permitidas.
     * 
     * @param nuevoEstado el nuevo estado del evento
     * @throws EstadoEventoInvalidoException si la transición no es válida
     */
    public void cambiarEstado(EstadoEvento nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        }
        
        if (!estaTransicionPermitida(this.estado, nuevoEstado)) {
            throw new EstadoEventoInvalidoException(
                String.format("No se puede cambiar del estado %s al estado %s", 
                    this.estado, nuevoEstado));
        }
        
        this.estado = nuevoEstado;
    }

    /**
     * Verifica si el evento está activo (confirmado o en ejecución).
     * 
     * @return true si el evento está activo
     */
    public boolean estaActivo() {
        return estado == EstadoEvento.CONFIRMADO || estado == EstadoEvento.EN_EJECUCION;
    }

    /**
     * Verifica si el evento está cerrado (finalizado).
     * 
     * @return true si el evento está cerrado
     */
    public boolean estaCerrado() {
        return estado == EstadoEvento.FINALIZADO;
    }

    /**
     * Verifica si el evento está en planificación.
     * 
     * @return true si el evento está en planificación
     */
    public boolean estaEnPlanificacion() {
        return estado == EstadoEvento.PLANIFICACION;
    }

    /**
     * Obtiene todas las participaciones de asistentes (excluyendo organizadores).
     * 
     * @return lista de participaciones de asistentes
     */
    public List<Participacion> getParticipacionesAsistentes() {
        return participaciones.stream()
            .filter(p -> p.getRol() == Rol.ASISTENTE)
            .toList();
    }

    /**
     * Obtiene todas las personas que tienen un rol específico en el evento.
     * 
     * @param rol el rol a buscar
     * @return lista de personas con el rol especificado
     * @throws IllegalArgumentException si el rol es nulo
     */
    public List<Persona> getPersonasPorRol(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        return participaciones.stream()
            .filter(p -> p.getRol() == rol)
            .map(Participacion::getPersona)
            .toList();
    }

    /**
     * Verifica si una persona está inscrita en el evento (con cualquier rol).
     * 
     * @param persona la persona a verificar
     * @return true si la persona está inscrita
     */
    public boolean estaPersonaInscrita(Persona persona) {
        if (persona == null) {
            return false;
        }
        return participaciones.stream()
            .anyMatch(p -> p.getPersona().equals(persona));
    }

    /**
     * Busca la participación de una persona específica en el evento.
     * 
     * @param persona la persona a buscar
     * @return Optional con la participación si existe
     */
    public Optional<Participacion> buscarParticipacion(Persona persona) {
        if (persona == null) {
            return Optional.empty();
        }
        return participaciones.stream()
            .filter(p -> p.getPersona().equals(persona))
            .findFirst();
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public int getDuracionEstimada() {
        return duracionEstimada;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public boolean isRequiereInscripcion() {
        return requiereInscripcion;
    }

    public List<Participacion> getParticipaciones() {
        return new ArrayList<>(participaciones); // Copia defensiva
    }

    // Setters con validación
    public void setNombre(String nombre) {
        this.nombre = validarNombre(nombre);
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = validarFechaInicio(fechaInicio);
    }

    // ========== MÉTODOS DE VALIDACIÓN PRIVADOS ==========

    /**
     * Valida el nombre del evento.
     */
    private String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del evento no puede ser nulo ni vacío");
        }
        if (nombre.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre del evento no puede exceder 100 caracteres");
        }
        return nombre.trim();
    }

    /**
     * Valida la fecha de inicio del evento.
     */
    private LocalDate validarFechaInicio(LocalDate fechaInicio) {
        if (fechaInicio == null) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula");
        }
        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser en el pasado");
        }
        return fechaInicio;
    }

    /**
     * Valida la duración del evento.
     */
    private int validarDuracion(int duracionEstimadaHoras) {
        if (duracionEstimadaHoras <= 0) {
            throw new IllegalArgumentException("La duración estimada debe ser mayor a 0 horas");
        }
        if (duracionEstimadaHoras > 240) { // Máximo 10 días
            throw new IllegalArgumentException("La duración estimada no puede exceder 240 horas");
        }
        return duracionEstimadaHoras;
    }

    /**
     * Hook para que las subclases validen datos específicos.
     * Por defecto no hace nada.
     */
    protected void validarDatosEspecificos() {
        // Las subclases pueden sobrescribir este método
    }

    /**
     * Valida los parámetros básicos para inscripción.
     */
    private void validarParametrosInscripcion(Persona persona, Rol rol) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
    }

    /**
     * Valida que el evento esté en estado válido para inscripciones.
     */
    private void validarEstadoParaInscripcion() {
        if (!requiereInscripcion) {
            throw new InscripcionNoPermitidaException("Este evento no requiere inscripción");
        }
        if (estado != EstadoEvento.CONFIRMADO) {
            throw new InscripcionNoPermitidaException(
                "Solo se pueden inscribir personas en eventos confirmados. Estado actual: " + estado);
        }
    }

    /**
     * Valida que el rol sea válido para este tipo de evento.
     */
    private void validarRolParaEvento(Rol rol) {
        if (!validarRol(rol)) {
            throw new RolInvalidoParaEventoException(
                String.format("El rol %s no es válido para este tipo de evento (%s)", 
                    rol, this.getClass().getSimpleName()));
        }
    }

    /**
     * Valida que la persona no esté ya inscrita.
     */
    private void validarPersonaNoInscrita(Persona persona) {
        if (estaPersonaInscrita(persona)) {
            throw new PersonaYaInscritaException(
                String.format("La persona %s ya está inscrita en el evento %s", 
                    persona.getNombreCompleto(), this.nombre));
        }
    }

    /**
     * Valida que haya cupo disponible.
     */
    private void validarCupoDisponible() {
        if (!hayCupoDisponible()) {
            throw new CupoMaximoSuperadoException(
                String.format("No hay cupo disponible en el evento %s (cupo máximo: %d)", 
                    this.nombre, getCupoMaximo()));
        }
    }

    /**
     * Verifica si una transición de estado es permitida.
     */
    private boolean estaTransicionPermitida(EstadoEvento estadoActual, EstadoEvento nuevoEstado) {
        if (estadoActual == nuevoEstado) {
            return true; // Mismo estado siempre permitido
        }
        
        return switch (estadoActual) {
            case PLANIFICACION -> nuevoEstado == EstadoEvento.CONFIRMADO;
            case CONFIRMADO -> nuevoEstado == EstadoEvento.EN_EJECUCION || 
                              nuevoEstado == EstadoEvento.PLANIFICACION;
            case EN_EJECUCION -> nuevoEstado == EstadoEvento.FINALIZADO;
            case FINALIZADO -> false; // Los eventos finalizados no pueden cambiar de estado
        };
    }

}
