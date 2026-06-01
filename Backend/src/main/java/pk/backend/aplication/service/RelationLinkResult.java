package pk.backend.aplication.service;

public record RelationLinkResult(
        long linkedRelations,
        long missingSources,
        long missingTargets
) {
}
