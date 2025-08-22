package gov.municipio.domain.model;

public class Pelicula {
   private String nombre;
   private float duracionEnMinutos;

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
