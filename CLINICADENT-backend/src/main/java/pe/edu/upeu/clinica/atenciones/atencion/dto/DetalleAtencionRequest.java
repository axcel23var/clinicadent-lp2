package pe.edu.upeu.clinica.atenciones.atencion.dto;

import jakarta.validation.constraints.NotBlank;
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
public class DetalleAtencionRequest {

    @NotNull(message = "Selecciona el procedimiento realizado")
    private Long idProcedimiento;

    @NotBlank(message = "La pieza dental es obligatoria")
    @Size(max = 10, message = "La pieza dental FDI no debe exceder 10 caracteres")
    private String piezaDental;

    private String intervencion;

    @Size(max = 255, message = "La observacion no debe exceder 255 caracteres")
    private String observacion;
}
