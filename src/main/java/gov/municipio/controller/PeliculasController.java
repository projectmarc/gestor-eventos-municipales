package gov.municipio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class PeliculasController {

    @FXML
    private TextField txtNombre;
    
    @FXML
    private TextField txtDuracionHoras;
    
    @FXML
    private Button btnRegistrar;
    
    @FXML
    private Button btnLimpiar;
    
    @FXML
    private Button btnEditar;
    
    @FXML
    private Button btnBorrar;
    
    @FXML
    private TableView<?> tblPeliculas;
    
    @FXML
    private TableColumn<?, ?> colId;
    
    @FXML
    private TableColumn<?, ?> colNombre;
    
    @FXML
    private TableColumn<?, ?> colDuracionHoras;
    
    @FXML
    private TableColumn<?, ?> colDuracionFmt;

    @FXML
    private void initialize() {
        // Inicialización de la vista
        // Por ahora vacío, se puede implementar lógica aquí más adelante
    }
}
