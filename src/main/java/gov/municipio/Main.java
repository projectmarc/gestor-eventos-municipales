package gov.municipio;

import gov.municipio.domain.service.EventoService;
import gov.municipio.domain.service.PersonaService;
import gov.municipio.util.DataInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Inicializar datos de prueba
        DataInitializer dataInitializer = new DataInitializer();
        dataInitializer.inicializarDatosPrueba();
        
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/gov/municipio/view/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Gestor de Eventos Municipales");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            // Cerrar las conexiones JPA al cerrar la aplicación
            PersonaService.getInstance().cerrar();
            EventoService.getInstance().cerrar();
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}