package ar.com.agendamedica.service;

import ar.com.agendamedica.domain.entity.ExcepcionAgenda;
import ar.com.agendamedica.domain.entity.Franja;
import ar.com.agendamedica.domain.entity.PlantillaDisponibilidad;
import ar.com.agendamedica.domain.entity.Profesional;
import ar.com.agendamedica.domain.enums.EstadoFranja;
import ar.com.agendamedica.domain.enums.TipoExcepcionAgenda;
import ar.com.agendamedica.dto.*;
import ar.com.agendamedica.exception.BadRequestException;
import ar.com.agendamedica.exception.ResourceNotFoundException;
import ar.com.agendamedica.repository.ExcepcionAgendaRepository;
import ar.com.agendamedica.repository.FranjaRepository;
import ar.com.agendamedica.repository.PlantillaDisponibilidadRepository;
import ar.com.agendamedica.repository.ProfesionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AgendaService {
    private final FranjaRepository franjaRepository;
    private final ProfesionalRepository profesionalRepository;
    private final PlantillaDisponibilidadRepository plantillaRepository;
    private final ExcepcionAgendaRepository excepcionRepository;

    public AgendaService(FranjaRepository franjaRepository,
                         ProfesionalRepository profesionalRepository,
                         PlantillaDisponibilidadRepository plantillaRepository,
                         ExcepcionAgendaRepository excepcionRepository) {
        this.franjaRepository = franjaRepository;
        this.profesionalRepository = profesionalRepository;
        this.plantillaRepository = plantillaRepository;
        this.excepcionRepository = excepcionRepository;
    }

    @Transactional(readOnly = true)
    public List<FranjaResponse> agendaDiaria(Long profesionalId, LocalDate fecha) {
        obtenerProfesional(profesionalId);

        return franjaRepository.findByProfesionalIdAndInicioBetweenOrderByInicioAsc(
                        profesionalId,
                        fecha.atStartOfDay(),
                        fecha.plusDays(1).atStartOfDay().minusNanos(1))
                .stream()
                .map(FranjaResponse::from)
                .toList();
    }

    @Transactional
    public PlantillaDisponibilidadResponse crearPlantilla(PlantillaDisponibilidadRequest request) {
        Profesional profesional = obtenerProfesional(request.profesionalId());

        if (!request.horaFin().isAfter(request.horaInicio())) {
            throw new BadRequestException("La hora de fin debe ser posterior a la hora de inicio");
        }
        if (request.vigenciaHasta() != null && request.vigenciaHasta().isBefore(request.vigenciaDesde())) {
            throw new BadRequestException("La vigencia hasta no puede ser anterior a la vigencia desde");
        }

        PlantillaDisponibilidad plantilla = new PlantillaDisponibilidad();
        plantilla.setProfesional(profesional);
        plantilla.setDiaSemana(request.diaSemana());
        plantilla.setHoraInicio(request.horaInicio());
        plantilla.setHoraFin(request.horaFin());
        plantilla.setDuracionMin(request.duracionMin());
        plantilla.setVigenciaDesde(request.vigenciaDesde());
        plantilla.setVigenciaHasta(request.vigenciaHasta());

        return PlantillaDisponibilidadResponse.from(plantillaRepository.save(plantilla));
    }

    @Transactional
    public GenerarFranjasResponse generarFranjas(GenerarFranjasRequest request) {
        Profesional profesional = obtenerProfesional(request.profesionalId());

        if (request.hasta().isBefore(request.desde())) {
            throw new BadRequestException("La fecha hasta no puede ser anterior a desde");
        }
        if (ChronoUnit.DAYS.between(request.desde(), request.hasta()) > 90) {
            throw new BadRequestException("Se pueden generar como máximo 90 días por operación");
        }

        List<PlantillaDisponibilidad> plantillas =
                plantillaRepository.findByProfesionalIdAndVigenciaDesdeLessThanEqual(
                        profesional.getId(), request.hasta());

        List<ExcepcionAgenda> excepciones =
                excepcionRepository.findByProfesionalIdAndFechaBetweenOrderByFechaAsc(
                        profesional.getId(), request.desde(), request.hasta());

        int creadas = 0;
        int existentes = 0;
        int omitidasPorCierre = 0;

        for (LocalDate fecha = request.desde(); !fecha.isAfter(request.hasta()); fecha = fecha.plusDays(1)) {
            final LocalDate fechaActual = fecha;

            for (PlantillaDisponibilidad plantilla : plantillas) {
                if (plantilla.getDiaSemana() != fechaActual.getDayOfWeek()) {
                    continue;
                }
                if (fechaActual.isBefore(plantilla.getVigenciaDesde())) {
                    continue;
                }
                if (plantilla.getVigenciaHasta() != null && fechaActual.isAfter(plantilla.getVigenciaHasta())) {
                    continue;
                }

                LocalTime hora = plantilla.getHoraInicio();
                while (!hora.plusMinutes(plantilla.getDuracionMin()).isAfter(plantilla.getHoraFin())) {
                    LocalDateTime inicio = LocalDateTime.of(fechaActual, hora);
                    LocalDateTime fin = inicio.plusMinutes(plantilla.getDuracionMin());

                    if (estaCerrada(excepciones, fechaActual, hora, fin.toLocalTime())) {
                        omitidasPorCierre++;
                    } else if (franjaRepository.existsByProfesionalIdAndInicio(profesional.getId(), inicio)) {
                        existentes++;
                    } else {
                        Franja franja = new Franja();
                        franja.setProfesional(profesional);
                        franja.setInicio(inicio);
                        franja.setFin(fin);
                        franja.setEstado(EstadoFranja.LIBRE);
                        franja.setOrigen("PLANTILLA");
                        franjaRepository.save(franja);
                        creadas++;
                    }

                    hora = hora.plusMinutes(plantilla.getDuracionMin());
                }
            }
        }

        return new GenerarFranjasResponse(creadas, existentes, omitidasPorCierre);
    }

    private boolean estaCerrada(List<ExcepcionAgenda> excepciones,
                                LocalDate fecha,
                                LocalTime inicio,
                                LocalTime fin) {
        return excepciones.stream()
                .filter(e -> e.getTipo() == TipoExcepcionAgenda.CIERRE)
                .filter(e -> e.getFecha().equals(fecha))
                .anyMatch(e -> {
                    if (e.getHoraInicio() == null || e.getHoraFin() == null) {
                        return true;
                    }
                    return inicio.isBefore(e.getHoraFin()) && fin.isAfter(e.getHoraInicio());
                });
    }

    private Profesional obtenerProfesional(Long id) {
        return profesionalRepository.findById(id)
                .filter(Profesional::isActivo)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional activo no encontrado: " + id));
    }
}
