package gov.municipio.controller;

import gov.municipio.application.PersonaService;
import gov.municipio.domain.model.Persona;
import gov.municipio.domain.model.enums.Rol;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

public class BuscarPersonaDialogController {
    
    @FXML
    private TextField txtDni;
    
    @FXML
    private Label lblError;
    
    @FXML
    private Button btnAceptar;
    
    @FXML
    private Button btnCancelar;
    
    private PersonaService personaService;
    private Persona personaSeleccionada;
    private Rol rolSeleccionado;
    private boolean resultado = false;
    
    public void setPersonaService(PersonaService personaService) {
        this.personaService = personaService;
    }
    
    public void setRol(Rol rol) {
        this.rolSeleccionado = rol;
    }
    
    @FXML
    private void initialize() {
        // Limpiar cualquier error cuando se cambie el texto
        txtDni.textProperty().addListener((obs, oldText, newText) -> {
            lblError.setVisible(false);
            lblError.setManaged(false);
        });
    }
    
    @FXML
    private void aceptar() {
        String dni = txtDni.getText().trim();
        
        if (dni.isEmpty()) {
            mostrarError("Debe ingresar un DNI");
            return;
        }
        
        try {
            Optional<Persona> personaOpt = personaService.buscarPersonaPorDni(dni);
            
            if (personaOpt.isPresent()) {
                personaSeleccionada = personaOpt.get();
                resultado = true;
                cerrarDialog();
            } else {
                mostrarError("No se encontró una persona con DNI: " + dni);
            }
        } catch (Exception e) {
            mostrarError("Error al buscar persona: " + e.getMessage());
        }
    }
    
    @FXML
    private void cancelar() {
        resultado = false;
        cerrarDialog();
    }
    
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
    
    private void cerrarDialog() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
    
    public boolean getResultado() {
        return resultado;
    }
    
    public Persona getPersonaSeleccionada() {
        return personaSeleccionada;
    }
    
    public Rol getRolSeleccionado() {
        return rolSeleccionado;
    }
}
