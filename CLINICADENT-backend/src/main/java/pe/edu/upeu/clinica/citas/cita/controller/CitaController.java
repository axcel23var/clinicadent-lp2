package pe.edu.upeu.clinica.citas.cita.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.clinica.citas.cita.dto.CitaRequest;
import pe.edu.upeu.clinica.citas.cita.dto.CitaResponse;
import pe.edu.upeu.clinica.citas.cita.service.CitaService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/citas")
@RequiredArgsConstructor
@Tag(name = "Citas", description = "Modulo 1 - Programacion y estado de citas")
public class CitaController {

    private final CitaService citaService;

    @PostMapping
    @Operation(summary = "Programar cita")
    public ResponseEntity<CitaResponse> programar(@Valid @RequestBody CitaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.programar(request));
    }

    @GetMapping
    @Operation(summary = "Listar citas")
    public ResponseEntity<List<CitaResponse>> listar(
            @RequestParam(required = false) Long idPaciente) {
        if (idPaciente != null) {
            return ResponseEntity.ok(citaService.listarPorPaciente(idPaciente));
        }
        return ResponseEntity.ok(citaService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cita por id")
    public ResponseEntity<CitaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerPorId(id));
    }

    @GetMapping("/{id}/estado")
    @Operation(summary = "Consultar estado de la cita")
    public ResponseEntity<Map<String, String>> consultarEstado(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("id", String.valueOf(id), "estado", citaService.consultarEstado(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cita programada")
    public ResponseEntity<CitaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaService.actualizar(id, request));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar cita")
    public ResponseEntity<Map<String, String>> cancelar(@PathVariable Long id) {
        citaService.cancelar(id);
        return ResponseEntity.ok(Map.of("id", String.valueOf(id), "estado", "CANCELADA"));
    }
}
