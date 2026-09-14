package ar.com.agendamedica.controller;

import ar.com.agendamedica.dto.PacienteRequest;
import ar.com.agendamedica.dto.PacienteResponse;
import ar.com.agendamedica.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {
    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public List<PacienteResponse> buscar(@RequestParam(required = false) String query) {
        return pacienteService.buscar(query);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PacienteResponse crear(@Valid @RequestBody PacienteRequest request) {
        return pacienteService.crear(request);
    }
}
