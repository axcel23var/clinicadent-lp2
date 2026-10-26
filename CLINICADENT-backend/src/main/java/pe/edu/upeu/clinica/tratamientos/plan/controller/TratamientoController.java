package pe.edu.upeu.clinica.tratamientos.plan.controller;

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
import pe.edu.upeu.clinica.tratamientos.plan.dto.DetalleTratamientoRequest;
import pe.edu.upeu.clinica.tratamientos.plan.dto.TratamientoEstadoRequest;
import pe.edu.upeu.clinica.tratamientos.plan.dto.TratamientoRequest;
import pe.edu.upeu.clinica.tratamientos.plan.dto.TratamientoResponse;
import pe.edu.upeu.clinica.tratamientos.plan.service.TratamientoService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tratamientos")
@RequiredArgsConstructor
@Tag(name = "Tratamientos", description = "Modulo 2 - Plan de tratamiento cabecera-detalle")
public class TratamientoController {

    private final TratamientoService tratamientoService;

    @PostMapping
    @Operation(summary = "Crear plan de tratamiento")
    public ResponseEntity<TratamientoResponse> crear(@Valid @RequestBody TratamientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tratamientoService.crear(request));
    }

    @PostMapping("/{id}/procedimientos")
    @Operation(summary = "Agregar procedimiento y recalcular costo y saldo")
    public ResponseEntity<TratamientoResponse> agregarProcedimiento(
            @PathVariable Long id,
            @Valid @RequestBody DetalleTratamientoRequest request) {
        return ResponseEntity.ok(tratamientoService.agregarProcedimiento(id, request));
    }

    @GetMapping
    @Operation(summary = "Listar tratamientos")
    public ResponseEntity<List<TratamientoResponse>> listar(
            @RequestParam(required = false) Long idPaciente) {
        if (idPaciente != null) {
            return ResponseEntity.ok(tratamientoService.listarPorPaciente(idPaciente));
        }
        return ResponseEntity.ok(tratamientoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tratamiento por id")
    public ResponseEntity<TratamientoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tratamientoService.obtenerPorId(id));
    }

    @GetMapping("/{id}/saldo")
    @Operation(summary = "Consultar saldo pendiente del tratamiento")
    public ResponseEntity<Map<String, Object>> obtenerSaldo(@PathVariable Long id) {
        BigDecimal saldo = tratamientoService.obtenerSaldoPendiente(id);
        return ResponseEntity.ok(Map.of("tratamientoId", id, "saldoPendiente", saldo));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado del tratamiento")
    public ResponseEntity<TratamientoResponse> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody TratamientoEstadoRequest request) {
        return ResponseEntity.ok(tratamientoService.actualizarEstado(id, request.getEstado()));
    }
}
