package pk.backend.infrastructure.service.parser;

public record GmlParseResult(
        long featureMemberCount,
        long handledObjectCount,
        long skippedObjectCount
) {
}