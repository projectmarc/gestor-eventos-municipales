package gov.municipio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InicioController {

    // ==> Deben coincidir con los fx:id del inicio.fxml
    @FXML private Label lblPlanificacion, lblConfirmado, lblEjecucion, lblFinalizado;
    @FXML private Label lblFerias, lblConciertos, lblExposiciones, lblTalleres;
    @FXML private Label lblCiclosCine, lblConferencias, lblSeminarios, lblTeatro;

    @FXML
    private void initialize() {
        // TODO: reemplazar por tus contadores reales (EventoStore, servicio, etc.)
        lblPlanificacion.setText("4");
        lblConfirmado.setText("5");
        lblEjecucion.setText("2");
        lblFinalizado.setText("1");

        lblFerias.setText("2");
        lblConciertos.setText("3");
        lblExposiciones.setText("2");
        lblTalleres.setText("3");
        lblCiclosCine.setText("2");
        lblConferencias.setText("1");
        lblSeminarios.setText("2");
        lblTeatro.setText("1");
    }

    // Si luego querés calcular de verdad:
    // private int contarPorEstado(EstadoEvento estado) { ... }
    // private int contarPorTipo(TipoEvento tipo) { ... }
}
