package pe.edu.upeu.clinica.tratamientos.catalogo.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import pe.edu.upeu.clinica.tratamientos.catalogo.dto.ProcedimientoRequest;
import pe.edu.upeu.clinica.tratamientos.catalogo.dto.ProcedimientoResponse;
import pe.edu.upeu.clinica.tratamientos.catalogo.entity.Procedimiento;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProcedimientoMapper {

    @Mapping(target = "id", ignore = true)
    Procedimiento toEntity(ProcedimientoRequest request);

    ProcedimientoResponse toResponse(Procedimiento entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Procedimiento entity, ProcedimientoRequest request);
}
