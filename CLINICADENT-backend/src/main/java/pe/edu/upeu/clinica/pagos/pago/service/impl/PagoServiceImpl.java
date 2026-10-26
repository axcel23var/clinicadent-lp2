package pe.edu.upeu.clinica.pagos.pago.service.impl;

import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.clinica.pagos.pago.dto.PagoRequest;
import pe.edu.upeu.clinica.pagos.pago.dto.PagoResponse;
import pe.edu.upeu.clinica.pagos.pago.dto.DetallePagoRequest;
import pe.edu.upeu.clinica.pagos.pago.dto.DistribucionPagoRequest;
import pe.edu.upeu.clinica.pagos.pago.entity.DetallePago;
import pe.edu.upeu.clinica.pagos.pago.entity.DistribucionPago;
import pe.edu.upeu.clinica.pagos.pago.entity.Pago;
import pe.edu.upeu.clinica.pagos.pago.mapper.PagoMapper;
import pe.edu.upeu.clinica.pagos.pago.repository.DistribucionPagoRepository;
import pe.edu.upeu.clinica.pagos.pago.repository.MetodoPagoRepository;
import pe.edu.upeu.clinica.pagos.pago.repository.PagoRepository;
import pe.edu.upeu.clinica.pagos.pago.service.PagoService;
import pe.edu.upeu.clinica.shared.exception.MontoExcedidoException;
import pe.edu.upeu.clinica.shared.exception.ReglaNegocioException;
import pe.edu.upeu.clinica.shared.exception.ResourceNotFoundException;
import pe.edu.upeu.clinica.tratamientos.TratamientoSaldoPort;
import pe.edu.upeu.clinica.tratamientos.plan.entity.Tratamiento;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
@Transactional
public class PagoServiceImpl implements PagoService {

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_PAGADO = "PAGADO";

    private final PagoRepository pagoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final DistribucionPagoRepository distribucionPagoRepository;
    private final PagoMapper pagoMapper;
    private final TratamientoSaldoPort tratamientoSaldoPort;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public PagoResponse registrarAbono(PagoRequest request) {
        if (request.getDistribuciones() == null || request.getDistribuciones().isEmpty()) {
            throw new ReglaNegocioException("Distribuye el abono entre uno o más tratamientos");
        }
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new ReglaNegocioException("Agrega al menos un método de pago");
        }

        Set<Long> tratamientos = new HashSet<>();
        BigDecimal montoDistribuido = BigDecimal.ZERO;
        for (DistribucionPagoRequest distribucion : request.getDistribuciones()) {
            if (!tratamientos.add(distribucion.getIdTratamiento())) {
                throw new ReglaNegocioException("No repitas tratamientos en la misma operación de pago");
            }
            BigDecimal saldo = tratamientoSaldoPort.obtenerSaldoPendiente(distribucion.getIdTratamiento());
            if (distribucion.getMontoAplicado().compareTo(saldo) > 0) {
                throw new MontoExcedidoException("La distribución excede el saldo del tratamiento "
                        + distribucion.getIdTratamiento());
            }
            montoDistribuido = montoDistribuido.add(distribucion.getMontoAplicado());
        }

        Set<Long> metodos = new HashSet<>();
        BigDecimal montoTotalAbono = BigDecimal.ZERO;
        for (DetallePagoRequest detalle : request.getDetalles()) {
            if (!metodos.add(detalle.getIdMetodoPago())) {
                throw new ReglaNegocioException("No repitas métodos de pago en el mismo abono");
            }
            if (!metodoPagoRepository.existsById(detalle.getIdMetodoPago())) {
                throw new ResourceNotFoundException("MetodoPago", detalle.getIdMetodoPago());
            }
            montoTotalAbono = montoTotalAbono.add(detalle.getMontoAbonado());
        }

        if (montoTotalAbono.compareTo(montoDistribuido) != 0) {
            throw new ReglaNegocioException("La suma de los métodos debe coincidir con la suma distribuida a tratamientos");
        }

        Pago pago = Pago.builder()
            .idTratamiento(request.getDistribuciones().get(0).getIdTratamiento())
            .montoTotal(BigDecimal.ZERO)
            .build();

        for (DistribucionPagoRequest distribucionRequest : request.getDistribuciones()) {
            pago.agregarDistribucion(DistribucionPago.builder()
                .tratamiento(entityManager.getReference(Tratamiento.class, distribucionRequest.getIdTratamiento()))
                .montoAplicado(distribucionRequest.getMontoAplicado())
                .build());
        }

        for (DetallePagoRequest detalleRequest : request.getDetalles()) {
            DetallePago detalle = DetallePago.builder()
                .metodoPago(metodoPagoRepository.findById(detalleRequest.getIdMetodoPago())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "MetodoPago", detalleRequest.getIdMetodoPago())))
                .montoAbonado(detalleRequest.getMontoAbonado())
                .numeroOperacion(detalleRequest.getNumeroOperacion())
                .build();
            pago.agregarDetalle(detalle);
        }

        Pago guardado = pagoRepository.saveAndFlush(pago);
        for (DistribucionPagoRequest distribucion : request.getDistribuciones()) {
            tratamientoSaldoPort.aplicarAbono(distribucion.getIdTratamiento(), distribucion.getMontoAplicado());
        }
        return toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponse obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> listar() {
        return pagoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> listarPorTratamiento(Long idTratamiento) {
        Map<Long, Pago> pagos = new LinkedHashMap<>();
        pagoRepository.findByIdTratamiento(idTratamiento)
            .forEach(pago -> pagos.put(pago.getId(), pago));
        distribucionPagoRepository.findByTratamientoId(idTratamiento)
            .forEach(distribucion -> pagos.put(distribucion.getPago().getId(), distribucion.getPago()));
        return pagos.values().stream()
                .map(this::toResponse)
                .toList();
    }

    private PagoResponse toResponse(Pago pago) {
        PagoResponse response = pagoMapper.toResponse(pago);
        List<Long> idsTratamientos = pago.getDistribuciones().isEmpty()
            ? List.of(pago.getIdTratamiento())
            : pago.getDistribuciones().stream()
                .map(distribucion -> distribucion.getTratamiento().getId())
                .toList();
        boolean todosPagados = idsTratamientos.stream()
            .allMatch(id -> tratamientoSaldoPort.obtenerSaldoPendiente(id).compareTo(BigDecimal.ZERO) == 0);
        response.setEstado(todosPagados ? ESTADO_PAGADO : ESTADO_PENDIENTE);
        return response;
    }

    private Pago buscar(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));
    }
}
