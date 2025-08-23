package gov.municipio.infrastructure.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Configuración centralizada para la persistencia JPA
 * Implementa el patrón Singleton para el EntityManagerFactory
 */
public class PersistenceConfig {
    private static PersistenceConfig instance;
    private EntityManagerFactory entityManagerFactory;
    
    private static final String PERSISTENCE_UNIT_NAME = "eventos-municipales-pu";
    
    private PersistenceConfig() {
        initializeEntityManagerFactory();
    }
    
    public static PersistenceConfig getInstance() {
        if (instance == null) {
            synchronized (PersistenceConfig.class) {
                if (instance == null) {
                    instance = new PersistenceConfig();
                }
            }
        }
        return instance;
    }
    
    private void initializeEntityManagerFactory() {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            System.err.println("Error al inicializar EntityManagerFactory: " + e.getMessage());
            throw new RuntimeException("No se pudo inicializar la persistencia", e);
        }
    }
    
    public EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            initializeEntityManagerFactory();
        }
        return entityManagerFactory;
    }
    
    public void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
    
    /**
     * Hook para el shutdown de la aplicación
     */
    public void shutdown() {
        close();
    }
}
