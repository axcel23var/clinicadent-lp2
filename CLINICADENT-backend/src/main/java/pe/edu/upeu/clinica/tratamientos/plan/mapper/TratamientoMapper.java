package pe.edu.upeu.clinica.tratamientos.plan.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pe.edu.upeu.clinica.tratamientos.plan.dto.DetalleTratamientoResponse;
import pe.edu.upeu.clinica.tratamientos.plan.dto.TratamientoRequest;
import pe.edu.upeu.clinica.tratamientos.plan.dto.TratamientoResponse;
import pe.edu.upeu.clinica.tratamientos.plan.entity.DetalleTratamiento;
import pe.edu.upeu.clinica.tratamientos.plan.entity.Tratamiento;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TratamientoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "costoTotal", ignore = true)
    @Mapping(target = "saldoPendiente", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    Tratamiento toEntity(TratamientoRequest request);

    TratamientoResponse toResponse(Tratamiento entity);

    @Mapping(target = "idProcedimiento", source = "procedimiento.id")
    DetalleTratamientoResponse toDetalleResponse(DetalleTratamiento entity);
}
