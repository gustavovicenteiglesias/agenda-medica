package ar.com.agendamedica.service;

import ar.com.agendamedica.domain.entity.Franja;
import ar.com.agendamedica.domain.entity.Paciente;
import ar.com.agendamedica.domain.entity.Turno;
import ar.com.agendamedica.domain.entity.TurnoEvento;
import ar.com.agendamedica.domain.enums.EstadoFranja;
import ar.com.agendamedica.domain.enums.EstadoTurno;
import ar.com.agendamedica.domain.enums.TipoEventoTurno;
import ar.com.agendamedica.dto.CrearTurnoRequest;
import ar.com.agendamedica.dto.TurnoResponse;
import ar.com.agendamedica.exception.BadRequestException;
import ar.com.agendamedica.exception.ConflictException;
import ar.com.agendamedica.exception.ResourceNotFoundException;
import ar.com.agendamedica.repository.FranjaRepository;
import ar.com.agendamedica.repository.TurnoEventoRepository;
import ar.com.agendamedica.repository.TurnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TurnoService {
    private static final List<EstadoTurno> ESTADOS_ACTIVOS = List.of(EstadoTurno.RESERVADO, EstadoTurno.CONFIRMADO);

    private final TurnoRepository turnoRepository;
    private final TurnoEventoRepository turnoEventoRepository;
    private final FranjaRepository franjaRepository;
    private final PacienteService pacienteService;

    public TurnoService(TurnoRepository turnoRepository,
                        TurnoEventoRepository turnoEventoRepository,
                        FranjaRepository franjaRepository,
                        PacienteService pacienteService) {
        this.turnoRepository = turnoRepository;
        this.turnoEventoRepository = turnoEventoRepository;
        this.franjaRepository = franjaRepository;
        this.pacienteService = pacienteService;
    }

    @Transactional
    public TurnoResponse crear(CrearTurnoRequest request) {
        Paciente paciente = pacienteService.obtenerEntidadActiva(request.pacienteId());
        Franja franja = franjaRepository.findByIdForUpdate(request.franjaId())
                .orElseThrow(() -> new ResourceNotFoundException("Franja no encontrada: " + request.franjaId()));

        if (franja.getInicio().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se puede reservar una franja en el pasado");
        }
        if (franja.getEstado() != EstadoFranja.LIBRE) {
            throw new ConflictException("La franja ya no está disponible");
        }
        if (turnoRepository.existsByFranjaIdAndEstadoIn(franja.getId(), ESTADOS_ACTIVOS)) {
            throw new ConflictException("La franja ya posee un turno activo");
        }

        Turno turno = new Turno();
        turno.setPaciente(paciente);
        turno.setProfesional(franja.getProfesional());
        turno.setFranja(franja);
        turno.setInicio(franja.getInicio());
        turno.setFin(franja.getFin());
        turno.setEstado(EstadoTurno.RESERVADO);
        turno.setMotivoConsulta(normalize(request.motivoConsulta()));
        turno = turnoRepository.save(turno);

        franja.setEstado(EstadoFranja.OCUPADA);
        franjaRepository.save(franja);

        TurnoEvento evento = new TurnoEvento();
        evento.setTurno(turno);
        evento.setTipo(TipoEventoTurno.CREACION);
        evento.setFranjaNuevaId(franja.getId());
        evento.setDetalle("Turno creado");
        turnoEventoRepository.save(evento);

        return TurnoResponse.from(turno);
    }

    @Transactional(readOnly = true)
    public TurnoResponse obtener(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado: " + id));
        return TurnoResponse.from(turno);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
