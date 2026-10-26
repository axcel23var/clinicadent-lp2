package pe.edu.upeu.clinica.pagos.pago.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
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
public class DistribucionPagoRequest {

    @NotNull(message = "Selecciona el tratamiento que recibe el abono")
    private Long idTratamiento;

    @NotNull(message = "El monto aplicado al tratamiento es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto aplicado debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El monto aplicado debe tener formato 10,2")
    private BigDecimal montoAplicado;
}