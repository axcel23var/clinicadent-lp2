package pe.edu.upeu.clinica.citas.cita.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.clinica.citas.cita.entity.DetalleCita;

import java.util.List;

public interface DetalleCitaRepository extends JpaRepository<DetalleCita, Long> {

    List<DetalleCita> findByCitaId(Long idCita);
}
