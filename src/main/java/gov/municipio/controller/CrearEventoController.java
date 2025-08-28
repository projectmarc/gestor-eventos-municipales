package gov.municipio.controller;

import gov.municipio.domain.model.enums.EstadoEvento;
import gov.municipio.ui.nav.Router;
import gov.municipio.ui.nav.Screen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class CrearEventoController {
    
    // Información básica
    @FXML private TextField txtNombre;
    @FXML private DatePicker dpFechaInicio;
    @FXML private TextField txtDuracion;
    
    // Inscripción y participantes
    @FXML private CheckBox chkRequiereInscripcion;
    @FXML private TextField txtCupoMaximo;
    
    // Tipo de evento
    @FXML private ComboBox<String> cbTipoEvento;
    
    // Paneles específicos por tipo de evento
    @FXML private VBox paneFeria;
    @FXML private VBox paneConcierto;
    @FXML private VBox paneExposicion;
    @FXML private VBox paneTaller;
    @FXML private VBox paneCicloCine;
    @FXML private VBox paneNoTipo;
    
    // Controles específicos de Feria
    @FXML private TextField txtCantidadStands;
    @FXML private RadioButton rbAireLibre;
    @FXML private RadioButton rbTechada;
    @FXML private ToggleGroup tgUbicacionFeria;
    
    // Controles específicos de Concierto
    @FXML private TextField txtGeneroMusical;
    @FXML private TextField txtCapacidadMaxima;
    
    // Controles específicos de Exposición
    @FXML private ComboBox<String> cbTipoArte;
    
    // Controles específicos de Taller
    @FXML private TextField txtCupoTaller;
    @FXML private RadioButton rbPresencial;
    @FXML private RadioButton rbVirtual;
    @FXML private ToggleGroup tgModalidadTaller;
    
    // Controles específicos de Ciclo de Cine
    @FXML private ListView<String> lvPeliculas;
    @FXML private Button btnAgregarPelicula;
    @FXML private Button btnQuitarPelicula;
    @FXML private Button btnSubirPelicula;
    @FXML private Button btnBajarPelicula;
    @FXML private CheckBox chkCharlasPosteriores;
    
    // Botones de acción
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;
    
    private Router router;
    
    // Lista observables para los ListView (solo películas)
    private ObservableList<String> peliculas;
    
    public CrearEventoController() {
        // Constructor vacío para el patrón actual
    }
    
    public void setRouter(Router router) {
        this.router = router;
    }
    
    @FXML
    private void initialize() {
        configurarComboBoxes();
        configurarListViews();
        configurarEventHandlers();
        configurarValidaciones();
        inicializarPaneles();
    }
    
    private void configurarComboBoxes() {
        // ComboBox de tipos de evento
        cbTipoEvento.setItems(FXCollections.observableArrayList(
            "Feria", "Concierto", "Exposición", "Taller", "Ciclo de cine"
        ));
        
        // ComboBox de tipos de arte (Exposición)
        cbTipoArte.setItems(FXCollections.observableArrayList(
            "Pintura", "Escultura", "Fotografía", "Arte Digital", "Arte Contemporáneo", 
            "Arte Clásico", "Instalaciones", "Performance"
        ));
    }
    
    private void configurarListViews() {
        // Inicializar solo la lista de películas
        peliculas = FXCollections.observableArrayList();
        
        // Asignar al ListView
        lvPeliculas.setItems(peliculas);
    }
    
    private void configurarEventHandlers() {
        // Handler para cambio de tipo de evento
        cbTipoEvento.setOnAction(event -> mostrarPanelEspecifico());
        
        // Handler para checkbox de inscripción
        chkRequiereInscripcion.setOnAction(event -> {
            boolean requiere = chkRequiereInscripcion.isSelected();
            txtCupoMaximo.setDisable(!requiere);
        });
        
        // Handlers para botones de películas
        btnAgregarPelicula.setOnAction(event -> agregarPelicula());
        btnQuitarPelicula.setOnAction(event -> quitarPelicula());
        btnSubirPelicula.setOnAction(event -> moverPeliculaArriba());
        btnBajarPelicula.setOnAction(event -> moverPeliculaAbajo());
        
        // Handlers para botones principales
        btnCancelar.setOnAction(event -> cancelar());
        btnGuardar.setOnAction(event -> guardarEvento());
    }
    
    private void configurarValidaciones() {
        // Deshabilitar cupo máximo inicialmente
        txtCupoMaximo.setDisable(true);
        
        // Configurar validaciones de texto numérico
        configurarCampoNumerico(txtDuracion);
        configurarCampoNumerico(txtCupoMaximo);
        configurarCampoNumerico(txtCantidadStands);
        configurarCampoNumerico(txtCupoTaller);
    }
    
    private void configurarCampoNumerico(TextField campo) {
        campo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                campo.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    private void inicializarPaneles() {
        // Ocultar todos los paneles específicos al inicio
        ocultarTodosLosPanelesEspecificos();
        // Mostrar panel de "no tipo seleccionado"
        paneNoTipo.setVisible(true);
        paneNoTipo.setManaged(true);
    }
    
    private void mostrarPanelEspecifico() {
        String tipoSeleccionado = cbTipoEvento.getValue();
        
        // Ocultar todos los paneles primero
        ocultarTodosLosPanelesEspecificos();
        
        if (tipoSeleccionado == null) {
            paneNoTipo.setVisible(true);
            paneNoTipo.setManaged(true);
            return;
        }
        
        // Mostrar el panel correspondiente
        switch (tipoSeleccionado) {
            case "Feria":
                paneFeria.setVisible(true);
                paneFeria.setManaged(true);
                break;
            case "Concierto":
                paneConcierto.setVisible(true);
                paneConcierto.setManaged(true);
                break;
            case "Exposición":
                paneExposicion.setVisible(true);
                paneExposicion.setManaged(true);
                break;
            case "Taller":
                paneTaller.setVisible(true);
                paneTaller.setManaged(true);
                break;
            case "Ciclo de cine":
                paneCicloCine.setVisible(true);
                paneCicloCine.setManaged(true);
                break;
        }
    }
    
    private void ocultarTodosLosPanelesEspecificos() {
        VBox[] paneles = {paneFeria, paneConcierto, paneExposicion, paneTaller, paneCicloCine, paneNoTipo};
        for (VBox panel : paneles) {
            panel.setVisible(false);
            panel.setManaged(false);
        }
    }
    
    // Métodos para manejar películas
    private void agregarPelicula() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar Película");
        dialog.setHeaderText("Nueva película");
        dialog.setContentText("Título de la película:");
        
        dialog.showAndWait().ifPresent(titulo -> {
            if (!titulo.trim().isEmpty() && !peliculas.contains(titulo.trim())) {
                peliculas.add(titulo.trim());
            }
        });
    }
    
    private void quitarPelicula() {
        String seleccionada = lvPeliculas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            peliculas.remove(seleccionada);
        } else {
            mostrarAlerta("Selección requerida", "Debe seleccionar una película para quitar.", Alert.AlertType.WARNING);
        }
    }
    
    private void moverPeliculaArriba() {
        int indiceSeleccionado = lvPeliculas.getSelectionModel().getSelectedIndex();
        if (indiceSeleccionado > 0) {
            String pelicula = peliculas.remove(indiceSeleccionado);
            peliculas.add(indiceSeleccionado - 1, pelicula);
            lvPeliculas.getSelectionModel().select(indiceSeleccionado - 1);
        }
    }
    
    private void moverPeliculaAbajo() {
        int indiceSeleccionado = lvPeliculas.getSelectionModel().getSelectedIndex();
        if (indiceSeleccionado >= 0 && indiceSeleccionado < peliculas.size() - 1) {
            String pelicula = peliculas.remove(indiceSeleccionado);
            peliculas.add(indiceSeleccionado + 1, pelicula);
            lvPeliculas.getSelectionModel().select(indiceSeleccionado + 1);
        }
    }
    
    // Métodos principales
    private void guardarEvento() {
        try {
            if (!validarDatosBasicos()) {
                return;
            }
            
            // Aquí iría la lógica para crear el evento según el tipo seleccionado
            // Por ahora solo mostramos la información que se capturaría
            
            String mensaje = construirMensajeResumen();
            mostrarAlerta("Evento creado", mensaje, Alert.AlertType.INFORMATION);
            
            // Limpiar formulario después de guardar
            limpiarFormulario();
            
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al guardar el evento: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private boolean validarDatosBasicos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "El nombre del evento es obligatorio.", Alert.AlertType.WARNING);
            txtNombre.requestFocus();
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
        
        if (cbTipoEvento.getValue() == null) {
            mostrarAlerta("Campo requerido", "Debe seleccionar un tipo de evento.", Alert.AlertType.WARNING);
            cbTipoEvento.requestFocus();
            return false;
        }
        
        // Validaciones específicas por tipo de evento
        return validarDatosEspecificos();
    }
    
    private boolean validarDatosEspecificos() {
        String tipoEvento = cbTipoEvento.getValue();
        
        switch (tipoEvento) {
            case "Feria":
                if (txtCantidadStands.getText().trim().isEmpty()) {
                    mostrarAlerta("Campo requerido", "La cantidad de stands es obligatoria para ferias.", Alert.AlertType.WARNING);
                    txtCantidadStands.requestFocus();
                    return false;
                }
                if (tgUbicacionFeria.getSelectedToggle() == null) {
                    mostrarAlerta("Campo requerido", "Debe seleccionar el tipo de ubicación para la feria.", Alert.AlertType.WARNING);
                    return false;
                }
                break;
                
            case "Taller":
                if (txtCupoTaller.getText().trim().isEmpty()) {
                    mostrarAlerta("Campo requerido", "El cupo máximo es obligatorio para talleres.", Alert.AlertType.WARNING);
                    txtCupoTaller.requestFocus();
                    return false;
                }
                if (tgModalidadTaller.getSelectedToggle() == null) {
                    mostrarAlerta("Campo requerido", "Debe seleccionar la modalidad del taller.", Alert.AlertType.WARNING);
                    return false;
                }
                break;
                
            case "Concierto":
                if (txtGeneroMusical.getText().trim().isEmpty()) {
                    mostrarAlerta("Campo requerido", "El género musical es obligatorio para conciertos.", Alert.AlertType.WARNING);
                    txtGeneroMusical.requestFocus();
                    return false;
                }
                if (txtCapacidadMaxima.getText().trim().isEmpty()) {
                    mostrarAlerta("Campo requerido", "La capacidad máxima es obligatoria para conciertos.", Alert.AlertType.WARNING);
                    txtCapacidadMaxima.requestFocus();
                    return false;
                }
                break;
                
            case "Ciclo de cine":
                if (peliculas.isEmpty()) {
                    mostrarAlerta("Campo requerido", "Debe agregar al menos una película para el ciclo de cine.", Alert.AlertType.WARNING);
                    return false;
                }
                break;
        }
        
        return true;
    }
    
    private String construirMensajeResumen() {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Evento: ").append(txtNombre.getText()).append("\n");
        mensaje.append("Tipo: ").append(cbTipoEvento.getValue()).append("\n");
        mensaje.append("Fecha: ").append(dpFechaInicio.getValue()).append("\n");
        
        if (!txtDuracion.getText().trim().isEmpty()) {
            mensaje.append("Duración: ").append(txtDuracion.getText()).append(" horas\n");
        }
        
        mensaje.append("Estado: ").append(EstadoEvento.PLANIFICACION).append("\n");
        mensaje.append("Requiere inscripción: ").append(chkRequiereInscripcion.isSelected() ? "Sí" : "No").append("\n");
        
        if (chkRequiereInscripcion.isSelected() && !txtCupoMaximo.getText().trim().isEmpty()) {
            mensaje.append("Cupo máximo: ").append(txtCupoMaximo.getText()).append("\n");
        }
        
        mensaje.append("\nNota: Los responsables del evento se pueden agregar después de la creación.");
        
        return mensaje.toString();
    }
    
    private void limpiarFormulario() {
        // Limpiar campos básicos
        txtNombre.clear();
        dpFechaInicio.setValue(null);
        txtDuracion.clear();
        
        // Limpiar inscripción
        chkRequiereInscripcion.setSelected(false);
        txtCupoMaximo.clear();
        txtCupoMaximo.setDisable(true);
        
        // Limpiar solo lista de películas
        peliculas.clear();
        
        // Limpiar tipo de evento
        cbTipoEvento.setValue(null);
        
        // Limpiar campos específicos
        limpiarCamposEspecificos();
        
        // Reinicializar paneles
        inicializarPaneles();
    }
    
    private void limpiarCamposEspecificos() {
        // Feria
        txtCantidadStands.clear();
        if (tgUbicacionFeria != null) {
            tgUbicacionFeria.selectToggle(null);
        }
        
        // Concierto
        txtGeneroMusical.clear();
        txtCapacidadMaxima.clear();
        
        // Exposición
        cbTipoArte.setValue(null);
        
        // Taller
        txtCupoTaller.clear();
        if (tgModalidadTaller != null) {
            tgModalidadTaller.selectToggle(null);
        }
        
        // Ciclo de cine
        chkCharlasPosteriores.setSelected(false);
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
               dpFechaInicio.getValue() != null ||
               !txtDuracion.getText().trim().isEmpty() ||
               cbTipoEvento.getValue() != null;
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
