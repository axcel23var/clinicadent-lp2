package pe.edu.upeu.clinica.atenciones.atencion.dto;

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
public class AtencionResponse {

    private Long id;
    private Long idCita;
    private Long idOdontologo;
    private LocalDateTime faAtencion;
    private String diagnostico;
    private String notaMedica;
    private List<DetalleAtencionResponse> detalles;
}
