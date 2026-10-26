package pe.edu.upeu.clinica.atenciones.odontologo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.clinica.atenciones.odontologo.entity.Odontologo;

import java.util.Optional;

public interface OdontologoRepository extends JpaRepository<Odontologo, Long> {

    Optional<Odontologo> findByCop(String cop);

    boolean existsByCop(String cop);

    boolean existsByCopAndIdNot(String cop, Long id);
}
