package gov.municipio.controller;

import gov.municipio.application.EventoService;
import gov.municipio.config.ServiceContainer;
import gov.municipio.domain.model.evento.Evento;
import gov.municipio.domain.model.enums.EstadoEvento;
import gov.municipio.ui.nav.Router;
import gov.municipio.ui.nav.Screen;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class GestionarEventoController {
    
    @FXML
    private Label lblNombre;
    
    @FXML
    private Label lblDuracion;
    
    @FXML
    private Label lblFechaInicio;
    
    @FXML
    private Label lblCupoMaximo;
    
    @FXML
    private TextField txtNombre;
    
    @FXML
    private TextField txtDuracion;
    
    @FXML
    private DatePicker dpFechaInicio;
    
    @FXML
    private TextField txtCupoMaximo;
    
    @FXML
    private Button btnEditar;
    
    @FXML
    private Button btnGuardar;
    
    @FXML
    private Button btnCancelar;
    
    private Router router;
    private EventoService eventoService;
    private Evento eventoActual;
    
    public GestionarEventoController() {
        // Constructor vacío para el patrón actual
    }
    
    public void setRouter(Router router) {
        this.router = router;
    }
    
    public void setServiceContainer(ServiceContainer serviceContainer) {
        this.eventoService = serviceContainer.eventoService();
    }
    
    public void setEvento(Evento evento) {
        this.eventoActual = evento;
        cargarDatosEvento();
    }
    
    @FXML
    private void initialize() {
        // Inicialmente mostrar solo los labels (modo lectura)
        mostrarModoLectura();
    }
    
    private void cargarDatosEvento() {
        if (eventoActual != null) {
            // Cargar datos en los labels
            lblNombre.setText(eventoActual.getNombre());
            lblDuracion.setText(String.valueOf(eventoActual.getDuracionEstimada()));
            lblFechaInicio.setText(eventoActual.getFechaInicio().toString());
            
            if (eventoActual.isRequiereInscripcion() && eventoActual.getCupoMaximoDefinido() != null) {
                lblCupoMaximo.setText(String.valueOf(eventoActual.getCupoMaximoDefinido()));
            } else {
                lblCupoMaximo.setText("Sin límite");
            }
            
            // Cargar datos en los campos de edición (inicialmente ocultos)
            txtNombre.setText(eventoActual.getNombre());
            txtDuracion.setText(String.valueOf(eventoActual.getDuracionEstimada()));
            dpFechaInicio.setValue(eventoActual.getFechaInicio());
            
            if (eventoActual.isRequiereInscripcion() && eventoActual.getCupoMaximoDefinido() != null) {
                txtCupoMaximo.setText(String.valueOf(eventoActual.getCupoMaximoDefinido()));
            } else {
                txtCupoMaximo.setText("");
            }
            
            // Habilitar/deshabilitar edición según el estado
            btnEditar.setDisable(!eventoActual.estaEnPlanificacion());
        }
    }
    
    @FXML
    private void editar() {
        mostrarModoEdicion();
    }
    
    @FXML
    private void guardar() {
        try {
            // Validar y actualizar los datos
            String nombre = txtNombre.getText().trim();
            String duracionTexto = txtDuracion.getText().trim();
            LocalDate fechaInicio = dpFechaInicio.getValue();
            String cupoTexto = txtCupoMaximo.getText().trim();
            
            // Validaciones básicas
            if (nombre.isEmpty()) {
                mostrarError("Error", "El nombre no puede estar vacío");
                return;
            }
            
            int duracion;
            try {
                duracion = Integer.parseInt(duracionTexto);
            } catch (NumberFormatException e) {
                mostrarError("Error", "La duración debe ser un número válido");
                return;
            }
            
            if (fechaInicio == null) {
                mostrarError("Error", "Debe seleccionar una fecha de inicio");
                return;
            }
            
            // Actualizar el evento
            eventoActual.setNombre(nombre);
            eventoActual.setDuracionEstimada(duracion);
            eventoActual.setFechaInicio(fechaInicio);
            
            // Actualizar cupo máximo solo si el evento requiere inscripción
            if (eventoActual.isRequiereInscripcion()) {
                if (cupoTexto.isEmpty()) {
                    mostrarError("Error", "El cupo máximo es obligatorio para eventos que requieren inscripción");
                    return;
                }
                try {
                    int cupo = Integer.parseInt(cupoTexto);
                    eventoActual.setCupoMaximo(cupo);
                } catch (NumberFormatException e) {
                    mostrarError("Error", "El cupo máximo debe ser un número válido");
                    return;
                }
            }
            
            // Guardar cambios en la base de datos
            eventoService.actualizarEvento(eventoActual);
            
            // Actualizar la vista y volver al modo lectura
            cargarDatosEvento();
            mostrarModoLectura();
            
            mostrarInfo("Éxito", "Los datos del evento han sido actualizados correctamente");
            
        } catch (Exception e) {
            mostrarError("Error", "Error al guardar los cambios: " + e.getMessage());
        }
    }
    
    @FXML
    private void cancelar() {
        // Recargar los datos originales y volver al modo lectura
        cargarDatosEvento();
        mostrarModoLectura();
    }
    
    @FXML
    private void volver() {
        if (router != null) {
            router.goTo(Screen.EVENTOS);
        }
    }
    
    private void mostrarModoLectura() {
        // Mostrar labels, ocultar campos de edición
        lblNombre.setVisible(true);
        lblDuracion.setVisible(true);
        lblFechaInicio.setVisible(true);
        lblCupoMaximo.setVisible(true);
        
        txtNombre.setVisible(false);
        txtNombre.setManaged(false);
        txtDuracion.setVisible(false);
        txtDuracion.setManaged(false);
        dpFechaInicio.setVisible(false);
        dpFechaInicio.setManaged(false);
        txtCupoMaximo.setVisible(false);
        txtCupoMaximo.setManaged(false);
        
        // Mostrar botón editar, ocultar guardar/cancelar
        btnEditar.setVisible(true);
        btnEditar.setManaged(true);
        btnGuardar.setVisible(false);
        btnGuardar.setManaged(false);
        btnCancelar.setVisible(false);
        btnCancelar.setManaged(false);
    }
    
    private void mostrarModoEdicion() {
        // Ocultar labels, mostrar campos de edición
        lblNombre.setVisible(false);
        lblDuracion.setVisible(false);
        lblFechaInicio.setVisible(false);
        lblCupoMaximo.setVisible(false);
        
        txtNombre.setVisible(true);
        txtNombre.setManaged(true);
        txtDuracion.setVisible(true);
        txtDuracion.setManaged(true);
        dpFechaInicio.setVisible(true);
        dpFechaInicio.setManaged(true);
        txtCupoMaximo.setVisible(true);
        txtCupoMaximo.setManaged(true);
        
        // Ocultar botón editar, mostrar guardar/cancelar
        btnEditar.setVisible(false);
        btnEditar.setManaged(false);
        btnGuardar.setVisible(true);
        btnGuardar.setManaged(true);
        btnCancelar.setVisible(true);
        btnCancelar.setManaged(true);
        
        // Enfocar el primer campo
        txtNombre.requestFocus();
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
