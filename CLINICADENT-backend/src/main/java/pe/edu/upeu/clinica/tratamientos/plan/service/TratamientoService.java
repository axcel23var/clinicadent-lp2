package pe.edu.upeu.clinica.tratamientos.plan.service;

import pe.edu.upeu.clinica.tratamientos.plan.dto.DetalleTratamientoRequest;
import pe.edu.upeu.clinica.tratamientos.plan.dto.TratamientoRequest;
import pe.edu.upeu.clinica.tratamientos.plan.dto.TratamientoResponse;

import java.math.BigDecimal;
import java.util.List;

public interface TratamientoService {

    TratamientoResponse crear(TratamientoRequest request);

    TratamientoResponse agregarProcedimiento(Long tratamientoId, DetalleTratamientoRequest request);

    TratamientoResponse obtenerPorId(Long id);

    List<TratamientoResponse> listar();

    List<TratamientoResponse> listarPorPaciente(Long idPaciente);

    TratamientoResponse actualizarEstado(Long id, String estado);

    BigDecimal obtenerSaldoPendiente(Long tratamientoId);
}
