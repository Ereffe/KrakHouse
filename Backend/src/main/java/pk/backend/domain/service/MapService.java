package pk.backend.domain.service;

import pk.backend.domain.model.CityMap.CityMap;

public interface MapService {
    CityMap createMap(String mapType);
    String getType();
}
