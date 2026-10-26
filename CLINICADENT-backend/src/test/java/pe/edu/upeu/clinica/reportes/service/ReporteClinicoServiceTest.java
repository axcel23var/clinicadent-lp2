package pe.edu.upeu.clinica.reportes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.clinica.atenciones.atencion.entity.Atencion;
import pe.edu.upeu.clinica.atenciones.atencion.entity.DetalleAtencion;
import pe.edu.upeu.clinica.atenciones.atencion.repository.AtencionRepository;
import pe.edu.upeu.clinica.citas.cita.entity.Cita;
import pe.edu.upeu.clinica.citas.cita.repository.CitaRepository;
import pe.edu.upeu.clinica.pagos.pago.entity.DetallePago;
import pe.edu.upeu.clinica.pagos.pago.entity.MetodoPago;
import pe.edu.upeu.clinica.pagos.pago.entity.Pago;
import pe.edu.upeu.clinica.pagos.pago.repository.PagoRepository;
import pe.edu.upeu.clinica.reportes.dto.ReporteClinicoResponse;
import pe.edu.upeu.clinica.tratamientos.catalogo.entity.Procedimiento;
import pe.edu.upeu.clinica.tratamientos.plan.entity.Tratamiento;
import pe.edu.upeu.clinica.tratamientos.plan.repository.TratamientoRepository;

@ExtendWith(MockitoExtension.class)
class ReporteClinicoServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private AtencionRepository atencionRepository;

    @Mock
    private TratamientoRepository tratamientoRepository;

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private ReporteClinicoService reporteClinicoService;

    @Test
    void agregaIndicadoresClinicosYFinancierosDelPeriodo() {
        LocalDate desde = LocalDate.of(2026, 10, 1);
        LocalDate hasta = LocalDate.of(2026, 10, 31);
        LocalDateTime fecha = LocalDateTime.of(2026, 10, 22, 10, 0);
        when(citaRepository.findByFechaHoraGreaterThanEqualAndFechaHoraLessThan(
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(
                        Cita.builder().estado(Cita.ESTADO_PROGRAMADA).build(),
                        Cita.builder().estado(Cita.ESTADO_ATENDIDA).build(),
                        Cita.builder().estado(Cita.ESTADO_CANCELADA).build()));

        Procedimiento limpieza = Procedimiento.builder().id(1L).nombre("Limpieza dental").build();
        DetalleAtencion detalleAtencion = DetalleAtencion.builder()
                .procedimiento(limpieza)
                .intervencion("Limpieza dental")
                .piezaDental("16")
                .build();
        when(atencionRepository.findByFaAtencionGreaterThanEqualAndFaAtencionLessThan(
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(Atencion.builder().detalles(List.of(detalleAtencion)).build()));
        when(tratamientoRepository.findAll()).thenReturn(List.of(
                Tratamiento.builder().estado(Tratamiento.ESTADO_EN_PROCESO)
                        .saldoPendiente(new BigDecimal("75.00")).build(),
                Tratamiento.builder().estado(Tratamiento.ESTADO_FINALIZADO)
                        .saldoPendiente(BigDecimal.ZERO).build()));

        MetodoPago efectivo = MetodoPago.builder().id(1L).nombre("Efectivo").build();
        DetallePago detallePago = DetallePago.builder()
                .metodoPago(efectivo)
                .montoAbonado(new BigDecimal("50.00"))
                .build();
        when(pagoRepository.findByFechaPagoGreaterThanEqualAndFechaPagoLessThan(
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(Pago.builder()
                        .fechaPago(fecha)
                        .montoTotal(new BigDecimal("50.00"))
                        .detalles(List.of(detallePago))
                        .build()));

        ReporteClinicoResponse reporte = reporteClinicoService.generar(desde, hasta);

        assertEquals(3, reporte.getTotalCitas());
        assertEquals(1, reporte.getCitasProgramadas());
        assertEquals(1, reporte.getCitasAtendidas());
        assertEquals(1, reporte.getCitasCanceladas());
        assertEquals(1, reporte.getTotalAtenciones());
        assertEquals(1, reporte.getTratamientosActivos());
        assertEquals(new BigDecimal("75.00"), reporte.getSaldoPendienteActual());
        assertEquals(new BigDecimal("50.00"), reporte.getTotalRecaudado());
        assertEquals("Limpieza dental", reporte.getProcedimientosMasRealizados().get(0).getProcedimiento());
        assertEquals("Efectivo", reporte.getRecaudacionPorMetodo().get(0).getMetodo());
        assertEquals(1, reporte.getRecaudacionDiaria().size());
    }
}
