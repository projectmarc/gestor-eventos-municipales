package gov.municipio.ui.nav;

import gov.municipio.config.ServiceContainer;
import gov.municipio.controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.lang.reflect.Constructor;

public class Router {
    private final StackPane container;
    private final ServiceContainer services;

    public Router(StackPane container, ServiceContainer services) {
        this.container = container;
        this.services = services;
    }

    public void goTo(Screen screen) {
        Parent view = loadView(screen.getFxmlPath());
        container.getChildren().setAll(view);
    }

    private Parent loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            loader.setControllerFactory(type -> {
                // Reglas de inyección por tipo — se agregan más según crezcan los controladores
                if (type == PersonasController.class) {
                    return new PersonasController(services.personaService());
                }
                if (type == MainController.class) {
                    return new MainController(services);
                }
                if (type == InicioController.class) {
                    return new InicioController(services.personaService());
                }
                if (type == EventosController.class) {
                    EventosController controller = new EventosController();
                    controller.setRouter(this);
                    return controller;
                }
                if (type == CrearEventoController.class) {
                    CrearEventoController controller = new CrearEventoController();
                    controller.setRouter(this);
                    return controller;
                }
                if (type == PeliculasController.class) {
                    return new PeliculasController();
                }

                // Fallback: intentar constructor sin args (para pantallas que no necesiten servicios)
                try {
                    Constructor<?> ctor = type.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    return ctor.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("No se pudo crear el controlador: " + type, e);
                }
            });

            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar " + fxmlPath, e);
        }
    }
}

