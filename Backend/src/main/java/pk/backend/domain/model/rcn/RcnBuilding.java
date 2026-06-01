package pk.backend.domain.model.rcn;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rcn_buildings")
@NoArgsConstructor
public class RcnBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String gmlId;

    @Column(length = 255)
    private String buildingId;

    @Column(length = 100)
    private String buildingType;

    @Column(columnDefinition = "LONGTEXT")
    private String geometryText;

    @Column(name = "center_x")
    private Double centerX;

    @Column(name = "center_y")
    private Double centerY;

    private Integer srid;

    @Column(length = 255)
    private String parcelRef;

    @Column(length = 255)
    private String addressRef;

    @ManyToOne(fetch = FetchType.LAZY)
    private RcnParcel parcel;

    @ManyToOne(fetch = FetchType.LAZY)
    private RcnAddress address;

    public RcnBuilding(String gmlId) {
        this.gmlId = gmlId;
    }
}
