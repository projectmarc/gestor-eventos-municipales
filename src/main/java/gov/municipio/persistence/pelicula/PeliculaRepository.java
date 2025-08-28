package gov.municipio.persistence.pelicula;

import gov.municipio.domain.model.Pelicula;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de persistencia de la entidad Pelicula.
 */
public interface PeliculaRepository {
    
    /**
     * Guarda una película. Si no tiene ID, la persiste (INSERT).
     * Si ya tiene ID, la actualiza (UPDATE).
     * 
     * @param pelicula la película a guardar
     * @return la película guardada con su ID asignado
     */
    Pelicula save(Pelicula pelicula);
    
    /**
     * Busca una película por su ID.
     * 
     * @param id el ID de la película
     * @return Optional con la película si existe, vacío si no existe
     */
    Optional<Pelicula> findById(Long id);
    
    /**
     * Busca una película por su nombre.
     * 
     * @param nombre el nombre de la película
     * @return Optional con la película si existe, vacío si no existe
     */
    Optional<Pelicula> findByNombre(String nombre);
    
    /**
     * Verifica si existe una película con el nombre especificado.
     * 
     * @param nombre el nombre a verificar
     * @return true si existe una película con ese nombre, false en caso contrario
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Obtiene todas las películas registradas.
     * 
     * @return lista de todas las películas
     */
    List<Pelicula> findAll();
    
    /**
     * Elimina una película.
     * 
     * @param pelicula la película a eliminar
     */
    void delete(Pelicula pelicula);
}
