package pe.edu.upeu.clinica.atenciones.atencion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.clinica.atenciones.atencion.entity.Atencion;

import java.time.LocalDateTime;
import java.util.List;

public interface AtencionRepository extends JpaRepository<Atencion, Long> {

    List<Atencion> findByIdCita(Long idCita);

    List<Atencion> findByIdOdontologo(Long idOdontologo);

    List<Atencion> findByFaAtencionGreaterThanEqualAndFaAtencionLessThan(
            LocalDateTime desdeInclusive,
            LocalDateTime hastaExclusivo);
}
