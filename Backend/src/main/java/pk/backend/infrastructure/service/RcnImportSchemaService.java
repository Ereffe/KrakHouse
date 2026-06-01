package pk.backend.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RcnImportSchemaService {

    private final JdbcTemplate jdbcTemplate;

    public void ensurePricePointSchema() {
        ensureColumn("rcn_properties", "gross_price", "DECIMAL(15, 2) NULL");
        ensureColumn("rcn_locals", "gross_price", "DECIMAL(15, 2) NULL");
        ensureColumn("rcn_locals", "geometry_text", "LONGTEXT NULL");
        ensureColumn("rcn_locals", "center_x", "DOUBLE NULL");
        ensureColumn("rcn_locals", "center_y", "DOUBLE NULL");
        ensureColumn("rcn_locals", "srid", "INT NULL");
        ensureColumn("rcn_parcels", "registry_area_m2", "DECIMAL(18, 4) NULL");
        ensureColumn("rcn_parcels", "center_x", "DOUBLE NULL");
        ensureColumn("rcn_parcels", "center_y", "DOUBLE NULL");
        ensureColumn("rcn_parcels", "srid", "INT NULL");
        ensureColumn("rcn_buildings", "center_x", "DOUBLE NULL");
        ensureColumn("rcn_buildings", "center_y", "DOUBLE NULL");
        ensureColumn("rcn_buildings", "srid", "INT NULL");
        ensurePricePointTable();
        ensureColumn("rcn_price_points", "price_per_m2", "DECIMAL(18, 4) NULL");
        ensureFinalPricePointTable();
        ensurePropertyAreaTotalsTable();
        ensureImportIndexes();
    }

    private void ensureColumn(String tableName, String columnName, String columnDefinition) {
        Integer columnCount = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM information_schema.columns
                        WHERE table_schema = DATABASE()
                          AND table_name = ?
                          AND column_name = ?
                        """,
                Integer.class,
                tableName,
                columnName
        );

        if (columnCount == null || columnCount == 0) {
            log.info("Adding missing RCN import column: table={}, column={}", tableName, columnName);
            jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition);
        }
    }

    private void ensureIndex(String tableName, String indexName, String columnList) {
        Integer indexCount = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM information_schema.statistics
                        WHERE table_schema = DATABASE()
                          AND table_name = ?
                          AND index_name = ?
                        """,
                Integer.class,
                tableName,
                indexName
        );

        if (indexCount == null || indexCount == 0) {
            log.info("Creating RCN import index: table={}, index={}, columns={}", tableName, indexName, columnList);
            jdbcTemplate.execute("CREATE INDEX " + indexName + " ON " + tableName + " (" + columnList + ")");
        }
    }

    private void ensurePricePointTable() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS rcn_price_points (
                    id BIGINT NOT NULL AUTO_INCREMENT,
                    transaction_gml_id VARCHAR(255) NOT NULL,
                    property_gml_id VARCHAR(255) NOT NULL,
                    price DECIMAL(15, 2) NULL,
                    price_per_m2 DECIMAL(18, 4) NOT NULL,
                    object_type VARCHAR(32) NOT NULL,
                    object_gml_id VARCHAR(255) NOT NULL,
                    center_x DOUBLE NOT NULL,
                    center_y DOUBLE NOT NULL,
                    srid INT NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_rcn_price_point (
                        transaction_gml_id,
                        property_gml_id,
                        object_type,
                        object_gml_id
                    ),
                    KEY idx_rcn_price_points_object (object_type, object_gml_id),
                    KEY idx_rcn_price_points_location (center_x, center_y)
                )
                """);
    }

    private void ensureFinalPricePointTable() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS rcn_final_price_points (
                    id BIGINT NOT NULL AUTO_INCREMENT,
                    center_x DOUBLE NOT NULL,
                    center_y DOUBLE NOT NULL,
                    price_per_square_meter DECIMAL(18, 4) NOT NULL,
                    PRIMARY KEY (id),
                    KEY idx_rcn_final_price_points_location (center_x, center_y),
                    KEY idx_rcn_final_price_points_price (price_per_square_meter)
                )
                """);
    }

    private void ensurePropertyAreaTotalsTable() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS rcn_property_area_totals (
                    property_gml_id VARCHAR(255) NOT NULL,
                    total_area_m2 DECIMAL(18, 4) NOT NULL,
                    PRIMARY KEY (property_gml_id),
                    KEY idx_rcn_property_area_totals_area (total_area_m2)
                )
                """);
    }

    private void ensureImportIndexes() {
        ensureIndex(
                "rcn_unresolved_references",
                "idx_rcn_refs_type_relation_source_target",
                "source_type, relation_name, source_gml_id, target_gml_id"
        );
        ensureIndex(
                "rcn_unresolved_references",
                "idx_rcn_refs_type_relation_target",
                "source_type, relation_name, target_gml_id"
        );
        ensureIndex("rcn_parcels", "idx_rcn_parcels_gml_area", "gml_id, registry_area_m2");
        ensureIndex("rcn_buildings", "idx_rcn_buildings_gml_location", "gml_id, srid, center_x, center_y");
        ensureIndex("rcn_locals", "idx_rcn_locals_gml_area_location", "gml_id, usable_area, srid, center_x, center_y");
        ensureIndex("rcn_properties", "idx_rcn_properties_id_gml", "id, gml_id");
        ensureIndex("rcn_properties", "idx_rcn_properties_gml", "gml_id");
        ensureIndex("rcn_transactions", "idx_rcn_transactions_gml_price", "gml_id, price");
        ensureIndex("rcn_price_points", "idx_rcn_price_points_price_location", "price_per_m2, center_x, center_y");
    }
}
