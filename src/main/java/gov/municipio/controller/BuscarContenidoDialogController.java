package gov.municipio.controller;

import gov.municipio.application.ArtistaBandaService;
import gov.municipio.application.PeliculaService;
import gov.municipio.domain.model.ArtistaBanda;
import gov.municipio.domain.model.Pelicula;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

public class BuscarContenidoDialogController {
    
    @FXML
    private Label lblTitulo;
    
    @FXML
    private Label lblInstruccion;
    
    @FXML
    private TextField txtId;
    
    @FXML
    private Label lblError;
    
    @FXML
    private Button btnAceptar;
    
    @FXML
    private Button btnCancelar;
    
    private PeliculaService peliculaService;
    private ArtistaBandaService artistaBandaService;
    private TipoContenido tipoContenido;
    private Object contenidoSeleccionado;
    private boolean resultado = false;
    
    public enum TipoContenido {
        PELICULA, ARTISTA_BANDA
    }
    
    public void setPeliculaService(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }
    
    public void setArtistaBandaService(ArtistaBandaService artistaBandaService) {
        this.artistaBandaService = artistaBandaService;
    }
    
    public void setTipoContenido(TipoContenido tipo) {
        this.tipoContenido = tipo;
        configurarInterfazSegunTipo();
    }
    
    @FXML
    private void initialize() {
        // Limpiar cualquier error cuando se cambie el texto
        txtId.textProperty().addListener((obs, oldText, newText) -> {
            lblError.setVisible(false);
            lblError.setManaged(false);
        });
    }
    
    private void configurarInterfazSegunTipo() {
        if (tipoContenido == TipoContenido.PELICULA) {
            lblTitulo.setText("Agregar Película");
            lblInstruccion.setText("ID de la película:");
            txtId.setPromptText("Ingrese el ID de la película");
        } else if (tipoContenido == TipoContenido.ARTISTA_BANDA) {
            lblTitulo.setText("Agregar Artista/Banda");
            lblInstruccion.setText("ID del artista/banda:");
            txtId.setPromptText("Ingrese el ID del artista/banda");
        }
    }
    
    @FXML
    private void aceptar() {
        String idTexto = txtId.getText().trim();
        
        if (idTexto.isEmpty()) {
            mostrarError("Debe ingresar un ID");
            return;
        }
        
        try {
            Long id = Long.parseLong(idTexto);
            
            if (tipoContenido == TipoContenido.PELICULA) {
                buscarPelicula(id);
            } else if (tipoContenido == TipoContenido.ARTISTA_BANDA) {
                buscarArtistaBanda(id);
            }
            
        } catch (NumberFormatException e) {
            mostrarError("El ID debe ser un número válido");
        }
    }
    
    private void buscarPelicula(Long id) {
        try {
            Optional<Pelicula> peliculaOpt = peliculaService.buscarPeliculaPorId(id);
            
            if (peliculaOpt.isPresent()) {
                contenidoSeleccionado = peliculaOpt.get();
                resultado = true;
                cerrarDialog();
            } else {
                mostrarError("No se encontró una película con ID: " + id);
            }
        } catch (Exception e) {
            mostrarError("Error al buscar película: " + e.getMessage());
        }
    }
    
    private void buscarArtistaBanda(Long id) {
        try {
            Optional<ArtistaBanda> artistaOpt = artistaBandaService.buscarArtistaBandaPorId(id);
            
            if (artistaOpt.isPresent()) {
                contenidoSeleccionado = artistaOpt.get();
                resultado = true;
                cerrarDialog();
            } else {
                mostrarError("No se encontró un artista/banda con ID: " + id);
            }
        } catch (Exception e) {
            mostrarError("Error al buscar artista/banda: " + e.getMessage());
        }
    }
    
    @FXML
    private void cancelar() {
        resultado = false;
        cerrarDialog();
    }
    
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
    
    private void cerrarDialog() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
    
    public boolean getResultado() {
        return resultado;
    }
    
    public Object getContenidoSeleccionado() {
        return contenidoSeleccionado;
    }
    
    public TipoContenido getTipoContenido() {
        return tipoContenido;
    }
}
