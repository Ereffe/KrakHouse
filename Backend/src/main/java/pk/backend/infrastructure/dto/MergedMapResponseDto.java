package pk.backend.infrastructure.dto;

import java.util.List;

public record MergedMapResponseDto(
        List<FilterInfo> type,
        List<List<Boolean>> data
) {
    public record FilterInfo(
            String type,
            Float lowerBound,
            Float upperBound
    ) {}
}
