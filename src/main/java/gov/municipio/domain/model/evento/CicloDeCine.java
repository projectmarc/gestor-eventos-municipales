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

    public CicloDeCine(String nombre, LocalDate fechaInicio, int duracionEstimadaHoras, boolean requiereInscripcion, List<Pelicula> peliculas, boolean incluyeCharlaPosteriores) {
        super(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion);
        this.peliculas = peliculas != null ? peliculas : new ArrayList<>();
        this.incluyeCharlaPosteriores = incluyeCharlaPosteriores;
    }

    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.ORGANIZADOR || rol == Rol.ASISTENTE;
    }

    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    public boolean isIncluyeCharlaPosteriores() {
        return incluyeCharlaPosteriores;
    }
}
