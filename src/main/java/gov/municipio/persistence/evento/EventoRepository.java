package gov.municipio.persistence.evento;

import gov.municipio.domain.model.evento.Evento;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de persistencia de la entidad Evento.
 */
public interface EventoRepository {
    
    /**
     * Guarda un evento. Si no tiene ID, lo persiste (INSERT).
     * Si ya tiene ID, lo actualiza (UPDATE).
     * 
     * @param evento el evento a guardar
     * @return el evento guardado con su ID asignado
     */
    Evento save(Evento evento);
    
    /**
     * Busca un evento por su ID.
     * 
     * @param id el ID del evento
     * @return Optional con el evento si existe, vacío si no existe
     */
    Optional<Evento> findById(Long id);
    
    /**
     * Busca un evento por su ID cargando las participaciones de forma eager.
     * 
     * @param id el ID del evento
     * @return Optional con el evento si existe, vacío si no existe
     */
    Optional<Evento> findByIdWithParticipaciones(Long id);
    
    /**
     * Obtiene todos los eventos registrados.
     * 
     * @return lista de todos los eventos
     */
    List<Evento> findAll();
    
    /**
     * Elimina un evento.
     * 
     * @param evento el evento a eliminar
     */
    void delete(Evento evento);
}
