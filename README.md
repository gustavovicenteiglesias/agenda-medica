# Agenda Médica

Sistema web para la gestión de agenda y turnos médicos.

## Stack

- Frontend: React + Vite + TypeScript
- Backend: Spring Boot 3 + Java 21
- Persistencia: Spring Data JPA / Hibernate
- Base de datos: MySQL 8
- Seguridad: Spring Security (JWT pendiente)
- API: REST + OpenAPI/Swagger

## Estado actual

El backend ya cubre el núcleo operativo del MVP:

- CRUD y búsqueda de pacientes con baja lógica.
- Agenda diaria por profesional.
- Plantillas semanales de disponibilidad.
- Generación/materialización idempotente de franjas.
- Creación transaccional de turnos.
- Prevención de doble reserva con bloqueo pesimista.
- Cancelación con liberación de franja.
- Reprogramación con trazabilidad de franja anterior y nueva.
- Registro de eventos del turno.
- Seed de desarrollo mediante perfil `dev`.
- CI para compilar backend y frontend.

## Endpoints principales

```text
GET    /api/pacientes?query=
GET    /api/pacientes/{id}
POST   /api/pacientes
PUT    /api/pacientes/{id}
DELETE /api/pacientes/{id}

GET  /api/agenda?profesionalId=1&fecha=YYYY-MM-DD
POST /api/agenda/plantillas
POST /api/agenda/generar-franjas

POST /api/turnos
GET  /api/turnos/{id}
POST /api/turnos/{id}/cancelacion
POST /api/turnos/{id}/reprogramacion
```

Swagger queda disponible en `/swagger-ui.html` al levantar el backend.

## Desarrollo local

La conexión por defecto espera MySQL en:

```text
jdbc:mysql://localhost:3306/agenda_medica
usuario: root
password: root
```

Se puede modificar con `DB_URL`, `DB_USER` y `DB_PASSWORD`.

Para cargar datos mínimos:

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

El seed crea un profesional, dos pacientes y franjas de prueba.

## Pendiente para cerrar el MVP

- Login JWT y autorización por roles ADMIN, RECEPCION y MEDICO.
- Excepciones/bloqueos completos desde API.
- Estados atendido/ausente.
- Auditoría de operaciones críticas.
- Integración real del frontend React con la API.
- Tests de integración y concurrencia.

> Durante el desarrollo los endpoints permanecen temporalmente abiertos. No es una configuración de producción.

## Alcance

El MVP se concentra en pacientes, disponibilidad, agenda, asignación/cancelación/reprogramación de turnos, excepciones y auditoría mínima. No incluye historia clínica, facturación, WhatsApp ni multi-profesional operativo.
