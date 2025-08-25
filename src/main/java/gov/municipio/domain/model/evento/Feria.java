package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.enums.TipoLugar;
import gov.municipio.domain.model.enums.EstadoEvento;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Representa una feria como evento comercial/cultural.
 * Es un modelo rico que encapsula comportamientos específicos del dominio ferial.
 */
public class Feria extends Evento {
    private static final int STANDS_MINIMOS = 5;
    private static final int STANDS_MAXIMOS = 1000;
    private static final int DURACION_MINIMA_FERIA = 8; // 8 horas mínimo
    private static final int DURACION_MAXIMA_FERIA = 24 * 30; // 30 días máximo
    
    private int cantidadStands;
    private TipoLugar tipoLocacion;
    private final List<String> standsOcupados;
    private double costoStandBasico;
    private boolean incluyeAnimaciones;
    private String tematicaPrincipal;

    public Feria(int cantidadStands, TipoLugar tipoLocacion, String nombre, 
                 LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        
        validarDuracionFeria(duracionEstimadaHoras);
        this.cantidadStands = validarCantidadStands(cantidadStands);
        this.tipoLocacion = validarTipoLocacion(tipoLocacion);
        this.standsOcupados = new ArrayList<>();
        this.costoStandBasico = calcularCostoStandPorDefecto(tipoLocacion);
        this.incluyeAnimaciones = false;
        this.tematicaPrincipal = "GENERAL";
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ORGANIZADOR || rol == Rol.ASISTENTE;
    }

    // Métodos de comportamiento del dominio

    /**
     * Asigna un stand a un expositor.
     */
    public void asignarStand(String identificadorStand, String nombreExpositor) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se pueden asignar stands en una feria finalizada");
        }
        
        if (identificadorStand == null || identificadorStand.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del stand no puede estar vacío");
        }
        
        if (nombreExpositor == null || nombreExpositor.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del expositor no puede estar vacío");
        }
        
        String standLimpio = identificadorStand.trim();
        
        if (standsOcupados.contains(standLimpio)) {
            throw new IllegalArgumentException("El stand '" + standLimpio + "' ya está ocupado");
        }
        
        if (standsOcupados.size() >= cantidadStands) {
            throw new IllegalArgumentException("La feria ha alcanzado su capacidad máxima de " + cantidadStands + " stands");
        }
        
        standsOcupados.add(standLimpio);
    }

    /**
     * Libera un stand ocupado.
     */
    public void liberarStand(String identificadorStand) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se pueden liberar stands durante la ejecución o después de finalizada la feria");
        }
        
        if (identificadorStand == null || identificadorStand.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del stand no puede estar vacío");
        }
        
        String standLimpio = identificadorStand.trim();
        boolean removido = standsOcupados.remove(standLimpio);
        
        if (!removido) {
            throw new IllegalArgumentException("El stand '" + standLimpio + "' no está ocupado");
        }
    }

    /**
     * Cambia la cantidad total de stands disponibles.
     */
    public void cambiarCantidadStands(int nuevaCantidad) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la cantidad de stands en una feria en ejecución o finalizada");
        }
        
        int standsOcupadosActuales = standsOcupados.size();
        if (nuevaCantidad < standsOcupadosActuales) {
            throw new IllegalArgumentException("La nueva cantidad (" + nuevaCantidad + 
                ") no puede ser menor que los stands ocupados (" + standsOcupadosActuales + ")");
        }
        
        this.cantidadStands = validarCantidadStands(nuevaCantidad);
        this.costoStandBasico = calcularCostoStandPorDefecto(tipoLocacion);
    }

    /**
     * Cambia el tipo de locación de la feria.
     */
    public void cambiarTipoLocacion(TipoLugar nuevoTipo) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar el tipo de locación en una feria en ejecución o finalizada");
        }
        
        this.tipoLocacion = validarTipoLocacion(nuevoTipo);
        this.costoStandBasico = calcularCostoStandPorDefecto(nuevoTipo);
    }

    /**
     * Establece el costo básico por stand.
     */
    public void establecerCostoStandBasico(double nuevoCosto) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar el costo en una feria finalizada");
        }
        
        if (nuevoCosto < 0) {
            throw new IllegalArgumentException("El costo del stand no puede ser negativo");
        }
        
        if (nuevoCosto > 1000000) {
            throw new IllegalArgumentException("El costo del stand no puede superar $1,000,000");
        }
        
        this.costoStandBasico = nuevoCosto;
    }

    /**
     * Configura si la feria incluye animaciones.
     */
    public void configurarAnimaciones(boolean incluir) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la configuración de una feria finalizada");
        }
        
        this.incluyeAnimaciones = incluir;
    }

    /**
     * Establece la temática principal de la feria.
     */
    public void establecerTematicaPrincipal(String tematica) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la temática de una feria finalizada");
        }
        
        if (tematica == null || tematica.trim().isEmpty()) {
            this.tematicaPrincipal = "GENERAL";
        } else {
            String tematicaLimpia = tematica.trim().toUpperCase();
            if (tematicaLimpia.length() > 50) {
                throw new IllegalArgumentException("La temática no puede superar los 50 caracteres");
            }
            this.tematicaPrincipal = tematicaLimpia;
        }
    }

    // Métodos de consulta del dominio

    /**
     * Verifica si la feria puede inaugurarse según las reglas de negocio.
     */
    public boolean puedeInaugurarse() {
        return getEstado() == EstadoEvento.CONFIRMADO &&
               getCantidadParticipantesPorRol(Rol.ORGANIZADOR) >= 2 &&
               standsOcupados.size() >= (cantidadStands * 0.3); // Al menos 30% de ocupación
    }

    /**
     * Calcula el porcentaje de ocupación de stands.
     */
    public double getPorcentajeOcupacion() {
        if (cantidadStands == 0) {
            return 0;
        }
        return (double) standsOcupados.size() / cantidadStands * 100;
    }

    /**
     * Obtiene la cantidad de stands disponibles.
     */
    public int getStandsDisponibles() {
        return cantidadStands - standsOcupados.size();
    }

    /**
     * Verifica si la feria está agotada (todos los stands ocupados).
     */
    public boolean estaAgotada() {
        return standsOcupados.size() == cantidadStands;
    }

    /**
     * Verifica si está cerca del límite de capacidad (80%+).
     */
    public boolean estaCercaDelLimite() {
        return getPorcentajeOcupacion() >= 80.0;
    }

    /**
     * Calcula el ingreso estimado por stands.
     */
    public double getIngresoEstimadoStands() {
        return standsOcupados.size() * costoStandBasico;
    }

    /**
     * Verifica si es una feria grande (más de 100 stands).
     */
    public boolean esFeriaGrande() {
        return cantidadStands > 100;
    }

    /**
     * Verifica si es una feria pequeña (menos de 20 stands).
     */
    public boolean esFeriaPequena() {
        return cantidadStands < 20;
    }

    /**
     * Verifica si un stand específico está ocupado.
     */
    public boolean estaStandOcupado(String identificadorStand) {
        if (identificadorStand == null) {
            return false;
        }
        return standsOcupados.contains(identificadorStand.trim());
    }

    /**
     * Calcula la duración de la feria en días.
     */
    public int getDuracionEnDias() {
        return getDuracionEstimada() / 24;
    }

    /**
     * Verifica si es una feria de larga duración (más de 7 días).
     */
    public boolean esFeriaLargaDuracion() {
        return getDuracionEnDias() > 7;
    }

    // Métodos de validación privados

    private int validarCantidadStands(int cantidad) {
        if (cantidad < STANDS_MINIMOS) {
            throw new IllegalArgumentException("Una feria debe tener al menos " + STANDS_MINIMOS + " stands");
        }
        
        if (cantidad > STANDS_MAXIMOS) {
            throw new IllegalArgumentException("Una feria no puede tener más de " + STANDS_MAXIMOS + " stands");
        }
        
        return cantidad;
    }

    private TipoLugar validarTipoLocacion(TipoLugar tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de locación no puede ser nulo");
        }
        
        return tipo;
    }

    private void validarDuracionFeria(int duracion) {
        if (duracion < DURACION_MINIMA_FERIA) {
            throw new IllegalArgumentException("Una feria debe durar al menos " + DURACION_MINIMA_FERIA + " horas");
        }
        
        if (duracion > DURACION_MAXIMA_FERIA) {
            throw new IllegalArgumentException("Una feria no puede durar más de " + DURACION_MAXIMA_FERIA + " horas (30 días)");
        }
    }

    /**
     * Calcula el costo básico del stand según el tipo de locación.
     */
    private double calcularCostoStandPorDefecto(TipoLugar tipo) {
        return switch (tipo) {
            case TECHADO -> 5000.0;  // Más caro por facilidades
            case AIRE_LIBRE -> 3000.0;  // Más económico
        };
    }

    // Getters con encapsulamiento

    public int getCantidadStands() {
        return cantidadStands;
    }

    public TipoLugar getTipoLocacion() {
        return tipoLocacion;
    }

    /**
     * Retorna una copia inmutable de la lista de stands ocupados.
     */
    public List<String> getStandsOcupados() {
        return Collections.unmodifiableList(standsOcupados);
    }

    public double getCostoStandBasico() {
        return costoStandBasico;
    }

    public boolean incluyeAnimaciones() {
        return incluyeAnimaciones;
    }

    public String getTematicaPrincipal() {
        return tematicaPrincipal;
    }

    @Override
    public String toString() {
        return String.format("Feria{nombre='%s', stands=%d/%d (%.1f%%), temática=%s, ubicación=%s, estado=%s}",
                getNombre(), standsOcupados.size(), cantidadStands, 
                getPorcentajeOcupacion(), tematicaPrincipal, tipoLocacion, getEstado());
    }
}
