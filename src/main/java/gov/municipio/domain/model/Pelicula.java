package gov.municipio.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "peliculas", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
@Access(AccessType.FIELD)
public class Pelicula {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 120)
    private String nombre;
    
    @Column(nullable = false)
    private int duracionEnMinutos;

    // Constructor por defecto protegido para JPA
    protected Pelicula() {
        // JPA necesita un constructor por defecto
    }

    public Pelicula(String nombre, int duracionEnMinutos) {
        this.nombre = validarNombre(nombre);
        this.duracionEnMinutos = validarDuracionEnMinutos(duracionEnMinutos);
    }

    // Getters
    public Long getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }

    public int getDuracionEnMinutos() {
        return duracionEnMinutos;
    }

    // Métodos de comportamiento para actualizar datos
    public void cambiarNombre(String nuevoNombre) {
        this.nombre = validarNombre(nuevoNombre);
    }

    public void cambiarDuracion(int nuevaDuracionEnMinutos) {
        this.duracionEnMinutos = validarDuracionEnMinutos(nuevaDuracionEnMinutos);
    }

    // Métodos de validación privados
    private String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacío");
        }
        return nombre.trim();
    }

    private int validarDuracionEnMinutos(int duracionEnMinutos) {
        if (duracionEnMinutos <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a 0 minutos");
        }
        return duracionEnMinutos;
    }

    // Métodos de utilidad para conversión
    public double getDuracionEnHoras() {
        return duracionEnMinutos / 60.0;
    }

    public String getDuracionFormateada() {
        int horas = duracionEnMinutos / 60;
        int minutos = duracionEnMinutos % 60;
        return String.format("%d:%02d", horas, minutos);
    }

    @Override
    public String toString() {
        return String.format("Pelicula{id=%d, nombre='%s', duracionEnMinutos=%d}",
                id, nombre, duracionEnMinutos);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Pelicula pelicula = (Pelicula) obj;
        return nombre.equals(pelicula.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.hashCode();
    }
}
