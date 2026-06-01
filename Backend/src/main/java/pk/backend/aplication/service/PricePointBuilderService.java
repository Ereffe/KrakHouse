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

    private static final String INSERT_PARCEL_POINTS = """
            INSERT IGNORE INTO rcn_price_points (
                transaction_gml_id,
                property_gml_id,
                price,
                object_type,
                object_gml_id,
                center_x,
                center_y,
                srid
            )
            SELECT
                tx.gml_id,
                property.gml_id,
                tx.price,
                'PARCEL',
                parcel.gml_id,
                parcel.center_x,
                parcel.center_y,
                parcel.srid
            FROM rcn_transactions tx
            JOIN rcn_unresolved_references tx_property
                ON tx_property.source_type = 'RCN_Transakcja'
               AND tx_property.relation_name = 'nieruchomosc'
               AND tx_property.source_gml_id = tx.gml_id
            JOIN rcn_properties property
                ON property.gml_id = tx_property.target_gml_id
            JOIN rcn_unresolved_references property_parcel
                ON property_parcel.source_type = 'RCN_Nieruchomosc'
               AND property_parcel.relation_name = 'dzialka'
               AND property_parcel.source_gml_id = property.gml_id
            JOIN rcn_parcels parcel
                ON parcel.gml_id = property_parcel.target_gml_id
            WHERE tx.price IS NOT NULL
              AND parcel.center_x IS NOT NULL
              AND parcel.center_y IS NOT NULL
              AND parcel.srid = 4326
            """;

    private static final String INSERT_BUILDING_POINTS = """
            INSERT IGNORE INTO rcn_price_points (
                transaction_gml_id,
                property_gml_id,
                price,
                object_type,
                object_gml_id,
                center_x,
                center_y,
                srid
            )
            SELECT
                tx.gml_id,
                property.gml_id,
                tx.price,
                'BUILDING',
                building.gml_id,
                building.center_x,
                building.center_y,
                building.srid
            FROM rcn_transactions tx
            JOIN rcn_unresolved_references tx_property
                ON tx_property.source_type = 'RCN_Transakcja'
               AND tx_property.relation_name = 'nieruchomosc'
               AND tx_property.source_gml_id = tx.gml_id
            JOIN rcn_properties property
                ON property.gml_id = tx_property.target_gml_id
            JOIN rcn_unresolved_references property_building
                ON property_building.source_type = 'RCN_Nieruchomosc'
               AND property_building.relation_name = 'budynek'
               AND property_building.source_gml_id = property.gml_id
            JOIN rcn_buildings building
                ON building.gml_id = property_building.target_gml_id
            WHERE tx.price IS NOT NULL
              AND building.center_x IS NOT NULL
              AND building.center_y IS NOT NULL
              AND building.srid = 4326
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RcnImportSchemaService schemaService;

    public PricePointBuildResult rebuildPricePoints() {
        Instant startedAt = Instant.now();
        log.info("Rebuilding RCN price points");

        schemaService.ensurePricePointSchema();
        jdbcTemplate.update("DELETE FROM rcn_price_points");
        int parcelPoints = jdbcTemplate.update(INSERT_PARCEL_POINTS);
        int buildingPoints = jdbcTemplate.update(INSERT_BUILDING_POINTS);

        PricePointBuildResult result = new PricePointBuildResult(parcelPoints, buildingPoints);
        double elapsedSeconds = Math.max(1, Duration.between(startedAt, Instant.now()).toMillis()) / 1000.0;

        log.info("Finished rebuilding RCN price points: parcelPoints={}, buildingPoints={}, totalPoints={}, elapsedSeconds={}, pointsPerSecond={}",
                result.parcelPoints(),
                result.buildingPoints(),
                result.totalPoints(),
                String.format("%.1f", elapsedSeconds),
                String.format("%.1f", result.totalPoints() / elapsedSeconds));

        return result;
    }
}
