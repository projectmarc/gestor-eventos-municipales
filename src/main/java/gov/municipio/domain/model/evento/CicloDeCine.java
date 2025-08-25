package gov.municipio.domain.model.evento;

import gov.municipio.domain.model.Pelicula;
import gov.municipio.domain.model.enums.Rol;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Entity
@Table(name = "ciclos_cine")
public class CicloDeCine extends Evento {
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ciclo_cine_id")
    @OrderColumn(name = "orden_proyeccion")
    private List<Pelicula> peliculas = new ArrayList<>();
    
    @Column(nullable = false)
    private boolean incluyeCharlaPosteriores;

    public CicloDeCine() {}

    public CicloDeCine(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, 
                       List<Pelicula> peliculas, boolean incluyeCharlaPosteriores) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.peliculas = peliculas != null ? peliculas : new ArrayList<>();
        this.incluyeCharlaPosteriores = incluyeCharlaPosteriores;
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ORGANIZADOR || rol == Rol.ASISTENTE;
    }

    public void agregarPelicula(Pelicula pelicula) {
        if (pelicula != null) {
            peliculas.add(pelicula);
        }
    }

    public void removerPelicula(Pelicula pelicula) {
        peliculas.remove(pelicula);
    }

    public void reordenarPeliculas(List<Pelicula> nuevoOrden) {
        if (nuevoOrden != null) {
            this.peliculas = new ArrayList<>(nuevoOrden);
        }
    }

    // Getters y Setters
    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(List<Pelicula> peliculas) {
        this.peliculas = peliculas != null ? peliculas : new ArrayList<>();
    }

    public boolean isIncluyeCharlaPosteriores() {
        return incluyeCharlaPosteriores;
    }

    public void setIncluyeCharlaPosteriores(boolean incluyeCharlaPosteriores) {
        this.incluyeCharlaPosteriores = incluyeCharlaPosteriores;
    }

    public String getOrdenProyeccion() {
        return peliculas.stream()
                .map(Pelicula::getNombre)
                .reduce((p1, p2) -> p1 + ", " + p2)
                .orElse("Sin películas");
    }

    public float getDuracionTotalPeliculas() {
        return (float) peliculas.stream()
                .mapToDouble(Pelicula::getDuracionEnMinutos)
                .sum();
    }
}
