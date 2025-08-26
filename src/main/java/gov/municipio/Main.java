package gov.municipio;

import gov.municipio.config.ServiceContainer;
import gov.municipio.ui.nav.Router;
import gov.municipio.ui.nav.Screen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ServiceContainer services = new ServiceContainer();

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Gestor de Eventos Municipales");
        stage.show();

        Router router = new Router(root, services);
        // Cargar pantalla inicial
        router.goTo(Screen.MAIN);
    }

    public static void main(String[] args) { launch(args); }
}
