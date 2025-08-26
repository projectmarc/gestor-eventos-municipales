package gov.municipio.controller;

import gov.municipio.config.ServiceContainer;
import gov.municipio.ui.nav.Router;
import gov.municipio.ui.nav.Screen;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class MainController {
    @FXML private StackPane contentStack;
    @FXML private NavbarController navbarController;

    private Router router;
    private final ServiceContainer services;

    public MainController(ServiceContainer services) {
        this.services = services;
    }

    @FXML
    private void initialize() {
        router = new Router(contentStack, services);
        if (navbarController != null) {
            navbarController.setRouter(router);
        }
        router.goTo(Screen.INICIO);
    }
}