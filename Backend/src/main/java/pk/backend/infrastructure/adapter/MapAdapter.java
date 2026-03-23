package pk.backend.infrastructure.adapter;

import pk.backend.aplication.port.inbound.MapSource;
import pk.backend.domain.CityMap.CityMap;

public class MapAdapter implements MapSource {
    @Override
    public CityMap createMap(Class<? extends CityMap> mapClass) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
