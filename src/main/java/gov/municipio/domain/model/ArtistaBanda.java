package gov.municipio.domain.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Representa un artista o banda que puede participar en conciertos.
 * Entidad simple con validaciones básicas de dominio.
 */
@Entity
@Table(name = "artista_banda")
public class ArtistaBanda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;
    
    protected ArtistaBanda() {
        // Constructor protegido para JPA
    }
    
    public ArtistaBanda(String nombre) {
        this.nombre = validarNombre(nombre);
    }
    
    // -------- Métodos de dominio
    
    public void cambiarNombre(String nuevoNombre) {
        this.nombre = validarNombre(nuevoNombre);
    }
    
    // -------- Getters
    
    public Long getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    // -------- Validaciones privadas
    
    private String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del artista/banda no puede ser nulo ni vacío");
        }
        String nombreLimpio = nombre.trim();
        if (nombreLimpio.length() > 100) {
            throw new IllegalArgumentException("El nombre del artista/banda no puede exceder 100 caracteres");
        }
        return nombreLimpio;
    }
    
    // -------- equals, hashCode, toString
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtistaBanda that)) return false;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("ArtistaBanda{id=%d, nombre='%s'}", id, nombre);
    }
}
