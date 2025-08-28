package gov.municipio.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;

import static java.time.DayOfWeek.*;

public class CalendarioController {

    @FXML private Label lblMes;
    @FXML private GridPane grid;
    @FXML private ListView<String> lstEventos;

    private YearMonth ym = YearMonth.now();
    private LocalDate selected = LocalDate.now();
    private static final Locale ES = Locale.forLanguageTag("es-AR");

    @FXML
    private void initialize() {
        grid.setHgap(6);
        grid.setVgap(6);
        grid.setPadding(new Insets(10));   // <- lo seteás por código
        renderMonth();
    }

    @FXML private void prevMonth(){ ym = ym.minusMonths(1); renderMonth(); }
    @FXML private void nextMonth(){ ym = ym.plusMonths(1);  renderMonth(); }

    @FXML
    private void crearEventoDiaSeleccionado() {
        // TODO: abrir flujo de “nuevo evento” pre-cargando 'selected'
        new Alert(Alert.AlertType.INFORMATION, "Crear evento en: " + selected).showAndWait();
    }

    private void renderMonth() {
        lblMes.setText(ym.getMonth().getDisplayName(TextStyle.FULL, ES) + " " + ym.getYear());
        grid.getChildren().clear();

        // Encabezado LU..DO
        DayOfWeek[] dow = {MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY};
        for (int c=0;c<7;c++) {
            Label l = new Label(dow[c].getDisplayName(TextStyle.SHORT, ES).toUpperCase());
            l.setStyle("-fx-font-weight:bold;");
            grid.add(l, c, 0);
        }

        LocalDate first = ym.atDay(1);
        int shift = (first.getDayOfWeek().getValue()+6)%7; // LU=0..DO=6
        LocalDate cursor = first.minusDays(shift);

        for (int row=1; row<=6; row++) {
            for (int col=0; col<7; col++) {
                LocalDate day = cursor;
                VBox cell = buildDayCell(day);
                grid.add(cell, col, row);
                cursor = cursor.plusDays(1);
            }
        }
        lstEventos.getItems().setAll(describirEventosDelDia(selected));
    }

    private VBox buildDayCell(LocalDate day) {
        Label lbl = new Label(String.valueOf(day.getDayOfMonth()));
        lbl.setStyle("-fx-font-size:12; -fx-font-weight:bold;");

        FlowPane chips = new FlowPane(4,4);
        chips.setPrefWrapLength(120);

        int n = contarEventos(day); // TODO: conectar a tu store
        for (int i=0; i<Math.min(n, 3); i++) {
            Label chip = new Label("●");
            chip.setStyle("-fx-font-size:9;");
            chips.getChildren().add(chip);
        }
        if (n > 3) chips.getChildren().add(new Label("+" + (n-3)));

        VBox box = new VBox(6, lbl, chips);
        box.setPadding(new Insets(6));
        box.setStyle(styleForDay(day));
        box.setOnMouseClicked(e -> {
            selected = day;
            lstEventos.getItems().setAll(describirEventosDelDia(day));
            renderMonth(); // refresca borde de selección
        });
        return box;
    }

    private String styleForDay(LocalDate d) {
        String base = "-fx-border-color:#ddd; -fx-background-color:#fff;";
        if (!YearMonth.from(d).equals(ym)) base += " -fx-opacity:0.55;";
        if (d.equals(LocalDate.now())) base += " -fx-background-color: linear-gradient(#fff,#f0faff);";
        if (d.equals(selected)) base += " -fx-border-color:#3894FF; -fx-border-width:2;";
        return base;
    }

    // ==== Conexión con tus datos (reemplazar por tu EventoStore) ====
    private int contarEventos(LocalDate d) {
        // Ejemplo:
        // return (int) EventoStore.getList().stream()
        //          .filter(ev -> ev.getFecha().equals(d))
        //          .count();
        return 0;
    }
    private List<String> describirEventosDelDia(LocalDate d) {
        // Ejemplo:
        // return EventoStore.getList().stream()
        //     .filter(ev -> ev.getFecha().equals(d))
        //     .map(ev -> ev.getHora() + " " + ev.getNombre() +
        //           (ev.isRequiereInscripcion()
        //             ? " (inscripción" + (ev.tieneCupo()? " " + ev.getCupoRestante()+"/"+ev.getCupo() : " sin cupo") + ")"
        //             : " (abierto)"))
        //     .toList();
        return List.of();
    }
}
