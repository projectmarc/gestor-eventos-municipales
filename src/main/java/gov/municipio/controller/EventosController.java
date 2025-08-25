package gov.municipio.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class EventosController {

    // Tabla
    @FXML private TableView<EventRow> tblEventos;
    @FXML private TableColumn<EventRow, String> colNombre;
    @FXML private TableColumn<EventRow, String> colFecha;
    @FXML private TableColumn<EventRow, Number> colDuracion;
    @FXML private TableColumn<EventRow, String> colEstado;
    @FXML private TableColumn<EventRow, Boolean> colInscripcion;

    // Buscador
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiar;
    @FXML private Label lblTotal;

    // Form
    @FXML private TextField txtNombre;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtDuracion;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private CheckBox chkInscripcion;

    // Acciones
    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;

    // Datos
    private final ObservableList<EventRow> master = FXCollections.observableArrayList();
    private FilteredList<EventRow> filtered;
    private SortedList<EventRow> sorted;

    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        // --- Columnas ---
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colFecha.setCellValueFactory(c ->
                Bindings.createStringBinding(
                        () -> c.getValue().getFecha() != null ? c.getValue().getFecha().format(dateFmt) : "",
                        c.getValue().fechaProperty())
        );
        colDuracion.setCellValueFactory(c -> c.getValue().duracionHorasProperty());
        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());
        colInscripcion.setCellValueFactory(c -> c.getValue().inscripcionProperty());
        colInscripcion.setCellFactory(CheckBoxTableCell.forTableColumn(colInscripcion));

        // --- Estados (puede ser tu Enum en el futuro) ---
        cmbEstado.setItems(FXCollections.observableArrayList("Programado", "Confirmado", "Cancelado"));

        // --- Validar que Duración sea numérica entera ---
        UnaryOperator<TextFormatter.Change> onlyInt = ch -> {
            String newText = ch.getControlNewText();
            if (newText.isEmpty()) return ch;
            return newText.matches("\\d{0,4}") ? ch : null; // hasta 4 dígitos
        };
        txtDuracion.setTextFormatter(new TextFormatter<>(onlyInt));

        // --- DatePicker con formateo dd/MM/yyyy ---
        dpFecha.setConverter(new StringConverter<>() {
            @Override public String toString(LocalDate date) {
                return date == null ? "" : date.format(dateFmt);
            }
            @Override public LocalDate fromString(String s) {
                if (s == null || s.trim().isEmpty()) return null;
                return LocalDate.parse(s, dateFmt);
            }
        });

        // --- Datos de ejemplo ---
        master.addAll(
                new EventRow("Concierto Primavera", LocalDate.now().plusDays(10), 2, "Confirmado", true),
                new EventRow("Feria de Artesanos", LocalDate.now().plusDays(3), 6, "Programado", false),
                new EventRow("Charla de Seguridad Vial", LocalDate.now().plusDays(20), 1, "Programado", true)
        );

        // --- Filtrado / Orden ---
        filtered = new FilteredList<>(master, r -> true);
        sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tblEventos.comparatorProperty());
        tblEventos.setItems(sorted);

        // --- Selección de fila -> carga formulario ---
        tblEventos.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> loadToForm(sel));

        // --- Botones ---
        btnBuscar.setOnAction(e -> applyFilter());
        btnLimpiar.setOnAction(e -> {
            txtBuscar.clear();
            applyFilter();
        });

        btnNuevo.setOnAction(e -> clearForm());

        btnGuardar.setOnAction(e -> saveOrUpdate());

        btnEliminar.setOnAction(e -> {
            EventRow sel = tblEventos.getSelectionModel().getSelectedItem();
            if (sel != null) {
                master.remove(sel);
                tblEventos.getSelectionModel().clearSelection();
                clearForm();
                refreshTotal();
            } else {
                alertInfo("Seleccione un evento para eliminar.");
            }
        });

        refreshTotal();
    }

    private void applyFilter() {
        String q = txtBuscar.getText();
        filtered.setPredicate(r -> {
            if (q == null || q.isBlank()) return true;
            String s = q.toLowerCase().trim();
            return r.getNombre().toLowerCase().contains(s);
        });
        refreshTotal();
    }

    private void loadToForm(EventRow r) {
        if (r == null) {
            clearForm();
            return;
        }
        txtNombre.setText(r.getNombre());
        dpFecha.setValue(r.getFecha());
        txtDuracion.setText(r.getDuracionHoras() == null ? "" : r.getDuracionHoras().toString());
        cmbEstado.getSelectionModel().select(r.getEstado());
        chkInscripcion.setSelected(r.isInscripcion());
    }

    private void clearForm() {
        txtNombre.clear();
        dpFecha.setValue(null);
        txtDuracion.clear();
        cmbEstado.getSelectionModel().clearSelection();
        chkInscripcion.setSelected(false);
        tblEventos.getSelectionModel().clearSelection();
    }

    private void saveOrUpdate() {
        // Validaciones simples
        String nombre = safeTrim(txtNombre.getText());
        LocalDate fecha = dpFecha.getValue();
        String durTxt = safeTrim(txtDuracion.getText());
        String estado = cmbEstado.getSelectionModel().getSelectedItem();
        boolean insc = chkInscripcion.isSelected();

        if (nombre.isBlank()) { alertWarn("El nombre es obligatorio."); txtNombre.requestFocus(); return; }
        if (fecha == null) { alertWarn("La fecha es obligatoria."); dpFecha.requestFocus(); return; }
        if (durTxt.isBlank()) { alertWarn("La duración (h) es obligatoria."); txtDuracion.requestFocus(); return; }
        Integer dur;
        try {
            dur = Integer.parseInt(durTxt);
        } catch (NumberFormatException ex) {
            alertWarn("La duración debe ser un número entero.");
            txtDuracion.requestFocus();
            return;
        }
        if (dur < 0) { alertWarn("La duración no puede ser negativa."); txtDuracion.requestFocus(); return; }
        if (estado == null || estado.isBlank()) { alertWarn("El estado es obligatorio."); cmbEstado.requestFocus(); return; }

        EventRow sel = tblEventos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            // Alta
            master.add(new EventRow(nombre, fecha, dur, estado, insc));
        } else {
            // Edición
            sel.setNombre(nombre);
            sel.setFecha(fecha);
            sel.setDuracionHoras(dur);
            sel.setEstado(estado);
            sel.setInscripcion(insc);
            // refrescar tabla (si hace falta)
            tblEventos.refresh();
        }

        clearForm();
        applyFilter(); // mantiene filtro actual
        refreshTotal();
    }

    private void refreshTotal() {
        lblTotal.setText(String.valueOf(filtered.size()));
    }

    private static String safeTrim(String s) { return s == null ? "" : s.trim(); }

    private void alertWarn(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void alertInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    // =========================
    // Modelo simple para la UI
    // =========================
    public static class EventRow {
        private final StringProperty nombre = new SimpleStringProperty();
        private final ObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();
        private final IntegerProperty duracionHoras = new SimpleIntegerProperty();
        private final StringProperty estado = new SimpleStringProperty();
        private final BooleanProperty inscripcion = new SimpleBooleanProperty();

        public EventRow(String nombre, LocalDate fecha, Integer duracionHoras, String estado, boolean inscripcion) {
            setNombre(nombre);
            setFecha(fecha);
            setDuracionHoras(duracionHoras);
            setEstado(estado);
            setInscripcion(inscripcion);
        }

        public String getNombre() { return nombre.get(); }
        public void setNombre(String v) { nombre.set(v); }
        public StringProperty nombreProperty() { return nombre; }

        public LocalDate getFecha() { return fecha.get(); }
        public void setFecha(LocalDate v) { fecha.set(v); }
        public ObjectProperty<LocalDate> fechaProperty() { return fecha; }

        public Integer getDuracionHoras() { return duracionHoras.get(); }
        public void setDuracionHoras(Integer v) { duracionHoras.set(Objects.requireNonNullElse(v, 0)); }
        public IntegerProperty duracionHorasProperty() { return duracionHoras; }

        public String getEstado() { return estado.get(); }
        public void setEstado(String v) { estado.set(v); }
        public StringProperty estadoProperty() { return estado; }

        public boolean isInscripcion() { return inscripcion.get(); }
        public void setInscripcion(boolean v) { inscripcion.set(v); }
        public BooleanProperty inscripcionProperty() { return inscripcion; }
    }
}
