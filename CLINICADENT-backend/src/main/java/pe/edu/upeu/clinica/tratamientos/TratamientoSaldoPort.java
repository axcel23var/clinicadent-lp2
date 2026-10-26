package pe.edu.upeu.clinica.tratamientos;

import java.math.BigDecimal;

/**
 * API publica del modulo de Tratamientos para liquidacion de caja.
 */
public interface TratamientoSaldoPort {

    BigDecimal obtenerSaldoPendiente(Long tratamientoId);

    void aplicarAbono(Long tratamientoId, BigDecimal montoAbono);
}
