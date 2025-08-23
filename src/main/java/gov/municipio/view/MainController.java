package gov.municipio.view;

import gov.municipio.domain.model.Persona;
import gov.municipio.domain.model.evento.*;
import gov.municipio.domain.model.enums.*;
import gov.municipio.domain.service.EventoService;
import gov.municipio.domain.service.PersonaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class MainController {

    @FXML private TabPane mainTabPane;
    
    // Tab Personas
    @FXML private TextField nombrePersona;
    @FXML private TextField apellidoPersona;
    @FXML private TextField dniPersona;
    @FXML private TextField telefonoPersona;
    @FXML private TextField emailPersona;
    @FXML private TableView<Persona> tablaPersonas;
    @FXML private TableColumn<Persona, String> colNombrePersona;
    @FXML private TableColumn<Persona, String> colApellidoPersona;
    @FXML private TableColumn<Persona, Integer> colDniPersona;
    @FXML private TableColumn<Persona, String> colTelefonoPersona;
    @FXML private TableColumn<Persona, String> colEmailPersona;
    
    // Tab Eventos
    @FXML private TextField nombreEvento;
    @FXML private DatePicker fechaEvento;
    @FXML private TextField duracionEvento;
    @FXML private CheckBox requiereInscripcionEvento;
    @FXML private ComboBox<String> tipoEvento;
    @FXML private VBox camposEspecificos;
    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, String> colNombreEvento;
    @FXML private TableColumn<Evento, LocalDate> colFechaEvento;
    @FXML private TableColumn<Evento, String> colDuracionEvento;
    @FXML private TableColumn<Evento, EstadoEvento> colEstadoEvento;
    @FXML private TableColumn<Evento, String> colTipoEvento;
    
    // Tab Participaciones
    @FXML private ComboBox<Evento> comboEventos;
    @FXML private ComboBox<Persona> comboPersonas;
    @FXML private ComboBox<Rol> comboRoles;
    @FXML private TableView<Evento> tablaEventosParticipacion;
    @FXML private TableColumn<Evento, String> colNombreEventoParticipacion;
    @FXML private TableColumn<Evento, LocalDate> colFechaEventoParticipacion;
    @FXML private TableColumn<Evento, EstadoEvento> colEstadoEventoParticipacion;
    @FXML private TableColumn<Evento, Integer> colParticipantesEventoParticipacion;
    @FXML private Label infoEvento;
    
    private PersonaService personaService;
    private EventoService eventoService;
    private ObservableList<Persona> listaPersonas;
    private ObservableList<Evento> listaEventos;

    @FXML
    private void initialize() {
        personaService = PersonaService.getInstance();
        eventoService = EventoService.getInstance();
        
        inicializarTablaPersonas();
        inicializarTablaEventos();
        inicializarTablaEventosParticipacion();
        inicializarComboBoxes();
        cargarDatos();
    }

    private void inicializarTablaPersonas() {
        colNombrePersona.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidoPersona.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDniPersona.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colTelefonoPersona.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmailPersona.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        listaPersonas = FXCollections.observableArrayList();
        tablaPersonas.setItems(listaPersonas);
    }

    private void inicializarTablaEventos() {
        colNombreEvento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFechaEvento.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        
        // Formatear la duración para mostrar "X horas"
        colDuracionEvento.setCellValueFactory(data -> {
            int duracion = data.getValue().getDuracionEstimada();
            String textoFormateado = duracion + (duracion == 1 ? " hora" : " horas");
            return new javafx.beans.property.SimpleStringProperty(textoFormateado);
        });
        
        colEstadoEvento.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colTipoEvento.setCellValueFactory(data -> {
            String tipo = data.getValue().getClass().getSimpleName();
            return new javafx.beans.property.SimpleStringProperty(tipo);
        });
        
        listaEventos = FXCollections.observableArrayList();
        tablaEventos.setItems(listaEventos);
    }

    private void inicializarTablaEventosParticipacion() {
        colNombreEventoParticipacion.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFechaEventoParticipacion.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colEstadoEventoParticipacion.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        // Configurar columna para mostrar número de participantes
        colParticipantesEventoParticipacion.setCellValueFactory(data -> {
            int participantes = data.getValue().getParticipaciones().size();
            return new javafx.beans.property.SimpleIntegerProperty(participantes).asObject();
        });
    }

    private void inicializarComboBoxes() {
        tipoEvento.setItems(FXCollections.observableArrayList(
            "Feria", "Concierto", "Exposición", "Taller", "Ciclo de Cine"
        ));
        
        comboRoles.setItems(FXCollections.observableArrayList(Arrays.asList(Rol.values())));
    }

    private void cargarDatos() {
        cargarPersonas();
        cargarEventos();
        cargarEventosConParticipaciones();
    }

    private void cargarPersonas() {
        listaPersonas.clear();
        listaPersonas.addAll(personaService.listarTodas());
        comboPersonas.setItems(listaPersonas);
    }

    private void cargarEventos() {
        listaEventos.clear();
        listaEventos.addAll(eventoService.listarTodos());
        comboEventos.setItems(listaEventos);
    }

    private void cargarEventosConParticipaciones() {
        // Usar el método optimizado del servicio
        List<Evento> eventosConParticipaciones = eventoService.listarEventosConParticipaciones();
        
        ObservableList<Evento> listaEventosConParticipaciones = FXCollections.observableArrayList(eventosConParticipaciones);
        tablaEventosParticipacion.setItems(listaEventosConParticipaciones);
    }

    @FXML
    private void guardarPersona() {
        try {
            String nombre = nombrePersona.getText().trim();
            String apellido = apellidoPersona.getText().trim();
            String dniText = dniPersona.getText().trim();
            String telefono = telefonoPersona.getText().trim();
            String email = emailPersona.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || dniText.isEmpty()) {
                mostrarError("Los campos nombre, apellido y DNI son obligatorios");
                return;
            }

            int dni = Integer.parseInt(dniText);
            
            Persona persona = new Persona(nombre, apellido, dni, telefono, email);
            personaService.guardar(persona);
            
            limpiarCamposPersona();
            cargarPersonas();
            mostrarInfo("Persona guardada exitosamente");
            
        } catch (NumberFormatException e) {
            mostrarError("El DNI debe ser un número válido");
        } catch (Exception e) {
            mostrarError("Error al guardar persona: " + e.getMessage());
        }
    }

    @FXML
    private void guardarEvento() {
        try {
            String nombre = nombreEvento.getText().trim();
            LocalDate fecha = fechaEvento.getValue();
            String duracionText = duracionEvento.getText().trim();
            boolean requiereInscripcion = requiereInscripcionEvento.isSelected();
            String tipo = tipoEvento.getValue();

            if (nombre.isEmpty() || fecha == null || duracionText.isEmpty() || tipo == null) {
                mostrarError("Todos los campos básicos son obligatorios");
                return;
            }

            int duracion = Integer.parseInt(duracionText);
            
            Evento evento = crearEventoSegunTipo(tipo, nombre, fecha, duracion, requiereInscripcion);
            eventoService.guardar(evento);
            
            limpiarCamposEvento();
            cargarEventos();
            mostrarInfo("Evento guardado exitosamente");
            
        } catch (NumberFormatException e) {
            mostrarError("La duración debe ser un número válido");
        } catch (Exception e) {
            mostrarError("Error al guardar evento: " + e.getMessage());
        }
    }

    private Evento crearEventoSegunTipo(String tipo, String nombre, LocalDate fecha, int duracion, boolean requiereInscripcion) {
        return switch (tipo) {
            case "Feria" -> new Feria(nombre, fecha, duracion, requiereInscripcion, 10, TipoLugar.AIRE_LIBRE);
            case "Concierto" -> new Concierto(nombre, fecha, duracion, requiereInscripcion, 
                List.of("Artista Principal"), TipoEntrada.GRATUITA, "Rock", 100);
            case "Exposición" -> new Exposicion(nombre, fecha, duracion, requiereInscripcion, "Arte Moderno");
            case "Taller" -> new Taller(nombre, fecha, duracion, requiereInscripcion, ModalidadTaller.PRESENCIAL, 20);
            case "Ciclo de Cine" -> new CicloDeCine(nombre, fecha, duracion, requiereInscripcion, List.of(), false);
            default -> throw new IllegalArgumentException("Tipo de evento no válido: " + tipo);
        };
    }

    @FXML
    private void inscribirPersona() {
        try {
            Evento evento = comboEventos.getValue();
            Persona persona = comboPersonas.getValue();
            Rol rol = comboRoles.getValue();

            if (evento == null || persona == null || rol == null) {
                mostrarError("Debe seleccionar evento, persona y rol");
                return;
            }

            eventoService.inscribirPersona(evento.getId(), persona.getId(), rol);
            mostrarInfo("Persona inscrita exitosamente");
            cargarEventos();
            cargarEventosConParticipaciones();
            
        } catch (Exception e) {
            mostrarError("Error al inscribir persona: " + e.getMessage());
        }
    }

    @FXML
    private void cambiarEstadoEvento() {
        Evento eventoSeleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        if (eventoSeleccionado == null) {
            mostrarError("Seleccione un evento");
            return;
        }

        // Crear diálogo para seleccionar nuevo estado
        ChoiceDialog<EstadoEvento> dialog = new ChoiceDialog<>(eventoSeleccionado.getEstado(), EstadoEvento.values());
        dialog.setTitle("Cambiar Estado");
        dialog.setHeaderText("Seleccione el nuevo estado para: " + eventoSeleccionado.getNombre());
        dialog.setContentText("Estado:");

        dialog.showAndWait().ifPresent(nuevoEstado -> {
            try {
                eventoService.cambiarEstado(eventoSeleccionado.getId(), nuevoEstado);
                cargarEventos();
                mostrarInfo("Estado cambiado exitosamente");
            } catch (Exception e) {
                mostrarError("Error al cambiar estado: " + e.getMessage());
            }
        });
    }

    private void limpiarCamposPersona() {
        nombrePersona.clear();
        apellidoPersona.clear();
        dniPersona.clear();
        telefonoPersona.clear();
        emailPersona.clear();
    }

    private void limpiarCamposEvento() {
        nombreEvento.clear();
        fechaEvento.setValue(null);
        duracionEvento.clear();
        requiereInscripcionEvento.setSelected(false);
        tipoEvento.setValue(null);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}