package pe.edu.upeu.clinica.pagos.pago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.clinica.pagos.pago.entity.MetodoPago;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {

    boolean existsByNombre(String nombre);
}
