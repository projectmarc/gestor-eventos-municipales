package gov.municipio.config;

import gov.municipio.application.PersonaService;
// Con JPA, acá también se crearian EntityManagerFactory, repositorios, etc

public class ServiceContainer {
    private final PersonaService personaService;

    public ServiceContainer() {
        // ⚠️ En el futuro: pasale repos/JPA/DAO al servicio
        this.personaService = new PersonaService();
    }

    public PersonaService personaService() { return personaService; }
}
