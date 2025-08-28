package gov.municipio.controller;

import gov.municipio.application.ArtistaBandaService;
import gov.municipio.domain.model.ArtistaBanda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ArtistasBandasController {

    private final ArtistaBandaService artistaBandaService; // ✅ final + obligatorio

    // Campos del formulario
    @FXML private TextField txtNombre;
    @FXML private Button btnRegistrar;
    @FXML private Button btnLimpiar;
    
    // Botones de acciones
    @FXML private Button btnEditar;
    @FXML private Button btnBorrar;
    
    // Tabla y columnas
    @FXML private TableView<ArtistaBanda> tblArtistasBandas;
    @FXML private TableColumn<ArtistaBanda, Long> colId;
    @FXML private TableColumn<ArtistaBanda, String> colNombre;
    
    // Lista observable para la tabla
    private ObservableList<ArtistaBanda> artistasBandas;
    
    // ArtistaBanda seleccionado para edición
    private ArtistaBanda artistaBandaEnEdicion = null;

    // 👇 JavaFX NO llama este constructor por defecto; se lo indicamos con controllerFactory
    public ArtistasBandasController(ArtistaBandaService artistaBandaService) {
        this.artistaBandaService = artistaBandaService;
    }

    @FXML
    private void initialize() {
        // Configurar las columnas de la tabla
        configurarTabla();
        
        // Cargar datos iniciales
        cargarArtistasBandas();
        
        // Configurar selección de tabla para edición
        configurarSeleccionTabla();
    }
    
    /**
     * Configura las columnas de la tabla para mostrar los datos de los artistas/bandas.
     */
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        
        // Configurar ancho de columnas
        colId.setPrefWidth(50);
        colNombre.setPrefWidth(300);
    }
    
    /**
     * Configura la selección de la tabla para habilitar edición.
     */
    private void configurarSeleccionTabla() {
        tblArtistasBandas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            btnEditar.setDisable(!haySeleccion);
            btnBorrar.setDisable(!haySeleccion);
        });
        
        // Inicialmente deshabilitar botones de acción
        btnEditar.setDisable(true);
        btnBorrar.setDisable(true);
    }
    
    /**
     * Carga todos los artistas/bandas desde el servicio y los muestra en la tabla.
     */
    private void cargarArtistasBandas() {
        try {
            List<ArtistaBanda> listaArtistasBandas = artistaBandaService.obtenerTodosLosArtistasBandas();
            artistasBandas = FXCollections.observableArrayList(listaArtistasBandas);
            tblArtistasBandas.setItems(artistasBandas);
            
            System.out.println("Cargados " + listaArtistasBandas.size() + " artistas/bandas en la tabla");
            
        } catch (Exception e) {
            e.printStackTrace();
            error("Error al cargar datos", "No se pudieron cargar los artistas/bandas: " + e.getMessage());
        }
    }

    @FXML
    private void onRegistrarArtistaBanda() {
        String nombre = txtNombre.getText();

        try {
            if (artistaBandaEnEdicion == null) {
                // Registrar nuevo artista/banda
                ArtistaBanda a = artistaBandaService.registrarArtistaBanda(nombre);
                info("Artista/banda registrado", "Se registró '" + a.getNombre() + "' con ID: " + a.getId());
            } else {
                // Actualizar artista/banda existente
                ArtistaBanda a = artistaBandaService.actualizarArtistaBanda(artistaBandaEnEdicion.getId(), nombre);
                info("Artista/banda actualizado", "Se actualizó '" + a.getNombre() + "'");
                cancelarEdicion();
            }
            
            limpiarCampos();
            cargarArtistasBandas(); // Recargar la tabla
            
        } catch (IllegalArgumentException e) {
            error("Datos inválidos", e.getMessage());
        } catch (IllegalStateException e) {
            error("No se pudo guardar", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            error("Error inesperado", "Ocurrió un error al guardar el artista/banda.");
        }
    }
    
    @FXML
    private void onLimpiarCampos() {
        limpiarCampos();
        cancelarEdicion();
    }
    
    @FXML
    private void onEditarArtistaBanda() {
        ArtistaBanda seleccionado = tblArtistasBandas.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            error("Sin selección", "Debe seleccionar un artista/banda para editar");
            return;
        }
        
        // Cargar datos en el formulario
        artistaBandaEnEdicion = seleccionado;
        txtNombre.setText(seleccionado.getNombre());
        
        // Cambiar texto del botón
        btnRegistrar.setText("Actualizar");
        
        // Enfocar el primer campo
        txtNombre.requestFocus();
    }
    
    @FXML
    private void onBorrarArtistaBanda() {
        ArtistaBanda seleccionado = tblArtistasBandas.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            error("Sin selección", "Debe seleccionar un artista/banda para eliminar");
            return;
        }
        
        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar el artista/banda?");
        confirmacion.setContentText("Se eliminará: " + seleccionado.getNombre());
        
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                artistaBandaService.eliminarArtistaBanda(seleccionado.getId());
                info("Artista/banda eliminado", "Se eliminó '" + seleccionado.getNombre() + "'");
                cargarArtistasBandas(); // Recargar la tabla
                
                // Si estaba editando el artista/banda eliminado, cancelar edición
                if (artistaBandaEnEdicion != null && artistaBandaEnEdicion.getId().equals(seleccionado.getId())) {
                    cancelarEdicion();
                    limpiarCampos();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                error("Error al eliminar", "No se pudo eliminar el artista/banda: " + e.getMessage());
            }
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtNombre.requestFocus();
    }
    
    private void cancelarEdicion() {
        artistaBandaEnEdicion = null;
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
