package gov.municipio.domain.model;

import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.evento.Evento;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participaciones", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"persona_id", "evento_id", "rol"}))
public class Participacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion;

    @Column(name = "activa", nullable = false)
    private boolean activa = true;

    public Participacion() {
        this.fechaInscripcion = LocalDateTime.now();
    }

    public Participacion(Persona persona, Evento evento, Rol rol) {
        this();
        validarParametrosConstructor(persona, evento, rol);
        this.persona = persona;
        this.evento = evento;
        this.rol = rol;
    }

    // Validación en constructor (Modelo Rico)
    private void validarParametrosConstructor(Persona persona, Evento evento, Rol rol) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo");
        }
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
    }

    // Métodos de comportamiento (Modelo Rico)
    public boolean esOrganizador() {
        return rol == Rol.ORGANIZADOR;
    }

    public boolean esAsistente() {
        return rol == Rol.ASISTENTE;
    }

    public boolean esInstructor() {
        return rol == Rol.INSTRUCTOR;
    }

    public boolean esCurador() {
        return rol == Rol.CURADOR;
    }

    public boolean esArtista() {
        return rol == Rol.ARTISTA;
    }

    public boolean esRolEspecializado() {
        return rol != Rol.ORGANIZADOR && rol != Rol.ASISTENTE;
    }

    public void desactivar() {
        if (!activa) {
            throw new IllegalStateException("La participación ya está desactivada");
        }
        this.activa = false;
    }

    public void reactivar() {
        if (activa) {
            throw new IllegalStateException("La participación ya está activa");
        }
        this.activa = true;
    }

    public void cambiarRol(Rol nuevoRol) {
        if (nuevoRol == null) {
            throw new IllegalArgumentException("El nuevo rol no puede ser nulo");
        }
        if (!evento.validarRol(nuevoRol)) {
            throw new IllegalArgumentException("El rol " + nuevoRol + " no es válido para este tipo de evento");
        }
        this.rol = nuevoRol;
    }

    public boolean esMismaPersonaYEvento(Persona otraPersona, Evento otroEvento) {
        return this.persona.equals(otraPersona) && this.evento.equals(otroEvento);
    }

    public String getDescripcionCompleta() {
        return persona.getNombreCompleto() + " como " + rol.name().toLowerCase() + 
               " en " + evento.getNombre();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
        this.persona = persona;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo");
        }
        this.evento = evento;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        this.rol = rol;
    }

    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public String toString() {
        return getDescripcionCompleta() + (activa ? "" : " (INACTIVA)");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Participacion that = (Participacion) obj;
        return persona.equals(that.persona) && 
               evento.equals(that.evento) && 
               rol == that.rol;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(persona, evento, rol);
    }
}
