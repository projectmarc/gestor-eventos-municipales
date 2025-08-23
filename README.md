# Gestor de Eventos Municipales

Sistema de gestión para eventos culturales municipales desarrollado en Java con JavaFX y JPA.

## Características

### Funcionalidades Principales
- **Gestión de Personas**: Alta, modificación y consulta de personas
- **Gestión de Eventos**: Crear y administrar diferentes tipos de eventos
- **Gestión de Participaciones**: Inscribir personas en eventos según roles
- **Persistencia de Datos**: Base de datos H2 con JPA/Hibernate
- **Interfaz Gráfica**: JavaFX con diseño intuitivo

### Tipos de Eventos
- **Feria**: Con cantidad de stands y tipo de locación
- **Concierto**: Con lista de artistas, tipo de entrada y capacidad
- **Exposición**: Con disciplina artística y curador
- **Taller**: Con modalidad, cupo máximo e instructor
- **Ciclo de Cine**: Con lista de películas y charlas

### Roles de Participación
- **Organizador**: Responsable de la organización del evento
- **Artista**: Participante artístico en conciertos
- **Curador**: Responsable de exposiciones
- **Instructor**: Responsable de talleres
- **Asistente**: Participante general en eventos

### Estados de Eventos
- **Planificación**: Estado inicial
- **Confirmado**: Evento confirmado, permite inscripciones
- **En Ejecución**: Evento en curso
- **Finalizado**: Evento terminado

## Tecnologías Utilizadas
- **Java 21**: Lenguaje de programación
- **JavaFX 23**: Interfaz gráfica de usuario
- **JPA/Hibernate**: Persistencia de datos
- **H2 Database**: Base de datos embebida
- **Maven**: Gestión de dependencias

## Estructura del Proyecto

```
src/main/java/gov/municipio/
├── Main.java                          # Clase principal de la aplicación
├── domain/
│   ├── model/                         # Entidades del dominio
│   │   ├── Persona.java              # Entidad persona
│   │   ├── Participacion.java        # Relación persona-evento
│   │   ├── Pelicula.java            # Entidad película
│   │   ├── enums/                    # Enumeraciones
│   │   │   ├── EstadoEvento.java
│   │   │   ├── Rol.java
│   │   │   ├── ModalidadTaller.java
│   │   │   ├── TipoEntrada.java
│   │   │   └── TipoLugar.java
│   │   └── evento/                   # Jerarquía de eventos
│   │       ├── Evento.java          # Clase abstracta base
│   │       ├── Feria.java
│   │       ├── Concierto.java
│   │       ├── Exposicion.java
│   │       ├── Taller.java
│   │       └── CicloDeCine.java
│   └── service/                      # Servicios de negocio
│       ├── PersonaService.java       # Lógica de negocio de personas
│       └── EventoService.java        # Lógica de negocio de eventos
├── view/                            # Interfaz gráfica
│   └── MainController.java          # Controlador principal
└── util/
    └── DataInitializer.java         # Inicializador de datos de prueba
```

## Requisitos

- Java 21 o superior
- Maven 3.8 o superior

## Instalación y Ejecución

### 1. Clonar el repositorio
```bash
git clone <url-del-repositorio>
cd gestor-eventos-municipales
```

### 2. Compilar el proyecto
```bash
mvn clean compile
```

### 3. Ejecutar la aplicación
```bash
mvn clean javafx:run
```

## Características Técnicas

### Persistencia
- Configuración JPA en `persistence.xml`
- Base de datos H2 embebida (archivo `data/eventos_municipales.h2.db`)
- Estrategia de herencia `JOINED` para eventos
- Relaciones JPA configuradas correctamente

### Validaciones
- Validación de roles por tipo de evento
- Validación de cupo máximo en talleres y conciertos
- Validación de estados para inscripciones
- Validación de DNI único para personas

### Interfaz de Usuario
- Diseño con pestañas para diferentes funcionalidades
- Formularios intuitivos para registro
- Tablas para visualización de datos
- Mensajes informativos y de error

## Datos de Prueba

La aplicación incluye datos de prueba que se cargan automáticamente:
- 8 personas con diferentes roles
- 5 eventos de diferentes tipos
- Participaciones configuradas
- Estados variados para demostrar funcionalidades

## Funcionalidades Implementadas

### ✅ Requerimientos Funcionales
- [x] Gestionar alta, modificación y baja de eventos y personas
- [x] Asociar personas a eventos según rol y tipo de evento
- [x] Registrar participantes en eventos según tipo
- [x] Visualizar calendario de eventos
- [x] Cambiar el estado de un evento
- [x] Listar participantes por evento
- [x] Validar cupo máximo en eventos que lo requieran

### ✅ Requerimientos Técnicos
- [x] Aplicación JavaFX con interfaz gráfica
- [x] Persistencia con JPA
- [x] Diseño orientado a objetos con herencia, composición y relaciones
- [x] Clases abstractas e interfaces
- [x] Modelo rico con lógica de negocio

## Mejoras Implementadas

1. **Persistencia JPA**: Base de datos H2 con configuración completa
2. **Validaciones mejoradas**: Cupo máximo, estados, roles específicos
3. **Modelo enriquecido**: Atributos faltantes en eventos (curador, instructor, artistas)
4. **Interfaz completa**: Formularios y tablas funcionales
5. **Servicios de negocio**: Separación de responsabilidades
6. **Datos de prueba**: Sistema inicializado con ejemplos
7. **Manejo de errores**: Validaciones y mensajes informativos