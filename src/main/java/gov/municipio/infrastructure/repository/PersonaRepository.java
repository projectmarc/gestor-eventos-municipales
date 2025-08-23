package gov.municipio.infrastructure.repository;

import gov.municipio.domain.model.Persona;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de persistencia de Persona
 * Implementa el patrón Repository para separar la lógica de acceso a datos
 */
public class PersonaRepository {
    private static PersonaRepository instance;
    private EntityManagerFactory emf;

    private PersonaRepository() {
        emf = Persistence.createEntityManagerFactory("eventos-municipales-pu");
    }

    public static PersonaRepository getInstance() {
        if (instance == null) {
            instance = new PersonaRepository();
        }
        return instance;
    }

    public Persona save(Persona persona) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            if (persona.getId() == null) {
                em.persist(persona);
            } else {
                persona = em.merge(persona);
            }
            
            em.getTransaction().commit();
            return persona;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<Persona> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Persona persona = em.find(Persona.class, id);
            return Optional.ofNullable(persona);
        } finally {
            em.close();
        }
    }

    public Optional<Persona> findByDni(int dni) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Persona> query = em.createQuery(
                "SELECT p FROM Persona p WHERE p.dni = :dni", Persona.class);
            query.setParameter("dni", dni);
            List<Persona> resultados = query.getResultList();
            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
        } finally {
            em.close();
        }
    }

    public List<Persona> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Persona> query = em.createQuery("SELECT p FROM Persona p ORDER BY p.apellido, p.nombre", Persona.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Persona> findByName(String nombre) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Persona> query = em.createQuery(
                "SELECT p FROM Persona p WHERE LOWER(p.nombre) LIKE LOWER(:nombre) OR LOWER(p.apellido) LIKE LOWER(:nombre)", 
                Persona.class);
            query.setParameter("nombre", "%" + nombre + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existsByDni(int dni) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(p) FROM Persona p WHERE p.dni = :dni", Long.class);
            query.setParameter("dni", dni);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Persona persona = em.find(Persona.class, id);
            if (persona != null) {
                em.remove(persona);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
