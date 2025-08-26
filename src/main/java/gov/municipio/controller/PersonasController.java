package gov.municipio.controller;

import gov.municipio.domain.model.Persona;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;

public class PersonasController {
    
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDni;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private Button btnRegistrar;
    @FXML private TableView<Persona> tblPersonas;
    
    @FXML
    private void initialize() {
        // Inicialización del controlador
    }

    @FXML
    private void onRegistrarPersona() {
        // Limpiar los campos
        limpiarCampos();
    }

    public void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtDni.clear();
        txtTelefono.clear();
        txtEmail.clear();
    }


}
