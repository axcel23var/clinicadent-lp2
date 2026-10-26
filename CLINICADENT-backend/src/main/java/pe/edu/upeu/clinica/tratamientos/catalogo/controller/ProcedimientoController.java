package pe.edu.upeu.clinica.tratamientos.catalogo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.clinica.tratamientos.catalogo.dto.ProcedimientoRequest;
import pe.edu.upeu.clinica.tratamientos.catalogo.dto.ProcedimientoResponse;
import pe.edu.upeu.clinica.tratamientos.catalogo.service.ProcedimientoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/procedimientos")
@RequiredArgsConstructor
@Tag(name = "Procedimientos", description = "Modulo 2 - Tarifario medico")
public class ProcedimientoController {

    private final ProcedimientoService procedimientoService;

    @PostMapping
    @Operation(summary = "Registrar procedimiento en el tarifario")
    public ResponseEntity<ProcedimientoResponse> crear(@Valid @RequestBody ProcedimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(procedimientoService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Listar tarifario de procedimientos")
    public ResponseEntity<List<ProcedimientoResponse>> listar(
            @RequestParam(required = false) Long idEspecialidad) {
        if (idEspecialidad != null) {
            return ResponseEntity.ok(procedimientoService.listarPorEspecialidad(idEspecialidad));
        }
        return ResponseEntity.ok(procedimientoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener procedimiento por id")
    public ResponseEntity<ProcedimientoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(procedimientoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar procedimiento")
    public ResponseEntity<ProcedimientoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProcedimientoRequest request) {
        return ResponseEntity.ok(procedimientoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar procedimiento")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        procedimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
