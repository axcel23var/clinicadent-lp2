package pe.edu.upeu.clinica.atenciones.atencion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.clinica.atenciones.atencion.dto.AtencionRequest;
import pe.edu.upeu.clinica.atenciones.atencion.dto.AtencionResponse;
import pe.edu.upeu.clinica.atenciones.atencion.service.AtencionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/atenciones")
@RequiredArgsConstructor
@Tag(name = "Atenciones", description = "Modulo 3 - Ficha medica y odontograma transaccional")
public class AtencionController {

    private final AtencionService atencionService;

    @PostMapping
    @Operation(summary = "Registrar atencion clinica y marcar la cita como ATENDIDA")
    public ResponseEntity<AtencionResponse> registrar(@Valid @RequestBody AtencionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atencionService.registrarAtencion(request));
    }

    @GetMapping
    @Operation(summary = "Listar atenciones")
    public ResponseEntity<List<AtencionResponse>> listar(
            @RequestParam(required = false) Long idCita) {
        if (idCita != null) {
            return ResponseEntity.ok(atencionService.listarPorCita(idCita));
        }
        return ResponseEntity.ok(atencionService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener atencion por id")
    public ResponseEntity<AtencionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(atencionService.obtenerPorId(id));
    }
}
