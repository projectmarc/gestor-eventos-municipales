package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.ModalidadTaller;
import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.enums.EstadoEvento;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Representa un taller como evento educativo.
 * Es un modelo rico que encapsula comportamientos específicos del dominio educativo.
 */
public class Taller extends Evento {
    private static final int CUPO_MINIMO = 1;
    private static final int CUPO_MAXIMO = 500;
    private static final int DURACION_MINIMA_TALLER = 1; // 1 hora mínimo
    private static final int DURACION_MAXIMA_TALLER = 24 * 7; // 7 días máximo
    
    private ModalidadTaller modalidad;
    private int cupoMaximo;
    private final List<String> materialesNecesarios;
    private String nivelRequerido; // PRINCIPIANTE, INTERMEDIO, AVANZADO
    private int edadMinima;
    private String descripcionContenido;
    private boolean certificaParticipacion;

    public Taller(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, 
                  boolean requiereInscripcion, ModalidadTaller modalidad, int cupoMaximo) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        
        validarDuracionTaller(duracionEstimadaHoras);
        this.modalidad = validarModalidad(modalidad);
        this.cupoMaximo = validarCupoMaximo(cupoMaximo);
        this.materialesNecesarios = new ArrayList<>();
        this.nivelRequerido = "PRINCIPIANTE"; // Valor por defecto
        this.edadMinima = 16; // Edad mínima por defecto
        this.certificaParticipacion = false;
        this.descripcionContenido = "";
    }

    @Override
    protected void validarCupoDisponible() {
        int asistentesActuales = getCantidadParticipantesPorRol(Rol.ASISTENTE);
        if (asistentesActuales >= cupoMaximo) {
            throw new IllegalArgumentException("El taller ha alcanzado su cupo máximo de " + cupoMaximo + " asistentes");
        }
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.INSTRUCTOR || rol == Rol.ASISTENTE || rol == Rol.ORGANIZADOR;
    }

    // Métodos de comportamiento del dominio específicos mejorados

    public void cambiarCupoMaximo(int nuevoCupo) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar el cupo de un taller en ejecución o finalizado");
        }
        
        int asistentesActuales = getCantidadParticipantesPorRol(Rol.ASISTENTE);
        if (nuevoCupo < asistentesActuales) {
            throw new IllegalArgumentException("El nuevo cupo (" + nuevoCupo + ") no puede ser menor que los asistentes actuales (" + asistentesActuales + ")");
        }
        
        this.cupoMaximo = validarCupoMaximo(nuevoCupo);
    }

    /**
     * Cambia la modalidad del taller.
     */
    public void cambiarModalidad(ModalidadTaller nuevaModalidad) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la modalidad de un taller en ejecución o finalizado");
        }
        
        this.modalidad = validarModalidad(nuevaModalidad);
    }

    /**
     * Agrega un material necesario para el taller.
     */
    public void agregarMaterialNecesario(String material) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se pueden agregar materiales a un taller finalizado");
        }
        
        if (material == null || material.trim().isEmpty()) {
            throw new IllegalArgumentException("El material no puede estar vacío");
        }
        
        String materialLimpio = material.trim();
        if (materialesNecesarios.contains(materialLimpio)) {
            throw new IllegalArgumentException("El material '" + materialLimpio + "' ya está en la lista");
        }
        
        if (materialesNecesarios.size() >= 20) {
            throw new IllegalArgumentException("Un taller no puede tener más de 20 materiales diferentes");
        }
        
        materialesNecesarios.add(materialLimpio);
    }

    /**
     * Remueve un material de la lista de necesarios.
     */
    public void removerMaterialNecesario(String material) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se pueden remover materiales durante la ejecución o después de finalizado el taller");
        }
        
        if (material == null || material.trim().isEmpty()) {
            throw new IllegalArgumentException("El material no puede estar vacío");
        }
        
        boolean removido = materialesNecesarios.remove(material.trim());
        if (!removido) {
            throw new IllegalArgumentException("El material '" + material.trim() + "' no está en la lista");
        }
    }

    /**
     * Establece el nivel requerido para el taller.
     */
    public void establecerNivelRequerido(String nivel) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar el nivel de un taller finalizado");
        }
        
        this.nivelRequerido = validarNivelRequerido(nivel);
    }

    /**
     * Establece la edad mínima para participar en el taller.
     */
    public void establecerEdadMinima(int edad) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la edad mínima de un taller finalizado");
        }
        
        this.edadMinima = validarEdadMinima(edad);
    }

    /**
     * Establece la descripción del contenido del taller.
     */
    public void establecerDescripcionContenido(String descripcion) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la descripción de un taller finalizado");
        }
        
        if (descripcion != null && descripcion.trim().length() > 2000) {
            throw new IllegalArgumentException("La descripción del contenido no puede superar los 2000 caracteres");
        }
        
        this.descripcionContenido = descripcion != null ? descripcion.trim() : "";
    }

    /**
     * Configura si el taller certifica la participación.
     */
    public void configurarCertificacion(boolean certifica) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la configuración de certificación de un taller finalizado");
        }
        
        this.certificaParticipacion = certifica;
    }

    // Métodos de consulta del dominio mejorados

    public int getCuposDisponibles() {
        return cupoMaximo - getCantidadParticipantesPorRol(Rol.ASISTENTE);
    }

    public boolean tieneCupoDisponible() {
        return getCuposDisponibles() > 0;
    }

    /**
     * Verifica si el taller puede comenzar según las reglas de negocio.
     */
    public boolean puedeComenzar() {
        return getEstado() == EstadoEvento.CONFIRMADO &&
               getCantidadParticipantesPorRol(Rol.INSTRUCTOR) >= 1 &&
               getCantidadParticipantesPorRol(Rol.ASISTENTE) >= 1 &&
               (descripcionContenido != null && !descripcionContenido.isEmpty());
    }

    /**
     * Calcula el ratio de asistentes por instructor.
     */
    public double getRatioAsistentesInstructores() {
        int instructores = getCantidadParticipantesPorRol(Rol.INSTRUCTOR);
        int asistentes = getCantidadParticipantesPorRol(Rol.ASISTENTE);
        
        if (instructores == 0) {
            return asistentes > 0 ? Double.POSITIVE_INFINITY : 0;
        }
        
        return (double) asistentes / instructores;
    }

    /**
     * Verifica si tiene un ratio adecuado instructor-asistentes.
     */
    public boolean tieneRatioAdecuado() {
        double ratio = getRatioAsistentesInstructores();
        
        // Ratio recomendado según modalidad
        return switch (modalidad) {
            case PRESENCIAL -> ratio <= 15; // Máximo 15 asistentes por instructor
            case VIRTUAL -> ratio <= 25;    // Puede manejar más en virtual
        };
    }

    /**
     * Calcula el porcentaje de ocupación actual.
     */
    public double getPorcentajeOcupacion() {
        int asistentes = getCantidadParticipantesPorRol(Rol.ASISTENTE);
        return (double) asistentes / cupoMaximo * 100;
    }

    /**
     * Verifica si el taller está cerca del límite de capacidad (85%+).
     */
    public boolean estaCercaDelLimite() {
        return getPorcentajeOcupacion() >= 85.0;
    }

    /**
     * Verifica si es un taller intensivo (más de 8 horas por día).
     */
    public boolean esTallerIntensivo() {
        int diasDuracion = getDuracionEstimada() / 24;
        if (diasDuracion == 0) diasDuracion = 1; // Al menos un día
        
        double horasPorDia = (double) getDuracionEstimada() / diasDuracion;
        return horasPorDia > 8;
    }

    /**
     * Verifica si es un taller de múltiples días.
     */
    public boolean esMultiplesDias() {
        return getDuracionEstimada() > 24;
    }

    /**
     * Obtiene la duración en días del taller.
     */
    public int getDuracionEnDias() {
        return Math.max(1, getDuracionEstimada() / 24);
    }

    /**
     * Verifica si requiere materiales especiales.
     */
    public boolean requiereMaterialesEspeciales() {
        return !materialesNecesarios.isEmpty();
    }

    /**
     * Verifica si es apto para todas las edades.
     */
    public boolean esAptoTodasEdades() {
        return edadMinima <= 6;
    }

    /**
     * Verifica si es solo para adultos.
     */
    public boolean esSoloAdultos() {
        return edadMinima >= 18;
    }

    // Métodos de validación privados

    private ModalidadTaller validarModalidad(ModalidadTaller modalidad) {
        if (modalidad == null) {
            throw new IllegalArgumentException("La modalidad del taller no puede ser nula");
        }
        return modalidad;
    }

    private int validarCupoMaximo(int cupo) {
        if (cupo < CUPO_MINIMO) {
            throw new IllegalArgumentException("El cupo máximo debe ser al menos " + CUPO_MINIMO);
        }
        if (cupo > CUPO_MAXIMO) {
            throw new IllegalArgumentException("El cupo máximo no puede superar los " + CUPO_MAXIMO + " asistentes");
        }
        return cupo;
    }

    private void validarDuracionTaller(int duracion) {
        if (duracion < DURACION_MINIMA_TALLER) {
            throw new IllegalArgumentException("Un taller debe durar al menos " + DURACION_MINIMA_TALLER + " hora");
        }
        
        if (duracion > DURACION_MAXIMA_TALLER) {
            throw new IllegalArgumentException("Un taller no puede durar más de " + DURACION_MAXIMA_TALLER + " horas (7 días)");
        }
    }

    private String validarNivelRequerido(String nivel) {
        if (nivel == null || nivel.trim().isEmpty()) {
            return "PRINCIPIANTE";
        }
        
        String nivelNormalizado = nivel.trim().toUpperCase();
        if (!nivelNormalizado.equals("PRINCIPIANTE") && 
            !nivelNormalizado.equals("INTERMEDIO") && 
            !nivelNormalizado.equals("AVANZADO")) {
            throw new IllegalArgumentException("Nivel inválido. Debe ser: PRINCIPIANTE, INTERMEDIO o AVANZADO");
        }
        
        return nivelNormalizado;
    }

    private int validarEdadMinima(int edad) {
        if (edad < 3) {
            throw new IllegalArgumentException("La edad mínima no puede ser menor a 3 años");
        }
        
        if (edad > 99) {
            throw new IllegalArgumentException("La edad mínima no puede ser mayor a 99 años");
        }
        
        return edad;
    }

    // Getters con encapsulamiento

    public ModalidadTaller getModalidad() {
        return modalidad;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    /**
     * Retorna una copia inmutable de la lista de materiales necesarios.
     */
    public List<String> getMaterialesNecesarios() {
        return Collections.unmodifiableList(materialesNecesarios);
    }

    public String getNivelRequerido() {
        return nivelRequerido;
    }

    public int getEdadMinima() {
        return edadMinima;
    }

    public String getDescripcionContenido() {
        return descripcionContenido;
    }

    public boolean certificaParticipacion() {
        return certificaParticipacion;
    }

    @Override
    public String toString() {
        return String.format("Taller{nombre='%s', modalidad=%s, cupo=%d/%d (%.1f%%), nivel=%s, edad mín=%d, estado=%s}",
                getNombre(), modalidad, getCantidadParticipantesPorRol(Rol.ASISTENTE), cupoMaximo, 
                getPorcentajeOcupacion(), nivelRequerido, edadMinima, getEstado());
    }
}
