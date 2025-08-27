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

    

public class EventosFormController {
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