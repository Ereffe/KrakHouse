package pk.backend.infrastructure.dto;

import pk.backend.domain.utils.CompareCondition;
import pk.backend.domain.utils.Filter;

public record FilteredMapDto(
        Filter filter,
        float value,
        CompareCondition condition
) {
}
