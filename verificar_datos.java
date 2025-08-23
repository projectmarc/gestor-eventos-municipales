import gov.municipio.domain.service.EventoService;
import gov.municipio.domain.service.PersonaService;
import gov.municipio.domain.model.evento.Evento;
import gov.municipio.domain.model.Persona;

public class verificar_datos {
    public static void main(String[] args) {
        EventoService eventoService = EventoService.getInstance();
        PersonaService personaService = PersonaService.getInstance();
        
        System.out.println("=== PERSONAS ===");
        var personas = personaService.listarTodas();
        System.out.println("Total personas: " + personas.size());
        personas.forEach(p -> System.out.println("- " + p.getNombre() + " " + p.getApellido()));
        
        System.out.println("\n=== EVENTOS ===");
        var eventos = eventoService.listarTodos();
        System.out.println("Total eventos: " + eventos.size());
        eventos.forEach(e -> {
            System.out.println("- " + e.getNombre() + " (" + e.getFechaInicio() + ")");
            System.out.println("  Participaciones: " + e.getParticipaciones().size());
            e.getParticipaciones().forEach(p -> 
                System.out.println("    * " + p.getPersona().getNombre() + " " + 
                                 p.getPersona().getApellido() + " - " + p.getRol()));
        });
        
        eventoService.cerrar();
    }
}
