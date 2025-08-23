package gov.municipio.domain.model;

import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.evento.Evento;
import jakarta.persistence.*;

@Entity
@Table(name = "participaciones")
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

    public Participacion() {}

    public Participacion(Persona persona, Evento evento, Rol rol) {
        if(persona == null || evento == null || rol == null) {
            throw new IllegalArgumentException("Los parámetros no pueden ser nulos");
        }
        this.persona = persona;
        this.evento = evento;
        this.rol = rol;
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
        this.persona = persona;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return persona.getNombreCompleto() + " - " + rol + " en " + evento.getNombre();
    }
}
