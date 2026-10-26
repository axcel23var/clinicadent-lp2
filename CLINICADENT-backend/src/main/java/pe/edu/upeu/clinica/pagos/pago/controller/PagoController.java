package pe.edu.upeu.clinica.pagos.pago.controller;

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
import pe.edu.upeu.clinica.pagos.pago.dto.PagoRequest;
import pe.edu.upeu.clinica.pagos.pago.dto.PagoResponse;
import pe.edu.upeu.clinica.pagos.pago.service.PagoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Modulo 4 - Liquidacion administrativa y amortizaciones")
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    @Operation(summary = "Registrar abono validando el saldo pendiente del tratamiento")
    public ResponseEntity<PagoResponse> registrarAbono(@Valid @RequestBody PagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.registrarAbono(request));
    }

    @GetMapping
    @Operation(summary = "Listar pagos")
    public ResponseEntity<List<PagoResponse>> listar(
            @RequestParam(required = false) Long idTratamiento) {
        if (idTratamiento != null) {
            return ResponseEntity.ok(pagoService.listarPorTratamiento(idTratamiento));
        }
        return ResponseEntity.ok(pagoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pago por id")
    public ResponseEntity<PagoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }
}
