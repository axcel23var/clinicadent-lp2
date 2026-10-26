package pe.edu.upeu.clinica.atenciones.atencion.service;

import pe.edu.upeu.clinica.atenciones.atencion.dto.AtencionRequest;
import pe.edu.upeu.clinica.atenciones.atencion.dto.AtencionResponse;

import java.util.List;

public interface AtencionService {

    AtencionResponse registrarAtencion(AtencionRequest request);

    AtencionResponse obtenerPorId(Long id);

    List<AtencionResponse> listar();

    List<AtencionResponse> listarPorCita(Long idCita);
}
