package gov.municipio.ui.nav;

public enum Screen {
    MAIN("/gov/municipio/view/fxml/main-view.fxml", "Principal"),
    INICIO("/gov/municipio/view/fxml/inicio.fxml", "Inicio"),
    EVENTOS("/gov/municipio/view/fxml/eventos.fxml", "Eventos"),
    PERSONAS("/gov/municipio/view/fxml/personas.fxml", "Personas");

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
