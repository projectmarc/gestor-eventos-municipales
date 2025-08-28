package gov.municipio.application;

import gov.municipio.domain.model.Pelicula;
import gov.municipio.persistence.pelicula.PeliculaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para el caso de uso de gestión de películas.
 * Orquesta las operaciones de dominio y persistencia.
 */
public class PeliculaService {
    
    private final PeliculaRepository peliculaRepository;
    
    /**
     * Constructor que recibe el repositorio por inyección de dependencias manual.
     * 
     * @param peliculaRepository el repositorio de películas
     */
    public PeliculaService(PeliculaRepository peliculaRepository) {
        if (peliculaRepository == null) {
            throw new IllegalArgumentException("PeliculaRepository no puede ser nulo");
        }
        this.peliculaRepository = peliculaRepository;
    }
    
    /**
     * Registra una nueva película en el sistema.
     * Valida que el nombre no esté duplicado antes de persistir.
     * 
     * @param nombre el nombre de la película
     * @param duracionEnMinutos la duración en minutos de la película
     * @return la película registrada con su ID asignado
     * @throws IllegalStateException si ya existe una película con el mismo nombre
     * @throws IllegalArgumentException si algún parámetro no es válido
     */
    public Pelicula registrarPelicula(String nombre, int duracionEnMinutos) {
        // Verificar que el nombre no esté duplicado
        if (peliculaRepository.existsByNombre(nombre)) {
            throw new IllegalStateException("Ya existe una película registrada con el nombre: " + nombre);
        }
        
        // Crear la película (las validaciones se ejecutan en el constructor del dominio)
        Pelicula nuevaPelicula = new Pelicula(nombre, duracionEnMinutos);
        
        // Persistir y retornar la película con su ID asignado
        return peliculaRepository.save(nuevaPelicula);
    }
    
    /**
     * Busca una película por su ID.
     * 
     * @param id el ID de la película
     * @return Optional con la película si existe
     */
    public Optional<Pelicula> buscarPeliculaPorId(Long id) {
        return peliculaRepository.findById(id);
    }
    
    /**
     * Busca una película por su nombre.
     * 
     * @param nombre el nombre de la película
     * @return Optional con la película si existe
     */
    public Optional<Pelicula> buscarPeliculaPorNombre(String nombre) {
        return peliculaRepository.findByNombre(nombre);
    }
    
    /**
     * Obtiene todas las películas registradas.
     * 
     * @return lista de todas las películas
     */
    public List<Pelicula> obtenerTodasLasPeliculas() {
        return peliculaRepository.findAll();
    }
    
    /**
     * Actualiza los datos de una película existente.
     * 
     * @param id el ID de la película a actualizar
     * @param nuevoNombre el nuevo nombre (puede ser null para no cambiar)
     * @param nuevaDuracionEnMinutos la nueva duración en minutos (puede ser null para no cambiar)
     * @return la película actualizada
     * @throws IllegalStateException si la película no existe
     */
    public Pelicula actualizarPelicula(Long id, String nuevoNombre, Integer nuevaDuracionEnMinutos) {
        Optional<Pelicula> peliculaOpt = peliculaRepository.findById(id);
        if (peliculaOpt.isEmpty()) {
            throw new IllegalStateException("No existe una película con el ID: " + id);
        }
        
        Pelicula pelicula = peliculaOpt.get();
        
        // Actualizar solo los campos que no son nulos
        if (nuevoNombre != null) {
            // Verificar duplicado solo si el nombre cambió
            if (!pelicula.getNombre().equals(nuevoNombre.trim()) && 
                peliculaRepository.existsByNombre(nuevoNombre)) {
                throw new IllegalStateException("Ya existe una película con el nombre: " + nuevoNombre);
            }
            pelicula.cambiarNombre(nuevoNombre);
        }
        if (nuevaDuracionEnMinutos != null) {
            pelicula.cambiarDuracion(nuevaDuracionEnMinutos);
        }
        
        return peliculaRepository.save(pelicula);
    }
    
    /**
     * Elimina una película por su ID.
     * 
     * @param id el ID de la película a eliminar
     * @throws IllegalStateException si la película no existe
     */
    public void eliminarPelicula(Long id) {
        Optional<Pelicula> peliculaOpt = peliculaRepository.findById(id);
        if (peliculaOpt.isEmpty()) {
            throw new IllegalStateException("No existe una película con el ID: " + id);
        }
        
        peliculaRepository.delete(peliculaOpt.get());
    }
}
