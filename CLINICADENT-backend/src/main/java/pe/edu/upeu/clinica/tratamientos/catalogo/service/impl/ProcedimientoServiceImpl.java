package pe.edu.upeu.clinica.tratamientos.catalogo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.clinica.shared.exception.ReglaNegocioException;
import pe.edu.upeu.clinica.shared.exception.ResourceNotFoundException;
import pe.edu.upeu.clinica.tratamientos.catalogo.dto.ProcedimientoRequest;
import pe.edu.upeu.clinica.tratamientos.catalogo.dto.ProcedimientoResponse;
import pe.edu.upeu.clinica.tratamientos.catalogo.entity.Procedimiento;
import pe.edu.upeu.clinica.tratamientos.catalogo.mapper.ProcedimientoMapper;
import pe.edu.upeu.clinica.tratamientos.catalogo.repository.EspecialidadRepository;
import pe.edu.upeu.clinica.tratamientos.catalogo.repository.ProcedimientoRepository;
import pe.edu.upeu.clinica.tratamientos.catalogo.service.ProcedimientoService;

import java.util.List;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
@Transactional
public class ProcedimientoServiceImpl implements ProcedimientoService {

    private final ProcedimientoRepository procedimientoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final ProcedimientoMapper procedimientoMapper;

    @Override
    public ProcedimientoResponse crear(ProcedimientoRequest request) {
        validarNombreUnico(request.getNombre(), null);
        validarEspecialidad(request.getIdEspecialidad());
        return procedimientoMapper.toResponse(procedimientoRepository.save(procedimientoMapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProcedimientoResponse obtenerPorId(Long id) {
        return procedimientoMapper.toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcedimientoResponse> listar() {
        return procedimientoRepository.findAll().stream()
                .map(procedimientoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcedimientoResponse> listarPorEspecialidad(Long idEspecialidad) {
        validarEspecialidad(idEspecialidad);
        return procedimientoRepository.findByIdEspecialidad(idEspecialidad).stream()
                .map(procedimientoMapper::toResponse)
                .toList();
    }

    @Override
    public ProcedimientoResponse actualizar(Long id, ProcedimientoRequest request) {
        Procedimiento procedimiento = buscar(id);
        validarNombreUnico(request.getNombre(), id);
        validarEspecialidad(request.getIdEspecialidad());
        procedimientoMapper.updateEntity(procedimiento, request);
        return procedimientoMapper.toResponse(procedimientoRepository.save(procedimiento));
    }

    @Override
    public void eliminar(Long id) {
        procedimientoRepository.delete(buscar(id));
    }

    private void validarNombreUnico(String nombre, Long id) {
        boolean duplicado = id == null
                ? procedimientoRepository.existsByNombre(nombre)
                : procedimientoRepository.existsByNombreAndIdNot(nombre, id);
        if (duplicado) {
            throw new ReglaNegocioException("Ya existe un procedimiento con nombre " + nombre);
        }
    }

    private void validarEspecialidad(Long idEspecialidad) {
        if (idEspecialidad != null && !especialidadRepository.existsById(idEspecialidad)) {
            throw new ResourceNotFoundException("Especialidad", idEspecialidad);
        }
    }

    private Procedimiento buscar(Long id) {
        return procedimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procedimiento", id));
    }
}
