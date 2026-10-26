package pe.edu.upeu.clinica.tratamientos.plan.entity;

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
@Table(name = "TRATAMIENTOS")
public class Tratamiento {

    public static final String ESTADO_BORRADOR = "BORRADOR";
    public static final String ESTADO_APROBADO = "APROBADO";
    public static final String ESTADO_EN_PROCESO = "EN_PROCESO";
    public static final String ESTADO_FINALIZADO = "FINALIZADO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "ID_PACIENTE", nullable = false)
    private Long idPaciente;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "ESTADO", nullable = false, length = 20)
    private String estado;

    @Column(name = "COSTO_TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoTotal;

    @Column(name = "SALDO_PENDIENTE", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoPendiente;

    @Builder.Default
    @OneToMany(mappedBy = "tratamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleTratamiento> detalles = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = ESTADO_BORRADOR;
        }
        if (costoTotal == null) {
            costoTotal = BigDecimal.ZERO;
        }
        if (saldoPendiente == null) {
            saldoPendiente = BigDecimal.ZERO;
        }
    }

    public void agregarDetalle(DetalleTratamiento detalle) {
        detalle.setTratamiento(this);
        detalles.add(detalle);
        recalcularTotales();
    }

    public void recalcularTotales() {
        BigDecimal nuevoCosto = detalles.stream()
                .map(detalle -> detalle.getSubtotal() == null ? BigDecimal.ZERO : detalle.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal costoActual = costoTotal == null ? BigDecimal.ZERO : costoTotal;
        BigDecimal saldoActual = saldoPendiente == null ? costoActual : saldoPendiente;
        BigDecimal pagado = costoActual.subtract(saldoActual);
        this.costoTotal = nuevoCosto;
        this.saldoPendiente = nuevoCosto.subtract(pagado);
    }
}
