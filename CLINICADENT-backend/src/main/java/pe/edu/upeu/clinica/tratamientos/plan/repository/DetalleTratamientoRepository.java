package pe.edu.upeu.clinica.tratamientos.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.clinica.tratamientos.plan.entity.DetalleTratamiento;

import java.util.List;

public interface DetalleTratamientoRepository extends JpaRepository<DetalleTratamiento, Long> {

    List<DetalleTratamiento> findByTratamientoId(Long idTratamiento);
}
