package gov.municipio.domain.model;

import java.time.Year;
import java.util.Set;

/**
 * Representa una película como entidad del dominio cinematográfico.
 * Es un modelo rico que encapsula comportamientos específicos de una película individual.
 */
public class Pelicula {
    private static final float DURACION_MINIMA = 1.0f; // 1 minuto mínimo
    private static final float DURACION_MAXIMA = 600.0f; // 10 horas máximo
    private static final float LIMITE_CORTOMETRAJE = 30.0f; // Hasta 30 minutos
    private static final float LIMITE_LARGOMETRAJE = 90.0f; // Desde 90 minutos
    
    private static final Set<String> GENEROS_VALIDOS = Set.of(
        "DRAMA", "COMEDIA", "ACCION", "TERROR", "CIENCIA_FICCION", 
        "ROMANCE", "THRILLER", "DOCUMENTAL", "ANIMACION", "MUSICAL",
        "WESTERN", "GUERRA", "BIOGRAFIA", "HISTORIA", "CRIMEN", "OTRO"
    );
    
    private String nombre;
    private float duracionEnMinutos;
    private String genero;
    private String director;
    private int anioEstreno;
    private String sinopsis;
    private boolean esAptaTodoPublico;
    private int edadMinimaRecomendada;

    public Pelicula(String nombre, float duracionEnMinutos) {
        this.nombre = validarNombre(nombre);
        this.duracionEnMinutos = validarDuracion(duracionEnMinutos);
        this.genero = "OTRO"; // Valor por defecto
        this.director = "";
        this.anioEstreno = Year.now().getValue(); // Año actual por defecto
        this.sinopsis = "";
        this.esAptaTodoPublico = true; // Por defecto apta para todo público
        this.edadMinimaRecomendada = 0;
    }

    // Constructor completo
    public Pelicula(String nombre, float duracionEnMinutos, String genero, 
                    String director, int anioEstreno, boolean esAptaTodoPublico) {
        this.nombre = validarNombre(nombre);
        this.duracionEnMinutos = validarDuracion(duracionEnMinutos);
        this.genero = validarGenero(genero);
        this.director = validarDirector(director);
        this.anioEstreno = validarAnioEstreno(anioEstreno);
        this.sinopsis = "";
        this.esAptaTodoPublico = esAptaTodoPublico;
        this.edadMinimaRecomendada = esAptaTodoPublico ? 0 : 13; // Valor por defecto según aptitud
    }

    // Métodos de comportamiento específicos de una película

    /**
     * Cambia el nombre de la película.
     */
    public void cambiarNombre(String nuevoNombre) {
        this.nombre = validarNombre(nuevoNombre);
    }

    /**
     * Ajusta la duración de la película.
     */
    public void ajustarDuracion(float nuevaDuracion) {
        this.duracionEnMinutos = validarDuracion(nuevaDuracion);
    }

    /**
     * Cambia el género de la película.
     */
    public void cambiarGenero(String nuevoGenero) {
        this.genero = validarGenero(nuevoGenero);
    }

    /**
     * Establece el director de la película.
     */
    public void establecerDirector(String nuevoDirector) {
        this.director = validarDirector(nuevoDirector);
    }

    /**
     * Cambia el año de estreno.
     */
    public void cambiarAnioEstreno(int nuevoAnio) {
        this.anioEstreno = validarAnioEstreno(nuevoAnio);
    }

    /**
     * Establece la sinopsis de la película.
     */
    public void establecerSinopsis(String nuevaSinopsis) {
        if (nuevaSinopsis != null && nuevaSinopsis.trim().length() > 2000) {
            throw new IllegalArgumentException("La sinopsis no puede superar los 2000 caracteres");
        }
        this.sinopsis = nuevaSinopsis != null ? nuevaSinopsis.trim() : "";
    }

    /**
     * Configura si la película es apta para todo público.
     */
    public void configurarAptitudPublico(boolean aptaTodoPublico, int edadMinima) {
        this.esAptaTodoPublico = aptaTodoPublico;
        this.edadMinimaRecomendada = aptaTodoPublico ? 0 : validarEdadMinima(edadMinima);
    }

    // Métodos de consulta específicos de la película

    /**
     * Determina el tipo de película según su duración.
     */
    public TipoPelicula getTipoPorDuracion() {
        if (duracionEnMinutos <= LIMITE_CORTOMETRAJE) {
            return TipoPelicula.CORTOMETRAJE;
        } else if (duracionEnMinutos < LIMITE_LARGOMETRAJE) {
            return TipoPelicula.MEDIOMETRAJE;
        } else {
            return TipoPelicula.LARGOMETRAJE;
        }
    }

    /**
     * Verifica si es un cortometraje.
     */
    public boolean esCortometraje() {
        return getTipoPorDuracion() == TipoPelicula.CORTOMETRAJE;
    }

    /**
     * Verifica si es un largometraje.
     */
    public boolean esLargometraje() {
        return getTipoPorDuracion() == TipoPelicula.LARGOMETRAJE;
    }

    /**
     * Calcula la duración en horas y minutos.
     */
    public String getDuracionFormateada() {
        int horas = (int) (duracionEnMinutos / 60);
        int minutos = (int) (duracionEnMinutos % 60);
        
        if (horas == 0) {
            return minutos + " min";
        } else if (minutos == 0) {
            return horas + "h";
        } else {
            return horas + "h " + minutos + "min";
        }
    }

    /**
     * Verifica si es una película antigua (más de 30 años).
     */
    public boolean esPeliculaAntigua() {
        return (Year.now().getValue() - anioEstreno) > 30;
    }

    /**
     * Verifica si es una película reciente (menos de 5 años).
     */
    public boolean esPeliculaReciente() {
        return (Year.now().getValue() - anioEstreno) <= 5;
    }

    /**
     * Verifica si es una película clásica (más de 50 años).
     */
    public boolean esPeliculaClasica() {
        return (Year.now().getValue() - anioEstreno) > 50;
    }

    /**
     * Calcula la edad de la película en años.
     */
    public int getEdadEnAnios() {
        return Year.now().getValue() - anioEstreno;
    }

    /**
     * Verifica si es apta para una edad específica.
     */
    public boolean esAptaParaEdad(int edad) {
        return edad >= edadMinimaRecomendada;
    }

    /**
     * Verifica si requiere supervisión de adultos para menores.
     */
    public boolean requiereSupervisionAdultos() {
        return !esAptaTodoPublico && edadMinimaRecomendada > 0;
    }

    /**
     * Verifica si tiene información completa.
     */
    public boolean tieneInformacionCompleta() {
        return !nombre.isEmpty() && 
               !director.isEmpty() && 
               !genero.equals("OTRO") && 
               !sinopsis.isEmpty();
    }

    // Métodos de validación privados

    private String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la película no puede estar vacío");
        }
        
        String nombreLimpio = nombre.trim();
        if (nombreLimpio.length() < 1) {
            throw new IllegalArgumentException("El nombre de la película debe tener al menos 1 carácter");
        }
        
        if (nombreLimpio.length() > 200) {
            throw new IllegalArgumentException("El nombre de la película no puede superar los 200 caracteres");
        }
        
        return nombreLimpio;
    }

    private float validarDuracion(float duracion) {
        if (duracion < DURACION_MINIMA) {
            throw new IllegalArgumentException("La duración mínima de una película es " + DURACION_MINIMA + " minutos");
        }
        
        if (duracion > DURACION_MAXIMA) {
            throw new IllegalArgumentException("La duración máxima de una película es " + DURACION_MAXIMA + " minutos");
        }
        
        return duracion;
    }

    private String validarGenero(String genero) {
        if (genero == null || genero.trim().isEmpty()) {
            return "OTRO";
        }
        
        String generoNormalizado = genero.trim().toUpperCase();
        if (!GENEROS_VALIDOS.contains(generoNormalizado)) {
            throw new IllegalArgumentException("Género inválido. Géneros válidos: " + GENEROS_VALIDOS);
        }
        
        return generoNormalizado;
    }

    private String validarDirector(String director) {
        if (director == null) {
            return "";
        }
        
        String directorLimpio = director.trim();
        if (directorLimpio.length() > 100) {
            throw new IllegalArgumentException("El nombre del director no puede superar los 100 caracteres");
        }
        
        return directorLimpio;
    }

    private int validarAnioEstreno(int anio) {
        int anioActual = Year.now().getValue();
        if (anio < 1888) { // Año de la primera película
            throw new IllegalArgumentException("El año de estreno no puede ser anterior a 1888");
        }
        
        if (anio > anioActual + 5) { // Máximo 5 años en el futuro
            throw new IllegalArgumentException("El año de estreno no puede ser más de 5 años en el futuro");
        }
        
        return anio;
    }

    private int validarEdadMinima(int edad) {
        if (edad < 0 || edad > 21) {
            throw new IllegalArgumentException("La edad mínima recomendada debe estar entre 0 y 21 años");
        }
        return edad;
    }

    // Getters

    public String getNombre() {
        return nombre;
    }

    public float getDuracionEnMinutos() {
        return duracionEnMinutos;
    }

    public String getGenero() {
        return genero;
    }

    public String getDirector() {
        return director;
    }

    public int getAnioEstreno() {
        return anioEstreno;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public boolean isEsAptaTodoPublico() {
        return esAptaTodoPublico;
    }

    public int getEdadMinimaRecomendada() {
        return edadMinimaRecomendada;
    }

    // Equals, hashCode y toString

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Pelicula pelicula = (Pelicula) obj;
        return nombre.equals(pelicula.nombre) && 
               Float.compare(pelicula.duracionEnMinutos, duracionEnMinutos) == 0 &&
               anioEstreno == pelicula.anioEstreno;
    }

    @Override
    public int hashCode() {
        return nombre.hashCode() * 31 + Float.hashCode(duracionEnMinutos) * 17 + anioEstreno;
    }

    @Override
    public String toString() {
        return String.format("Pelicula{nombre='%s', duración=%s, género=%s, año=%d, director='%s'}",
                nombre, getDuracionFormateada(), genero, anioEstreno, director);
    }

    // Enum interno para tipos de película
    public enum TipoPelicula {
        CORTOMETRAJE,
        MEDIOMETRAJE, 
        LARGOMETRAJE
    }
}
