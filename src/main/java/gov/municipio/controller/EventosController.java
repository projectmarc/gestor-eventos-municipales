package gov.municipio.controller;

import gov.municipio.domain.model.evento.Evento;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import gov.municipio.domain.model.enums.EstadoEvento;



public class EventosController {
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
}
