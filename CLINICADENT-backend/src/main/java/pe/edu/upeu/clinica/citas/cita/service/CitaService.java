package pe.edu.upeu.clinica.citas.cita.service;

import pe.edu.upeu.clinica.citas.cita.dto.CitaRequest;
import pe.edu.upeu.clinica.citas.cita.dto.CitaResponse;

import java.util.List;

public interface CitaService {

    CitaResponse programar(CitaRequest request);

    CitaResponse obtenerPorId(Long id);

    List<CitaResponse> listar();

    List<CitaResponse> listarPorPaciente(Long idPaciente);

    String consultarEstado(Long id);

    CitaResponse actualizar(Long id, CitaRequest request);

    void cancelar(Long id);
}
