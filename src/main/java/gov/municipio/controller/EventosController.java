package gov.municipio.controller;


import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import gov.municipio.domain.model.enums.EstadoEvento;
 import gov.municipio.domain.model.evento.Evento;


import java.net.URL;
import java.util.ResourceBundle;

public class EventosController implements Initializable {
    @FXML
    private TextField txtBuscar;

    @FXML
    private ComboBox<EstadoEvento> cbEstado;

    @FXML
    private ComboBox<String> cbTipo;

    @FXML
    private Button btnNuevo;

    @FXML
    private TableView<Evento> tblEventos;

    @FXML
    private TableColumn<Evento, String> colNombre;

    @FXML
    private TableColumn<Evento, String> colFecha;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicialización de componentes y carga de datos
        cbEstado.getItems().setAll(EstadoEvento.values());
        cbTipo.getItems().addAll("Concierto", "Ciclo de cine","Exposición", "Taller", "Feria");
        // Configurar columnas
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colFecha.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFechaInicio().toString()));

    }

}
