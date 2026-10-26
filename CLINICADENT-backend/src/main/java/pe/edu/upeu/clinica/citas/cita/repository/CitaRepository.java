package pe.edu.upeu.clinica.citas.cita.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.clinica.citas.cita.entity.Cita;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByIdPaciente(Long idPaciente);

    List<Cita> findByFechaHoraGreaterThanEqualAndFechaHoraLessThan(
            LocalDateTime desdeInclusive,
            LocalDateTime hastaExclusivo);
}
