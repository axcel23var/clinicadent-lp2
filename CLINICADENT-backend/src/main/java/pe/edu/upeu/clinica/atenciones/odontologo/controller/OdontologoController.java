package pe.edu.upeu.clinica.atenciones.odontologo.controller;

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
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.clinica.atenciones.odontologo.dto.OdontologoRequest;
import pe.edu.upeu.clinica.atenciones.odontologo.dto.OdontologoResponse;
import pe.edu.upeu.clinica.atenciones.odontologo.service.OdontologoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/odontologos")
@RequiredArgsConstructor
@Tag(name = "Odontologos", description = "Modulo 3 - Padron de odontologos")
public class OdontologoController {

    private final OdontologoService odontologoService;

    @PostMapping
    @Operation(summary = "Registrar odontologo")
    public ResponseEntity<OdontologoResponse> crear(@Valid @RequestBody OdontologoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(odontologoService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Listar odontologos")
    public ResponseEntity<List<OdontologoResponse>> listar() {
        return ResponseEntity.ok(odontologoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener odontologo por id")
    public ResponseEntity<OdontologoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(odontologoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar odontologo")
    public ResponseEntity<OdontologoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody OdontologoRequest request) {
        return ResponseEntity.ok(odontologoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar odontologo")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        odontologoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
