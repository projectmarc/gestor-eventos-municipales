package gov.municipio.ui.nav;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class Router {
    private final StackPane container;

    public Router(StackPane container) {
        this.container = container;
    }

    public void goTo(Screen screen) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(screen.getFxmlPath()));
            container.getChildren().setAll(view);
        } catch (IOException e) {
            // re-lanzamos como unchecked para no propagar IOException
            throw new RuntimeException("No se pudo cargar la vista: " + screen, e);
        }
    }
}
