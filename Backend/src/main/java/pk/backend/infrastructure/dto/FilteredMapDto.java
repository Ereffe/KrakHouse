package pk.backend.infrastructure.dto;

import pk.backend.domain.model.utils.CompareCondition;

public record FilteredMapDto(
        String mapFilter,
        float value,
        CompareCondition condition
) {
}
