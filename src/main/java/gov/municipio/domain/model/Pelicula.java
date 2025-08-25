package gov.municipio.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "peliculas")
public class Pelicula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private float duracionEnMinutos;
    
    private String director;
    private String genero;
    private String sinopsis;

    public Pelicula() {}

    public Pelicula(String nombre, float duracionEnMinutos) {
        this.nombre = nombre;
        this.duracionEnMinutos = duracionEnMinutos;
    }

    public Pelicula(String nombre, float duracionEnMinutos, String director, String genero, String sinopsis) {
        this.nombre = nombre;
        this.duracionEnMinutos = duracionEnMinutos;
        this.director = director;
        this.genero = genero;
        this.sinopsis = sinopsis;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getDuracionEnMinutos() {
        return duracionEnMinutos;
    }

    public void setDuracionEnMinutos(float duracionEnMinutos) {
        this.duracionEnMinutos = duracionEnMinutos;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    @Override
    public String toString() {
        return nombre + " (" + duracionEnMinutos + " min)";
    }
}
