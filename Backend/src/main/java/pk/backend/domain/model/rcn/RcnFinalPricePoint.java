package pk.backend.domain.model.rcn;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
        name = "rcn_final_price_points",
        indexes = {
                @Index(name = "idx_rcn_final_price_points_location", columnList = "center_x, center_y"),
                @Index(name = "idx_rcn_final_price_points_price", columnList = "price_per_square_meter")
        }
)
@NoArgsConstructor
public class RcnFinalPricePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Longitude in EPSG:4326, ready for Google Maps-style coordinates. */
    @Column(name = "center_x", nullable = false)
    private Double centerX;

    /** Latitude in EPSG:4326, ready for Google Maps-style coordinates. */
    @Column(name = "center_y", nullable = false)
    private Double centerY;

    @Column(name = "price_per_square_meter", nullable = false, precision = 18, scale = 4)
    private BigDecimal pricePerSquareMeter;
}
