package gov.municipio.application;

import gov.municipio.domain.model.ArtistaBanda;
import gov.municipio.persistence.artistabanda.ArtistaBandaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para el caso de uso de gestión de artistas/bandas.
 * Orquesta las operaciones de dominio y persistencia.
 */
public class ArtistaBandaService {
    
    private final ArtistaBandaRepository artistaBandaRepository;
    
    /**
     * Constructor que recibe el repositorio por inyección de dependencias manual.
     * 
     * @param artistaBandaRepository el repositorio de artistas/bandas
     */
    public ArtistaBandaService(ArtistaBandaRepository artistaBandaRepository) {
        if (artistaBandaRepository == null) {
            throw new IllegalArgumentException("ArtistaBandaRepository no puede ser nulo");
        }
        this.artistaBandaRepository = artistaBandaRepository;
    }
    
    /**
     * Registra un nuevo artista/banda.
     * 
     * @param nombre el nombre del artista/banda
     * @return el artista/banda registrado con ID asignado
     * @throws IllegalArgumentException si los datos son inválidos
     * @throws IllegalStateException si ya existe un artista/banda con ese nombre
     */
    public ArtistaBanda registrarArtistaBanda(String nombre) {
        // Validar que no exista ya
        if (artistaBandaRepository.findByNombre(nombre).isPresent()) {
            throw new IllegalStateException("Ya existe un artista/banda con el nombre: " + nombre);
        }
        
        // Crear y guardar
        ArtistaBanda artistaBanda = new ArtistaBanda(nombre);
        return artistaBandaRepository.save(artistaBanda);
    }
    
    /**
     * Busca un artista/banda por su ID.
     * 
     * @param id el ID del artista/banda
     * @return Optional con el artista/banda si existe
     */
    public Optional<ArtistaBanda> buscarArtistaBandaPorId(Long id) {
        return artistaBandaRepository.findById(id);
    }
    
    /**
     * Busca un artista/banda por su nombre.
     * 
     * @param nombre el nombre del artista/banda
     * @return Optional con el artista/banda si existe
     */
    public Optional<ArtistaBanda> buscarArtistaBandaPorNombre(String nombre) {
        return artistaBandaRepository.findByNombre(nombre);
    }
    
    /**
     * Obtiene todos los artistas/bandas registrados.
     * 
     * @return lista de todos los artistas/bandas
     */
    public List<ArtistaBanda> obtenerTodosLosArtistasBandas() {
        return artistaBandaRepository.findAll();
    }
    
    /**
     * Actualiza los datos de un artista/banda existente.
     * 
     * @param id el ID del artista/banda a actualizar
     * @param nuevoNombre el nuevo nombre (puede ser null para no cambiar)
     * @return el artista/banda actualizado
     * @throws IllegalStateException si el artista/banda no existe
     */
    public ArtistaBanda actualizarArtistaBanda(Long id, String nuevoNombre) {
        Optional<ArtistaBanda> opt = artistaBandaRepository.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalStateException("No existe un artista/banda con ID: " + id);
        }
        
        ArtistaBanda artistaBanda = opt.get();
        
        // Actualizar nombre si se proporciona
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            // Verificar que el nuevo nombre no esté ya en uso por otro artista/banda
            Optional<ArtistaBanda> existente = artistaBandaRepository.findByNombre(nuevoNombre);
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                throw new IllegalStateException("Ya existe otro artista/banda con el nombre: " + nuevoNombre);
            }
            artistaBanda.cambiarNombre(nuevoNombre);
        }
        
        return artistaBandaRepository.save(artistaBanda);
    }
    
    /**
     * Elimina un artista/banda por su ID.
     * 
     * @param id el ID del artista/banda a eliminar
     * @throws IllegalStateException si el artista/banda no existe
     */
    public void eliminarArtistaBanda(Long id) {
        if (!artistaBandaRepository.existsById(id)) {
            throw new IllegalStateException("No existe un artista/banda con ID: " + id);
        }
        artistaBandaRepository.deleteById(id);
    }
}
