# 📅 Calendario Reutilizable - Documentación

## Descripción
Componente de calendario funcional y reutilizable para JavaFX que permite navegar entre meses, seleccionar fechas y mostrar eventos de manera visual.

## Archivos del Componente
- `calendario-view.fxml` - Vista FXML del calendario
- `CalendarioController.java` - Controlador con la lógica del calendario  
- `CalendarioFactory.java` - Factory para crear instancias del calendario
- Estilos incluidos en `dashboard.css`

## Características

### ✅ Funcionalidades Implementadas
- **Navegación entre meses** con botones anterior/siguiente
- **Selección de fechas** con callback personalizable
- **Destacado visual** de días con eventos
- **Día actual** marcado con color especial
- **Contador de eventos** por mes
- **Interfaz completamente localizada** en español
- **Estilos CSS modernos** integrados

### 🎯 Casos de Uso
- Dashboard principal del sistema
- Módulo de gestión de eventos
- Planificación de actividades
- Visualización de calendario de cualquier aplicación

## Uso Básico

### 1. Incluir en FXML
```xml
<!-- Opción 1: Usar fx:include -->
<fx:include fx:id="calendario" source="calendario-view.fxml" />

<!-- Opción 2: Crear programáticamente (ver factory) -->
```

### 2. Configurar en el Controlador
```java
@FXML
private CalendarioController calendarioController;

@Override
public void initialize(URL location, ResourceBundle resources) {
    // Configurar callback para selección de fechas
    if (calendarioController != null) {
        calendarioController.setCallback(this::onDateSelected);
    }
}

private void onDateSelected(LocalDate date) {
    System.out.println("Fecha seleccionada: " + date);
    // Lógica personalizada aquí
}
```

### 3. Uso con Factory (Programático)
```java
import gov.municipio.util.CalendarioFactory;

// Crear calendario básico
CalendarioFactory.CalendarioResult resultado = CalendarioFactory.crearCalendario();
VBox calendarioView = resultado.getView();
CalendarioController controller = resultado.getController();

// Crear calendario con eventos
Set<LocalDate> fechasConEventos = new HashSet<>();
fechasConEventos.add(LocalDate.now().plusDays(3));
fechasConEventos.add(LocalDate.now().plusDays(7));

CalendarioController.CalendarioCallback callback = date -> {
    System.out.println("Evento seleccionado: " + date);
};

CalendarioFactory.CalendarioResult calendarioConEventos = 
    CalendarioFactory.crearCalendarioConEventos(fechasConEventos, callback);
```

## API del Controlador

### Métodos Principales

#### Gestión de Eventos
```java
// Establecer fechas con eventos
controller.setFechasConEventos(Set<LocalDate> fechas);

// Agregar fecha con evento
controller.agregarFechaConEvento(LocalDate fecha);

// Remover fecha con evento  
controller.removerFechaConEvento(LocalDate fecha);
```

#### Navegación
```java
// Navegar a mes/año específico
controller.navigateToYearMonth(YearMonth.of(2025, 12));

// Ir a la fecha actual
controller.navigateToToday();

// Obtener fecha seleccionada
LocalDate fechaSeleccionada = controller.getSelectedDate();
```

#### Callbacks
```java
// Configurar callback para selección
controller.setCallback(date -> {
    // Tu lógica aquí
    mostrarEventosDelDia(date);
});
```

## Estilos CSS

### Clases CSS Principales
- `.calendario-panel` - Panel contenedor
- `.calendario-header-nav` - Barra de navegación
- `.calendario-grid` - Grid del calendario
- `.calendario-dia` - Día normal
- `.calendario-dia-evento` - Día con eventos (naranja)
- `.calendario-dia-hoy` - Día actual (azul)
- `.calendario-nav-button` - Botones anterior/siguiente

### Personalización
```css
/* Cambiar color de días con eventos */
.calendario-dia-evento {
    -fx-background-color: #your-color;
}

/* Cambiar color del día actual */
.calendario-dia-hoy {
    -fx-background-color: #your-color;
}
```

## Ejemplos de Integración

### Ejemplo 1: Dashboard Principal
```java
// En el controlador del dashboard
private void integrarCalendario() {
    calendarioController.setCallback(fecha -> {
        cargarEventosDelDia(fecha);
        actualizarVistaEventos(fecha);
    });
    
    // Cargar eventos del mes actual
    Set<LocalDate> eventos = eventoService.getFechasConEventos(YearMonth.now());
    calendarioController.setFechasConEventos(eventos);
}
```

### Ejemplo 2: Selector de Fecha para Formulario
```java
// Para formularios de creación de eventos
private void mostrarSelectorFecha() {
    try {
        CalendarioFactory.CalendarioResult calendario = CalendarioFactory.crearCalendario();
        
        calendario.getController().setCallback(fechaSeleccionada -> {
            campoFecha.setText(fechaSeleccionada.toString());
            cerrarSelectorFecha();
        });
        
        // Mostrar en popup o dialog
        mostrarEnPopup(calendario.getView());
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

### Ejemplo 3: Vista de Planificación
```java
// Para módulo de planificación
private void configurarVistaPlanificacion() {
    // Obtener eventos del servicio
    Map<LocalDate, List<Evento>> eventosPorFecha = eventoService.getAllEventos();
    
    calendarioController.setFechasConEventos(eventosPorFecha.keySet());
    calendarioController.setCallback(fecha -> {
        List<Evento> eventosDelDia = eventosPorFecha.get(fecha);
        mostrarDetallesEventos(eventosDelDia);
    });
}
```

## Arquitectura

### Patrón MVC
- **Model**: LocalDate, YearMonth, Set<LocalDate>
- **View**: calendario-view.fxml
- **Controller**: CalendarioController

### Patrón Observer (Callback)
- Interface `CalendarioCallback` para notificar selecciones
- Desacoplamiento entre calendario y lógica de negocio

### Factory Pattern
- `CalendarioFactory` para crear instancias preconfiguradas
- Simplifica la creación y configuración

## Dependencias
- **JavaFX**: Para la interfaz gráfica
- **Java Time API**: Para manejo de fechas
- **FXML**: Para la definición de la vista

## Notas Técnicas
- Compatible con Java 11+
- Localización en español (es-ES)
- Thread-safe para operaciones básicas
- Optimizado para rendimiento en navegación rápida

## Próximas Mejoras Posibles
- [ ] Soporte para múltiples localizaciones
- [ ] Eventos con diferentes colores por categoría  
- [ ] Vista mensual/semanal/diaria
- [ ] Integración con bases de datos
- [ ] Tooltips con información de eventos
- [ ] Exportación a formatos de calendario (iCal)

---
**Creado para**: Sistema de Gestión de Eventos Culturales  
**Versión**: 1.0  
**Fecha**: Agosto 2025
