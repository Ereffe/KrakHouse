package pk.backend.domain.model.rcn;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
        name = "rcn_price_points",
        indexes = {
                @Index(name = "idx_rcn_price_points_object", columnList = "object_type, object_gml_id"),
                @Index(name = "idx_rcn_price_points_location", columnList = "center_x, center_y")
        },
        uniqueConstraints = @UniqueConstraint(
                name = "uk_rcn_price_point",
                columnNames = {"transaction_gml_id", "property_gml_id", "object_type", "object_gml_id"}
        )
)
@NoArgsConstructor
public class RcnPricePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_gml_id", nullable = false, length = 255)
    private String transactionGmlId;

    @Column(name = "property_gml_id", nullable = false, length = 255)
    private String propertyGmlId;

    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "price_per_m2", nullable = false, precision = 18, scale = 4)
    private BigDecimal pricePerM2;

    @Column(name = "object_type", nullable = false, length = 32)
    private String objectType;

    @Column(name = "object_gml_id", nullable = false, length = 255)
    private String objectGmlId;

    /** Longitude in EPSG:4326, ready for Google Maps-style coordinates. */
    @Column(name = "center_x", nullable = false)
    private Double centerX;

    /** Latitude in EPSG:4326, ready for Google Maps-style coordinates. */
    @Column(name = "center_y", nullable = false)
    private Double centerY;

    @Column(nullable = false)
    private Integer srid;
}
