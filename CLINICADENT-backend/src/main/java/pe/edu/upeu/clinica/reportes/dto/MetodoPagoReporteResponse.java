package pe.edu.upeu.clinica.reportes.dto;

import java.math.BigDecimal;
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
public class MetodoPagoReporteResponse {

    private String metodo;
    private BigDecimal total;
}