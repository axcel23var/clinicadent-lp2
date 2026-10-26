package pe.edu.upeu.clinica.pagos.pago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.clinica.pagos.pago.entity.DetallePago;

import java.util.List;

public interface DetallePagoRepository extends JpaRepository<DetallePago, Long> {

    List<DetallePago> findByPagoId(Long idPago);
}
