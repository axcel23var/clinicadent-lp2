package pe.edu.upeu.clinica.reportes.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteClinicoResponse {

    private LocalDate desde;
    private LocalDate hasta;
    private long totalCitas;
    private long citasProgramadas;
    private long citasAtendidas;
    private long citasCanceladas;
    private long totalAtenciones;
    private long tratamientosActivos;
    private BigDecimal saldoPendienteActual;
    private long cantidadPagos;
    private BigDecimal totalRecaudado;
    private List<ProcedimientoReporteResponse> procedimientosMasRealizados;
    private List<RecaudacionDiariaResponse> recaudacionDiaria;
    private List<MetodoPagoReporteResponse> recaudacionPorMetodo;
}