package gov.municipio.ui.nav;

public enum Screen {
    MAIN("/gov/municipio/view/fxml/main-view.fxml", "Principal"),
    INICIO("/gov/municipio/view/fxml/inicio.fxml", "Inicio"),
    EVENTOS("/gov/municipio/view/fxml/eventos.fxml", "Eventos"),
    GESTIONAR_EVENTO("/gov/municipio/view/fxml/gestionar-evento.fxml", "Gestionar Evento"),
    CREAR_EVENTO("/gov/municipio/view/fxml/crear-evento.fxml", "Crear Evento"),
    PERSONAS("/gov/municipio/view/fxml/personas.fxml", "Personas"),
    PELICULAS("/gov/municipio/view/fxml/peliculas.fxml", "Películas"),
    ARTISTAS_BANDAS("/gov/municipio/view/fxml/artistas-bandas.fxml", "Artistas/Bandas");

    private String fxmlPath;
    private String title;

    Screen(String fxmlPath, String title) {
        this.fxmlPath = fxmlPath;
        this.title = title;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public String getTitle() {
        return title;
    }
}
