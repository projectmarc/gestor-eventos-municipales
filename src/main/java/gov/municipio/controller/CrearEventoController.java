package gov.municipio.controller;

import gov.municipio.domain.model.enums.EstadoEvento;
import gov.municipio.ui.nav.Router;
import gov.municipio.ui.nav.Screen;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class CrearEventoController {
    
    // Campos básicos
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cbTipoEvento;
    @FXML private DatePicker dpFechaInicio;
    @FXML private TextField txtDuracion;
    @FXML private CheckBox chkRequiereInscripcion;
    
    // Botones de acción
    @FXML private Button btnCancelar;
    @FXML private Button btnCrear;
    
    private Router router;
    
    public CrearEventoController() {
        // Constructor vacío para el patrón actual
    }
    
    public void setRouter(Router router) {
        this.router = router;
    }
    
    @FXML
    private void initialize() {
        configurarComboBoxes();
        configurarEventHandlers();
        configurarValidaciones();
    }
    
    private void configurarComboBoxes() {
        // ComboBox de tipos de evento
        cbTipoEvento.setItems(FXCollections.observableArrayList(
            "Feria", "Concierto", "Exposición", "Taller", "Ciclo de cine"
        ));
    }
    
    private void configurarEventHandlers() {
        // Handler para cambio de tipo de evento (Taller requiere inscripción automáticamente)
        cbTipoEvento.setOnAction(event -> {
            String tipoSeleccionado = cbTipoEvento.getValue();
            if ("Taller".equals(tipoSeleccionado)) {
                chkRequiereInscripcion.setSelected(true);
                chkRequiereInscripcion.setDisable(true);
            } else {
                chkRequiereInscripcion.setDisable(false);
            }
        });
        
        // Handlers para botones principales
        btnCancelar.setOnAction(event -> cancelar());
        btnCrear.setOnAction(event -> crearEvento());
    }
    
    private void configurarValidaciones() {
        // Configurar validaciones de texto numérico para duración
        configurarCampoNumerico(txtDuracion);
    }
    
    private void configurarCampoNumerico(TextField campo) {
        campo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                campo.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    // Métodos principales
    private void crearEvento() {
        try {
            if (!validarDatosBasicos()) {
                return;
            }
            
            // Aquí iría la lógica para crear el evento según el tipo seleccionado
            // Por ahora solo mostramos la información que se capturaría
            
            String mensaje = construirMensajeResumen();
            mostrarAlerta("Evento creado", mensaje, Alert.AlertType.INFORMATION);
            
            // Limpiar formulario después de crear
            limpiarFormulario();
            
            // Volver al listado de eventos
            volverAEventos();
            
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al crear el evento: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private boolean validarDatosBasicos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "El nombre del evento es obligatorio.", Alert.AlertType.WARNING);
            txtNombre.requestFocus();
            return false;
        }
        
        if (cbTipoEvento.getValue() == null) {
            mostrarAlerta("Campo requerido", "Debe seleccionar un tipo de evento.", Alert.AlertType.WARNING);
            cbTipoEvento.requestFocus();
            return false;
        }
        
        if (dpFechaInicio.getValue() == null) {
            mostrarAlerta("Campo requerido", "La fecha de inicio es obligatoria.", Alert.AlertType.WARNING);
            dpFechaInicio.requestFocus();
            return false;
        }
        
        if (dpFechaInicio.getValue().isBefore(LocalDate.now())) {
            mostrarAlerta("Fecha inválida", "La fecha de inicio no puede ser anterior a hoy.", Alert.AlertType.WARNING);
            dpFechaInicio.requestFocus();
            return false;
        }
        
        if (txtDuracion.getText().trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "La duración en horas es obligatoria.", Alert.AlertType.WARNING);
            txtDuracion.requestFocus();
            return false;
        }
        
        try {
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            if (duracion <= 0) {
                mostrarAlerta("Duración inválida", "La duración debe ser mayor a 0 horas.", Alert.AlertType.WARNING);
                txtDuracion.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Duración inválida", "La duración debe ser un número válido.", Alert.AlertType.WARNING);
            txtDuracion.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private String construirMensajeResumen() {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Evento: ").append(txtNombre.getText()).append("\n");
        mensaje.append("Tipo: ").append(cbTipoEvento.getValue()).append("\n");
        mensaje.append("Fecha: ").append(dpFechaInicio.getValue()).append("\n");
        mensaje.append("Duración: ").append(txtDuracion.getText()).append(" horas\n");
        mensaje.append("Estado: ").append(EstadoEvento.PLANIFICACION).append("\n");
        mensaje.append("Requiere inscripción: ").append(chkRequiereInscripcion.isSelected() ? "Sí" : "No").append("\n");
        
        mensaje.append("\nNota: Los responsables del evento se pueden agregar después de la creación.");
        
        return mensaje.toString();
    }
    
    private void limpiarFormulario() {
        // Limpiar campos básicos
        txtNombre.clear();
        cbTipoEvento.setValue(null);
        dpFechaInicio.setValue(null);
        txtDuracion.clear();
        
        // Resetear inscripción
        chkRequiereInscripcion.setSelected(false);
        chkRequiereInscripcion.setDisable(false);
    }
    
    private void cancelar() {
        // Confirmar cancelación si hay datos
        if (hayDatosIngresados()) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar cancelación");
            confirmacion.setHeaderText("¿Está seguro de que desea cancelar?");
            confirmacion.setContentText("Se perderán todos los datos ingresados.");
            
            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    volverAEventos();
                }
            });
        } else {
            volverAEventos();
        }
    }
    
    private boolean hayDatosIngresados() {
        return !txtNombre.getText().trim().isEmpty() ||
               cbTipoEvento.getValue() != null ||
               dpFechaInicio.getValue() != null ||
               !txtDuracion.getText().trim().isEmpty();
    }
    
    @FXML
    private void volverAEventos() {
        if (router != null) {
            router.goTo(Screen.EVENTOS);
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
