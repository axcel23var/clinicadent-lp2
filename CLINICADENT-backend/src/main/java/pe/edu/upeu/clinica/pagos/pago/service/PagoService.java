package pe.edu.upeu.clinica.pagos.pago.service;

import pe.edu.upeu.clinica.pagos.pago.dto.PagoRequest;
import pe.edu.upeu.clinica.pagos.pago.dto.PagoResponse;

import java.util.List;

public interface PagoService {

    PagoResponse registrarAbono(PagoRequest request);

    PagoResponse obtenerPorId(Long id);

    List<PagoResponse> listar();

    List<PagoResponse> listarPorTratamiento(Long idTratamiento);
}
