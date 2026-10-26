package pe.edu.upeu.clinica.pagos.pago.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pe.edu.upeu.clinica.pagos.pago.dto.DetallePagoResponse;
import pe.edu.upeu.clinica.pagos.pago.dto.DistribucionPagoResponse;
import pe.edu.upeu.clinica.pagos.pago.dto.PagoResponse;
import pe.edu.upeu.clinica.pagos.pago.entity.DetallePago;
import pe.edu.upeu.clinica.pagos.pago.entity.DistribucionPago;
import pe.edu.upeu.clinica.pagos.pago.entity.Pago;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PagoMapper {

    @Mapping(target = "estado", ignore = true)
    PagoResponse toResponse(Pago entity);

    @Mapping(target = "idMetodoPago", source = "metodoPago.id")
    DetallePagoResponse toDetalleResponse(DetallePago entity);

    @Mapping(target = "idTratamiento", source = "tratamiento.id")
    DistribucionPagoResponse toDistribucionResponse(DistribucionPago entity);
}
