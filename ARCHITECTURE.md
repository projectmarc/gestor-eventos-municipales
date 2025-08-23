# Arquitectura de Carpetas - Gestor de Eventos Municipales

## 📁 Estructura Recomendada

```
src/main/java/gov/municipio/
├── 📁 Main.java                                 # Punto de entrada de la aplicación
├── 📁 domain/                                   # Capa de dominio (lógica de negocio)
│   ├── 📁 model/                               # Entidades del dominio
│   │   ├── 📄 Persona.java
│   │   ├── 📄 Participacion.java
│   │   ├── 📄 Pelicula.java
│   │   ├── 📁 enums/                          # Enumeraciones del dominio
│   │   │   ├── 📄 EstadoEvento.java
│   │   │   ├── 📄 Rol.java
│   │   │   ├── 📄 ModalidadTaller.java
│   │   │   ├── 📄 TipoEntrada.java
│   │   │   └── 📄 TipoLugar.java
│   │   └── 📁 evento/                         # Jerarquía de eventos
│   │       ├── 📄 Evento.java (abstracta)
│   │       ├── 📄 Feria.java
│   │       ├── 📄 Concierto.java
│   │       ├── 📄 Exposicion.java
│   │       ├── 📄 Taller.java
│   │       └── 📄 CicloDeCine.java
│   ├── 📁 service/                            # Servicios de dominio (lógica de negocio)
│   │   ├── 📄 PersonaService.java
│   │   └── 📄 EventoService.java
│   ├── 📁 exception/                          # ⭐ NUEVO: Excepciones específicas del dominio
│   │   ├── 📄 DomainException.java
│   │   └── 📄 EventoBusinessException.java
│   └── 📁 validator/                          # ⭐ NUEVO: Validadores de dominio
│       └── 📄 PersonaValidator.java
├── 📁 infrastructure/                          # ⭐ NUEVO: Capa de infraestructura
│   ├── 📁 repository/                         # Repositorios (acceso a datos)
│   │   ├── 📄 PersonaRepository.java
│   │   └── 📄 EventoRepository.java
│   └── 📁 config/                            # Configuración de infraestructura
│       └── 📄 PersistenceConfig.java
├── 📁 view/                                   # Capa de presentación
│   ├── 📄 MainController.java
│   └── 📁 dto/                              # ⭐ NUEVO: Data Transfer Objects
│       ├── 📄 EventoDTO.java
│       └── 📄 PersonaDTO.java
└── 📁 util/                                  # Utilidades
    ├── 📄 DataInitializer.java
    └── 📁 constants/                         # ⭐ NUEVO: Constantes de la aplicación
        └── 📄 AppConstants.java

src/main/resources/
├── 📁 gov/municipio/view/
│   └── 📄 main-view.fxml
└── 📁 META-INF/
    └── 📄 persistence.xml
```

## 🏗️ Beneficios de la Nueva Arquitectura

### 1. **Separación de Responsabilidades (SRP)**
- **Domain**: Lógica de negocio pura
- **Infrastructure**: Acceso a datos y configuración
- **View**: Presentación y DTOs

### 2. **Patrón Repository**
- Abstrae el acceso a datos de los servicios
- Facilita testing con mocks
- Centraliza las consultas JPA

### 3. **Manejo de Excepciones**
- Excepciones específicas del dominio
- Mejor control de errores de negocio
- Mensajes más claros para el usuario

### 4. **Validaciones Centralizadas**
- Validadores específicos por entidad
- Reutilización de lógica de validación
- Consistencia en las reglas de negocio

### 5. **DTOs para la Vista**
- Evita exposición de entidades JPA en la UI
- Control sobre qué datos se muestran
- Mejor rendimiento (lazy loading controlado)

## 📋 Próximos Pasos Recomendados

### Inmediatos:
1. **Mover lógica de persistencia** de Services a Repositories
2. **Implementar validadores** en las operaciones de negocio
3. **Crear DTOs** para la comunicación view-service
4. **Usar excepciones específicas** en lugar de IllegalArgumentException

### Mediano plazo:
1. **Factory pattern** para creación de eventos
2. **Interfaces** para servicios y repositorios
3. **Configuración externa** para base de datos
4. **Logging** estructurado
5. **Tests unitarios** por capa

### Largo plazo:
1. **Cache** para consultas frecuentes
2. **Auditoría** de cambios
3. **Eventos de dominio** para notificaciones
4. **API REST** si se necesita integración

## ⚠️ Consideraciones Importantes

- **Migración gradual**: No es necesario refactorizar todo de una vez
- **Compatibilidad**: Mantener funcionamiento actual durante transición
- **Testing**: Agregar tests antes de refactorizar
- **Documentación**: Actualizar README con nueva estructura

## 🎯 Prioridades Sugeridas

1. **Alta**: Repository pattern + PersistenceConfig
2. **Media**: DTOs + Validadores
3. **Baja**: Constantes + Excepciones específicas

Esta estructura sigue principios **SOLID** y **Clean Architecture**, facilitando mantenimiento y escalabilidad.
