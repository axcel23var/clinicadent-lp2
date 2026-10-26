package pe.edu.upeu.clinica.atenciones.odontologo.dto;

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
public class OdontologoResponse {

    private Long id;
    private String cop;
    private String nombres;
    private String apellidos;
    private String especialidad;
}
