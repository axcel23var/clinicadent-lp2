package pe.edu.upeu.clinica.tratamientos.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedimientoResponse {

    private Long id;
    private Long idEspecialidad;
    private String nombre;
    private BigDecimal precio;
}
