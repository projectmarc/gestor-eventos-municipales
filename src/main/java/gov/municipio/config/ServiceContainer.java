package gov.municipio.config;

import gov.municipio.application.PersonaService;
import gov.municipio.persistence.persona.JpaPersonaRepository;
import gov.municipio.persistence.persona.PersonaRepository;

public class ServiceContainer {
    private final PersonaService personaService;

    public ServiceContainer() {
        // Configuración manual de dependencias (sin framework DI)
        PersonaRepository personaRepository = new JpaPersonaRepository();
        this.personaService = new PersonaService(personaRepository);
    }

    public PersonaService personaService() { 
        return personaService; 
    }
}
