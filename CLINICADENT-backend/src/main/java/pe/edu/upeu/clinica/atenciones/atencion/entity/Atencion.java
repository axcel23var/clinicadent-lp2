package pe.edu.upeu.clinica.atenciones.atencion.entity;

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
@Table(name = "ATENCIONES")
public class Atencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "ID_CITA", nullable = false)
    private Long idCita;

    @Column(name = "ID_ODONTOLOGO", nullable = false)
    private Long idOdontologo;

    @Column(name = "FA_ATENCION", nullable = false)
    private LocalDateTime faAtencion;

    @Column(name = "DIAGNOSTICO", nullable = false, length = 500)
    private String diagnostico;

    @Column(name = "NOTA_MEDICA", length = 500)
    private String notaMedica;

    @Builder.Default
    @OneToMany(mappedBy = "atencion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleAtencion> detalles = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (faAtencion == null) {
            faAtencion = LocalDateTime.now();
        }
    }

    public void agregarDetalle(DetalleAtencion detalle) {
        detalle.setAtencion(this);
        detalles.add(detalle);
    }
}
