package gov.municipio.controller;

import gov.municipio.domain.model.evento.*;
import gov.municipio.domain.model.enums.EstadoEvento;
import gov.municipio.domain.service.EventoService;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class EventosController implements Initializable {
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<EstadoEvento> cbEstado;
    @FXML private ComboBox<String> cbTipo;
    @FXML private Button btnNuevo;
    @FXML private TableView<Evento> tblEventos;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, String> colFecha;
    @FXML private TableColumn<Evento, String> colEstado;
    @FXML private TableColumn<Evento, String> colTipo;
    @FXML private TableColumn<Evento, String> colRequiereInscripcion;

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
