package gov.municipio.controller;

import gov.municipio.application.EventoService;
import gov.municipio.config.ServiceContainer;
import gov.municipio.domain.model.evento.Evento;
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
    @FXML private TextField txtCupoMaximo;
    
    // Botones de acción
    @FXML private Button btnCancelar;
    @FXML private Button btnCrear;
    
    private Router router;
    private EventoService eventoService;
    
    public CrearEventoController() {
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
                txtCupoMaximo.setDisable(false);
            } else {
                chkRequiereInscripcion.setDisable(false);
            }
        });
        
        // Handler para checkbox de requiere inscripción
        chkRequiereInscripcion.setOnAction(event -> {
            txtCupoMaximo.setDisable(!chkRequiereInscripcion.isSelected());
            if (!chkRequiereInscripcion.isSelected()) {
                txtCupoMaximo.clear();
            }
        });
        
        // Handlers para botones principales
        btnCancelar.setOnAction(event -> cancelar());
        btnCrear.setOnAction(event -> crearEvento());
    }
    
    private void configurarValidaciones() {
        // Configurar validaciones de texto numérico para duración
        configurarCampoNumerico(txtDuracion);
        
        // Configurar validaciones de texto numérico para cupo máximo
        configurarCampoNumerico(txtCupoMaximo);
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
            
            // Extraer datos del formulario
            String nombre = txtNombre.getText().trim();
            String tipoEvento = cbTipoEvento.getValue();
            LocalDate fechaInicio = dpFechaInicio.getValue();
            int duracionEstimada = Integer.parseInt(txtDuracion.getText().trim());
            boolean requiereInscripcion = chkRequiereInscripcion.isSelected();
            Integer cupoMaximo = null;
            
            if (requiereInscripcion && !txtCupoMaximo.getText().trim().isEmpty()) {
                cupoMaximo = Integer.parseInt(txtCupoMaximo.getText().trim());
            }
            
            // Crear el evento usando el servicio
            Evento eventoCreado = eventoService.crearEventoBasico(
                tipoEvento, nombre, fechaInicio, duracionEstimada, requiereInscripcion, cupoMaximo
            );
            
            // Mostrar mensaje de éxito
            String mensaje = construirMensajeExito(eventoCreado);
            mostrarAlerta("Evento creado exitosamente", mensaje, Alert.AlertType.INFORMATION);
            
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
        
        // Validar cupo máximo si requiere inscripción
        if (chkRequiereInscripcion.isSelected()) {
            if (txtCupoMaximo.getText().trim().isEmpty()) {
                mostrarAlerta("Campo requerido", "El cupo máximo es obligatorio para eventos que requieren inscripción.", Alert.AlertType.WARNING);
                txtCupoMaximo.requestFocus();
                return false;
            }
            
            try {
                int cupo = Integer.parseInt(txtCupoMaximo.getText().trim());
                if (cupo <= 0) {
                    mostrarAlerta("Cupo inválido", "El cupo máximo debe ser mayor a 0.", Alert.AlertType.WARNING);
                    txtCupoMaximo.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Cupo inválido", "El cupo máximo debe ser un número válido.", Alert.AlertType.WARNING);
                txtCupoMaximo.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    private String construirMensajeExito(Evento evento) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Evento creado exitosamente:\n\n");
        mensaje.append("ID: ").append(evento.getId()).append("\n");
        mensaje.append("Nombre: ").append(evento.getNombre()).append("\n");
        mensaje.append("Tipo: ").append(evento.getClass().getSimpleName()).append("\n");
        mensaje.append("Fecha: ").append(evento.getFechaInicio()).append("\n");
        mensaje.append("Duración: ").append(evento.getDuracionEstimada()).append(" horas\n");
        mensaje.append("Estado: ").append(evento.getEstado()).append("\n");
        mensaje.append("Requiere inscripción: ").append(evento.isRequiereInscripcion() ? "Sí" : "No").append("\n");
        
        if (evento.isRequiereInscripcion() && evento.getCupoMaximoDefinido() != null) {
            mensaje.append("Cupo máximo: ").append(evento.getCupoMaximoDefinido()).append("\n");
        }
        
        return mensaje.toString();
    }
    
    private void limpiarFormulario() {
        // Limpiar campos básicos
        txtNombre.clear();
        cbTipoEvento.setValue(null);
        dpFechaInicio.setValue(null);
        txtDuracion.clear();
        txtCupoMaximo.clear();
        
        // Resetear inscripción
        chkRequiereInscripcion.setSelected(false);
        chkRequiereInscripcion.setDisable(false);
        txtCupoMaximo.setDisable(true);
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
               !txtDuracion.getText().trim().isEmpty() ||
               !txtCupoMaximo.getText().trim().isEmpty();
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
