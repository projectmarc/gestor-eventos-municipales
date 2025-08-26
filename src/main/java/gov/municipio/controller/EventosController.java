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
        colEstado = new TableColumn<>("Estado");
        colEstado.setSortable(false);
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
        colRequiereInscripcion = new TableColumn<>("Requiere inscripción");
        colRequiereInscripcion.setSortable(false);
        colRequiereInscripcion.setCellValueFactory(cd -> {
            String txt = cd.getValue().isRequiereInscripcion() ? "Requiere" : "No requiere";
            return new SimpleStringProperty(txt);
        });

        if (!tblEventos.getColumns().contains(colEstado)) {
            tblEventos.getColumns().addAll(colEstado, colTipo, colRequiereInscripcion);
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
            // Si tu servicio expone otro método (p. ej. listarTodos()), podés usarlo.
            // Aquí intentamos listar los eventos con sus participaciones si está disponible.
            try {
                backingData.setAll(eventoService.listarEventosConParticipaciones());
            } catch (Exception ex) {
                // Fallback: si hubiera otro método básico, podés agregarlo.
                // backingData.setAll(eventoService.listarTodos());
                // Por ahora, si falla dejamos la lista vacía y mostramos en consola.
                System.err.println("No se pudieron cargar eventoss: " + ex.getMessage());
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

    // Normalización básica para búsqueda (minúsculas y quita espacios extra)
    private String normalizar(String s) {
        if (s == null) return "";
        return s.toLowerCase(LOCALE_ES).trim();
    }
}
