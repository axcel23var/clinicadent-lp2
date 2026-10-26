package pe.edu.upeu.clinica.pagos.pago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {

    private Long id;
    private Long idTratamiento;
    private LocalDateTime fechaPago;
    private BigDecimal montoTotal;
    private String estado;
    private List<DetallePagoResponse> detalles;
    private List<DistribucionPagoResponse> distribuciones;
}
