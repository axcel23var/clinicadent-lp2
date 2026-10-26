package pe.edu.upeu.clinica.pagos.pago.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.clinica.pagos.pago.dto.MetodoPagoRequest;
import pe.edu.upeu.clinica.pagos.pago.dto.MetodoPagoResponse;
import pe.edu.upeu.clinica.pagos.pago.service.MetodoPagoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metodos-pago")
@RequiredArgsConstructor
@Tag(name = "Metodos de pago", description = "Modulo 4 - Catalogo de medios de caja")
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    @PostMapping
    @Operation(summary = "Registrar metodo de pago")
    public ResponseEntity<MetodoPagoResponse> crear(@Valid @RequestBody MetodoPagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(metodoPagoService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Listar metodos de pago")
    public ResponseEntity<List<MetodoPagoResponse>> listar() {
        return ResponseEntity.ok(metodoPagoService.listar());
    }
}
