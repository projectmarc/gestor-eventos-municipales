package gov.municipio.controller;

import gov.municipio.ui.nav.Router;
import gov.municipio.ui.nav.Screen;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class EventosController {
    
    @FXML
    private Button btnCrearEvento;
    
    private Router router;
    
    public EventosController() {
        // Constructor vacío para el patrón actual
    }
    
    public void setRouter(Router router) {
        this.router = router;
    }
    
    @FXML
    private void crearEvento() {
        if (router != null) {
            router.goTo(Screen.CREAR_EVENTO);
        }
    }
}
