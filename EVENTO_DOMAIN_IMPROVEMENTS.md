# Mejoras del Modelo de Dominio Rico - Clase Evento

## Resumen de Cambios

La clase `Evento` ha sido transformada de un modelo anémico a un **modelo de dominio rico** que encapsula reglas de negocio complejas y mantiene la integridad de los datos.

## 🚀 Nuevas Características

### 1. Validaciones Robustas en la Creación
- **Nombre**: No puede ser nulo, vacío o exceder 100 caracteres
- **Fecha**: No puede ser nula ni estar en el pasado
- **Duración**: Debe ser positiva y no exceder 240 horas (10 días)
- **Datos específicos**: Cada subclase valida sus atributos particulares

### 2. Transiciones de Estado Controladas
- **Estados válidos**: `PLANIFICACION` → `CONFIRMADO` → `EN_EJECUCION` → `FINALIZADO`
- **Transiciones bloqueadas**: No se puede saltar estados ni retroceder desde `FINALIZADO`
- **Excepción específica**: `EstadoEventoInvalidoException` para transiciones inválidas

### 3. Sistema de Inscripciones Inteligente
- **Validación de estado**: Solo eventos `CONFIRMADO` permiten inscripciones
- **Control de roles**: Cada tipo de evento define roles válidos
- **Cupo máximo**: Prevención automática de sobrecupo
- **Inscripciones duplicadas**: Bloquea automáticamente personas ya inscritas

### 4. Excepciones Específicas del Dominio
- `EventoException`: Clase base para excepciones del dominio
- `EstadoEventoInvalidoException`: Transiciones de estado inválidas
- `InscripcionNoPermitidaException`: Inscripciones en estados incorrectos
- `RolInvalidoParaEventoException`: Roles no válidos para el tipo de evento
- `CupoMaximoSuperadoException`: Cupo agotado
- `PersonaYaInscritaException`: Inscripciones duplicadas

### 5. Métodos de Consulta Útiles
- `estaActivo()`: Evento confirmado o en ejecución
- `estaCerrado()`: Evento finalizado
- `estaEnPlanificacion()`: Evento en planificación
- `hayCupoDisponible()`: Verifica disponibilidad
- `getLugaresDisponibles()`: Cantidad exacta de lugares libres
- `estaPersonaInscrita()`: Verifica inscripción de una persona
- `buscarParticipacion()`: Encuentra la participación específica

### 6. Separación de Responsabilidades por Tipo
- **Taller**: Cupo máximo específico, modalidad presencial/virtual
- **Concierto**: Capacidad máxima, validación de artistas
- **Exposición**: Disciplina artística, roles específicos (curador)
- **Feria**: Cantidad de stands, tipo de lugar
- **Ciclo de Cine**: Lista de películas, validaciones específicas

## 🛡️ Validaciones Implementadas

### Nivel Base (Evento)
```java
// Validaciones comunes a todos los eventos
- Nombre no vacío (máx 100 caracteres)
- Fecha no en el pasado
- Duración positiva (máx 240 horas)
```

### Nivel Específico (Subclases)
```java
// Taller
- Modalidad no nula
- Cupo entre 1 y 50 participantes

// Concierto  
- Artista principal no vacío
- Género musical no vacío
- Capacidad entre 1 y 100,000

// Exposición
- Disciplina artística no vacía (máx 50 caracteres)

// Feria
- Cantidad de stands entre 1 y 500
- Tipo de lugar no nulo

// Ciclo de Cine
- Al menos 1 película (máx 20)
- Orden de proyección no vacío
- Películas válidas (no nulas)
```

## 🔧 Extensibilidad

### Hooks para Subclases
```java
protected void validarDatosEspecificos() {
    // Las subclases pueden sobrescribir para agregar validaciones
}

protected int getCupoMaximo() {
    // Las subclases pueden definir cupos específicos
    return Integer.MAX_VALUE; // Sin límite por defecto
}

public abstract boolean validarRol(Rol rol);
// Cada subclase define sus roles válidos
```

### Ejemplo de Extensión
```java
public class ConferenciaAcademica extends Evento {
    private String areaTematica;
    private int creditosAcademicos;
    
    @Override
    public boolean validarRol(Rol rol) {
        return rol == Rol.INSTRUCTOR || 
               rol == Rol.ASISTENTE || 
               rol == Rol.ORGANIZADOR;
    }
    
    @Override
    protected void validarDatosEspecificos() {
        super.validarDatosEspecificos();
        // Validaciones específicas de conferencia
    }
}
```

## 📊 Ejemplo de Uso

```java
// Crear evento con validaciones automáticas
Taller taller = new Taller(
    "Taller de Java Avanzado",
    LocalDate.now().plusDays(7),
    8, // 8 horas
    true, // Requiere inscripción
    ModalidadTaller.PRESENCIAL,
    25 // Cupo máximo
);

// Confirmar evento para permitir inscripciones
taller.cambiarEstado(EstadoEvento.CONFIRMADO);

// Inscribir personas con validaciones automáticas
Participacion participacion = taller.inscribirPersona(instructor, Rol.INSTRUCTOR);

// Consultas útiles
boolean hayLugar = taller.hayCupoDisponible();
int lugares = taller.getLugaresDisponibles();
List<Persona> instructores = taller.getPersonasPorRol(Rol.INSTRUCTOR);
boolean activo = taller.estaActivo();
```

## 🧪 Pruebas Incluidas

La clase `EventoDomainTest` demuestra:
- Validaciones en la creación
- Transiciones de estado
- Sistema de inscripciones
- Validación de roles por tipo
- Métodos de consulta
- Estados del evento

Ejecutar con: `mvn exec:java -Dexec.mainClass="gov.municipio.dev.EventoDomainTest"`

## 🎯 Beneficios Obtenidos

1. **Integridad de Datos**: Imposible crear eventos en estados inconsistentes
2. **Reglas de Negocio Centralizadas**: Lógica en el dominio, no dispersa
3. **Reutilización**: Comportamientos comunes en la clase base
4. **Extensibilidad**: Fácil agregar nuevos tipos de eventos
5. **Mantenibilidad**: Cambios en reglas solo afectan una clase
6. **Testabilidad**: Lógica aislada y fácil de probar
7. **Expresividad**: El código refleja claramente las reglas del negocio
