package ar.com.agendamedica.service;

import ar.com.agendamedica.domain.entity.Auditoria;
import ar.com.agendamedica.dto.AuditoriaResponse;
import ar.com.agendamedica.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditoriaService {
    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public void registrar(String accion, String entidad, Long entidadId, String metadata) {
        Auditoria auditoria = new Auditoria();
        auditoria.setAccion(accion);
        auditoria.setEntidad(entidad);
        auditoria.setEntidadId(entidadId);
        auditoria.setMetadata(metadata);
        auditoriaRepository.save(auditoria);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> ultimas() {
        return auditoriaRepository.findTop100ByOrderByFechaDesc()
                .stream()
                .map(AuditoriaResponse::from)
                .toList();
    }
}
