package pe.edu.upeu.clinica.pagos.pago.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.clinica.pagos.pago.dto.MetodoPagoRequest;
import pe.edu.upeu.clinica.pagos.pago.dto.MetodoPagoResponse;
import pe.edu.upeu.clinica.pagos.pago.entity.MetodoPago;
import pe.edu.upeu.clinica.pagos.pago.mapper.MetodoPagoMapper;
import pe.edu.upeu.clinica.pagos.pago.repository.DetallePagoRepository;
import pe.edu.upeu.clinica.pagos.pago.repository.MetodoPagoRepository;
import pe.edu.upeu.clinica.pagos.pago.service.MetodoPagoService;
import pe.edu.upeu.clinica.shared.exception.ReglaNegocioException;
import pe.edu.upeu.clinica.shared.exception.ResourceNotFoundException;

import java.util.List;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
@Transactional
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;
    private final DetallePagoRepository detallePagoRepository;
    private final MetodoPagoMapper metodoPagoMapper;

    @Override
    public MetodoPagoResponse crear(MetodoPagoRequest request) {
        if (metodoPagoRepository.existsByNombre(request.getNombre())) {
            throw new ReglaNegocioException("Ya existe el metodo de pago " + request.getNombre());
        }
        return metodoPagoMapper.toResponse(metodoPagoRepository.save(metodoPagoMapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public MetodoPagoResponse obtenerPorId(Long id) {
        return metodoPagoMapper.toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPagoResponse> listar() {
        return metodoPagoRepository.findAll().stream()
                .map(metodoPagoMapper::toResponse)
                .toList();
    }

    @Override
    public MetodoPagoResponse actualizar(Long id, MetodoPagoRequest request) {
        MetodoPago metodo = buscar(id);
        if (metodoPagoRepository.existsByNombreAndIdNot(request.getNombre(), id)) {
            throw new ReglaNegocioException("Ya existe el metodo de pago " + request.getNombre());
        }
        metodoPagoMapper.updateEntity(metodo, request);
        return metodoPagoMapper.toResponse(metodoPagoRepository.save(metodo));
    }

    @Override
    public void eliminar(Long id) {
        MetodoPago metodo = buscar(id);
        if (detallePagoRepository.existsByMetodoPagoId(id)) {
            throw new ReglaNegocioException("No se puede eliminar el método de pago porque tiene pagos registrados asociados.");
        }
        metodoPagoRepository.delete(metodo);
    }

    private MetodoPago buscar(Long id) {
        return metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MetodoPago", id));
    }
}
