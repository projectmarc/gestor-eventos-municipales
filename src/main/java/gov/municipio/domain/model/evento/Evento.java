package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.*;
import gov.municipio.domain.model.enums.EstadoEvento;
import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.exceptions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Clase abstracta que representa un evento cultural del municipio.
 * Modelo de dominio rico con reglas centralizadas.
 */
public abstract class Evento {
    private String nombre;
    private LocalDate fechaInicio;
    private int duracionEstimada;
    private EstadoEvento estado;
    private boolean requiereInscripcion;
    private Integer cupoMaximo; // Obligatorio si requiereInscripcion = true
    private final List<Participacion> participaciones = new ArrayList<>();

    public Evento(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, Integer cupoMaximo) {
        this.nombre = validarNombre(nombre);
        this.fechaInicio = validarFechaInicio(fechaInicio);
        this.duracionEstimada = validarDuracion(duracionEstimadaHoras);
        this.estado = EstadoEvento.PLANIFICACION;
        this.requiereInscripcion = requiereInscripcion;
        this.cupoMaximo = validarCupoMaximo(cupoMaximo, requiereInscripcion);
    }

    protected Evento() {
        this.estado = EstadoEvento.PLANIFICACION;
    }

    // -------- API de participaciones

    /** Agrega un responsable (organizador/curador/instructor) solo en PLANIFICACIÓN. */
    public Participacion agregarResponsable(Persona persona, Rol rolResponsable) {
        if (persona == null || rolResponsable == null)
            throw new IllegalArgumentException("Persona y rol son obligatorios");
        if (!esRolResponsable(rolResponsable))
            throw new IllegalArgumentException("Rol no es de responsable: " + rolResponsable);
        if (!estaEnPlanificacion())
            throw new IllegalStateException("Solo se pueden agregar responsables en PLANIFICACIÓN");
        validarRolParaEvento(rolResponsable);
        validarPersonaNoInscrita(persona);

        Participacion p = new Participacion(persona, this, rolResponsable);
        participaciones.add(p);
        return p;
    }

    /** Inscribe a un asistente solo si requiere inscripción y el estado es CONFIRMADO o EN_EJECUCIÓN. */
    public Participacion inscribirAsistente(Persona persona) {
        if (persona == null)
            throw new IllegalArgumentException("Persona obligatoria");
        validarEstadoParaInscripcion_Ajustado(); // requiereInscripcion && estado ∈ {CONFIRMADO, EN_EJECUCION}
        validarRolParaEvento(Rol.ASISTENTE);     // por si la subclase no admite asistentes
        validarPersonaNoInscrita(persona);
        validarCupoDisponible();                 // cupo cuenta solo asistentes

        Participacion p = new Participacion(persona, this, Rol.ASISTENTE);
        participaciones.add(p);
        return p;
    }

    /**
     * Remueve participación:
     * - Responsables: solo en PLANIFICACIÓN.
     * - Asistentes: permitido hasta EN_EJECUCIÓN (no en FINALIZADO).
     */
    public boolean removerParticipacion(Persona persona) {
        Optional<Participacion> opt = buscarParticipacion(persona);
        if (opt.isEmpty()) return false;
        Participacion p = opt.get();

        if (p.esRolDeLiderazgo()) {
            if (!estaEnPlanificacion())
                throw new IllegalStateException("Solo se pueden quitar responsables en PLANIFICACIÓN");
        } else if (p.esAsistente()) {
            if (estado == EstadoEvento.FINALIZADO)
                throw new IllegalStateException("No se pueden quitar asistentes en FINALIZADO");
            // permitido en CONFIRMADO y EN_EJECUCIÓN
        }
        return participaciones.remove(p);
    }

    /** Método genérico (si aún lo usás en tests); mantiene las validaciones de negocio. */
    public Participacion inscribirPersona(Persona persona, Rol rol) {
        if (rol == Rol.ASISTENTE) {
            return inscribirAsistente(persona);
        }
        return agregarResponsable(persona, rol);
    }

    // -------- Reglas polimórficas

    /** Cada tipo de evento define qué roles son válidos. */
    public abstract boolean validarRol(Rol rol);

    /** Cada tipo de evento define qué roles de responsables son obligatorios para confirmar. */
    protected abstract List<Rol> rolesResponsablesObligatorios();

    // -------- Cupo

    protected int getCupoMaximo() { 
        return cupoMaximo != null ? cupoMaximo : Integer.MAX_VALUE; 
    }

    public boolean hayCupoDisponible() {
        if (!requiereInscripcion) return true;
        return getParticipacionesAsistentes().size() < getCupoMaximo();
    }

    public int getLugaresDisponibles() {
        if (!requiereInscripcion) return -1; // sin límite
        int max = getCupoMaximo();
        if (max == Integer.MAX_VALUE) return -1;
        return Math.max(0, max - getParticipacionesAsistentes().size());
    }

    // -------- Estados

    public void cambiarEstado(EstadoEvento nuevoEstado) {
        if (nuevoEstado == null) throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        if (!estaTransicionPermitida(this.estado, nuevoEstado)) {
            throw new EstadoEventoInvalidoException(
                "No se puede cambiar del estado " + this.estado + " al estado " + nuevoEstado);
        }
        // Si vamos a CONFIRMADO, exigir responsables obligatorios
        if (nuevoEstado == EstadoEvento.CONFIRMADO) {
            validarResponsablesObligatoriosAntesDeConfirmar();
        }
        this.estado = nuevoEstado;
    }

    private void validarResponsablesObligatoriosAntesDeConfirmar() {
        for (Rol rol : rolesResponsablesObligatorios()) {
            if (getPersonasPorRol(rol).isEmpty()) {
                throw new IllegalStateException("Falta al menos un " + rol + " para confirmar el evento");
            }
        }
    }

    public boolean estaActivo() {
        return estado == EstadoEvento.CONFIRMADO || estado == EstadoEvento.EN_EJECUCION;
    }

    public boolean estaCerrado() {
        return estado == EstadoEvento.FINALIZADO;
    }

    public boolean estaEnPlanificacion() {
        return estado == EstadoEvento.PLANIFICACION;
    }

    private boolean estaTransicionPermitida(EstadoEvento actual, EstadoEvento nuevo) {
        if (actual == nuevo) return true;
        return switch (actual) {
            case PLANIFICACION -> nuevo == EstadoEvento.CONFIRMADO;
            case CONFIRMADO    -> nuevo == EstadoEvento.EN_EJECUCION || nuevo == EstadoEvento.PLANIFICACION;
            case EN_EJECUCION  -> nuevo == EstadoEvento.FINALIZADO;
            case FINALIZADO    -> false;
        };
    }

    // -------- Consultas auxiliares

    public List<Participacion> getParticipacionesAsistentes() {
        return participaciones.stream().filter(p -> p.getRol() == Rol.ASISTENTE).toList();
    }

    public List<Persona> getPersonasPorRol(Rol rol) {
        if (rol == null) throw new IllegalArgumentException("El rol no puede ser nulo");
        return participaciones.stream()
                .filter(p -> p.getRol() == rol)
                .map(Participacion::getPersona)
                .toList();
    }

    public boolean estaPersonaInscrita(Persona persona) {
        if (persona == null) return false;
        return participaciones.stream().anyMatch(p -> p.getPersona().equals(persona));
    }

    public Optional<Participacion> buscarParticipacion(Persona persona) {
        if (persona == null) return Optional.empty();
        return participaciones.stream().filter(p -> p.getPersona().equals(persona)).findFirst();
    }

    // -------- Getters/Setters básicos

    public String getNombre() { return nombre; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public int getDuracionEstimada() { return duracionEstimada; }
    public EstadoEvento getEstado() { return estado; }
    public boolean isRequiereInscripcion() { return requiereInscripcion; }
    public Integer getCupoMaximoDefinido() { return cupoMaximo; }
    public List<Participacion> getParticipaciones() { return new ArrayList<>(participaciones); } // copia defensiva

    public void setNombre(String nombre) { this.nombre = validarNombre(nombre); }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = validarFechaInicio(fechaInicio); }

    // -------- Validaciones básicas

    private String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre del evento no puede ser nulo ni vacío");
        if (nombre.trim().length() > 100)
            throw new IllegalArgumentException("El nombre del evento no puede exceder 100 caracteres");
        return nombre.trim();
    }

    private LocalDate validarFechaInicio(LocalDate fechaInicio) {
        if (fechaInicio == null) throw new IllegalArgumentException("La fecha de inicio no puede ser nula");
        if (fechaInicio.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de inicio no puede ser en el pasado");
        return fechaInicio;
    }

    private int validarDuracion(int horas) {
        if (horas <= 0) throw new IllegalArgumentException("La duración estimada debe ser mayor a 0 horas");
        if (horas > 240) throw new IllegalArgumentException("La duración estimada no puede exceder 240 horas");
        return horas;
    }

    private Integer validarCupoMaximo(Integer cupoMaximo, boolean requiereInscripcion) {
        if (requiereInscripcion) {
            if (cupoMaximo == null) {
                throw new IllegalArgumentException("El cupo máximo es obligatorio para eventos que requieren inscripción");
            }
            if (cupoMaximo <= 0) {
                throw new IllegalArgumentException("El cupo máximo debe ser mayor a 0");
            }
            if (cupoMaximo > 1000000) {
                throw new IllegalArgumentException("El cupo máximo no puede exceder 1,000,000 participantes");
            }
        } else {
            // Si no requiere inscripción, el cupo debe ser null
            if (cupoMaximo != null) {
                throw new IllegalArgumentException("No se debe especificar cupo máximo para eventos que no requieren inscripción");
            }
        }
        return cupoMaximo;
    }

    private void validarEstadoParaInscripcion_Ajustado() {
        if (!requiereInscripcion)
            throw new InscripcionNoPermitidaException("Este evento no requiere inscripción");
        if (estado != EstadoEvento.CONFIRMADO && estado != EstadoEvento.EN_EJECUCION) {
            throw new InscripcionNoPermitidaException(
                "Solo se pueden inscribir asistentes en CONFIRMADO o EN_EJECUCIÓN. Estado actual: " + estado);
        }
    }

    private void validarRolParaEvento(Rol rol) {
        if (!validarRol(rol)) {
            throw new RolInvalidoParaEventoException(
                "El rol " + rol + " no es válido para este tipo de evento (" + this.getClass().getSimpleName() + ")");
        }
    }

    private void validarPersonaNoInscrita(Persona persona) {
        if (estaPersonaInscrita(persona)) {
            throw new PersonaYaInscritaException(
                "La persona " + persona.getNombreCompleto() + " ya participa en el evento " + this.nombre);
        }
    }

    private void validarCupoDisponible() {
        if (!hayCupoDisponible()) {
            throw new CupoMaximoSuperadoException(
                "No hay cupo disponible en el evento " + this.nombre + " (cupo máximo: " + getCupoMaximo() + ")");
        }
    }

    private static boolean esRolResponsable(Rol r) {
        return r == Rol.ORGANIZADOR || r == Rol.INSTRUCTOR || r == Rol.CURADOR;
    }
}
