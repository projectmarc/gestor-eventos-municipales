# Persistencia de Eventos - Implementación Completa

## Resumen de Cambios Realizados

Se ha implementado exitosamente la persistencia para los eventos del sistema. Ahora los eventos se pueden crear desde la interfaz y se guardan automáticamente en la base de datos.

## Funcionalidades Implementadas

### 1. **Anotaciones JPA agregadas a las entidades del dominio:**
   - `Evento` (clase base abstracta)
   - `Concierto`, `Taller`, `Exposicion`, `Feria`, `CicloDeCine` (subclases)
   - `Participacion` (entidad de relación)

### 2. **Campo de Cupo Máximo en la interfaz:**
   - Campo `txtCupoMaximo` agregado al formulario
   - Se habilita/deshabilita automáticamente según el checkbox "Requiere inscripción"
   - Validación obligatoria cuando se requiere inscripción

### 3. **Repositorio y Servicio de Eventos:**
   - `EventoRepository` (interfaz)
   - `EventoRepositoryImpl` (implementación JPA)
   - `EventoService` (servicio de aplicación)

### 4. **Controlador actualizado:**
   - `CrearEventoController` conectado con la persistencia
   - Validaciones completas del formulario
   - Mensajes de confirmación al crear eventos

## Cómo Probar la Funcionalidad

### 1. **Ejecutar la aplicación:**
```bash
mvn javafx:run
```

### 2. **Crear un nuevo evento:**
   1. Navegar a "Eventos" desde el menú
   2. Hacer clic en "Crear evento"
   3. Completar el formulario:
      - **Nombre**: Cualquier nombre (obligatorio)
      - **Tipo**: Seleccionar entre Feria, Concierto, Exposición, Taller, Ciclo de cine
      - **Fecha de inicio**: Fecha futura (obligatorio)
      - **Duración**: Número de horas (obligatorio)
      - **Requiere inscripción**: Marcar si se necesita inscripción
      - **Cupo máximo**: Solo si se marcó "Requiere inscripción"
   4. Hacer clic en "Crear"

### 3. **Comportamiento especial por tipo:**
   - **Taller**: Automáticamente requiere inscripción (checkbox deshabilitado)
   - **Otros tipos**: Inscripción opcional

### 4. **Verificar en base de datos:**
Los eventos se guardan en H2 database en `./data/municipio-db.mv.db`

## Estructura de Tablas Creadas

```sql
-- Tabla principal de eventos
eventos (id, nombre, fechaInicio, duracionEstimada, estado, requiereInscripcion, cupoMaximo)

-- Tablas específicas por tipo (herencia JOINED)
conciertos (id)
talleres (id, modalidad)
exposiciones (id, disciplinaArtistica)
ferias (id, cantidadStands, tipoLugar)
ciclos_de_cine (id, incluyeCharlasPosteriores)

-- Tabla de participaciones
participaciones (id, persona_id, evento_id, rol)

-- Tablas de relación muchos a muchos
concierto_artista_banda (concierto_id, artista_banda_id)
ciclo_cine_pelicula (ciclo_cine_id, pelicula_id, orden)
```

## Estados de los Eventos

Los eventos se crean en estado `PLANIFICACION` por defecto. Los estados disponibles son:
- `PLANIFICACION`: Estado inicial
- `CONFIRMADO`: Evento confirmado con responsables asignados
- `EN_EJECUCION`: Evento en curso
- `FINALIZADO`: Evento terminado

## Datos por Defecto Utilizados

Para poder crear eventos básicos sin datos específicos, se utilizan valores por defecto:

- **Feria**: 1 stand, tipo "TECHADO"
- **Concierto**: Lista vacía de artistas (se pueden agregar después)
- **Exposición**: Disciplina "Arte General"
- **Taller**: Modalidad "PRESENCIAL"
- **Ciclo de cine**: Lista vacía de películas, sin charlas posteriores

## Próximos Pasos

Los datos específicos de cada tipo de evento (artistas, películas, etc.) se pueden agregar posteriormente mediante funcionalidades adicionales que se pueden implementar en el futuro.

## Notas Técnicas

- Se utiliza herencia JOINED en JPA para mantener la estructura de clases del dominio
- Las validaciones del dominio se mantienen intactas
- El patrón Repository-Service se mantiene consistente con el resto del proyecto
- La inyección de dependencias se hace manualmente a través del ServiceContainer
