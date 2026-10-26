package pe.edu.upeu.clinica.pagos.pago.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequest {

    @NotEmpty(message = "Distribuye el abono entre uno o más tratamientos")
    @Valid
    private List<DistribucionPagoRequest> distribuciones;

    @NotEmpty(message = "Agrega al menos un método de pago")
    @Valid
    private List<DetallePagoRequest> detalles;
}
