package gov.municipio.persistence.persona;

import gov.municipio.domain.model.Persona;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de persistencia de la entidad Persona.
 */
public interface PersonaRepository {
    
    /**
     * Guarda una persona. Si no tiene ID, la persiste (INSERT).
     * Si ya tiene ID, la actualiza (UPDATE).
     * 
     * @param persona la persona a guardar
     * @return la persona guardada con su ID asignado
     */
    Persona save(Persona persona);
    
    /**
     * Busca una persona por su ID.
     * 
     * @param id el ID de la persona
     * @return Optional con la persona si existe, vacío si no existe
     */
    Optional<Persona> findById(Long id);
    
    /**
     * Busca una persona por su DNI.
     * 
     * @param dni el DNI de la persona
     * @return Optional con la persona si existe, vacío si no existe
     */
    Optional<Persona> findByDni(String dni);
    
    /**
     * Verifica si existe una persona con el DNI especificado.
     * 
     * @param dni el DNI a verificar
     * @return true si existe una persona con ese DNI, false en caso contrario
     */
    boolean existsByDni(String dni);
    
    /**
     * Obtiene todas las personas registradas.
     * 
     * @return lista de todas las personas
     */
    List<Persona> findAll();
    
    /**
     * Elimina una persona.
     * 
     * @param persona la persona a eliminar
     */
    void delete(Persona persona);
}
