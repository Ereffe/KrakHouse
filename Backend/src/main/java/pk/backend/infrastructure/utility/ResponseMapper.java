package pk.backend.infrastructure.utility;

import lombok.experimental.UtilityClass;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.CityMap.GridMap;
import pk.backend.infrastructure.dto.FilteredMapDto;
import pk.backend.infrastructure.dto.FilteredMapListResponseDto;
import pk.backend.infrastructure.dto.MergedMapResponseDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class ResponseMapper {

    public static MergedMapResponseDto mapToMergedDto(CityMap mergedMap, List<CityMap> sourceMaps, List<FilteredMapDto> filters) {
        List<MergedMapResponseDto.FilterInfo> filterInfos = new ArrayList<>();
        
        for (int i = 0; i < filters.size(); i++) {
            FilteredMapDto f = filters.get(i);
            String provider = (sourceMaps != null && i < sourceMaps.size()) ? sourceMaps.get(i).getDataProvider() : null;
            filterInfos.add(new MergedMapResponseDto.FilterInfo(f.mapFilter(), provider, f.minValue(), f.maxValue()));
        }

        if (mergedMap == null) {
            return new MergedMapResponseDto(filterInfos, Collections.emptyList(), GridMap.LATITUDE_LEFT_BORDER, GridMap.LATITUDE_RIGHT_BORDER, GridMap.LONGITUDE_TOP_BORDER, GridMap.LONGITUDE_BOTTOM_BORDER);
        }

        List<List<Boolean>> data = mergedMap.getBoxMatrix().stream()
                .map(row -> row.stream()
                        .map(Objects::nonNull)
                        .toList())
                .toList();

        return new MergedMapResponseDto(filterInfos, data, GridMap.LATITUDE_LEFT_BORDER, GridMap.LATITUDE_RIGHT_BORDER, GridMap.LONGITUDE_TOP_BORDER, GridMap.LONGITUDE_BOTTOM_BORDER);
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
                        map.getDataProvider(),
                        data
                ));
            }
        }
        return new FilteredMapListResponseDto(singleMaps, GridMap.LATITUDE_LEFT_BORDER, GridMap.LATITUDE_RIGHT_BORDER, GridMap.LONGITUDE_TOP_BORDER, GridMap.LONGITUDE_BOTTOM_BORDER);
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
