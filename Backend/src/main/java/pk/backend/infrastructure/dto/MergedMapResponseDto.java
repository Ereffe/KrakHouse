package pk.backend.infrastructure.dto;

import java.util.List;

public record MergedMapResponseDto(
        List<FilterInfo> type,
        List<List<Boolean>> data,
        double LATITUDE_LEFT_BORDER,
        double LATITUDE_RIGHT_BORDER,
        double LONGITUDE_TOP_BORDER,
        double LONGITUDE_BOTTOM_BORDER

) {
    public record FilterInfo(
            String type,
            String dataProvider,
            Float lowerBound,
            Float upperBound
    ) {}
}
