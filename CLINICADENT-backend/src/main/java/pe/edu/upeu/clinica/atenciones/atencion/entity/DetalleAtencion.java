package pe.edu.upeu.clinica.atenciones.atencion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.clinica.tratamientos.catalogo.entity.Procedimiento;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DETALLE_ATENCIONES")
public class DetalleAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ATENCION", nullable = false)
    private Atencion atencion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PROCEDIMIENTO")
    private Procedimiento procedimiento;

    @Column(name = "PIEZA_DENTAL", nullable = false, length = 10)
    private String piezaDental;

    @Column(name = "INTERVENCION", nullable = false, length = 255)
    private String intervencion;

    @Column(name = "OBSERVACION", length = 255)
    private String observacion;
}
