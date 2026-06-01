package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import pk.backend.infrastructure.config.ImportProperties;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelationLinkerService {

    private final JdbcTemplate jdbcTemplate;
    private final ImportProperties properties;

    public RelationLinkResult linkAll() {
        LinkCounter total = new LinkCounter();

        for (LinkSpec spec : linkSpecs()) {
            LinkCounter relation = link(spec);
            total.add(relation);
        }

        RelationLinkResult result = total.toResult();
        log.info("Finished RCN relation linking: linked={}, missingSources={}, missingTargets={}",
                result.linkedRelations(), result.missingSources(), result.missingTargets());
        return result;
    }

    private LinkCounter link(LinkSpec spec) {
        Instant startedAt = Instant.now();
        LinkCounter counter = new LinkCounter();
        long lastReferenceId = 0;
        long lastLoggedLinked = 0;

        long candidateReferences = countCandidateReferences(spec);
        counter.missingSources = countMissingSources(spec);
        counter.missingTargets = countMissingTargets(spec);

        log.info("Starting RCN relation link: relation={}, candidates={}, missingSources={}, missingTargets={}",
                spec.name(), candidateReferences, counter.missingSources, counter.missingTargets);

        while (true) {
            List<LinkCandidate> candidates = findNextCandidates(spec, lastReferenceId);
            if (candidates.isEmpty()) {
                break;
            }

            updateTargets(spec, candidates);
            counter.linkedRelations += candidates.size();
            lastReferenceId = candidates.getLast().referenceId();

            if (counter.linkedRelations - lastLoggedLinked >= properties.getLinkLogInterval()) {
                logProgress(spec, counter.linkedRelations, candidateReferences, startedAt);
                lastLoggedLinked = counter.linkedRelations;
            }
        }

        logProgress(spec, counter.linkedRelations, candidateReferences, startedAt);
        log.info("Finished RCN relation link: relation={}, linked={}, missingSources={}, missingTargets={}",
                spec.name(), counter.linkedRelations, counter.missingSources, counter.missingTargets);

        return counter;
    }

    private List<LinkCandidate> findNextCandidates(LinkSpec spec, long afterReferenceId) {
        String sql = """
                SELECT r.id AS reference_id, source.id AS source_id, target.id AS target_id
                FROM rcn_unresolved_references r
                JOIN %s source ON source.gml_id = r.source_gml_id
                JOIN %s target ON target.gml_id = r.target_gml_id
                WHERE r.source_type = ?
                  AND r.relation_name = ?
                  AND r.id > ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM rcn_unresolved_references earlier
                      JOIN %s earlier_target ON earlier_target.gml_id = earlier.target_gml_id
                      WHERE earlier.source_type = r.source_type
                        AND earlier.relation_name = r.relation_name
                        AND earlier.source_gml_id = r.source_gml_id
                        AND earlier.id < r.id
                  )
                ORDER BY r.id
                LIMIT ?
                """.formatted(spec.sourceTable(), spec.targetTable(), spec.targetTable());

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new LinkCandidate(
                        rs.getLong("reference_id"),
                        rs.getLong("source_id"),
                        rs.getLong("target_id")
                ),
                spec.sourceType(),
                spec.relationName(),
                afterReferenceId,
                properties.getBatchSize()
        );
    }

    private void updateTargets(LinkSpec spec, List<LinkCandidate> candidates) {
        String sql = "UPDATE " + spec.sourceTable() + " SET " + spec.fkColumn() + " = ? WHERE id = ?";

        jdbcTemplate.batchUpdate(sql, candidates, properties.getBatchSize(), this::bindUpdate);
    }

    private void bindUpdate(PreparedStatement statement, LinkCandidate candidate) throws SQLException {
        statement.setLong(1, candidate.targetId());
        statement.setLong(2, candidate.sourceId());
    }

    private long countCandidateReferences(LinkSpec spec) {
        String sql = """
                SELECT COUNT(*)
                FROM rcn_unresolved_references
                WHERE source_type = ?
                  AND relation_name = ?
                """;

        Long count = jdbcTemplate.queryForObject(sql, Long.class, spec.sourceType(), spec.relationName());
        return count == null ? 0 : count;
    }

    private long countMissingSources(LinkSpec spec) {
        String sql = """
                SELECT COUNT(*)
                FROM rcn_unresolved_references r
                LEFT JOIN %s source ON source.gml_id = r.source_gml_id
                WHERE r.source_type = ?
                  AND r.relation_name = ?
                  AND source.id IS NULL
                """.formatted(spec.sourceTable());

        Long count = jdbcTemplate.queryForObject(sql, Long.class, spec.sourceType(), spec.relationName());
        return count == null ? 0 : count;
    }

    private long countMissingTargets(LinkSpec spec) {
        String sql = """
                SELECT COUNT(*)
                FROM rcn_unresolved_references r
                JOIN %s source ON source.gml_id = r.source_gml_id
                LEFT JOIN %s target ON target.gml_id = r.target_gml_id
                WHERE r.source_type = ?
                  AND r.relation_name = ?
                  AND target.id IS NULL
                """.formatted(spec.sourceTable(), spec.targetTable());

        Long count = jdbcTemplate.queryForObject(sql, Long.class, spec.sourceType(), spec.relationName());
        return count == null ? 0 : count;
    }

    private void logProgress(LinkSpec spec, long linked, long candidateReferences, Instant startedAt) {
        long elapsedMillis = Math.max(1, Duration.between(startedAt, Instant.now()).toMillis());
        double elapsedSeconds = elapsedMillis / 1000.0;
        double rowsPerSecond = linked / elapsedSeconds;

        log.info("RCN relation link progress: relation={}, linked={}, candidates={}, elapsedSeconds={}, rowsPerSecond={}",
                spec.name(), linked, candidateReferences, String.format("%.1f", elapsedSeconds), String.format("%.1f", rowsPerSecond));
    }

    private List<LinkSpec> linkSpecs() {
        return List.of(
                new LinkSpec(
                        "transaction->property",
                        "RCN_Transakcja",
                        "nieruchomosc",
                        "rcn_transactions",
                        "rcn_properties",
                        "property_id"
                ),
                new LinkSpec(
                        "property->parcel",
                        "RCN_Nieruchomosc",
                        "dzialka",
                        "rcn_properties",
                        "rcn_parcels",
                        "parcel_id"
                ),
                new LinkSpec(
                        "property->building",
                        "RCN_Nieruchomosc",
                        "budynek",
                        "rcn_properties",
                        "rcn_buildings",
                        "building_id"
                ),
                new LinkSpec(
                        "property->local",
                        "RCN_Nieruchomosc",
                        "lokal",
                        "rcn_properties",
                        "rcn_locals",
                        "local_id"
                ),
                new LinkSpec(
                        "local->address",
                        "RCN_Lokal",
                        "adresBudynkuZLokalem",
                        "rcn_locals",
                        "rcn_addresses",
                        "address_id"
                ),
                new LinkSpec(
                        "parcel->address",
                        "RCN_Dzialka",
                        "adresDzialki",
                        "rcn_parcels",
                        "rcn_addresses",
                        "address_id"
                ),
                new LinkSpec(
                        "building->address",
                        "RCN_Budynek",
                        "adresBudynku",
                        "rcn_buildings",
                        "rcn_addresses",
                        "address_id"
                )
        );
    }

    private record LinkSpec(
            String name,
            String sourceType,
            String relationName,
            String sourceTable,
            String targetTable,
            String fkColumn
    ) {

        private LinkSpec {
            Objects.requireNonNull(name);
            Objects.requireNonNull(sourceType);
            Objects.requireNonNull(relationName);
            Objects.requireNonNull(sourceTable);
            Objects.requireNonNull(targetTable);
            Objects.requireNonNull(fkColumn);
        }
    }

    private record LinkCandidate(long referenceId, long sourceId, long targetId) {
    }

    private static class LinkCounter {
        private long linkedRelations;
        private long missingSources;
        private long missingTargets;

        void add(LinkCounter other) {
            linkedRelations += other.linkedRelations;
            missingSources += other.missingSources;
            missingTargets += other.missingTargets;
        }

        RelationLinkResult toResult() {
            return new RelationLinkResult(linkedRelations, missingSources, missingTargets);
        }
    }
}
