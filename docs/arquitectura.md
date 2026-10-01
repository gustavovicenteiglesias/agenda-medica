# Arquitectura

## Stack

- Frontend: React + Vite + TypeScript
- Backend: Spring Boot 3 + Java 21
- Persistencia: MySQL 8
- ORM: Spring Data JPA / Hibernate
- Seguridad: Spring Security; JWT pendiente
- API: REST + OpenAPI/Swagger

## Núcleo implementado

```text
Paciente
   |
   v
Turno ----> TurnoEvento
   |
   v
Franja <---- PlantillaDisponibilidad
   ^
   |
ExcepcionAgenda
```

Las franjas se materializan en MySQL. Una plantilla recurrente genera slots futuros y el proceso es idempotente.

## Reserva

```text
POST /api/turnos
      |
      v
TurnoService @Transactional
      |
      +-- bloquea Franja con PESSIMISTIC_WRITE
      +-- valida que sea futura y LIBRE
      +-- verifica que no exista turno activo
      +-- crea Turno RESERVADO
      +-- cambia Franja a OCUPADA
      +-- registra TurnoEvento CREACION
```

## Cancelación

La cancelación cambia el turno a `CANCELADO`, libera la franja y registra un evento de cancelación con motivo.

## Reprogramación

La reprogramación bloquea la franja actual y la nueva, valida la nueva disponibilidad, libera la anterior, ocupa la nueva y registra `franjaAnteriorId` y `franjaNuevaId` en `TurnoEvento`.

## Generación de agenda

`AgendaService` toma las plantillas vigentes dentro de un rango de hasta 90 días y genera franjas por duración. No duplica horarios existentes y omite franjas alcanzadas por excepciones de tipo `CIERRE`.

## Seguridad

La API continúa abierta sólo durante desarrollo. Antes del cierre del MVP se deben implementar autenticación JWT y autorización por roles `ADMIN`, `RECEPCION` y `MEDICO`.

## CI

GitHub Actions compila el backend con Java 21/Maven y ejecuta el build del frontend con Node 20.
