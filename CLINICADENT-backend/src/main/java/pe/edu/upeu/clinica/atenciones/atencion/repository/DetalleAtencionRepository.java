package pe.edu.upeu.clinica.atenciones.atencion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.clinica.atenciones.atencion.entity.DetalleAtencion;

import java.util.List;

public interface DetalleAtencionRepository extends JpaRepository<DetalleAtencion, Long> {

    List<DetalleAtencion> findByAtencionId(Long idAtencion);
}
