package pe.edu.upeu.clinica.tratamientos.plan.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class TratamientoRequest {

    @NotNull(message = "El id del paciente es obligatorio")
    private Long idPaciente;

    @NotEmpty(message = "Agrega al menos un procedimiento al plan")
    @Valid
    private List<DetalleTratamientoRequest> detalles;
}
