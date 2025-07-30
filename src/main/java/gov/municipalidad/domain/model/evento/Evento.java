package gov.municipalidad.domain.model.evento;


import gov.municipalidad.domain.model.*;
import java.time.LocalDate;

public class Evento {
    private String nombre;
    private LocalDate fechaInicio;
    private int duracionEstimadaHoras;
    private boolean requiereInscripcion;

    public Evento(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.duracionEstimadaHoras = duracionEstimadaHoras;
        this.requiereInscripcion = requiereInscripcion;
    }

    void inscribirPersona(Persona persona) {
        // Lógica para inscribir a la persona al evento
    }

}
