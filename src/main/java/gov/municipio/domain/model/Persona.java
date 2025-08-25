package gov.municipio.domain.model;

import java.util.regex.Pattern;

public class Persona {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\d{8,15}$");
    
    private final String nombre;
    private final String apellido;
    private final String dni;
    private String telefono;
    private String email;

    public Persona(String nombre, String apellido, String dni, String telefono, String email) {
        this.nombre = validarNombre(nombre);
        this.apellido = validarApellido(apellido);
        this.dni = validarDni(dni);
        this.telefono = validarTelefono(telefono);
        this.email = validarEmail(email);
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDni() {
        return dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    // Métodos de comportamiento para actualizar datos de contacto
    public void cambiarEmail(String nuevoEmail) {
        this.email = validarEmail(nuevoEmail);
    }

    public void cambiarTelefono(String nuevoTelefono) {
        this.telefono = validarTelefono(nuevoTelefono);
    }

    // Métodos de validación privados
    private String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacío");
        }
        return nombre.trim();
    }

    private String validarApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede ser nulo ni vacío");
        }
        return apellido.trim();
    }

    private String validarDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede ser nulo ni vacío");
        }
        
        String dniLimpio = dni.trim();
        if (!dniLimpio.matches("\\d{7,8}")) {
            throw new IllegalArgumentException("El DNI debe contener solo números y tener entre 7 y 8 dígitos");
        }
        
        return dniLimpio;
    }

    private String validarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono no puede ser nulo ni vacío");
        }
        
        String telefonoLimpio = telefono.replaceAll("[\\s\\-\\(\\)]", "");
        
        if (!TELEFONO_PATTERN.matcher(telefonoLimpio).matches()) {
            throw new IllegalArgumentException("El teléfono debe contener solo números y tener entre 8 y 15 dígitos");
        }
        
        return telefonoLimpio;
    }

    private String validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser nulo ni vacío");
        }
        
        String emailLimpio = email.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(emailLimpio).matches()) {
            throw new IllegalArgumentException("El email no tiene un formato válido");
        }
        
        return emailLimpio;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return String.format("Persona{nombre='%s', apellido='%s', dni='%s', telefono='%s', email='%s'}",
                nombre, apellido, dni, telefono, email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Persona persona = (Persona) obj;
        return dni.equals(persona.dni);
    }

    @Override
    public int hashCode() {
        return dni.hashCode();
    }
}
