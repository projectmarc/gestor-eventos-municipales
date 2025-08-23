package gov.municipio.view.dto;

import gov.municipio.domain.model.evento.Evento;
import gov.municipio.domain.model.enums.EstadoEvento;

import java.time.LocalDate;

/**
 * DTO para transferir datos de eventos a la capa de presentación
 * Evita exponer entidades JPA directamente en la vista
 */
public class EventoDTO {
    private Long id;
    private String nombre;
    private LocalDate fechaInicio;
    private int duracionEstimada;
    private EstadoEvento estado;
    private String tipoEvento;
    private boolean requiereInscripcion;
    private int participantes;

    public EventoDTO() {}

    public EventoDTO(Evento evento) {
        this.id = evento.getId();
        this.nombre = evento.getNombre();
        this.fechaInicio = evento.getFechaInicio();
        this.duracionEstimada = evento.getDuracionEstimada();
        this.estado = evento.getEstado();
        this.tipoEvento = evento.getClass().getSimpleName();
        this.requiereInscripcion = evento.isRequiereInscripcion();
        this.participantes = evento.getParticipaciones().size();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public int getDuracionEstimada() { return duracionEstimada; }
    public void setDuracionEstimada(int duracionEstimada) { this.duracionEstimada = duracionEstimada; }

    public EstadoEvento getEstado() { return estado; }
    public void setEstado(EstadoEvento estado) { this.estado = estado; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public boolean isRequiereInscripcion() { return requiereInscripcion; }
    public void setRequiereInscripcion(boolean requiereInscripcion) { this.requiereInscripcion = requiereInscripcion; }

    public int getParticipantes() { return participantes; }
    public void setParticipantes(int participantes) { this.participantes = participantes; }

    public String getDuracionFormateada() {
        return duracionEstimada + (duracionEstimada == 1 ? " hora" : " horas");
    }
}
