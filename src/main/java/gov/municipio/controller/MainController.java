package gov.municipio.controller;

import gov.municipio.ui.nav.Router;
import gov.municipio.ui.nav.Screen;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class MainController {
    @FXML private StackPane contentStack;
    @FXML private NavbarController navbarController;

    private Router router;

   @FXML
   private void initialize() {
       router = new Router(contentStack);
       if (navbarController != null) navbarController.setRouter(router);
       router.goTo(Screen.INICIO); // <- nada de lblPlanificacion acá
   }
}