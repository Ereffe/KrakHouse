package pk.backend.domain.service;

import pk.backend.domain.model.CityMap.CityMap;

public interface MapService {
    CityMap createMap();
    String getType();
    int getMinValue();
    int getMaxValue();
}
