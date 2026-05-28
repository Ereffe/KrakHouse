package pk.backend.infrastructure.utility;

import lombok.experimental.UtilityClass;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.infrastructure.dto.FilteredMapDto;
import pk.backend.infrastructure.dto.FilteredMapListResponseDto;
import pk.backend.infrastructure.dto.MergedMapResponseDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class ResponseMapper {

    public static MergedMapResponseDto mapToMergedDto(CityMap map, List<FilteredMapDto> filters) {
        List<MergedMapResponseDto.FilterInfo> filterInfos = filters.stream()
                .map(f -> new MergedMapResponseDto.FilterInfo(f.mapFilter(), f.minValue(), f.maxValue()))
                .toList();

        if (map == null) {
            return new MergedMapResponseDto(filterInfos, Collections.emptyList());
        }

        List<List<Boolean>> data = map.getBoxMatrix().stream()
                .map(row -> row.stream()
                        .map(Objects::nonNull)
                        .toList())
                .toList();

        return new MergedMapResponseDto(filterInfos, data);
    }

    public static FilteredMapListResponseDto mapToFilteredListDto(List<CityMap> maps, List<FilteredMapDto> filters) {
        List<FilteredMapListResponseDto.SingleMapResponseDto> singleMaps = new ArrayList<>();
        if (maps != null) {
            for (int i = 0; i < maps.size(); i++) {
                CityMap map = maps.get(i);
                if (map == null) continue;
                
                String type = filters.get(i).mapFilter();

                List<List<Number>> data = map.getBoxMatrix().stream()
                        .map(row -> row.stream()
                                .map(box -> box == null ? null : box.getValue().rawValue())
                                .toList())
                        .toList();

                singleMaps.add(new FilteredMapListResponseDto.SingleMapResponseDto(
                        type,
                        getValueType(type),
                        data
                ));
            }
        }
        return new FilteredMapListResponseDto(singleMaps);
    }

    private static String getValueType(String type) {
        if (type == null) return "Unknown";
        return switch (type.toUpperCase()) {
            case "AIR_QUALITY" -> "Air Quality Index (AQI)";
            case "CRIME" -> "Percentage %";
            case "NOISE" -> "Decibels dB";
            case "PRICE" -> "Currency PLN";
            default -> "Unknown";
        };
    }
}
