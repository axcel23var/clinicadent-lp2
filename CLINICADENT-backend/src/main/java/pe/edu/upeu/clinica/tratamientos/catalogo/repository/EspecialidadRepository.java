package pe.edu.upeu.clinica.tratamientos.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.clinica.tratamientos.catalogo.entity.Especialidad;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {

    boolean existsByNombre(String nombre);

    boolean existsByNombreAndIdNot(String nombre, Long id);
}
