# Gestor de Eventos Municipales

Sistema integral para la gestión de eventos culturales municipales desarrollado con Java y JavaFX. Permite administrar ferias, conciertos, exposiciones, talleres y ciclos de cine con un modelo de dominio rico y una interfaz gráfica moderna.

## 📋 Características Principales

- **Gestión Completa de Eventos**: Creación, edición y administración de eventos culturales
- **Tipos de Eventos Soportados**:
  - 🎪 Ferias
  -   Conciertos  
  - 🖼️ Exposiciones
  - 📚 Talleres
  - 🎬 Ciclos de cine
- **Sistema de Participaciones**: Gestión de organizadores, instructores, curadores y asistentes
- **Estados del Evento**: Flujo controlado (Planificación → Confirmado → En Ejecución → Finalizado)
- **Validaciones de Negocio**: Cupos máximos, fechas, roles y transiciones de estado
- **Interfaz Gráfica Moderna**: Implementada con JavaFX y CSS personalizado
- **Persistencia de Datos**: Base de datos H2 embebida con JPA/Hibernate

## 🛠️ Stack Tecnológico

- **Java 21** - Lenguaje de programación
- **JavaFX 23.0.2** - Framework para la interfaz gráfica
- **Maven** - Gestión de dependencias y construcción
- **JPA/Hibernate 6.5.2** - Persistencia de datos
- **H2 Database 2.2.224** - Base de datos embebida
- **CSS** - Estilos personalizados para la interfaz

## 🏗️ Arquitectura

El proyecto sigue una arquitectura en capas con Domain Driven Design:

```
src/main/java/gov/municipio/
├── domain/          # Modelos de dominio y reglas de negocio
├── application/     # Servicios de aplicación
├── persistence/     # Repositorios y acceso a datos
├── controller/      # Controladores de la interfaz
├── ui/             # Navegación y utilidades de UI
├── config/         # Configuración e inyección de dependencias

## 🚀 Instalación y Ejecución

### Prerrequisitos

- **Java 21** o superior
- **Maven 3.6+**

### Ejecución

1. **Clonar el repositorio**:
```bash
git clone <url-del-repositorio>
cd gestor-eventos-municipales
```

2. **Compilar el proyecto**:
```bash
mvn clean compile
```

3. **Ejecutar la aplicación**:
```bash
mvn javafx:run
```

### Base de Datos

La aplicación utiliza una base de datos H2 embebida que se crea automáticamente en el directorio `data/`. No requiere configuración adicional.

## 📱 Funcionalidades

### Gestión de Eventos

- **Crear eventos** con validaciones específicas por tipo
- **Editar eventos** en estado de planificación
- **Confirmar eventos** con validación de responsables obligatorios
- **Iniciar y finalizar eventos** siguiendo el flujo de estados
- **Vista de calendario** para visualización temporal

### Gestión de Participaciones

- **Inscribir responsables** (organizadores, instructores, curadores)
- **Registrar asistentes** con control de cupos
- **Validaciones de roles** específicas por tipo de evento
- **Gestión de bajas** según el estado del evento

### Interfaz de Usuario

- **Dashboard principal** con estadísticas
- **Vista de calendario** interactiva
- **Formularios dinámicos** para cada tipo de evento
- **Diálogos de búsqueda** para personas y contenido
- **Navegación fluida** entre secciones

## 🎯 Reglas de Negocio Implementadas

### Estados de Eventos
- **Planificación**: Permite agregar/quitar responsables
- **Confirmado**: Permite inscripción de asistentes
- **En Ejecución**: Evento activo, permite inscripcion de asistentes
- **Finalizado**: Evento cerrado, no permite cambios

### Validaciones
- **Fechas**: No se permiten eventos en el pasado
- **Cupos**: Control automático de lugares disponibles
- **Roles**: Validación específica por tipo de evento
- **Responsables**: Roles obligatorios antes de confirmar
