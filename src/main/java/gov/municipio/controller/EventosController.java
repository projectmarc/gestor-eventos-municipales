package gov.municipio.controller;

import gov.municipio.application.EventoService;
import gov.municipio.config.ServiceContainer;
import gov.municipio.domain.model.evento.Evento;
import gov.municipio.ui.nav.Router;
import gov.municipio.ui.nav.Screen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventosController {
    
    @FXML
    private Button btnCrearEvento;
    
    @FXML
    private Button btnGestionarEvento;
    
    @FXML
    private TableView<Evento> tblEventos;
    
    @FXML
    private TableColumn<Evento, String> colNombre;
    
    @FXML
    private TableColumn<Evento, String> colFechaInicio;
    
    @FXML
    private TableColumn<Evento, String> colDuracion;
    
    @FXML
    private TableColumn<Evento, String> colTipoEvento;
    
    @FXML
    private TableColumn<Evento, String> colEstado;
    
    private Router router;
    private EventoService eventoService;
    private ObservableList<Evento> eventos;
    
    public EventosController() {
        // Constructor vacío para el patrón actual
    }
    
    public void setRouter(Router router) {
        this.router = router;
    }
    
    public void setServiceContainer(ServiceContainer serviceContainer) {
        this.eventoService = serviceContainer.eventoService();
    }
    
    @FXML
    private void initialize() {
        configurarTabla();
        cargarEventos();
    }
    
    /**
     * Configura las columnas de la tabla para mostrar los datos de los eventos.
     */
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        
        // Configurar columna de fecha con formato personalizado
        colFechaInicio.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getFechaInicio();
            String fechaFormateada = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return new javafx.beans.property.SimpleStringProperty(fechaFormateada);
        });
        
        // Configurar columna de duración con formato personalizado
        colDuracion.setCellValueFactory(cellData -> {
            int duracion = cellData.getValue().getDuracionEstimada();
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(duracion));
        });
        
        // Configurar columna de tipo de evento
        colTipoEvento.setCellValueFactory(cellData -> {
            String tipo = cellData.getValue().getClass().getSimpleName();
            return new javafx.beans.property.SimpleStringProperty(tipo);
        });
        
        // Configurar columna de estado
        colEstado.setCellValueFactory(cellData -> {
            String estado = convertirEstadoATexto(cellData.getValue().getEstado());
            return new javafx.beans.property.SimpleStringProperty(estado);
        });
    }
    
    /**
     * Carga todos los eventos desde el servicio y los muestra en la tabla.
     */
    private void cargarEventos() {
        try {
            List<Evento> listaEventos = eventoService.obtenerTodosLosEventos();
            eventos = FXCollections.observableArrayList(listaEventos);
            tblEventos.setItems(eventos);
            
            System.out.println("Cargados " + listaEventos.size() + " eventos en la tabla");
            
        } catch (Exception e) {
            e.printStackTrace();
            error("Error al cargar datos", "No se pudieron cargar los eventos: " + e.getMessage());
        }
    }
    
    @FXML
    private void crearEvento() {
        if (router != null) {
            router.goTo(Screen.CREAR_EVENTO);
        }
    }
    
    @FXML
    private void gestionarEvento() {
        Evento eventoSeleccionado = tblEventos.getSelectionModel().getSelectedItem();
        if (eventoSeleccionado == null) {
            error("Selección requerida", "Debe seleccionar un evento de la tabla para gestionar");
            return;
        }
        
        if (router != null) {
            router.goToGestionarEvento(eventoSeleccionado);
        }
    }
    
    private void error(String titulo, String detalle) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(detalle);
        alert.showAndWait();
    }
    
    /**
     * Convierte el estado del evento a una representación en texto más amigable.
     */
    private String convertirEstadoATexto(gov.municipio.domain.model.enums.EstadoEvento estado) {
        return switch (estado) {
            case PLANIFICACION -> "En Planificación";
            case CONFIRMADO -> "Confirmado";
            case EN_EJECUCION -> "En Ejecución";
            case FINALIZADO -> "Finalizado";
        };
    }
}
