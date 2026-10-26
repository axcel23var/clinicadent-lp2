package pe.edu.upeu.clinica.citas.cita.dto;

import jakarta.validation.constraints.NotNull;
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
public class DetalleCitaRequest {

    @NotNull(message = "Selecciona un procedimiento")
    private Long idProcedimiento;

    @Size(max = 500, message = "Las observaciones no deben exceder 500 caracteres")
    private String observaciones;
}
