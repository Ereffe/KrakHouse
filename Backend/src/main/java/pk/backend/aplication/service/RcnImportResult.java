package pk.backend.aplication.service;

import java.nio.file.Path;

public record RcnImportResult(
        Long jobId,
        Path downloadedFile,
        Path parsedGmlFile,
        long featureMemberCount,
        long handledObjectCount,
        long skippedObjectCount,
        RelationLinkResult relationLinkResult
) {
}
