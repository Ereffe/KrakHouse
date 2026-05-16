package pk.backend.aplication.service;
import org.springframework.stereotype.Service;
import pk.backend.aplication.port.inbound.ControllerPort;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.box.AirQualityBox;
import pk.backend.domain.model.box.BoxValue;
import pk.backend.domain.model.box.CrimeBox;
import pk.backend.domain.model.box.NoiseBox;
import pk.backend.domain.model.box.PriceBox;
import pk.backend.domain.service.MapService;

import pk.backend.infrastructure.dto.FilteredMapDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



@Service
public class CityMapService implements ControllerPort {

    private final List<MapService> services;

    public CityMapService(List<MapService> services) {
        this.services = services;
    }

    @Override
    public List<String> getFilters() {
        List<String> result = new ArrayList<>();
        for (var s : services) {
            result.add(s.getType());
        }
        return result;
    }

    @Override
    public CityMap getMergedMaps(List<FilteredMapDto> filteredMaps) {
        List<CityMap> maps = new ArrayList<>();

        for (var dto : filteredMaps) {
            var svc = findServiceForType(dto.mapFilter());
            if (svc == null) continue;
            CityMap map = svc.createMap(dto.mapFilter());

            BoxValue value = createBoxValue(dto.mapFilter(), dto.value());
            map.applyFilter(value, dto.condition());
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
            CityMap map = svc.createMap(dto.mapFilter());
            BoxValue value = createBoxValue(dto.mapFilter(), dto.value());
            map.applyFilter(value, dto.condition());
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

    private BoxValue createBoxValue(String mapFilter, float value) {
        if (mapFilter == null) return null;
        switch (mapFilter.toUpperCase()) {
            case "PRICE":
                return new PriceBox(BigDecimal.valueOf(value));
            case "NOISE":
                return new NoiseBox(Math.round(value));
            case "CRIME":
                return new CrimeBox(Math.round(value));
            case "AIR_QUALITY":
                return new AirQualityBox(Math.round(value));
            default:
                // fallback to price
                return new PriceBox(BigDecimal.valueOf(value));
        }
    }

}
