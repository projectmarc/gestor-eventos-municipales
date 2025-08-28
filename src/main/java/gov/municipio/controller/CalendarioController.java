package gov.municipio.controller;

import gov.municipio.application.EventoService;
import gov.municipio.config.ServiceContainer;
import gov.municipio.domain.model.evento.Evento;
import gov.municipio.domain.model.enums.EstadoEvento;
import gov.municipio.ui.nav.Router;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para la vista de calendario mensual.
 * Muestra eventos organizados por fecha con navegación entre meses.
 */
public class CalendarioController {
    
    @FXML private Label lblMesAnio;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private GridPane gridCalendario;
    @FXML private ListView<String> listEventosDia;
    @FXML private Label lblFechaSeleccionada;
    
    private Router router;
    private EventoService eventoService;
    
    private YearMonth mesActual;
    private LocalDate fechaSeleccionada;
    private Map<LocalDate, List<Evento>> eventosPorFecha;
    
    // Formato para mostrar el mes y año
    private final DateTimeFormatter formatoMesAnio = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.of("es", "ES"));
    
    public void setRouter(Router router) {
        this.router = router;
    }
    
    public void setServiceContainer(ServiceContainer serviceContainer) {
        this.eventoService = serviceContainer.eventoService();
    }
    
    @FXML
    private void initialize() {
        mesActual = YearMonth.now();
        fechaSeleccionada = LocalDate.now();
        
        // Configurar los encabezados de días de la semana
        configurarEncabezadosDias();
        
        // Configurar la lista de eventos para que sea clickeable
        configurarListaEventos();
        
        // Cargar el calendario inicial
        actualizarCalendario();
    }
    
    /**
     * Configura los encabezados de los días de la semana (LUN-DOM).
     */
    private void configurarEncabezadosDias() {
        String[] diasSemana = {"LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM"};
        
        for (int i = 0; i < diasSemana.length; i++) {
            Label lblDia = new Label(diasSemana[i]);
            lblDia.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;");
            lblDia.setMaxWidth(Double.MAX_VALUE);
            lblDia.setAlignment(Pos.CENTER);
            gridCalendario.add(lblDia, i, 0);
        }
    }
    
    /**
     * Configura la lista de eventos para permitir navegación al hacer clic.
     */
    private void configurarListaEventos() {
        listEventosDia.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String eventoSeleccionado = listEventosDia.getSelectionModel().getSelectedItem();
                if (eventoSeleccionado != null && router != null) {
                    // Extraer el ID del evento del texto mostrado
                    Long eventoId = extraerIdDelTextoEvento(eventoSeleccionado);
                    if (eventoId != null) {
                        navegarAGestionarEvento(eventoId);
                    }
                }
            }
        });
    }
    
    /**
     * Extrae el ID del evento del texto mostrado en la lista.
     */
    private Long extraerIdDelTextoEvento(String textoEvento) {
        try {
            // El formato es "ID: nombre del evento"
            String[] partes = textoEvento.split(":");
            if (partes.length >= 2) {
                return Long.parseLong(partes[0].trim());
            }
        } catch (NumberFormatException e) {
            System.err.println("Error al extraer ID del evento: " + textoEvento);
        }
        return null;
    }
    
    /**
     * Navega a la pantalla de gestionar evento.
     */
    private void navegarAGestionarEvento(Long eventoId) {
        try {
            Evento evento = eventoService.buscarEventoPorId(eventoId).orElse(null);
            if (evento != null && router != null) {
                router.goToGestionarEvento(evento);
            }
        } catch (Exception e) {
            System.err.println("Error al navegar al evento: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza todo el calendario: título, eventos y celdas.
     */
    private void actualizarCalendario() {
        // Actualizar título del mes/año
        lblMesAnio.setText(mesActual.format(formatoMesAnio).toUpperCase());
        
        // Cargar eventos del mes
        cargarEventosDelMes();
        
        // Actualizar las celdas del calendario
        actualizarCeldasCalendario();
        
        // Actualizar la información de la fecha seleccionada
        actualizarInfoFechaSeleccionada();
    }
    
    /**
     * Carga los eventos del mes actual desde el servicio.
     */
    private void cargarEventosDelMes() {
        try {
            LocalDate primerDia = mesActual.atDay(1);
            LocalDate ultimoDia = mesActual.atEndOfMonth();
            
            List<Evento> eventos = eventoService.obtenerEventosPorRangoFechas(primerDia, ultimoDia);
            
            // Agrupar eventos por fecha
            eventosPorFecha = eventos.stream()
                .collect(Collectors.groupingBy(Evento::getFechaInicio));
                
        } catch (Exception e) {
            System.err.println("Error al cargar eventos: " + e.getMessage());
            eventosPorFecha = Map.of();
        }
    }
    
    /**
     * Actualiza las celdas del calendario con las fechas y puntos de eventos.
     */
    private void actualizarCeldasCalendario() {
        // Limpiar celdas existentes (excepto encabezados)
        gridCalendario.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        
        LocalDate primerDiaDelMes = mesActual.atDay(1);
        int diaSemanaInicial = primerDiaDelMes.getDayOfWeek().getValue(); // 1=Lunes, 7=Domingo
        
        // Ajustar para que Domingo sea 0
        if (diaSemanaInicial == 7) {
            diaSemanaInicial = 0;
        }
        
        int diasEnMes = mesActual.lengthOfMonth();
        int filaActual = 1;
        int columnaActual = diaSemanaInicial;
        
        for (int dia = 1; dia <= diasEnMes; dia++) {
            LocalDate fecha = mesActual.atDay(dia);
            StackPane celdaFecha = crearCeldaFecha(fecha);
            
            gridCalendario.add(celdaFecha, columnaActual, filaActual);
            
            columnaActual++;
            if (columnaActual > 6) {
                columnaActual = 0;
                filaActual++;
            }
        }
    }
    
    /**
     * Crea una celda para una fecha específica con puntos indicadores de eventos.
     */
    private StackPane crearCeldaFecha(LocalDate fecha) {
        StackPane celda = new StackPane();
        celda.setPrefSize(80, 60);
        celda.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1;");
        
        // Resaltar la fecha seleccionada
        if (fecha.equals(fechaSeleccionada)) {
            celda.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2; -fx-background-color: #E3F2FD;");
        }
        
        VBox contenido = new VBox();
        contenido.setAlignment(Pos.TOP_CENTER);
        contenido.setSpacing(2);
        
        // Número del día
        Label lblDia = new Label(String.valueOf(fecha.getDayOfMonth()));
        lblDia.setStyle("-fx-font-weight: bold;");
        contenido.getChildren().add(lblDia);
        
        // Puntos indicadores de eventos
        List<Evento> eventosDelDia = eventosPorFecha.getOrDefault(fecha, List.of());
        if (!eventosDelDia.isEmpty()) {
            VBox puntosContainer = new VBox();
            puntosContainer.setAlignment(Pos.CENTER);
            puntosContainer.setSpacing(1);
            
            // Mostrar máximo 3 puntos
            int cantidadPuntos = Math.min(eventosDelDia.size(), 3);
            for (int i = 0; i < cantidadPuntos; i++) {
                Circle punto = new Circle(3);
                punto.setFill(obtenerColorPorEstado(eventosDelDia.get(i).getEstado()));
                puntosContainer.getChildren().add(punto);
            }
            
            contenido.getChildren().add(puntosContainer);
        }
        
        celda.getChildren().add(contenido);
        
        // Hacer clickeable la celda
        celda.setOnMouseClicked(event -> {
            fechaSeleccionada = fecha;
            actualizarCalendario(); // Refrescar para resaltar la nueva selección
        });
        
        return celda;
    }
    
    /**
     * Obtiene el color del punto según el estado del evento.
     */
    private javafx.scene.paint.Color obtenerColorPorEstado(EstadoEvento estado) {
        return switch (estado) {
            case PLANIFICACION -> javafx.scene.paint.Color.ORANGE;
            case CONFIRMADO -> javafx.scene.paint.Color.GREEN;
            case EN_EJECUCION -> javafx.scene.paint.Color.BLUE;
            case FINALIZADO -> javafx.scene.paint.Color.GRAY;
        };
    }
    
    /**
     * Actualiza la información de la fecha seleccionada y la lista de eventos.
     */
    private void actualizarInfoFechaSeleccionada() {
        if (fechaSeleccionada != null) {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale.of("es", "ES"));
            lblFechaSeleccionada.setText(fechaSeleccionada.format(formato));
            
            // Actualizar lista de eventos
            List<Evento> eventosDelDia = eventosPorFecha.getOrDefault(fechaSeleccionada, List.of());
            listEventosDia.getItems().clear();
            
            if (eventosDelDia.isEmpty()) {
                listEventosDia.getItems().add("No hay eventos programados");
            } else {
                for (Evento evento : eventosDelDia) {
                    String textoEvento = String.format("%d: %s (%s)", 
                        evento.getId(), 
                        evento.getNombre(), 
                        evento.getEstado().toString());
                    listEventosDia.getItems().add(textoEvento);
                }
            }
        }
    }
    
    @FXML
    private void mesAnterior() {
        mesActual = mesActual.minusMonths(1);
        actualizarCalendario();
    }
    
    @FXML
    private void mesSiguiente() {
        mesActual = mesActual.plusMonths(1);
        actualizarCalendario();
    }
}
