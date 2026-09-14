# Arquitectura inicial

## Stack

- Frontend: React + Vite + TypeScript
- Backend: Spring Boot 3 + Java 21
- Persistencia: MySQL 8
- ORM: Spring Data JPA / Hibernate
- Seguridad: Spring Security + JWT (JWT pendiente de implementar)
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

## Entidades

- Usuario
- Paciente
- Profesional
- PlantillaDisponibilidad
- ExcepcionAgenda
- Franja
- Turno
- TurnoEvento
- Auditoria

## Flujo vertical implementado

El primer caso de uso operativo implementado es la creación de un turno:

```text
React (futuro)
   |
   v
POST /api/turnos
   |
   v
TurnoController
   |
   v
TurnoService @Transactional
   |---- obtiene Paciente activo
   |---- bloquea Franja con PESSIMISTIC_WRITE
   |---- revalida estado LIBRE y fecha futura
   |---- verifica que no exista turno activo
   |---- crea Turno RESERVADO
   |---- cambia Franja a OCUPADA
   |---- registra TurnoEvento CREACION
   v
MySQL
```

Si la franja fue ocupada antes de confirmar, el servicio devuelve `409 Conflict` y la transacción no crea un segundo turno.

## Criterio de diseño de agenda

En el MVP se materializarán franjas futuras en MySQL para simplificar la consulta de agenda y el control de concurrencia. La siguiente iteración debe generar esas franjas a partir de `PlantillaDisponibilidad`, respetando excepciones y sin alterar turnos ya comprometidos.

## Seguridad

Spring Security ya forma parte del proyecto, pero mientras se construye el primer flujo vertical la API está temporalmente abierta. Esta decisión es sólo de desarrollo. La autenticación JWT y la autorización por roles `ADMIN`, `RECEPCION` y `MEDICO` siguen siendo requisito del MVP.
