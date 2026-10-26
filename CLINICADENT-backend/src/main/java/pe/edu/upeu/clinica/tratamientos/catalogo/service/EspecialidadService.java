package pe.edu.upeu.clinica.tratamientos.catalogo.service;

import pe.edu.upeu.clinica.tratamientos.catalogo.dto.EspecialidadRequest;
import pe.edu.upeu.clinica.tratamientos.catalogo.dto.EspecialidadResponse;

import java.util.List;

public interface EspecialidadService {

    EspecialidadResponse crear(EspecialidadRequest request);

    EspecialidadResponse obtenerPorId(Long id);

    List<EspecialidadResponse> listar();

    EspecialidadResponse actualizar(Long id, EspecialidadRequest request);

    void eliminar(Long id);
}
