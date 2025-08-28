package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.Rol;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "exposiciones")
public class Exposicion extends Evento {
    @Column(nullable = false, length = 50)
    private String disciplinaArtistica;

    public Exposicion(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, Integer cupoMaximo, String disciplinaArtistica) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion, cupoMaximo);
        this.disciplinaArtistica = validarDisciplinaArtistica(disciplinaArtistica);
    }

    // Constructor protegido para JPA
    protected Exposicion() {
        super();
    }

    public String getDisciplinaArtistica() {
        return disciplinaArtistica;
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.CURADOR || rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR;
    }

    @Override
    protected List<Rol> rolesResponsablesObligatorios() {
        return List.of(Rol.ORGANIZADOR, Rol.CURADOR);
    }

    private String validarDisciplinaArtistica(String disciplinaArtistica) {
        if (disciplinaArtistica == null || disciplinaArtistica.trim().isEmpty()) {
            throw new IllegalArgumentException("La disciplina artística no puede ser nula ni vacía");
        }
        if (disciplinaArtistica.trim().length() > 50) {
            throw new IllegalArgumentException("La disciplina artística no puede exceder 50 caracteres");
        }
        return disciplinaArtistica.trim();
    }
}
