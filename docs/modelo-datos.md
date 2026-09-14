# Modelo de datos inicial

El MVP separa disponibilidad de turnos comprometidos.

## Entidades

- `Usuario`: credenciales, rol y estado activo.
- `Paciente`: datos mínimos de identificación y contacto; baja lógica mediante `activo`.
- `Profesional`: médico asociado a la agenda.
- `PlantillaDisponibilidad`: regla semanal recurrente con vigencia.
- `ExcepcionAgenda`: apertura o cierre excepcional para una fecha/rango horario.
- `Franja`: slot materializado reservable. Estados: `LIBRE`, `OCUPADA`, `BLOQUEADA`.
- `Turno`: compromiso entre paciente, profesional y franja.
- `TurnoEvento`: historial de creación, cancelación, reprogramación, atención y ausencia.
- `Auditoria`: trazabilidad general de acciones críticas.

## Relaciones principales

```text
Profesional 1 --- N PlantillaDisponibilidad
Profesional 1 --- N ExcepcionAgenda
Profesional 1 --- N Franja
Profesional 1 --- N Turno
Paciente    1 --- N Turno
Franja      1 --- N Turno (histórico; sólo uno puede estar activo)
Turno       1 --- N TurnoEvento
Usuario     1 --- N TurnoEvento
Usuario     1 --- N Auditoria
```

## Concurrencia de reserva

La reserva no se resuelve sólo leyendo el estado de la franja. `FranjaRepository` incluye `findByIdForUpdate(...)` con bloqueo pesimista. El servicio de turnos deberá ejecutar la validación y creación dentro de una transacción: bloquear la franja, verificar que siga `LIBRE`, comprobar que no exista un turno activo y recién entonces crear el turno y pasar la franja a `OCUPADA`.

Si dos usuarios intentan reservar la misma franja, sólo uno debe confirmar; el segundo recibirá un conflicto HTTP 409.

## Índices iniciales

Se agregaron índices para búsquedas frecuentes por DNI/apellido, profesional+fecha de franja, profesional+inicio de turno y auditoría por entidad/fecha.
