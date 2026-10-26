package pe.edu.upeu.clinica.pagos.pago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.clinica.pagos.pago.entity.Pago;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findFirstByIdTratamientoOrderByIdAsc(Long idTratamiento);

    List<Pago> findByIdTratamiento(Long idTratamiento);

    List<Pago> findByFechaPagoGreaterThanEqualAndFechaPagoLessThan(
            LocalDateTime desdeInclusive,
            LocalDateTime hastaExclusivo);
}
