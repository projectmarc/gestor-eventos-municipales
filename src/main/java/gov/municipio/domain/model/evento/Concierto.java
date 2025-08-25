package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.enums.EstadoEvento;
import java.time.LocalDate;
import java.util.Set;

/**
 * Representa un concierto como evento musical.
 * Es un modelo rico que encapsula comportamientos específicos del dominio musical.
 */
public class Concierto extends Evento {
    private static final int CAPACIDAD_MINIMA = 10;
    private static final int CAPACIDAD_MAXIMA = 100000;
    private static final Set<String> GENEROS_VALIDOS = Set.of(
        "ROCK", "POP", "JAZZ", "CLASICA", "FOLK", "ELECTRONICA", 
        "REGGAE", "BLUES", "COUNTRY", "TANGO", "CUMBIA", "OTRO"
    );
    
    private String artistaPrincipal;
    private String generoMusical;
    private int capacidadMaxima;
    private boolean requiereEquipoEspecial;

    public Concierto(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, 
                     boolean requiereInscripcion, String artistaPrincipal, 
                     String generoMusical, int capacidadMaxima) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.artistaPrincipal = validarArtistaPrincipal(artistaPrincipal);
        this.generoMusical = validarGeneroMusical(generoMusical);
        this.capacidadMaxima = validarCapacidadMaxima(capacidadMaxima);
        this.requiereEquipoEspecial = determinarEquipoEspecial(generoMusical);
    }

    @Override
    protected void validarCupoDisponible() {
        int asistentesActuales = getCantidadParticipantesPorRol(Rol.ASISTENTE);
        if (asistentesActuales >= capacidadMaxima) {
            throw new IllegalArgumentException("El concierto ha alcanzado su capacidad máxima de " + capacidadMaxima + " asistentes");
        }
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR || rol == Rol.ARTISTA;
    }

    // Métodos de comportamiento del dominio

    /**
     * Cambia el artista principal del concierto.
     * Solo permite cambio si el evento no está en ejecución o finalizado.
     */
    public void cambiarArtistaPrincipal(String nuevoArtista) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar el artista de un concierto en ejecución o finalizado");
        }
        
        this.artistaPrincipal = validarArtistaPrincipal(nuevoArtista);
    }

    /**
     * Cambia el género musical del concierto.
     */
    public void cambiarGeneroMusical(String nuevoGenero) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar el género de un concierto en ejecución o finalizado");
        }
        
        this.generoMusical = validarGeneroMusical(nuevoGenero);
        this.requiereEquipoEspecial = determinarEquipoEspecial(nuevoGenero);
    }

    /**
     * Ajusta la capacidad máxima del concierto.
     */
    public void ajustarCapacidadMaxima(int nuevaCapacidad) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la capacidad de un concierto en ejecución o finalizado");
        }
        
        int asistentesActuales = getCantidadParticipantesPorRol(Rol.ASISTENTE);
        if (nuevaCapacidad < asistentesActuales) {
            throw new IllegalArgumentException("La nueva capacidad (" + nuevaCapacidad + 
                ") no puede ser menor que los asistentes actuales (" + asistentesActuales + ")");
        }
        
        this.capacidadMaxima = validarCapacidadMaxima(nuevaCapacidad);
    }

    /**
     * Verifica si hay suficiente personal técnico para el concierto.
     */
    public boolean tieneSuficientePersonalTecnico() {
        int organizadores = getCantidadParticipantesPorRol(Rol.ORGANIZADOR);
        
        if (requiereEquipoEspecial) {
            return organizadores >= 3; // Requiere más personal técnico
        } else {
            return organizadores >= 1; // Personal mínimo
        }
    }

    /**
     * Calcula el ratio de asistentes por organizador.
     */
    public double getRatioAsistentesOrganizadores() {
        int organizadores = getCantidadParticipantesPorRol(Rol.ORGANIZADOR);
        int asistentes = getCantidadParticipantesPorRol(Rol.ASISTENTE);
        
        if (organizadores == 0) {
            return asistentes > 0 ? Double.POSITIVE_INFINITY : 0;
        }
        
        return (double) asistentes / organizadores;
    }

    // Métodos de consulta del dominio

    /**
     * Verifica si el concierto puede comenzar según las reglas de negocio.
     */
    public boolean puedeComenzar() {
        return getEstado() == EstadoEvento.CONFIRMADO &&
               getCantidadParticipantesPorRol(Rol.ARTISTA) >= 1 &&
               tieneSuficientePersonalTecnico() &&
               getCantidadParticipantesPorRol(Rol.ASISTENTE) > 0;
    }

    /**
     * Calcula el porcentaje de ocupación actual.
     */
    public double getPorcentajeOcupacion() {
        int asistentes = getCantidadParticipantesPorRol(Rol.ASISTENTE);
        return (double) asistentes / capacidadMaxima * 100;
    }

    /**
     * Obtiene la cantidad de entradas disponibles.
     */
    public int getEntradasDisponibles() {
        return capacidadMaxima - getCantidadParticipantesPorRol(Rol.ASISTENTE);
    }

    /**
     * Verifica si el concierto está agotado.
     */
    public boolean estaAgotado() {
        return getEntradasDisponibles() == 0;
    }

    /**
     * Verifica si el concierto está cerca del límite de capacidad (90%+).
     */
    public boolean estaCercaDelLimite() {
        return getPorcentajeOcupacion() >= 90.0;
    }

    /**
     * Determina si es un concierto masivo (más de 5000 asistentes de capacidad).
     */
    public boolean esConciertoMasivo() {
        return capacidadMaxima > 5000;
    }

    /**
     * Verifica si es un concierto íntimo (menos de 100 asistentes de capacidad).
     */
    public boolean esConciertoIntimo() {
        return capacidadMaxima < 100;
    }

    // Métodos de validación privados

    private String validarArtistaPrincipal(String artista) {
        if (artista == null || artista.trim().isEmpty()) {
            throw new IllegalArgumentException("El artista principal no puede estar vacío");
        }
        
        String artistaLimpio = artista.trim();
        if (artistaLimpio.length() < 2) {
            throw new IllegalArgumentException("El nombre del artista debe tener al menos 2 caracteres");
        }
        
        if (artistaLimpio.length() > 100) {
            throw new IllegalArgumentException("El nombre del artista no puede superar los 100 caracteres");
        }
        
        return artistaLimpio;
    }

    private String validarGeneroMusical(String genero) {
        if (genero == null || genero.trim().isEmpty()) {
            return "OTRO"; // Valor por defecto
        }
        
        String generoNormalizado = genero.trim().toUpperCase();
        if (!GENEROS_VALIDOS.contains(generoNormalizado)) {
            throw new IllegalArgumentException("Género musical inválido. Géneros válidos: " + GENEROS_VALIDOS);
        }
        
        return generoNormalizado;
    }

    private int validarCapacidadMaxima(int capacidad) {
        if (capacidad < CAPACIDAD_MINIMA) {
            throw new IllegalArgumentException("La capacidad mínima es " + CAPACIDAD_MINIMA + " personas");
        }
        
        if (capacidad > CAPACIDAD_MAXIMA) {
            throw new IllegalArgumentException("La capacidad máxima es " + CAPACIDAD_MAXIMA + " personas");
        }
        
        return capacidad;
    }

    /**
     * Determina si el género musical requiere equipo especial.
     */
    private boolean determinarEquipoEspecial(String genero) {
        return Set.of("ELECTRONICA", "ROCK", "CLASICA").contains(genero);
    }

    // Getters

    public String getArtistaPrincipal() {
        return artistaPrincipal;
    }

    public String getGeneroMusical() {
        return generoMusical;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public boolean requiereEquipoEspecial() {
        return requiereEquipoEspecial;
    }

    @Override
    public String toString() {
        return String.format("Concierto{nombre='%s', artista='%s', género=%s, ocupación=%d/%d (%.1f%%), estado=%s}",
                getNombre(), artistaPrincipal, generoMusical, 
                getCantidadParticipantesPorRol(Rol.ASISTENTE), capacidadMaxima, 
                getPorcentajeOcupacion(), getEstado());
    }
}