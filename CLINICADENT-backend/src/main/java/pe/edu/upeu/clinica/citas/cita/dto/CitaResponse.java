package pe.edu.upeu.clinica.citas.cita.dto;

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
public class CitaResponse {

    private Long id;
    private Long idPaciente;
    private LocalDateTime fechaHora;
    private String estado;
    private List<DetalleCitaResponse> detalles;
}
