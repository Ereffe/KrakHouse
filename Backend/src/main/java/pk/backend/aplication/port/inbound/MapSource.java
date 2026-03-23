package pk.backend.aplication.port.inbound;

import pk.backend.domain.CityMap.CityMap;

public interface MapSource {
    CityMap createMap(Class<? extends CityMap> mapClass);
}
