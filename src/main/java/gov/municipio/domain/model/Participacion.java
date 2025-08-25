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
    
    @ManyToOne
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
        this.persona = persona;
        this.evento = evento;
        this.rol = rol;
    }

    public Persona getPersona() {
        return persona;
    }

    public Evento getEvento() {
        return evento;
    }

    public Rol getRol() {
        return rol;
    }
}
