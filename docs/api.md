# API REST inicial

Base path: `/api`

## Endpoints implementados

### Pacientes

- `GET /api/pacientes?query=`: busca por DNI exacto o por apellido.
- `POST /api/pacientes`: crea un paciente. Devuelve `201 Created`.

### Agenda

- `GET /api/agenda?profesionalId={id}&fecha=YYYY-MM-DD`: devuelve las franjas del profesional para ese día.

### Turnos

- `POST /api/turnos`: crea un turno sobre una franja libre. Devuelve `201 Created`.
- `GET /api/turnos/{id}`: consulta un turno.

La creación de turno bloquea la fila de la franja con `PESSIMISTIC_WRITE`, vuelve a validar su estado dentro de la transacción y responde `409 Conflict` si dejó de estar disponible. También marca la franja como `OCUPADA` y crea un `TurnoEvento` de tipo `CREACION`.

### Ejemplo de creación de turno

```json
{
  "pacienteId": 1,
  "franjaId": 10,
  "motivoConsulta": "Control"
}
```

## Manejo de errores implementado

- `400 Bad Request`: datos inválidos o intento de reservar una franja pasada.
- `404 Not Found`: paciente, profesional, franja o turno inexistente.
- `409 Conflict`: DNI duplicado, paciente inactivo o franja no disponible.

Las respuestas de error usan un cuerpo JSON uniforme con timestamp, status, mensaje, path y errores de validación cuando corresponde.

## Endpoints previstos para las siguientes iteraciones

- `POST /api/auth/login`
- `GET /api/pacientes/{id}`
- `PUT /api/pacientes/{id}`
- `DELETE /api/pacientes/{id}` (baja lógica)
- `GET /api/agenda/disponibles?desde=&hasta=`
- `POST /api/agenda/excepciones`
- `POST /api/agenda/bloqueos`
- `POST /api/turnos/{id}/cancelacion`
- `POST /api/turnos/{id}/reprogramacion`

## Seguridad

La configuración actual deja los endpoints abiertos para facilitar la construcción y prueba del flujo vertical inicial. Esto es temporal. Antes de considerar cerrado el MVP se implementará autenticación y autorización con Spring Security/JWT y roles `ADMIN`, `RECEPCION` y `MEDICO`.
