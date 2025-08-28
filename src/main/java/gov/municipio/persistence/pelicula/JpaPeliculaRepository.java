package gov.municipio.persistence.pelicula;

import gov.municipio.domain.model.Pelicula;
import gov.municipio.persistence.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA del repositorio de Pelicula.
 */
public class JpaPeliculaRepository implements PeliculaRepository {
    
    @Override
    public Pelicula save(Pelicula pelicula) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            Pelicula result;
            if (pelicula.getId() == null) {
                // Entidad nueva - persist
                em.persist(pelicula);
                result = pelicula;
            } else {
                // Entidad existente - merge
                result = em.merge(pelicula);
            }
            
            em.getTransaction().commit();
            return result;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la película: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<Pelicula> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Pelicula pelicula = em.find(Pelicula.class, id);
            return Optional.ofNullable(pelicula);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<Pelicula> findByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return Optional.empty();
        }
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Pelicula> query = em.createQuery(
                "SELECT p FROM Pelicula p WHERE p.nombre = :nombre", 
                Pelicula.class
            );
            query.setParameter("nombre", nombre.trim());
            
            try {
                Pelicula pelicula = query.getSingleResult();
                return Optional.of(pelicula);
            } catch (NoResultException e) {
                return Optional.empty();
            }
            
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean existsByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(p) FROM Pelicula p WHERE p.nombre = :nombre", 
                Long.class
            );
            query.setParameter("nombre", nombre.trim());
            
            Long count = query.getSingleResult();
            return count > 0;
            
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Pelicula> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Pelicula> query = em.createQuery(
                "SELECT p FROM Pelicula p ORDER BY p.nombre", 
                Pelicula.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public void delete(Pelicula pelicula) {
        if (pelicula == null || pelicula.getId() == null) {
            throw new IllegalArgumentException("No se puede eliminar una película nula o sin ID");
        }
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // Primero buscamos la entidad para asegurarnos de que está en el contexto
            Pelicula peliculaToDelete = em.find(Pelicula.class, pelicula.getId());
            if (peliculaToDelete != null) {
                em.remove(peliculaToDelete);
            }
            
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar la película: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
