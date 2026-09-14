# Agenda Médica

Sistema web para la gestión de agenda y turnos médicos.

## Stack

- Frontend: React + Vite + TypeScript
- Backend: Spring Boot 3 + Java 21
- Persistencia: Spring Data JPA / Hibernate
- Base de datos: MySQL 8
- Seguridad: Spring Security (JWT pendiente)
- API: REST + OpenAPI/Swagger

## Estructura

```text
agenda-medica/
├── frontend/
├── backend/
├── docs/
├── .gitignore
└── README.md
```

## Dominio inicial

El backend contiene las entidades y repositorios base del MVP:

- Usuario
- Paciente
- Profesional
- PlantillaDisponibilidad
- ExcepcionAgenda
- Franja
- Turno
- TurnoEvento
- Auditoria

## Primer flujo vertical implementado

Ya está disponible el circuito inicial:

1. Crear y buscar pacientes.
2. Consultar la agenda diaria de un profesional.
3. Crear un turno sobre una franja libre.
4. Bloquear la franja durante la confirmación con `PESSIMISTIC_WRITE`.
5. Revalidar disponibilidad dentro de la transacción.
6. Marcar la franja como `OCUPADA`.
7. Registrar un `TurnoEvento` de creación.
8. Responder `409 Conflict` si la franja dejó de estar disponible.

Endpoints principales:

```text
GET  /api/pacientes?query=
POST /api/pacientes
GET  /api/agenda?profesionalId=1&fecha=2026-09-16
POST /api/turnos
GET  /api/turnos/{id}
```

Swagger queda disponible en `/swagger-ui.html` al levantar el backend.

> Seguridad: durante esta etapa los endpoints se encuentran abiertos para facilitar las pruebas del flujo inicial. La autenticación JWT y los roles ADMIN, RECEPCION y MEDICO se implementarán antes de cerrar el MVP.

## Próximos pasos

- Generación/materialización de franjas desde la plantilla semanal.
- Cancelación de turnos y liberación de franjas.
- Reprogramación transaccional con historial.
- Login JWT y autorización por roles.
- Integración del frontend React con la agenda real.

## Alcance del MVP

El MVP se concentra en pacientes, disponibilidad, agenda, asignación/cancelación/reprogramación de turnos, excepciones y auditoría mínima. No incluye historia clínica, facturación, WhatsApp ni multi-profesional operativo en esta etapa.
