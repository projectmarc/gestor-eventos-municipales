package gov.municipio.ui.nav;

public enum Screen {
    INICIO("/gov/municipio/view/fxml/inicio.fxml", "Inicio"),
    EVENTOS("/gov/municipio/view/fxml/eventos.fxml", "Eventos"),
    PERSONAS("/gov/municipio/view/fxml/personas.fxml", "Personas"),
    CALENDARIO("/gov/municipio/view/fxml/calendario.fxml", "Calendario");

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
