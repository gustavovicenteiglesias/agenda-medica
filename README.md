# Agenda Médica

Sistema web para la gestión de agenda y turnos médicos.

## Stack

- Frontend: React + Vite + TypeScript
- Backend: Spring Boot 3 + Java 21
- Persistencia: Spring Data JPA / Hibernate
- Base de datos: MySQL 8
- Seguridad: Spring Security
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

## Dominio inicial implementado

El backend ya contiene las entidades y repositorios base del MVP:

- Usuario
- Paciente
- Profesional
- PlantillaDisponibilidad
- ExcepcionAgenda
- Franja
- Turno
- TurnoEvento
- Auditoria

También se agregó bloqueo pesimista de `Franja` para preparar la prevención de doble reserva en el servicio transaccional de turnos.

## Próximo paso

Implementar el primer flujo vertical completo:

1. ABM/búsqueda de pacientes.
2. Consulta de franjas disponibles.
3. Creación transaccional de turno.
4. Respuesta HTTP 409 ante intento de doble reserva.
5. Exposición de endpoints REST para integrar el frontend.

## Alcance del MVP

El MVP se concentra en pacientes, disponibilidad, agenda, asignación/cancelación/reprogramación de turnos, excepciones y auditoría mínima. No incluye historia clínica, facturación, WhatsApp ni multi-profesional operativo en esta etapa.
