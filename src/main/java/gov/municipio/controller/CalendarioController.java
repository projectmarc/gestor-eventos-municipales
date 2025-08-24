package gov.municipio.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Controlador para el componente de calendario reutilizable
 */
public class CalendarioController implements Initializable {

    @FXML
    private GridPane calendarioGrid;
    
    @FXML
    private Label mesAnoLabel;
    
    @FXML
    private Label calendarioInfo;
    
    @FXML
    private Label eventoCountLabel;
    
    @FXML
    private Button prevMonthButton;
    
    @FXML
    private Button nextMonthButton;

    private YearMonth currentYearMonth;
    private Set<LocalDate> fechasConEventos;
    private LocalDate selectedDate;
    
    // Callback para cuando se selecciona una fecha
    private CalendarioCallback callback;
    
    /**
     * Interface para callback cuando se selecciona una fecha
     */
    public interface CalendarioCallback {
        void onDateSelected(LocalDate date);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentYearMonth = YearMonth.now();
        fechasConEventos = new HashSet<>();
        
        // Agregar algunas fechas de ejemplo con eventos
        fechasConEventos.add(LocalDate.of(2025, 8, 24)); // Hoy
        fechasConEventos.add(LocalDate.of(2025, 8, 28));
        fechasConEventos.add(LocalDate.of(2025, 9, 2));
        fechasConEventos.add(LocalDate.of(2025, 9, 5));
        
        updateCalendar();
    }

    /**
     * Actualiza la visualización del calendario
     */
    private void updateCalendar() {
        // Limpiar días anteriores (manteniendo headers)
        calendarioGrid.getChildren().removeIf(node -> 
            GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        
        // Actualizar título del mes/año
        Locale spanishLocale = Locale.forLanguageTag("es-ES");
        String mesNombre = currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, spanishLocale);
        String titulo = mesNombre.substring(0, 1).toUpperCase() + mesNombre.substring(1) + " " + currentYearMonth.getYear();
        mesAnoLabel.setText(titulo);
        
        // Obtener primer día del mes y cuántos días tiene
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int daysInMonth = currentYearMonth.lengthOfMonth();
        
        // Determinar en qué día de la semana empieza el mes (0=Domingo, 6=Sábado)
        int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        
        // Agregar días del mes
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            Label dayLabel = createDayLabel(date, day);
            
            // Calcular posición en el grid
            int totalDaysFromStart = day - 1 + startDayOfWeek;
            int row = (totalDaysFromStart / 7) + 1; // +1 porque la fila 0 son los headers
            int col = totalDaysFromStart % 7;
            
            GridPane.setRowIndex(dayLabel, row);
            GridPane.setColumnIndex(dayLabel, col);
            calendarioGrid.getChildren().add(dayLabel);
        }
        
        // Actualizar contador de eventos
        long eventosEsteMes = fechasConEventos.stream()
            .filter(fecha -> YearMonth.from(fecha).equals(currentYearMonth))
            .count();
        eventoCountLabel.setText(eventosEsteMes + " eventos este mes");
    }

    /**
     * Crea un label para un día del calendario
     */
    private Label createDayLabel(LocalDate date, int day) {
        Label dayLabel = new Label(String.valueOf(day));
        dayLabel.setAlignment(Pos.CENTER);
        dayLabel.setMaxWidth(Double.MAX_VALUE);
        dayLabel.setMaxHeight(Double.MAX_VALUE);
        
        // Determinar estilo según el tipo de día
        if (date.equals(LocalDate.now())) {
            dayLabel.getStyleClass().add("calendario-dia-hoy");
        } else if (fechasConEventos.contains(date)) {
            dayLabel.getStyleClass().add("calendario-dia-evento");
        } else {
            dayLabel.getStyleClass().add("calendario-dia");
        }
        
        // Agregar evento de click
        dayLabel.setOnMouseClicked(event -> selectDate(date));
        
        return dayLabel;
    }

    /**
     * Selecciona una fecha y ejecuta el callback si está definido
     */
    private void selectDate(LocalDate date) {
        selectedDate = date;
        
        // Actualizar información
        Locale spanishLocale = Locale.forLanguageTag("es-ES");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy", spanishLocale);
        String fechaFormateada = date.format(formatter);
        
        if (fechasConEventos.contains(date)) {
            calendarioInfo.setText("Fecha seleccionada: " + fechaFormateada + " (Con eventos)");
        } else {
            calendarioInfo.setText("Fecha seleccionada: " + fechaFormateada);
        }
        
        // Ejecutar callback si está definido
        if (callback != null) {
            callback.onDateSelected(date);
        }
    }

    /**
     * Navegar al mes anterior
     */
    @FXML
    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }

    /**
     * Navegar al mes siguiente
     */
    @FXML
    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }

    // Métodos públicos para configuración externa

    /**
     * Establece las fechas que tienen eventos
     */
    public void setFechasConEventos(Set<LocalDate> fechas) {
        this.fechasConEventos = new HashSet<>(fechas);
        updateCalendar();
    }

    /**
     * Agrega una fecha con evento
     */
    public void agregarFechaConEvento(LocalDate fecha) {
        fechasConEventos.add(fecha);
        updateCalendar();
    }

    /**
     * Remueve una fecha con evento
     */
    public void removerFechaConEvento(LocalDate fecha) {
        fechasConEventos.remove(fecha);
        updateCalendar();
    }

    /**
     * Establece el callback para cuando se selecciona una fecha
     */
    public void setCallback(CalendarioCallback callback) {
        this.callback = callback;
    }

    /**
     * Obtiene la fecha actualmente seleccionada
     */
    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    /**
     * Navega a un mes/año específico
     */
    public void navigateToYearMonth(YearMonth yearMonth) {
        currentYearMonth = yearMonth;
        updateCalendar();
    }

    /**
     * Navega a la fecha actual
     */
    public void navigateToToday() {
        currentYearMonth = YearMonth.now();
        selectDate(LocalDate.now());
        updateCalendar();
    }
}
