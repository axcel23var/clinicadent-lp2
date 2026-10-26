package pe.edu.upeu.clinica.atenciones.odontologo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.clinica.atenciones.odontologo.dto.OdontologoRequest;
import pe.edu.upeu.clinica.atenciones.odontologo.dto.OdontologoResponse;
import pe.edu.upeu.clinica.atenciones.odontologo.entity.Odontologo;
import pe.edu.upeu.clinica.atenciones.odontologo.mapper.OdontologoMapper;
import pe.edu.upeu.clinica.atenciones.odontologo.repository.OdontologoRepository;
import pe.edu.upeu.clinica.atenciones.odontologo.service.OdontologoService;
import pe.edu.upeu.clinica.shared.exception.ReglaNegocioException;
import pe.edu.upeu.clinica.shared.exception.ResourceNotFoundException;

import java.util.List;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
@Transactional
public class OdontologoServiceImpl implements OdontologoService {

    private final OdontologoRepository odontologoRepository;
    private final OdontologoMapper odontologoMapper;

    @Override
    public OdontologoResponse crear(OdontologoRequest request) {
        if (odontologoRepository.existsByCop(request.getCop())) {
            throw new ReglaNegocioException("Ya existe un odontologo con COP " + request.getCop());
        }
        return odontologoMapper.toResponse(odontologoRepository.save(odontologoMapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public OdontologoResponse obtenerPorId(Long id) {
        return odontologoMapper.toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OdontologoResponse> listar() {
        return odontologoRepository.findAll().stream()
                .map(odontologoMapper::toResponse)
                .toList();
    }

    @Override
    public OdontologoResponse actualizar(Long id, OdontologoRequest request) {
        Odontologo odontologo = buscar(id);
        if (odontologoRepository.existsByCopAndIdNot(request.getCop(), id)) {
            throw new ReglaNegocioException("Ya existe un odontologo con COP " + request.getCop());
        }
        odontologoMapper.updateEntity(odontologo, request);
        return odontologoMapper.toResponse(odontologoRepository.save(odontologo));
    }

    @Override
    public void eliminar(Long id) {
        odontologoRepository.delete(buscar(id));
    }

    private Odontologo buscar(Long id) {
        return odontologoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Odontologo", id));
    }
}
