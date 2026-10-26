package pe.edu.upeu.clinica.citas.cita.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CITAS")
public class Cita {

    public static final String ESTADO_PROGRAMADA = "PROGRAMADA";
    public static final String ESTADO_ATENDIDA = "ATENDIDA";
    public static final String ESTADO_CANCELADA = "CANCELADA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "ID_PACIENTE", nullable = false)
    private Long idPaciente;

    @Column(name = "FECHA_HORA", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "ESTADO", nullable = false, length = 20)
    private String estado;

    @Builder.Default
    @OneToMany(mappedBy = "cita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCita> detalles = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (estado == null) {
            estado = ESTADO_PROGRAMADA;
        }
    }

    public void agregarDetalle(DetalleCita detalle) {
        detalle.setCita(this);
        detalles.add(detalle);
    }
}
