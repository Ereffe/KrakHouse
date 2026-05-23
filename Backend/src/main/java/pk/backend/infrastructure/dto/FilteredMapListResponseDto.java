package pk.backend.infrastructure.dto;

import java.util.List;

public record FilteredMapListResponseDto(
        List<SingleMapResponseDto> maps
) {
    public record SingleMapResponseDto(
            String type,
            String valueType,
            List<List<Number>> data
    ) {}
}
