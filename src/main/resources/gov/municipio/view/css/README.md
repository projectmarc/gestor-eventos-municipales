# Material Design CSS Framework para JavaFX

Este directorio contiene los archivos CSS que implementan Material Design para la aplicación de gestión de eventos municipales.

## Estructura de Archivos

### `material-base.css`
Contiene las variables de color, tipografía y estilos base del Material Design:
- **Paleta de colores**: Primary (Azul), Secondary (Índigo), Surface, Background, Error, Success, Warning
- **Tipografía**: Sistema de texto basado en Material Design (Display, Headline, Title, Body, Label)
- **Elevaciones**: Sombras para dar profundidad
- **Variables CSS**: Colores, espaciados, bordes reutilizables

### `material-components.css`
Estilos para componentes específicos:
- **Botones**: Primary, Secondary, Outlined, Text, Icon
- **Cards**: Surface, Elevated, Outlined, Metric cards
- **Tablas**: Estilos Material para TableView
- **Navegación**: Navbar con estilos Material
- **Formularios**: TextField, ComboBox, DatePicker
- **Diálogos**: Estilos para ventanas modales
- **Contenedores**: Layout containers con spacing consistente

### `material-states.css`
Estados especiales y elementos avanzados:
- **Estados de error/éxito/advertencia**
- **Badges de estado** para eventos (Planificación, Confirmado, etc.)
- **Elementos de calendario** con interactividad
- **ListView** con hover y selección
- **CheckBoxes** con estilo Material
- **Tooltips** y elementos de feedback

## Cómo Usar

### En archivos FXML
```xml
<VBox styleClass="content-container"
      stylesheets="@../css/material-base.css,@../css/material-components.css,@../css/material-states.css">
```

### Clases de Utilidad Principales

#### Tipografía
- `text-headline-large`, `text-headline-medium`, `text-headline-small`
- `text-title-large`, `text-title-medium`, `text-title-small`
- `text-body-large`, `text-body-medium`, `text-body-small`
- `text-label-large`, `text-label-medium`, `text-label-small`

#### Botones
- `btn-primary`: Botón principal con color azul
- `btn-secondary`: Botón secundario con color índigo
- `btn-outlined`: Botón con borde, fondo transparente
- `btn-text`: Botón de texto sin fondo
- `btn-icon`: Botón circular para iconos

#### Contenedores
- `content-container`: Container principal con padding y spacing
- `section-container`: Container para secciones con card styling
- `metric-card`: Card especial para métricas del dashboard

#### Estados
- `error`, `success`, `warning`: Colores de texto para estados
- `status-planificacion`, `status-confirmado`, `status-ejecucion`, `status-finalizado`: Badges de estado

#### Espaciado
- `padding-small`, `padding-medium`, `padding-large`
- `spacing-small`, `spacing-medium`, `spacing-large`

## Paleta de Colores

### Primary (Azul)
- 50: #E3F2FD (muy claro)
- 500: #2196F3 (principal)
- 700: #1976D2 (oscuro)

### Secondary (Índigo)
- 500: #3F51B5 (principal)

### Surface & Background
- Surface: #FFFFFF
- Background: #FAFAFA
- Surface Variant: #F5F5F5

### Estados
- Error: #F44336
- Success: #4CAF50
- Warning: #FF9800

## Ejemplos de Uso

### Dashboard con Métricas
```xml
<VBox styleClass="content-container">
    <Label text="Dashboard" styleClass="text-headline-medium"/>
    <HBox styleClass="spacing-large">
        <VBox styleClass="metric-card">
            <Label text="150" styleClass="metric-value"/>
            <Label text="Total Eventos" styleClass="metric-label"/>
        </VBox>
    </HBox>
</VBox>
```

### Formulario
```xml
<VBox styleClass="section-container">
    <Label text="Crear Evento" styleClass="text-title-medium"/>
    <VBox spacing="8">
        <Label text="Nombre" styleClass="text-label-large"/>
        <TextField fx:id="txtNombre"/>
    </VBox>
    <Button text="Guardar" styleClass="btn-primary"/>
</VBox>
```

### Tabla
```xml
<TableView styleClass="table-view">
    <!-- columns -->
</TableView>
```

## Características Destacadas

1. **Consistencia**: Todos los elementos siguen las especificaciones Material Design
2. **Responsivo**: Spacing y padding adaptativos
3. **Accesibilidad**: Contrastes apropiados y focus states
4. **Modular**: Archivos separados por funcionalidad
5. **Reutilizable**: Sistema de clases de utilidad
6. **Interactivo**: Estados hover, pressed, focused para todos los componentes

## Personalización

Para cambiar colores principales, editar las variables en `material-base.css`:
```css
.root {
    -fx-primary-500: #TU_COLOR_AQUI;
    -fx-secondary-500: #TU_COLOR_SECUNDARIO;
}
```

Para agregar nuevos componentes, seguir el patrón establecido en `material-components.css`.
