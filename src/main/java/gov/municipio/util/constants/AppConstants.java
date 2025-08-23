package gov.municipio.util.constants;

/**
 * Constantes de la aplicación
 */
public final class AppConstants {
    
    // Constantes de validación
    public static final int MIN_DNI_DIGITS = 7;
    public static final int MAX_DNI_DIGITS = 8;
    public static final int MAX_NOMBRE_LENGTH = 50;
    public static final int MAX_EMAIL_LENGTH = 100;
    
    // Constantes de eventos
    public static final int MIN_DURACION_EVENTO = 1;
    public static final int MAX_DURACION_EVENTO = 720; // 30 días en horas
    
    // Constantes de cupo
    public static final int MIN_CUPO_TALLER = 1;
    public static final int MAX_CUPO_TALLER = 100;
    public static final int MIN_CAPACIDAD_CONCIERTO = 1;
    public static final int MAX_CAPACIDAD_CONCIERTO = 10000;
    
    // Constantes de UI
    public static final String APP_TITLE = "Gestor de Eventos Municipales";
    public static final int DEFAULT_WINDOW_WIDTH = 1200;
    public static final int DEFAULT_WINDOW_HEIGHT = 800;
    
    // Mensajes comunes
    public static final String MSG_CAMPO_OBLIGATORIO = "Este campo es obligatorio";
    public static final String MSG_FORMATO_INVALIDO = "El formato ingresado no es válido";
    public static final String MSG_OPERACION_EXITOSA = "Operación realizada exitosamente";
    public static final String MSG_ERROR_INESPERADO = "Se produjo un error inesperado";
    
    private AppConstants() {
        // Clase de constantes - no instanciable
    }
}
