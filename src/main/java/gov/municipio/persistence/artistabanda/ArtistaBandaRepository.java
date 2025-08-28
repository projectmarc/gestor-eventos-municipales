package gov.municipio.persistence.artistabanda;

import gov.municipio.domain.model.ArtistaBanda;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar la persistencia de artistas/bandas.
 */
public interface ArtistaBandaRepository {
    
    /**
     * Guarda o actualiza un artista/banda.
     * 
     * @param artistaBanda el artista/banda a guardar
     * @return el artista/banda guardado con ID asignado
     */
    ArtistaBanda save(ArtistaBanda artistaBanda);
    
    /**
     * Busca un artista/banda por su ID.
     * 
     * @param id el ID del artista/banda
     * @return Optional con el artista/banda si existe
     */
    Optional<ArtistaBanda> findById(Long id);
    
    /**
     * Busca un artista/banda por su nombre.
     * 
     * @param nombre el nombre del artista/banda
     * @return Optional con el artista/banda si existe
     */
    Optional<ArtistaBanda> findByNombre(String nombre);
    
    /**
     * Obtiene todos los artistas/bandas.
     * 
     * @return lista de todos los artistas/bandas
     */
    List<ArtistaBanda> findAll();
    
    /**
     * Elimina un artista/banda por su ID.
     * 
     * @param id el ID del artista/banda a eliminar
     */
    void deleteById(Long id);
    
    /**
     * Verifica si existe un artista/banda con el ID dado.
     * 
     * @param id el ID a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Long id);
}
