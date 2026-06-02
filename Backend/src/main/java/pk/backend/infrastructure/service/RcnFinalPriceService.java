package pk.backend.infrastructure.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import pk.backend.infrastructure.model.DiscreteData;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class RcnFinalPriceService {

    private static final BigDecimal MIN_PRICE_PER_SQUARE_METER = BigDecimal.valueOf(5_000);
    private static final BigDecimal MAX_PRICE_PER_SQUARE_METER = BigDecimal.valueOf(60_000);

    private static final String CLEANUP_INVALID_PRICE_POINTS_QUERY = """
            DELETE FROM rcn_final_price_points
            WHERE center_x IS NULL
               OR center_y IS NULL
               OR price_per_square_meter IS NULL
               OR price_per_square_meter < ?
               OR price_per_square_meter > ?
            """;

    private static final String PRICE_POINTS_QUERY = """
            SELECT center_x, center_y, price_per_square_meter
            FROM rcn_final_price_points
            WHERE center_x IS NOT NULL
              AND center_y IS NOT NULL
              AND price_per_square_meter IS NOT NULL
              AND price_per_square_meter >= ?
              AND price_per_square_meter <= ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final AtomicBoolean invalidPriceDataCleaned = new AtomicBoolean(false);

    @Getter
    @Value("${rcn.price.data-provider}")
    private String dataProvider;

    public List<DiscreteData<BigDecimal>> getPriceData() {
        cleanupInvalidPriceData();

        List<DiscreteData<BigDecimal>> priceData = jdbcTemplate.query(
                PRICE_POINTS_QUERY,
                (resultSet, rowNumber) -> DiscreteData.<BigDecimal>builder()
                        .longitude(resultSet.getDouble("center_x"))
                        .latitude(resultSet.getDouble("center_y"))
                        .value(resultSet.getBigDecimal("price_per_square_meter"))
                        .build(),
                MIN_PRICE_PER_SQUARE_METER,
                MAX_PRICE_PER_SQUARE_METER
        );

        if (priceData.isEmpty()) {
            throw new IllegalStateException("No RCN final price data available. Run the RCN import before requesting the price map.");
        }

        log.info("Loaded RCN final price data points: {}", priceData.size());
        return priceData;
    }

    private void cleanupInvalidPriceData() {
        if (!invalidPriceDataCleaned.compareAndSet(false, true)) {
            return;
        }

        int deleted = jdbcTemplate.update(
                CLEANUP_INVALID_PRICE_POINTS_QUERY,
                MIN_PRICE_PER_SQUARE_METER,
                MAX_PRICE_PER_SQUARE_METER
        );

        if (deleted > 0) {
            log.info("Removed invalid RCN final price data points: deleted={}, minPricePerSquareMeter={}, maxPricePerSquareMeter={}",
                    deleted,
                    MIN_PRICE_PER_SQUARE_METER,
                    MAX_PRICE_PER_SQUARE_METER);
        }
    }
}
