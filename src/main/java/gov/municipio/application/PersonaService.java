package gov.municipio.application;

import gov.municipio.domain.model.Persona;
import gov.municipio.persistence.persona.PersonaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para el caso de uso de gestión de personas.
 * Orquesta las operaciones de dominio y persistencia.
 */
public class PersonaService {
    
    private final PersonaRepository personaRepository;
    
    /**
     * Constructor que recibe el repositorio por inyección de dependencias manual.
     * 
     * @param personaRepository el repositorio de personas
     */
    public PersonaService(PersonaRepository personaRepository) {
        if (personaRepository == null) {
            throw new IllegalArgumentException("PersonaRepository no puede ser nulo");
        }
        this.personaRepository = personaRepository;
    }
    
    /**
     * Registra una nueva persona en el sistema.
     * Valida que el DNI no esté duplicado antes de persistir.
     * 
     * @param nombre el nombre de la persona
     * @param apellido el apellido de la persona  
     * @param dni el DNI de la persona (debe ser único)
     * @param telefono el teléfono de la persona
     * @param email el email de la persona
     * @return la persona registrada con su ID asignado
     * @throws IllegalStateException si ya existe una persona con el mismo DNI
     * @throws IllegalArgumentException si algún parámetro no es válido
     */
    public Persona registrarPersona(String nombre, String apellido, String dni, String telefono, String email) {
        // Verificar que el DNI no esté duplicado
        if (personaRepository.existsByDni(dni)) {
            throw new IllegalStateException("Ya existe una persona registrada con el DNI: " + dni);
        }
        
        // Crear la persona (las validaciones se ejecutan en el constructor del dominio)
        Persona nuevaPersona = new Persona(nombre, apellido, dni, telefono, email);
        
        // Persistir y retornar la persona con su ID asignado
        return personaRepository.save(nuevaPersona);
    }
    
    /**
     * Busca una persona por su ID.
     * 
     * @param id el ID de la persona
     * @return Optional con la persona si existe
     */
    public Optional<Persona> buscarPersonaPorId(Long id) {
        return personaRepository.findById(id);
    }
    
    /**
     * Busca una persona por su DNI.
     * 
     * @param dni el DNI de la persona
     * @return Optional con la persona si existe
     */
    public Optional<Persona> buscarPersonaPorDni(String dni) {
        return personaRepository.findByDni(dni);
    }
    
    /**
     * Obtiene todas las personas registradas.
     * 
     * @return lista de todas las personas
     */
    public List<Persona> obtenerTodasLasPersonas() {
        return personaRepository.findAll();
    }
    
    /**
     * Actualiza los datos de contacto de una persona existente.
     * 
     * @param id el ID de la persona a actualizar
     * @param nuevoTelefono el nuevo teléfono (puede ser null para no cambiar)
     * @param nuevoEmail el nuevo email (puede ser null para no cambiar)
     * @return la persona actualizada
     * @throws IllegalStateException si la persona no existe
     */
    public Persona actualizarDatosContacto(Long id, String nuevoTelefono, String nuevoEmail) {
        Optional<Persona> personaOpt = personaRepository.findById(id);
        if (personaOpt.isEmpty()) {
            throw new IllegalStateException("No existe una persona con el ID: " + id);
        }
        
        Persona persona = personaOpt.get();
        
        // Actualizar solo los campos que no son nulos
        if (nuevoTelefono != null) {
            persona.cambiarTelefono(nuevoTelefono);
        }
        if (nuevoEmail != null) {
            persona.cambiarEmail(nuevoEmail);
        }
        
        return personaRepository.save(persona);
    }
}
