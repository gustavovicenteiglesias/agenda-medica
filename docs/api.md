# API REST

Base path: `/api`

## Pacientes

- `GET /api/pacientes?query=`
- `GET /api/pacientes/{id}`
- `POST /api/pacientes`
- `PUT /api/pacientes/{id}`
- `DELETE /api/pacientes/{id}` — baja lógica

## Agenda

- `GET /api/agenda?profesionalId={id}&fecha=YYYY-MM-DD`
- `POST /api/agenda/plantillas`
- `POST /api/agenda/generar-franjas`

Ejemplo de plantilla:

```json
{
  "profesionalId": 1,
  "diaSemana": "MONDAY",
  "horaInicio": "09:00",
  "horaFin": "13:00",
  "duracionMin": 30,
  "vigenciaDesde": "2026-10-01",
  "vigenciaHasta": null
}
```

Ejemplo de generación:

```json
{
  "profesionalId": 1,
  "desde": "2026-10-01",
  "hasta": "2026-11-30"
}
```

La generación es idempotente: no duplica una franja ya existente para el mismo profesional e inicio. Los cierres registrados como excepción se respetan durante la generación.

## Turnos

- `POST /api/turnos`
- `GET /api/turnos/{id}`
- `POST /api/turnos/{id}/cancelacion`
- `POST /api/turnos/{id}/reprogramacion`

Crear turno:

```json
{
  "pacienteId": 1,
  "franjaId": 10,
  "motivoConsulta": "Control"
}
```

Cancelar:

```json
{
  "motivo": "Paciente avisó que no puede asistir"
}
```

Reprogramar:

```json
{
  "franjaId": 18,
  "motivo": "Cambio solicitado por el paciente"
}
```

La creación y la reprogramación bloquean la franja con `PESSIMISTIC_WRITE` y revalidan disponibilidad dentro de la transacción. Si otro usuario tomó la franja, la API responde `409 Conflict`.

## Errores

- `400 Bad Request`: datos inválidos o regla de negocio no cumplida.
- `404 Not Found`: recurso inexistente.
- `409 Conflict`: DNI duplicado, turno no modificable o franja ocupada.

## Seguridad

Spring Security está incorporado, pero los endpoints permanecen temporalmente abiertos para desarrollo. JWT y roles se implementarán antes de cerrar el MVP.
