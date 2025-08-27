package gov.municipio.controller;

import gov.municipio.application.PersonaService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InicioController {
    
    private final PersonaService personaService;
    
    @FXML private Label lblCantidadPersonas;
    
    public InicioController(PersonaService personaService) {
        this.personaService = personaService;
    }
    
    @FXML
    private void initialize() {
        cargarMetricas();
    }
    
    private void cargarMetricas() {
        try {
            // Obtener la cantidad de personas registradas
            int cantidadPersonas = personaService.obtenerTodasLasPersonas().size();
            lblCantidadPersonas.setText(String.valueOf(cantidadPersonas));
            
        } catch (Exception e) {
            e.printStackTrace();
            lblCantidadPersonas.setText("Error");
        }
    }
}
