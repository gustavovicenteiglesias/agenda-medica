# API REST inicial

Base path: `/api`

## Autenticación

- `POST /api/auth/login`

## Pacientes

- `GET /api/pacientes?query=`
- `GET /api/pacientes/{id}`
- `POST /api/pacientes`
- `PUT /api/pacientes/{id}`
- `DELETE /api/pacientes/{id}` (baja lógica)

## Agenda

- `GET /api/agenda?fecha=YYYY-MM-DD`
- `GET /api/agenda/disponibles?desde=&hasta=`
- `POST /api/agenda/excepciones`
- `POST /api/agenda/bloqueos`

## Turnos

- `POST /api/turnos`
- `GET /api/turnos/{id}`
- `POST /api/turnos/{id}/cancelacion`
- `POST /api/turnos/{id}/reprogramacion`

La creación/reprogramación deberá revalidar la franja dentro de una transacción. Si la franja ya fue ocupada, la API responderá `409 Conflict`.
