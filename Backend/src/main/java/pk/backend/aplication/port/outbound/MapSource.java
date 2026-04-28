package pk.backend.aplication.port.outbound;

import pk.backend.domain.CityMap.CityMap;

public interface MapSource {
    CityMap createMap(Class<? extends CityMap> mapClass);
}
