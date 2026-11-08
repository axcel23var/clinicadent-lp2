package pe.edu.upeu.clinica.pagos.pago.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import pe.edu.upeu.clinica.pagos.pago.dto.MetodoPagoRequest;
import pe.edu.upeu.clinica.pagos.pago.dto.MetodoPagoResponse;
import pe.edu.upeu.clinica.pagos.pago.entity.MetodoPago;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MetodoPagoMapper {

    @Mapping(target = "id", ignore = true)
    MetodoPago toEntity(MetodoPagoRequest request);

    MetodoPagoResponse toResponse(MetodoPago entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget MetodoPago entity, MetodoPagoRequest request);
}
