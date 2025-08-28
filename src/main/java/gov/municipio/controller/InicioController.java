package gov.municipio.controller;

import gov.municipio.application.EventoService;
import gov.municipio.application.PersonaService;
import gov.municipio.domain.model.enums.EstadoEvento;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Map;

public class InicioController {
    
    private final PersonaService personaService;
    private final EventoService eventoService;
    
    // Labels para métricas básicas
    @FXML private Label lblCantidadPersonas;
    @FXML private Label lblCantidadEventosTotal;
    @FXML private Label lblCantidadEventosHoy;
    
    // Labels para tipos de eventos
    @FXML private Label lblCantidadFerias;
    @FXML private Label lblCantidadConciertos;
    @FXML private Label lblCantidadExposiciones;
    @FXML private Label lblCantidadTalleres;
    @FXML private Label lblCantidadCiclosCine;
    
    // Labels para estados de eventos
    @FXML private Label lblCantidadPlanificacion;
    @FXML private Label lblCantidadConfirmados;
    @FXML private Label lblCantidadEnEjecucion;
    @FXML private Label lblCantidadFinalizados;
    
    public InicioController(PersonaService personaService, EventoService eventoService) {
        this.personaService = personaService;
        this.eventoService = eventoService;
    }
    
    @FXML
    private void initialize() {
        cargarMetricas();
    }
    
    private void cargarMetricas() {
        try {
            // Métricas básicas
            cargarMetricasBasicas();
            
            // Métricas por tipo de evento
            cargarMetricasPorTipoEvento();
            
            // Métricas por estado de evento
            cargarMetricasPorEstadoEvento();
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErrorEnLabels();
        }
    }
    
    private void cargarMetricasBasicas() {
        // Cantidad de personas registradas
        int cantidadPersonas = personaService.obtenerTodasLasPersonas().size();
        lblCantidadPersonas.setText(String.valueOf(cantidadPersonas));
        
        // Cantidad total de eventos
        int cantidadEventosTotal = eventoService.obtenerCantidadTotalEventos();
        lblCantidadEventosTotal.setText(String.valueOf(cantidadEventosTotal));
        
        // Cantidad de eventos hoy
        int cantidadEventosHoy = eventoService.obtenerCantidadEventosHoy();
        lblCantidadEventosHoy.setText(String.valueOf(cantidadEventosHoy));
    }
    
    private void cargarMetricasPorTipoEvento() {
        Map<String, Integer> eventosPorTipo = eventoService.obtenerCantidadEventosPorTipo();
        
        lblCantidadFerias.setText(String.valueOf(eventosPorTipo.get("Feria")));
        lblCantidadConciertos.setText(String.valueOf(eventosPorTipo.get("Concierto")));
        lblCantidadExposiciones.setText(String.valueOf(eventosPorTipo.get("Exposición")));
        lblCantidadTalleres.setText(String.valueOf(eventosPorTipo.get("Taller")));
        lblCantidadCiclosCine.setText(String.valueOf(eventosPorTipo.get("Ciclo de cine")));
    }
    
    private void cargarMetricasPorEstadoEvento() {
        Map<EstadoEvento, Integer> eventosPorEstado = eventoService.obtenerCantidadEventosPorEstado();
        
        lblCantidadPlanificacion.setText(String.valueOf(eventosPorEstado.get(EstadoEvento.PLANIFICACION)));
        lblCantidadConfirmados.setText(String.valueOf(eventosPorEstado.get(EstadoEvento.CONFIRMADO)));
        lblCantidadEnEjecucion.setText(String.valueOf(eventosPorEstado.get(EstadoEvento.EN_EJECUCION)));
        lblCantidadFinalizados.setText(String.valueOf(eventosPorEstado.get(EstadoEvento.FINALIZADO)));
    }
    
    private void mostrarErrorEnLabels() {
        // En caso de error, mostrar "Error" en todos los labels
        lblCantidadPersonas.setText("Error");
        lblCantidadEventosTotal.setText("Error");
        lblCantidadEventosHoy.setText("Error");
        
        lblCantidadFerias.setText("Error");
        lblCantidadConciertos.setText("Error");
        lblCantidadExposiciones.setText("Error");
        lblCantidadTalleres.setText("Error");
        lblCantidadCiclosCine.setText("Error");
        
        lblCantidadPlanificacion.setText("Error");
        lblCantidadConfirmados.setText("Error");
        lblCantidadEnEjecucion.setText("Error");
        lblCantidadFinalizados.setText("Error");
    }
}
