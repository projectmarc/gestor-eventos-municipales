package gov.municipio.domain.model.evento;


import gov.municipio.domain.model.*;
import gov.municipio.domain.model.enums.EstadoEvento;
import gov.municipio.domain.model.enums.Rol;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public abstract class Evento {
    private String nombre;
    private LocalDate fechaInicio;
    private int duracionEstimada;
    private EstadoEvento estado;
    private final boolean requiereInscripcion;
    private final List<Participacion> participaciones = new ArrayList<>();
    private final LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaModificacion;

    public Evento(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion) {
        this.nombre = validarNombre(nombre);
        this.fechaInicio = validarFechaInicio(fechaInicio);
        this.duracionEstimada = validarDuracion(duracionEstimadaHoras);
        this.estado = EstadoEvento.PLANIFICACION;
        this.requiereInscripcion = requiereInscripcion;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaUltimaModificacion = LocalDateTime.now();
    }

    // Constructor protegido para subclases que necesiten configuración especial
    protected Evento() {
        this.estado = EstadoEvento.PLANIFICACION;
        this.requiereInscripcion = false;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaUltimaModificacion = LocalDateTime.now();
    }

    // Métodos de comportamiento del dominio

    public Participacion inscribirPersona(Persona persona, Rol rol) {
        validarInscripcion(persona, rol);
        
        // Verificar si la persona ya está inscrita con el mismo rol
        if (estaInscrita(persona, rol)) {
            throw new IllegalArgumentException("La persona ya está inscrita en este evento con el rol " + rol);
        }
        
        // Validar cupo si es necesario
        validarCupoDisponible();

        Participacion participacion = new Participacion(persona, this, rol);
        participaciones.add(participacion);
        actualizarFechaModificacion();
        return participacion;
    }

    public void desinscribirPersona(Persona persona) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
        
        if (estado == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede desinscribir personas de un evento finalizado");
        }

        boolean removido = participaciones.removeIf(p -> p.getPersona().equals(persona));
        if (!removido) {
            throw new IllegalArgumentException("La persona no está inscrita en este evento");
        }
        
        actualizarFechaModificacion();
    }

    public void cambiarEstado(EstadoEvento nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        }
        
        validarTransicionEstado(nuevoEstado);
        this.estado = nuevoEstado;
        actualizarFechaModificacion();
    }

    public void cambiarNombre(String nuevoNombre) {
        if (estado == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar el nombre de un evento finalizado");
        }
        
        this.nombre = validarNombre(nuevoNombre);
        actualizarFechaModificacion();
    }

    public void reprogramar(LocalDate nuevaFecha) {
        if (estado == EstadoEvento.EN_EJECUCION || estado == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede reprogramar un evento en ejecución o finalizado");
        }
        
        this.fechaInicio = validarFechaInicio(nuevaFecha);
        actualizarFechaModificacion();
    }

    public void cambiarDuracion(int nuevaDuracion) {
        if (estado == EstadoEvento.EN_EJECUCION || estado == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la duración de un evento en ejecución o finalizado");
        }
        
        this.duracionEstimada = validarDuracion(nuevaDuracion);
        actualizarFechaModificacion();
    }

    // Métodos de consulta

    public boolean estaInscrita(Persona persona, Rol rol) {
        if (persona == null) return false;
        return participaciones.stream()
                .anyMatch(p -> p.getPersona().equals(persona) && p.getRol() == rol);
    }

    public boolean estaInscrita(Persona persona) {
        if (persona == null) return false;
        return participaciones.stream()
                .anyMatch(p -> p.getPersona().equals(persona));
    }

    public int getCantidadParticipantes() {
        return participaciones.size();
    }

    public int getCantidadParticipantesPorRol(Rol rol) {
        if (rol == null) return 0;
        return (int) participaciones.stream()
                .filter(p -> p.getRol() == rol)
                .count();
    }

    public List<Persona> getPersonasPorRol(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        return participaciones.stream()
                .filter(p -> p.getRol() == rol)
                .map(Participacion::getPersona)
                .toList();
    }

    public List<Participacion> getParticipaciones() {
        return Collections.unmodifiableList(participaciones);
    }

    public boolean puedeInscribirse() {
        return estado == EstadoEvento.CONFIRMADO && requiereInscripcion;
    }

    public boolean estaVigente() {
        return fechaInicio.isAfter(LocalDate.now()) || fechaInicio.isEqual(LocalDate.now());
    }

    public LocalDate getFechaFin() {
        return fechaInicio.plusDays(duracionEstimada / 24);
    }

    // Métodos abstractos que deben implementar las subclases
    public abstract boolean validarRol(Rol rol);
    
    // Método que las subclases pueden sobrescribir para validar cupo
    protected void validarCupoDisponible() {
        // Implementación por defecto: no hay límite de cupo
    }

    // Métodos de validación privados
    private String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del evento no puede ser nulo ni vacío");
        }
        if (nombre.trim().length() < 3) {
            throw new IllegalArgumentException("El nombre del evento debe tener al menos 3 caracteres");
        }
        if (nombre.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre del evento no puede superar los 100 caracteres");
        }
        return nombre.trim();
    }

    private LocalDate validarFechaInicio(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a hoy");
        }
        return fecha;
    }

    private int validarDuracion(int duracion) {
        if (duracion <= 0) {
            throw new IllegalArgumentException("La duración debe ser positiva");
        }
        if (duracion > 24 * 30) { // Máximo 30 días
            throw new IllegalArgumentException("La duración no puede superar los 30 días (720 horas)");
        }
        return duracion;
    }

    private void validarInscripcion(Persona persona, Rol rol) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        if (!validarRol(rol)) {
            throw new IllegalArgumentException("El rol " + rol + " no es válido para este tipo de evento");
        }
        if (requiereInscripcion && estado != EstadoEvento.CONFIRMADO) {
            throw new IllegalArgumentException("El evento debe estar confirmado para inscribir personas");
        }
        if (estado == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se pueden inscribir personas en un evento finalizado");
        }
    }

    private void validarTransicionEstado(EstadoEvento nuevoEstado) {
        switch (estado) {
            case PLANIFICACION:
                if (nuevoEstado != EstadoEvento.CONFIRMADO) {
                    throw new IllegalArgumentException("Desde PLANIFICACION solo se puede pasar a CONFIRMADO");
                }
                break;
            case CONFIRMADO:
                if (nuevoEstado != EstadoEvento.EN_EJECUCION && nuevoEstado != EstadoEvento.PLANIFICACION) {
                    throw new IllegalArgumentException("Desde CONFIRMADO solo se puede pasar a EN_EJECUCION o volver a PLANIFICACION");
                }
                break;
            case EN_EJECUCION:
                if (nuevoEstado != EstadoEvento.FINALIZADO) {
                    throw new IllegalArgumentException("Desde EN_EJECUCION solo se puede pasar a FINALIZADO");
                }
                break;
            case FINALIZADO:
                throw new IllegalArgumentException("Un evento finalizado no puede cambiar de estado");
        }
    }

    private void actualizarFechaModificacion() {
        this.fechaUltimaModificacion = LocalDateTime.now();
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaUltimaModificacion() {
        return fechaUltimaModificacion;
    }

    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Evento evento = (Evento) obj;
        return nombre.equals(evento.nombre) && 
               fechaInicio.equals(evento.fechaInicio);
    }

    @Override
    public int hashCode() {
        return nombre.hashCode() * 31 + fechaInicio.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s{nombre='%s', fechaInicio=%s, estado=%s, participantes=%d}",
                getClass().getSimpleName(), nombre, fechaInicio, estado, participaciones.size());
    }
}
