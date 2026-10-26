package pe.edu.upeu.clinica.reportes.dto;

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
public class ProcedimientoReporteResponse {

    private String procedimiento;
    private long vecesRealizado;
}