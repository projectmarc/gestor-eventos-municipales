package gov.municipio.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controlador principal del dashboard
 */
public class MainController implements Initializable {

    // Elementos de navegación
    @FXML
    private Button dashboardNavButton;
    
    @FXML
    private Button eventosNavButton;
    
    @FXML
    private Button personasNavButton;
    
    @FXML
    private Button calendarioNavButton;
    
    @FXML
    private Button crearEventoNavButton;

    // Elementos de acciones rápidas
    @FXML
    private Button crearEventoButton;
    
    @FXML
    private Button anadirPersonaButton;
    
    @FXML
    private Button verEventosButton;
    
    @FXML
    private Button verPersonasButton;

    // Calendario incluido
    @FXML
    private VBox calendario;
    
    @FXML
    private CalendarioController calendarioController;

    // Barra de estado
    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar callbacks de botones
        setupButtonActions();
        
        // Configurar calendario callback
        if (calendarioController != null) {
            calendarioController.setCallback(this::onDateSelected);
        }
        
        // Actualizar estado
        statusLabel.setText("Dashboard cargado - Listo");
    }

    /**
     * Configura las acciones de los botones
     */
    private void setupButtonActions() {
        // Botones de navegación
        dashboardNavButton.setOnAction(e -> navigateToDashboard());
        eventosNavButton.setOnAction(e -> navigateToEventos());
        personasNavButton.setOnAction(e -> navigateToPersonas());
        calendarioNavButton.setOnAction(e -> navigateToCalendario());
        crearEventoNavButton.setOnAction(e -> crearNuevoEvento());

        // Botones de acciones rápidas
        crearEventoButton.setOnAction(e -> crearNuevoEvento());
        anadirPersonaButton.setOnAction(e -> anadirNuevaPersona());
        verEventosButton.setOnAction(e -> verTodosLosEventos());
        verPersonasButton.setOnAction(e -> verTodasLasPersonas());
    }

    /**
     * Callback cuando se selecciona una fecha en el calendario
     */
    private void onDateSelected(LocalDate date) {
        statusLabel.setText("Fecha seleccionada: " + date.toString());
        System.out.println("Fecha seleccionada: " + date);
    }

    // Métodos de navegación
    private void navigateToDashboard() {
        statusLabel.setText("Navegando a Dashboard");
        System.out.println("Navegando a Dashboard");
    }

    private void navigateToEventos() {
        statusLabel.setText("Navegando a Eventos");
        System.out.println("Navegando a Eventos");
    }

    private void navigateToPersonas() {
        statusLabel.setText("Navegando a Personas");
        System.out.println("Navegando a Personas");
    }

    private void navigateToCalendario() {
        statusLabel.setText("Navegando a Calendario");
        System.out.println("Navegando a Calendario");
        if (calendarioController != null) {
            calendarioController.navigateToToday();
        }
    }

    // Métodos de acciones
    private void crearNuevoEvento() {
        statusLabel.setText("Creando nuevo evento...");
        System.out.println("Crear nuevo evento");
    }

    private void anadirNuevaPersona() {
        statusLabel.setText("Añadiendo nueva persona...");
        System.out.println("Añadir nueva persona");
    }

    private void verTodosLosEventos() {
        statusLabel.setText("Mostrando todos los eventos");
        System.out.println("Ver todos los eventos");
    }

    private void verTodasLasPersonas() {
        statusLabel.setText("Mostrando todas las personas");
        System.out.println("Ver todas las personas");
    }

    /**
     * Obtiene el controlador del calendario para acceso externo
     */
    public CalendarioController getCalendarioController() {
        return calendarioController;
    }
}