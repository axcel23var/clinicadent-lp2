package pe.edu.upeu.clinica.tratamientos.plan.dto;

import jakarta.validation.constraints.NotBlank;
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
public class TratamientoEstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
