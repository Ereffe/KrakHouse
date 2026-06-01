package pk.backend.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RcnImportSchemaService {

    private final JdbcTemplate jdbcTemplate;

    public void ensurePricePointSchema() {
        ensureColumn("rcn_parcels", "center_x", "DOUBLE NULL");
        ensureColumn("rcn_parcels", "center_y", "DOUBLE NULL");
        ensureColumn("rcn_parcels", "srid", "INT NULL");
        ensureColumn("rcn_buildings", "center_x", "DOUBLE NULL");
        ensureColumn("rcn_buildings", "center_y", "DOUBLE NULL");
        ensureColumn("rcn_buildings", "srid", "INT NULL");
        ensurePricePointTable();
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
            jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition);
        }
    }

    private void ensurePricePointTable() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS rcn_price_points (
                    id BIGINT NOT NULL AUTO_INCREMENT,
                    transaction_gml_id VARCHAR(255) NOT NULL,
                    property_gml_id VARCHAR(255) NOT NULL,
                    price DECIMAL(15, 2) NULL,
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
}
