package pe.edu.upeu.clinica.atenciones.odontologo.service;

import pe.edu.upeu.clinica.atenciones.odontologo.dto.OdontologoRequest;
import pe.edu.upeu.clinica.atenciones.odontologo.dto.OdontologoResponse;

import java.util.List;

public interface OdontologoService {

    OdontologoResponse crear(OdontologoRequest request);

    OdontologoResponse obtenerPorId(Long id);

    List<OdontologoResponse> listar();

    OdontologoResponse actualizar(Long id, OdontologoRequest request);

    void eliminar(Long id);
}
