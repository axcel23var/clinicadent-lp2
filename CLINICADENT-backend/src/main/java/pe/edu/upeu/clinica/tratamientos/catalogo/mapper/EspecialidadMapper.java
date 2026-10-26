package pe.edu.upeu.clinica.tratamientos.catalogo.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import pe.edu.upeu.clinica.tratamientos.catalogo.dto.EspecialidadRequest;
import pe.edu.upeu.clinica.tratamientos.catalogo.dto.EspecialidadResponse;
import pe.edu.upeu.clinica.tratamientos.catalogo.entity.Especialidad;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EspecialidadMapper {

    @Mapping(target = "id", ignore = true)
    Especialidad toEntity(EspecialidadRequest request);

    EspecialidadResponse toResponse(Especialidad entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Especialidad entity, EspecialidadRequest request);
}
