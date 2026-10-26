package pe.edu.upeu.clinica.citas.paciente.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.clinica.citas.paciente.dto.PacienteRequest;
import pe.edu.upeu.clinica.citas.paciente.dto.PacienteResponse;
import pe.edu.upeu.clinica.citas.paciente.entity.Paciente;
import pe.edu.upeu.clinica.citas.paciente.mapper.PacienteMapper;
import pe.edu.upeu.clinica.citas.paciente.repository.PacienteRepository;
import pe.edu.upeu.clinica.citas.paciente.service.PacienteService;
import pe.edu.upeu.clinica.shared.exception.ReglaNegocioException;
import pe.edu.upeu.clinica.shared.exception.ResourceNotFoundException;

import java.util.List;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
@Transactional
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @Override
    public PacienteResponse crear(PacienteRequest request) {
        if (pacienteRepository.existsByDni(request.getDni())) {
            throw new ReglaNegocioException("Ya existe un paciente con DNI " + request.getDni());
        }
        Paciente paciente = pacienteMapper.toEntity(request);
        return pacienteMapper.toResponse(pacienteRepository.save(paciente));
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorId(Long id) {
        return pacienteMapper.toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorDni(String dni) {
        Paciente paciente = pacienteRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con DNI " + dni));
        return pacienteMapper.toResponse(paciente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        return pacienteRepository.findAll().stream()
                .map(pacienteMapper::toResponse)
                .toList();
    }

    @Override
    public PacienteResponse actualizar(Long id, PacienteRequest request) {
        Paciente paciente = buscar(id);
        pacienteRepository.findByDni(request.getDni())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ReglaNegocioException("Ya existe un paciente con DNI " + request.getDni());
                });
        pacienteMapper.updateEntity(paciente, request);
        return pacienteMapper.toResponse(pacienteRepository.save(paciente));
    }

    @Override
    public void eliminar(Long id) {
        Paciente paciente = buscar(id);
        pacienteRepository.delete(paciente);
    }

    private Paciente buscar(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", id));
    }
}
