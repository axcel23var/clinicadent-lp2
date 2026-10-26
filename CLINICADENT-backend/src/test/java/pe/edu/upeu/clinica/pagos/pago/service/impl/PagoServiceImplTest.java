package pe.edu.upeu.clinica.pagos.pago.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.clinica.pagos.pago.dto.DetallePagoRequest;
import pe.edu.upeu.clinica.pagos.pago.dto.DistribucionPagoRequest;
import pe.edu.upeu.clinica.pagos.pago.dto.PagoRequest;
import pe.edu.upeu.clinica.pagos.pago.dto.PagoResponse;
import pe.edu.upeu.clinica.pagos.pago.entity.MetodoPago;
import pe.edu.upeu.clinica.pagos.pago.entity.Pago;
import pe.edu.upeu.clinica.tratamientos.plan.entity.Tratamiento;
import pe.edu.upeu.clinica.pagos.pago.mapper.PagoMapper;
import pe.edu.upeu.clinica.pagos.pago.repository.DistribucionPagoRepository;
import pe.edu.upeu.clinica.pagos.pago.repository.MetodoPagoRepository;
import pe.edu.upeu.clinica.pagos.pago.repository.PagoRepository;
import pe.edu.upeu.clinica.shared.exception.ReglaNegocioException;
import pe.edu.upeu.clinica.tratamientos.TratamientoSaldoPort;

@ExtendWith(MockitoExtension.class)
class PagoServiceImplTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private MetodoPagoRepository metodoPagoRepository;

        @Mock
        private DistribucionPagoRepository distribucionPagoRepository;

        @Mock
        private EntityManager entityManager;

    @Mock
    private PagoMapper pagoMapper;

    @Mock
    private TratamientoSaldoPort tratamientoSaldoPort;

    @InjectMocks
    private PagoServiceImpl pagoService;

    @Test
    void distribuyeUnPagoEntreVariosTratamientosYMetodos() {
        MetodoPago efectivo = MetodoPago.builder().id(1L).nombre("Efectivo").build();
        MetodoPago tarjeta = MetodoPago.builder().id(2L).nombre("Tarjeta").build();
        PagoResponse response = PagoResponse.builder().idTratamiento(9L).build();
        Tratamiento primerTratamiento = Tratamiento.builder().id(9L).build();
        Tratamiento segundoTratamiento = Tratamiento.builder().id(10L).build();
        when(metodoPagoRepository.existsById(1L)).thenReturn(true);
        when(metodoPagoRepository.existsById(2L)).thenReturn(true);
        when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(efectivo));
        when(metodoPagoRepository.findById(2L)).thenReturn(Optional.of(tarjeta));
        when(tratamientoSaldoPort.obtenerSaldoPendiente(9L))
                .thenReturn(new BigDecimal("100.00"), new BigDecimal("80.00"));
        when(tratamientoSaldoPort.obtenerSaldoPendiente(10L))
                .thenReturn(new BigDecimal("50.00"), new BigDecimal("40.00"));
        when(entityManager.getReference(Tratamiento.class, 9L)).thenReturn(primerTratamiento);
        when(entityManager.getReference(Tratamiento.class, 10L)).thenReturn(segundoTratamiento);
        when(pagoRepository.saveAndFlush(any(Pago.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(pagoMapper.toResponse(any(Pago.class))).thenReturn(response);

        PagoRequest request = PagoRequest.builder()
                .distribuciones(List.of(
                        DistribucionPagoRequest.builder()
                                .idTratamiento(9L)
                                .montoAplicado(new BigDecimal("20.00"))
                                .build(),
                        DistribucionPagoRequest.builder()
                                .idTratamiento(10L)
                                .montoAplicado(new BigDecimal("10.00"))
                                .build()))
                .detalles(List.of(
                        DetallePagoRequest.builder()
                                .idMetodoPago(1L)
                                .montoAbonado(new BigDecimal("10.00"))
                                .build(),
                        DetallePagoRequest.builder()
                                .idMetodoPago(2L)
                                .montoAbonado(new BigDecimal("20.00"))
                                .numeroOperacion("OP-20")
                                .build()))
                .build();

        PagoResponse result = pagoService.registrarAbono(request);

        ArgumentCaptor<Pago> pagoCaptor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository).saveAndFlush(pagoCaptor.capture());
        assertEquals(2, pagoCaptor.getValue().getDetalles().size());
        assertEquals(2, pagoCaptor.getValue().getDistribuciones().size());
        assertEquals(new BigDecimal("30.00"), pagoCaptor.getValue().getMontoTotal());
        assertEquals("PENDIENTE", result.getEstado());
        verify(tratamientoSaldoPort).aplicarAbono(9L, new BigDecimal("20.00"));
        verify(tratamientoSaldoPort).aplicarAbono(10L, new BigDecimal("10.00"));
    }

    @Test
    void rechazaUnMetodoDePagoDuplicadoEnElMismoAbono() {
                when(metodoPagoRepository.existsById(1L)).thenReturn(true);
                when(tratamientoSaldoPort.obtenerSaldoPendiente(9L)).thenReturn(new BigDecimal("50.00"));
        PagoRequest request = PagoRequest.builder()
                        .distribuciones(List.of(DistribucionPagoRequest.builder()
                                .idTratamiento(9L)
                                .montoAplicado(new BigDecimal("11.00"))
                                .build()))
                .detalles(List.of(
                        DetallePagoRequest.builder().idMetodoPago(1L).montoAbonado(BigDecimal.TEN).build(),
                        DetallePagoRequest.builder().idMetodoPago(1L).montoAbonado(BigDecimal.ONE).build()))
                .build();

        assertThrows(ReglaNegocioException.class, () -> pagoService.registrarAbono(request));

        verify(pagoRepository, never()).saveAndFlush(any(Pago.class));
        verify(tratamientoSaldoPort, never()).aplicarAbono(any(Long.class), any(BigDecimal.class));
    }
}
