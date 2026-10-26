package pe.edu.upeu.clinica.citas.paciente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.clinica.citas.paciente.entity.Paciente;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByDni(String dni);

    boolean existsByDni(String dni);
}
