package pe.edu.upeu.clinica.pagos.pago.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.clinica.pagos.pago.dto.MetodoPagoRequest;
import pe.edu.upeu.clinica.pagos.pago.dto.MetodoPagoResponse;
import pe.edu.upeu.clinica.pagos.pago.mapper.MetodoPagoMapper;
import pe.edu.upeu.clinica.pagos.pago.repository.MetodoPagoRepository;
import pe.edu.upeu.clinica.pagos.pago.service.MetodoPagoService;
import pe.edu.upeu.clinica.shared.exception.ReglaNegocioException;

import java.util.List;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
@Transactional
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;
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
    public List<MetodoPagoResponse> listar() {
        return metodoPagoRepository.findAll().stream()
                .map(metodoPagoMapper::toResponse)
                .toList();
    }
}
