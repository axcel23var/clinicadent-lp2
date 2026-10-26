package pe.edu.upeu.clinica.citas.cita.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitaRequest {

    @NotNull(message = "El id del paciente es obligatorio")
    private Long idPaciente;

    @NotNull(message = "La fecha y hora de la cita son obligatorias")
    private LocalDateTime fechaHora;

    @NotEmpty(message = "Agrega al menos un procedimiento solicitado")
    @Valid
    private List<DetalleCitaRequest> detalles;
}
