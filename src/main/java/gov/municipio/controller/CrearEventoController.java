package gov.municipio.controller;

import gov.municipio.ui.nav.Router;
import gov.municipio.ui.nav.Screen;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class CrearEventoController {
    
    @FXML
    private Button btnVolver;
    
    private Router router;
    
    public CrearEventoController() {
        // Constructor vacío para el patrón actual
    }
    
    public void setRouter(Router router) {
        this.router = router;
    }
    
    @FXML
    private void volverAEventos() {
        if (router != null) {
            router.goTo(Screen.EVENTOS);
        }
    }
}
