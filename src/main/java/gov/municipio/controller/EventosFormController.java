package gov.municipio.controller;

import gov.municipio.domain.model.evento.*;
import gov.municipio.domain.model.Pelicula;
import gov.municipio.domain.model.enums.EstadoEvento;
import gov.municipio.domain.model.enums.TipoEntrada;
import gov.municipio.domain.model.enums.TipoLugar;
import gov.municipio.domain.service.EventoService;
import gov.municipio.domain.model.enums.ModalidadTaller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.EnumSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.event.ActionEvent;
import java.util.stream.Collectors;

    

public class EventosFormController {
    @FXML private TextField txtNombre;
    @FXML private DatePicker dpFechaInicio;
    @FXML private Spinner<Integer> spDuracion;
    @FXML private ComboBox<String> cbTipoEvento;
    @FXML private ComboBox<EstadoEvento> cbEstadoEvento;
    @FXML private CheckBox chkRequiereInscripcion;
    //Feria
    @FXML private AnchorPane feriaPane;
    @FXML private ComboBox<TipoLugar> cbTipoLugar;
    @FXML private Spinner<Integer> spStands;
    //Taller
    @FXML private AnchorPane tallerPane;
    @FXML private Spinner<Integer> spCupoMaximo;
    @FXML private TextField txtInstructor;
    @FXML private ComboBox<ModalidadTaller> cbModalidad;
    //Concierto
    @FXML private AnchorPane conciertoPane;
    @FXML private TextField txtArtistas;
    @FXML private ComboBox<TipoEntrada> cbTipoEntrada;
    @FXML private TextField txtGeneroMusical;
    @FXML private Spinner<Integer> spCapacidadMaxima;
    //Exposicion
    @FXML private AnchorPane exposicionPane;
    @FXML private TextField txtDisciplinaArtistica;
    //Ciclo de cine
    @FXML private AnchorPane cicloCinePane;
    @FXML private TextField txtPelicula;
    @FXML private CheckBox chkRequiereInscripcionCine;

}