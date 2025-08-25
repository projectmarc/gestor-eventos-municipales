package gov.municipio.domain.model.evento;


import gov.municipio.domain.model.*;
import gov.municipio.domain.model.enums.EstadoEvento;
import gov.municipio.domain.model.enums.Rol;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "eventos")
public abstract class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private int duracionEstimada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEvento estado;

    @Column(nullable = false)
    private boolean requiereInscripcion;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Participacion> participaciones = new ArrayList<>();

    public Evento(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion) {
        this();
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.duracionEstimada = duracionEstimadaHoras;
        this.estado = EstadoEvento.PLANIFICACION;
        this.requiereInscripcion = requiereInscripcion;
    }

    public Evento() {
        this.estado = EstadoEvento.PLANIFICACION;
    }

        public Participacion inscribirPersona(Persona persona, Rol rol) {
        if(rol == null){
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        if (!validarRol(rol)){
            throw new IllegalArgumentException("El rol " + rol + " no es válido para este tipo de evento");
        }
        
        if(estado == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede inscribir a un evento finalizado");
        }

        // Solo verificar estado confirmado para asistentes en eventos que requieren inscripción
        if(rol == Rol.ASISTENTE && requiereInscripcion && estado != EstadoEvento.CONFIRMADO){
            throw new IllegalArgumentException("El evento debe estar confirmado para inscribir asistentes");
        }

        // Validar cupo máximo solo para asistentes
        if (rol == Rol.ASISTENTE && !validarCupo()) {
            throw new IllegalArgumentException("No hay cupo disponible para este evento");
        }

        Participacion participacion = new Participacion(persona, this, rol);
        participaciones.add(participacion);
        return participacion;
    }

    public boolean validarRol(Rol rol) {
        return true;
    }

    // Método para validar cupo - por defecto no hay límite
    public boolean validarCupo() {
        return true;
    }

    public List<Participacion> getParticipaciones() {
        return participaciones;
    }

    public void cambiarEstado(EstadoEvento nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        }
        this.estado = nuevoEstado;
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

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

}
