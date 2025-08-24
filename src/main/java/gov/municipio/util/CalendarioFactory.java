package gov.municipio.util;

import gov.municipio.controller.CalendarioController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilidad para crear y configurar instancias del calendario reutilizable
 */
public class CalendarioFactory {

    /**
     * Crea una nueva instancia del calendario
     */
    public static CalendarioResult crearCalendario() throws IOException {
        FXMLLoader loader = new FXMLLoader(CalendarioFactory.class.getResource("/gov/municipio/view/calendario-view.fxml"));
        VBox calendarioView = loader.load();
        CalendarioController controller = loader.getController();
        
        return new CalendarioResult(calendarioView, controller);
    }

    /**
     * Crea un calendario preconfigurado con fechas de eventos específicas
     */
    public static CalendarioResult crearCalendarioConEventos(Set<LocalDate> fechasConEventos, 
                                                            CalendarioController.CalendarioCallback callback) throws IOException {
        CalendarioResult resultado = crearCalendario();
        
        if (fechasConEventos != null && !fechasConEventos.isEmpty()) {
            resultado.getController().setFechasConEventos(fechasConEventos);
        }
        
        if (callback != null) {
            resultado.getController().setCallback(callback);
        }
        
        return resultado;
    }

    /**
     * Ejemplo de uso del calendario con eventos predefinidos
     */
    public static CalendarioResult crearCalendarioEjemplo() throws IOException {
        Set<LocalDate> fechasEjemplo = new HashSet<>();
        fechasEjemplo.add(LocalDate.now()); // Hoy
        fechasEjemplo.add(LocalDate.now().plusDays(3)); // En 3 días
        fechasEjemplo.add(LocalDate.now().plusDays(7)); // En una semana
        fechasEjemplo.add(LocalDate.now().plusDays(14)); // En dos semanas

        CalendarioController.CalendarioCallback callback = date -> {
            System.out.println("📅 Fecha seleccionada: " + date);
            System.out.println("🎯 Verificando eventos para esta fecha...");
        };

        return crearCalendarioConEventos(fechasEjemplo, callback);
    }

    /**
     * Clase resultado que contiene tanto la vista como el controlador
     */
    public static class CalendarioResult {
        private final VBox view;
        private final CalendarioController controller;

        public CalendarioResult(VBox view, CalendarioController controller) {
            this.view = view;
            this.controller = controller;
        }

        public VBox getView() {
            return view;
        }

        public CalendarioController getController() {
            return controller;
        }
    }
}
