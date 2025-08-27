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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import javafx.event.ActionEvent;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.input.KeyEvent;

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
    }

    // Combos
    private void configurarCombos() {
        cbEstado.getItems().setAll(EnumSet.allOf(EstadoEvento.class));
        cbEstado.setPromptText("Todos los estados");

        // Tipos de evento
        cbTipo.getItems().setAll("Concierto", "Ciclo de Cine", "Exposicion", "Taller", "Feria");
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
        colEstado.setCellValueFactory(cd -> {
            var e = cd.getValue();
            var estado = e.getEstado() != null ? e.getEstado().name() : "";
            return new SimpleStringProperty(estado);
        });

        // Tipo
        colTipo = new TableColumn<>("Tipo");
        colTipo.setSortable(false);
        colTipo.setCellValueFactory(cd -> new SimpleStringProperty(tipoAmigable(cd.getValue())));

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
                
                btnEditar.setOnAction(e -> {
                    Evento ev = getTableView().getItems().get(getIndex());
                    onEditarEvento(ev);
                });

                btnEliminar.setOnAction(e -> {
                    Evento ev = getTableView().getItems().get(getIndex());
                    onEliminarEvento(ev);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, btnEditar, btnEliminar);
                    setGraphic(box);
                }
            }
        });

        tblEventos.getColumns().add(colAcciones);

        if (!tblEventos.getColumns().contains(colEstado)) {
            tblEventos.getColumns().add(colEstado);
            tblEventos.getColumns().add(colTipo);
            tblEventos.getColumns().add(colRequiereInscripcion);
        }

        // Label cuando no hay resultados
        tblEventos.placeholderProperty().bind(
                Bindings.createObjectBinding(
                        () -> new Label("Sin resultados"),
                        tblEventos.itemsProperty()
                )
        );
    }

    // Carga de datos desde el servicio
    private void cargarDatos() {
            backingData.clear(); // limpiar cualquier resto
    
            var hoy = java.time.LocalDate.now();

            var e1 = new Concierto("Rock en la Plaza", hoy.plusDays(7), 4, true,
                        java.util.List.of("La Renga", "Los Redondos"), TipoEntrada.GRATUITA, "Rock", 500);
            e1.setEstado(EstadoEvento.CONFIRMADO);

            var e2 = new Exposicion("Arte Local", hoy.plusDays(15), 6, false, "Pintura");
            e2.setEstado(EstadoEvento.PLANIFICACION);

            var e3 = new Feria("Feria de Emprendedores", hoy.plusDays(30), 10, true,
                    60, TipoLugar.AIRE_LIBRE);
            e3.setEstado(EstadoEvento.EN_EJECUCION);

            var p1 = new Pelicula("Relatos Salvajes", 122f);
            var p2 = new Pelicula("Nueve Reinas", 114f);
            var e4 = new CicloDeCine("Clásicos", hoy.plusDays(20), 3, true,
                    java.util.List.of(p1, p2), false);
            e4.setEstado(EstadoEvento.FINALIZADO);

            var e5 = new Taller("Taller de Cerámica", hoy.plusDays(10), 5, true, ModalidadTaller.PRESENCIAL, 40);
            e5.setEstado(EstadoEvento.CONFIRMADO);

            var e6 = new Concierto("RockAndROLL", hoy.plusDays(7), 4, true,
            java.util.List.of("AIRBAG", "Soda Stereo"), TipoEntrada.GRATUITA, "Rock", 500);
            e6.setEstado(EstadoEvento.CONFIRMADO);

            var e7 = new Concierto("Cumbia Loca", hoy.plusDays(7), 4, true,
            java.util.List.of("Christian Warner", "Viejas Locas"), TipoEntrada.GRATUITA, "Rock", 500);
            e7.setEstado(EstadoEvento.FINALIZADO);

            backingData.addAll(e1, e2, e3, e4, e5, e6, e7);
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

