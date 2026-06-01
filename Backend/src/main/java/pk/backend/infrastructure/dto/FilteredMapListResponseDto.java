package pk.backend.infrastructure.dto;

import java.util.List;

public record FilteredMapListResponseDto(
        List<SingleMapResponseDto> maps,
        double LATITUDE_LEFT_BORDER,
        double LATITUDE_RIGHT_BORDER,
        double LONGITUDE_TOP_BORDER,
        double LONGITUDE_BOTTOM_BORDER
) {
    public record SingleMapResponseDto(
            String type,
            String valueType,
            String dataProvider,
            List<List<Number>> data
    ) {}
}
