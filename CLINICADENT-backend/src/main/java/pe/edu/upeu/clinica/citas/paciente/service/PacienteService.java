package pe.edu.upeu.clinica.citas.paciente.service;

import pe.edu.upeu.clinica.citas.paciente.dto.PacienteRequest;
import pe.edu.upeu.clinica.citas.paciente.dto.PacienteResponse;

import java.util.List;

public interface PacienteService {

    PacienteResponse crear(PacienteRequest request);

    PacienteResponse obtenerPorId(Long id);

    PacienteResponse obtenerPorDni(String dni);

    List<PacienteResponse> listar();

    PacienteResponse actualizar(Long id, PacienteRequest request);

    void eliminar(Long id);
}
