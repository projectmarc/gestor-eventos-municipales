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

public class EventosController implements Initializable {
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<EstadoEvento> cbEstado;
    @FXML private ComboBox<String> cbTipo;
    @FXML private Button btnNuevo;
    @FXML private Button btnLimpiar;
    @FXML private TableView<Evento> tblEventos;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, String> colFecha;
    @FXML private TableColumn<Evento, String> colEstado;
    @FXML private TableColumn<Evento, String> colTipo;
    @FXML private TableColumn<Evento, String> colRequiereInscripcion;
    @FXML private TableColumn<Evento, Void> colAcciones;

    private final ObservableList<Evento> backingData = FXCollections.observableArrayList();
    private FilteredList<Evento> filtered;
    private SortedList<Evento> sorted;

    private final EventoService eventoService = EventoService.getInstance();
    private static final Locale LOCALE_ES = Locale.of("es", "AR");
    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarCombos();
        configurarTabla();
        cargarDatos();
        configurarFiltrado();
        wiringBusquedaYFiltros();

        btnNuevo.setOnAction(this::onNuevo);
        if (btnLimpiar != null) {
            btnLimpiar.setOnAction(e -> {
                txtBuscar.clear();
                cbEstado.setValue(null);
                cbTipo.setValue(null);
                aplicarFiltros();
            });
        }
        cbEstado.setEditable(false);
        cbTipo.setEditable(false);
    }

    // Combos
    private void configurarCombos() {
        cbEstado.getItems().setAll(EnumSet.allOf(EstadoEvento.class));
        cbEstado.setPromptText("Todos los estados");

        cbTipo.getItems().setAll("Concierto", "Ciclo de Cine", "Exposición", "Taller", "Feria");
        cbTipo.setPromptText("Todos los tipos");
    }

    // Tabla y columnas
    private void configurarTabla() {
        //Nombre
        colNombre.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNombre()));

        //Fecha
        colFecha.setCellValueFactory(cd -> {
            var e = cd.getValue();
            var f = e.getFechaInicio() != null ? FECHA_FMT.format(e.getFechaInicio()) : "";
            return new SimpleStringProperty(f);
        });

        // Estado   
        colEstado.setCellValueFactory(cd ->
            new SimpleStringProperty(estadoAmigable(cd.getValue().getEstado()))
        );

        // Tipo
        colTipo.setCellValueFactory(cd -> new SimpleStringProperty(tipoAmigable(cd.getValue())));
        colRequiereInscripcion.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().isRequiereInscripcion() ? "Requiere" : "No requiere")
        );

        // Requiere inscripción
        colRequiereInscripcion.setCellValueFactory(cd -> {
            String txt = cd.getValue().isRequiereInscripcion() ? "Requiere" : "No requiere";
            return new SimpleStringProperty(txt);
        });

        //Acciones
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Borrar");
            {
                btnEditar.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                btnEliminar.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: red;");
                btnEditar.setOnAction(e -> onEditarEvento(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e -> onEliminarEvento(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, btnEditar, btnEliminar));
            }
        });

        tblEventos.placeholderProperty().bind(
                Bindings.createObjectBinding(() -> new Label("Sin resultados"), tblEventos.itemsProperty())
        );
    }

    // Carga de datos desde el servicio
    private void cargarDatos() {
        try {
            backingData.setAll(eventoService.listarEventosConParticipaciones());
        } catch (Exception ex) {
            System.err.println("No se pudieron cargar eventos: " + ex.getMessage());
        }   
    }

    // Configuración de filtros
    private void configurarFiltrado() {
    filtered = new FilteredList<>(backingData, e -> true);
    sorted = new SortedList<>(filtered);
    sorted.comparatorProperty().bind(tblEventos.comparatorProperty());
    tblEventos.setItems(sorted);

    }

    private void wiringBusquedaYFiltros() {
        // Texto: 
        txtBuscar.textProperty().addListener((obs, old, val) -> aplicarFiltros());

        // Estado
        cbEstado.valueProperty().addListener((obs, old, val) -> aplicarFiltros());

        // Tipo
        cbTipo.valueProperty().addListener((obs, old, val) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        final String texto = normalizar(txtBuscar.getText());
        final EstadoEvento estadoSel = cbEstado.getValue();
        final String tipoSel = cbTipo.getValue(); 

        Predicate<Evento> pred = e -> true;

        // Filtro por nombre
        if (texto != null && !texto.isBlank()) {
            pred = pred.and(e -> normalizar(e.getNombre()).contains(texto));
        }
        // Filtro por estado
        if (estadoSel != null) {
            pred = pred.and(e -> estadoSel.equals(e.getEstado()));
        }
        // Filtro por tipo
        if (tipoSel != null && !tipoSel.isBlank()) {
            pred = pred.and(e -> tipoAmigable(e).equalsIgnoreCase(tipoSel));
        }

        filtered.setPredicate(pred);
    }

    // Convierte el nombre de la clase a un nombre legible en la UI
    private String tipoAmigable(Evento e) {
        if (e instanceof Concierto) return "Concierto";
        if (e instanceof CicloDeCine) return "Ciclo de Cine";
        if (e instanceof Exposicion) return "Exposición";
        if (e instanceof Taller) return "Taller";
        if (e instanceof Feria) return "Feria";
        return e.getClass().getSimpleName();
    }

    // Normalización para búsqueda (minúsculas y quita espacios extra)
    private String normalizar(String s) {
        if (s == null) return "";
        return s.toLowerCase(LOCALE_ES).trim();
    }

    // Métodos FXML
    @FXML
    private void onBuscar(ActionEvent e) {
        aplicarFiltros();
    }

    @FXML
    private void onFiltroChanged(ActionEvent e) {
        aplicarFiltros();
    }

    @FXML
    private void onLimpiarFiltros(ActionEvent e) {
        txtBuscar.clear();
        cbEstado.setValue(null);
        cbTipo.getSelectionModel().clearSelection();
        cbTipo.setValue(null);
        tblEventos.getSortOrder().clear();
        aplicarFiltros();
    }

    private void onEditarEvento(Evento evento) {
        System.out.println("Editar: " + evento.getNombre());
        // Aquí iría la lógica para editar el evento
        // Por ejemplo, abrir un diálogo con un formulario prellenado
    }   

    private void onEliminarEvento(Evento evento) {
        var ok = alertConfirm("¿Eliminar el evento \"" + evento.getNombre() + "\"?");
        if (ok) {
            backingData.remove(evento);
            // Aquí también se debería eliminar el evento del servicio/repositorio
        }
    }

    private boolean alertConfirm(String msg) {
        var res = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL).showAndWait();
        return res.isPresent() && res.get() == ButtonType.OK;
    }


}