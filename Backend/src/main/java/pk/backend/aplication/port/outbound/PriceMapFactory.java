package pk.backend.aplication.port.outbound;

import pk.backend.domain.model.CityMap.CityMap;

public interface PriceMapFactory {
    CityMap createMap();
}
