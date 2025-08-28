package gov.municipio.persistence.evento;

import gov.municipio.domain.model.evento.Evento;
import gov.municipio.persistence.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA del repositorio de eventos.
 */
public class EventoRepositoryImpl implements EventoRepository {

    @Override
    public Evento save(Evento evento) {
        EntityManager em = JpaUtil.getEntityManager();
        
        try {
            em.getTransaction().begin();
            
            Evento eventoGuardado;
            if (evento.getId() == null) {
                // Nuevo evento - persist
                em.persist(evento);
                eventoGuardado = evento;
            } else {
                // Evento existente - merge
                eventoGuardado = em.merge(evento);
            }
            
            em.getTransaction().commit();
            return eventoGuardado;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar el evento", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Evento> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        
        EntityManager em = JpaUtil.getEntityManager();
        
        try {
            Evento evento = em.find(Evento.class, id);
            return Optional.ofNullable(evento);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Evento> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        
        try {
            TypedQuery<Evento> query = em.createQuery("SELECT e FROM Evento e ORDER BY e.fechaInicio", Evento.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Evento evento) {
        if (evento == null || evento.getId() == null) {
            return;
        }
        
        EntityManager em = JpaUtil.getEntityManager();
        
        try {
            em.getTransaction().begin();
            
            // Buscar la entidad gestionada
            Evento eventoGestionado = em.find(Evento.class, evento.getId());
            if (eventoGestionado != null) {
                em.remove(eventoGestionado);
            }
            
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar el evento", e);
        } finally {
            em.close();
        }
    }
}
