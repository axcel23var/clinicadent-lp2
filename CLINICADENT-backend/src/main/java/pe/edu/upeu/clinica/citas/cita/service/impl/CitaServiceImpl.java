package pe.edu.upeu.clinica.citas.cita.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.clinica.citas.CitaEstadoPort;
import pe.edu.upeu.clinica.citas.cita.dto.CitaRequest;
import pe.edu.upeu.clinica.citas.cita.dto.CitaResponse;
import pe.edu.upeu.clinica.citas.cita.dto.DetalleCitaRequest;
import pe.edu.upeu.clinica.citas.cita.entity.Cita;
import pe.edu.upeu.clinica.citas.cita.entity.DetalleCita;
import pe.edu.upeu.clinica.citas.cita.mapper.CitaMapper;
import pe.edu.upeu.clinica.citas.cita.repository.CitaRepository;
import pe.edu.upeu.clinica.citas.cita.service.CitaService;
import pe.edu.upeu.clinica.citas.paciente.repository.PacienteRepository;
import pe.edu.upeu.clinica.shared.exception.ReglaNegocioException;
import pe.edu.upeu.clinica.shared.exception.ResourceNotFoundException;
import pe.edu.upeu.clinica.tratamientos.catalogo.entity.Procedimiento;
import pe.edu.upeu.clinica.tratamientos.catalogo.repository.ProcedimientoRepository;

import java.util.List;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
@Transactional
public class CitaServiceImpl implements CitaService, CitaEstadoPort {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final ProcedimientoRepository procedimientoRepository;
    private final CitaMapper citaMapper;

    @Override
    public CitaResponse programar(CitaRequest request) {
        validarPaciente(request.getIdPaciente());
        Cita cita = citaMapper.toEntity(request);
        cita.setEstado(Cita.ESTADO_PROGRAMADA);
        agregarDetalles(cita, request);
        return citaMapper.toResponse(citaRepository.save(cita));
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {
        return citaMapper.toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar() {
        return citaRepository.findAll().stream()
                .map(citaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listarPorPaciente(Long idPaciente) {
        validarPaciente(idPaciente);
        return citaRepository.findByIdPaciente(idPaciente).stream()
                .map(citaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String consultarEstado(Long id) {
        return buscar(id).getEstado();
    }

    @Override
    public CitaResponse actualizar(Long id, CitaRequest request) {
        Cita cita = buscar(id);
        if (Cita.ESTADO_ATENDIDA.equals(cita.getEstado())) {
            throw new ReglaNegocioException("No se puede modificar una cita ya atendida");
        }
        validarPaciente(request.getIdPaciente());
        cita.setIdPaciente(request.getIdPaciente());
        cita.setFechaHora(request.getFechaHora());
        cita.getDetalles().clear();
        agregarDetalles(cita, request);
        return citaMapper.toResponse(citaRepository.save(cita));
    }

    @Override
    public void cancelar(Long id) {
        Cita cita = buscar(id);
        if (Cita.ESTADO_ATENDIDA.equals(cita.getEstado())) {
            throw new ReglaNegocioException("No se puede cancelar una cita ya atendida");
        }
        cita.setEstado(Cita.ESTADO_CANCELADA);
        citaRepository.save(cita);
    }

    @Override
    public void actualizarEstado(Long idCita, String estado) {
        Cita cita = buscar(idCita);
        if (Cita.ESTADO_CANCELADA.equals(cita.getEstado())) {
            throw new ReglaNegocioException("La cita " + idCita + " esta cancelada y no puede cambiar de estado");
        }
        cita.setEstado(estado);
        citaRepository.save(cita);
    }

    private void agregarDetalles(Cita cita, CitaRequest request) {
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new ReglaNegocioException("La cita debe solicitar al menos un procedimiento");
        }
        List<Long> idsProcedimientos = request.getDetalles().stream()
                .map(DetalleCitaRequest::getIdProcedimiento)
                .toList();
        if (idsProcedimientos.stream().distinct().count() != idsProcedimientos.size()) {
            throw new ReglaNegocioException("No repitas procedimientos en la misma cita");
        }
        for (DetalleCitaRequest detalleRequest : request.getDetalles()) {
            Procedimiento procedimiento = procedimientoRepository.findById(detalleRequest.getIdProcedimiento())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Procedimiento", detalleRequest.getIdProcedimiento()));
            DetalleCita detalle = citaMapper.toDetalleEntity(detalleRequest);
            detalle.setProcedimiento(procedimiento);
            detalle.setMotivoConsulta(procedimiento.getNombre());
            cita.agregarDetalle(detalle);
        }
    }

    private void validarPaciente(Long idPaciente) {
        if (!pacienteRepository.existsById(idPaciente)) {
            throw new ResourceNotFoundException("Paciente", idPaciente);
        }
    }

    private Cita buscar(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", id));
    }
}
