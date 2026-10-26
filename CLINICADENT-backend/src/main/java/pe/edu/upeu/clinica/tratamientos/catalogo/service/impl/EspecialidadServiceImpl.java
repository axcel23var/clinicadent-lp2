package pe.edu.upeu.clinica.tratamientos.catalogo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.clinica.shared.exception.ReglaNegocioException;
import pe.edu.upeu.clinica.shared.exception.ResourceNotFoundException;
import pe.edu.upeu.clinica.tratamientos.catalogo.dto.EspecialidadRequest;
import pe.edu.upeu.clinica.tratamientos.catalogo.dto.EspecialidadResponse;
import pe.edu.upeu.clinica.tratamientos.catalogo.entity.Especialidad;
import pe.edu.upeu.clinica.tratamientos.catalogo.mapper.EspecialidadMapper;
import pe.edu.upeu.clinica.tratamientos.catalogo.repository.EspecialidadRepository;
import pe.edu.upeu.clinica.tratamientos.catalogo.service.EspecialidadService;

import java.util.List;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
@Transactional
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final EspecialidadMapper especialidadMapper;

    @Override
    public EspecialidadResponse crear(EspecialidadRequest request) {
        if (especialidadRepository.existsByNombre(request.getNombre())) {
            throw new ReglaNegocioException("Ya existe una especialidad con nombre " + request.getNombre());
        }
        return especialidadMapper.toResponse(especialidadRepository.save(especialidadMapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public EspecialidadResponse obtenerPorId(Long id) {
        return especialidadMapper.toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspecialidadResponse> listar() {
        return especialidadRepository.findAll().stream()
                .map(especialidadMapper::toResponse)
                .toList();
    }

    @Override
    public EspecialidadResponse actualizar(Long id, EspecialidadRequest request) {
        Especialidad especialidad = buscar(id);
        if (especialidadRepository.existsByNombreAndIdNot(request.getNombre(), id)) {
            throw new ReglaNegocioException("Ya existe una especialidad con nombre " + request.getNombre());
        }
        especialidadMapper.updateEntity(especialidad, request);
        return especialidadMapper.toResponse(especialidadRepository.save(especialidad));
    }

    @Override
    public void eliminar(Long id) {
        especialidadRepository.delete(buscar(id));
    }

    private Especialidad buscar(Long id) {
        return especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad", id));
    }
}
