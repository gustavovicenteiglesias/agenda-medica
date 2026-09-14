package ar.com.agendamedica.controller;

import ar.com.agendamedica.dto.CrearTurnoRequest;
import ar.com.agendamedica.dto.TurnoResponse;
import ar.com.agendamedica.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {
    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TurnoResponse crear(@Valid @RequestBody CrearTurnoRequest request) {
        return turnoService.crear(request);
    }

    @GetMapping("/{id}")
    public TurnoResponse obtener(@PathVariable Long id) {
        return turnoService.obtener(id);
    }
}
