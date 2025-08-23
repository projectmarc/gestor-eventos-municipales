# PROJECT REQUIREMENTS

## Contexto
El municipio desea una aplicación para gestionar los distintos eventos culturales que organiza durante el año (ej. ferias, exposiciones, conciertos, talleres, ciclos de cine).  
La aplicación debe permitir crear, modificar, eliminar y consultar eventos, personas involucradas y asistentes.

---

## Entidades Principales

### Evento (abstracto)
- **Atributos comunes:**
  - Nombre
  - Fecha de inicio
  - Duración estimada
  - Estado (`Planificación`, `Confirmado`, `En ejecución`, `Finalizado`)
  - Responsables de organización (una o varias personas)

- **Subtipos:**
  - **Feria**
    - Cantidad de stands
    - Tipo (al aire libre / techada)
  - **Concierto**
    - Lista de artistas/bandas
    - Tipo de entrada (gratuita / paga)
  - **Exposición**
    - Tipo de arte (fotografía, pintura, etc.)
    - Curador
  - **Taller**
    - Cupo máximo
    - Instructor
    - Modalidad (presencial / virtual)
  - **Ciclo de Cine**
    - Lista de películas
    - Orden de proyección
    - Charlas posteriores (sí / no)

---

### Persona
- **Datos:**
  - Nombre completo
  - DNI
  - Teléfono
  - Correo electrónico

- **Roles (definidos al asociar con evento, no al dar de alta):**
  - Organizador
  - Artista
  - Curador
  - Instructor
  - Asistente (rol separado)

---

## Reglas y Restricciones
- Una persona se da de alta sin rol; el rol se asigna al vincularla con un evento.
- Restricción de roles:
  - Ejemplo: un curador solo puede asociarse a una Exposición, no a un Ciclo de Cine.
- Eventos abiertos: sin inscripción.
- Eventos con inscripción:
  - No permiten inscripciones si:
    - El evento no está **Confirmado**.
    - El evento está **Finalizado**.
  - Validar **cupo máximo** donde corresponda (ej: talleres).

---

## Requerimientos Funcionales
1. Gestionar alta, modificación y baja de eventos y personas.
2. Asociar personas a eventos según rol y tipo de evento.
3. Registrar participantes en eventos según tipo.
4. Visualizar calendario de eventos.
5. Cambiar el estado de un evento.
6. Listar participantes por evento.
7. Validar cupo máximo en eventos que lo requieran.

---

## Requerimientos Técnicos
- Implementar una **aplicación JavaFX** con interfaz gráfica.
- Persistencia con **JPA**:
  - Usar entidades y relaciones apropiadas.
- Diseño orientado a objetos:
  - Aplicar **herencia**, **composición** y **relaciones**.
  - Usar **clases abstractas** e **interfaces** donde corresponda.
- Modelo rico: las clases deben contener lógica de negocio, no solo datos.