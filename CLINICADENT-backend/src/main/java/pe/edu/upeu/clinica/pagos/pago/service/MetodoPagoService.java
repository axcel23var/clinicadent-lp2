package pe.edu.upeu.clinica.pagos.pago.service;

import pe.edu.upeu.clinica.pagos.pago.dto.MetodoPagoRequest;
import pe.edu.upeu.clinica.pagos.pago.dto.MetodoPagoResponse;

import java.util.List;

public interface MetodoPagoService {

    MetodoPagoResponse crear(MetodoPagoRequest request);

    MetodoPagoResponse obtenerPorId(Long id);

    List<MetodoPagoResponse> listar();

    MetodoPagoResponse actualizar(Long id, MetodoPagoRequest request);

    void eliminar(Long id);
}
