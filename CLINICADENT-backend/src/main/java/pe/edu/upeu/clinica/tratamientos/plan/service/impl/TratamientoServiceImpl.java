package pe.edu.upeu.clinica.tratamientos.plan.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.clinica.shared.exception.MontoExcedidoException;
import pe.edu.upeu.clinica.shared.exception.ReglaNegocioException;
import pe.edu.upeu.clinica.shared.exception.ResourceNotFoundException;
import pe.edu.upeu.clinica.tratamientos.TratamientoSaldoPort;
import pe.edu.upeu.clinica.tratamientos.catalogo.entity.Procedimiento;
import pe.edu.upeu.clinica.tratamientos.catalogo.repository.ProcedimientoRepository;
import pe.edu.upeu.clinica.tratamientos.plan.dto.DetalleTratamientoRequest;
import pe.edu.upeu.clinica.tratamientos.plan.dto.TratamientoRequest;
import pe.edu.upeu.clinica.tratamientos.plan.dto.TratamientoResponse;
import pe.edu.upeu.clinica.tratamientos.plan.entity.DetalleTratamiento;
import pe.edu.upeu.clinica.tratamientos.plan.entity.Tratamiento;
import pe.edu.upeu.clinica.tratamientos.plan.mapper.TratamientoMapper;
import pe.edu.upeu.clinica.tratamientos.plan.repository.TratamientoRepository;
import pe.edu.upeu.clinica.tratamientos.plan.service.TratamientoService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
@Transactional
public class TratamientoServiceImpl implements TratamientoService, TratamientoSaldoPort {

    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            Tratamiento.ESTADO_BORRADOR,
            Tratamiento.ESTADO_APROBADO,
            Tratamiento.ESTADO_EN_PROCESO,
            Tratamiento.ESTADO_FINALIZADO);

    private final TratamientoRepository tratamientoRepository;
    private final ProcedimientoRepository procedimientoRepository;
    private final TratamientoMapper tratamientoMapper;

    @Override
    public TratamientoResponse crear(TratamientoRequest request) {
        Tratamiento tratamiento = tratamientoMapper.toEntity(request);
        tratamiento.setEstado(Tratamiento.ESTADO_BORRADOR);
        tratamiento.setCostoTotal(BigDecimal.ZERO);
        tratamiento.setSaldoPendiente(BigDecimal.ZERO);
        if (request.getDetalles() != null) {
            Set<Long> procedimientos = new HashSet<>();
            for (DetalleTratamientoRequest detalle : request.getDetalles()) {
                if (!procedimientos.add(detalle.getIdProcedimiento())) {
                    throw new ReglaNegocioException("No repitas procedimientos en el mismo plan");
                }
                tratamiento.agregarDetalle(construirDetalle(detalle));
            }
        }
        return tratamientoMapper.toResponse(tratamientoRepository.save(tratamiento));
    }

    @Override
    public TratamientoResponse agregarProcedimiento(Long tratamientoId, DetalleTratamientoRequest request) {
        Tratamiento tratamiento = buscar(tratamientoId);
        if (Tratamiento.ESTADO_FINALIZADO.equals(tratamiento.getEstado())) {
            throw new ReglaNegocioException("No se pueden agregar procedimientos a un tratamiento finalizado");
        }
        if (tratamiento.getDetalles().stream()
                .anyMatch(detalle -> detalle.getProcedimiento().getId().equals(request.getIdProcedimiento()))) {
            throw new ReglaNegocioException("Ese procedimiento ya está en el plan; actualiza su cantidad en el detalle");
        }
        tratamiento.agregarDetalle(construirDetalle(request));
        return tratamientoMapper.toResponse(tratamientoRepository.save(tratamiento));
    }

    @Override
    @Transactional(readOnly = true)
    public TratamientoResponse obtenerPorId(Long id) {
        return tratamientoMapper.toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TratamientoResponse> listar() {
        return tratamientoRepository.findAll().stream()
                .map(tratamientoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TratamientoResponse> listarPorPaciente(Long idPaciente) {
        return tratamientoRepository.findByIdPaciente(idPaciente).stream()
                .map(tratamientoMapper::toResponse)
                .toList();
    }

    @Override
    public TratamientoResponse actualizarEstado(Long id, String estado) {
        String estadoNormalizado = estado == null ? "" : estado.trim().toUpperCase();
        if (!ESTADOS_VALIDOS.contains(estadoNormalizado)) {
            throw new ReglaNegocioException("Estado de tratamiento invalido: " + estado);
        }
        Tratamiento tratamiento = buscar(id);
        validarTransicion(tratamiento, estadoNormalizado);
        tratamiento.setEstado(estadoNormalizado);
        return tratamientoMapper.toResponse(tratamientoRepository.save(tratamiento));
    }

    private void validarTransicion(Tratamiento tratamiento, String nuevoEstado) {
        String estadoActual = tratamiento.getEstado();
        if (estadoActual.equals(nuevoEstado)) {
            return;
        }
        boolean transicionValida = switch (estadoActual) {
            case Tratamiento.ESTADO_BORRADOR -> Tratamiento.ESTADO_APROBADO.equals(nuevoEstado);
            case Tratamiento.ESTADO_APROBADO -> Tratamiento.ESTADO_EN_PROCESO.equals(nuevoEstado);
            case Tratamiento.ESTADO_EN_PROCESO -> Tratamiento.ESTADO_FINALIZADO.equals(nuevoEstado);
            default -> false;
        };
        if (!transicionValida) {
            throw new ReglaNegocioException("Transicion no permitida: " + estadoActual + " -> " + nuevoEstado);
        }
        if (Tratamiento.ESTADO_FINALIZADO.equals(nuevoEstado)
                && tratamiento.getSaldoPendiente().compareTo(BigDecimal.ZERO) > 0) {
            throw new ReglaNegocioException("No se puede finalizar un tratamiento con saldo pendiente");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obtenerSaldoPendiente(Long tratamientoId) {
        return buscar(tratamientoId).getSaldoPendiente();
    }

    @Override
    public void aplicarAbono(Long tratamientoId, BigDecimal montoAbono) {
        Tratamiento tratamiento = buscar(tratamientoId);
        if (montoAbono.compareTo(tratamiento.getSaldoPendiente()) > 0) {
            throw new MontoExcedidoException("El abono excede el saldo pendiente del tratamiento");
        }
        tratamiento.setSaldoPendiente(tratamiento.getSaldoPendiente().subtract(montoAbono));
        if (Tratamiento.ESTADO_BORRADOR.equals(tratamiento.getEstado())
                || Tratamiento.ESTADO_APROBADO.equals(tratamiento.getEstado())) {
            tratamiento.setEstado(Tratamiento.ESTADO_EN_PROCESO);
        }
        tratamientoRepository.save(tratamiento);
    }

    private DetalleTratamiento construirDetalle(DetalleTratamientoRequest request) {
        Procedimiento procedimiento = procedimientoRepository.findById(request.getIdProcedimiento())
                .orElseThrow(() -> new ResourceNotFoundException("Procedimiento", request.getIdProcedimiento()));
        BigDecimal subtotal = procedimiento.getPrecio().multiply(BigDecimal.valueOf(request.getCantidad()));
        return DetalleTratamiento.builder()
            .procedimiento(procedimiento)
                .precioUnitario(procedimiento.getPrecio())
                .cantidad(request.getCantidad())
                .subtotal(subtotal)
                .build();
    }

    private Tratamiento buscar(Long id) {
        return tratamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamiento", id));
    }
}
