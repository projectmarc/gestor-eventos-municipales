package gov.municipio.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utilidad para manejar la creación de EntityManager para JPA.
 * Implementa el patrón Singleton para el EntityManagerFactory.
 */
public class JpaUtil {
    
    private static final String PERSISTENCE_UNIT_NAME = "municipioPU";
    private static EntityManagerFactory entityManagerFactory;
    
    // Constructor privado para evitar instanciación
    private JpaUtil() {
        throw new AssertionError("Esta clase no debe ser instanciada");
    }
    
    /**
     * Obtiene la instancia del EntityManagerFactory de forma lazy.
     * 
     * @return EntityManagerFactory configurado
     */
    private static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            try {
                entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            } catch (Exception e) {
                System.err.println("Error al crear EntityManagerFactory: " + e.getMessage());
                throw new RuntimeException("No se pudo inicializar JPA", e);
            }
        }
        return entityManagerFactory;
    }
    
    /**
     * Crea un nuevo EntityManager.
     * Es responsabilidad del llamador cerrar el EntityManager.
     * 
     * @return EntityManager nuevo
     */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    /**
     * Cierra el EntityManagerFactory.
     * Debe llamarse al finalizar la aplicación.
     */
    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }
}
