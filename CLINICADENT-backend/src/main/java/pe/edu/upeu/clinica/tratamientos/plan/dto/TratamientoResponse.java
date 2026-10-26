package pe.edu.upeu.clinica.tratamientos.plan.dto;

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
public class TratamientoResponse {

    private Long id;
    private Long idPaciente;
    private LocalDateTime fechaCreacion;
    private String estado;
    private BigDecimal costoTotal;
    private BigDecimal saldoPendiente;
    private List<DetalleTratamientoResponse> detalles;
}
