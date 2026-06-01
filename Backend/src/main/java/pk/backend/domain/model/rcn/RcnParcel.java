package pk.backend.domain.model.rcn;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "rcn_parcels")
@NoArgsConstructor
public class RcnParcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String gmlId;

    @Column(length = 255)
    private String parcelId;

    @Column(length = 255)
    private String precinct;

    @Column(columnDefinition = "LONGTEXT")
    private String geometryText;

    @Column(name = "registry_area_m2", precision = 18, scale = 4)
    private BigDecimal registryAreaM2;

    @Column(name = "center_x")
    private Double centerX;

    @Column(name = "center_y")
    private Double centerY;

    private Integer srid;

    @Column(length = 255)
    private String addressRef;

    @ManyToOne(fetch = FetchType.LAZY)
    private RcnAddress address;

    public RcnParcel(String gmlId) {
        this.gmlId = gmlId;
    }
}
