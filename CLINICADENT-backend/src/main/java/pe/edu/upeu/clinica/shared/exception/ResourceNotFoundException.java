package pe.edu.upeu.clinica.shared.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String recurso, Long id) {
        super("%s no encontrado con id %d".formatted(recurso, id));
    }
}
