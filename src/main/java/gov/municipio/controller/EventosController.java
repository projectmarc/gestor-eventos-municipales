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

//import javafx.scene.input.MouseEvent;
//import javafx.scene.input.KeyEvent;

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
    //Sección de creación de eventos
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
        configurarCombosFormulario();
        configurarVisibilidadFormularios();
        configurarSpinners();

        btnLimpiar.disableProperty().bind(
            txtBuscar.textProperty().isEmpty()
                .and(cbEstado.valueProperty().isNull())
                .and(cbTipo.valueProperty().isNull())
        );
    }

    // Combos
    private void configurarCombos() {
        cbEstado.getItems().clear();
        cbEstado.getItems().add(null);
        cbEstado.getItems().addAll(EnumSet.allOf(EstadoEvento.class));

        cbEstado.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(EstadoEvento item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else if (item == null) {
                    setText("Todos los estados");
                } else {
                    setText(item.name());
                }
            }
        });
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
            java.util.List.of("AIRBAG", "Soda Stereo"), TipoEntrada.GRATUITA,   "Rock", 500);
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

    // Formulario de creación de Evento
    @FXML
    private void onNuevoEvento(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gov/municipio/view/fxml/evento-form.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setTitle("Crear Nuevo Evento");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL); // Bloquea hasta cerrar
        stage.showAndWait();
    }

    private void configurarCombosFormulario(){
        cbTipoEvento.getItems().setAll("Concierto", "Ciclo de Cine", "Exposición", "Taller", "Feria");
        cbEstadoEvento.getItems().setAll(EnumSet.allOf(EstadoEvento.class));
        cbTipoLugar.getItems().setAll(EnumSet.allOf(TipoLugar.class));
        cbModalidad.getItems().setAll(EnumSet.allOf(ModalidadTaller.class));
    }
    
    private void configurarVisibilidadFormularios(){
        feriaPane.setVisible(false);
        feriaPane.setManaged(false);

        tallerPane.setVisible(false);
        tallerPane.setManaged(false);

        conciertoPane.setVisible(false);
        conciertoPane.setManaged(false);

        cbTipoEvento.valueProperty().addListener((obs, old, nuevo) -> {
            if ("Feria".equals(nuevo)){
                feriaPane.setVisible(true);
                feriaPane.setManaged(true);

                tallerPane.setVisible(false);
                tallerPane.setManaged(false);
                conciertoPane.setVisible(false);
                conciertoPane.setManaged(false);
                exposicionPane.setVisible(false);
                exposicionPane.setManaged(false);
                cicloCinePane.setVisible(false);
                cicloCinePane.setManaged(false);
            }else if("Taller".equals(nuevo)){
                tallerPane.setVisible(true);
                tallerPane.setManaged(true);

                feriaPane.setVisible(false);
                feriaPane.setManaged(false);
                conciertoPane.setVisible(false);
                conciertoPane.setManaged(false);
                exposicionPane.setVisible(false);
                exposicionPane.setManaged(false);
                cicloCinePane.setVisible(false);
                cicloCinePane.setManaged(false);
            }else if("Concierto".equals(nuevo)){
                conciertoPane.setVisible(true);
                conciertoPane.setManaged(true);

                feriaPane.setVisible(false);
                feriaPane.setManaged(false);
                tallerPane.setVisible(false);
                tallerPane.setManaged(false);
                exposicionPane.setVisible(false);
                exposicionPane.setManaged(false);
                cicloCinePane.setVisible(false);
                cicloCinePane.setManaged(false);
            }else if("Exposicion".equals(nuevo)){
                exposicionPane.setVisible(true);
                exposicionPane.setManaged(true);

                feriaPane.setVisible(false);
                feriaPane.setManaged(false);
                tallerPane.setVisible(false);
                tallerPane.setManaged(false);
                conciertoPane.setVisible(false);
                conciertoPane.setManaged(false);
                cicloCinePane.setVisible(false);
                cicloCinePane.setManaged(false);
            }else if("Ciclo de Cine".equals(nuevo)){
                cicloCinePane.setVisible(true);
                cicloCinePane.setManaged(true);

                feriaPane.setVisible(false);
                feriaPane.setManaged(false);
                tallerPane.setVisible(false);
                tallerPane.setManaged(false);
                conciertoPane.setVisible(false);
                conciertoPane.setManaged(false);
                exposicionPane.setVisible(false);
                exposicionPane.setManaged(false);
            }else{
                feriaPane.setVisible(false);
                feriaPane.setManaged(false);
                tallerPane.setVisible(false);
                tallerPane.setManaged(false);
                conciertoPane.setVisible(false);
                conciertoPane.setManaged(false);
                exposicionPane.setVisible(false);
                exposicionPane.setManaged(false);
                cicloCinePane.setVisible(false);
                cicloCinePane.setManaged(false);
            }
        });

    }

    //Valores iniciales para los spinner
    private void configurarSpinners() {
        spDuracion.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 2));
        spStands.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 10));
        spCupoMaximo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 200, 20));
    }
    @FXML
    private void onCancelar(ActionEvent e) {
        limpiarFormulario();
    }
    @FXML
    private void onCrearEvento(ActionEvent e) {
        String nombre = txtNombre.getText();
        var fechaInicio = dpFechaInicio.getValue();
        int duracionEstimadaHoras = spDuracion.getValue();
        boolean requiereInscripcion = chkRequiereInscripcion.isSelected();
        EstadoEvento estado = cbEstadoEvento.getValue();
        String tipo = cbTipoEvento.getValue();

        Evento nuevo = null;

        switch(tipo){
            case "Concierto" ->{              
                List<String> artistas = Arrays.asList(txtArtistas.getText().split(",")); 
                TipoEntrada tipoEntrada = cbTipoEntrada.getValue();
                String generoMusical = txtGeneroMusical.getText();
                int capacidadMaxima = spCapacidadMaxima.getValue();
                nuevo = new Concierto(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion, artistas, tipoEntrada, generoMusical, capacidadMaxima);
            }
            case "Exposicion" -> {
                String disciplinaArtistica = txtDisciplinaArtistica.getText();
                nuevo = new Exposicion(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion, disciplinaArtistica);
            }
            case "Ciclo de Cine" -> {
                List<Pelicula> peliculas = Arrays.stream(txtPelicula.getText().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(title -> new Pelicula(title, 0f))  
                .collect(Collectors.toList());

                boolean incluyeCharlaPosteriores = chkRequiereInscripcionCine.isSelected();
                nuevo = new CicloDeCine(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion, peliculas, incluyeCharlaPosteriores);
            }
            case "Feria" -> {
                int cantidadStands = spStands.getValue();
                TipoLugar tipoLocacion = cbTipoLugar.getValue();
                nuevo = new Feria(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion, cantidadStands, tipoLocacion);
            }
            case "Taller" -> {
                int cupoMaximo = spCupoMaximo.getValue();
                ModalidadTaller modalidad = cbModalidad.getValue();
                nuevo = new Taller(nombre, fechaInicio, duracionEstimadaHoras, requiereInscripcion, modalidad, cupoMaximo);
            }
        }
        if (nuevo != null) {
            nuevo.setEstado(estado);
            backingData.add(nuevo);
            limpiarFormulario();
        }
    }
    private void limpiarFormulario() {
        txtNombre.clear();
        dpFechaInicio.setValue(null);
        spDuracion.getValueFactory().setValue(2);
        cbTipoEvento.getSelectionModel().clearSelection();
        cbEstadoEvento.getSelectionModel().clearSelection();
        chkRequiereInscripcion.setSelected(false);
        feriaPane.setVisible(false); feriaPane.setManaged(false);
        tallerPane.setVisible(false); tallerPane.setManaged(false);
        conciertoPane.setVisible(false); conciertoPane.setManaged(false);
        exposicionPane.setVisible(false); exposicionPane.setManaged(false);
        cicloCinePane.setVisible(false); cicloCinePane.setManaged(false);

}

}