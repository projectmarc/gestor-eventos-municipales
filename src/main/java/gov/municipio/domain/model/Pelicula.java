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

   public Pelicula() {}


   public Pelicula(String nombre, float duracionEnMinutos) {
       this.nombre = nombre;
       this.duracionEnMinutos = duracionEnMinutos;
   }

   public String getNombre() {
       return nombre;
   }

   public float getDuracionEnMinutos() {
       return duracionEnMinutos;
   }
}
