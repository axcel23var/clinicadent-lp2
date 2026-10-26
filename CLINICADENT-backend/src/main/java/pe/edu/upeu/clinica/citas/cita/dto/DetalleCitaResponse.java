package pe.edu.upeu.clinica.citas.cita.dto;

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
public class DetalleCitaResponse {

    private Long id;
    private Long idProcedimiento;
    private String motivoConsulta;
    private String observaciones;
}
