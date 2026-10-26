package pe.edu.upeu.clinica.pagos.pago.dto;

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
public class MetodoPagoResponse {

    private Long id;
    private String nombre;
    private String descripcion;
}
