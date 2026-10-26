package pe.edu.upeu.clinica.atenciones.atencion.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pe.edu.upeu.clinica.atenciones.atencion.dto.AtencionRequest;
import pe.edu.upeu.clinica.atenciones.atencion.dto.AtencionResponse;
import pe.edu.upeu.clinica.atenciones.atencion.dto.DetalleAtencionRequest;
import pe.edu.upeu.clinica.atenciones.atencion.dto.DetalleAtencionResponse;
import pe.edu.upeu.clinica.atenciones.atencion.entity.Atencion;
import pe.edu.upeu.clinica.atenciones.atencion.entity.DetalleAtencion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AtencionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "faAtencion", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    Atencion toEntity(AtencionRequest request);

    AtencionResponse toResponse(Atencion entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "atencion", ignore = true)
    @Mapping(target = "procedimiento", ignore = true)
    @Mapping(target = "intervencion", ignore = true)
    DetalleAtencion toDetalleEntity(DetalleAtencionRequest request);

    @Mapping(target = "idProcedimiento", source = "procedimiento.id")
    DetalleAtencionResponse toDetalleResponse(DetalleAtencion entity);
}
