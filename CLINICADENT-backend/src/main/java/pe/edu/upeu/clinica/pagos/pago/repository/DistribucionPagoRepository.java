package pe.edu.upeu.clinica.pagos.pago.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.clinica.pagos.pago.entity.DistribucionPago;

public interface DistribucionPagoRepository extends JpaRepository<DistribucionPago, Long> {

    @Query("select d from DistribucionPago d join fetch d.pago where d.tratamiento.id = :idTratamiento")
    List<DistribucionPago> findByTratamientoId(@Param("idTratamiento") Long idTratamiento);
}