package gov.municipio.controller;

import gov.municipio.application.EventoService;
import gov.municipio.application.PersonaService;
import gov.municipio.config.ServiceContainer;
import gov.municipio.domain.model.Persona;
import gov.municipio.domain.model.enums.Rol;
import gov.municipio.domain.model.evento.Evento;
import gov.municipio.ui.nav.Router;
import gov.municipio.ui.nav.Screen;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GestionarEventoController {
    
    @FXML
    private Label lblNombre;
    
    @FXML
    private Label lblDuracion;
    
    @FXML
    private Label lblFechaInicio;
    
    @FXML
    private Label lblCupoMaximo;
    
    @FXML
    private TextField txtNombre;
    
    @FXML
    private TextField txtDuracion;
    
    @FXML
    private DatePicker dpFechaInicio;
    
    @FXML
    private TextField txtCupoMaximo;
    
    @FXML
    private Button btnEditar;
    
    @FXML
    private Button btnGuardar;
    
    @FXML
    private Button btnCancelar;
    
    @FXML
    private VBox vboxResponsables;
    
    @FXML
    private VBox vboxAsistentes;
    
    private Router router;
    private EventoService eventoService;
    private PersonaService personaService;
    private Evento eventoActual;
    
    public GestionarEventoController() {
        // Constructor vacío para el patrón actual
    }
    
    public void setRouter(Router router) {
        this.router = router;
    }
    
    public void setServiceContainer(ServiceContainer serviceContainer) {
        this.eventoService = serviceContainer.eventoService();
        this.personaService = serviceContainer.personaService();
    }
    
    public void setEvento(Evento evento) {
        this.eventoActual = evento;
        cargarDatosEvento();
        cargarPersonasResponsables();
        cargarAsistentes();
    }
    
    @FXML
    private void initialize() {
        // Inicialmente mostrar solo los labels (modo lectura)
        mostrarModoLectura();
    }
    
    private void cargarDatosEvento() {
        if (eventoActual != null) {
            // Cargar datos en los labels
            lblNombre.setText(eventoActual.getNombre());
            lblDuracion.setText(String.valueOf(eventoActual.getDuracionEstimada()));
            lblFechaInicio.setText(eventoActual.getFechaInicio().toString());
            
            if (eventoActual.isRequiereInscripcion() && eventoActual.getCupoMaximoDefinido() != null) {
                lblCupoMaximo.setText(String.valueOf(eventoActual.getCupoMaximoDefinido()));
            } else {
                lblCupoMaximo.setText("Sin límite");
            }
            
            // Cargar datos en los campos de edición (inicialmente ocultos)
            txtNombre.setText(eventoActual.getNombre());
            txtDuracion.setText(String.valueOf(eventoActual.getDuracionEstimada()));
            dpFechaInicio.setValue(eventoActual.getFechaInicio());
            
            if (eventoActual.isRequiereInscripcion() && eventoActual.getCupoMaximoDefinido() != null) {
                txtCupoMaximo.setText(String.valueOf(eventoActual.getCupoMaximoDefinido()));
            } else {
                txtCupoMaximo.setText("");
            }
            
            // Habilitar/deshabilitar edición según el estado
            btnEditar.setDisable(!eventoActual.estaEnPlanificacion());
        }
    }
    
    @FXML
    private void editar() {
        mostrarModoEdicion();
    }
    
    @FXML
    private void guardar() {
        try {
            // Validar y actualizar los datos
            String nombre = txtNombre.getText().trim();
            String duracionTexto = txtDuracion.getText().trim();
            LocalDate fechaInicio = dpFechaInicio.getValue();
            String cupoTexto = txtCupoMaximo.getText().trim();
            
            // Validaciones básicas
            if (nombre.isEmpty()) {
                mostrarError("Error", "El nombre no puede estar vacío");
                return;
            }
            
            int duracion;
            try {
                duracion = Integer.parseInt(duracionTexto);
            } catch (NumberFormatException e) {
                mostrarError("Error", "La duración debe ser un número válido");
                return;
            }
            
            if (fechaInicio == null) {
                mostrarError("Error", "Debe seleccionar una fecha de inicio");
                return;
            }
            
            // Actualizar el evento
            eventoActual.setNombre(nombre);
            eventoActual.setDuracionEstimada(duracion);
            eventoActual.setFechaInicio(fechaInicio);
            
            // Actualizar cupo máximo solo si el evento requiere inscripción
            if (eventoActual.isRequiereInscripcion()) {
                if (cupoTexto.isEmpty()) {
                    mostrarError("Error", "El cupo máximo es obligatorio para eventos que requieren inscripción");
                    return;
                }
                try {
                    int cupo = Integer.parseInt(cupoTexto);
                    eventoActual.setCupoMaximo(cupo);
                } catch (NumberFormatException e) {
                    mostrarError("Error", "El cupo máximo debe ser un número válido");
                    return;
                }
            }
            
            // Guardar cambios en la base de datos
            eventoService.actualizarEvento(eventoActual);
            
            // Actualizar la vista y volver al modo lectura
            cargarDatosEvento();
            mostrarModoLectura();
            
            mostrarInfo("Éxito", "Los datos del evento han sido actualizados correctamente");
            
        } catch (Exception e) {
            mostrarError("Error", "Error al guardar los cambios: " + e.getMessage());
        }
    }
    
    @FXML
    private void cancelar() {
        // Recargar los datos originales y volver al modo lectura
        cargarDatosEvento();
        mostrarModoLectura();
    }
    
    @FXML
    private void volver() {
        if (router != null) {
            router.goTo(Screen.EVENTOS);
        }
    }
    
    private void mostrarModoLectura() {
        // Mostrar labels, ocultar campos de edición
        lblNombre.setVisible(true);
        lblDuracion.setVisible(true);
        lblFechaInicio.setVisible(true);
        lblCupoMaximo.setVisible(true);
        
        txtNombre.setVisible(false);
        txtNombre.setManaged(false);
        txtDuracion.setVisible(false);
        txtDuracion.setManaged(false);
        dpFechaInicio.setVisible(false);
        dpFechaInicio.setManaged(false);
        txtCupoMaximo.setVisible(false);
        txtCupoMaximo.setManaged(false);
        
        // Mostrar botón editar, ocultar guardar/cancelar
        btnEditar.setVisible(true);
        btnEditar.setManaged(true);
        btnGuardar.setVisible(false);
        btnGuardar.setManaged(false);
        btnCancelar.setVisible(false);
        btnCancelar.setManaged(false);
    }
    
    private void mostrarModoEdicion() {
        // Ocultar labels, mostrar campos de edición
        lblNombre.setVisible(false);
        lblDuracion.setVisible(false);
        lblFechaInicio.setVisible(false);
        lblCupoMaximo.setVisible(false);
        
        txtNombre.setVisible(true);
        txtNombre.setManaged(true);
        txtDuracion.setVisible(true);
        txtDuracion.setManaged(true);
        dpFechaInicio.setVisible(true);
        dpFechaInicio.setManaged(true);
        txtCupoMaximo.setVisible(true);
        txtCupoMaximo.setManaged(true);
        
        // Ocultar botón editar, mostrar guardar/cancelar
        btnEditar.setVisible(false);
        btnEditar.setManaged(false);
        btnGuardar.setVisible(true);
        btnGuardar.setManaged(true);
        btnCancelar.setVisible(true);
        btnCancelar.setManaged(true);
        
        // Enfocar el primer campo
        txtNombre.requestFocus();
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    // ========== MÉTODOS PARA GESTIÓN DE PERSONAS RESPONSABLES ==========
    
    private void cargarPersonasResponsables() {
        if (eventoActual == null) return;
        
        vboxResponsables.getChildren().clear();
        
        // Obtener roles válidos para este tipo de evento
        List<Rol> rolesResponsables = obtenerRolesResponsablesParaEvento();
        
        for (Rol rol : rolesResponsables) {
            VBox seccionRol = crearSeccionParaRol(rol);
            vboxResponsables.getChildren().add(seccionRol);
        }
    }
    
    private List<Rol> obtenerRolesResponsablesParaEvento() {
        // Filtrar solo los roles responsables (no asistente) que son válidos para este evento
        return List.of(Rol.ORGANIZADOR, Rol.CURADOR, Rol.INSTRUCTOR)
                .stream()
                .filter(rol -> eventoActual.validarRol(rol))
                .collect(Collectors.toList());
    }
    
    private VBox crearSeccionParaRol(Rol rol) {
        VBox seccionRol = new VBox(5);
        
        // Título de la sección con botón para agregar
        HBox headerRol = new HBox(10);
        Label lblRol = new Label(rol.toString() + "S:");
        lblRol.setStyle("-fx-font-weight: bold;");
        
        Button btnAgregar = new Button("Agregar " + rol.toString());
        btnAgregar.setOnAction(e -> abrirDialogoBuscarPersona(rol));
        
        // Solo permitir agregar responsables si el evento está en planificación
        btnAgregar.setDisable(!eventoActual.estaEnPlanificacion());
        
        headerRol.getChildren().addAll(lblRol, btnAgregar);
        seccionRol.getChildren().add(headerRol);
        
        // Lista de personas con este rol
        VBox listaPersonas = new VBox(3);
        List<Persona> personasConRol = eventoActual.getPersonasPorRol(rol);
        
        for (Persona persona : personasConRol) {
            HBox itemPersona = crearItemPersona(persona, rol);
            listaPersonas.getChildren().add(itemPersona);
        }
        
        if (personasConRol.isEmpty()) {
            Label lblVacio = new Label("No hay " + rol.toString().toLowerCase() + "s asignados");
            lblVacio.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            listaPersonas.getChildren().add(lblVacio);
        }
        
        seccionRol.getChildren().add(listaPersonas);
        return seccionRol;
    }
    
    private HBox crearItemPersona(Persona persona, Rol rol) {
        HBox itemPersona = new HBox(10);
        
        // Información de la persona
        VBox infoPersona = new VBox(2);
        infoPersona.getChildren().addAll(
            new Label(persona.getNombre() + " " + persona.getApellido()),
            new Label("DNI: " + persona.getDni()),
            new Label("Tel: " + persona.getTelefono()),
            new Label("Email: " + persona.getEmail())
        );
        
        // Botón para quitar (solo si está en planificación)
        Button btnQuitar = new Button("Quitar");
        btnQuitar.setOnAction(e -> quitarPersonaResponsable(persona));
        btnQuitar.setDisable(!eventoActual.estaEnPlanificacion());
        
        itemPersona.getChildren().addAll(infoPersona, btnQuitar);
        return itemPersona;
    }
    
    private void abrirDialogoBuscarPersona(Rol rol) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gov/municipio/view/fxml/buscar-persona-dialog.fxml"));
            Parent root = loader.load();
            
            BuscarPersonaDialogController dialogController = loader.getController();
            dialogController.setPersonaService(personaService);
            dialogController.setRol(rol);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Agregar " + rol.toString());
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(vboxResponsables.getScene().getWindow());
            dialogStage.showAndWait();
            
            if (dialogController.getResultado() && dialogController.getPersonaSeleccionada() != null) {
                agregarPersonaResponsable(dialogController.getPersonaSeleccionada(), rol);
            }
            
        } catch (Exception e) {
            mostrarError("Error", "Error al abrir el diálogo: " + e.getMessage());
        }
    }
    
    private void agregarPersonaResponsable(Persona persona, Rol rol) {
        try {
            eventoActual.agregarResponsable(persona, rol);
            eventoService.actualizarEvento(eventoActual);
            cargarPersonasResponsables(); // Recargar la vista
            mostrarInfo("Éxito", "Persona agregada como " + rol.toString() + " exitosamente");
        } catch (Exception e) {
            mostrarError("Error", "Error al agregar persona: " + e.getMessage());
        }
    }
    
    private void quitarPersonaResponsable(Persona persona) {
        try {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Está seguro de quitar a " + persona.getNombre() + " " + persona.getApellido() + " del evento?");
            
            if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                eventoActual.removerParticipacion(persona);
                eventoService.actualizarEvento(eventoActual);
                cargarPersonasResponsables(); // Recargar la vista
                mostrarInfo("Éxito", "Persona quitada del evento exitosamente");
            }
        } catch (Exception e) {
            mostrarError("Error", "Error al quitar persona: " + e.getMessage());
        }
    }
    
    // ========== MÉTODOS PARA GESTIÓN DE ASISTENTES ==========
    
    private void cargarAsistentes() {
        if (eventoActual == null) return;
        
        vboxAsistentes.getChildren().clear();
        
        // Verificar si el evento permite asistentes
        if (!eventoActual.validarRol(Rol.ASISTENTE)) {
            Label lblNoPermitido = new Label("Este tipo de evento no permite asistentes");
            lblNoPermitido.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            vboxAsistentes.getChildren().add(lblNoPermitido);
            return;
        }
        
        // Crear sección de asistentes
        VBox seccionAsistentes = crearSeccionAsistentes();
        vboxAsistentes.getChildren().add(seccionAsistentes);
    }
    
    private VBox crearSeccionAsistentes() {
        VBox seccionAsistentes = new VBox(5);
        
        // Header con botón para agregar asistente
        HBox headerAsistentes = new HBox(10);
        Label lblAsistentes = new Label("LISTA DE ASISTENTES:");
        lblAsistentes.setStyle("-fx-font-weight: bold;");
        
        Button btnAgregarAsistente = new Button("Inscribir Asistente");
        btnAgregarAsistente.setOnAction(e -> abrirDialogoInscribirAsistente());
        
        // Solo permitir inscribir si el evento está confirmado o en ejecución y requiere inscripción
        boolean puedeInscribir = eventoActual.isRequiereInscripcion() && 
                                (eventoActual.estaActivo());
        btnAgregarAsistente.setDisable(!puedeInscribir);
        
        headerAsistentes.getChildren().addAll(lblAsistentes, btnAgregarAsistente);
        seccionAsistentes.getChildren().add(headerAsistentes);
        
        // Información del cupo si requiere inscripción
        if (eventoActual.isRequiereInscripcion()) {
            HBox infoCupo = new HBox(10);
            int lugaresDisponibles = eventoActual.getLugaresDisponibles();
            int totalAsistentes = eventoActual.getParticipacionesAsistentes().size();
            
            Label lblCupoInfo = new Label(String.format("Inscriptos: %d | Disponibles: %d", 
                                                       totalAsistentes, lugaresDisponibles));
            lblCupoInfo.setStyle("-fx-font-style: italic;");
            
            if (lugaresDisponibles == 0) {
                lblCupoInfo.setStyle("-fx-font-style: italic; -fx-text-fill: red;");
                lblCupoInfo.setText(lblCupoInfo.getText() + " (COMPLETO)");
            }
            
            infoCupo.getChildren().add(lblCupoInfo);
            seccionAsistentes.getChildren().add(infoCupo);
        }
        
        // Lista de asistentes
        VBox listaAsistentes = new VBox(3);
        List<Persona> asistentes = eventoActual.getPersonasPorRol(Rol.ASISTENTE);
        
        for (Persona asistente : asistentes) {
            HBox itemAsistente = crearItemAsistente(asistente);
            listaAsistentes.getChildren().add(itemAsistente);
        }
        
        if (asistentes.isEmpty()) {
            Label lblVacio = new Label("No hay asistentes inscriptos");
            lblVacio.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            listaAsistentes.getChildren().add(lblVacio);
        }
        
        seccionAsistentes.getChildren().add(listaAsistentes);
        return seccionAsistentes;
    }
    
    private HBox crearItemAsistente(Persona asistente) {
        HBox itemAsistente = new HBox(10);
        
        // Información del asistente
        VBox infoAsistente = new VBox(2);
        infoAsistente.getChildren().addAll(
            new Label(asistente.getNombre() + " " + asistente.getApellido()),
            new Label("DNI: " + asistente.getDni()),
            new Label("Tel: " + asistente.getTelefono()),
            new Label("Email: " + asistente.getEmail())
        );
        
        // Botón para dar de baja (solo si está confirmado o en ejecución)
        Button btnDarDeBaja = new Button("Dar de Baja");
        btnDarDeBaja.setOnAction(e -> darDeBajaAsistente(asistente));
        
        // Solo permitir dar de baja si no está finalizado
        btnDarDeBaja.setDisable(eventoActual.estaCerrado());
        
        itemAsistente.getChildren().addAll(infoAsistente, btnDarDeBaja);
        return itemAsistente;
    }
    
    private void abrirDialogoInscribirAsistente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gov/municipio/view/fxml/buscar-persona-dialog.fxml"));
            Parent root = loader.load();
            
            BuscarPersonaDialogController dialogController = loader.getController();
            dialogController.setPersonaService(personaService);
            dialogController.setRol(Rol.ASISTENTE);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Inscribir Asistente");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(vboxAsistentes.getScene().getWindow());
            dialogStage.showAndWait();
            
            if (dialogController.getResultado() && dialogController.getPersonaSeleccionada() != null) {
                inscribirAsistente(dialogController.getPersonaSeleccionada());
            }
            
        } catch (Exception e) {
            mostrarError("Error", "Error al abrir el diálogo: " + e.getMessage());
        }
    }
    
    private void inscribirAsistente(Persona persona) {
        try {
            eventoActual.inscribirAsistente(persona);
            eventoService.actualizarEvento(eventoActual);
            cargarAsistentes(); // Recargar la vista
            mostrarInfo("Éxito", "Asistente inscripto exitosamente");
        } catch (Exception e) {
            mostrarError("Error", "Error al inscribir asistente: " + e.getMessage());
        }
    }
    
    private void darDeBajaAsistente(Persona asistente) {
        try {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar baja");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Está seguro de dar de baja a " + asistente.getNombre() + " " + asistente.getApellido() + "?");
            
            if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                eventoActual.removerParticipacion(asistente);
                eventoService.actualizarEvento(eventoActual);
                cargarAsistentes(); // Recargar la vista
                mostrarInfo("Éxito", "Asistente dado de baja exitosamente");
            }
        } catch (Exception e) {
            mostrarError("Error", "Error al dar de baja asistente: " + e.getMessage());
        }
    }
}
