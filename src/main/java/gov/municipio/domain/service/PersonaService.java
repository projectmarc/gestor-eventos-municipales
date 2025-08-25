package gov.municipio.domain.service;

import gov.municipio.domain.model.Persona;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class PersonaService {
    private static PersonaService instance;
    private EntityManagerFactory emf;

    private PersonaService() {
        emf = Persistence.createEntityManagerFactory("eventos-municipales-pu");
    }

    public static PersonaService getInstance() {
        if (instance == null) {
            instance = new PersonaService();
        }
        return instance;
    }

    public Persona guardar(Persona persona) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            // Validar que no exista otra persona con el mismo DNI
            if (persona.getId() == null && existeDni(persona.getDni())) {
                throw new IllegalArgumentException("Ya existe una persona con DNI: " + persona.getDni());
            }
            
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

    public Optional<Persona> buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Persona persona = em.find(Persona.class, id);
            return Optional.ofNullable(persona);
        } finally {
            em.close();
        }
    }

    public Optional<Persona> buscarPorDni(int dni) {
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

    public List<Persona> listarTodas() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Persona> query = em.createQuery("SELECT p FROM Persona p ORDER BY p.apellido, p.nombre", Persona.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Persona> buscarPorNombre(String nombre) {
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

    public void eliminar(Long id) {
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

    private boolean existeDni(int dni) {
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

    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
