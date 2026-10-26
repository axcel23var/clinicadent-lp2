package pe.edu.upeu.clinica.pagos.pago.dto;

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
public class DistribucionPagoResponse {

    private Long id;
    private Long idTratamiento;
    private BigDecimal montoAplicado;
}