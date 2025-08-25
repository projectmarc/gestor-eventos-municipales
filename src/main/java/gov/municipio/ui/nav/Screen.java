package gov.municipio.ui.nav;

public enum Screen {
    // Cuando uno quiere agregar una nueva dirección de navegación, solo hay que agregarla acá
    INICIO("/gov/municipio/view/fxml/inicio.fxml", "Inicio"),
    EVENTOS("/gov/municipio/view/fxml/eventos.fxml", "Eventos");

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
