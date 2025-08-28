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
    
    /**
     * Actualiza todos los datos de una persona existente.
     * 
     * @param id el ID de la persona a actualizar
     * @param nombre el nuevo nombre
     * @param apellido el nuevo apellido
     * @param dni el nuevo DNI
     * @param telefono el nuevo teléfono
     * @param email el nuevo email
     * @return la persona actualizada
     * @throws IllegalStateException si la persona no existe o si el DNI ya está en uso por otra persona
     */
    public Persona actualizarPersona(Long id, String nombre, String apellido, String dni, String telefono, String email) {
        Optional<Persona> personaOpt = personaRepository.findById(id);
        if (personaOpt.isEmpty()) {
            throw new IllegalStateException("No existe una persona con el ID: " + id);
        }
        
        Persona persona = personaOpt.get();
        
        // Verificar que el DNI no esté duplicado (si es diferente al actual)
        if (!persona.getDni().equals(dni) && personaRepository.existsByDni(dni)) {
            throw new IllegalStateException("Ya existe una persona registrada con el DNI: " + dni);
        }
        
        // Actualizar los campos usando los métodos de la entidad
        persona.cambiarNombre(nombre);
        persona.cambiarApellido(apellido);
        persona.cambiarDni(dni);
        persona.cambiarTelefono(telefono);
        persona.cambiarEmail(email);
        
        return personaRepository.save(persona);
    }
    
    /**
     * Elimina una persona del sistema.
     * 
     * @param id el ID de la persona a eliminar
     * @throws IllegalStateException si la persona no existe
     */
    public void eliminar(Long id) {
        Optional<Persona> personaOpt = personaRepository.findById(id);
        if (personaOpt.isEmpty()) {
            throw new IllegalStateException("No existe una persona con el ID: " + id);
        }
        personaRepository.delete(personaOpt.get());
    }
    
    /**
     * Busca personas por nombre o apellido (búsqueda parcial).
     * 
     * @param termino el término de búsqueda
     * @return lista de personas que coinciden con el término
     */
    public List<Persona> buscarPorNombreOApellido(String termino) {
        // Implementación simple: filtrar todas las personas
        return personaRepository.findAll().stream()
            .filter(p -> p.getNombre().toLowerCase().contains(termino.toLowerCase()) ||
                        p.getApellido().toLowerCase().contains(termino.toLowerCase()))
            .toList();
    }
}
