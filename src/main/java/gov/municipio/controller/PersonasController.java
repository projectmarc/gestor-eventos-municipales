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
     * Carga todas las personas desde el servicio y las muestra en la tabla.
     */
    private void cargarPersonas() {
        try {
            List<Persona> listaPersonas = personaService.obtenerTodasLasPersonas();
            personas = FXCollections.observableArrayList(listaPersonas);
            tblPersonas.setItems(personas);
            
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
            Persona p = personaService.registrarPersona(nombre, apellido, dni, tel, email);
            limpiarCampos();
            
            // Recargar la tabla para mostrar la nueva persona
            cargarPersonas();
            
            info("Persona registrada", "Se registró a " + apellido + ", " + nombre + " con ID: " + p.getId());
        } catch (IllegalArgumentException e){
            error("Datos inválidos", e.getMessage());
        } catch (IllegalStateException e){
            error("No se pudo registrar", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            error("Error inesperado", "Ocurrió un error al registrar la persona.");
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtDni.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtNombre.requestFocus();
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
