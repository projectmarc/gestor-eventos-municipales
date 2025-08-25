package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.Pelicula;
import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.enums.EstadoEvento;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;

/**
 * Representa un ciclo de cine como evento cultural.
 * Es un modelo rico que encapsula comportamientos específicos del dominio cinematográfico
 * enfocado en la gestión y organización de múltiples películas como conjunto.
 */
public class CicloDeCine extends Evento {
    private static final int MAXIMO_PELICULAS_POR_CICLO = 20;
    private static final int MINIMO_PELICULAS_POR_CICLO = 1;
    private static final String ORDEN_CRONOLOGICO = "CRONOLOGICO";
    private static final String ORDEN_TEMATICO = "TEMATICO";
    private static final String ORDEN_ALEATORIO = "ALEATORIO";
    private static final String ORDEN_POR_DURACION = "POR_DURACION";
    
    private final List<Pelicula> peliculas;
    private String ordenProyeccion;
    private boolean incluyeCharlaPosteriores;
    private String tematicaCiclo;
    private int tiempoEntreProyecciones; // en minutos
    private boolean permiteInterrupciones;

    public CicloDeCine(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, 
                       boolean requiereInscripcion, List<Pelicula> peliculas, 
                       String ordenProyeccion, boolean incluyeCharlaPosteriores) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.peliculas = new ArrayList<>();
        this.ordenProyeccion = validarOrdenProyeccion(ordenProyeccion);
        this.incluyeCharlaPosteriores = incluyeCharlaPosteriores;
        this.tematicaCiclo = "GENERAL";
        this.tiempoEntreProyecciones = 15; // 15 minutos por defecto
        this.permiteInterrupciones = false;
        
        // Validar y agregar películas
        if (peliculas != null && !peliculas.isEmpty()) {
            for (Pelicula pelicula : peliculas) {
                agregarPelicula(pelicula);
            }
        }
        
        validarCicloCompleto();
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ORGANIZADOR || rol == Rol.ASISTENTE || rol == Rol.CURADOR;
    }

    // Métodos de comportamiento del dominio - Gestión de la colección

    /**
     * Agrega una película al ciclo de cine.
     * Delega la validación de la película individual a la clase Pelicula.
     */
    public void agregarPelicula(Pelicula pelicula) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se pueden agregar películas a un ciclo finalizado");
        }
        
        if (pelicula == null) {
            throw new IllegalArgumentException("La película no puede ser nula");
        }
        
        if (peliculas.size() >= MAXIMO_PELICULAS_POR_CICLO) {
            throw new IllegalArgumentException("El ciclo no puede tener más de " + MAXIMO_PELICULAS_POR_CICLO + " películas");
        }
        
        if (yaContienePelicula(pelicula)) {
            throw new IllegalArgumentException("La película '" + pelicula.getNombre() + "' ya está en el ciclo");
        }
        
        peliculas.add(pelicula);
    }

    /**
     * Remueve una película del ciclo.
     */
    public void removerPelicula(Pelicula pelicula) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se pueden remover películas de un ciclo en ejecución o finalizado");
        }
        
        if (pelicula == null) {
            throw new IllegalArgumentException("La película no puede ser nula");
        }
        
        boolean removida = peliculas.removeIf(p -> p.getNombre().equals(pelicula.getNombre()));
        if (!removida) {
            throw new IllegalArgumentException("La película '" + pelicula.getNombre() + "' no está en el ciclo");
        }
    }

    /**
     * Cambia el orden de proyección del ciclo.
     */
    public void cambiarOrdenProyeccion(String nuevoOrden) {
        if (getEstado() == EstadoEvento.EN_EJECUCION || getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar el orden de un ciclo en ejecución o finalizado");
        }
        
        this.ordenProyeccion = validarOrdenProyeccion(nuevoOrden);
    }

    /**
     * Habilita o deshabilita las charlas posteriores.
     */
    public void configurarCharlaPosteriores(boolean incluir) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la configuración de un ciclo finalizado");
        }
        
        this.incluyeCharlaPosteriores = incluir;
    }

    /**
     * Establece la temática del ciclo.
     */
    public void establecerTematicaCiclo(String tematica) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la temática de un ciclo finalizado");
        }
        
        if (tematica == null || tematica.trim().isEmpty()) {
            this.tematicaCiclo = "GENERAL";
        } else {
            String tematicaLimpia = tematica.trim();
            if (tematicaLimpia.length() > 100) {
                throw new IllegalArgumentException("La temática no puede superar los 100 caracteres");
            }
            this.tematicaCiclo = tematicaLimpia;
        }
    }

    /**
     * Configura el tiempo entre proyecciones.
     */
    public void configurarTiempoEntreProyecciones(int minutos) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la configuración de un ciclo finalizado");
        }
        
        if (minutos < 0) {
            throw new IllegalArgumentException("El tiempo entre proyecciones no puede ser negativo");
        }
        
        if (minutos > 120) {
            throw new IllegalArgumentException("El tiempo entre proyecciones no puede superar las 2 horas (120 minutos)");
        }
        
        this.tiempoEntreProyecciones = minutos;
    }

    /**
     * Configura si se permiten interrupciones durante las proyecciones.
     */
    public void configurarInterrupciones(boolean permitir) {
        if (getEstado() == EstadoEvento.FINALIZADO) {
            throw new IllegalArgumentException("No se puede cambiar la configuración de un ciclo finalizado");
        }
        
        this.permiteInterrupciones = permitir;
    }

    // Métodos de consulta del dominio - Análisis del conjunto

    /**
     * Calcula la duración total estimada de todas las películas en minutos.
     */
    public float getDuracionTotalPeliculas() {
        return (float) peliculas.stream()
                .mapToDouble(Pelicula::getDuracionEnMinutos)
                .sum();
    }

    /**
     * Calcula la duración total del ciclo incluyendo charlas y tiempos entre proyecciones.
     */
    public float getDuracionTotalCiclo() {
        float duracionPeliculas = getDuracionTotalPeliculas();
        float tiempoEntreProyeccionesTotal = (peliculas.size() - 1) * tiempoEntreProyecciones;
        float tiempoCharlas = incluyeCharlaPosteriores ? peliculas.size() * 30.0f : 0; // 30 min por charla
        
        return duracionPeliculas + tiempoEntreProyeccionesTotal + tiempoCharlas;
    }

    /**
     * Calcula la duración promedio de las películas.
     */
    public float getDuracionPromedioPeliculas() {
        if (peliculas.isEmpty()) {
            return 0;
        }
        return getDuracionTotalPeliculas() / peliculas.size();
    }

    /**
     * Verifica si el ciclo tiene una duración coherente con las películas programadas.
     */
    public boolean esDuracionCoherente() {
        if (peliculas.isEmpty()) {
            return true;
        }
        
        float duracionCicloHoras = getDuracionTotalCiclo() / 60;
        float margenError = 2.0f; // 2 horas de margen
        
        return getDuracionEstimada() >= (duracionCicloHoras - margenError) &&
               getDuracionEstimada() <= (duracionCicloHoras + margenError);
    }

    /**
     * Obtiene las películas agrupadas por género.
     */
    public Map<String, List<Pelicula>> getPeliculasPorGenero() {
        return peliculas.stream()
                .collect(Collectors.groupingBy(Pelicula::getGenero));
    }

    /**
     * Obtiene las películas agrupadas por década.
     */
    public Map<String, List<Pelicula>> getPeliculasPorDecada() {
        return peliculas.stream()
                .collect(Collectors.groupingBy(p -> {
                    int decada = (p.getAnioEstreno() / 10) * 10;
                    return decada + "s";
                }));
    }

    /**
     * Obtiene la cantidad de películas por tipo (cortometraje, largometraje, etc.).
     */
    public Map<Pelicula.TipoPelicula, Long> getCantidadPorTipoPelicula() {
        return peliculas.stream()
                .collect(Collectors.groupingBy(
                    Pelicula::getTipoPorDuracion,
                    Collectors.counting()
                ));
    }

    /**
     * Verifica si todas las películas son aptas para todo público.
     */
    public boolean esAptoTodoPublico() {
        return peliculas.stream()
                .allMatch(Pelicula::isEsAptaTodoPublico);
    }

    /**
     * Obtiene la edad mínima recomendada para el ciclo completo.
     */
    public int getEdadMinimaRecomendada() {
        return peliculas.stream()
                .mapToInt(Pelicula::getEdadMinimaRecomendada)
                .max()
                .orElse(0);
    }

    /**
     * Verifica si el ciclo tiene diversidad de géneros (al menos 3 géneros diferentes).
     */
    public boolean tieneDiversidadGeneros() {
        long generosUnicos = peliculas.stream()
                .map(Pelicula::getGenero)
                .distinct()
                .count();
        return generosUnicos >= 3;
    }

    /**
     * Verifica si es un ciclo temático (más del 70% de películas del mismo género).
     */
    public boolean esCicloTematico() {
        if (peliculas.isEmpty()) {
            return false;
        }
        
        Map<String, Long> peliculasPorGenero = peliculas.stream()
                .collect(Collectors.groupingBy(Pelicula::getGenero, Collectors.counting()));
        
        long maxPeliculasGenero = peliculasPorGenero.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        
        return (double) maxPeliculasGenero / peliculas.size() >= 0.7;
    }

    /**
     * Verifica si incluye películas clásicas (más de 50 años).
     */
    public boolean incluyePeliculasClasicas() {
        return peliculas.stream()
                .anyMatch(Pelicula::esPeliculaClasica);
    }

    /**
     * Verifica si incluye solo películas recientes (menos de 5 años).
     */
    public boolean esCicloContemporaneo() {
        return !peliculas.isEmpty() && 
               peliculas.stream().allMatch(Pelicula::esPeliculaReciente);
    }

    /**
     * Verifica si una película ya está en el ciclo.
     */
    public boolean yaContienePelicula(Pelicula pelicula) {
        if (pelicula == null) {
            return false;
        }
        return peliculas.stream()
                .anyMatch(p -> p.getNombre().equals(pelicula.getNombre()));
    }

    /**
     * Obtiene el número de películas en el ciclo.
     */
    public int getCantidadPeliculas() {
        return peliculas.size();
    }

    /**
     * Verifica si el ciclo está completo (tiene al menos una película).
     */
    public boolean estaCompleto() {
        return !peliculas.isEmpty();
    }

    /**
     * Verifica si es un ciclo extenso (más de 10 películas).
     */
    public boolean esCicloExtenso() {
        return peliculas.size() > 10;
    }

    /**
     * Verifica si es un ciclo corto (3 películas o menos).
     */
    public boolean esCicloCorto() {
        return peliculas.size() <= 3;
    }

    // Métodos de validación privados

    private String validarOrdenProyeccion(String orden) {
        if (orden == null || orden.trim().isEmpty()) {
            return ORDEN_CRONOLOGICO; // Valor por defecto
        }
        
        String ordenNormalizado = orden.trim().toUpperCase();
        if (!ordenNormalizado.equals(ORDEN_CRONOLOGICO) && 
            !ordenNormalizado.equals(ORDEN_TEMATICO) && 
            !ordenNormalizado.equals(ORDEN_ALEATORIO) &&
            !ordenNormalizado.equals(ORDEN_POR_DURACION)) {
            throw new IllegalArgumentException("Orden de proyección inválido. Debe ser: CRONOLOGICO, TEMATICO, ALEATORIO o POR_DURACION");
        }
        
        return ordenNormalizado;
    }

    private void validarCicloCompleto() {
        if (peliculas.size() < MINIMO_PELICULAS_POR_CICLO) {
            throw new IllegalArgumentException("Un ciclo de cine debe tener al menos " + MINIMO_PELICULAS_POR_CICLO + " película");
        }
    }

    // Getters con encapsulamiento

    /**
     * Retorna una copia inmutable de la lista de películas.
     */
    public List<Pelicula> getPeliculas() {
        return Collections.unmodifiableList(peliculas);
    }

    public String getOrdenProyeccion() {
        return ordenProyeccion;
    }

    public boolean incluyeCharlaPosteriores() {
        return incluyeCharlaPosteriores;
    }

    public String getTematicaCiclo() {
        return tematicaCiclo;
    }

    public int getTiempoEntreProyecciones() {
        return tiempoEntreProyecciones;
    }

    public boolean permiteInterrupciones() {
        return permiteInterrupciones;
    }

    @Override
    public String toString() {
        return String.format("CicloDeCine{nombre='%s', películas=%d, temática='%s', orden=%s, duración=%.1fh, estado=%s}",
                getNombre(), peliculas.size(), tematicaCiclo, ordenProyeccion, 
                getDuracionTotalCiclo() / 60, getEstado());
    }
}
