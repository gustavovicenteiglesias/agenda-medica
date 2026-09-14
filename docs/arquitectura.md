# Arquitectura inicial

## Stack

- Frontend: React + Vite + TypeScript
- Backend: Spring Boot 3 + Java 21
- Persistencia: MySQL 8
- ORM: Spring Data JPA / Hibernate
- Seguridad: Spring Security + JWT
- API: REST + OpenAPI/Swagger

## Módulos del MVP

1. Autenticación y usuarios.
2. Pacientes.
3. Configuración de disponibilidad semanal.
4. Excepciones y bloqueos de agenda.
5. Franjas horarias.
6. Turnos: creación, cancelación y reprogramación.
7. Estados de atención: reservado, cancelado, atendido y ausente.
8. Auditoría mínima.

## Entidades previstas

- Usuario
- Paciente
- Profesional
- PlantillaDisponibilidad
- ExcepcionAgenda
- Franja
- Turno
- TurnoEvento
- Auditoria

## Criterio de diseño

En el MVP se materializarán franjas futuras en MySQL para simplificar la consulta de agenda y el control de concurrencia. La reserva de una franja se confirmará en una transacción y deberá impedir doble asignación.
