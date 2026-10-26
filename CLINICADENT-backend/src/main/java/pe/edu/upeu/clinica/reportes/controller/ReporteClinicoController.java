package pe.edu.upeu.clinica.reportes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.clinica.reportes.dto.ReporteClinicoResponse;
import pe.edu.upeu.clinica.reportes.service.ReporteClinicoService;

@RestController
@RequestMapping("/api/v1/reportes")
@Tag(name = "Reportes", description = "Indicadores clínicos y financieros por período")
public class ReporteClinicoController {

    private final ReporteClinicoService reporteClinicoService;

    public ReporteClinicoController(ReporteClinicoService reporteClinicoService) {
        this.reporteClinicoService = reporteClinicoService;
    }

    @GetMapping("/resumen")
    @Operation(summary = "Consultar resumen de operaciones y recaudación por fechas")
    public ReporteClinicoResponse resumen(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteClinicoService.generar(desde, hasta);
    }
}