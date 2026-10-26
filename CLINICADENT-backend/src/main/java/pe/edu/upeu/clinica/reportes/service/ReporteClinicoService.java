package pe.edu.upeu.clinica.reportes.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.clinica.atenciones.atencion.entity.Atencion;
import pe.edu.upeu.clinica.atenciones.atencion.repository.AtencionRepository;
import pe.edu.upeu.clinica.citas.cita.entity.Cita;
import pe.edu.upeu.clinica.citas.cita.repository.CitaRepository;
import pe.edu.upeu.clinica.pagos.pago.entity.Pago;
import pe.edu.upeu.clinica.pagos.pago.repository.PagoRepository;
import pe.edu.upeu.clinica.reportes.dto.MetodoPagoReporteResponse;
import pe.edu.upeu.clinica.reportes.dto.ProcedimientoReporteResponse;
import pe.edu.upeu.clinica.reportes.dto.RecaudacionDiariaResponse;
import pe.edu.upeu.clinica.reportes.dto.ReporteClinicoResponse;
import pe.edu.upeu.clinica.shared.exception.ReglaNegocioException;
import pe.edu.upeu.clinica.tratamientos.plan.entity.Tratamiento;
import pe.edu.upeu.clinica.tratamientos.plan.repository.TratamientoRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteClinicoService {

    private final CitaRepository citaRepository;
    private final AtencionRepository atencionRepository;
    private final TratamientoRepository tratamientoRepository;
    private final PagoRepository pagoRepository;

    public ReporteClinicoResponse generar(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new ReglaNegocioException("El rango de fechas es inválido");
        }

        LocalDateTime desdeInclusive = desde.atStartOfDay();
        LocalDateTime hastaExclusivo = hasta.plusDays(1).atStartOfDay();
        List<Cita> citas = citaRepository.findByFechaHoraGreaterThanEqualAndFechaHoraLessThan(
                desdeInclusive, hastaExclusivo);
        List<Atencion> atenciones = atencionRepository.findByFaAtencionGreaterThanEqualAndFaAtencionLessThan(
                desdeInclusive, hastaExclusivo);
        List<Pago> pagos = pagoRepository.findByFechaPagoGreaterThanEqualAndFechaPagoLessThan(
                desdeInclusive, hastaExclusivo);
        List<Tratamiento> tratamientos = tratamientoRepository.findAll();

        BigDecimal recaudacion = pagos.stream()
                .map(pago -> pago.getMontoTotal() == null ? BigDecimal.ZERO : pago.getMontoTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldoPendiente = tratamientos.stream()
                .map(Tratamiento::getSaldoPendiente)
                .filter(saldo -> saldo != null && saldo.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReporteClinicoResponse.builder()
                .desde(desde)
                .hasta(hasta)
                .totalCitas(citas.size())
                .citasProgramadas(contarCitas(citas, Cita.ESTADO_PROGRAMADA))
                .citasAtendidas(contarCitas(citas, Cita.ESTADO_ATENDIDA))
                .citasCanceladas(contarCitas(citas, Cita.ESTADO_CANCELADA))
                .totalAtenciones(atenciones.size())
                .tratamientosActivos(tratamientos.stream()
                        .filter(tratamiento -> !Tratamiento.ESTADO_FINALIZADO.equals(tratamiento.getEstado()))
                        .count())
                .saldoPendienteActual(saldoPendiente)
                .cantidadPagos(pagos.size())
                .totalRecaudado(recaudacion)
                .procedimientosMasRealizados(resumirProcedimientos(atenciones))
                .recaudacionDiaria(resumirRecaudacionDiaria(pagos))
                .recaudacionPorMetodo(resumirMetodos(pagos))
                .build();
    }

    private long contarCitas(List<Cita> citas, String estado) {
        return citas.stream().filter(cita -> estado.equals(cita.getEstado())).count();
    }

    private List<ProcedimientoReporteResponse> resumirProcedimientos(List<Atencion> atenciones) {
        Map<String, Long> conteos = atenciones.stream()
                .flatMap(atencion -> atencion.getDetalles().stream())
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getProcedimiento() == null
                                ? detalle.getIntervencion()
                                : detalle.getProcedimiento().getNombre(),
                        Collectors.counting()));
        return conteos.entrySet().stream()
                .map(entry -> ProcedimientoReporteResponse.builder()
                        .procedimiento(entry.getKey())
                        .vecesRealizado(entry.getValue())
                        .build())
                .sorted(Comparator.comparingLong(ProcedimientoReporteResponse::getVecesRealizado).reversed())
                .limit(5)
                .toList();
    }

    private List<RecaudacionDiariaResponse> resumirRecaudacionDiaria(List<Pago> pagos) {
        Map<LocalDate, BigDecimal> porFecha = pagos.stream()
                .collect(Collectors.groupingBy(
                        pago -> pago.getFechaPago().toLocalDate(),
                        TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                pago -> pago.getMontoTotal() == null ? BigDecimal.ZERO : pago.getMontoTotal(),
                                BigDecimal::add)));
        return porFecha.entrySet().stream()
                .map(entry -> RecaudacionDiariaResponse.builder()
                        .fecha(entry.getKey())
                        .total(entry.getValue())
                        .build())
                .toList();
    }

    private List<MetodoPagoReporteResponse> resumirMetodos(List<Pago> pagos) {
        Map<String, BigDecimal> porMetodo = pagos.stream()
                .flatMap(pago -> pago.getDetalles().stream())
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getMetodoPago().getNombre(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                detalle -> detalle.getMontoAbonado() == null
                                        ? BigDecimal.ZERO
                                        : detalle.getMontoAbonado(),
                                BigDecimal::add)));
        return porMetodo.entrySet().stream()
                .map(entry -> MetodoPagoReporteResponse.builder()
                        .metodo(entry.getKey())
                        .total(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(MetodoPagoReporteResponse::getTotal).reversed())
                .toList();
    }
}