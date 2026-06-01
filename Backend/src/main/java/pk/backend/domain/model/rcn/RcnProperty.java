package pk.backend.domain.model.rcn;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "rcn_properties")
@NoArgsConstructor
public class RcnProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String gmlId;

    @Column(length = 100)
    private String propertyType;

    @Column(name = "gross_price", precision = 15, scale = 2)
    private BigDecimal grossPrice;

    @Column(length = 255)
    private String parcelRef;

    @Column(length = 255)
    private String buildingRef;

    @Column(length = 255)
    private String localRef;

    @Column(length = 255)
    private String addressRef;

    @ManyToOne(fetch = FetchType.LAZY)
    private RcnParcel parcel;

    @ManyToOne(fetch = FetchType.LAZY)
    private RcnBuilding building;

    @ManyToOne(fetch = FetchType.LAZY)
    private RcnLocal local;

    @ManyToOne(fetch = FetchType.LAZY)
    private RcnAddress address;

    public RcnProperty(String gmlId) {
        this.gmlId = gmlId;
    }
}
