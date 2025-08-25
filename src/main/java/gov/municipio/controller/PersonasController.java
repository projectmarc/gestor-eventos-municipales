package gov.municipio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PersonasController {
    
    @FXML private Label lblTitulo;
    @FXML private Button btnAgregar;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbRol;
    
    @FXML
    private void initialize() {
        // Inicialización del controlador
    }
}
