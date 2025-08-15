package gov.municipio.domain.model.evento;


import gov.municipio.domain.model.*;
import gov.municipio.domain.model.enums.EstadoEvento;
import gov.municipio.domain.model.enums.Rol;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public abstract class Evento {
    private String nombre;
    private LocalDate fechaInicio;
    private int duracionEstimada;
    private EstadoEvento estado;
    private boolean requiereInscripcion;
    private List<Participacion> participaciones = new ArrayList<>();

    public Evento(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.duracionEstimada = duracionEstimadaHoras;
        this.estado = EstadoEvento.PLANIFICACION; // Estado inicial por defecto
        this.requiereInscripcion = requiereInscripcion;
    }

    void inscribirPersona(Persona persona, Rol rol) {
        if(rol == null){
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        if (!validarRol(rol)){
            throw new IllegalArgumentException("El rol " + rol + " no es válido para este tipo de evento");
        }
        if(estado != EstadoEvento.CONFIRMADO){
            throw new IllegalArgumentException("El evento debe estar confirmado para inscribir personas");
        }
    }

    public boolean validarRol(Rol rol) {
        return true;
    }

}
