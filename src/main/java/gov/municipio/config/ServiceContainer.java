package gov.municipio.config;

import gov.municipio.application.PersonaService;
import gov.municipio.application.PeliculaService;
import gov.municipio.application.ArtistaBandaService;
import gov.municipio.application.EventoService;
import gov.municipio.persistence.persona.JpaPersonaRepository;
import gov.municipio.persistence.persona.PersonaRepository;
import gov.municipio.persistence.pelicula.JpaPeliculaRepository;
import gov.municipio.persistence.pelicula.PeliculaRepository;
import gov.municipio.persistence.artistabanda.JpaArtistaBandaRepository;
import gov.municipio.persistence.artistabanda.ArtistaBandaRepository;
import gov.municipio.persistence.evento.EventoRepository;
import gov.municipio.persistence.evento.EventoRepositoryImpl;

public class ServiceContainer {
    private final PersonaService personaService;
    private final PeliculaService peliculaService;
    private final ArtistaBandaService artistaBandaService;
    private final EventoService eventoService;

    public ServiceContainer() {
        // Configuración manual de dependencias (sin framework DI)
        PersonaRepository personaRepository = new JpaPersonaRepository();
        this.personaService = new PersonaService(personaRepository);
        
        PeliculaRepository peliculaRepository = new JpaPeliculaRepository();
        this.peliculaService = new PeliculaService(peliculaRepository);
        
        ArtistaBandaRepository artistaBandaRepository = new JpaArtistaBandaRepository();
        this.artistaBandaService = new ArtistaBandaService(artistaBandaRepository);
        
        EventoRepository eventoRepository = new EventoRepositoryImpl();
        this.eventoService = new EventoService(eventoRepository);
    }

    public PersonaService personaService() { 
        return personaService; 
    }
    
    public PeliculaService peliculaService() {
        return peliculaService;
    }
    
    public ArtistaBandaService artistaBandaService() {
        return artistaBandaService;
    }
    
    public EventoService eventoService() {
        return eventoService;
    }
}
