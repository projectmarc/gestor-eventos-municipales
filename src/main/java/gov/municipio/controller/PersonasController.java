package gov.municipio.controller;

import gov.municipio.application.PersonaService;
import gov.municipio.domain.model.Persona;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PersonasController {

    private final PersonaService personaService; // ✅ final + obligatorio

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDni;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private Button btnRegistrar;

    // 👇 JavaFX NO llama este constructor por defecto; se lo indicamos con controllerFactory
    public PersonasController(PersonaService personaService) {
        this.personaService = personaService;
    }


    @FXML
    private void initialize() {
        // Nada por ahora
    }

    @FXML
    private void onRegistrarPersona() {
        String nombre   = txtNombre.getText();
        String apellido = txtApellido.getText();
        String dni      = txtDni.getText();
        String tel      = txtTelefono.getText();
        String email    = txtEmail.getText();

        try {
            Persona p = new Persona(nombre, apellido, dni, tel, email);
            personaService.registrar(p);
            limpiarCampos();
            info("Persona registrada", "Se registró a " + apellido + ", " + nombre);
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
