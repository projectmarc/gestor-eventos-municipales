package gov.municipio.domain.service;

import gov.municipio.domain.model.Participacion;
import gov.municipio.domain.model.Persona;
import gov.municipio.domain.model.evento.Evento;
import gov.municipio.domain.model.enums.EstadoEvento;
import gov.municipio.domain.model.enums.Rol;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EventoService {
    private static EventoService instance;
    private EntityManagerFactory emf;

    private EventoService() {
        emf = Persistence.createEntityManagerFactory("eventos-municipales-pu");
    }

    public static EventoService getInstance() {
        if (instance == null) {
            instance = new EventoService();
        }
        return instance;
    }

    public Evento guardar(Evento evento) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            if (evento.getId() == null) {
                em.persist(evento);
            } else {
                evento = em.merge(evento);
            }
            
            em.getTransaction().commit();
            return evento;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<Evento> buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Evento evento = em.find(Evento.class, id);
            if (evento != null) {
                // Cargar participaciones lazy
                evento.getParticipaciones().size();
            }
            return Optional.ofNullable(evento);
        } finally {
            em.close();
        }
    }

    public List<Evento> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                "SELECT DISTINCT e FROM Evento e LEFT JOIN FETCH e.participaciones p LEFT JOIN FETCH p.persona ORDER BY e.fechaInicio DESC", Evento.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Evento> buscarPorEstado(EstadoEvento estado) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                "SELECT e FROM Evento e WHERE e.estado = :estado ORDER BY e.fechaInicio", Evento.class);
            query.setParameter("estado", estado);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Evento> buscarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                "SELECT e FROM Evento e WHERE e.fechaInicio BETWEEN :fechaInicio AND :fechaFin ORDER BY e.fechaInicio", 
                Evento.class);
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Evento> buscarPorNombre(String nombre) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                "SELECT e FROM Evento e WHERE LOWER(e.nombre) LIKE LOWER(:nombre)", Evento.class);
            query.setParameter("nombre", "%" + nombre + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Participacion inscribirPersona(Long eventoId, Long personaId, Rol rol) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            Evento evento = em.find(Evento.class, eventoId);
            Persona persona = em.find(Persona.class, personaId);
            
            if (evento == null) {
                throw new IllegalArgumentException("Evento no encontrado");
            }
            if (persona == null) {
                throw new IllegalArgumentException("Persona no encontrada");
            }

            // Verificar si la persona ya está inscrita en este evento con este rol
            TypedQuery<Long> queryExistente = em.createQuery(
                "SELECT COUNT(p) FROM Participacion p WHERE p.evento.id = :eventoId AND p.persona.id = :personaId AND p.rol = :rol", 
                Long.class);
            queryExistente.setParameter("eventoId", eventoId);
            queryExistente.setParameter("personaId", personaId);
            queryExistente.setParameter("rol", rol);
            
            if (queryExistente.getSingleResult() > 0) {
                throw new IllegalArgumentException("La persona ya está inscrita en este evento con el rol " + rol);
            }

            Participacion participacion = evento.inscribirPersona(persona, rol);
            em.persist(participacion);
            
            em.getTransaction().commit();
            return participacion;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void cambiarEstado(Long eventoId, EstadoEvento nuevoEstado) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            Evento evento = em.find(Evento.class, eventoId);
            if (evento == null) {
                throw new IllegalArgumentException("Evento no encontrado");
            }
            
            evento.cambiarEstado(nuevoEstado);
            em.merge(evento);
            
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Participacion> listarParticipaciones(Long eventoId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Participacion> query = em.createQuery(
                "SELECT p FROM Participacion p JOIN FETCH p.persona WHERE p.evento.id = :eventoId ORDER BY p.rol, p.persona.apellido", 
                Participacion.class);
            query.setParameter("eventoId", eventoId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Participacion> listarParticipacionesPorRol(Long eventoId, Rol rol) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Participacion> query = em.createQuery(
                "SELECT p FROM Participacion p JOIN FETCH p.persona WHERE p.evento.id = :eventoId AND p.rol = :rol ORDER BY p.persona.apellido", 
                Participacion.class);
            query.setParameter("eventoId", eventoId);
            query.setParameter("rol", rol);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Evento evento = em.find(Evento.class, id);
            if (evento != null) {
                em.remove(evento);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    public List<Evento> listarEventosConParticipaciones() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                "SELECT DISTINCT e FROM Evento e " +
                "LEFT JOIN FETCH e.participaciones p " +
                "LEFT JOIN FETCH p.persona " +
                "ORDER BY e.fechaInicio DESC", Evento.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
