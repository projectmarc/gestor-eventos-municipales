package gov.municipio.persistence.artistabanda;

import gov.municipio.domain.model.ArtistaBanda;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import gov.municipio.persistence.JpaUtil;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA del repositorio de artistas/bandas.
 */
public class JpaArtistaBandaRepository implements ArtistaBandaRepository {
    
    @Override
    public ArtistaBanda save(ArtistaBanda artistaBanda) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            if (artistaBanda.getId() == null) {
                // Nuevo artista/banda
                em.persist(artistaBanda);
            } else {
                // Actualizar artista/banda existente
                artistaBanda = em.merge(artistaBanda);
            }
            
            em.getTransaction().commit();
            return artistaBanda;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar artista/banda", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<ArtistaBanda> findById(Long id) {
        if (id == null) return Optional.empty();
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            ArtistaBanda artistaBanda = em.find(ArtistaBanda.class, id);
            return Optional.ofNullable(artistaBanda);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<ArtistaBanda> findByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return Optional.empty();
        }
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<ArtistaBanda> query = em.createQuery(
                "SELECT a FROM ArtistaBanda a WHERE a.nombre = :nombre", 
                ArtistaBanda.class
            );
            query.setParameter("nombre", nombre.trim());
            
            return Optional.of(query.getSingleResult());
            
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ArtistaBanda> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<ArtistaBanda> query = em.createQuery(
                "SELECT a FROM ArtistaBanda a ORDER BY a.nombre", 
                ArtistaBanda.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteById(Long id) {
        if (id == null) return;
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            ArtistaBanda artistaBanda = em.find(ArtistaBanda.class, id);
            if (artistaBanda != null) {
                em.remove(artistaBanda);
            }
            
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar artista/banda", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM ArtistaBanda a WHERE a.id = :id", 
                Long.class
            );
            query.setParameter("id", id);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}
