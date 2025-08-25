package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.enums.EstadoEvento;
import java.time.LocalDate;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Representa una exposición artística como evento cultural.
 * Es un modelo rico que encapsula comportamientos específicos del dominio artístico.
 */
public class Exposicion extends Evento {
    private static final Set<String> DISCIPLINAS_VALIDAS = Set.of(
        "PINTURA", "ESCULTURA", "FOTOGRAFIA", "ARTE_DIGITAL", "INSTALACION",
        "GRABADO", "CERAMICA", "TEXTIL", "ARTE_CONTEMPORANEO", "ARTE_CLASICO", "MIXTA"
    );
    private static final int DURACION_MINIMA_EXPOSICION = 24; // 1 día
    private static final int DURACION_MAXIMA_EXPOSICION = 24 * 90; // 90 días
    
    private String disciplinaArtistica;
    private final List<String> artistasParticipantes;
    private String descripcionTematica;
    private boolean permiteVisitasGuiadas;
    private int aforoMaximoDiario;

    public Exposicion(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, 
                      boolean requiereInscripcion, String disciplinaArtistica) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        
        validarDuracionExposicion(duracionEstimadaHoras);
        this.disciplinaArtistica = validarDisciplinaArtistica(disciplinaArtistica);
        this.artistasParticipantes = new ArrayList<>();
        this.permiteVisitasGuiadas = true; // Por defecto permite visitas guiadas
        this.aforoMaximoDiario = calcularAforoMaximoPorDefecto();
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.CURADOR || rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR || rol == Rol.ARTISTA;
    }

    @Override
    protected void validarCupoDisponible() {
        if (!isRequiereInscripcion()) {
            return; // No hay límite si no requiere inscripción
        }
        
        int asistentesActuales = getCantidadParticipantesPorRol(Rol.ASISTENTE);
        if (asistentesActuales >= aforoMaximoDiario) {
            throw new IllegalArgumentException("La exposición ha alcanzado su aforo máximo diario de " + aforoMaximoDiario + " visitantes");
        }
    }

    // Métodos de comportamiento del dominio

    /**
     * Agrega un artista participante a la exposición.
     */
    public void agregarArtista(String nombreArtista) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se pueden agregar artistas a una exposición finalizada");
        }
        
        if (nombreArtista == null || nombreArtista.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del artista no puede estar vacío");
        }
        
        String artistaLimpio = nombreArtista.trim();
        if (artistasParticipantes.contains(artistaLimpio)) {
            throw new IllegalArgumentException("El artista '" + artistaLimpio + "' ya está participando en la exposición");
        }
        
        if (artistasParticipantes.size() >= 50) {
            throw new IllegalArgumentException("Una exposición no puede tener más de 50 artistas participantes");
        }
        
        artistasParticipantes.add(artistaLimpio);
    }

    /**
     * Remueve un artista participante de la exposición.
     */
    public void removerArtista(String nombreArtista) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se pueden remover artistas de una exposición en ejecución o finalizada");
        }
        
        if (nombreArtista == null || nombreArtista.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del artista no puede estar vacío");
        }
        
        boolean removido = artistasParticipantes.remove(nombreArtista.trim());
        if (!removido) {
            throw new IllegalArgumentException("El artista '" + nombreArtista.trim() + "' no está participando en la exposición");
        }
    }

    /**
     * Cambia la disciplina artística de la exposición.
     */
    public void cambiarDisciplinaArtistica(String nuevaDisciplina) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la disciplina de una exposición en ejecución o finalizada");
        }
        
        this.disciplinaArtistica = validarDisciplinaArtistica(nuevaDisciplina);
    }

    /**
     * Establece la descripción temática de la exposición.
     */
    public void establecerDescripcionTematica(String descripcion) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la descripción de una exposición finalizada");
        }
        
        if (descripcion != null && descripcion.trim().length() > 1000) {
            throw new IllegalArgumentException("La descripción temática no puede superar los 1000 caracteres");
        }
        
        this.descripcionTematica = descripcion != null ? descripcion.trim() : null;
    }

    /**
     * Configura si la exposición permite visitas guiadas.
     */
    public void configurarVisitasGuiadas(boolean permitir) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la configuración de una exposición finalizada");
        }
        
        this.permiteVisitasGuiadas = permitir;
    }

    /**
     * Ajusta el aforo máximo diario de la exposición.
     */
    public void ajustarAforoMaximoDiario(int nuevoAforo) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar el aforo de una exposición en ejecución o finalizada");
        }
        
        if (nuevoAforo < 1) {
            throw new IllegalArgumentException("El aforo máximo debe ser al menos 1 persona");
        }
        
        if (nuevoAforo > 10000) {
            throw new IllegalArgumentException("El aforo máximo no puede superar las 10,000 personas por día");
        }
        
        this.aforoMaximoDiario = nuevoAforo;
    }

    // Métodos de consulta del dominio

    /**
     * Verifica si la exposición puede inaugurarse según las reglas de negocio.
     */
    public boolean puedeInaugurarse() {
        return getEstado() == EstadoEvento.CONFIRMADO &&
               getCantidadParticipantesPorRol(Rol.CURADOR) >= 1 &&
               !artistasParticipantes.isEmpty() &&
               (descripcionTematica != null && !descripcionTematica.isEmpty());
    }

    /**
     * Verifica si es una exposición individual (un solo artista).
     */
    public boolean esExposicionIndividual() {
        return artistasParticipantes.size() == 1;
    }

    /**
     * Verifica si es una exposición colectiva (múltiples artistas).
     */
    public boolean esExposicionColectiva() {
        return artistasParticipantes.size() > 1;
    }

    /**
     * Calcula la duración de la exposición en días.
     */
    public int getDuracionEnDias() {
        return getDuracionEstimada() / 24;
    }

    /**
     * Verifica si es una exposición de larga duración (más de 30 días).
     */
    public boolean esExposicionLargaDuracion() {
        return getDuracionEnDias() > 30;
    }

    /**
     * Verifica si es una exposición temporal (menos de 7 días).
     */
    public boolean esExposicionTemporal() {
        return getDuracionEnDias() < 7;
    }

    /**
     * Obtiene el número de artistas participantes.
     */
    public int getCantidadArtistas() {
        return artistasParticipantes.size();
    }

    /**
     * Verifica si un artista específico está participando.
     */
    public boolean participaArtista(String nombreArtista) {
        if (nombreArtista == null) {
            return false;
        }
        return artistasParticipantes.contains(nombreArtista.trim());
    }

    /**
     * Calcula el promedio de visitantes esperados por día.
     */
    public double getPromedioVisitantesPorDia() {
        int totalAsistentes = getCantidadParticipantesPorRol(Rol.ASISTENTE);
        int diasDuracion = getDuracionEnDias();
        
        if (diasDuracion == 0) {
            return 0;
        }
        
        return (double) totalAsistentes / diasDuracion;
    }

    // Métodos de validación privados

    private String validarDisciplinaArtistica(String disciplina) {
        if (disciplina == null || disciplina.trim().isEmpty()) {
            throw new IllegalArgumentException("La disciplina artística no puede estar vacía");
        }
        
        String disciplinaNormalizada = disciplina.trim().toUpperCase();
        if (!DISCIPLINAS_VALIDAS.contains(disciplinaNormalizada)) {
            throw new IllegalArgumentException("Disciplina artística inválida. Disciplinas válidas: " + DISCIPLINAS_VALIDAS);
        }
        
        return disciplinaNormalizada;
    }

    private void validarDuracionExposicion(int duracion) {
        if (duracion < DURACION_MINIMA_EXPOSICION) {
            throw new IllegalArgumentException("Una exposición debe durar al menos " + DURACION_MINIMA_EXPOSICION + " horas (1 día)");
        }
        
        if (duracion > DURACION_MAXIMA_EXPOSICION) {
            throw new IllegalArgumentException("Una exposición no puede durar más de " + DURACION_MAXIMA_EXPOSICION + " horas (90 días)");
        }
    }

    private int calcularAforoMaximoPorDefecto() {
        // Aforo por defecto basado en la disciplina
        return switch (disciplinaArtistica) {
            case "INSTALACION", "ARTE_CONTEMPORANEO" -> 200; // Requieren más espacio
            case "ESCULTURA" -> 300;
            case "PINTURA", "FOTOGRAFIA", "GRABADO" -> 500;
            default -> 400;
        };
    }

    // Getters con encapsulamiento

    public String getDisciplinaArtistica() {
        return disciplinaArtistica;
    }

    /**
     * Retorna una copia inmutable de la lista de artistas participantes.
     */
    public List<String> getArtistasParticipantes() {
        return Collections.unmodifiableList(artistasParticipantes);
    }

    public String getDescripcionTematica() {
        return descripcionTematica;
    }

    public boolean permiteVisitasGuiadas() {
        return permiteVisitasGuiadas;
    }

    public int getAforoMaximoDiario() {
        return aforoMaximoDiario;
    }

    @Override
    public String toString() {
        return String.format("Exposicion{nombre='%s', disciplina=%s, artistas=%d, duración=%d días, estado=%s}",
                getNombre(), disciplinaArtistica, artistasParticipantes.size(), 
                getDuracionEnDias(), getEstado());
    }
}
