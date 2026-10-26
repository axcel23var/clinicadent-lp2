package pe.edu.upeu.clinica.tratamientos.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.clinica.tratamientos.plan.entity.Tratamiento;

import java.util.List;

public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    List<Tratamiento> findByIdPaciente(Long idPaciente);
}
