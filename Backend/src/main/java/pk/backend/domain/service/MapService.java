package pk.backend.domain.service;

import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.box.ValueObjects.BoxValue;

public interface MapService {
    BoxValue createBoxValue(Number value);
    CityMap createMap();
    String getType();
    int getMinValue();
    int getMaxValue();
}
