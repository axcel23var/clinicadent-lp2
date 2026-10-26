package pe.edu.upeu.clinica.pagos.pago.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class MetodoPagoRequest {

    @NotBlank(message = "El nombre del metodo de pago es obligatorio")
    @Size(max = 50, message = "El nombre no debe exceder 50 caracteres")
    private String nombre;

    @Size(max = 150, message = "La descripcion no debe exceder 150 caracteres")
    private String descripcion;
}
