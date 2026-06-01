package pk.backend.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import pk.backend.infrastructure.config.ImportProperties;
import pk.backend.infrastructure.dto.rcn.RcnAddressDto;
import pk.backend.infrastructure.dto.rcn.RcnBuildingDto;
import pk.backend.infrastructure.dto.rcn.RcnLocalDto;
import pk.backend.infrastructure.dto.rcn.RcnObjectDto;
import pk.backend.infrastructure.dto.rcn.RcnParcelDto;
import pk.backend.infrastructure.dto.rcn.RcnPropertyDto;
import pk.backend.infrastructure.dto.rcn.RcnTransactionDto;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RcnJdbcBatchWriter {

    private static final String TRANSACTION_SQL = """
            INSERT INTO rcn_transactions (gml_id, transaction_code, price, property_ref)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                transaction_code = VALUES(transaction_code),
                price = VALUES(price),
                property_ref = VALUES(property_ref)
            """;

    private static final String PROPERTY_SQL = """
            INSERT INTO rcn_properties (gml_id, property_type, parcel_ref, building_ref, local_ref)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                property_type = VALUES(property_type),
                parcel_ref = VALUES(parcel_ref),
                building_ref = VALUES(building_ref),
                local_ref = VALUES(local_ref)
            """;

    private static final String LOCAL_SQL = """
            INSERT INTO rcn_locals (gml_id, local_number, usable_area, address_ref)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                local_number = VALUES(local_number),
                usable_area = VALUES(usable_area),
                address_ref = VALUES(address_ref)
            """;

    private static final String PARCEL_SQL = """
            INSERT INTO rcn_parcels (gml_id, parcel_id, precinct, address_ref, geometry_text, center_x, center_y, srid)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                parcel_id = VALUES(parcel_id),
                precinct = VALUES(precinct),
                address_ref = VALUES(address_ref),
                geometry_text = VALUES(geometry_text),
                center_x = VALUES(center_x),
                center_y = VALUES(center_y),
                srid = VALUES(srid)
            """;

    private static final String BUILDING_SQL = """
            INSERT INTO rcn_buildings (gml_id, building_id, building_type, address_ref, geometry_text, center_x, center_y, srid)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                building_id = VALUES(building_id),
                building_type = VALUES(building_type),
                address_ref = VALUES(address_ref),
                geometry_text = VALUES(geometry_text),
                center_x = VALUES(center_x),
                center_y = VALUES(center_y),
                srid = VALUES(srid)
            """;

    private static final String ADDRESS_SQL = """
            INSERT INTO rcn_addresses (gml_id, city, street, building_number)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                city = VALUES(city),
                street = VALUES(street),
                building_number = VALUES(building_number)
            """;

    private static final String REFERENCE_SQL = """
            INSERT IGNORE INTO rcn_unresolved_references
                (source_type, source_gml_id, relation_name, target_gml_id, created_at)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
            """;

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final ImportProperties properties;
    private final RcnImportSchemaService schemaService;

    private final List<RcnTransactionDto> transactions = new ArrayList<>();
    private final List<RcnPropertyDto> propertiesBuffer = new ArrayList<>();
    private final List<RcnLocalDto> locals = new ArrayList<>();
    private final List<RcnParcelDto> parcels = new ArrayList<>();
    private final List<RcnBuildingDto> buildings = new ArrayList<>();
    private final List<RcnAddressDto> addresses = new ArrayList<>();
    private final Set<ReferenceCandidate> references = new LinkedHashSet<>();

    public void prepareImport() {
        schemaService.ensurePricePointSchema();
        flush();
        jdbcTemplate.update("DELETE FROM rcn_unresolved_references");
        log.info("Cleared unresolved RCN reference candidates before import");
    }

    public synchronized void accept(RcnObjectDto dto) {
        if (dto == null || isBlank(dto.gmlId())) {
            return;
        }

        if (dto instanceof RcnTransactionDto transactionDto) {
            transactions.add(transactionDto);
            addReference("RCN_Transakcja", transactionDto.gmlId(), "nieruchomosc", transactionDto.propertyRef());
            flushIfNeeded(transactions);
        } else if (dto instanceof RcnPropertyDto propertyDto) {
            propertiesBuffer.add(propertyDto);
            addReferences("RCN_Nieruchomosc", propertyDto.gmlId(), "dzialka", propertyDto.parcelRefs());
            addReferences("RCN_Nieruchomosc", propertyDto.gmlId(), "budynek", propertyDto.buildingRefs());
            addReferences("RCN_Nieruchomosc", propertyDto.gmlId(), "lokal", propertyDto.localRefs());
            flushIfNeeded(propertiesBuffer);
        } else if (dto instanceof RcnLocalDto localDto) {
            locals.add(localDto);
            addReference("RCN_Lokal", localDto.gmlId(), "adresBudynkuZLokalem", localDto.addressRef());
            flushIfNeeded(locals);
        } else if (dto instanceof RcnParcelDto parcelDto) {
            parcels.add(parcelDto);
            addReferences("RCN_Dzialka", parcelDto.gmlId(), "adresDzialki", parcelDto.addressRefs());
            flushIfNeeded(parcels);
        } else if (dto instanceof RcnBuildingDto buildingDto) {
            buildings.add(buildingDto);
            addReference("RCN_Budynek", buildingDto.gmlId(), "adresBudynku", buildingDto.addressRef());
            flushIfNeeded(buildings);
        } else if (dto instanceof RcnAddressDto addressDto) {
            addresses.add(addressDto);
            flushIfNeeded(addresses);
        } else {
            throw new IllegalArgumentException("Unsupported RCN DTO type: " + dto.getClass().getName());
        }

        flushReferencesIfNeeded();
    }

    public synchronized void flush() {
        transactionTemplate.executeWithoutResult(status -> {
            flushTransactions();
            flushProperties();
            flushLocals();
            flushParcels();
            flushBuildings();
            flushAddresses();
            flushReferences();
        });
    }

    private void flushIfNeeded(Collection<?> buffer) {
        if (buffer.size() >= batchSize()) {
            flush();
        }
    }

    private void flushReferencesIfNeeded() {
        if (references.size() >= batchSize()) {
            flush();
        }
    }

    private void flushTransactions() {
        if (transactions.isEmpty()) {
            return;
        }

        List<RcnTransactionDto> batch = new ArrayList<>(transactions);
        jdbcTemplate.batchUpdate(TRANSACTION_SQL, batch, batchSize(), this::bindTransaction);
        transactions.clear();
    }

    private void flushProperties() {
        if (propertiesBuffer.isEmpty()) {
            return;
        }

        List<RcnPropertyDto> batch = new ArrayList<>(propertiesBuffer);
        jdbcTemplate.batchUpdate(PROPERTY_SQL, batch, batchSize(), this::bindProperty);
        propertiesBuffer.clear();
    }

    private void flushLocals() {
        if (locals.isEmpty()) {
            return;
        }

        List<RcnLocalDto> batch = new ArrayList<>(locals);
        jdbcTemplate.batchUpdate(LOCAL_SQL, batch, batchSize(), this::bindLocal);
        locals.clear();
    }

    private void flushParcels() {
        if (parcels.isEmpty()) {
            return;
        }

        List<RcnParcelDto> batch = new ArrayList<>(parcels);
        jdbcTemplate.batchUpdate(PARCEL_SQL, batch, batchSize(), this::bindParcel);
        parcels.clear();
    }

    private void flushBuildings() {
        if (buildings.isEmpty()) {
            return;
        }

        List<RcnBuildingDto> batch = new ArrayList<>(buildings);
        jdbcTemplate.batchUpdate(BUILDING_SQL, batch, batchSize(), this::bindBuilding);
        buildings.clear();
    }

    private void flushAddresses() {
        if (addresses.isEmpty()) {
            return;
        }

        List<RcnAddressDto> batch = new ArrayList<>(addresses);
        jdbcTemplate.batchUpdate(ADDRESS_SQL, batch, batchSize(), this::bindAddress);
        addresses.clear();
    }

    private void flushReferences() {
        if (references.isEmpty()) {
            return;
        }

        List<ReferenceCandidate> batch = new ArrayList<>(references);
        jdbcTemplate.batchUpdate(REFERENCE_SQL, batch, batchSize(), this::bindReference);
        references.clear();
    }

    private void bindTransaction(PreparedStatement statement, RcnTransactionDto dto) throws SQLException {
        statement.setString(1, dto.gmlId());
        statement.setString(2, dto.transactionCode());
        statement.setBigDecimal(3, dto.grossPrice());
        statement.setString(4, dto.propertyRef());
    }

    private void bindProperty(PreparedStatement statement, RcnPropertyDto dto) throws SQLException {
        statement.setString(1, dto.gmlId());
        statement.setString(2, dto.propertyType());
        statement.setString(3, firstRef(dto.parcelRefs()));
        statement.setString(4, firstRef(dto.buildingRefs()));
        statement.setString(5, firstRef(dto.localRefs()));
    }

    private void bindLocal(PreparedStatement statement, RcnLocalDto dto) throws SQLException {
        statement.setString(1, dto.gmlId());
        statement.setString(2, dto.localId());
        statement.setBigDecimal(3, dto.usableArea());
        statement.setString(4, dto.addressRef());
    }

    private void bindParcel(PreparedStatement statement, RcnParcelDto dto) throws SQLException {
        statement.setString(1, dto.gmlId());
        statement.setString(2, dto.parcelId());
        statement.setString(3, dto.zoning());
        statement.setString(4, firstRef(dto.addressRefs()));
        statement.setString(5, dto.geometryText());
        setDouble(statement, 6, dto.centerX());
        setDouble(statement, 7, dto.centerY());
        setInteger(statement, 8, dto.srid());
    }

    private void bindBuilding(PreparedStatement statement, RcnBuildingDto dto) throws SQLException {
        statement.setString(1, dto.gmlId());
        statement.setString(2, dto.buildingId());
        statement.setString(3, dto.buildingType());
        statement.setString(4, dto.addressRef());
        statement.setString(5, dto.geometryText());
        setDouble(statement, 6, dto.centerX());
        setDouble(statement, 7, dto.centerY());
        setInteger(statement, 8, dto.srid());
    }

    private void bindAddress(PreparedStatement statement, RcnAddressDto dto) throws SQLException {
        statement.setString(1, dto.gmlId());
        statement.setString(2, dto.city());
        statement.setString(3, dto.street());
        statement.setString(4, dto.buildingNumber());
    }

    private void bindReference(PreparedStatement statement, ReferenceCandidate reference) throws SQLException {
        statement.setString(1, reference.sourceType());
        statement.setString(2, reference.sourceGmlId());
        statement.setString(3, reference.relationName());
        statement.setString(4, reference.targetGmlId());
    }

    private void addReferences(String sourceType, String sourceGmlId, String relationName, Collection<String> targetGmlIds) {
        if (targetGmlIds == null) {
            return;
        }

        for (String targetGmlId : targetGmlIds) {
            addReference(sourceType, sourceGmlId, relationName, targetGmlId);
        }
    }

    private void addReference(String sourceType, String sourceGmlId, String relationName, String targetGmlId) {
        if (isBlank(sourceGmlId) || isBlank(targetGmlId)) {
            return;
        }

        references.add(new ReferenceCandidate(
                sourceType,
                sourceGmlId,
                relationName,
                normalizeHref(targetGmlId)
        ));
    }

    private String normalizeHref(String href) {
        String trimmed = href.trim();
        return trimmed.startsWith("#") ? trimmed.substring(1) : trimmed;
    }

    private String firstRef(List<String> refs) {
        return refs == null || refs.isEmpty() ? null : refs.getFirst();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void setDouble(PreparedStatement statement, int parameterIndex, Double value) throws SQLException {
        if (value == null) {
            statement.setObject(parameterIndex, null);
        } else {
            statement.setDouble(parameterIndex, value);
        }
    }

    private void setInteger(PreparedStatement statement, int parameterIndex, Integer value) throws SQLException {
        if (value == null) {
            statement.setObject(parameterIndex, null);
        } else {
            statement.setInt(parameterIndex, value);
        }
    }

    private int batchSize() {
        return properties.getBatchSize();
    }

    private record ReferenceCandidate(
            String sourceType,
            String sourceGmlId,
            String relationName,
            String targetGmlId
    ) {
    }
}
