package pe.edu.upeu.clinica.pagos.pago.entity;

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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PAGOS")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "ID_TRATAMIENTO", nullable = false)
    private Long idTratamiento;

    @Column(name = "FECHA_PAGO", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "MONTO_TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Builder.Default
    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePago> detalles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DistribucionPago> distribuciones = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (fechaPago == null) {
            fechaPago = LocalDateTime.now();
        }
        if (montoTotal == null) {
            montoTotal = BigDecimal.ZERO;
        }
    }

    public void agregarDetalle(DetallePago detalle) {
        detalle.setPago(this);
        detalles.add(detalle);
        recalcularMontoTotal();
    }

    public void agregarDistribucion(DistribucionPago distribucion) {
        distribucion.setPago(this);
        distribuciones.add(distribucion);
    }

    @SuppressWarnings("null")
    public void recalcularMontoTotal() {
        this.montoTotal = detalles.stream()
                .map(item -> item.getMontoAbonado() == null ? BigDecimal.ZERO : item.getMontoAbonado())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.fechaPago = LocalDateTime.now();
    }
}
