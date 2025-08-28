package gov.municipio.controller;

import gov.municipio.application.PersonaService;
import gov.municipio.domain.model.Persona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class PersonasController {

    private final PersonaService personaService; // ✅ final + obligatorio

    // Campos del formulario
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDni;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private Button btnRegistrar;
    @FXML private Button btnLimpiar;
    
    // Botones de acciones
    @FXML private Button btnEditar;
    @FXML private Button btnBorrar;
    
    // Tabla y columnas
    @FXML private TableView<Persona> tblPersonas;
    @FXML private TableColumn<Persona, Long> colId;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colApellido;
    @FXML private TableColumn<Persona, String> colDni;
    @FXML private TableColumn<Persona, String> colTelefono;
    @FXML private TableColumn<Persona, String> colEmail;
    
    // Lista observable para la tabla
    private ObservableList<Persona> personas;
    
    // Persona seleccionada para edición
    private Persona personaEnEdicion = null;

    // 👇 JavaFX NO llama este constructor por defecto; se lo indicamos con controllerFactory
    public PersonasController(PersonaService personaService) {
        this.personaService = personaService;
    }


    @FXML
    private void initialize() {
        // Configurar las columnas de la tabla
        configurarTabla();
        
        // Cargar datos iniciales
        cargarPersonas();
        
        // Configurar selección de tabla para edición
        configurarSeleccionTabla();
    }
    
    /**
     * Configura las columnas de la tabla para mostrar los datos de las personas.
     */
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Configurar ancho de columnas
        colId.setPrefWidth(50);
        colNombre.setPrefWidth(120);
        colApellido.setPrefWidth(120);
        colDni.setPrefWidth(80);
        colTelefono.setPrefWidth(100);
        colEmail.setPrefWidth(180);
    }
    
    /**
     * Configura la selección de la tabla para habilitar edición.
     */
    private void configurarSeleccionTabla() {
        tblPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            btnEditar.setDisable(!haySeleccion);
            btnBorrar.setDisable(!haySeleccion);
        });
        
        // Inicialmente deshabilitar botones de acción
        btnEditar.setDisable(true);
        btnBorrar.setDisable(true);
    }
    
    /**
     * Carga todas las personas desde el servicio y las muestra en la tabla.
     */
    private void cargarPersonas() {
        try {
            List<Persona> listaPersonas = personaService.obtenerTodasLasPersonas();
            
            if (personas == null) {
                personas = FXCollections.observableArrayList(listaPersonas);
                tblPersonas.setItems(personas);
            } else {
                personas.clear();
                personas.addAll(listaPersonas);
            }
            
            System.out.println("Cargadas " + listaPersonas.size() + " personas en la tabla");
            
        } catch (Exception e) {
            e.printStackTrace();
            error("Error al cargar datos", "No se pudieron cargar las personas: " + e.getMessage());
        }
    }

    @FXML
    private void onRegistrarPersona() {
        String nombre   = txtNombre.getText();
        String apellido = txtApellido.getText();
        String dni      = txtDni.getText();
        String tel      = txtTelefono.getText();
        String email    = txtEmail.getText();

        try {
            if (personaEnEdicion == null) {
                // Crear nueva persona
                Persona p = personaService.registrarPersona(nombre, apellido, dni, tel, email);
                info("Persona registrada", "Se registró a " + apellido + ", " + nombre + " con ID: " + p.getId());
                // Recargar la tabla para mostrar la nueva persona
                cargarPersonas();
            } else {
                // Actualizar persona existente
                Persona personaActualizada = personaService.actualizarPersona(personaEnEdicion.getId(), nombre, apellido, dni, tel, email);
                info("Persona actualizada", "Se actualizó a " + apellido + ", " + nombre);
                
                // Encontrar y actualizar el objeto en la lista observable
                for (int i = 0; i < personas.size(); i++) {
                    if (personas.get(i).getId().equals(personaEnEdicion.getId())) {
                        personas.set(i, personaActualizada);
                        break;
                    }
                }
                
                // Forzar refresh de la tabla
                tblPersonas.refresh();
            }
            
            limpiarFormulario();
            
        } catch (IllegalArgumentException e){
            error("Datos inválidos", e.getMessage());
        } catch (IllegalStateException e){
            error("No se pudo procesar", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            error("Error inesperado", "Ocurrió un error al procesar la persona.");
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtApellido.clear();
        txtDni.clear();
        txtTelefono.clear();
        txtEmail.clear();
        personaEnEdicion = null;
        btnRegistrar.setText("Crear");
        tblPersonas.getSelectionModel().clearSelection();
        txtNombre.requestFocus();
    }
    
    @FXML
    private void onEditarPersona() {
        Persona personaSeleccionada = tblPersonas.getSelectionModel().getSelectedItem();
        if (personaSeleccionada != null) {
            personaEnEdicion = personaSeleccionada;
            txtNombre.setText(personaSeleccionada.getNombre());
            txtApellido.setText(personaSeleccionada.getApellido());
            txtDni.setText(personaSeleccionada.getDni());
            txtTelefono.setText(personaSeleccionada.getTelefono());
            txtEmail.setText(personaSeleccionada.getEmail());
            
            btnRegistrar.setText("Actualizar");
        }
    }
    
    @FXML
    private void onBorrarPersona() {
        Persona personaSeleccionada = tblPersonas.getSelectionModel().getSelectedItem();
        if (personaSeleccionada != null) {
            // Crear un Alert de confirmación
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Estás seguro de que deseas eliminar esta persona?");
            alert.setContentText("Nombre: " + personaSeleccionada.getNombre() + " " + personaSeleccionada.getApellido());
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        personaService.eliminar(personaSeleccionada.getId());
                        info("Éxito", "Persona eliminada exitosamente");
                        cargarPersonas();
                        limpiarFormulario();
                    } catch (Exception e) {
                        error("Error", "Error al eliminar la persona: " + e.getMessage());
                    }
                }
            });
        }
    }
    
    @FXML
    private void onLimpiarFormulario() {
        limpiarFormulario();
    }

    private void error(String titulo, String detalle){
        Alert a = new Alert(Alert.AlertType.ERROR, detalle, ButtonType.OK);
        a.setHeaderText(titulo);
        a.showAndWait();
    }
    private void info(String titulo, String detalle){
        Alert a = new Alert(Alert.AlertType.INFORMATION, detalle, ButtonType.OK);
        a.setHeaderText(titulo);
        a.showAndWait();
    }
}
