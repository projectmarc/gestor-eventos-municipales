package gov.municipio.persistence.persona;

import gov.municipio.domain.model.Persona;
import gov.municipio.persistence.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA del repositorio de Persona.
 */
public class JpaPersonaRepository implements PersonaRepository {
    
    @Override
    public Persona save(Persona persona) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            Persona result;
            if (persona.getId() == null) {
                // Entidad nueva - persist
                em.persist(persona);
                result = persona;
            } else {
                // Entidad existente - merge
                result = em.merge(persona);
            }
            
            em.getTransaction().commit();
            return result;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la persona: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<Persona> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Persona persona = em.find(Persona.class, id);
            return Optional.ofNullable(persona);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<Persona> findByDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return Optional.empty();
        }
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Persona> query = em.createQuery(
                "SELECT p FROM Persona p WHERE p.dni = :dni", 
                Persona.class
            );
            query.setParameter("dni", dni.trim());
            
            try {
                Persona persona = query.getSingleResult();
                return Optional.of(persona);
            } catch (NoResultException e) {
                return Optional.empty();
            }
            
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean existsByDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return false;
        }
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(p) FROM Persona p WHERE p.dni = :dni", 
                Long.class
            );
            query.setParameter("dni", dni.trim());
            
            Long count = query.getSingleResult();
            return count > 0;
            
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Persona> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Persona> query = em.createQuery(
                "SELECT p FROM Persona p ORDER BY p.apellido, p.nombre", 
                Persona.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public void delete(Persona persona) {
        if (persona == null || persona.getId() == null) {
            throw new IllegalArgumentException("No se puede eliminar una persona nula o sin ID");
        }
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // Primero buscamos la entidad para asegurarnos de que está en el contexto
            Persona personaToDelete = em.find(Persona.class, persona.getId());
            if (personaToDelete != null) {
                em.remove(personaToDelete);
            }
            
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar la persona: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
