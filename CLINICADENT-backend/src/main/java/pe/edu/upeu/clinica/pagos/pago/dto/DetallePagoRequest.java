package pe.edu.upeu.clinica.pagos.pago.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class DetallePagoRequest {

    @NotNull(message = "Selecciona un método de pago")
    private Long idMetodoPago;

    @NotNull(message = "El monto por método es obligatorio")
    @DecimalMin(value = "0.01", message = "Cada monto debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El monto debe tener formato 10,2")
    private BigDecimal montoAbonado;

    @Size(max = 50, message = "El número de operación no debe exceder 50 caracteres")
    private String numeroOperacion;
}