package ar.com.agendamedica.service;

import ar.com.agendamedica.dto.FranjaResponse;
import ar.com.agendamedica.exception.ResourceNotFoundException;
import ar.com.agendamedica.repository.FranjaRepository;
import ar.com.agendamedica.repository.ProfesionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AgendaService {
    private final FranjaRepository franjaRepository;
    private final ProfesionalRepository profesionalRepository;

    public AgendaService(FranjaRepository franjaRepository, ProfesionalRepository profesionalRepository) {
        this.franjaRepository = franjaRepository;
        this.profesionalRepository = profesionalRepository;
    }

    @Transactional(readOnly = true)
    public List<FranjaResponse> agendaDiaria(Long profesionalId, LocalDate fecha) {
        profesionalRepository.findById(profesionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado: " + profesionalId));

        return franjaRepository.findByProfesionalIdAndInicioBetweenOrderByInicioAsc(
                        profesionalId,
                        fecha.atStartOfDay(),
                        fecha.plusDays(1).atStartOfDay().minusNanos(1))
                .stream()
                .map(FranjaResponse::from)
                .toList();
    }
}
