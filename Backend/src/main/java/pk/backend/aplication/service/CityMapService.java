package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pk.backend.aplication.port.inbound.ControllerPort;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.domain.service.MapService;
import pk.backend.infrastructure.dto.FilterResponseDto;
import pk.backend.infrastructure.dto.FilteredMapDto;

import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class CityMapService implements ControllerPort {

    private final List<MapService> services;

    @Override
    public List<FilterResponseDto> getFilters() {
        List<FilterResponseDto> result = new ArrayList<>();
        for (var s : services) {
            result.add(new FilterResponseDto(s.getType(), s.getMinValue(), s.getMaxValue()));
        }
        return result;
    }

    @Override
    public CityMap getMergedMaps(List<FilteredMapDto> filteredMaps) {
        List<CityMap> maps = new ArrayList<>();

        for (var dto : filteredMaps) {
            var svc = findServiceForType(dto.mapFilter());
            if (svc == null) continue;
            CityMap map = svc.createMap();

            BoxValue min = svc.createBoxValue(dto.minValue());
            BoxValue max = svc.createBoxValue(dto.maxValue());
            map.applyFilter(min, max);
            maps.add(map);
        }

        if (maps.isEmpty()) return null;

        CityMap merged = maps.get(0);
        for (int i = 1; i < maps.size(); i++) {
            merged = merged.merge(maps.get(i));
        }
        return merged;
    }

    @Override
    public List<CityMap> getFilteredMap(List<FilteredMapDto> filteredMaps) {
        List<CityMap> result = new ArrayList<>();

        for (var dto : filteredMaps) {
            var svc = findServiceForType(dto.mapFilter());
            if (svc == null) continue;
            CityMap map = svc.createMap();
            
            BoxValue min = svc.createBoxValue(dto.minValue());
            BoxValue max = svc.createBoxValue(dto.maxValue());
            map.applyFilter(min, max);
            result.add(map);
        }

        return result;
    }

    private MapService findServiceForType(String type) {
        for (var s : services) {
            if (s.getType().equalsIgnoreCase(type)) return s;
        }
        return null;
    }

}
