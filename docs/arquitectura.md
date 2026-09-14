# Arquitectura

## Visión general

```text
React + TypeScript
       |
       | HTTP / JSON
       v
Spring Boot 3 / Java 21
       |
       | JPA / Hibernate
       v
MySQL 8
```

## Backend

Organización inicial:

```text
ar.com.agendamedica
├── domain
│   ├── entity
│   └── enums
├── repository
├── service
├── controller
├── dto
├── security
├── exception
└── config
```

El dominio separa las reglas recurrentes de agenda (`PlantillaDisponibilidad`), las excepciones (`ExcepcionAgenda`), las franjas concretas (`Franja`) y los compromisos asumidos (`Turno`). `TurnoEvento` conserva la trazabilidad específica de cada turno y `Auditoria` registra operaciones críticas generales.

## Concurrencia

La asignación de turnos será transaccional. La franja se recuperará con bloqueo pesimista antes de confirmar la reserva. Esto evita que dos recepcionistas confirmen simultáneamente el mismo horario.

## Frontend

React se encargará de la agenda visual, formularios, búsqueda de pacientes y consumo de la API REST. La lógica de negocio y validación definitiva permanece en el backend.
