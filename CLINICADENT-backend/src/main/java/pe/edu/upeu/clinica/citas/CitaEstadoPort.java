package pe.edu.upeu.clinica.citas;

/**
 * API publica del modulo de Citas para que Atenciones actualice el estado.
 */
public interface CitaEstadoPort {

    String ESTADO_ATENDIDA = "ATENDIDA";

    void actualizarEstado(Long idCita, String estado);
}
