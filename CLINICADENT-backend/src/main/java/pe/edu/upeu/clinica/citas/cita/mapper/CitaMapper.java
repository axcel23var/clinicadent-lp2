package pe.edu.upeu.clinica.citas.cita.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pe.edu.upeu.clinica.citas.cita.dto.CitaRequest;
import pe.edu.upeu.clinica.citas.cita.dto.CitaResponse;
import pe.edu.upeu.clinica.citas.cita.dto.DetalleCitaRequest;
import pe.edu.upeu.clinica.citas.cita.dto.DetalleCitaResponse;
import pe.edu.upeu.clinica.citas.cita.entity.Cita;
import pe.edu.upeu.clinica.citas.cita.entity.DetalleCita;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CitaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    Cita toEntity(CitaRequest request);

    CitaResponse toResponse(Cita entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cita", ignore = true)
    @Mapping(target = "procedimiento", ignore = true)
    @Mapping(target = "motivoConsulta", ignore = true)
    DetalleCita toDetalleEntity(DetalleCitaRequest request);

    @Mapping(target = "idProcedimiento", source = "procedimiento.id")
    DetalleCitaResponse toDetalleResponse(DetalleCita entity);
}
