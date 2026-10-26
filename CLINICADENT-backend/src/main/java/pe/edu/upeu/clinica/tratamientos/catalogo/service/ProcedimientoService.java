package pe.edu.upeu.clinica.tratamientos.catalogo.service;

import pe.edu.upeu.clinica.tratamientos.catalogo.dto.ProcedimientoRequest;
import pe.edu.upeu.clinica.tratamientos.catalogo.dto.ProcedimientoResponse;

import java.util.List;

public interface ProcedimientoService {

    ProcedimientoResponse crear(ProcedimientoRequest request);

    ProcedimientoResponse obtenerPorId(Long id);

    List<ProcedimientoResponse> listar();

    List<ProcedimientoResponse> listarPorEspecialidad(Long idEspecialidad);

    ProcedimientoResponse actualizar(Long id, ProcedimientoRequest request);

    void eliminar(Long id);
}
