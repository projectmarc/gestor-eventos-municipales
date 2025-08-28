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
    
    public void goToGestionarEvento(gov.municipio.domain.model.evento.Evento evento) {
        try {
            // Recargar el evento con las participaciones para evitar LazyInitializationException
            gov.municipio.domain.model.evento.Evento eventoConParticipaciones = 
                services.eventoService().buscarEventoPorIdConParticipaciones(evento.getId())
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Screen.GESTIONAR_EVENTO.getFxmlPath()));
            
            loader.setControllerFactory(type -> {
                if (type == GestionarEventoController.class) {
                    GestionarEventoController controller = new GestionarEventoController();
                    controller.setRouter(this);
                    controller.setServiceContainer(services);
                    return controller;
                }
                return null;
            });
            
            Parent view = loader.load();
            GestionarEventoController controller = loader.getController();
            controller.setEvento(eventoConParticipaciones);
            
            container.getChildren().setAll(view);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la pantalla gestionar evento", e);
        }
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
                    return new InicioController(services.personaService(), services.eventoService());
                }
                if (type == EventosController.class) {
                    EventosController controller = new EventosController();
                    controller.setRouter(this);
                    controller.setServiceContainer(services);
                    return controller;
                }
                if (type == CrearEventoController.class) {
                    CrearEventoController controller = new CrearEventoController();
                    controller.setRouter(this);
                    controller.setServiceContainer(services);
                    return controller;
                }
                if (type == GestionarEventoController.class) {
                    GestionarEventoController controller = new GestionarEventoController();
                    controller.setRouter(this);
                    controller.setServiceContainer(services);
                    return controller;
                }
                if (type == PeliculasController.class) {
                    return new PeliculasController(services.peliculaService());
                }
                if (type == ArtistasBandasController.class) {
                    return new ArtistasBandasController(services.artistaBandaService());
                }
                if (type == CalendarioController.class) {
                    CalendarioController controller = new CalendarioController();
                    controller.setRouter(this);
                    controller.setServiceContainer(services);
                    return controller;
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

