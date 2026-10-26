package pe.edu.upeu.clinica.atenciones.atencion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class AtencionRequest {

    @NotNull(message = "El id de la cita es obligatorio")
    private Long idCita;

    @NotNull(message = "El id del odontologo es obligatorio")
    private Long idOdontologo;

    @NotBlank(message = "El diagnostico es obligatorio")
    @Size(max = 500, message = "El diagnostico no debe exceder 500 caracteres")
    private String diagnostico;

    @Size(max = 500, message = "La nota medica no debe exceder 500 caracteres")
    private String notaMedica;

    @NotEmpty(message = "Debe registrar al menos una pieza dental del odontograma")
    @Valid
    private List<DetalleAtencionRequest> detalles;
}
