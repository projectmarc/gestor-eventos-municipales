package gov.municipio.config;

import gov.municipio.application.PersonaService;
import gov.municipio.application.PeliculaService;
import gov.municipio.persistence.persona.JpaPersonaRepository;
import gov.municipio.persistence.persona.PersonaRepository;
import gov.municipio.persistence.pelicula.JpaPeliculaRepository;
import gov.municipio.persistence.pelicula.PeliculaRepository;

public class ServiceContainer {
    private final PersonaService personaService;
    private final PeliculaService peliculaService;

    public ServiceContainer() {
        // Configuración manual de dependencias (sin framework DI)
        PersonaRepository personaRepository = new JpaPersonaRepository();
        this.personaService = new PersonaService(personaRepository);
        
        PeliculaRepository peliculaRepository = new JpaPeliculaRepository();
        this.peliculaService = new PeliculaService(peliculaRepository);
    }

    public PersonaService personaService() { 
        return personaService; 
    }
    
    public PeliculaService peliculaService() {
        return peliculaService;
    }
}
