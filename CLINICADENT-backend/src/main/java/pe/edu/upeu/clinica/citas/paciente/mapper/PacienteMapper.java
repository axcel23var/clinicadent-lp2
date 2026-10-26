package pe.edu.upeu.clinica.citas.paciente.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import pe.edu.upeu.clinica.citas.paciente.dto.PacienteRequest;
import pe.edu.upeu.clinica.citas.paciente.dto.PacienteResponse;
import pe.edu.upeu.clinica.citas.paciente.entity.Paciente;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PacienteMapper {

    @Mapping(target = "id", ignore = true)
    Paciente toEntity(PacienteRequest request);

    PacienteResponse toResponse(Paciente entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Paciente entity, PacienteRequest request);
}
