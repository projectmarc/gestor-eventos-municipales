package gov.municipio.domain.model;

import jakarta.persistence.*;
import java.util.regex.Pattern;

@Entity
@Table(name = "personas")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String apellido;
    
    @Column(unique = true, nullable = false)
    private int dni;
    
    private String telefono;
    
    @Column(unique = true)
    private String email;

    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern TELEFONO_PATTERN = 
        Pattern.compile("^[\\d\\s\\-\\+\\(\\)]{10,15}$");

    public Persona() {}

    public Persona(String nombre, String apellido, int dni, String telefono, String email) {
        this.setNombre(nombre);
        this.setApellido(apellido);
        this.setDni(dni);
        this.setTelefono(telefono);
        this.setEmail(email);
    }

    // Getters y Setters con validación (Modelo Rico)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre no puede exceder 50 caracteres");
        }
        this.nombre = nombre.trim();
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede estar vacío");
        }
        if (apellido.length() > 50) {
            throw new IllegalArgumentException("El apellido no puede exceder 50 caracteres");
        }
        this.apellido = apellido.trim();
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        if (!esValidoDni(dni)) {
            throw new IllegalArgumentException("DNI inválido. Debe tener entre 7 y 8 dígitos");
        }
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono != null && !telefono.trim().isEmpty()) {
            if (!esValidoTelefono(telefono)) {
                throw new IllegalArgumentException("Formato de teléfono inválido");
            }
            this.telefono = telefono.trim();
        } else {
            this.telefono = telefono;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (!esValidoEmail(email)) {
                throw new IllegalArgumentException("Formato de email inválido");
            }
            this.email = email.trim().toLowerCase();
        } else {
            this.email = email;
        }
    }

    // Métodos de comportamiento (Modelo Rico)
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getIniciales() {
        return (nombre.charAt(0) + "." + apellido.charAt(0) + ".").toUpperCase();
    }

    public boolean tieneEmail() {
        return email != null && !email.trim().isEmpty();
    }

    public boolean tieneTelefono() {
        return telefono != null && !telefono.trim().isEmpty();
    }

    public boolean esContactoCompleto() {
        return tieneEmail() && tieneTelefono();
    }

    // Métodos de validación privados (Modelo Rico)
    private boolean esValidoDni(int dni) {
        String dniStr = String.valueOf(dni);
        return dniStr.length() >= 7 && dniStr.length() <= 8 && dni > 0;
    }

    private boolean esValidoEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    private boolean esValidoTelefono(String telefono) {
        return telefono != null && TELEFONO_PATTERN.matcher(telefono.trim()).matches();
    }

    // Métodos de negocio (Modelo Rico)
    public void actualizarDatosPersonales(String nuevoNombre, String nuevoApellido) {
        setNombre(nuevoNombre);
        setApellido(nuevoApellido);
    }

    public void actualizarContacto(String nuevoTelefono, String nuevoEmail) {
        setTelefono(nuevoTelefono);
        setEmail(nuevoEmail);
    }

    public boolean esIgualPorDni(Persona otraPersona) {
        return otraPersona != null && this.dni == otraPersona.dni;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (DNI: " + dni + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Persona persona = (Persona) obj;
        return dni == persona.dni;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(dni);
    }
}
