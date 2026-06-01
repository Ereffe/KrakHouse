package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import pk.backend.infrastructure.service.RcnImportSchemaService;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricePointBuilderService {

    private static final int PRICE_POINT_CHUNK_SIZE = 50_000;

    private static final String INSERT_PROPERTY_AREA_TOTALS = """
            INSERT INTO rcn_property_area_totals (
                property_gml_id,
                total_area_m2
            )
            SELECT
                property_parcel.source_gml_id,
                SUM(parcel.registry_area_m2)
            FROM rcn_unresolved_references property_parcel
            JOIN rcn_parcels parcel
                ON parcel.gml_id = property_parcel.target_gml_id
            WHERE property_parcel.source_type = 'RCN_Nieruchomosc'
              AND property_parcel.relation_name = 'dzialka'
              AND parcel.registry_area_m2 IS NOT NULL
              AND parcel.registry_area_m2 > 0
            GROUP BY property_parcel.source_gml_id
            """;

    private static final String INSERT_PARCEL_POINTS = """
            INSERT IGNORE INTO rcn_price_points (
                transaction_gml_id,
                property_gml_id,
                price,
                price_per_m2,
                object_type,
                object_gml_id,
                center_x,
                center_y,
                srid
            )
            SELECT
                tx.gml_id,
                property.gml_id,
                COALESCE(property.gross_price, tx.price),
                COALESCE(property.gross_price, tx.price) / property_area.total_area_m2,
                'PARCEL',
                parcel.gml_id,
                parcel.center_x,
                parcel.center_y,
                parcel.srid
            FROM rcn_properties property
            JOIN rcn_unresolved_references tx_property
                ON tx_property.source_type = 'RCN_Transakcja'
               AND tx_property.relation_name = 'nieruchomosc'
               AND tx_property.target_gml_id = property.gml_id
            JOIN rcn_transactions tx
                ON tx.gml_id = tx_property.source_gml_id
            JOIN rcn_property_area_totals property_area
                ON property_area.property_gml_id = property.gml_id
            JOIN rcn_unresolved_references property_parcel
                ON property_parcel.source_type = 'RCN_Nieruchomosc'
               AND property_parcel.relation_name = 'dzialka'
               AND property_parcel.source_gml_id = property.gml_id
            JOIN rcn_parcels parcel
                ON parcel.gml_id = property_parcel.target_gml_id
            WHERE COALESCE(property.gross_price, tx.price) IS NOT NULL
              AND parcel.center_x IS NOT NULL
              AND parcel.center_y IS NOT NULL
              AND parcel.srid = 4326
              AND property.id > ?
              AND property.id <= ?
            """;

    private static final String INSERT_BUILDING_POINTS = """
            INSERT IGNORE INTO rcn_price_points (
                transaction_gml_id,
                property_gml_id,
                price,
                price_per_m2,
                object_type,
                object_gml_id,
                center_x,
                center_y,
                srid
            )
            SELECT
                tx.gml_id,
                property.gml_id,
                COALESCE(property.gross_price, tx.price),
                COALESCE(property.gross_price, tx.price) / property_area.total_area_m2,
                'BUILDING',
                building.gml_id,
                building.center_x,
                building.center_y,
                building.srid
            FROM rcn_properties property
            JOIN rcn_unresolved_references tx_property
                ON tx_property.source_type = 'RCN_Transakcja'
               AND tx_property.relation_name = 'nieruchomosc'
               AND tx_property.target_gml_id = property.gml_id
            JOIN rcn_transactions tx
                ON tx.gml_id = tx_property.source_gml_id
            JOIN rcn_property_area_totals property_area
                ON property_area.property_gml_id = property.gml_id
            JOIN rcn_unresolved_references property_building
                ON property_building.source_type = 'RCN_Nieruchomosc'
               AND property_building.relation_name = 'budynek'
               AND property_building.source_gml_id = property.gml_id
            JOIN rcn_buildings building
                ON building.gml_id = property_building.target_gml_id
            WHERE COALESCE(property.gross_price, tx.price) IS NOT NULL
              AND building.center_x IS NOT NULL
              AND building.center_y IS NOT NULL
              AND building.srid = 4326
              AND property.id > ?
              AND property.id <= ?
            """;

    private static final String INSERT_LOCAL_POINTS = """
            INSERT IGNORE INTO rcn_price_points (
                transaction_gml_id,
                property_gml_id,
                price,
                price_per_m2,
                object_type,
                object_gml_id,
                center_x,
                center_y,
                srid
            )
            SELECT
                tx.gml_id,
                property.gml_id,
                COALESCE(local_unit.gross_price, property.gross_price, tx.price),
                COALESCE(local_unit.gross_price, property.gross_price, tx.price) / local_unit.usable_area,
                'LOCAL',
                local_unit.gml_id,
                local_unit.center_x,
                local_unit.center_y,
                local_unit.srid
            FROM rcn_properties property
            JOIN rcn_unresolved_references tx_property
                ON tx_property.source_type = 'RCN_Transakcja'
               AND tx_property.relation_name = 'nieruchomosc'
               AND tx_property.target_gml_id = property.gml_id
            JOIN rcn_transactions tx
                ON tx.gml_id = tx_property.source_gml_id
            JOIN rcn_unresolved_references property_local
                ON property_local.source_type = 'RCN_Nieruchomosc'
               AND property_local.relation_name = 'lokal'
               AND property_local.source_gml_id = property.gml_id
            JOIN rcn_locals local_unit
                ON local_unit.gml_id = property_local.target_gml_id
            WHERE COALESCE(local_unit.gross_price, property.gross_price, tx.price) IS NOT NULL
              AND local_unit.usable_area IS NOT NULL
              AND local_unit.usable_area > 0
              AND local_unit.center_x IS NOT NULL
              AND local_unit.center_y IS NOT NULL
              AND local_unit.srid = 4326
              AND property.id > ?
              AND property.id <= ?
            """;

    private static final String INSERT_FINAL_PRICE_POINTS = """
            INSERT INTO rcn_final_price_points (
                center_x,
                center_y,
                price_per_square_meter
            )
            SELECT
                center_x,
                center_y,
                price_per_m2
            FROM rcn_price_points
            WHERE center_x IS NOT NULL
              AND center_y IS NOT NULL
              AND price_per_m2 IS NOT NULL
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RcnImportSchemaService schemaService;

    public PricePointBuildResult rebuildPricePoints() {
        Instant startedAt = Instant.now();
        log.info("Rebuilding RCN price points");

        schemaService.ensurePricePointSchema();
        logSourceCounts();

        truncateTable("rcn_price_points");
        truncateTable("rcn_final_price_points");
        long propertyAreaTotals = rebuildPropertyAreaTotals();

        IdRange propertyIdRange = propertyIdRange();
        long parcelPoints = insertChunked("parcel points", INSERT_PARCEL_POINTS, propertyIdRange);
        long buildingPoints = insertChunked("building points", INSERT_BUILDING_POINTS, propertyIdRange);
        long localPoints = insertChunked("local points", INSERT_LOCAL_POINTS, propertyIdRange);
        long finalDataPoints = rebuildFinalPricePoints();

        PricePointBuildResult result = new PricePointBuildResult(parcelPoints, buildingPoints, localPoints, finalDataPoints);
        double elapsedSeconds = Math.max(1, Duration.between(startedAt, Instant.now()).toMillis()) / 1000.0;

        log.info("Finished rebuilding RCN price points: propertyAreaTotals={}, parcelPoints={}, buildingPoints={}, localPoints={}, finalDataPoints={}, totalPoints={}, elapsedSeconds={}, pointsPerSecond={}",
                propertyAreaTotals,
                result.parcelPoints(),
                result.buildingPoints(),
                result.localPoints(),
                result.finalDataPoints(),
                result.totalPoints(),
                String.format("%.1f", elapsedSeconds),
                String.format("%.1f", result.totalPoints() / elapsedSeconds));

        return result;
    }

    private long rebuildPropertyAreaTotals() {
        Instant startedAt = Instant.now();
        truncateTable("rcn_property_area_totals");
        log.info("RCN price-point phase started: property area totals");

        int inserted = jdbcTemplate.update(INSERT_PROPERTY_AREA_TOTALS);

        log.info("RCN price-point phase finished: property area totals, inserted={}, elapsedSeconds={}",
                inserted,
                formatElapsed(startedAt));
        return inserted;
    }

    private long rebuildFinalPricePoints() {
        Instant startedAt = Instant.now();
        log.info("RCN price-point phase started: final frontend table");

        int inserted = jdbcTemplate.update(INSERT_FINAL_PRICE_POINTS);

        log.info("RCN price-point phase finished: final frontend table, inserted={}, elapsedSeconds={}",
                inserted,
                formatElapsed(startedAt));
        return inserted;
    }

    private long insertChunked(String label, String sql, IdRange propertyIdRange) {
        if (propertyIdRange.isEmpty()) {
            log.info("RCN price-point phase skipped: {}, no properties found", label);
            return 0;
        }

        Instant startedAt = Instant.now();
        long lowerBound = propertyIdRange.minId() - 1;
        long maxId = propertyIdRange.maxId();
        long totalInserted = 0;
        long chunkCount = propertyIdRange.chunkCount(PRICE_POINT_CHUNK_SIZE);
        long chunkNumber = 0;

        log.info("RCN price-point phase started: {}, propertyIdRange=[{}, {}], chunkSize={}, chunks={}",
                label,
                propertyIdRange.minId(),
                propertyIdRange.maxId(),
                PRICE_POINT_CHUNK_SIZE,
                chunkCount);

        while (lowerBound < maxId) {
            long upperBound = Math.min(lowerBound + PRICE_POINT_CHUNK_SIZE, maxId);
            int inserted = jdbcTemplate.update(sql, lowerBound, upperBound);
            totalInserted += inserted;
            chunkNumber++;

            log.info("RCN price-point progress: phase={}, chunk={}/{}, propertyIdRange=({}, {}], insertedThisChunk={}, totalInserted={}, elapsedSeconds={}, rowsPerSecond={}",
                    label,
                    chunkNumber,
                    chunkCount,
                    lowerBound,
                    upperBound,
                    inserted,
                    totalInserted,
                    formatElapsed(startedAt),
                    formatRate(totalInserted, startedAt));

            lowerBound = upperBound;
        }

        log.info("RCN price-point phase finished: {}, inserted={}, elapsedSeconds={}, rowsPerSecond={}",
                label,
                totalInserted,
                formatElapsed(startedAt),
                formatRate(totalInserted, startedAt));
        return totalInserted;
    }

    private void truncateTable(String tableName) {
        log.info("Clearing RCN price-point table: {}", tableName);
        jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
    }

    private IdRange propertyIdRange() {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(MIN(id), 0), COALESCE(MAX(id), 0) FROM rcn_properties",
                (resultSet, rowNumber) -> new IdRange(resultSet.getLong(1), resultSet.getLong(2))
        );
    }

    private void logSourceCounts() {
        log.info("RCN price-point source counts: transactions={}, properties={}, parcelRefs={}, buildingRefs={}, localRefs={}, parcelsWithAreaAndCenter={}, buildingsWithCenter={}, localsWithAreaAndCenter={}",
                count("SELECT COUNT(*) FROM rcn_transactions WHERE price IS NOT NULL"),
                count("SELECT COUNT(*) FROM rcn_properties"),
                count("SELECT COUNT(*) FROM rcn_unresolved_references WHERE source_type = 'RCN_Nieruchomosc' AND relation_name = 'dzialka'"),
                count("SELECT COUNT(*) FROM rcn_unresolved_references WHERE source_type = 'RCN_Nieruchomosc' AND relation_name = 'budynek'"),
                count("SELECT COUNT(*) FROM rcn_unresolved_references WHERE source_type = 'RCN_Nieruchomosc' AND relation_name = 'lokal'"),
                count("SELECT COUNT(*) FROM rcn_parcels WHERE registry_area_m2 IS NOT NULL AND registry_area_m2 > 0 AND center_x IS NOT NULL AND center_y IS NOT NULL AND srid = 4326"),
                count("SELECT COUNT(*) FROM rcn_buildings WHERE center_x IS NOT NULL AND center_y IS NOT NULL AND srid = 4326"),
                count("SELECT COUNT(*) FROM rcn_locals WHERE usable_area IS NOT NULL AND usable_area > 0 AND center_x IS NOT NULL AND center_y IS NOT NULL AND srid = 4326"));
    }

    private long count(String sql) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class);
        return value == null ? 0 : value;
    }

    private String formatElapsed(Instant startedAt) {
        return String.format("%.1f", Math.max(1, Duration.between(startedAt, Instant.now()).toMillis()) / 1000.0);
    }

    private String formatRate(long rowCount, Instant startedAt) {
        double elapsedSeconds = Math.max(1, Duration.between(startedAt, Instant.now()).toMillis()) / 1000.0;
        return String.format("%.1f", rowCount / elapsedSeconds);
    }

    private record IdRange(long minId, long maxId) {

        boolean isEmpty() {
            return minId <= 0 || maxId <= 0 || minId > maxId;
        }

        long chunkCount(int chunkSize) {
            if (isEmpty()) {
                return 0;
            }

            return ((maxId - minId) / chunkSize) + 1;
        }
    }
}
