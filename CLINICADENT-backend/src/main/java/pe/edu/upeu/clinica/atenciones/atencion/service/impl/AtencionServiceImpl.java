package pe.edu.upeu.clinica.atenciones.atencion.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.clinica.atenciones.atencion.dto.AtencionRequest;
import pe.edu.upeu.clinica.atenciones.atencion.dto.AtencionResponse;
import pe.edu.upeu.clinica.atenciones.atencion.entity.Atencion;
import pe.edu.upeu.clinica.atenciones.atencion.entity.DetalleAtencion;
import pe.edu.upeu.clinica.atenciones.atencion.mapper.AtencionMapper;
import pe.edu.upeu.clinica.atenciones.atencion.repository.AtencionRepository;
import pe.edu.upeu.clinica.atenciones.atencion.service.AtencionService;
import pe.edu.upeu.clinica.atenciones.odontologo.repository.OdontologoRepository;
import pe.edu.upeu.clinica.citas.CitaEstadoPort;
import pe.edu.upeu.clinica.shared.exception.ResourceNotFoundException;
import pe.edu.upeu.clinica.tratamientos.catalogo.entity.Procedimiento;
import pe.edu.upeu.clinica.tratamientos.catalogo.repository.ProcedimientoRepository;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
public class AtencionServiceImpl implements AtencionService {

    private final AtencionRepository atencionRepository;
    private final OdontologoRepository odontologoRepository;
    private final AtencionMapper atencionMapper;
    private final CitaEstadoPort citaEstadoPort;
    private final ProcedimientoRepository procedimientoRepository;

    @Override
    @Transactional
    public AtencionResponse registrarAtencion(AtencionRequest request) {
        if (!odontologoRepository.existsById(request.getIdOdontologo())) {
            throw new ResourceNotFoundException("Odontologo", request.getIdOdontologo());
        }

        Atencion atencion = atencionMapper.toEntity(request);
        Set<String> procedimientosPorPieza = new HashSet<>();
        request.getDetalles().forEach(detalleRequest -> {
            String clave = detalleRequest.getPiezaDental().trim() + ":" + detalleRequest.getIdProcedimiento();
            if (!procedimientosPorPieza.add(clave)) {
                throw new pe.edu.upeu.clinica.shared.exception.ReglaNegocioException(
                        "No repitas el mismo procedimiento en una pieza dental");
            }
            Procedimiento procedimiento = procedimientoRepository.findById(detalleRequest.getIdProcedimiento())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Procedimiento", detalleRequest.getIdProcedimiento()));
            DetalleAtencion detalle = atencionMapper.toDetalleEntity(detalleRequest);
            detalle.setProcedimiento(procedimiento);
            detalle.setIntervencion(procedimiento.getNombre());
            atencion.agregarDetalle(detalle);
        });

        Atencion guardada = atencionRepository.saveAndFlush(atencion);
        citaEstadoPort.actualizarEstado(request.getIdCita(), CitaEstadoPort.ESTADO_ATENDIDA);
        return atencionMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public AtencionResponse obtenerPorId(Long id) {
        return atencionMapper.toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtencionResponse> listar() {
        return atencionRepository.findAll().stream()
                .map(atencionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtencionResponse> listarPorCita(Long idCita) {
        return atencionRepository.findByIdCita(idCita).stream()
                .map(atencionMapper::toResponse)
                .toList();
    }

    private Atencion buscar(Long id) {
        return atencionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atencion", id));
    }
}
