package pe.edu.upeu.clinica.atenciones.odontologo.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import pe.edu.upeu.clinica.atenciones.odontologo.dto.OdontologoRequest;
import pe.edu.upeu.clinica.atenciones.odontologo.dto.OdontologoResponse;
import pe.edu.upeu.clinica.atenciones.odontologo.entity.Odontologo;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OdontologoMapper {

    @Mapping(target = "id", ignore = true)
    Odontologo toEntity(OdontologoRequest request);

    OdontologoResponse toResponse(Odontologo entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Odontologo entity, OdontologoRequest request);
}
