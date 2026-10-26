package pe.edu.upeu.clinica.atenciones.atencion.dto;

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
public class DetalleAtencionResponse {

    private Long id;
    private Long idProcedimiento;
    private String piezaDental;
    private String intervencion;
    private String observacion;
}
