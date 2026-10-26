package pe.edu.upeu.clinica.tratamientos.plan.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.clinica.shared.exception.ReglaNegocioException;
import pe.edu.upeu.clinica.tratamientos.catalogo.repository.ProcedimientoRepository;
import pe.edu.upeu.clinica.tratamientos.plan.entity.Tratamiento;
import pe.edu.upeu.clinica.tratamientos.plan.mapper.TratamientoMapper;
import pe.edu.upeu.clinica.tratamientos.plan.repository.TratamientoRepository;

@ExtendWith(MockitoExtension.class)
class TratamientoServiceImplTest {

    @Mock
    private TratamientoRepository tratamientoRepository;

    @Mock
    private ProcedimientoRepository procedimientoRepository;

    @Mock
    private TratamientoMapper tratamientoMapper;

    @InjectMocks
    private TratamientoServiceImpl tratamientoService;

    @Test
    void noPermiteFinalizarTratamientoConSaldoPendiente() {
        Tratamiento tratamiento = Tratamiento.builder()
                .id(4L)
                .estado(Tratamiento.ESTADO_EN_PROCESO)
                .saldoPendiente(new BigDecimal("25.00"))
                .build();
        when(tratamientoRepository.findById(4L)).thenReturn(Optional.of(tratamiento));

        assertThrows(ReglaNegocioException.class,
                () -> tratamientoService.actualizarEstado(4L, Tratamiento.ESTADO_FINALIZADO));

        verify(tratamientoRepository, never()).save(tratamiento);
    }

    @Test
    void noPermiteSaltarEstadosDelTratamiento() {
        Tratamiento tratamiento = Tratamiento.builder()
                .id(4L)
                .estado(Tratamiento.ESTADO_BORRADOR)
                .saldoPendiente(BigDecimal.ZERO)
                .build();
        when(tratamientoRepository.findById(4L)).thenReturn(Optional.of(tratamiento));

        assertThrows(ReglaNegocioException.class,
                () -> tratamientoService.actualizarEstado(4L, Tratamiento.ESTADO_EN_PROCESO));

        verify(tratamientoRepository, never()).save(tratamiento);
    }
}
