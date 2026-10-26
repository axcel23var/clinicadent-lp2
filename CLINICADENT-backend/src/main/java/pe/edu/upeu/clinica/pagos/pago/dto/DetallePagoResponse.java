package pe.edu.upeu.clinica.pagos.pago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePagoResponse {

    private Long id;
    private Long idMetodoPago;
    private BigDecimal montoAbonado;
    private String numeroOperacion;
}
