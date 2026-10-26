package pe.edu.upeu.clinica.tratamientos.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.clinica.tratamientos.catalogo.entity.Procedimiento;

import java.util.List;

public interface ProcedimientoRepository extends JpaRepository<Procedimiento, Long> {

    boolean existsByNombre(String nombre);

    boolean existsByNombreAndIdNot(String nombre, Long id);

    List<Procedimiento> findByIdEspecialidad(Long idEspecialidad);
}
