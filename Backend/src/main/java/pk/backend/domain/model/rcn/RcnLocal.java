package pk.backend.domain.model.rcn;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "rcn_locals")
@NoArgsConstructor
public class RcnLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String gmlId;

    @Column(length = 100)
    private String localNumber;

    @Column(precision = 12, scale = 2)
    private BigDecimal usableArea;

    @Column(length = 255)
    private String buildingRef;

    @Column(length = 255)
    private String addressRef;

    @ManyToOne(fetch = FetchType.LAZY)
    private RcnBuilding building;

    @ManyToOne(fetch = FetchType.LAZY)
    private RcnAddress address;

    public RcnLocal(String gmlId) {
        this.gmlId = gmlId;
    }
}