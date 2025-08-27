package gov.municipio.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "peliculas")
@Access(AccessType.FIELD)
public class Pelicula {
    private static final float DURACION_MINIMA = 1.0f;
    private static final float DURACION_MAXIMA = 180.0f; // 3 horas máximo
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 150)
    private String nombre;
    
    @Column(nullable = false)
    private float duracionEnMinutos;

    // Constructor por defecto protegido para JPA
    protected Pelicula() {
        // JPA necesita un constructor por defecto
    }

    public Pelicula(String nombre, float duracionEnMinutos) {
        this.nombre = validarNombre(nombre);
        this.duracionEnMinutos = validarDuracion(duracionEnMinutos);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public float getDuracionEnMinutos() {
        return duracionEnMinutos;
    }

    // Métodos de utilidad
    public String getDuracionFormateada() {
        int horas = (int) (duracionEnMinutos / 60);
        int minutos = (int) (duracionEnMinutos % 60);
        
        if (horas > 0) {
            return String.format("%dh %dmin", horas, minutos);
        } else {
            return String.format("%dmin", minutos);
        }
    }

    // Métodos de validación privados
    private String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la película no puede ser nulo ni vacío");
        }
        if (nombre.trim().length() > 150) {
            throw new IllegalArgumentException("El nombre de la película no puede exceder los 150 caracteres");
        }
        return nombre.trim();
    }

    private float validarDuracion(float duracion) {
        if (duracion < DURACION_MINIMA) {
            throw new IllegalArgumentException("La duración debe ser al menos " + DURACION_MINIMA + " minutos");
        }
        if (duracion > DURACION_MAXIMA) {
            throw new IllegalArgumentException("La duración no puede exceder los " + DURACION_MAXIMA + " minutos");
        }
        return duracion;
    }

    @Override
    public String toString() {
        return String.format("Pelicula{id=%d, nombre='%s', duracion=%s}",
                id, nombre, getDuracionFormateada());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Pelicula pelicula = (Pelicula) obj;
        return nombre.equals(pelicula.nombre) && 
               Float.compare(pelicula.duracionEnMinutos, duracionEnMinutos) == 0;
    }

    @Override
    public int hashCode() {
        return nombre.hashCode() + Float.hashCode(duracionEnMinutos);
    }
}
