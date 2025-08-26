package gov.municipio.controller;

import gov.municipio.ui.nav.Router;
import gov.municipio.ui.nav.Screen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class NavbarController {
    private Router router;
    public void setRouter(Router router) { this.router = router; }

    @FXML
    private void onNavigate(ActionEvent e) {
        if (router == null) return;
        Object src = e.getSource();
        if (src instanceof Button b && b.getUserData() != null) {
            try {
                Screen screen = Screen.valueOf(b.getUserData().toString());
                router.goTo(screen);
            } catch (IllegalArgumentException ex) {
                System.err.println("Screen no encontrado: " + b.getUserData());
                // Opcional: mostrar una alerta al usuario
            }
        }
    }
}
