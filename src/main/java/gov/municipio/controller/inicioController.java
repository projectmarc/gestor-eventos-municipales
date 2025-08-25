package gov.municipio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class inicioController {
    @FXML private Label lblBienvenida;
    @FXML private Button btnNuevoEvento, btnPersonas, btnCalendario;
    @FXML private Label lblTotalEventos, lblEventosHoy, lblEventosSemana;
    @FXML private TableView<?> tablaRecientes;
    @FXML private TableColumn<?, ?> colNombre, colTipo, colFecha, colEstado;

    @FXML
    private void initialize() {
        if (lblBienvenida != null) {
            lblBienvenida.setText("Bienvenido/a al Gestor de Eventos Municipales");
        }
    }

    @FXML private void onNuevoEvento() {}
    @FXML private void onPersonas() {}
    @FXML private void onCalendario() {}
}
