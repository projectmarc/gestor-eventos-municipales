package gov.municipio.controller;

import gov.municipio.application.PeliculaService;
import gov.municipio.domain.model.Pelicula;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class PeliculasController {

    private final PeliculaService peliculaService; // ✅ final + obligatorio

    // Campos del formulario
    @FXML private TextField txtNombre;
    @FXML private TextField txtDuracionHoras;
    @FXML private Button btnRegistrar;
    @FXML private Button btnLimpiar;
    
    // Botones de acciones
    @FXML private Button btnEditar;
    @FXML private Button btnBorrar;
    
    // Tabla y columnas
    @FXML private TableView<Pelicula> tblPeliculas;
    @FXML private TableColumn<Pelicula, Long> colId;
    @FXML private TableColumn<Pelicula, String> colNombre;
    @FXML private TableColumn<Pelicula, Double> colDuracionHoras;
    @FXML private TableColumn<Pelicula, String> colDuracionFmt;
    
    // Lista observable para la tabla
    private ObservableList<Pelicula> peliculas;
    
    // Película seleccionada para edición
    private Pelicula peliculaEnEdicion = null;

    // 👇 JavaFX NO llama este constructor por defecto; se lo indicamos con controllerFactory
    public PeliculasController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    @FXML
    private void initialize() {
        // Configurar las columnas de la tabla
        configurarTabla();
        
        // Cargar datos iniciales
        cargarPeliculas();
        
        // Configurar selección de tabla para edición
        configurarSeleccionTabla();
    }
    
    /**
     * Configura las columnas de la tabla para mostrar los datos de las películas.
     */
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        
        // Para duración en horas, usar método personalizado
        colDuracionHoras.setCellValueFactory(cellData -> {
            double horas = cellData.getValue().getDuracionEnHoras();
            return new javafx.beans.property.SimpleObjectProperty<>(Math.round(horas * 100.0) / 100.0);
        });
        
        // Para duración formateada
        colDuracionFmt.setCellValueFactory(cellData -> {
            String formateada = cellData.getValue().getDuracionFormateada();
            return new javafx.beans.property.SimpleStringProperty(formateada);
        });
        
        // Configurar ancho de columnas
        colId.setPrefWidth(50);
        colNombre.setPrefWidth(200);
        colDuracionHoras.setPrefWidth(100);
        colDuracionFmt.setPrefWidth(120);
    }
    
    /**
     * Configura la selección de la tabla para habilitar edición.
     */
    private void configurarSeleccionTabla() {
        tblPeliculas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            btnEditar.setDisable(!haySeleccion);
            btnBorrar.setDisable(!haySeleccion);
        });
        
        // Inicialmente deshabilitar botones de acción
        btnEditar.setDisable(true);
        btnBorrar.setDisable(true);
    }
    
    /**
     * Carga todas las películas desde el servicio y las muestra en la tabla.
     */
    private void cargarPeliculas() {
        try {
            List<Pelicula> listaPeliculas = peliculaService.obtenerTodasLasPeliculas();
            peliculas = FXCollections.observableArrayList(listaPeliculas);
            tblPeliculas.setItems(peliculas);
            
            System.out.println("Cargadas " + listaPeliculas.size() + " películas en la tabla");
            
        } catch (Exception e) {
            e.printStackTrace();
            error("Error al cargar datos", "No se pudieron cargar las películas: " + e.getMessage());
        }
    }

    @FXML
    private void onRegistrarPelicula() {
        String nombre = txtNombre.getText();
        String duracionHorasStr = txtDuracionHoras.getText();

        try {
            // Validar y convertir duración de horas a minutos
            if (duracionHorasStr == null || duracionHorasStr.trim().isEmpty()) {
                error("Datos inválidos", "La duración es obligatoria");
                return;
            }
            
            double duracionHoras = Double.parseDouble(duracionHorasStr.trim());
            int duracionMinutos = (int) Math.round(duracionHoras * 60);
            
            if (peliculaEnEdicion == null) {
                // Crear nueva película
                Pelicula p = peliculaService.registrarPelicula(nombre, duracionMinutos);
                info("Película registrada", "Se registró '" + p.getNombre() + "' con ID: " + p.getId());
            } else {
                // Actualizar película existente
                Pelicula p = peliculaService.actualizarPelicula(peliculaEnEdicion.getId(), nombre, duracionMinutos);
                info("Película actualizada", "Se actualizó '" + p.getNombre() + "'");
                cancelarEdicion();
            }
            
            limpiarCampos();
            cargarPeliculas(); // Recargar la tabla
            
        } catch (NumberFormatException e) {
            error("Datos inválidos", "La duración debe ser un número válido");
        } catch (IllegalArgumentException e) {
            error("Datos inválidos", e.getMessage());
        } catch (IllegalStateException e) {
            error("No se pudo guardar", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            error("Error inesperado", "Ocurrió un error al guardar la película.");
        }
    }
    
    @FXML
    private void onLimpiarCampos() {
        limpiarCampos();
        cancelarEdicion();
    }
    
    @FXML
    private void onEditarPelicula() {
        Pelicula seleccionada = tblPeliculas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            error("Sin selección", "Debe seleccionar una película para editar");
            return;
        }
        
        // Cargar datos en el formulario
        peliculaEnEdicion = seleccionada;
        txtNombre.setText(seleccionada.getNombre());
        txtDuracionHoras.setText(String.format("%.2f", seleccionada.getDuracionEnHoras()));
        
        // Cambiar texto del botón
        btnRegistrar.setText("Actualizar");
        
        // Enfocar el primer campo
        txtNombre.requestFocus();
    }
    
    @FXML
    private void onBorrarPelicula() {
        Pelicula seleccionada = tblPeliculas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            error("Sin selección", "Debe seleccionar una película para eliminar");
            return;
        }
        
        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar la película?");
        confirmacion.setContentText("Se eliminará: " + seleccionada.getNombre());
        
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                peliculaService.eliminarPelicula(seleccionada.getId());
                info("Película eliminada", "Se eliminó '" + seleccionada.getNombre() + "'");
                cargarPeliculas(); // Recargar la tabla
                
                // Si estaba editando la película eliminada, cancelar edición
                if (peliculaEnEdicion != null && peliculaEnEdicion.getId().equals(seleccionada.getId())) {
                    cancelarEdicion();
                    limpiarCampos();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                error("Error al eliminar", "No se pudo eliminar la película: " + e.getMessage());
            }
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtDuracionHoras.clear();
        txtNombre.requestFocus();
    }
    
    private void cancelarEdicion() {
        peliculaEnEdicion = null;
        btnRegistrar.setText("Registrar");
    }

    private void error(String titulo, String detalle) {
        Alert a = new Alert(Alert.AlertType.ERROR, detalle, ButtonType.OK);
        a.setHeaderText(titulo);
        a.showAndWait();
    }
    
    private void info(String titulo, String detalle) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, detalle, ButtonType.OK);
        a.setHeaderText(titulo);
        a.showAndWait();
    }
}
