package pe.edu.upeu.clinica.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    Instant timestamp;
    int status;
    String error;
    String message;
    String path;
    List<CampoInvalido> camposInvalidos;

    @Value
    @Builder
    public static class CampoInvalido {
        String campo;
        String mensaje;
        Object valorRechazado;
    }
}
